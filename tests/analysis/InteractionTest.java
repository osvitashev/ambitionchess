package analysis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;

class InteractionTest {

	@Test
	void testCRUD() {
		int interaction = Interaction.createAdequateGuardTiedUp(Square.C1, Square.G5);
		assertEquals("{c1 guards (is tied up with) g5}", Interaction.toString(interaction));
		interaction = Interaction.createAdequateGuardTiedUp(Square.A1, Square.H8);
		assertEquals("{a1 guards (is tied up with) h8}", Interaction.toString(interaction));
		interaction = Interaction.createAdequateGuardTiedUp(Square.H8, Square.A1);
		assertEquals("{h8 guards (is tied up with) a1}", Interaction.toString(interaction));
		
		interaction = Interaction.createGuardOverprotect(Square.H8, Square.A1);
		assertEquals("{h8 overprotects a1}", Interaction.toString(interaction));
		

	}

}
