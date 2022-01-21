package util;

/***
 * Class containing static methods required to do bit manipulation on integer bit fields.
 * It is essentially a base class for Move and UndoInfo, but avoid explicit inheritance.
 *
 */

public class BitField32 {
	private static final long[] BIT_EXTRACTION_MASK = { 0, 0x1, 0x3, 0x7, 0xF, 0x1F, 0x3F, 0x7F, 0xFF };

	public static int setBits(int info, int val, int pos, int len) {
		int val2 = val;
		info &= ~(BIT_EXTRACTION_MASK[len] << pos);// this clears out the bit range we want to set.
		return info | (val2 << pos);
	}

	public static int getBits(int info, int pos, int len) {
		return (int) ((info >> pos) & BIT_EXTRACTION_MASK[len]);
	}

	public static int setBoolean(int info, boolean val, int pos) {// OPTIMIZE: try optimizing the branch...
		if (val)
			info |= (1L << pos);
		else
			info &= ~(1L << pos);// clears the bit
		return info;
	}

	public static boolean getBoolean(int info, int pos) {
		return ((info >> pos) % 2) == 1;
	}

}
