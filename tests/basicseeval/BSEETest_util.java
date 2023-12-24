package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class BSEETest_util {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void test() {
		
		test_game.loadFromFEN("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27");
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
		test_eval.evaluateQuietMoves();
		
		System.out.println(test_eval.debug_getAllOutputs());
		fail("Add the actual assetion, or just use this as a form of ducumentation...");
	}

}
