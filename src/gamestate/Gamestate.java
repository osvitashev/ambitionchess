package gamestate;

import static gamestate.Bitboard.*;
import static gamestate.GlobalConstants.*;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/**
 * Represents Board state. Essentially a wrapper around FEN notation with added
 * denorms.
 * 
 *
 */
public class Gamestate {
	private long[] playerBB = new long[Player.PLAYERS.length];
	private long[] pieceBB = new long[PieceType.PIECE_TYPES.length];

	/**
	 * position of respective kings updated on load and makeDirtyMove()
	 */
	private int[] kingSquare = new int[2];

	private int playerToMove = Player.NO_PLAYER;
	// TODO: denorm opponent as well in order to avoid calling getOtherPlayer many
	// times.

	/**
	 * used for 50 more rule. FEN already specifies it as a number of half-moves.
	 */
	private int quietHalfmoveClock = 0;
	private int gamePlyCount = 0;

	private boolean castling_WQ = false, castling_WK = false, castling_BQ = false, castling_BK = false;
	private int enpassantSq = Square.SQUARE_NONE;

	/**
	 * true if the current player to move is in check updated on load and makeMove()
	 */
	private boolean isCheck = false;
	
	
	private int[] undoStack = new int[200];
	private int undoStack_sze = 0;
	// TODO: there should be a stack of historical moves, zorbist codes too

	public int getPlayerToMove() {
		return playerToMove;
	}

	public int getQuietHalfmoveClock() {
		return quietHalfmoveClock;
	}

	public boolean getCastling_WQ() {
		return castling_WQ;
	}

	public boolean getCastling_WK() {
		return castling_WK;
	}

	public boolean getCastling_BQ() {
		return castling_BQ;
	}

	public boolean getCastling_BK() {
		return castling_BK;
	}

	public int getEnpassantSquare() {
		return enpassantSq;
	}

	public int getGamePlyCount() {
		return gamePlyCount;
	}

	public long getOccupied() {
		return playerBB[Player.WHITE] | playerBB[Player.BLACK];
	}

	public long getEmpty() {
		return ~getOccupied();
	}

	public long getPieces(int type) {
		assert PieceType.validate(type);
		return pieceBB[type];
	}

	public long getPieces(int player, int type) {
		assert Player.validate(player);
		assert PieceType.validate(type);
		return pieceBB[type] & playerBB[player];
	}

	public long getPlayerPieces(int p) {
		assert Player.validate(p);
		return playerBB[p];
	}

	/**
	 * Updates bitboards. Assumes that they are empty to begin with. May result in
	 * an invalid board state.
	 * 
	 * @param piece
	 * @param player
	 * @param sq
	 */
	private void putPieceAt(int piece, int player, int sq) {
		assert PieceType.validate(piece);
		assert Player.validate(player);
		assert Square.validate(sq);
		playerBB[player] = setBit(playerBB[player], sq);
		pieceBB[piece] = setBit(pieceBB[piece], sq);
	}

//	/**
//	 * It is preferred to use method taking piece type argument instead for
//	 * performance reasons.
//	 * 
//	 * @param sq
//	 */
//	private void clearPieceAt(int sq) {
//		assert Square.validate(sq);
//		playerBB[Player.WHITE] = clearBit(playerBB[Player.WHITE], sq);
//		playerBB[Player.BLACK] = clearBit(playerBB[Player.BLACK], sq);
//		for (int i = 0; i < PieceType.PIECE_TYPES.length; ++i)
//			pieceBB[PieceType.PIECE_TYPES[i]] = clearBit(pieceBB[PieceType.PIECE_TYPES[i]], sq);
//	}

	private void clearPieceAt(int pt, int sq) {
		assert Square.validate(sq);
		assert PieceType.validate(pt);
		playerBB[Player.WHITE] = clearBit(playerBB[Player.WHITE], sq);
		playerBB[Player.BLACK] = clearBit(playerBB[Player.BLACK], sq);
		pieceBB[pt] = clearBit(pieceBB[pt], sq);
	}

	public int getPieceAt(int sq) {
		assert Square.validate(sq);
		for (int i = 0; i < PieceType.PIECE_TYPES.length; ++i)
			if (testBit(pieceBB[i], sq))
				return i;
		return PieceType.NO_PIECE;
	}

	/**
	 * May return NO_PLAYER, so should be used with care.
	 * 
	 * @param sq
	 * @return player
	 */
	public int getPlayerAt(int sq) {
		assert Square.validate(sq);
		if (testBit(playerBB[Player.WHITE], sq))
			return Player.WHITE;
		else if (testBit(playerBB[Player.BLACK], sq))
			return Player.BLACK;
		else
			return Player.NO_PLAYER;
	}

	/**
	 * Tests contents of a square against a given piece type and player.
	 * 
	 * @param pieceType
	 * @param player
	 * @param sq
	 * @return boolean
	 */
	public boolean testPieceAt(int pieceType, int player, int sq) {
		assert PieceType.validate(pieceType);
		assert Player.validate(player);
		assert Square.validate(sq);
		return testBit(playerBB[player], sq) && testBit(pieceBB[pieceType], sq);
	}

	/**
	 * Validates that other side's king is NOT en prise, but side-to-move may be in
	 * check
	 * 
	 * @return
	 */
	boolean validateKingExposure() {
		// if (!Bitboard.hasOnly1Bit(pieceBB[PieceType.KING] & playerBB[Player.WHITE]))
		// return false;
		// if (!Bitboard.hasOnly1Bit(pieceBB[PieceType.KING] & playerBB[Player.BLACK]))
		// return false;
		// if (isPlayerInCheck(Player.getOtherPlayer(getPlayerToMove())))
		// return false;
		return !calculateIsPlayerInCheck(Player.getOtherPlayer(getPlayerToMove()));
	}

	private void setKingSquare(int sq, int pl) {
		assert Player.validate(pl);
		assert Square.validate(sq);
		kingSquare[pl] = sq;
	}

	public int getKingSquare(int pl) {
		assert Player.validate(pl);
		return kingSquare[pl];
	}

	/**
	 * Returns a bitboard of attack sources for a given square by s given player.
	 * 
	 * @param sq
	 * @param pl
	 * @return
	 */
	public long calculateSquareAttackers(int sq, int pl) {
		long ret = 0;
		assert Player.validate(pl);
		assert Square.validate(sq);
		// IDEA: this function can be a dynamic part of the game state. If there are no
		// more knights on the board, there is no point in checking for knight attacks.
		// Most moves do not modify such conditions.
		int otherPlayer = Player.getOtherPlayer(pl);
		ret |= getPieces(pl, PieceType.PAWN) & BitboardGen.getPawnAttackSet(sq, otherPlayer);
		ret |= (getPieces(pl, PieceType.QUEEN) | getPieces(pl, PieceType.ROOK)) & BitboardGen.getRookSet(sq, getOccupied());
		ret |= (getPieces(pl, PieceType.QUEEN) | getPieces(pl, PieceType.BISHOP)) & BitboardGen.getBishopSet(sq, getOccupied());
		ret |= getPieces(pl, PieceType.KNIGHT) & BitboardGen.getKnightSet(sq);
		ret |= getPieces(pl, PieceType.KING) & BitboardGen.getKingSet(sq);
		return ret;
	}

	/**
	 * Returns a bitboard mask which represents pieces which would result in the
	 * target square being attacked by the given player if those pieces were
	 * removed. Color-agnostic with regards to blockers. The bitboard returned by
	 * this function may need to be processed further in order to distinguish
	 * between pins, skewers and potential discovered attacks.
	 * 
	 * @param sq - attack target
	 * @param pl - attacker
	 * @return
	 */
	public long calculatePinsSkewersAndDiscoveredAttacks(int sq, int pl) {
		assert Player.validate(pl);
		assert Square.validate(sq);
		long ret = 0;
		long rookReverseSet = BitboardGen.getRookSet(sq, getOccupied());
		long rookBlockers = rookReverseSet & ~getEmpty();
		for (long zarg = rookBlockers, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBits
			long rookIndirectAttackers = (getPieces(pl, PieceType.QUEEN) | getPieces(pl, PieceType.ROOK)) & BitboardGen.getRookSet(sq, getOccupied() & ~barg) & ~rookReverseSet;
			if (rookIndirectAttackers != 0)
				ret |= barg;
		}
		long bishopReverseSet = BitboardGen.getBishopSet(sq, getOccupied());
		long bishopBlockers = bishopReverseSet & ~getEmpty();
		for (long zarg = bishopBlockers, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBits
			long bishopIndirectAttackers = (getPieces(pl, PieceType.QUEEN) | getPieces(pl, PieceType.BISHOP)) & BitboardGen.getBishopSet(sq, getOccupied() & ~barg) & ~bishopReverseSet;
			if (bishopIndirectAttackers != 0)
				ret |= barg;
		}
		return ret;
	}

	/**
	 * Does reverse attack set generation for a given square. Intended to detect
	 * checks and castling availability.
	 * 
	 * @param sq
	 * @param pl
	 * @return boolean
	 */
	public boolean calculateIsSquareAttackedBy(int sq, int pl) {
		assert Player.validate(pl);
		assert Square.validate(sq);
		if (!Bitboard.isEmpty(calculateSquareAttackers(sq, pl)))
			return true;
		return false;
	}

	/**
	 * Does reverse attack set generation for a king position.
	 * 
	 * @param pl - attacker
	 * @return boolean
	 */
	public boolean calculateIsPlayerInCheck(int pl) {
		assert Player.validate(pl);
		int sq = getKingSquare(pl);
		return calculateIsSquareAttackedBy(sq, Player.getOtherPlayer(pl));
	}

	/**
	 * getter for the board's state variable. => is the side-to-move in check?
	 * 
	 * updated on load and makeMove()
	 * @return
	 */
	public boolean getIsCheck() {
		return isCheck;
	}

	/**
	 * Only updates the board, but not the game state. Only suitable for move
	 * validation. Toggles side to move, but makes no other change except updating
	 * piece position and king position denorm. No updates to castling rights, en
	 * passant, or move counters
	 * 
	 * @param move
	 */
	void makeDirtyMove(int move) {
		int player = Move.getPlayer(move);
		switch (Move.getMoveType(move)) {
		case MoveType.PROMO_CAPTURE:
			clearPieceAt(Move.getPieceCapturedType(move), Move.getSquareTo(move));
			clearPieceAt(PieceType.PAWN, Move.getSquareFrom(move));
			putPieceAt(Move.getPiecePromotedType(move), player, Move.getSquareTo(move));
			break;
		case MoveType.PROMO:
			clearPieceAt(PieceType.PAWN, Move.getSquareFrom(move));
			putPieceAt(Move.getPiecePromotedType(move), player, Move.getSquareTo(move));
			break;
		case MoveType.CAPTURE:
			clearPieceAt(Move.getPieceType(move), Move.getSquareFrom(move));
			clearPieceAt(Move.getPieceCapturedType(move), Move.getSquareTo(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareTo(move));
			// TODO: add more distinction for the king update
			setKingSquare(getFirstSquareIndex(getPieces(player, PieceType.KING)), player);
			break;
		case MoveType.ENPASSANT:
			clearPieceAt(Move.getPieceType(move), Move.getSquareFrom(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareTo(move));
			if (player == Player.WHITE)
				clearPieceAt(Move.getPieceCapturedType(move), Move.getSquareTo(move) - 8);
			else
				clearPieceAt(Move.getPieceCapturedType(move), Move.getSquareTo(move) + 8);
			break;
		case MoveType.CASTLE_KING:
			if (player == Player.WHITE) {
				setKingSquare(Square.G1, player);
				clearPieceAt(PieceType.KING, Square.E1);
				clearPieceAt(PieceType.ROOK, Square.H1);
				putPieceAt(PieceType.KING, player, Square.G1);
				putPieceAt(PieceType.ROOK, player, Square.F1);
			} else {
				setKingSquare(Square.G8, player);
				clearPieceAt(PieceType.KING, Square.E8);
				clearPieceAt(PieceType.ROOK, Square.H8);
				putPieceAt(PieceType.KING, player, Square.G8);
				putPieceAt(PieceType.ROOK, player, Square.F8);
			}
			break;
		case MoveType.CASTLE_QUEEN:
			if (player == Player.WHITE) {
				setKingSquare(Square.C1, player);
				clearPieceAt(PieceType.KING, Square.E1);
				clearPieceAt(PieceType.ROOK, Square.A1);
				putPieceAt(PieceType.KING, player, Square.C1);
				putPieceAt(PieceType.ROOK, player, Square.D1);
			} else {
				setKingSquare(Square.C8, player);
				clearPieceAt(PieceType.KING, Square.E8);
				clearPieceAt(PieceType.ROOK, Square.A8);
				putPieceAt(PieceType.KING, player, Square.C8);
				putPieceAt(PieceType.ROOK, player, Square.D8);
			}
			break;
		case MoveType.NORMAL:
			clearPieceAt(Move.getPieceType(move), Move.getSquareFrom(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareTo(move));
			// TODO: more detection before king update
			setKingSquare(getFirstSquareIndex(getPieces(player, PieceType.KING)), player);
			break;
		case MoveType.DOUBLE_PUSH:
			clearPieceAt(Move.getPieceType(move), Move.getSquareFrom(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareTo(move));
			break;
		}
		playerToMove = Player.getOtherPlayer(player);
	}

	void unmakeDirtyMove(int move) {
		int player = Move.getPlayer(move);
		int otherPlayer = Player.getOtherPlayer(player);
		switch (Move.getMoveType(move)) {
		case MoveType.PROMO_CAPTURE:
			clearPieceAt(Move.getPiecePromotedType(move), Move.getSquareTo(move));
			putPieceAt(Move.getPieceCapturedType(move), otherPlayer, Move.getSquareTo(move));
			putPieceAt(PieceType.PAWN, player, Move.getSquareFrom(move));
			break;
		case MoveType.PROMO:
			clearPieceAt(Move.getPiecePromotedType(move), Move.getSquareTo(move));
			putPieceAt(PieceType.PAWN, player, Move.getSquareFrom(move));
			break;
		case MoveType.CAPTURE:
			clearPieceAt(Move.getPieceType(move), Move.getSquareTo(move));
			putPieceAt(Move.getPieceCapturedType(move), otherPlayer, Move.getSquareTo(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareFrom(move));
			// TODO: add more distinction for the king update
			setKingSquare(getFirstSquareIndex(getPieces(player, PieceType.KING)), player);
			break;
		case MoveType.ENPASSANT:
			clearPieceAt(PieceType.PAWN, Move.getSquareTo(move));
			putPieceAt(PieceType.PAWN, player, Move.getSquareFrom(move));
			if (player == Player.WHITE)
				putPieceAt(PieceType.PAWN, otherPlayer, Move.getSquareTo(move) - 8);
			else
				putPieceAt(PieceType.PAWN, otherPlayer, Move.getSquareTo(move) + 8);
			break;
		case MoveType.CASTLE_KING:
			if (player == Player.WHITE) {
				setKingSquare(Square.E1, player);
				clearPieceAt(PieceType.KING, Square.G1);
				clearPieceAt(PieceType.ROOK, Square.F1);
				putPieceAt(PieceType.KING, player, Square.E1);
				putPieceAt(PieceType.ROOK, player, Square.H1);
			} else {
				setKingSquare(Square.E8, player);
				clearPieceAt(PieceType.KING, Square.G8);
				clearPieceAt(PieceType.ROOK, Square.F8);
				putPieceAt(PieceType.KING, player, Square.E8);
				putPieceAt(PieceType.ROOK, player, Square.H8);
			}
			break;
		case MoveType.CASTLE_QUEEN:
			if (player == Player.WHITE) {
				setKingSquare(Square.E1, player);
				clearPieceAt(PieceType.KING, Square.C1);
				clearPieceAt(PieceType.ROOK, Square.D1);
				putPieceAt(PieceType.KING, player, Square.E1);
				putPieceAt(PieceType.ROOK, player, Square.A1);
			} else {
				setKingSquare(Square.E8, player);
				clearPieceAt(PieceType.KING, Square.C8);
				clearPieceAt(PieceType.ROOK, Square.D8);
				putPieceAt(PieceType.KING, player, Square.E8);
				putPieceAt(PieceType.ROOK, player, Square.A8);
			}
			break;
		case MoveType.NORMAL:
			clearPieceAt(Move.getPieceType(move), Move.getSquareTo(move));
			putPieceAt(Move.getPieceType(move), player, Move.getSquareFrom(move));
			// TODO: add more distinction for the king update
			setKingSquare(getFirstSquareIndex(getPieces(player, PieceType.KING)), player);
			break;
		case MoveType.DOUBLE_PUSH:
			clearPieceAt(PieceType.PAWN, Move.getSquareTo(move));
			putPieceAt(PieceType.PAWN, player, Move.getSquareFrom(move));
			break;
		}
		playerToMove = player;
	}

	/**
	 * Fully stores, then updates the game state. Including castling rights,
	 * enpassant square and 50-move counter
	 * 
	 * @param move
	 */
	public void makeMove(int move) {
		// OPTIMIZE: streamline tested conditions in this method
		
		//TODO: when i implement a denorm for king pins and skewers, it woudl need to be updated here.
		makeDirtyMove(move);
		int undoinfo = 0;
		undoinfo = UndoInfo.setHalfmoveCounter(undoinfo, quietHalfmoveClock);
		if (enpassantSq != Square.SQUARE_NONE) {
			undoinfo = UndoInfo.setIsEnpassantAvailable(undoinfo, true);
			undoinfo = UndoInfo.setEnpassantSquare(undoinfo, enpassantSq);
		}

		undoinfo = UndoInfo.setCastlingWK(undoinfo, castling_WK);
		undoinfo = UndoInfo.setCastlingWQ(undoinfo, castling_WQ);
		undoinfo = UndoInfo.setCastlingBK(undoinfo, castling_BK);
		undoinfo = UndoInfo.setCastlingBQ(undoinfo, castling_BQ);
		undoinfo = UndoInfo.setCheck(undoinfo, isCheck);
		undoStack[undoStack_sze] = undoinfo;
		undoStack_sze += 1;

		if (Move.getMoveType(move) == MoveType.PROMO_CAPTURE || Move.getMoveType(move) == MoveType.PROMO || Move.getMoveType(move) == MoveType.CAPTURE
				|| Move.getMoveType(move) == MoveType.ENPASSANT || Move.getMoveType(move) == MoveType.DOUBLE_PUSH
				|| (Move.getPieceType(move) == PieceType.PAWN && Move.getMoveType(move) == MoveType.NORMAL))// uninitialized value happens to coincide with a
																											// pawn
			quietHalfmoveClock = 0;
		else
			quietHalfmoveClock += 1;

		if (Move.getMoveType(move) == MoveType.DOUBLE_PUSH) {
			enpassantSq = (Move.getSquareTo(move) + Move.getSquareFrom(move)) / 2;
		} else
			enpassantSq = Square.SQUARE_NONE;

		if (Move.getPlayer(move) == Player.WHITE) {
			if (Move.getPieceType(move) == PieceType.KING || Move.getMoveType(move) == MoveType.CASTLE_KING || Move.getMoveType(move) == MoveType.CASTLE_QUEEN) {
				castling_WK = false;
				castling_WQ = false;
			} else if (Move.getPieceType(move) == PieceType.ROOK && Move.getSquareFrom(move) == Square.A1)
				castling_WQ = false;
			else if (Move.getPieceType(move) == PieceType.ROOK && Move.getSquareFrom(move) == Square.H1)
				castling_WK = false;

			if (Move.getSquareTo(move) == Square.A8)
				castling_BQ = false;
			else if (Move.getSquareTo(move) == Square.H8)
				castling_BK = false;
		} else {
			if (Move.getPieceType(move) == PieceType.KING || Move.getMoveType(move) == MoveType.CASTLE_KING || Move.getMoveType(move) == MoveType.CASTLE_QUEEN) {
				castling_BK = false;
				castling_BQ = false;
			} else if (Move.getPieceType(move) == PieceType.ROOK && Move.getSquareFrom(move) == Square.A8)
				castling_BQ = false;
			else if (Move.getPieceType(move) == PieceType.ROOK && Move.getSquareFrom(move) == Square.H8)
				castling_BK = false;

			// need special case for A1 because it coincides with uninitialized bit field
			if (Move.getSquareTo(move) == Square.A1 && Move.getMoveType(move) != MoveType.CASTLE_KING && Move.getMoveType(move) != MoveType.CASTLE_QUEEN)
				castling_WQ = false;
			else if (Move.getSquareTo(move) == Square.H1)
				castling_WK = false;
		}

		gamePlyCount += 1;
		isCheck = Move.getCheck(move);

	}

	public void unmakeMove(int move) {
		unmakeDirtyMove(move);
		int undoinfo = undoStack[undoStack_sze - 1];
		undoStack_sze -= 1;
		quietHalfmoveClock = UndoInfo.getHalfmoveCounter(undoinfo);
		if (UndoInfo.getIsEnpassantAvailable(undoinfo))
			enpassantSq = UndoInfo.getEnpassantSquare(undoinfo);
		else
			enpassantSq = Square.SQUARE_NONE;
		castling_WK = UndoInfo.getCastlingWK(undoinfo);
		castling_WQ = UndoInfo.getCastlingWQ(undoinfo);
		castling_BK = UndoInfo.getCastlingBK(undoinfo);
		castling_BQ = UndoInfo.getCastlingBQ(undoinfo);
		gamePlyCount -= 1;
		isCheck = UndoInfo.getIsCheck(undoinfo);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		int i = 56;
		b.append("   a |  b |  c |  d |  e |  f |  g |  h \r\n");
		b.append("-------------------------------------------\r\n");
		while (i >= 0) {
			if (i % 8 == 0) {
				b.append(i / 8 + 1);
				b.append('|');
			}
			int piece = getPieceAt(i);
			int player = getPlayerAt(i);
			char pSymbol = '.';
			switch (piece) {
			case PieceType.PAWN:
				pSymbol = 'p';
				break;
			case PieceType.ROOK:
				pSymbol = 'r';
				break;
			case PieceType.KNIGHT:
				pSymbol = 'n';
				break;
			case PieceType.BISHOP:
				pSymbol = 'b';
				break;
			case PieceType.QUEEN:
				pSymbol = 'q';
				break;
			case PieceType.KING:
				pSymbol = 'k';
				break;
			}
			if (Player.isWhite(player))
				pSymbol = Character.toUpperCase(pSymbol);
			b.append(" ");
			b.append(pSymbol);
			b.append(" |");
			b.append(" ");
			if (i % 8 == 7) {
				b.append(i / 8 + 1);
				b.append("\r\n");
				b.append("-------------------------------------------\r\n");
				i -= 16;
			}
			i++;
		}
		b.append("   a |  b |  c |  d |  e |  f |  g |  h ");
		return b.toString();
	}

	private void clear() {
		for (int i = 0; i < PieceType.PIECE_TYPES.length; ++i)
			pieceBB[PieceType.PIECE_TYPES[i]] = 0L;
		playerBB[Player.WHITE] = 0L;
		playerBB[Player.BLACK] = 0L;
		playerToMove = Player.NO_PLAYER;
		quietHalfmoveClock = 0;// used for 50 more rule
		gamePlyCount = 0;
		castling_WQ = false;
		castling_WK = false;
		castling_BQ = false;
		castling_BK = false;
		enpassantSq = Square.SQUARE_NONE;

		isCheck = false;
		for (int i = 0; i < undoStack.length; ++i)
			undoStack[i] = 0;
		undoStack_sze = 0;

		kingSquare[0] = 0;
		kingSquare[1] = 0;
	}

	public Gamestate() {
		loadFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
	}

	public Gamestate(String fen) {
		loadFromFEN(fen);
	}

	/**
	 * Functionally is equivalent to creating a new instance with fen as argument.
	 * 
	 * @param String fen
	 */
	public Gamestate loadFromFEN(String fen) {
		clear();
		if ("".equals(fen))
			fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		String squares = fen.substring(0, fen.indexOf(' '));
		String state = fen.substring(fen.indexOf(' ') + 1);

		String[] ranks = squares.split("/");
		int file;
		int rank = 7;
		for (String r : ranks) {
			file = 0;
			for (int i = 0; i < r.length(); i++) {
				char c = r.charAt(i);
				if (Character.isDigit(c)) {
					file += Integer.parseInt(c + "");
				} else {
					int sq = 8 * rank + file;
					int player = Character.isUpperCase(c) ? Player.WHITE : Player.BLACK;
					c = Character.toLowerCase(c);
					int piece = PieceType.NO_PIECE;
					switch (c) {
					case 'p':
						piece = PieceType.PAWN;
						break;
					case 'r':
						piece = PieceType.ROOK;
						break;
					case 'n':
						piece = PieceType.KNIGHT;
						break;
					case 'b':
						piece = PieceType.BISHOP;
						break;
					case 'q':
						piece = PieceType.QUEEN;
						break;
					case 'k':
						piece = PieceType.KING;
						break;
					default:
						piece = PieceType.NO_PIECE;
						break;
					}
					putPieceAt(piece, player, sq);
					file++;
				}
			}
			rank--;
		}
		playerToMove = state.toLowerCase().charAt(0) == 'w' ? Player.WHITE : Player.BLACK;
		if (state.contains("K"))
			castling_WK = true;
		if (state.contains("Q"))
			castling_WQ = true;
		if (state.contains("k"))
			castling_BK = true;
		if (state.contains("q"))
			castling_BQ = true;

		String[] flags = state.split(" ");
		if (flags.length >= 3) {
			String s = flags[2].toLowerCase().trim();
			enpassantSq = Square.algebraicStringToSquare(s);
			if (flags.length >= 4) {
				quietHalfmoveClock = Integer.parseInt(flags[3]);
				if (flags.length >= 5) {
					gamePlyCount = 2 * (Integer.parseInt(flags[4]) - 1);
					if (!Player.isWhite(playerToMove))
						gamePlyCount += 1;
				}
			}
		}
		// needed in case board is in an invalid state which will lead to more errors
		// this will be handled by the validation step.
		try {
			kingSquare[0] = (getPieces(Player.WHITE, PieceType.KING) == 0) ? Square.SQUARE_NONE : Bitboard.getFirstSquareIndex(getPieces(Player.WHITE, PieceType.KING));
			kingSquare[1] = (getPieces(Player.BLACK, PieceType.KING) == 0) ? Square.SQUARE_NONE : Bitboard.getFirstSquareIndex(getPieces(Player.BLACK, PieceType.KING));
			isCheck = calculateIsPlayerInCheck(playerToMove);
		} catch (Exception e) {

		}
		catch (AssertionError e) {
			
		}

		validateState();
		return this;
	}

	/**
	 * returns a string representing piece on a given square. Same used to generate
	 * FEN. Poor performance.
	 * 
	 * @param sq
	 * @return
	 */
	public String getPieceStringAt(int sq) {
		assert Square.validate(sq);
		int pt = getPieceAt(sq);
		String ret = PieceType.toString(pt);
		if (getPlayerAt(sq) != Player.WHITE)
			ret = ret.toLowerCase();
		return ret;
	}

	public String toFEN() {
		StringBuilder sb = new StringBuilder();
		int i = 56;
		while (i >= 0) {
			if (Player.NO_PLAYER == getPlayerAt(i)) {
				sb.append("1");
			} else {
				sb.append(getPieceStringAt(i));
			}
			if (i % 8 == 7) {
				if (i != 7)
					sb.append("/");
				i -= 16;
			}
			i++;
		}

		// color to move
		String colorToMove = getPlayerToMove() == Player.WHITE ? "w" : "b";
		sb.append(" ").append(colorToMove).append(" ");

		// castling rights
		if (!(getCastling_WK() || getCastling_WQ() || getCastling_BK() || getCastling_BQ())) {
			sb.append("-");
		} else {
			if (getCastling_WK()) {
				sb.append("K");
			}
			if (getCastling_WQ()) {
				sb.append("Q");
			}
			if (getCastling_BK()) {
				sb.append("k");
			}
			if (getCastling_BQ()) {
				sb.append("q");
			}
		}

		// en passant
		sb.append(" ");
		if (getEnpassantSquare() == Square.SQUARE_NONE) {
			sb.append("-");
		} else {
			sb.append(Square.squareToAlgebraicString(getEnpassantSquare()));
		}

		String fen = sb.toString();
		fen = fen.replaceAll("11111111", "8");
		fen = fen.replaceAll("1111111", "7");
		fen = fen.replaceAll("111111", "6");
		fen = fen.replaceAll("11111", "5");
		fen = fen.replaceAll("1111", "4");
		fen = fen.replaceAll("111", "3");
		fen = fen.replaceAll("11", "2");

		fen += " " + getQuietHalfmoveClock();
		int movenum = getGamePlyCount();
		if (Player.BLACK == getPlayerToMove())
			movenum--;
		movenum = 1 + movenum / 2;
		fen += " " + movenum;
		return fen;
	}

	/**
	 * heavy-handed validation of the game state. Is only meant to be used after
	 * loading from FEN and in tests. Tests done on move making and generation are
	 * supposed to ensure that all subsequent state transitions are valid.
	 * 
	 * @return
	 */
	public void validateState() {
		try {
			// validate that player bitboards are disjointed.
			if (Bitboard.popcount(getPlayerPieces(Player.WHITE) & getPlayerPieces(Player.BLACK)) != 0)
				throw new RuntimeException("Two players are cooccupying a square");
			// validate that piece types are disjointed.
			for (int sq : Square.SQUARES) {
				int temp = 0;
				for (int pt : PieceType.PIECE_TYPES) {
					if (Bitboard.testBit(getPieces(pt), sq))
						temp++;
				}
				if (temp > 1)
					throw new RuntimeException("Two pieces are cooccupying a square " + Square.squareToAlgebraicString(sq));
			}
			// validate enpassant
			if (getEnpassantSquare() != Square.SQUARE_NONE) {
				assert Square.validate(getEnpassantSquare());
				if (getPlayerToMove() == Player.WHITE && 5 != (getEnpassantSquare() / 8))
					throw new RuntimeException("Invalid enpassant square");
				if (getPlayerToMove() == Player.BLACK && 2 != (getEnpassantSquare() / 8))
					throw new RuntimeException("Invalid enpassant square");
			}
			// validate castling availability
			// Notice that this only concerns positions of king and rooks, but not whether
			// king is on check or whether the squares between rook and king are free, as
			// these conditions are subject to change
			if (getCastling_WK() && !(testPieceAt(PieceType.KING, Player.WHITE, Square.E1) && testPieceAt(PieceType.ROOK, Player.WHITE, Square.H1)))
				throw new RuntimeException("Invalid castling state!");
			if (getCastling_WQ() && !(testPieceAt(PieceType.KING, Player.WHITE, Square.E1) && testPieceAt(PieceType.ROOK, Player.WHITE, Square.A1)))
				throw new RuntimeException("Invalid castling state!");
			if (getCastling_BK() && !(testPieceAt(PieceType.KING, Player.BLACK, Square.E8) && testPieceAt(PieceType.ROOK, Player.BLACK, Square.H8)))
				throw new RuntimeException("Invalid castling state!");
			if (getCastling_BQ() && !(testPieceAt(PieceType.KING, Player.BLACK, Square.E8) && testPieceAt(PieceType.ROOK, Player.BLACK, Square.A8)))
				throw new RuntimeException("Invalid castling state!");
			
			if (Bitboard.popcount(getPieces(Player.WHITE, PieceType.KING)) != 1)
				throw new RuntimeException("Something wrong with kings!");
			if (Bitboard.popcount(getPieces(Player.BLACK, PieceType.KING)) != 1)
				throw new RuntimeException("Something wrong with kings!");
			
			// validate checks.
			if (calculateIsPlayerInCheck(getPlayerToMove()) != getIsCheck())
				throw new RuntimeException("Check flag mismatch!");
			if (calculateIsPlayerInCheck(Player.getOtherPlayer(getPlayerToMove())))
				throw new RuntimeException("King en prise!");
			// validate pawns on first and last ranks
			if (Bitboard.popcount(getPieces(PieceType.PAWN) & 0xff000000000000ffL) != 0)
				throw new RuntimeException("Pawns on first or last rank!");
			assert Player.validate(getPlayerToMove());
			// validate king denorm
			
			if (getKingSquare(Player.WHITE) != getFirstSquareIndex(getPieces(Player.WHITE, PieceType.KING)))
				throw new RuntimeException("Error in king denorm!");
			if (getKingSquare(Player.BLACK) != getFirstSquareIndex(getPieces(Player.BLACK, PieceType.KING)))
				throw new RuntimeException("Error in king denorm!");
			assert Square.validate(getKingSquare(Player.WHITE));
			assert Square.validate(getKingSquare(Player.BLACK));
		} catch (Exception e) {
			// uncomment when troubleshooting!
			// e.printStackTrace();
			throw new RuntimeException("Invalid Game State!");
		}
	}
}
