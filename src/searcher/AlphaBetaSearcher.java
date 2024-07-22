package searcher;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;

public class AlphaBetaSearcher {
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd = new Gamestate();
	private PrincipalVariation principalVariation = new PrincipalVariation();
	
	public Gamestate getBrd() {
		return brd;
	}

	public PrincipalVariation getPrincipalVariation() {
		return principalVariation;
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
	public long doSearchForCheckmate(long alpha, long beta, int depth, int maxDepthLimit) {
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		int movelist_size_old = movepool.size();
		if(depth == maxDepthLimit)
			return returnScoreFromSearch(movelist_size_old, 0);//this matches an evaluation with an even score and no flags set.
		move_generator.generateLegalMoves(brd, movepool);
		if (movepool.size() == movelist_size_old && brd.getIsCheck()) {
			long score = SearchResult.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchResult.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movelist_size_old, score);
			else
				return returnScoreFromSearch(movelist_size_old, alpha);
		}
		else {
			for (int i = movelist_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				long score = SearchResult.negateScore(
						doSearchForCheckmate(
							SearchResult.negateScore(beta),
							SearchResult.negateScore(alpha),
							depth+1,
							maxDepthLimit
						)
					);
				brd.unmakeMove(move);
				
				if(SearchResult.isScoreGreaterOrEqual(score, beta)) {
					return returnScoreFromSearch(movelist_size_old, beta);
				}
					
				if(SearchResult.isScoreGreater(score, alpha)){
					alpha=score;
					principalVariation.addMoveAtDepth(move, depth);
				}
			}
		}
		
		return returnScoreFromSearch(movelist_size_old, alpha);
	}
}
