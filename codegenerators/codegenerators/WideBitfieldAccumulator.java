package codegenerators;

import javax.management.RuntimeErrorException;

public class WideBitfieldAccumulator {
	byte payload[];
	byte free[];
	
	//1 2 4 8 16 32 64
	//1 2 3 4 5  6  7
	
	WideBitfieldAccumulator(int byteWidth){
		System.out.println("initializing WideBitfieldAccumulator to "+ byteWidth*8 +" bits");
		payload=new byte[byteWidth];
		free=new byte[byteWidth];
	}
	
	boolean get(int i) {
		return (payload[i/8] & (1<<(i%8))) !=0;
	}
	
	void lock(int i) {
		free[i/8] |= (1<<(i%8));
	}
	
	void setPayload(int i) {
		if((free[i/8] & (1<<(i%8))) !=0)
			throw new RuntimeException("index is unavailable!");
		payload[i/8] |= (1<<(i%8));
		free[i/8] |= (1<<(i%8));
	}
	
	void reset() {
		for(int i=0; i<payload.length;++i)
			payload[i]=0;
		for(int i=0; i<free.length;++i)
			free[i]=0;
	}
}
