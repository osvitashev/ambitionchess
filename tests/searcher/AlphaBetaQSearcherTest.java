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
//				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
//				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
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
//				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
//				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
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
//				System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
//				System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
				assertEquals(Arrays.stream(tests[t][1].substring(1, tests[t][1].length() - 1).split(" "))
						.filter(str -> str != null && !str.trim().isEmpty()).toArray(String[]::new).length, SearchOutcome.getDepth(outcome));
				assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
				assertEquals(tests[t][2], SearchOutcome.outcomeToString(outcome, true));
			}
		}
	}

	@Test
	void testBasicQSearch() {
		EvaluatorPesto pesto = new EvaluatorPesto();
		SearchContext context = new SearchContext();
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(AlphaBetaSearcher.Builder.newInstance()
				.setContext(context)
				.setBasicQSearch()
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

				// no available captures
				{ "8/8/1P4k1/5p2/N3n2p/8/1QK1B3/8 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=1135}" },
				{ "8/8/1P4k1/5p2/N3n2p/8/1QK1B3/8 b - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-1135}" },
				// no good captures
				{ "8/2k2r2/3b4/8/5P2/3Q2P1/2K5/8 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=400}" },
				{ "8/2k2r2/3b4/8/5P2/3Q2P1/2K5/8 b - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-400}" },
				// no captures because of pins/legality
				{ "8/8/3qk3/1q3p1p/8/3B2N1/3K3R/8 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-958}" },
				// moving the knight exposes a pinned rook, but puts opponent in check!
				{ "8/8/3q1k2/1q3p1p/8/3B2N1/3K3R/8 w - - 0 1", "{g3h5 f6g6}", "{depth=2, qDepth=2, maximizerScore=-946}" },

				//regular exchanges
				{ "3k4/3q4/3r4/8/3Q4/2B5/8/2K5 b - - 0 1", "{d6d4 c3d4 d7d4}", "{depth=3, qDepth=3, maximizerScore=983}" },
				{ "8/1p3p1k/2r3q1/8/8/PP6/2Q3B1/K5R1 w - - 0 1", "{c2g6 c6g6}", "{depth=2, qDepth=2, maximizerScore=99}" },
				{ "8/1p3p1k/2r3q1/8/8/PP6/1KQ3B1/6R1 w - - 0 1", "{g2c6 g6c2 b2c2 b7c6}", "{depth=4, qDepth=4, maximizerScore=488}" },
				//another case of 'if i don't give you tempo, you cannot use it against me.'
				{ "7r/1p3p1k/2r3q1/8/8/PP6/1KQ3B1/B5R1 w - - 0 1", "{g2c6}", "{depth=1, qDepth=1, maximizerScore=453}" },
				{ "8/1p3ppk/2r3q1/8/3B4/PP6/1KQ3B1/Q5R1 w - - 0 1", "{g2c6 g6c2 b2c2}", "{depth=3, qDepth=3, maximizerScore=1772}" },
				//enpassant in the pv
				
				{ "3r4/8/5k2/8/2Pp4/8/8/3K4 b - c3 0 1", "{d4c3 d1e2}", "{depth=2, qDepth=2, maximizerScore=641}" },
				{ "3r3R/8/5k2/8/2Pp4/4P3/5P2/3K3R b - c3 0 1", "{d8h8 h1h8 d4e3 f2e3}", "{depth=4, qDepth=4, maximizerScore=-684}" },
				{ "3r3R/6n1/5k2/8/2Pp4/4P3/5P2/3K3Q b - c3 0 1", "{d8h8 h1h8 d4e3 f2e3}", "{depth=4, qDepth=4, maximizerScore=-834}" },
				{ "3r4/6k1/8/8/2Pp4/8/1P6/3K4 b - c3 0 1", "{d4c3 d1c1 c3b2 c1b2}", "{depth=4, qDepth=4, maximizerScore=529}" },
				// known shortcomings

				// wrong side-to-move
				{ "8/5B2/4Nk1r/5P2/8/2K5/8/8 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=167}" },
				{ "8/8/5k2/1r6/2K5/8/8/8 b - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=531}" },

				// calling in check
				{ "6nr/6pp/2KRk3/8/4n3/8/4Q3/8 b - - 0 1", "{e6f5}", "{depth=1, qDepth=1, maximizerScore=19}" },
				// if there is a check response generated within q-search, we evaluate it
				{ "6nr/6pp/2KRk3/8/4n3/8/5Q2/8 b - - 0 1", "{e4d6}", "{depth=1, qDepth=1, maximizerScore=525}" },
				{ "6nr/6pp/1K1Rk3/8/4n3/8/4Q3/8 b - - 0 1", "{e6d6 e2e4}", "{depth=2, qDepth=2, maximizerScore=213}" },

				// calling in stalemate - returns score, but no stalemate flag - we do not hit the check? condition in main search
				{ "8/8/kr6/8/K7/P7/8/8 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-355}" },
				
				// calling in checkmate
				{ "8/8/7P/R7/8/5k2/8/2q2K2 w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-32000, CHECKMATE!}" },
				
				//shortcomings:
				//moving into check winning material?
				//=> should be impossible because of legal move generation
				
				//moving into stalemate winning material
				{ "8/1r6/4p3/5R2/k4P2/8/8/K7 b - - 0 1", "{e6f5}", "{depth=1, qDepth=1, maximizerScore=576}" },
				
				//moving into checkmate winning material
				/**
				 * g2f3 wins material but leads to checkmate.
				 * the move is rejected because: on the next turn While player is in check and has no legal moves (checkmate)
				 * it means that the depth=2 search falls through and returns alpha
				 */
				{ "8/8/1b6/5k2/4q3/5q2/6BP/2rN3K w - - 0 1", "{}", "{depth=0, qDepth=0, maximizerScore=-2056}" }
			};
			for (int t = 0; t < tests.length; ++t) {
				context.getBrd().loadFromFEN(tests[t][0]);
				long outcome = searcher.doSearch(
						SearchOutcome.createLowerBound(SearchOutcome.LOSS),
						SearchOutcome.createUpperBound(SearchOutcome.WIN)
					);
				System.out.println(
						tests[t][0] + " " + searcher.getPrincipalVariation().toString() + " " + SearchOutcome.outcomeToString(outcome, true));
				assertEquals(Arrays.stream(tests[t][1].substring(1, tests[t][1].length() - 1).split(" "))
						.filter(str -> str != null && !str.trim().isEmpty()).toArray(String[]::new).length, SearchOutcome.getDepth(outcome), "for FEN: "+ tests[t][0]);
				assertEquals(tests[t][1], searcher.getPrincipalVariation().toString(), "for FEN: "+ tests[t][0]);
				assertEquals(tests[t][2], SearchOutcome.outcomeToString(outcome, true), "for FEN: "+ tests[t][0]);
			}
		}
	}
}
