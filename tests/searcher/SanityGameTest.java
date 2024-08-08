package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SanityGameTest {

	String masterGameState;
	
	boolean nextMove(AlphaBetaSearcher s, SearchContext c) {
		c.getBrd().loadFromFEN(masterGameState);
		long outcome = s.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
		System.out.println(
				masterGameState + " " + s.getPrincipalVariation().toString() + " " + SearchOutcome.outcomeToString(outcome, true));
		
		try {
			c.getBrd().makeMove(s.getPrincipalVariation().getFirst());
			masterGameState=c.getBrd().toFEN();
		} catch (AssertionError e) {
			return false;
		}
		return true;
	}
	
	/**
	 * the two players use same evaluator, but one has q-search and an extra level of depth.
	 */
	@Test
	void test1() {
		masterGameState = "r1bqk2r/ppp1npbp/2np4/1P4p1/2P1P3/3Q1NP1/P3BP1P/RNB1K2R w KQkq - 0 1";
		
		EvaluatorPesto pestoB = new EvaluatorPesto();
		EvaluatorPesto pestoW = new EvaluatorPesto();
		
		SearchContext contextB = new SearchContext();
		AlphaBetaSearcher searcherB = new AlphaBetaSearcher(AlphaBetaSearcher.Builder.newInstance()
			.setContext(contextB)
			.setBasicQSearch()
		);
		contextB.setEvaluator(new Evaluator(Evaluator.Builder.newInstance()
			.setSearcher(searcherB)
			.setEvaluator(() -> {
				return pestoB.evaluate(contextB.getBrd());
			})// setEvaluator
		));
		
		SearchContext contextW = new SearchContext();
		AlphaBetaSearcher searcherW = new AlphaBetaSearcher(AlphaBetaSearcher.Builder.newInstance()
				.setContext(contextW)
			);
		contextW.setEvaluator(new Evaluator(Evaluator.Builder.newInstance()
			.setSearcher(searcherW)
			.setEvaluator(() -> {
				return pestoW.evaluate(contextW.getBrd());
			})// setEvaluator
		));
		
		searcherB.setFullDepthSearchLimit(4);
		searcherW.setFullDepthSearchLimit(3);
		
		boolean isEndedWithCheckmate=false;
		for(int i=0; i<40; ++i) {
			if(!nextMove(searcherW, contextW))
				break;
			if(!nextMove(searcherB, contextB))
				break;
			else
				if(contextB.getBrd().getIsCheck())
					isEndedWithCheckmate=true;
		}
		assertTrue(isEndedWithCheckmate);
	}

}
