package gamestate;

import static gamestate.Bitboard.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.Square;


class BitboardTest {

	@Test
	void testBitScanForward() {
		assertEquals(64, Bitboard.getFirstSquareIndex(0));// counter-intuitive but correct
		assertEquals(0, Bitboard.getFirstSquareIndex(1));
		assertEquals(1, Bitboard.getFirstSquareIndex(2));
		assertEquals(2, Bitboard.getFirstSquareIndex(4));
		assertEquals(1, Bitboard.getFirstSquareIndex(6));
		assertEquals(10, Bitboard.getFirstSquareIndex(1024 + 4096));
		assertEquals(63, Bitboard.getFirstSquareIndex(-9223372036854775808L));
		assertEquals(2, Bitboard.getFirstSquareIndex(-9223372036854775808L + 4));
		assertEquals(3, Bitboard.getFirstSquareIndex(4611686018427387904L + 8));
		assertEquals(62, Bitboard.getFirstSquareIndex(4611686018427387904L));
		assertEquals(3, Bitboard.getFirstSquareIndex(4611686018427387904L + 8));
	}

	@Test
	void testExtractLsb() {
		assertEquals(0, Bitboard.extractLsb(0));
		assertEquals(0, Bitboard.extractLsb(1));
		assertEquals(0, Bitboard.extractLsb(8));
		assertEquals(0, Bitboard.extractLsb(1024));
		assertEquals(0, Bitboard.extractLsb(274877906944L));
		assertEquals(17179869184L, Bitboard.extractLsb(262144 + 17179869184L));
		assertEquals(549755813888L + 4398046511104L, Bitboard.extractLsb(274877906944L + 549755813888L + 4398046511104L));
		assertEquals(0, Bitboard.extractLsb(-9223372036854775808L));
		assertEquals(-9223372036854775808L, Bitboard.extractLsb(-9223372036854775808L + 2));
	}

	@Test
	void testIsolateLsb() {
		assertEquals(0, Bitboard.isolateLsb(0));
		assertEquals(1, Bitboard.isolateLsb(1));
		assertEquals(8, Bitboard.isolateLsb(8));
		assertEquals(2048, Bitboard.isolateLsb(2048));
		assertEquals(4611686018427387904L, Bitboard.isolateLsb(4611686018427387904L));
		assertEquals(-9223372036854775808L, Bitboard.isolateLsb(-9223372036854775808L));
		assertEquals(1, Bitboard.isolateLsb(-1));
		assertEquals(1, Bitboard.isolateLsb(-1 - 8));
		assertEquals(8, Bitboard.isolateLsb(-8 - 512));
		assertEquals(1, Bitboard.isolateLsb(-9223372036854775808L + 1));
		assertEquals(2, Bitboard.isolateLsb(-9223372036854775808L + 2));
		assertEquals(8, Bitboard.isolateLsb(-9223372036854775808L + 1 + 1 + 2 + 4));
		assertEquals(2048, Bitboard.isolateLsb(2048 + 4611686018427387904L));
		assertEquals(4194304, Bitboard.isolateLsb(4194304 + 4398046511104L));
		assertEquals(549755813888L, Bitboard.isolateLsb(549755813888L + 4398046511104L));
	}

	@Test
	void testHasOnly1Bit() {
		assertTrue(Bitboard.hasOnly1Bit(0x1));
		assertTrue(Bitboard.hasOnly1Bit(2));
		assertTrue(Bitboard.hasOnly1Bit(64));
		assertTrue(Bitboard.hasOnly1Bit(4611686018427387904L));
		assertTrue(Bitboard.hasOnly1Bit(-9223372036854775808L));
		assertFalse(Bitboard.hasOnly1Bit(0));
		assertFalse(Bitboard.hasOnly1Bit(3));
		assertFalse(Bitboard.hasOnly1Bit(67));
		assertFalse(Bitboard.hasOnly1Bit(4611686018427387902L));
		assertFalse(Bitboard.hasOnly1Bit(-9223372036854775800L));
	}

	@Test
	void testPopcount() {
		assertEquals(0, popcount(0L));
		assertEquals(3, popcount(initFromAlgebraicSquares("b4", "b8", "g5")));
		assertEquals(5, popcount(initFromAlgebraicSquares("a1", "a8", "h1", "h8", "d3")));
	}

	@Test
	void testBitIterationMacro() {
		Long[][] summands = { {}, { 1L, 32L, 137438953472L }, { 64L, 8796093022208L, -9223372036854775808L }, { -9223372036854775808L } };
		for (int test = 0; test < summands.length; ++test) {
			Long val = 0L;
			for (int p = 0; p < summands[test].length; ++p)
				val += summands[test][p];
			ArrayList<Long> ans = new ArrayList<>();
			for (long zarg = val, b = zarg & -zarg; zarg != 0L; zarg &= (zarg - 1), b = zarg & -zarg) {
				ans.add(b);
			}
			// System.out.println(ans + " = " + val + " vs " + ans);
			// System.out.println(Bitboard.prettyPrint(val));
			assertArrayEquals(summands[test], ans.toArray());
		}
	}

	@Test
	void testBitAccess() {
		long v = 0;
		v = Bitboard.setBit(v, 43);
		v = Bitboard.clearBit(v, 3);
		v = Bitboard.setBit(v, 43);
		assertEquals(8796093022208L, v);
		assertTrue(Bitboard.testBit(v, 43));
		assertFalse(Bitboard.testBit(v, 3));
		v = Bitboard.toggleBit(v, 63);
		v = Bitboard.clearBit(v, 43);
		assertEquals(-9223372036854775808L, v);
		assertTrue(Bitboard.testBit(v, 63));
	}

	@Test
	void testPrettyPrint() {
		String ans = new String(
				"  a b c d e f g h \r\n" + "8 . . . . . . . . 8\r\n" + "7 . . . . . . . . 7\r\n" + "6 . . . . . . . . 6\r\n" + "5 . . . . . x . . 5\r\n"
						+ "4 . . . . . . . . 4\r\n" + "3 . . . . . . . . 3\r\n" + "2 . . . . . . . . 2\r\n" + "1 x . . . . x . . 1\r\n" + "  a b c d e f g h ");
		long v = initFromAlgebraicSquares("a1", "f1", "f5");
		String test = prettyPrint(v);
		assertEquals(ans, test);
		ans = new String(
				"  a b c d e f g h \r\n" + "8 x . . . . . . x 8\r\n" + "7 . . . . . . . . 7\r\n" + "6 . . x . . . . . 6\r\n" + "5 . . . . . . . . 5\r\n"
						+ "4 . . . . . . . . 4\r\n" + "3 . . . . . . . . 3\r\n" + "2 . . . . . . . . 2\r\n" + "1 . . . . . x . x 1\r\n" + "  a b c d e f g h ");
		v = initFromAlgebraicSquares("f1", "h1", "c6", "a8", "h8");
		test = prettyPrint(v);
		assertEquals(ans, test);
		v = initFromAlgebraicSquares("a1", "h1", "c6", "a8", "h8");
		assertEquals("{a1 h1 c6 a8 h8 }", Bitboard.toListString(v));
	}

	@Test
	void testInitFromAlgebraicSquares() {
		long v = initFromAlgebraicSquares("b1", "b1", "h8");
		assertTrue(Bitboard.testBit(v, 1));
		assertTrue(Bitboard.testBit(v, 63));

		v = initFromAlgebraicSquares("h7", "a0", "foobar", null);
		assertEquals(setBit(0, 55), v);
	}

	@Test
	void testBitIndexIterationMacro() {
		Integer[][] powers = { {}, { 0, 3, 42 }, { 2, 30, 60 }, { 63 }, { 17, 63 } };
		for (int test = 0; test < powers.length; ++test) {
			Long val = 0L;
			for (int p = 0; p < powers[test].length; ++p)
				val |= Long.rotateLeft(Long.valueOf(1L), powers[test][p]);
			ArrayList<Integer> ans = new ArrayList<>();
			{
				int bi = 0;
				for (long zarg = val, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
																																							// expanded
					bi = Bitboard.getFirstSquareIndex(barg);
					ans.add(bi);
				}
			}

			// System.out.println(ans + " = " + val);
			assertArrayEquals(powers[test], ans.toArray());
			
			ans = new ArrayList<>();
			int ind = 0;
			for (long zarg = val, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBits expanded
				ind = Bitboard.getFirstSquareIndex(barg);
				ans.add(ind);
			}
			assertArrayEquals(powers[test], ans.toArray());
		}
	}
	
	@Test
	void testBitShifts() {
		long v = 0xc3811c141c1481c3L;
		assertEquals(0x811c141c1481c300L, Bitboard.shiftNorth(v));
		assertEquals(0xc3811c141c1481L, Bitboard.shiftSouth(v));
		assertEquals(0x8602382838280286L, Bitboard.shiftEast(v));
		assertEquals(0x61400e0a0e0a4061L, Bitboard.shiftWest(v));
	}
	
	@Test
	void testMasks() {
		assertEquals(0xffL, Bitboard.getRankMask(Square.A1));
		assertEquals(0xffL, Bitboard.getRankMask(Square.C1));
		assertEquals(0xff000000000000L, Bitboard.getRankMask(Square.B7));
		assertEquals(0xff00000000000000L, Bitboard.getRankMask(Square.H8));
		
		assertEquals(0x101010101010101L, Bitboard.getFileMask(Square.A1));
		assertEquals(0x404040404040404L, Bitboard.getFileMask(Square.C1));
		assertEquals(0x202020202020202L, Bitboard.getFileMask(Square.B7));
		assertEquals(0x8080808080808080L, Bitboard.getFileMask(Square.H8));
		
		assertEquals(0x8040201008040201L, Bitboard.getDiagMask(Square.A1));
		assertEquals(0x804020100804L, Bitboard.getDiagMask(Square.C1));
		assertEquals(0x402010000000000L, Bitboard.getDiagMask(Square.B7));
		assertEquals(0x8040201008040201L, Bitboard.getDiagMask(Square.H8));
		
		assertEquals(0x1L, Bitboard.getAntiDiagMask(Square.A1));
		assertEquals(0x10204L, Bitboard.getAntiDiagMask(Square.C1));
		assertEquals(0x102040810204080L, Bitboard.getAntiDiagMask(Square.B7));
		assertEquals(0x4080000000000000L, Bitboard.getAntiDiagMask(Square.G8));
		assertEquals(0x8000000000000000L, Bitboard.getAntiDiagMask(Square.H8));
	}
	
	@Test
	void testSquaresBetween() {
		assertEquals(0x80400L, Bitboard.getSquaresBetween(Square.B1, Square.E4));
		assertEquals(0x80400L, Bitboard.getSquaresBetween(Square.E4, Square.B1));
		assertEquals(0x8102040000000L, Bitboard.getSquaresBetween(Square.C8, Square.H3));
		assertEquals(0x8102040000000L, Bitboard.getSquaresBetween(Square.H3, Square.C8));
		assertEquals(0x7e00000000000000L, Bitboard.getSquaresBetween(Square.A8, Square.H8));
		assertEquals(0x7e00000000000000L, Bitboard.getSquaresBetween(Square.H8, Square.A8));
		assertEquals(0x40404040404000L, Bitboard.getSquaresBetween(Square.G1, Square.G8));
		assertEquals(0x40404040404000L, Bitboard.getSquaresBetween(Square.G8, Square.G1));
		
		assertEquals(0x0L, Bitboard.getSquaresBetween(Square.G8, Square.G7));
		assertEquals(0x0L, Bitboard.getSquaresBetween(Square.A1, Square.A2));
		assertEquals(0x0L, Bitboard.getSquaresBetween(Square.B4, Square.C4));
		
		assertEquals(0x0L, Bitboard.getSquaresBetween(Square.F5, Square.D4));
		assertEquals(0x0L, Bitboard.getSquaresBetween(Square.B3, Square.H5));
		
	}

}
