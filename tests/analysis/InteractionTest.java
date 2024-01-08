package analysis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;

class InteractionTest {

	@Test
	void testCRUD() {
		int interaction = Interaction.createGuardBound_negativeToPositive(Square.C1, Square.G5);
		assertEquals("{c1 guards g5 (- to +)}", Interaction.toString(interaction));
		interaction = Interaction.createGuardBound_negativeToPositive(Square.A1, Square.H8);
		assertEquals("{a1 guards h8 (- to +)}", Interaction.toString(interaction));
		interaction = Interaction.createGuardBound_negativeToPositive(Square.H8, Square.A1);
		assertEquals("{h8 guards a1 (- to +)}", Interaction.toString(interaction));
		
		interaction = Interaction.createGuardBound_negativeToNeutral(Square.H5, Square.A3);
		assertEquals("{h5 guards a3 (- to 0)}", Interaction.toString(interaction));
		
		interaction = Interaction.createGuardBound_neutralToPositive(Square.D6, Square.C7);
		assertEquals("{d6 guards c7 (0 to +)}", Interaction.toString(interaction));
		
		interaction = Interaction.createPin_positive(Square.B2, Square.C3, Square.E4);
		assertEquals("{b2 pins c3 to e4 (+)}", Interaction.toString(interaction));
		interaction = Interaction.createPin_neutral(Square.H5, Square.H7, Square.H8);
		assertEquals("{h5 pins h7 to h8 (0)}", Interaction.toString(interaction));
		
		interaction = Interaction.createDiscoveredThreat_neutral(Square.B2, Square.C3, Square.E4);
		assertEquals("{b2 via c3 threatens e4 (0)}", Interaction.toString(interaction));
		interaction = Interaction.createDiscoveredThreat_positive(Square.H5, Square.H7, Square.H8);
		assertEquals("{h5 via h7 threatens h8 (+)}", Interaction.toString(interaction));
	}

}
