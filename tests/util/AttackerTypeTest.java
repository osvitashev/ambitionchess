package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

class AttackerTypeTest {

	@Test
	void testCRUD() {
		int value = AttackerType.create(PieceType.QUEEN, Square.F5);
		assertEquals(PieceType.QUEEN, AttackerType.getAttackerPieceType(value));
		assertEquals(Square.F5, AttackerType.getAttackerSquareFrom(value));
		
		value = AttackerType.nullValue();
		assertEquals(PieceType.NO_PIECE, AttackerType.getAttackerPieceType(value));
	}

}
