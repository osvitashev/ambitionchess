package exchangeval;

import gamestate.Gamestate;
import gamestate.DebugLibrary;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.MoveType;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

/**
 * Used to calculate outcomes of potential exchanges by performing series of
 * captures on a given square. This is very much inefficient, so it is meant to
 * be used as a 'golden standard' when testing Static Exchange Evaluation
 *
 */
public class ExpensiveExchangeEvaluator {
	//point values: https://www.chessprogramming.org/Point_Value
	//private static final int[] ShannonPieceValues = { 100, 300, 300, 500, 900, 100000  };
	static final int[] BerlinerPieceValues = { 100, 320, 333, 510, 800, 100000  };
	
	private static final int[] pieceValues = BerlinerPieceValues;// index matches piece type.

	private MovePool movepool = new MovePool();// main move pool used in the search
	private MovePool bufferpool = new MovePool();// temporary , used to do move filtering.
	private int square;// square the evaluation is for.
	private boolean generateCaptures;
	
	public static int getPieceTypeValue(int piecetype, boolean maximizingPlayer) {
		DebugLibrary.validatePieceType(piecetype);
		int temp = pieceValues[piecetype];
		if(!maximizingPlayer)
			temp = -1*temp;
		return temp;
	}

	/**
	 * Generates all legal captures and capture promotions stopping the the square
	 * private field.
	 * 
	 * @param brd
	 * @return
	 */
	private int generateExchangeCaptureMoves(Gamestate brd) {
		bufferpool.clear();
		MoveGen.generatePawnCaptures(brd, bufferpool);
		MoveGen.generatePawnPromotionsAndCapturePromotions(brd, bufferpool);
		MoveGen.generateRookCaptures(brd, bufferpool);
		MoveGen.generateKnightCaptures(brd, bufferpool);
		MoveGen.generateBishopCaptures(brd, bufferpool);
		MoveGen.generateQueenCaptures(brd, bufferpool);
		MoveGen.generateKingCaptures(brd, bufferpool);
		for (int i = 0; i < bufferpool.size(); ++i) {
			int move = bufferpool.get(i);
			if ((Move.getMoveType(move) == MoveType.CAPTURE || Move.getMoveType(move) == MoveType.PROMO_CAPTURE) && square == Move.getSquareTo(move)) {
				movepool.add(move);
			}
		}
		return movepool.size();
	}

	/**
	 * Generates legal non-captures in a position for the private square field. Only
	 * includes NORMAL and PROMO and DOUBLE_PUSH move type.
	 * 
	 * @param brd
	 * @return
	 */
	private int generateExchangeNonCaptureMoves(Gamestate brd) {
		bufferpool.clear();
		MoveGen.generatePawnMoves(brd, bufferpool);
		MoveGen.generatePawnPromotionsAndCapturePromotions(brd, bufferpool);
		MoveGen.generateRookMoves(brd, bufferpool);
		MoveGen.generateKnightMoves(brd, bufferpool);
		MoveGen.generateBishopMoves(brd, bufferpool);
		MoveGen.generateQueenMoves(brd, bufferpool);
		MoveGen.generateKingMoves(brd, bufferpool);
		for (int i = 0; i < bufferpool.size(); ++i) {
			int move = bufferpool.get(i);
			if ((Move.getMoveType(move) == MoveType.PROMO || Move.getMoveType(move) == MoveType.NORMAL || Move.getMoveType(move) == MoveType.DOUBLE_PUSH)
					&& square == Move.getSquareTo(move)) {
				movepool.add(move);
			}
		}
		return movepool.size();
	}

	/**
	 * Negamax/alpha beta type recursive algorithm.
	 * 
	 * @param brd
	 * @param isOpponent - determines whether we are minimizing or maximizing.
	 * @return
	 */
	private boolean toCaptureAndOccupy_step(Gamestate brd, boolean isOpponent) {
		boolean isDone = isOpponent;
		int movelist_size_old = movepool.size();
		generateExchangeCaptureMoves(brd);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			brd.makeMove(move);
			isDone = toCaptureAndOccupy_step(brd, !isOpponent);
			brd.unmakeMove(move);
			if (isDone && !isOpponent || !isDone && isOpponent)
				break;
		}
		movepool.resize(movelist_size_old);
		return isDone;
	}

	/**
	 * Negamax/alpha beta type recursive algorithm.
	 * 
	 * @param brd
	 * @param isOpponent - determines whether we are minimizing or maximizing.
	 * @return
	 */
	private boolean toMoveAndOccupy_step(Gamestate brd, boolean isOpponent) {
		boolean isDone = isOpponent;
		int movelist_size_old = movepool.size();
		generateExchangeNonCaptureMoves(brd);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			brd.makeMove(move);
			isDone = toCaptureAndOccupy_step(brd, !isOpponent);
			brd.unmakeMove(move);
			if (isDone && !isOpponent || !isDone && isOpponent)
				break;
		}
		movepool.resize(movelist_size_old);
		return isDone;
	}

	/**
	 * Determines if a given square can be occupied by a series of exchanges.
	 * Disregards material cost - only if occupation can be forced. En Passant is
	 * not considered because location of the captured pawn does not match the
	 * location of the pawn making the capture. IMPORTANT: player argument overrides
	 * FEN side-to-move.
	 * 
	 * @param brd
	 * @param square
	 * @param player
	 * @return
	 */
	public boolean toOccupy(Gamestate brd, int square, int player) {
		DebugLibrary.validatePlayer(player);
		DebugLibrary.validateSquare(square);
		if (brd.getPlayerAt(square) == player)
			return false;// friendly capture
		this.square = square;
		movepool.clear();
		// This is an insanely horrible substitute for a null move...
		if (brd.getPlayerToMove() != player) {
			String fen = brd.toFEN();
			fen = fen.replace((player == Player.WHITE) ? " b " : " w ", (player == Player.BLACK) ? " b " : " w ");
			brd = new Gamestate(fen);
		}
		if (brd.getPlayerAt(square) == Player.NO_PLAYER)
			return toMoveAndOccupy_step(brd, false);
		return toCaptureAndOccupy_step(brd, false);
	}

	private int incrementalMoveValue(int move, boolean maximizingPlayer) {
		int retVal = 0;
		if (Move.getMoveType(move) == MoveType.PROMO_CAPTURE) {
			throw new RuntimeException("NOT DONE YET!");
		} else if (Move.getMoveType(move) == MoveType.CAPTURE) {
			retVal = pieceValues[Move.getPieceCapturedType(move)];
		} else if (Move.getMoveType(move) == MoveType.PROMO) {
			throw new RuntimeException("NOT DONE YET!");
		}
		if(!maximizingPlayer)
			retVal = -1*retVal;
		return retVal;
	}
	
	private int evaluateSquareInPosition(Gamestate brd, int currentAccumulation, boolean maximizingPlayer) {
		return currentAccumulation;
	}

	// current implementation does not leave an option of rejecting a capture -
	// effectively this is the Occupy At All Costs version of exchanger.
	// ACTUALLY, NO! the current version returns material loss even of it does not lead to successful occupation.
	private int toExchange_step(Gamestate brd, int alpha, int beta, int currentValue, boolean maximizingPlayer) {
		int retVal = evaluateSquareInPosition(brd, currentValue,maximizingPlayer);
		int movelist_size_old = movepool.size();
		if(generateCaptures)
			generateExchangeCaptureMoves(brd);
		else {
			generateExchangeNonCaptureMoves(brd);
			generateCaptures = true;
		}
		if (maximizingPlayer) {
			int value = retVal;
			for (int i = movelist_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				value = Math.max(value, toExchange_step(brd, alpha, beta, currentValue + incrementalMoveValue(move, maximizingPlayer), false));
	//			System.out.println("Move " + Move.moveToString(move) + " has outcome of " + value);
				brd.unmakeMove(move);
				// if (value >= beta)
				// break;
				// alpha = Math.max(alpha, value);
			}
			retVal = value;
		} else {
			int value = retVal;
			for (int i = movelist_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				value = Math.min(value, toExchange_step(brd, alpha, beta, currentValue + incrementalMoveValue(move, maximizingPlayer), true));
	//			System.out.println("Move " + Move.moveToString(move) + " has outcome of " + value);
				brd.unmakeMove(move);
				// if (value <= alpha)
				// break;
				// beta = Math.min(beta, value);
			}
			retVal = value;

		}

		movepool.resize(movelist_size_old);
		return retVal;
	}

	public int toWinMaterial(Gamestate brd, int square) {
		DebugLibrary.validateSquare(square);		
		generateCaptures = brd.getPlayerAt(square) != Player.NO_PLAYER;
		
		this.square = square;
		movepool.clear();
		return toExchange_step(brd, -pieceValues[PieceType.KING], pieceValues[PieceType.KING], 0, true);
	}

}
