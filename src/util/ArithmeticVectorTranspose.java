package util;

import gamestate.Bitboard;

public class ArithmeticVectorTranspose {
	private static final int length = 6;
	private long[] data = new long[length];
	private int[] powersOf2 = new int[] {1, 2, 4, 8, 16, -32};
	
	private long getSum(long a, long b, long c) {
		return a^b^c;
	}
	
	private long getCarry(long a, long b, long c) {
		return a&b | (c&(a^b));
	}
	
	public void addScalar(long mask, int scalar) {
		long sum=0, carry=0;
		for(int i=0; i<length;++i) {
			if( ((1<<i)&scalar) ==0   ) {
				sum = getSum(data[i], 0l, carry);
				carry = getCarry(data[i], 0l, carry);
			}
			else {
				sum = getSum(data[i], ~0l, carry);
				carry = getCarry(data[i], ~0l, carry);
			}
			data[i]=sum&mask | data[i]&~mask;
		}
	}
	
	public int indexToScalar(int sq) {
		int ret=0;
		for(int i=0;i<length;++i)
			if( (Bitboard.initFromSquare(sq)&data[i])!=0   )
				ret+=powersOf2[i];
//		if( (Bitboard.initFromSquare(sq)&data[length-1])!=0   )
//			ret-=powersOf2[length-1];
		return ret;
	}
	
	public void clear() {
		for(int i=0;i<length;++i)
			data[i]=0;
	}
	
	
	/**
	 * 
	 * @param paradigit
	 * @param bit - 0 or non-0
	 * @return
	 */
	static private long helper_maskRightAndNotLeft(long lhs, int bit) {
		long rhs = bit==0 ? 0 :~0l;
		return ~lhs & rhs;
	}
	
	/**
	 * 
	 * @param paradigit
	 * @param bit - 0 or non-0
	 * @return
	 */
	static private long helper_maskLeftAndNotRight(long lhs, int bit) {
		long rhs = bit==0 ? 0 :~0l;
		return lhs & ~rhs;
	}
	

//	boolean lessThan(boolean [] lhs, boolean [] rhs){
//		if(lhs[length-1] == true && rhs[length-1]!=true)
//			return true;//lhs < 0 and rhs >= 0
//		for(int i=length-2;i>=0;--i){
//			if(lhs[i] != true && rhs[i]==true)
//			return true;
//		}
//		return false;
//	}
	
	public long maskWhereLessThan(int scalar) {
		long ret=0, off=0;
		ret |= helper_maskLeftAndNotRight(data[length-1], (1<<(length-1))&scalar);
		off |= helper_maskRightAndNotLeft(data[length-1], (1<<(length-1))&scalar);
		//skipping the sign bit
		for(int i=length-2; i>=0; --i) {
			ret |= helper_maskRightAndNotLeft(data[i], (1<<i)&scalar) & ~off; 
			off |= helper_maskLeftAndNotRight(data[i], (1<<i)&scalar);
		}
		
		return ret;
	}
	
}
