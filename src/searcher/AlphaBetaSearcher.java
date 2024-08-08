package searcher;

import java.util.function.IntSupplier;
import java.util.function.LongBinaryOperator;
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
	 * takes [alpha beta] range and returns SearchResult. Is not required to detect endgame states.
	 * In other words: it is acceptable to return static evaluation score even in a position that is a check/stalemate/checkmate
	 */
	private LongBinaryOperator qSearcher;
	/**
	 * only returns the score part of the SearchOutcome.
	 * It is caller's responsibility to distinguish between different types of draw if it is needed.
	 */
	private ToIntFunction<AlphaBetaSearcher>  drawScoreAssigner;
	
	public AlphaBetaSearcher(Builder bld) {
		context=bld.context;
		drawScoreAssigner=bld.drawScoreAssigner;
		if(bld.useBasicQSearch)
			qSearcher = this::doBasicQSearch;
		else
			qSearcher = this::doFlatQSearch;
	}
	
	/**
	 * marker to the movelist generated at the given depth.
	 */
	private int[] numMovesAtDepth = new int[50];
	
	/**
	 * it is incremented at the start of the recursive call. -1 as the start value makes it correct throughout.
	 */
	private int depth = 0;//todo: can actually be dropped. All we need is the game plycount at the beginning of the search. Search depth can be calculated using that and the updated game state!!!
		
	
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
//		depth--;
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
		
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		numMovesAtDepth[getDepth()]=0;
		
		MovePool movepool = context.getMovepool();
		Gamestate brd=context.getBrd();
		MoveGen move_generator = context.getMoveGenerator();
		
		int movepool_size_old = movepool.size();
		if(depth == fullDepthSearchLimit) {
			if(brd.getIsCheck() && move_generator.noMovesAreAvailable(brd, movepool)) {//q-search does not return checkmate score. this is here so we do not miss out on a definite checkmate.
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
				
				long score = qSearcher.applyAsLong(alpha, beta); 
				return score; //returnScoreFromSearch is called in the q-search handler
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
				depth++;
				long score = SearchOutcome.negateScore(
						doSearch(
							SearchOutcome.negateScore(beta),
							SearchOutcome.negateScore(alpha)
						)
					);
				depth--;
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
	
	/**
	 * returns evaluation value for the current position without doing more searching.
	 * @param alpha
	 * @param beta
	 * @return SearchOutcome
	 */
	private long doFlatQSearch(long alpha, long beta) {
//		depth++;
		return returnScoreFromSearch(SearchOutcome.createWithDepthAndScore(depth, context.getEvaluator().evaluate()));
	}
	
	/**
	 * 
	 * Generates all check evasions, but does not seek checks specifically. This way he have a hard limit on the size of the search tree.
	 * 
	 * @param alpha
	 * @param beta
	 * @return SearchOutcome
	 */
	private long doBasicQSearch(long alpha, long beta) {
		long temp=doBasicQSearch(alpha, beta, 0);
		return temp;
	}
	
	private long doBasicQSearch(long alpha, long beta, int qDepth) {
		//todo: detect and handle end game states reached in QS. This would probably require re-search to obtain a reliable score.
		
		//todo: whether or not principalVariation is getting updated should be parametrizable!
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		numMovesAtDepth[getDepth()]=0;
		
		MovePool movepool = context.getMovepool();
		Gamestate brd=context.getBrd();
		MoveGen move_generator = context.getMoveGenerator();
		
		int movepool_size_old = movepool.size();
		
		if(!brd.getIsCheck()) {
			long standPat = SearchOutcome.setQuiescenceDepth(SearchOutcome.createWithDepthAndScore(depth, context.getEvaluator().evaluate()), qDepth);
			if (SearchOutcome.compare(standPat, SearchOutcome.GTE, beta))
				return returnScoreFromSearch(beta);
			else if (SearchOutcome.compare(standPat, SearchOutcome.GT, alpha))
				alpha = standPat;
		}
		
		
		if(brd.getIsCheck())
			move_generator.generateLegalMoves(brd, movepool);
		else
			move_generator.generateNonQuietLegalMoves(brd, movepool);
		numMovesAtDepth[getDepth()]=movepool.size()-movepool_size_old;
		
		for (int i = movepool_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			depth++;
			brd.makeMove(move);
			long score = SearchOutcome.negateScore(
					doBasicQSearch(
						SearchOutcome.negateScore(beta),
						SearchOutcome.negateScore(alpha),
						qDepth+1
					)
				);
			brd.unmakeMove(move);
			depth--;
			if(SearchOutcome.compare(score, SearchOutcome.GTE, beta)) {
				return returnScoreFromSearch(beta);
			}
				
			if(SearchOutcome.compare(score, SearchOutcome.GT, alpha)){
				alpha=score;
				principalVariation.addMoveAtDepth(move, depth);
			}
		}
		
		return returnScoreFromSearch(alpha);
	}
	
	public static class Builder{
		boolean useBasicQSearch=false;
		
		private SearchContext context;
		
		public Builder setBasicQSearch() {
			useBasicQSearch=true;
			return this;
		}
		
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
