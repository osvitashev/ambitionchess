package searcher;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class AlphaBetaQSearcherTest {

	@Test
	void testFlatQSearch() {
		EvaluatorPesto pesto = new EvaluatorPesto();
		SearchContext context = new SearchContext();
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(AlphaBetaSearcher.Builder.newInstance()
				.setContext(context)
			);
		
		context.setEvaluator(new Evaluator(Evaluator.Builder.newInstance()
				.setSearcher(searcher)
				.setEvaluator(() -> {
					return pesto.evaluate(context.getBrd());
				})// setEvaluator
		));

		{
			searcher.setFullDepthSearchLimit(0);
			String[][] tests = {
					{ "8/8/1k6/3r4/8/8/3Q4/1K6 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=439}" },
					{ "8/8/1k6/3r4/8/8/3Q4/1K6 b - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-439}" },
	
			};
			for (int t = 0; t < tests.length; ++t) {
				context.getBrd().loadFromFEN(tests[t][0]);
				long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
				assertEquals(Arrays.stream(tests[t][1].substring(1, tests[t][1].length() - 1).split(" "))
						.filter(str -> str != null && !str.trim().isEmpty()).toArray(String[]::new).length, SearchOutcome.getDepth(outcome));
				assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
				assertEquals(tests[t][2], SearchOutcome.outcomeToString(outcome, true));
			}
		}
		
		{
			searcher.setFullDepthSearchLimit(1);
			String[][] tests = {
					{ "8/8/1k6/3r4/8/8/3Q4/1K6 w - - 0 1", "{d2d5}", "{depth=1, qDepth=0, maximizerScore=944}" },
					{ "8/8/1k6/3r4/8/8/3Q4/1K6 b - - 0 1", "{d5d2}", "{depth=1, qDepth=0, maximizerScore=557}" },
	
			};
			for (int t = 0; t < tests.length; ++t) {
				context.getBrd().loadFromFEN(tests[t][0]);
				long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
				assertEquals(Arrays.stream(tests[t][1].substring(1, tests[t][1].length() - 1).split(" "))
						.filter(str -> str != null && !str.trim().isEmpty()).toArray(String[]::new).length, SearchOutcome.getDepth(outcome));
				assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
				assertEquals(tests[t][2], SearchOutcome.outcomeToString(outcome, true));
			}
		}
		
		{
			searcher.setFullDepthSearchLimit(2);
			String[][] tests = {
					{ "8/8/2k5/3r3p/8/8/3Q4/2K5 w - - 0 1", "{d2g2 c6b6}", "{depth=2, qDepth=0, maximizerScore=351}" },
					{ "8/8/2k5/3r3p/8/8/3Q4/2K5 b - - 0 1", "{d5d2 c1d2}", "{depth=2, qDepth=0, maximizerScore=117}" },
	
			};
			for (int t = 0; t < tests.length; ++t) {
				context.getBrd().loadFromFEN(tests[t][0]);
				long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
				assertEquals(Arrays.stream(tests[t][1].substring(1, tests[t][1].length() - 1).split(" "))
						.filter(str -> str != null && !str.trim().isEmpty()).toArray(String[]::new).length, SearchOutcome.getDepth(outcome));
				assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
				assertEquals(tests[t][2], SearchOutcome.outcomeToString(outcome, true));
			}
		}
	}

}
