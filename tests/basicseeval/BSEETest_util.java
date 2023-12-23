package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class BSEETest_util {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	void assertHelper(String fen) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		
		System.out.println(test_eval.debug_getAllOutputs());
	}
	
	@Test
	void test() {
		assertHelper("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17");
	}

}
