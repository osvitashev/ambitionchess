package searcher;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;

public class AlphaBetaSearcher {
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd = new Gamestate();
	private PrincipalVariation principalVariation = new PrincipalVariation();
	
	/**
	 * Should also take a comparator that can be used instead of SearchResult.isScoreLess
	 * This could be a way to fine tune engine's preference when it comes to choosing
	 * between a leaf node with a stalemate and a node with some numerical value in the positional evaluation.
	 */
	
	private int fullDepthSearchLimit=0;
	
	public Gamestate getBrd() {
		return brd;
	}

	public PrincipalVariation getPrincipalVariation() {
		return principalVariation;
	}

	
	public void setFullDepthSearchLimit(int fullDepthSearchLimit) {
		this.fullDepthSearchLimit = fullDepthSearchLimit;
	}

	/**
	 * Will need to figure a way to make this configurable somehow.
	 * @return SearchResult
	 */
	public long evaluateGamestate() {
		return 0;
	}
	
	/**
	 * MUST be called as a wrapper around EVERY return statement of the recursive alpha-beta search.
	 * This function is responsible to doing cleanup tasks such as freeing up movepool.
	 * 
	 * Can be used for other useful extensions: such as keeping an incremental count of the number of calls/returns to the alpha-beta.
	 * 
	 * @param movelistSize
	 * @param score - to be returned
	 * @return score
	 */
	private long returnScoreFromSearch(int movelistSize, long score) {
		movepool.resize(movelistSize);//this could be removed if movelist size was added as a parameter to the recursive search
		return score;
	}
		
	/**
	 * @return SearchResult
	 */
	public long doSearchForCheckmate(long alpha, long beta, int depth) {
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		int movepool_size_old = movepool.size();
		if(depth == fullDepthSearchLimit)
			//this matches an evaluation with an even score and no flags set.
			return returnScoreFromSearch(movepool_size_old, SearchResult.createWithDepthAndScore(depth, 0));
		move_generator.generateLegalMoves(brd, movepool);
		if (movepool.size() == movepool_size_old && brd.getIsCheck()) {
			long score = SearchResult.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchResult.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movepool_size_old, score);
			else
				return returnScoreFromSearch(movepool_size_old, alpha);
		}
//		else if (movepool.size() == movepool_size_old && !brd.getIsCheck()) {
//			HANDLE THE CASE FOR STALEMATE
//		}
		else {
			for (int i = movepool_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				long score = SearchResult.negateScore(
						doSearchForCheckmate(
							SearchResult.negateScore(beta),
							SearchResult.negateScore(alpha),
							depth+1
						)
					);
				brd.unmakeMove(move);
				
				if(SearchResult.isScoreGreaterOrEqual(score, beta)) {
					return returnScoreFromSearch(movepool_size_old, beta);
				}
					
				if(SearchResult.isScoreGreater(score, alpha)){
					alpha=score;
					principalVariation.addMoveAtDepth(move, depth);
				}
			}
		}
		
		return returnScoreFromSearch(movepool_size_old, alpha);
	}
}
