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
		evaluator=bld.evaluator;
		makerMoveWithSideEffectts = bld.makerMoveWithSideEffectts;
		unmakerMoveWithSideEffectts=bld.unmakerMoveWithSideEffectts;
		PIECE_MATERIAL_VALUES=bld.PIECE_MATERIAL_VALUES;
	}
	
	//todo: this should also take a custom comparator!
	
	private AlphaBetaSearcher searcher;

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
	
	
	public int evaluate() {
		return evaluator.getAsInt();
	}
	
	
	public static class Builder {
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
        	evaluator = () -> 0;
        	makerMoveWithSideEffectts = (move) -> {};
        	unmakerMoveWithSideEffectts = (move) -> {};
        }
        
        public Builder setPieceMaterialValues(int [] matvals) {
        	PIECE_MATERIAL_VALUES = matvals;
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
