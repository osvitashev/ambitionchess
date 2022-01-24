package exchangeval;

import gamestate.Board;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.MoveType;

/**
 * Used to calculate outcomes of potential exchanges by performing series of
 * captures on a given square. This is very much inefficient, so it is meant to
 * be used as a 'golden standard' when testing Static Exchange Evaluation
 *
 */
public class ExpensiveExchangeEvaluator {
	private MovePool movepool = new MovePool();
	private int square;// square the evaluation is for.

	/**
	 * Generates all legal captures in a position. It is caller's responsibility to
	 * ensure that the ones played through fall on the correct square.
	 * 
	 * @param brd
	 * @param movepool
	 * @return
	 */
	private static int generateExchangeMoves(Board brd, MovePool movepool) {
		MoveGen.generatePawnCaptures(brd, movepool);
		MoveGen.generatePawnPromotionsAndCapturePromotions(brd, movepool);
		MoveGen.generateRookCaptures(brd, movepool);
		MoveGen.generateKnightCaptures(brd, movepool);
		MoveGen.generateBishopCaptures(brd, movepool);
		MoveGen.generateQueenCaptures(brd, movepool);
		MoveGen.generateKingCaptures(brd, movepool);
		return movepool.size();
	}

	private boolean toCaptureAndOccupy_opponentStep(Board brd) {
		boolean isWin = true;
		int movelist_size_old = movepool.size();
		generateExchangeMoves(brd, movepool);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			if ((Move.getMoveType(move) == MoveType.CAPTURE || Move.getMoveType(move) == MoveType.PROMO_CAPTURE) && square == Move.getSquareTo(move)) {
				// here return isWin iff every opponent's move leads to isWin
				brd.makeMove(move);
				isWin = toCaptureAndOccupy_step(brd);
				brd.unmakeMove(move);
				if (!isWin)
					break;
			}
		}
		movepool.resize(movelist_size_old);
		return isWin;
	}

	private boolean toCaptureAndOccupy_step(Board brd) {
		boolean isWin = false;
		int movelist_size_old = movepool.size();
		generateExchangeMoves(brd, movepool);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			if ((Move.getMoveType(move) == MoveType.CAPTURE || Move.getMoveType(move) == MoveType.PROMO_CAPTURE) && square == Move.getSquareTo(move)) {
				// here return isWin if ANY of our moves evaluates to isWin
				brd.makeMove(move);
				isWin = toCaptureAndOccupy_opponentStep(brd);
				brd.unmakeMove(move);
				if (isWin)
					break;
			}
		}
		movepool.resize(movelist_size_old);
		return isWin;
	}

	/**
	 * Determines if a given square can be occupied by a series of exchanges.
	 * Disregards material cost - only is occupation can be forced. En Passant is
	 * not considered because location of the captured pawn does not match te
	 * location of the pawn making the capture. Performs evaluation from the point
	 * of view of side-to-move.
	 * 
	 * @param brd
	 * @param square - occupied by the opponent
	 * @return
	 */
	public boolean toCaptureAndOccupy(Board brd, int square) {
		this.square = square;
		movepool.clear();
		return toCaptureAndOccupy_step(brd);
	}

}
