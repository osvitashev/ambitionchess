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
	public static int getFirstSquareIndex(long bb) {
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

	public static long shiftEast(long bb) {
		return (bb<<1) & ~0x101010101010101L;
	}
	
	public static long shiftWest(long bb) {
		return (bb>>>1) & ~0x8080808080808080L;// unsigned shift pads the argument with zeros.
	}
	
	public static long shiftNorth(long bb) {
		return bb << 8;
	}

	public static long shiftSouth(long bb) {
		return bb >>> 8;// unsigned shift pads the argument with zeros.
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

	private final static long ranks_64[] = new long[64];
	private final static long files_64[] = new long[64];
	private final static long diagonals_64[] = new long[64];
	private final static long anti_diagonals_64[] = new long[64];
	private final static long masks_in_between[][] = new long[64][64];
	static {
		for (int i = 0; i < 64; i += 8)
			ranks_64[i] = (long) (0b11111111) << (i);
		for (int x = 0; x < 8; ++x)
			for (int y = 0; y < 8; ++y)
				ranks_64[8 * y + x] = ranks_64[8 * y];
		for (int i = 0; i < 8; i += 1)
			files_64[i] = (long) (0b0000000100000001000000010000000100000001000000010000000100000001L) << i;
		for (int x = 0; x < 8; ++x)
			for (int y = 0; y < 8; ++y)
				files_64[8 * y + x] = files_64[x];

		long upper_left_side_zero = 0b1000000011000000111000001111000011111000111111001111111011111111L;
		long lower_right_side_zero = 0b1111111101111111001111110001111100001111000001110000001100000001L;
		long lower_left_side_zero = 0b1111111011111100111110001111000011100000110000001000000000000000L;
		long upper_right_side_zero = 0b0000000000000001000000110000011100001111000111110011111101111111L;
		long main_diagonal = 0b1000000001000000001000000001000000001000000001000000001000000001L; // A1 to H8
		long anti_diagonal = 0b0000000100000010000001000000100000010000001000000100000010000000L; // H1 to A8
		for (int i = 0; i < 8; i++) {
			long current_diagonal = (main_diagonal << i) & upper_left_side_zero;
			long tmp = current_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				diagonals_64[index] = current_diagonal;
				tmp = tmp & (~(1L << index));
			}
		}
		for (int i = 1; i < 8; i++) {
			long current_diagonal = (main_diagonal >>> i) & lower_right_side_zero;
			long tmp = current_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				diagonals_64[index] = current_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		for (int i = 1; i < 8; i++) {
			long current_anti_diagonal = (anti_diagonal << i) & lower_left_side_zero;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		{
			long current_anti_diagonal = anti_diagonal;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		for (int i = 1; i < 8; i++) {
			long current_anti_diagonal = (anti_diagonal >>> i) & upper_right_side_zero;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		
	    for (int i = 0; i < 64; ++i) {
	        for (int j = 0; j < 64; ++j) {
	            long sq1 = Bitboard.initFromSquare(i);
	            long sq2 = Bitboard.initFromSquare(j);

	            int dx = (j % 8) - (i % 8)  ;//(sq2.file() - sq1.file());
	            int dy = (j / 8) - (i / 8);//(sq2.rank() - sq1.rank());
	            int adx = dx > 0 ? dx : -dx;
	            int ady = dy > 0 ? dy : -dy;

	            if (dx == 0 || dy == 0 || adx == ady) {
	                long mask=0;
	                while (sq1 != sq2) {
	                    if (dx > 0) {
	                        sq1 = Bitboard.shiftEast(sq1);
	                    } else if (dx < 0) {
	                        sq1 = Bitboard.shiftWest(sq1);
	                    }
	                    if (dy > 0) {
	                        sq1 = Bitboard.shiftNorth(sq1);
	                    } else if (dy < 0) {
	                        sq1 = Bitboard.shiftSouth(sq1);
	                    }
	                    mask |= sq1;
	                }
	                masks_in_between[i][j] = mask & ~ sq2;
	            }
	        }
	    }
	}

	public static long getRankMask(int sq) {
		DebugLibrary.validateSquare(sq);
		return ranks_64[sq];
	}

	public static long getFileMask(int sq) {
		DebugLibrary.validateSquare(sq);
		return files_64[sq];
	}

	public static long getDiagMask(int sq) {
		DebugLibrary.validateSquare(sq);
		return diagonals_64[sq];
	}

	public static long getAntiDiagMask(int sq) {
		DebugLibrary.validateSquare(sq);
		return anti_diagonals_64[sq];
	}
	
	public static long getSquaresBetween(int sq1, int sq2) {
		DebugLibrary.validateSquare(sq1);
		DebugLibrary.validateSquare(sq2);
		return masks_in_between[sq1][sq2];
	}

	private Bitboard() {
	}

}
