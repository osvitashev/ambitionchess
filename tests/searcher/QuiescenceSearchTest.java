package searcher;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class QuiescenceSearchTest {
	static final boolean ENABLE_LOGGING=true;
	
	
	private int getPV_length(String pv) {
		String [] strings = pv.substring(1, pv.length() - 1).split(" ");
		return (int)(Arrays.stream(strings)
                .filter(str -> str != null && !str.trim().isEmpty())
                .count());
	}
	
	void runCase(String f1, String pv1, String f2, String pv2, boolean is1better) {
		long outcome1, outcome2;
		
		if(ENABLE_LOGGING)
			System.out.println(f1 + " vs. " + f2);
		
		searcher.getBrd().loadFromFEN(f1);
		outcome1 = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
		if(ENABLE_LOGGING)
			System.out.println(searcher.getPrincipalVariation().toString() + " " + SearchOutcome.outcomeToString(outcome1));
//		assertEquals(getPV_length(pv1), SearchOutcome.getDepth(outcome1));
//		assertEquals(getPV_length(pv1), SearchOutcome.getQuiescenceDepth(outcome1));
//		assertEquals(pv1, searcher.getPrincipalVariation().toString());
		searcher.getBrd().loadFromFEN("8/6k1/6p1/1p6/2n5/2R5/2K5/8 w - - 0 1");
		outcome2 = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
		if(ENABLE_LOGGING) {
			System.out.println(searcher.getPrincipalVariation().toString() + " " + SearchOutcome.outcomeToString(outcome2));
			System.out.println();
		}
//		assertEquals(getPV_length(pv2), SearchOutcome.getDepth(outcome2));
//		assertEquals(getPV_length(pv2), SearchOutcome.getQuiescenceDepth(outcome2));
//		assertEquals(pv2, searcher.getPrincipalVariation().toString());
		assertEquals(is1better, SearchOutcome.getScore(outcome1)>SearchOutcome.getScore(outcome2));
		
		
		
	}

	@Test
	@Disabled
	void testQSearch_comparable() {
		SearchContext context = new SearchContext();
		EvaluatorPesto pesto = new EvaluatorPesto();
		context.setEvaluator(new Evaluator(Evaluator.Builder.newInstance()
				.setEvaluator((mysearcher) -> {
					return pesto.evaluate(context.getBrd());
				})// setEvaluator
		));
		
		
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(context);
		
		searcher.setFullDepthSearchLimit(0);
		
		runCase(
				"8/6k1/6p1/8/2n5/2R5/2K5/8 w - - 0 1",
				"{c3c4}",
				"8/6k1/6p1/1p6/2n5/2R5/2K5/8 w - - 0 1",
				"{}",
				true
			);
		
		runCase(
				"8/5k2/8/5p1N/3p1Pp1/3K2P1/8/8 w - - 0 1",
				"{d3d4}",
				"8/8/6k1/5p1N/3p1Pp1/3K2P1/8/8 w - - 0 1",
				"{d3d4}",
				true
			);
		
		/**
		 * this fails, because evaluation function chooses stand-pat over taking h5
		 * 
		 * not sure how to handle hanging pieces....
		 */
		runCase(
				"8/5k2/1R1r2Nq/P6P/8/3B4/8/K7 b - - 0 1",
				"{d6d3}",
				"8/8/1R1r1kNq/P6P/8/3B4/8/K7 b - - 0 1",
				"{h6h5}",
				false
			);
		
	}
	
	/**
	 * just prints out the pv and evaluation outcome
	 * @param fen
	 */
	void qsTraceHelper(String fen) {
		searcher.getBrd().loadFromFEN(fen);
		String format = "%-50s %-30s %-15s\n";
		long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
		System.out.printf(
				format,
				fen,
				searcher.getPrincipalVariation().toString(),
				SearchOutcome.outcomeToString(outcome)
			);
	}
	
	@Test
	void testQSearch() {		
		searcher.setFullDepthSearchLimit(0);
		
		 qsTraceHelper("8/8/5k2/5N2/2p5/2K5/8/8 w - - 0 1");

		 qsTraceHelper("8/2k1rn2/4r3/4n3/3P4/2B5/1Q1K4/8 w - - 0 1");

		 qsTraceHelper("8/p3k3/1q2n3/5P2/8/1R6/PP6/K7 w - - 0 1");

		 qsTraceHelper("8/1p2k3/1q2n3/5P2/8/1R6/PP6/K7 w - - 0 1");
		 
		 qsTraceHelper("8/4N3/5k2/1p6/1K6/8/8/8 w - - 0 1");

		/**
		 * assuming that {b3b6 a7b6 null null f5e6 e7e6} and {b3b6 a7b6 f5e6 e7e6 null null}
		 * return the same score but one is examined before the other one...
		 * in the current implementation, the stand pat evaluation is only invoked if the null move option is exhausted.
		 * hence, the extra two null moves that are injected into PV at all times.
		 * 
		 * todo: maybe implement a routine that applies a given sequence of moves to a position and then invokes the evaluation function?
		 * 
		 * do both players need an independent flag for null accessibility?
		 */
	}

}
