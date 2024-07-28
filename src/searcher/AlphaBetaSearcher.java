package searcher;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;
import java.util.function.ToIntFunction;


public class AlphaBetaSearcher {
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd = new Gamestate();
	private PrincipalVariation principalVariation = new PrincipalVariation();
	private Evaluator evaluator;
	
	AlphaBetaSearcher(Evaluator evaluator){
		this.evaluator = evaluator;
		evaluator.setSearcher(this);
	}
	
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
		evaluator.setSearcher(this);
	}

	/**
	 * it is incremented at the start of the recursive call. -1 as the start value makes it correct throughout.
	 */
	private int depth = -1;
	
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
	
	public int getDepth() {
		return depth;
	}

	
	public void setFullDepthSearchLimit(int fullDepthSearchLimit) {
		this.fullDepthSearchLimit = fullDepthSearchLimit;
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
//		System.out.println(brd.getMoveHistoryString() + " {"+brd.toFEN() + "} returning " + SearchOutcome.outcomeToStringInMaximixerPerspective(score, getDepth()%2==0));
		
		depth--;
		movepool.resize(movelistSize);//this could be removed if movelist size was added as a parameter to the recursive search
		return score;
	}
		
	/**
	 * @return SearchResult
	 */
	public long doSearth(long alpha, long beta) {
		depth++;
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		
		
		int movepool_size_old = movepool.size();
		if(depth == fullDepthSearchLimit)
			/**
			 * if the current player is in check - do a (quick?) test for checkmate
			 * 		if it is not a checkmate, return unknown score
			 * if the current player is not in check, return the evaluation score.
			 * 
			 * this way we are going to miss stalemates at the max depth....
			 */
			
			//this matches an evaluation with an even score and no flags set.
			return returnScoreFromSearch(movepool_size_old, SearchOutcome.createWithDepthAndScore(depth, evaluator.evaluate()));
		move_generator.generateLegalMoves(brd, movepool);
		if (movepool.size() == movepool_size_old && brd.getIsCheck()) {
			long score = SearchOutcome.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchOutcome.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movepool_size_old, score);
			else
				return returnScoreFromSearch(movepool_size_old, alpha);
		}
		else if (movepool.size() == movepool_size_old && !brd.getIsCheck()) {
			//HANDLE THE CASE FOR STALEMATE

			//the draw evaluation is not necessarily symmetric. It could effectively count as a win for the weaker opponent, or a loss for the stronger opponent.
			long score = SearchOutcome.createStalemate(depth, evaluator.assignScoreToDraw());
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchOutcome.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movepool_size_old, score);
			else
				return returnScoreFromSearch(movepool_size_old, alpha);
		}
		else {
			for (int i = movepool_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				long score = SearchOutcome.negateScore(
						doSearth(
							SearchOutcome.negateScore(beta),
							SearchOutcome.negateScore(alpha)
						)
					);
				brd.unmakeMove(move);
				
				if(SearchOutcome.isScoreGreaterOrEqual(score, beta)) {
					return returnScoreFromSearch(movepool_size_old, beta);
				}
					
				if(SearchOutcome.isScoreGreater(score, alpha)){
					alpha=score;
					principalVariation.addMoveAtDepth(move, depth);
				}
			}
		}
		
		return returnScoreFromSearch(movepool_size_old, alpha);
	}
}
