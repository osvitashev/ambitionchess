package searcher;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
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

	/**
	 * only returns the score part of the SearchOutcome.
	 * It is caller's responsibility to distinguish between different types of draw if it is needed.
	 */
	private IntSupplier assignerScoreToDraw;
	
	private IntSupplier evaluator;
	
	/**
	 * is intended to incrementally update data needed for evaluation
	 * MUST call brd.makeMove(move);
	 * https://marcelkliemannel.com/articles/2021/avoiding-autoboxing-by-using-primitive-in-functional-interfaces-streams-and-optionals/
	 */
	private IntConsumer makerMoveWithSideEffectts;
	
	/**
	 * is intended to incrementally update data needed for evaluation
	 * MUST call brd.unmakeMove(move);
	 */
	private IntConsumer unmakerMoveWithSideEffectts;
	
	
	
	public int assignScoreToDraw() {
		return assignerScoreToDraw.getAsInt();
	}
	
	public int evaluate() {
		return evaluator.getAsInt();
	}
	
	
	public static class Builder {
		private IntSupplier assignerScoreToDraw;
		private IntSupplier evaluator;
		private IntConsumer makerMoveWithSideEffectts;
		private IntConsumer unmakerMoveWithSideEffectts;
		private AlphaBetaSearcher searcher;
		
		private int[] PIECE_MATERIAL_VALUES;
		
		public static Builder newInstance()
        {
			Builder bld = new Builder();
            return bld;
        }

        private Builder() {
        	assignerScoreToDraw = () -> 0;
        	evaluator = () -> 0;
        	makerMoveWithSideEffectts = (move) -> {};
        	unmakerMoveWithSideEffectts = (move) -> {};
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
        	
        	assignerScoreToDraw = () -> {
        		if(searcher.getDepth() % 2 == 0)
        			return SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER-searcher.getDepth();
        		else
        			return -SearchOutcome.FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER+searcher.getDepth();
        	};
        	return this;
        }

		public Builder setEvaluator(IntSupplier evaluator) {
			this.evaluator = evaluator;
			return this;
		}

		public Builder setSearcher(AlphaBetaSearcher searcher) {
			this.searcher= searcher;
			return this;
		}

	}
}
