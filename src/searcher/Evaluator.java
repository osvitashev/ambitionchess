package searcher;

import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * As i add more optional features to the evaluator (for example, incremental PSTs),
 * all of them will be added as private fields of the evaluator, but they only will be initialized if
 * the builder is called with corresponding parameters.
 * 
 * We definitely do not want to use inheritance for performance reasons...
 * Maybe there is a way to make it cleaner with interfaces....
 *
 */
public class Evaluator {	
	public final int[] PIECE_MATERIAL_VALUES;
	
	public Evaluator(Builder bld) {
		assignerScoreToDraw=bld.assignerScoreToDraw;
		evaluator=bld.evaluator;
		makerMoveWithSideEffectts = bld.makerMoveWithSideEffectts;
		unmakerMoveWithSideEffectts=bld.unmakerMoveWithSideEffectts;
		PIECE_MATERIAL_VALUES=bld.PIECE_MATERIAL_VALUES;
	}
	
	//todo: this should also take a custom comparator!
	
	private AlphaBetaSearcher searcher;
	
	public void setSearcher(AlphaBetaSearcher searcher) {
		this.searcher = searcher;
	}

	/**
	 * only returns the score part of the SearchOutcome.
	 * It is caller's responsibility to distinguish between different types of draw if it is needed.
	 */
	private ToIntFunction<AlphaBetaSearcher> assignerScoreToDraw;
	
	private ToIntFunction<AlphaBetaSearcher> evaluator;
	
	/**
	 * is intended to incrementally update data needed for evaluation
	 * MUST call brd.makeMove(move);
	 * https://marcelkliemannel.com/articles/2021/avoiding-autoboxing-by-using-primitive-in-functional-interfaces-streams-and-optionals/
	 */
	private ObjIntConsumer<AlphaBetaSearcher> makerMoveWithSideEffectts;
	
	/**
	 * is intended to incrementally update data needed for evaluation
	 * MUST call brd.unmakeMove(move);
	 */
	private ObjIntConsumer<AlphaBetaSearcher> unmakerMoveWithSideEffectts;
	
	
	
	public int assignScoreToDraw() {
		return assignerScoreToDraw.applyAsInt(searcher);
	}
	
	public int evaluate() {
		return evaluator.applyAsInt(searcher);
	}
	
	
	public static class Builder {
		private ToIntFunction<AlphaBetaSearcher> assignerScoreToDraw;
		private ToIntFunction<AlphaBetaSearcher> evaluator;
		private ObjIntConsumer<AlphaBetaSearcher> makerMoveWithSideEffectts;
		private ObjIntConsumer<AlphaBetaSearcher> unmakerMoveWithSideEffectts;
		
		private int[] PIECE_MATERIAL_VALUES;
		
		public static Builder newInstance()
        {
			Builder bld = new Builder();
            return bld;
        }

        private Builder() {
        	assignerScoreToDraw = searcher -> 0;
        	evaluator = searcher -> 0;
        	makerMoveWithSideEffectts = (searcher, move) -> {};
        	unmakerMoveWithSideEffectts = (searcher, move) -> {};
        }
        
        public Builder setPieceMaterialValues(int [] matvals) {
        	PIECE_MATERIAL_VALUES = matvals;
        	return this;
        }
        
        public Builder setDrawAsVictoryForMaximixingPlayer() {
        	//this version correctly handles { "8/8/8/5k2/7K/2R2n2/8/5q2 w - - 0 1", "{c3f3 f1f3}" }
//        	assignerScoreToDraw = (searcher) -> {
//        		if(searcher.getBrd().getPlayerToMove() == Player.WHITE ^ searcher.getDepth() % 2 == 1)
//        			return 1;
//        		else
//        			return -1;
//        	};
        	
        	assignerScoreToDraw = (searcher) -> {
        		if(searcher.getDepth() % 2 == 0)
        			return SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER-searcher.getDepth();
        		else
        			return -SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER+searcher.getDepth();
        	};
        	return this;
        }

		public Builder setEvaluator(ToIntFunction<AlphaBetaSearcher> evaluator) {
			this.evaluator = evaluator;
			return this;
		}


	}
}
