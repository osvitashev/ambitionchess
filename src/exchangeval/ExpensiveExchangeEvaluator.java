package exchangeval;

import gamestate.Board;
import gamestate.DebugLibrary;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.MoveType;
import gamestate.GlobalConstants.Player;

/**
 * Used to calculate outcomes of potential exchanges by performing series of
 * captures on a given square. This is very much inefficient, so it is meant to
 * be used as a 'golden standard' when testing Static Exchange Evaluation
 *
 */
public class ExpensiveExchangeEvaluator {
	private MovePool movepool = new MovePool();// main move pool used in the search
	private MovePool bufferpool = new MovePool();// temporary , used to dp move filtering.
	private int square;// square the evaluation is for.

	/**
	 * Generates all legal captures and capture promotions stopping the the square
	 * private field.
	 * 
	 * @param brd
	 * @return
	 */
	private int generateExchangeCaptureMoves(Board brd) {
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
	private int generateExchangeNonCaptureMoves(Board brd) {
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
	private boolean toCaptureAndOccupy_step(Board brd, boolean isOpponent) {
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
	private boolean toMoveAndOccupy_step(Board brd, boolean isOpponent) {
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
	public boolean toOccupy(Board brd, int square, int player) {
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
			brd = new Board(fen);
		}
		if (brd.getPlayerAt(square) == Player.NO_PLAYER)
			return toMoveAndOccupy_step(brd, false);
		return toCaptureAndOccupy_step(brd, false);
	}

}
