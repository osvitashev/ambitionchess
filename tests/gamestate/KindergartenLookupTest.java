package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;

class KindergartenLookupTest {

	@Test
	void testBishop() {
		long occ = 0x2318000850a700L;
		assertEquals(0x200L, KindergartenLookup.Bishop(Square.A1, occ));
		assertEquals(0x8050005L, KindergartenLookup.Bishop(Square.B2, occ));
		assertEquals(0x8040280028400000L, KindergartenLookup.Bishop(Square.E5, occ));
		assertEquals(0xa000a010080400L, KindergartenLookup.Bishop(Square.G6, occ));
		assertEquals(0xa000a01008000000L, KindergartenLookup.Bishop(Square.G7, occ));
		assertEquals(0x40201008000000L, KindergartenLookup.Bishop(Square.H8, occ));
	}
	
	@Test
	void testRookSet() {
		long occ = 0x2318000850a700L;
		assertEquals(0x1feL, KindergartenLookup.Rook(Square.A1, occ));
		assertEquals(0x2020202020502L, KindergartenLookup.Rook(Square.B2, occ));
		assertEquals(0x10ef10100000L, KindergartenLookup.Rook(Square.E5, occ));
		assertEquals(0x4040b04040400000L, KindergartenLookup.Rook(Square.G6, occ));
	}
	
	@Test
	void testQueenSet() {
		long occ = 0x2318000850a700L;
		assertEquals(0x200L | 0x1feL, KindergartenLookup.Queen(Square.A1, occ));
		assertEquals(0x8050005L | 0x2020202020502L, KindergartenLookup.Queen(Square.B2, occ));
		assertEquals(0x8040280028400000L | 0x10ef10100000L, KindergartenLookup.Queen(Square.E5, occ));
		assertEquals(0xa000a010080400L | 0x4040b04040400000L, KindergartenLookup.Queen(Square.G6, occ));
	}

}
