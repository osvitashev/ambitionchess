package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.Utilities.OutcomeEnum;

class BSEETest_evaluateTargetOverprotection {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	static final boolean skipAssertions = false;
	
	void test_evaluateTargetProtection(int sq, int player, int expectedOutcome) {
		test_eval.evaluateTargetPotection(sq, player, expectedOutcome);
	}
	
	@Test
	void test() {
//		test_game.loadFromFEN("3r4/1p2n1k1/p2r4/3b1n2/8/n2Q4/3R4/3R2K1 w - - 0 1");
//		test_eval.initialize();
//		test_evaluateTargetOverprotection(Square.D5, Player.WHITE, OutcomeEnum.NEGATIVE);
		
		test_game.loadFromFEN("3r4/1p2n1k1/p7/3b1n2/8/n2Q4/3R4/3R2K1 w - - 0 1");
		test_eval.initialize();
		test_evaluateTargetProtection(Square.D5, Player.WHITE, OutcomeEnum.NEGATIVE);
		
//		test_evaluateTargetOverprotection(Square.F5, Player.WHITE, OutcomeEnum.NEGATIVE);
//		test_evaluateTargetOverprotection(Square.A6, Player.WHITE, OutcomeEnum.NEGATIVE);
//		test_game.loadFromFEN("3kr3/6b1/8/4p3/8/3N4/2K5/8 w - - 0 1");
//		test_eval.initialize();
//		test_evaluateTargetOverprotection(Square.E5, Player.WHITE, OutcomeEnum.NEGATIVE);
		
		//a lot of candidates generated
//		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
//		test_eval.initialize();
//		test_evaluateTargetOverprotection(Square.D5, Player.WHITE, OutcomeEnum.NEGATIVE);
		
//		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
//		test_eval.initialize();
//		test_evaluateTargetOverprotection(Square.E5, Player.WHITE, OutcomeEnum.NEGATIVE);
	}

}
