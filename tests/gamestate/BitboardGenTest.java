package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BitboardGenTest {

	@Test
	void testKingSet() {
		assertEquals(0x302, BitboardGen.getKingSet(Square.A1));
		assertEquals(0x705, BitboardGen.getKingSet(Square.B1));
		assertEquals(0x7050, BitboardGen.getKingSet(Square.F1));
		assertEquals(0xe0a0, BitboardGen.getKingSet(Square.G1));
		assertEquals(0xc040, BitboardGen.getKingSet(Square.H1));
		assertEquals(0x302030000L, BitboardGen.getKingSet(Square.A4));
		assertEquals(0x203000000000000L, BitboardGen.getKingSet(Square.A8));
		assertEquals(0x141c000000000000L, BitboardGen.getKingSet(Square.D8));
		assertEquals(0x40c0000000000000L, BitboardGen.getKingSet(Square.H8));
		assertEquals(0xc040c0000000L, BitboardGen.getKingSet(Square.H5));
		assertEquals(0xe0a0e00, BitboardGen.getKingSet(Square.C3));
		assertEquals(0x3828380000000000L, BitboardGen.getKingSet(Square.E7));
		assertEquals(0xe0a0e00000000000L, BitboardGen.getKingSet(Square.G7));
	}

	@Test
	void testKnightSet() {
		assertEquals(0x20400L, BitboardGen.getKnightSet(Square.A1));
		assertEquals(0x50800L, BitboardGen.getKnightSet(Square.B1));
		assertEquals(0x508800L, BitboardGen.getKnightSet(Square.F1));
		assertEquals(0xa01000L, BitboardGen.getKnightSet(Square.G1));
		assertEquals(0x402000L, BitboardGen.getKnightSet(Square.H1));
		assertEquals(0x20400040200L, BitboardGen.getKnightSet(Square.A4));
		assertEquals(0x4020000000000L, BitboardGen.getKnightSet(Square.A8));
		assertEquals(0x22140000000000L, BitboardGen.getKnightSet(Square.D8));
		assertEquals(0x20400000000000L, BitboardGen.getKnightSet(Square.H8));
		assertEquals(0x40200020400000L, BitboardGen.getKnightSet(Square.H5));
		assertEquals(0xa1100110aL, BitboardGen.getKnightSet(Square.C3));
		assertEquals(0x4400442800000000L, BitboardGen.getKnightSet(Square.E7));
		assertEquals(0x100010a000000000L, BitboardGen.getKnightSet(Square.G7));
	}

	@Test
	void testBishopSet() {
		long occ = 0x2318000850a700L;
		assertEquals(0x200L, BitboardGen.getBishopSet(Square.A1, occ));
		assertEquals(0x8050005L, BitboardGen.getBishopSet(Square.B2, occ));
		assertEquals(0x8040280028400000L, BitboardGen.getBishopSet(Square.E5, occ));
		assertEquals(0xa000a010080400L, BitboardGen.getBishopSet(Square.G6, occ));
		assertEquals(0x8041221400142241L, BitboardGen.getBishopSetEmptyBoard(Square.D4));
	}

	@Test
	void testRookSet() {
		long occ = 0x2318000850a700L;
		assertEquals(0x1feL, BitboardGen.getRookSet(Square.A1, occ));
		assertEquals(0x2020202020502L, BitboardGen.getRookSet(Square.B2, occ));
		assertEquals(0x10ef10100000L, BitboardGen.getRookSet(Square.E5, occ));
		assertEquals(0x4040b04040400000L, BitboardGen.getRookSet(Square.G6, occ));
		assertEquals(0x8080808f7080808L, BitboardGen.getRookSetEmptyBoard(Square.D4));
	}

	@Test
	void testQueenSet() {
		long occ = 0x2318000850a700L;
		assertEquals(0x200L | 0x1feL, BitboardGen.getQueenSet(Square.A1, occ));
		assertEquals(0x8050005L | 0x2020202020502L, BitboardGen.getQueenSet(Square.B2, occ));
		assertEquals(0x8040280028400000L | 0x10ef10100000L, BitboardGen.getQueenSet(Square.E5, occ));
		assertEquals(0xa000a010080400L | 0x4040b04040400000L, BitboardGen.getQueenSet(Square.G6, occ));
		assertEquals(0x8041221400142241L | 0x8080808f7080808L, BitboardGen.getQueenSetEmptyBoard(Square.D4));
	}

	@Test
	void testPawnAttackSet() {
		assertEquals(0x200L, BitboardGen.getPawnAttackSet(Square.A1, Player.WHITE));
		assertEquals(0x0L, BitboardGen.getPawnAttackSet(Square.A1, Player.BLACK));
		assertEquals(0x200000000L, BitboardGen.getPawnAttackSet(Square.A4, Player.WHITE));
		assertEquals(0x20000L, BitboardGen.getPawnAttackSet(Square.A4, Player.BLACK));
		assertEquals(0x500000000000L, BitboardGen.getPawnAttackSet(Square.F5, Player.WHITE));
		assertEquals(0x50000000L, BitboardGen.getPawnAttackSet(Square.F5, Player.BLACK));
		assertEquals(0x40000000000000L, BitboardGen.getPawnAttackSet(Square.H6, Player.WHITE));
		assertEquals(0x4000000000L, BitboardGen.getPawnAttackSet(Square.H6, Player.BLACK));
		assertEquals(0x0L, BitboardGen.getPawnAttackSet(Square.H8, Player.WHITE));
		assertEquals(0x40000000000000L, BitboardGen.getPawnAttackSet(Square.H8, Player.BLACK));
	}
	
	@Test
	void testMultiplePawnAttackSet() {
		assertEquals(0x4000a00005020000L, BitboardGen.getMultiplePawnAttackSet(0x80004000020100l, Player.WHITE));
		assertEquals(0x4000a0000502L, BitboardGen.getMultiplePawnAttackSet(0x80004000020100l, Player.BLACK));
	}
	
	@Test
	void testMultiplePawnPushSet() {
		assertEquals(0x201000068c80000l, BitboardGen.getMultiplePawnPushSet(0x201000020e800L, Player.WHITE, 0x72c1188422e400L));
		assertEquals(0x30e018840204l, BitboardGen.getMultiplePawnPushSet(0x70c01884020400L, Player.BLACK, 0x72c1188422e400L));
	}
	
	@Test
	void testMultipleKnightSet() {
		assertEquals(660736, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("a1", "c1")));
		assertEquals(45053588739212288l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("e1", "g5")));
		assertEquals(2689605648l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("h1", "g2")));
		assertEquals(38932607513788416l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("c5", "f8")));
		assertEquals(145243304219377664l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("a6", "a8")));
		assertEquals(-8601707818633920512l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("g5", "f7")));
		assertEquals(246496766255104l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("g4", "h4")));
		assertEquals(-6903947788610961408l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("h8", "g6")));
		assertEquals(4899991333173757952l, BitboardGen.getMultipleKnightSet(Bitboard.initFromAlgebraicSquares("e7", "f1")));
	}

}
