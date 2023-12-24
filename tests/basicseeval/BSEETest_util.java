package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class BSEETest_util {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void test() {
		
		test_game.loadFromFEN("8/2r2k2/pp2np1p/2r2p2/3B4/P2KQ3/1PP3R1/8 w - - 0 1");
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
		test_eval.evaluateQuietMoves();
		
		System.out.println(test_eval.debug_getAllOutputs());
	}

}
