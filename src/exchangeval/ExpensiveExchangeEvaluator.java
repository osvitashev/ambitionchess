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

	/**
	 * Negamax/alpha beta type recursive algorithm.
	 * @param brd
	 * @param isOpponent - determines whether we are minimizing or maximizing.
	 * @return
	 */
	private boolean toCaptureAndOccupy_step(Board brd, boolean isOpponent) {
		boolean isDone = isOpponent;
		int movelist_size_old = movepool.size();
		generateExchangeMoves(brd, movepool);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			if ((Move.getMoveType(move) == MoveType.CAPTURE || Move.getMoveType(move) == MoveType.PROMO_CAPTURE) && square == Move.getSquareTo(move)) {
				brd.makeMove(move);
				isDone = toCaptureAndOccupy_step(brd, !isOpponent);
				brd.unmakeMove(move);
				if (isDone && !isOpponent || !isDone && isOpponent)
					break;
			}
		}
		movepool.resize(movelist_size_old);
		return isDone;
	}

	/**
	 * Determines if a given square can be occupied by a series of exchanges.
	 * Disregards material cost - only is occupation can be forced. En Passant is
	 * not considered because location of the captured pawn does not match the
	 * location of the pawn making the capture.
	 * IMPORTANT: player argument overrides FEN side-to-move.
	 * 
	 * @param brd
	 * @param square
	 * @return
	 */
	public boolean toCaptureAndOccupy(Board brd, int square, int player) {
		DebugLibrary.validatePlayer(player);
		DebugLibrary.validateSquare(square);
		this.square = square;
		movepool.clear();
		//This is an insanely horrible substitute for a null move...
		if(brd.getPlayerToMove() != player) {
			String fen = brd.toFEN();
			fen = fen.replace((player == Player.WHITE)? " b " : " w ", (player == Player.BLACK)? " b " : " w ");
			brd = new Board(fen);
		}
		return toCaptureAndOccupy_step(brd, false);
	}

}
