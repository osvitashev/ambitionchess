package puzzler;

import gamestate.Board;
import gamestate.MoveGen;

public class PuzzleSolver {
	
	private int[] movelist = new int[200];
	private int movelist_size=0;
	private int searchDepth=1;// value in halfmoves. Can only be an odd number.
	
	
	// returns true is black is checkmated
	boolean blackStep(Board brd, int depth) {
		boolean blackLost=false;
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		if(movelist_size == movelist_size_old && brd.isCheck()){
			
			blackLost = true;
		}
		else if(depth == searchDepth) {
			blackLost = false;
		}
		else if(movelist_size != movelist_size_old) {
			for(int i=movelist_size_old; i<movelist_size; ++i) {
				int move = movelist[i];
				
				brd.makeMove(move);
				blackLost = whiteStep(brd, depth+1);
				brd.unmakeMove(move);
				
				if(blackLost)
					break;
			}
		}
		
		movelist_size = movelist_size_old;
		return blackLost;
	}
	
	boolean whiteStep(Board brd, int depth) {
		boolean blackLost=true;
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		if(movelist_size != movelist_size_old || !brd.isCheck()){
			
			blackLost = false;
		}
		else if(depth == searchDepth) {
			blackLost = false;
		}
		else if(movelist_size != movelist_size_old) {
			for(int i=movelist_size_old; i<movelist_size; ++i) {
				int move = movelist[i];
				
				brd.makeMove(move);
				blackLost = blackStep(brd, depth+1);
				brd.unmakeMove(move);
				
				if(!blackLost)
					break;
			}
		}
		
		movelist_size = movelist_size_old;
		return blackLost;
	}
	
	public int whiteToPlayAndWin(Board brd, int depth) {
		movelist_size=0;//reset
		int movelist_size_old = movelist_size;
		movelist_size = MoveGen.generateLegalMoves(brd, movelist, movelist_size);
		for(int i=movelist_size_old; i<movelist_size; ++i) {
			int move = movelist[i];
			
			brd.makeMove(move);
			boolean temp = blackStep(brd, depth+1);
			brd.unmakeMove(move);
			
			if(temp)
				return move;
		}
		
		
		movelist_size = movelist_size_old;
		return 0;//return code for no-move 
	}

}
