package util;

public class BitwiseVectorTranspose {
	private long data[];
	private int size_data;
	
	public BitwiseVectorTranspose(int size) {
		data = new long [size];
		size_data=size;
	}
	
	public int size() {
		return size_data;
	}
	
	public void resetTo0s() {
		for(int i=0; i<size(); ++i)
			data[i]=0l;
	}
	
	/**
	 * reset to ZERO inverse. Incidentally this corresponds to NO_PIECE constant.
	 */
	public void resetTo1s() {
		for(int i=0; i<size(); ++i)
			data[i]=~0l;
	}
	
	public long get(int i) {
		return data[i];
	}
	
	public void set(int i, long val) {
		data[i]=val;
	}
	
	public void setWhere(int i, long val, long mask) {
		data[i]= data[i]&~mask | val&mask;
	}
	
	public void leftShiftWhere(long mask, int shift) {
		assert shift>=0;
		assert shift<size();
		for(int i=size()-1; i>=shift; --i)
			data[i]= data[i]&~mask | data[i-shift]&mask;
		for(int i=0; i<shift; ++i)
			data[i]=data[i]&~mask;
	}
	

}
