package puzzler;

import gamestate.Board;
import gamestate.MoveGen;
import gamestate.Move;

public class PuzzleSolver {

	private int[] movelist = new int[1000];
	private int movelist_size = 0;
	private int searchPlyDepth = 1;// value in halfmoves. Can only be an odd number.

	public PuzzleSolver(int searchDepth) {
		this.searchPlyDepth = 2*searchDepth-1;
	}

	public PuzzleSolver() {
		this.searchPlyDepth = 1;
	}

	// returns true is black is checkmated
	private boolean toPlayAndWin_opponentStep(Board brd, int depth) {
		boolean isWin = true;
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		if (movelist_size == movelist_size_old && brd.isCheck()) {// checkmate

			isWin = true;
		} else if (depth == searchPlyDepth) {
			isWin = false;
		} else if (movelist_size != movelist_size_old) {
			for (int i = movelist_size_old; i < movelist_size; ++i) {
				// here return isWin iff every opponent's move leads to isWin
				int move = movelist[i];

				brd.makeMove(move);
				isWin = toPlayAndWin_step(brd, depth + 1);
				brd.unmakeMove(move);

				if (!isWin)
					break;
			}
		}

		movelist_size = movelist_size_old;
		return isWin;
	}

	private boolean toPlayAndWin_step(Board brd, int depth) {
		boolean isWin = false;
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		if (movelist_size == movelist_size_old) {

			isWin = false;
		} else if (depth == searchPlyDepth) {
			isWin = false;
		} else if (movelist_size != movelist_size_old) {
			for (int i = movelist_size_old; i < movelist_size; ++i) {
				// here return isWin if ANY of our moves evaluates to isWin
				int move = movelist[i];

				brd.makeMove(move);
				isWin = toPlayAndWin_opponentStep(brd, depth + 1);
				brd.unmakeMove(move);

				if (isWin)
					break;
			}
		}

		movelist_size = movelist_size_old;
		return isWin;
	}

	public int toPlayAndWin(Board brd, int targetDepth) {
		searchPlyDepth = 2*targetDepth+1;
		return toPlayAndWin(brd);
	}

	public int toPlayAndWin(Board brd) {
		movelist_size = 0;// reset
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		for (int i = movelist_size_old; i < movelist_size; ++i) {
			int move = movelist[i];

			brd.makeMove(move);
			boolean temp = toPlayAndWin_opponentStep(brd, 1);
			brd.unmakeMove(move);

			if (temp)
				return move;
		}

		movelist_size = movelist_size_old;
		return 0;// return code for no-move
	}

}
