package analysis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;

class InteractionTest {

	@Test
	void testCRUD() {
		int interaction = Interaction.createGuardBound_negativeToPositive(Square.C1, Square.G5);
		assertEquals("{c1 guards (bound to) g5 (score: negative->positive)}", Interaction.toString(interaction));
		interaction = Interaction.createGuardBound_negativeToPositive(Square.A1, Square.H8);
		assertEquals("{a1 guards (bound to) h8 (score: negative->positive)}", Interaction.toString(interaction));
		interaction = Interaction.createGuardBound_negativeToPositive(Square.H8, Square.A1);
		assertEquals("{h8 guards (bound to) a1 (score: negative->positive)}", Interaction.toString(interaction));
		
		interaction = Interaction.createGuardBound_negativeToNeutral(Square.H5, Square.A3);
		assertEquals("{h5 guards (bound to) a3 (score: negative->neutral)}", Interaction.toString(interaction));
		
		interaction = Interaction.createGuardBound_neutralToPositive(Square.D6, Square.C7);
		assertEquals("{d6 guards (bound to) c7 (score: neutral->positive)}", Interaction.toString(interaction));

	}

}
