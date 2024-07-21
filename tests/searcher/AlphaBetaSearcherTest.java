package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class AlphaBetaSearcherTest {

	@Test
	void test() {
		Gamestate brd = new Gamestate("4r2k/p3NR1p/3pb1pB/1p6/2rbP3/8/PPP5/2K4R w - - 1 0");
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(brd);
		
		searcher.doSearchForCheckmate(SearchResult.LOSS, SearchResult.WIN, 0);
		System.out.println(searcher.principalVariation.toString());
		
		
		
	}

}
