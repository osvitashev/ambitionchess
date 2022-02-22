package puzzler;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;

public class PuzzleSolver {

	private MovePool movepool = new MovePool();
	private int searchPlyDepth = 1;// value in halfmoves. Can only be an odd number.

	public PuzzleSolver(int searchDepth) {
		this.searchPlyDepth = 2*searchDepth-1;
	}

	public PuzzleSolver() {
		this.searchPlyDepth = 1;
	}

	// returns true is black is checkmated
	private boolean toPlayAndWin_opponentStep(Gamestate brd, int depth) {
		boolean isWin = true;
		int movelist_size_old = movepool.size();
		MoveGen.generateLegalMoves(brd, movepool);
		if (movepool.size() == movelist_size_old && brd.getIsCheck()) {// checkmate

			isWin = true;
		} else if (depth == searchPlyDepth) {
			isWin = false;
		} else if (movepool.size() != movelist_size_old) {
			for (int i = movelist_size_old; i < movepool.size(); ++i) {
				// here return isWin iff every opponent's move leads to isWin
				int move = movepool.get(i);

				brd.makeMove(move);
				isWin = toPlayAndWin_step(brd, depth + 1);
				brd.unmakeMove(move);

				if (!isWin)
					break;
			}
		}

		movepool.resize(movelist_size_old);
		return isWin;
	}

	private boolean toPlayAndWin_step(Gamestate brd, int depth) {
		boolean isWin = false;
		int movelist_size_old = movepool.size();
		MoveGen.generateLegalMoves(brd, movepool);
		if (movepool.size() == movelist_size_old) {

			isWin = false;
		} else if (depth == searchPlyDepth) {
			isWin = false;
		} else if (movepool.size() != movelist_size_old) {
			for (int i = movelist_size_old; i < movepool.size(); ++i) {
				// here return isWin if ANY of our moves evaluates to isWin
				int move = movepool.get(i);

				brd.makeMove(move);
				isWin = toPlayAndWin_opponentStep(brd, depth + 1);
				brd.unmakeMove(move);

				if (isWin)
					break;
			}
		}

		movepool.resize(movelist_size_old);
		return isWin;
	}

	/**
	 * 
	 * @param brd
	 * @param targetDepth - What the puzzle description typically says. 'Mate in 2' means 'us-them-us' when expanded.
	 * @return
	 */
	public int toPlayAndWin(Gamestate brd, int targetDepth) {
		searchPlyDepth = 2*targetDepth-1;
		return toPlayAndWin(brd);
	}

	public int toPlayAndWin(Gamestate brd) {
		movepool.clear();
		int movelist_size_old = 0;
		MoveGen.generateLegalMoves(brd, movepool);
		for (int i = movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);

			brd.makeMove(move);
			boolean temp = toPlayAndWin_opponentStep(brd, 1);
			brd.unmakeMove(move);

			if (temp)
				return move;
		}
		movepool.resize( movelist_size_old);
		return 0;// return code for no-move
	}
}
