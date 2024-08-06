package searcher;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

import gamestate.Gamestate;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;
import searcher.Evaluator.Builder;

public class AlphaBetaSearcher {
	private SearchContext context;
	private PrincipalVariation principalVariation = new PrincipalVariation();
	/**
	 * only returns the score part of the SearchOutcome.
	 * It is caller's responsibility to distinguish between different types of draw if it is needed.
	 */
	private ToIntFunction<AlphaBetaSearcher>  drawScoreAssigner;
	
	public AlphaBetaSearcher(Builder bld) {
		context=bld.context;
		drawScoreAssigner=bld.drawScoreAssigner;
	}
	
	/**
	 * marker to the movelist generated at the given depth.
	 */
	private int[] numMovesAtDepth = new int[50];
	
	/**
	 * it is incremented at the start of the recursive call. -1 as the start value makes it correct throughout.
	 */
	private int depth = -1;//todo: can actually be dropped. All we need is the game plycount at the beginning of the search. Search depth can be calculated using that and the updated game state!!!
		
	
	private int fullDepthSearchLimit=0;

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
	private long returnScoreFromSearch(long score) {
//		System.out.println(brd.getMoveHistoryString() + " {"+brd.toFEN() + "} returning " + SearchOutcome.outcomeToStringInMaximixerPerspective(score, getDepth()%2==0));
		context.getMovepool().resize(context.getMovepool().size()-numMovesAtDepth[getDepth()]);
		depth--;
		//this could be removed if movelist size was added as a parameter to the recursive search
		return score;
	}
		
	/**
	 * currently implements a fail-hard version of alpha-beta.
	 * This means that the return value is strictly in the range [alpha, beta], Inclusive!
	 * If the search is done with an aspiration window, return value of either alpha or beta would mean the search going out of bounds.
	 * 
	 * @return SearchResult
	 */
	public long doSearch(long alpha, long beta) {
//		if(depth==-1)System.out.println(">> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(alpha, true) + " >> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(beta,true));
		depth++;
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		numMovesAtDepth[getDepth()]=0;
		
		MovePool movepool = context.getMovepool();
		Gamestate brd=context.getBrd();
		MoveGen move_generator = context.getMoveGenerator();
		Evaluator evaluator = context.getEvaluator();
		
		int movepool_size_old = movepool.size();
		if(depth == fullDepthSearchLimit) {
			if(brd.getIsCheck() && move_generator.noMovesAreAvailable(brd, movepool)) {
				long score = SearchOutcome.createCheckmate(depth);
				//notice that we do not need to resize the move pool - no new moves have been generated.
				if(SearchOutcome.compare(score, SearchOutcome.GTE, beta))
					return returnScoreFromSearch(beta);
				else if(SearchOutcome.compare(score, SearchOutcome.GT, alpha))
					return returnScoreFromSearch(score);
				else
					return returnScoreFromSearch(alpha);
			}
			else {
				return returnScoreFromSearch(SearchOutcome.createWithDepthAndScore(depth, evaluator.evaluate()));
			}
		}
			
		move_generator.generateLegalMoves(brd, movepool);
		numMovesAtDepth[getDepth()]=movepool.size()-movepool_size_old;
		
		if (movepool.size() == movepool_size_old && brd.getIsCheck()) {//CHECKMATE
			long score = SearchOutcome.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchOutcome.compare(score, SearchOutcome.GTE, beta))
				return returnScoreFromSearch(beta);
			else if(SearchOutcome.compare(score, SearchOutcome.GT, alpha))
				return returnScoreFromSearch(score);
			else
				return returnScoreFromSearch(alpha);
		}
		else if (movepool.size() == movepool_size_old && !brd.getIsCheck()) {// STALEMATE
			long score = SearchOutcome.createStalemate(depth, drawScoreAssigner.applyAsInt(this));
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchOutcome.compare(score, SearchOutcome.GTE, beta))
				return returnScoreFromSearch(beta);
			else if(SearchOutcome.compare(score, SearchOutcome.GT, alpha))
				return returnScoreFromSearch(score);
			else
				return returnScoreFromSearch(alpha);
		}
		else {
			for (int i = movepool_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				long score = SearchOutcome.negateScore(
						doSearch(
							SearchOutcome.negateScore(beta),
							SearchOutcome.negateScore(alpha)
						)
					);
				brd.unmakeMove(move);
				
				if(SearchOutcome.compare(score, SearchOutcome.GTE, beta)) {
					return returnScoreFromSearch(beta);
				}
					
				if(SearchOutcome.compare(score, SearchOutcome.GT, alpha)){
					alpha=score;
					principalVariation.addMoveAtDepth(move, depth);
				}
			}
		}
		
		return returnScoreFromSearch(alpha);
	}
	
	public static class Builder{
		private SearchContext context;
		public Builder setContext(SearchContext context) {
			this.context = context;
			return this;
		}


		private ToIntFunction<AlphaBetaSearcher> drawScoreAssigner;
		
		public static Builder newInstance()
        {
			Builder bld = new Builder();
            return bld;
        }

        private Builder() {
        	drawScoreAssigner = (seacher) -> 0;
        }
        
        
        public Builder setDrawAsVictoryForMaximixingPlayer() {
        	drawScoreAssigner = (searcher) -> {
        		if(searcher.getDepth() % 2 == 0)
        			return SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER-searcher.getDepth();
        		else
        			return -SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER+searcher.getDepth();
        	};
        	return this;
        }
	}
}
