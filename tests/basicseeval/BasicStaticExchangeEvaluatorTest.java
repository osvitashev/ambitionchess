package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BasicStaticExchangeEvaluatorTest {
	
	void helper_test_getLeastValuableAttacker_mask(long expected, String fen, int sq_target, int player, long clearedLocationsMask) {
		assertEquals(expected, BasicStaticExchangeEvaluator.getLeastValuableAttacker_mask(new Gamestate(fen),
				sq_target,
				player, clearedLocationsMask));
	}

	@Test
	void getLeastValuableAttacker_mask_test() {
		helper_test_getLeastValuableAttacker_mask(0L, "8/4k3/8/8/8/2K5/8/8 w - - 0 1", Square.E5, Player.WHITE, 0L);
		
		helper_test_getLeastValuableAttacker_mask(Bitboard.initFromSquare(Square.E3),
				"8/4k3/8/6p1/8/1K2P3/8/8 w - - 0 1", Square.D4, Player.WHITE, 0L);

	}

}
