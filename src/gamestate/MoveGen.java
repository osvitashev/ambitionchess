package gamestate;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/**
 * Class used to generate lists of pseudo-legal moves given a positions.
 * 
 * Contains methods of the form {{public static int generateXXX(Board brd, int[]
 * movelist, int movelist_size, int move)}} all of which generate moves, and
 * then put them through addToMoveListIfValid to validate them and set the check
 * flag on opponent's king. The idea is to be able to fine tune the performance
 * by doing early rejection of illegal moves without affecting legal move
 * generation and tests.
 * 
 *
 */
public class MoveGen {
	/**
	 * An abstract representation of a method which generates legal moves and
	 * appends them to a move list returning the new move list size. For now used to
	 * simplify move generation tests.
	 *
	 */
	public interface LegalMoveGenerator {
		int generateLegalMoves(Board brd, MovePool movepool);
	}

	/**
	 * Validates a candidate move for king exposure. If the move is valid (does not
	 * leave player's king in check), sets check flag for the other side. Then Adds
	 * the move to the return collection. This method is intended as an intermediary
	 * between various move generators for individual pieces.
	 * 
	 * Returns the new movelist_size
	 * 
	 * @param brd
	 * @param MovePool
	 * @param move
	 * @return movelist_size Updated collection size
	 */
	public static int addToMoveListIfValid(Board brd, MovePool movepool, int move) {
		brd.makeDirtyMove(move);
		if (brd.validateKingExposure()) {
			move = Move.setCheck(move, brd.isPlayerInCheck(brd.getPlayerToMove()));
			movepool.add(move);
		}
		brd.unmakeDirtyMove(move);
		return movepool.size();
	}

	/**
	 * Generates legal enpassant moves (up to two) for a given position. Sets check
	 * flag on them.
	 * 
	 * @param brd           valid game state
	 * @param MovePool
	 * @return movelist_size Updated collection size
	 */
	public static int generateEnpassant(Board brd, MovePool movepool) {
		int enpassantSquare = brd.getEnpassantSquare();
		int player = brd.getPlayerToMove();
		int otherPlayer = Player.getOtherPlayer(player);
		if (enpassantSquare != Square.SQUARE_NONE) {
			long attackersBitboard = brd.getPieces(player, PieceType.PAWN) & BitboardGen.getPawnAttackSet(enpassantSquare, otherPlayer);
			{
				int bi = 0;
				for (long zarg = attackersBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.bitScanForward(barg);
					int move = Move.createEnpassant(bi, enpassantSquare, player);
					addToMoveListIfValid(brd, movepool, move);
				}
			}

		}
		return movepool.size();
	}

	/**
	 * Generates legal single and double pawn pushes. This does not include
	 * promotions! Validates king exposure for own king and sets check flag for
	 * opponent's king. If the move is legal, it is added to movelist.
	 * 
	 * @param brd           valid game state
	 * @param movepool
	 * @return movelist_size Updated collection size
	 */
	public static int generatePawnMoves(Board brd, MovePool movepool) {
		int player = brd.getPlayerToMove();
		int move;
		long pawnBB;
		switch (player) {
		case Player.WHITE:
			pawnBB = brd.getPieces(player, PieceType.PAWN) & ~0x00FF000000000000L;// mask for 7th rank [a7-h7]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				// single
				if (0 == (brd.getOccupied() & Bitboard.shiftNorth(barg))) {// use shiftNorth to check is the target square is empty
					move = Move.createNormal(bi, bi + 8, PieceType.PAWN, player);
					addToMoveListIfValid(brd, movepool, move);
					// double
					if (0L != (barg & 0x000000000000FF00L)) {// bi is in [a2-h2]
						if (0 == (brd.getOccupied() & Bitboard.shiftNorth(Bitboard.shiftNorth(barg)))) {
							move = Move.createDoublePush(bi, bi + 16, player);
							addToMoveListIfValid(brd, movepool, move);
						}
					}
				}
			}
		}
			break;
		default:// black player
			pawnBB = brd.getPieces(player, PieceType.PAWN) & ~0x000000000000FF00L;// mask for 2nd rank [a2-h2]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				// single
				if (0 == (brd.getOccupied() & Bitboard.shiftSouth(barg))) {// use shiftSouth to check is the target square is empty
					move = Move.createNormal(bi, bi - 8, PieceType.PAWN, player);
					addToMoveListIfValid(brd, movepool, move);
					// double
					if (0L != (barg & 0x00FF000000000000L)) {// bi is in [a7-h7]
						if (0 == (brd.getOccupied() & Bitboard.shiftSouth(Bitboard.shiftSouth(barg)))) {
							move = Move.createDoublePush(bi, bi - 16, player);
							addToMoveListIfValid(brd, movepool, move);
						}
					}
				}
			}
		}
		}
		return movepool.size();
	}

	public static int generatePawnCaptures(Board brd, MovePool movepool) {
		// TODO: THIS IS INEFFECIENT...
		int player = brd.getPlayerToMove();
		long pawnBB;
		switch (player) {
		case Player.WHITE:
			pawnBB = brd.getPieces(player, PieceType.PAWN) & ~0x00FF000000000000L;// mask for 7th rank [a7-h7]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generatePawnCaptures_helper(brd, bi, movepool);
			}
		}
			break;
		default:// black player
			pawnBB = brd.getPieces(player, PieceType.PAWN) & ~0x000000000000FF00L;// mask for 2nd rank [a2-h2]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generatePawnCaptures_helper(brd, bi, movepool);
			}
		}
		}
		return movepool.size();
	}

	private static int generatePawnCaptures_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getPawnAttackSet(sqFrom, brd.getPlayerToMove()) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sqFrom, bi, PieceType.PAWN, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generatePawnPromotions(Board brd, MovePool movepool) {
		// TODO: THIS IS STUPIDLY INEFFECIENT...
		int player = brd.getPlayerToMove();
		long pawnBB;
		switch (player) {
		case Player.WHITE:
			pawnBB = brd.getPieces(player, PieceType.PAWN) & 0x00FF000000000000L;// mask for 7th rank [a7-h7]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generatePawnPromotions_helper(brd, bi, movepool);
			}
		}
			break;
		default:// black player
			pawnBB = brd.getPieces(player, PieceType.PAWN) & 0x000000000000FF00L;// mask for 2nd rank [a2-h2]
		{
			int bi = 0;
			for (long zarg = pawnBB, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generatePawnPromotions_helper(brd, bi, movepool);
			}
		}
		}
		return movepool.size();
	}

	private static int generatePawnPromotions_helper(Board brd, int sqFrom, MovePool movepool) {
		// sqFrom is guaranteed to be rank to be either 2 or 7
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getPawnAttackSet(sqFrom, brd.getPlayerToMove()) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapturePromo(sqFrom, bi, brd.getPieceAt(bi), PieceType.ROOK, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createCapturePromo(sqFrom, bi, brd.getPieceAt(bi), PieceType.KNIGHT, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createCapturePromo(sqFrom, bi, brd.getPieceAt(bi), PieceType.BISHOP, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createCapturePromo(sqFrom, bi, brd.getPieceAt(bi), PieceType.QUEEN, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		switch (brd.getPlayerToMove()) {
		case Player.WHITE:
			if (0 == (brd.getOccupied() & Bitboard.shiftNorth(Bitboard.setBit(0L, sqFrom)))) {// use shiftNorth to check is the target square is empty
				move = Move.createPromo(sqFrom, sqFrom + 8, PieceType.ROOK, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom + 8, PieceType.KNIGHT, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom + 8, PieceType.BISHOP, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom + 8, PieceType.QUEEN, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
			break;
		default:// black player
			if (0 == (brd.getOccupied() & Bitboard.shiftSouth(Bitboard.setBit(0L, sqFrom)))) {// use shiftNorth to check is the target square is empty
				move = Move.createPromo(sqFrom, sqFrom - 8, PieceType.ROOK, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom - 8, PieceType.KNIGHT, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom - 8, PieceType.BISHOP, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
				move = Move.createPromo(sqFrom, sqFrom - 8, PieceType.QUEEN, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	/**
	 * Generates legal king moves. Castling handled separately. Validates king
	 * exposure for own king and sets check flag for opponent's king. If the move is
	 * legal, it is added to movelist.
	 * 
	 * @param brd           valid game state
	 * @param movelist      return collection of moves
	 * @param movelist_size initial size of return collection and the place of the
	 *                      first insertion.
	 * @return movelist_size Updated collection size
	 */
	public static int generateKingMoves(Board brd, MovePool movepool) {
		int sq_from = Bitboard.bitScanForward(brd.getPieces(brd.getPlayerToMove(), PieceType.KING));
		int move;
		long targetBitboard = BitboardGen.getKingSet(sq_from) & brd.getEmpty();
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createNormal(sq_from, bi, PieceType.KING, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();

	}

	/**
	 * Generates legal king captures. Validates king exposure for own king and sets
	 * check flag for opponent's king. If the move is legal, it is added to
	 * movelist.
	 * 
	 * @param brd           valid game state
	 * @param movelist      return collection of moves
	 * @param movelist_size initial size of return collection and the place of the
	 *                      first insertion.
	 * @return movelist_size Updated collection size
	 */
	public static int generateKingCaptures(Board brd, MovePool movepool) {
		int sq_from = Bitboard.bitScanForward(brd.getPieces(brd.getPlayerToMove(), PieceType.KING));
		int move;
		long targetBitboard = BitboardGen.getKingSet(sq_from) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sq_from, bi, PieceType.KING, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateKnightMoves(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.KNIGHT),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateKnightMoves_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateKnightMoves_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getKnightSet(sqFrom) & brd.getEmpty();
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createNormal(sqFrom, bi, PieceType.KNIGHT, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateKnightCaptures(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.KNIGHT),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateKnightCaptures_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateKnightCaptures_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getKnightSet(sqFrom) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sqFrom, bi, PieceType.KNIGHT, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateRookMoves(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.ROOK),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateRookMoves_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateRookMoves_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getRookSet(sqFrom, brd.getOccupied()) & brd.getEmpty();
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createNormal(sqFrom, bi, PieceType.ROOK, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateRookCaptures(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.ROOK),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateRookCaptures_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateRookCaptures_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getRookSet(sqFrom, brd.getOccupied()) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sqFrom, bi, PieceType.ROOK, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateBishopMoves(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.BISHOP),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateBishopMoves_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateBishopMoves_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getBishopSet(sqFrom, brd.getOccupied()) & brd.getEmpty();
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createNormal(sqFrom, bi, PieceType.BISHOP, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateBishopCaptures(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.BISHOP),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateBishopCaptures_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateBishopCaptures_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getBishopSet(sqFrom, brd.getOccupied()) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sqFrom, bi, PieceType.BISHOP, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateQueenMoves(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.QUEEN),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateQueenMoves_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateQueenMoves_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getQueenSet(sqFrom, brd.getOccupied()) & brd.getEmpty();
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createNormal(sqFrom, bi, PieceType.QUEEN, brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	public static int generateQueenCaptures(Board brd, MovePool movepool) {
		{
			int bi = 0;
			for (long zarg = brd.getPieces(brd.getPlayerToMove(), PieceType.QUEEN),
					barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				generateQueenCaptures_helper(brd, bi, movepool);
			}
		}
		return movepool.size();
	}

	private static int generateQueenCaptures_helper(Board brd, int sqFrom, MovePool movepool) {
		DebugLibrary.validateSquare(sqFrom);
		int move;
		long targetBitboard = BitboardGen.getQueenSet(sqFrom, brd.getOccupied()) & brd.getPlayerPieces(Player.getOtherPlayer(brd.getPlayerToMove()));
		{
			int bi = 0;
			for (long zarg = targetBitboard, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.bitScanForward(barg);
				move = Move.createCapture(sqFrom, bi, PieceType.QUEEN, brd.getPieceAt(bi), brd.getPlayerToMove());
				addToMoveListIfValid(brd, movepool, move);
			}
		}
		return movepool.size();
	}

	/**
	 * Generates castling moves. Validates king exposure for own king and sets check
	 * flag for opponent's king. If the move is legal, it is added to movelist.
	 * 
	 * @param brd           valid game state
	 * @param movelist      return collection of moves
	 * @param movelist_size initial size of return collection and the place of the
	 *                      first insertion.
	 * @return movelist_size Updated collection size
	 */
	public static int generateCastling(Board brd, MovePool movepool) {
		// OPTIMIZE: For castling addToMoveListIfValid is redundant move validation
		int player = brd.getPlayerToMove();
		int otherPlayer = Player.getOtherPlayer(player);
		switch (player) {
		case Player.WHITE:
			if (brd.getCastling_WK() && !brd.isCheck()) {
				if (brd.getPieceAt(Square.F1) == PieceType.NO_PIECE && brd.getPieceAt(Square.G1) == PieceType.NO_PIECE) {
					if (!brd.isSquareAttackedBy(Square.F1, otherPlayer) && !brd.isSquareAttackedBy(Square.G1, otherPlayer)) {
						int move = Move.createCastleKing(Player.WHITE);
						addToMoveListIfValid(brd, movepool, move);
					}
				}
			}
			if (brd.getCastling_WQ() && !brd.isCheck()) {
				if (brd.getPieceAt(Square.B1) == PieceType.NO_PIECE && brd.getPieceAt(Square.C1) == PieceType.NO_PIECE && brd.getPieceAt(Square.D1) == PieceType.NO_PIECE) {
					if (!brd.isSquareAttackedBy(Square.C1, otherPlayer) && !brd.isSquareAttackedBy(Square.D1, otherPlayer)) {
						int move = Move.createCastleQueen(Player.WHITE);
						addToMoveListIfValid(brd, movepool, move);
					}
				}
			}
			break;
		default:// black player
			if (brd.getCastling_BK() && !brd.isCheck()) {
				if (brd.getPieceAt(Square.F8) == PieceType.NO_PIECE && brd.getPieceAt(Square.G8) == PieceType.NO_PIECE) {
					if (!brd.isSquareAttackedBy(Square.F8, otherPlayer) && !brd.isSquareAttackedBy(Square.G8, otherPlayer)) {
						int move = Move.createCastleKing(Player.BLACK);
						addToMoveListIfValid(brd, movepool, move);
					}
				}
			}
			if (brd.getCastling_BQ() && !brd.isCheck()) {
				if (brd.getPieceAt(Square.B8) == PieceType.NO_PIECE && brd.getPieceAt(Square.C8) == PieceType.NO_PIECE && brd.getPieceAt(Square.D8) == PieceType.NO_PIECE) {
					if (!brd.isSquareAttackedBy(Square.C8, otherPlayer) && !brd.isSquareAttackedBy(Square.D8, otherPlayer)) {
						int move = Move.createCastleQueen(Player.BLACK);
						addToMoveListIfValid(brd, movepool, move);
					}
				}
			}
		}
		return movepool.size();
	}

	public static int generateLegalMoves(Board brd, MovePool movepool) {
		generateEnpassant(brd, movepool);

		generatePawnMoves(brd, movepool);
		generatePawnCaptures(brd, movepool);
		generatePawnPromotions(brd, movepool);

		generateRookMoves(brd, movepool);
		generateRookCaptures(brd, movepool);

		generateKnightMoves(brd, movepool);
		generateKnightCaptures(brd, movepool);

		generateBishopMoves(brd, movepool);
		generateBishopCaptures(brd, movepool);

		generateQueenMoves(brd, movepool);
		generateQueenCaptures(brd, movepool);

		generateKingMoves(brd, movepool);
		generateKingCaptures(brd, movepool);

		generateCastling(brd, movepool);
		return movepool.size();

	}

}
