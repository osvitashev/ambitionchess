package puzzler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GameOutcomeTest {

	@Test
	void testBitAccess() {
		int go = GameOutcome.createWhiteWin(15);
		assertEquals(GameOutcome.getDistance(go), 15);
		assertEquals(GameOutcome.OutcomeType.WHITE_WIN, GameOutcome.getType(go));
	}

}
