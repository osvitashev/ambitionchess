package searcher;

import gamestate.Gamestate;
import gamestate.Move;
import gamestate.MoveGen;
import gamestate.MovePool;

/**
 * a very basic version of q-search
 * 
 * Does nto return checkmate/stalemate score. If there are no legal moves available, it simply returns the eval.
 * Does not use null moves
 * 
 * todo: add option to disable PV
 * todo: switch to pseudo-legal generation with return after king is captured.
 * 
 * @author sveta
 *
 */
public class QSearcherBasic {
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd;
	private PrincipalVariation principalVariation = new PrincipalVariation();
	private Evaluator evaluator;
	
	QSearcherBasic(Evaluator evaluator){
		this.evaluator = evaluator;
//		evaluator.setSearcher(this);
	}
	
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
//		evaluator.setSearcher(this);
	}

	public void setBrd(Gamestate brd) {
		this.brd = brd;
	}

	/**
	 * it is incremented at the start of the recursive call. -1 as the start value makes it correct throughout.
	 */
	private int depth = -1;//todo: can actually be dropped. All we need is the game plycount at the beginning of the search. Search depth can be calculated using that and the updated game state!!!
			
	public Gamestate getBrd() {
		return brd;
	}

	public PrincipalVariation getPrincipalVariation() {
		return principalVariation;
	}
	
	private int getDepth() {
		return depth;
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
	 * currently implements a fail-hard version of alpha-beta.
	 * This means that the return value is strictly in the range [alpha, beta], Inclusive!
	 * If the search is done with an aspiration window, return value of either alpha or beta would mean the search going out of bounds.
	 * 
	 * @return SearchResult
	 */
	private long doSearchRecursively(long alpha, long beta) {
//		if(depth==-1)System.out.println(">> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(alpha, true) + " >> "+ SearchOutcome.outcomeToStringInMaximixerPerspective(beta,true));
		depth++;
		principalVariation.resetAtDepth(depth);//todo:re-evaluate whether this is needed. Maybe, i can do it at addMoveAtDepth
		int movepool_size_old = movepool.size();
		
		long score = SearchOutcome.createWithDepthAndScore(depth, evaluator.evaluate());
		if(SearchOutcome.isScoreGreaterOrEqual(score, beta)) {
			return returnScoreFromSearch(movepool_size_old, beta);
		}
			
		if(SearchOutcome.isScoreGreater(score, alpha)){
			alpha=score;
		}
			
		if(brd.getIsCheck()){//consider: maybe it is acceptable to not even look at check and only generate captures here?
			move_generator.generateLegalMoves(brd, movepool);
		}
		else {
			move_generator.generateNonQuietLegalMoves(brd, movepool);
		}
		
		for (int i = movepool_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			brd.makeMove(move);
			score = SearchOutcome.negateScore(
					doSearchRecursively(
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
		
		return returnScoreFromSearch(movepool_size_old, alpha);
	}
	
	public long doSearch(long alpha, long beta) {
		return doSearchRecursively(alpha, beta);
	}
}
