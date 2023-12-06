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
	
	void test_evaluateTargetOverprotection(int sq, int player, int expectedOutcome) {
		test_eval.evaluateTargetOverpotection(sq, player, expectedOutcome);
	}
	
	@Test
	void test() {
		test_game.loadFromFEN("3r4/1p2n1k1/p2r4/3b1n2/8/n2Q4/3R4/3R2K1 w - - 0 1");
		test_eval.initialize();
		test_evaluateTargetOverprotection(Square.D5, Player.WHITE, OutcomeEnum.NEGATIVE);
		test_evaluateTargetOverprotection(Square.F5, Player.WHITE, OutcomeEnum.NEGATIVE);
		test_evaluateTargetOverprotection(Square.A6, Player.WHITE, OutcomeEnum.NEGATIVE);
	}

}
