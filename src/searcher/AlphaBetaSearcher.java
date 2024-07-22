package searcher;

import gamestate.Gamestate;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.MoveType;

public class AlphaBetaSearcher {
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd;
	public PrincipalVariation principalVariation = new PrincipalVariation();
	
	AlphaBetaSearcher(Gamestate game){
		this.brd = game;
	}
	
	public void reset() {
		movepool.clear();
	}
	
	/**
	 * Will need to figure a way to make this configurable somehow.
	 * @return SearchResult
	 */
	public long evaluateGamestate() {
		return 0;
	}
		
	/**
	 * @return SearchResult
	 */
	public long doSearchForCheckmate(long alpha, long beta, int depth, int maxDepthLimit) {
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		if(depth == maxDepthLimit)
			return 0;//this matches an evaluation with an even score and no flags set.
		int movelist_size_old = movepool.size();
		move_generator.generateLegalMoves(brd, movepool);
		if (movepool.size() == movelist_size_old && brd.getIsCheck()) {
			long score = SearchResult.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchResult.isScoreGreater(score, alpha))
				return score;
			else
				return alpha;
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
					movepool.resize(movelist_size_old);
					return beta;
				}
					
				if(SearchResult.isScoreGreater(score, alpha)){
					alpha=score;
					principalVariation.addMoveAtDepth(move, depth);
				}
			}
		}
		
		movepool.resize(movelist_size_old);
		return alpha;
	}
}
