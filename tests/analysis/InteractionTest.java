package analysis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;

class InteractionTest {

	@Test
	void testCRUD() {
		int interaction = Interaction.createOverprotection(Square.C1, Square.G5);
		assertEquals("{c1 overprotects g5}", Interaction.toString(interaction));
		interaction = Interaction.createOverprotection(Square.A1, Square.H8);
		assertEquals("{a1 overprotects h8}", Interaction.toString(interaction));
		interaction = Interaction.createOverprotection(Square.H8, Square.A1);
		assertEquals("{h8 overprotects a1}", Interaction.toString(interaction));
	}

}
