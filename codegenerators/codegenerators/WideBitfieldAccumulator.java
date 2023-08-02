package codegenerators;

import javax.management.RuntimeErrorException;

public class WideBitfieldAccumulator {
	byte payload[];
	byte used[];
	
	//1 2 4 8 16 32 64
	//1 2 3 4 5  6  7
	
	WideBitfieldAccumulator(int bitWidth){
		int byteWidth = (int) Math.ceil((double) bitWidth / 8);
		System.out.println("initializing WideBitfieldAccumulator to "+ bitWidth +" bits or + "+ byteWidth + " bytes");
		payload=new byte[byteWidth];
		used=new byte[byteWidth];
	}
	
	boolean get(int i) {
		return (payload[i/8] & (1<<(i%8))) !=0;
	}
	
	boolean isUsed(int i) {
		return (used[i/8] & (1<<(i%8))) !=0;
	}
	
	void lock(int i) {
		used[i/8] |= (1<<(i%8));
	}
	
	void setPayload(int i) {
		if((used[i/8] & (1<<(i%8))) !=0)
			throw new RuntimeException("index is unavailable!");
		payload[i/8] |= (1<<(i%8));
		used[i/8] |= (1<<(i%8));
	}
	
	void reset() {
		for(int i=0; i<payload.length;++i)
			payload[i]=0;
		for(int i=0; i<used.length;++i)
			used[i]=0;
	}
}
