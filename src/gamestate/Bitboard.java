package gamestate;

import gamestate.GlobalConstants.Square;

/**
 * 
//@formatter:off
 * Iteration macros used in lieu of real iterators
 * 
 * iterateOnBits
 * for(long zarg=${arg}, ${barg}= Bitboard.isolateLsb(zarg); zarg!=0L; zarg=Bitboard.extractLsb(zarg), ${barg}=Bitboard.isolateLsb(zarg)){//iterateOnBits expanded
 *	${line_selection}${cursor}
 * }
 * 
 * iterateOnBitIndices
 * {
 *	int bi=0;
 * 	for(long zarg=${arg}, ${barg}= Bitboard.isolateLsb(zarg); zarg!=0L; zarg=Bitboard.extractLsb(zarg), ${barg}=Bitboard.isolateLsb(zarg)){//iterateOnBitIndices expanded
 * 		bi=Bitboard.bitScanForward(barg);
 * 		${line_selection}${cursor}
 * 	}
 * }
 * 
 * 
 * 
 * 
//@formatter:on
 * @author sveta
 *
 */
public final class Bitboard {
	public static int bitScanForward(long bb) {
		return Long.numberOfTrailingZeros(bb);
	}

	public static long extractLsb(long bb) {
		return bb & (bb - 1);
	}

	public static long isolateLsb(long bb) {
		return bb & (-bb);
	}

	public static boolean hasOnly1Bit(long bb) {
		return bb != 0L && extractLsb(bb) == 0L;
	}

	public static boolean isEmpty(long bb) {
		return bb == 0L;
	}

	public static boolean testBit(long bb, int i) {
		DebugLibrary.validateSquare(i);
		return 0L != (bb & 1L << i);
	}

	public static long setBit(long bb, int i) {
		DebugLibrary.validateSquare(i);
		return bb | 1L << i;
	}

	public static long initFromSquare(int i) {
		DebugLibrary.validateSquare(i);
		return 1L << i;
	}

	public static long clearBit(long bb, int i) {
		DebugLibrary.validateSquare(i);
		return bb & ~(1L << i);
	}

	public static long toggleBit(long bb, int i) {
		DebugLibrary.validateSquare(i);
		return bb ^ (1L << i);
	}

	public static int popcount(long bb) {
		return Long.bitCount(bb);
	}

	// TODO: ADD directional shifts for east and west
	public static long shiftNorth(long bb) {
		return bb<<8;
	}
	
	public static long shiftSouth(long bb) {
		return bb>>>8;// unsigned shift pads the argument with zeros.
	}

	/**
	 * Expensive.
	 */
	public static long initFromAlgebraicSquares(String... args) {
		long ret = 0;
		int sq;
		for (String s : args) {
			sq = Square.algebraicStringToSquare(s);
			if (sq != Square.SQUARE_NONE)
				ret = setBit(ret, sq);
		}
		return ret;
	}

	/**
	 * Expensive.
	 */
	public static String prettyPrint(long bb) {
		StringBuilder b = new StringBuilder();
		int i = 56;
		b.append("  a b c d e f g h \r\n");
		while (i >= 0) {
			if (i % 8 == 0) {
				b.append(i / 8 + 1);
				b.append(' ');
			}
			if (Bitboard.testBit(bb, i))
				b.append("x ");
			else
				b.append(". ");
			if (i % 8 == 7) {
				b.append(i / 8 + 1);
				b.append("\r\n");
				i -= 16;
			}
			i++;
		}
		b.append("  a b c d e f g h ");
		return b.toString();
	}

	private Bitboard() {
	}

}
