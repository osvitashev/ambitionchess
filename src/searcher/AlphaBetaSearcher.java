package searcher;

import gamestate.Gamestate;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;

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
	private int depth = -1;//todo: can actually be dropped. All we need is the game plycount at the beginning of the search. Search depth can be calculated using that and the updated game state!!!
	private int qDepth=-1;//todo: this is obviously a placeholder
	
	private boolean[] isNullMoveUsedInQuiescenceByPlayer = {false, false};
	
	
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
		if(qDepth != -1)
			qDepth--;
		depth--;
		movepool.resize(movelistSize);//this could be removed if movelist size was added as a parameter to the recursive search
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
		
		
		int movepool_size_old = movepool.size();
		if(depth == fullDepthSearchLimit) {
			if(brd.getIsCheck() && move_generator.noMovesAreAvailable(brd, movepool)) {
				long score = SearchOutcome.createCheckmate(depth);
				if(SearchOutcome.isScoreGreater(score, alpha))
					return returnScoreFromSearch(movepool_size_old, score);
				else
					return returnScoreFromSearch(movepool_size_old, alpha);
			}
			else {
				depth--;
				return doQuiescenceSearch(alpha, beta);//returnScoreFromSearch is included in doQuiescenceSearch
			}
		}
			
		move_generator.generateLegalMoves(brd, movepool);
		if (movepool.size() == movepool_size_old && brd.getIsCheck()) {//CHECKMATE
			long score = SearchOutcome.createCheckmate(depth);
			//notice that we do not need to resize the move pool - no new moves have been generated.
			if(SearchOutcome.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movepool_size_old, score);
			else
				return returnScoreFromSearch(movepool_size_old, alpha);
		}
		else if (movepool.size() == movepool_size_old && !brd.getIsCheck()) {// STALEMATE
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
						doSearch(
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
	
	/**
	 * similar to regular alpha-beta, but with a stand-pat option.
	 * 
	 * Todo: possibly skip updating zobrist hash
	 * Todo: move ordering
	 * Todo: possibly skip updating principal variation
	 * 
	 * @return SearchResult
	 */
	private long doQuiescenceSearch(long alpha, long beta) {
//		if(depth==-1)System.out.println(">> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(alpha, true) + " >> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(beta,true));
		depth++;
		qDepth++;
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		
		
		int movepool_size_old = movepool.size();
		if(brd.getIsCheck() && move_generator.noMovesAreAvailable(brd, movepool)) {
			long score = SearchOutcome.createCheckmate(depth, qDepth);
			if(SearchOutcome.isScoreGreater(score, alpha))
				return returnScoreFromSearch(movepool_size_old, score);
			else
				return returnScoreFromSearch(movepool_size_old, alpha);
		}
		
		
		//consider: isCheckmate is a little expensive in the sense that it is throwing out information that could be used in move generation.
		
		if(brd.getIsCheck()){
			move_generator.generateLegalMoves(brd, movepool);
		}
		else {
			move_generator.generateNonQuietLegalMoves(brd, movepool);
		}
		
		/**
		 * >>>>>>>>>>>there are 4 cases:
		 * 
		 * isCheck and no generated moves
		 * >Would not occur at this point. There is a checkmate test earlier.
		 * 
		 * isCheck and generated moves
		 * >we have generated all legal moves is this position. Proceed normally.
		 * 
		 * notCheck and no generated moves
		 * >we have generated non-quiet moves only. No new moves generated could be because of a stalemate, but more likely there are simply no captures available.
		 * 
		 * notCheck and generated moves
		 * >we have generated non-quiet moves only. Proceed normally.
		 */
		
		if (movepool.size() == movepool_size_old) {//we know that there is no check at this point!
			assert !brd.getIsCheck();
			if(move_generator.noMovesAreAvailable(brd, movepool)) {
				long score = SearchOutcome.createStalemate(depth, qDepth, evaluator.assignScoreToDraw());
				//notice that we do not need to resize the move pool - no new moves have been generated.
				if(SearchOutcome.isScoreGreater(score, alpha))
					return returnScoreFromSearch(movepool_size_old, score);
				else
					return returnScoreFromSearch(movepool_size_old, alpha);
			}
			else {//not a stalemate - we just have ran out of possible captures.
				if(!isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]) {
					int move = Move.createNullMove(brd.getPlayerToMove());
					
					isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]=true;
					brd.makeMove(move);
					long score = SearchOutcome.negateScore(
							doQuiescenceSearch(
								SearchOutcome.negateScore(beta),
								SearchOutcome.negateScore(alpha)
							)
						);
					brd.unmakeMove(move);
					isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]=false;
					if(SearchOutcome.isScoreGreaterOrEqual(score, beta)) {
						return returnScoreFromSearch(movepool_size_old, beta);
					}
					if(SearchOutcome.isScoreGreater(score, alpha)){
						alpha = score;//this will be returned at the end of the method
						principalVariation.addMoveAtDepth(move, depth);
					}
				}
				else {
					long score = SearchOutcome.createWithDepthAndScore(depth, qDepth, evaluator.evaluate());
					if(SearchOutcome.isScoreGreaterOrEqual(score, beta)) {
						return returnScoreFromSearch(movepool_size_old, beta);
					}
					if(SearchOutcome.isScoreGreater(score, alpha)){
						alpha = score;//this will be returned at the end of the method
					}
				}
			}
		}
		else {
			long score;
			if(!isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]) {
				int move = Move.createNullMove(brd.getPlayerToMove());
				
				isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]=true;
				brd.makeMove(move);
				score = SearchOutcome.negateScore(
						doQuiescenceSearch(
							SearchOutcome.negateScore(beta),
							SearchOutcome.negateScore(alpha)
						)
					);
				brd.unmakeMove(move);
				isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]=false;
			}
			else {
				score = SearchOutcome.createWithDepthAndScore(depth, qDepth, evaluator.evaluate());
			}
			if(SearchOutcome.isScoreGreaterOrEqual(score, beta)) {
				return returnScoreFromSearch(movepool_size_old, beta);
			}
			if(SearchOutcome.isScoreGreater(score, alpha)){
				alpha = score;
				if(!isNullMoveUsedInQuiescenceByPlayer[brd.getPlayerToMove()]) {
					principalVariation.addMoveAtDepth(Move.createNullMove(brd.getPlayerToMove()), depth);
				}
			}
			for (int i = movepool_size_old; i < movepool.size(); ++i) {
				int move = movepool.get(i);
				brd.makeMove(move);
				score = SearchOutcome.negateScore(
						doQuiescenceSearch(
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
