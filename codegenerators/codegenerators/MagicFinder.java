package codegenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MagicFinder {
	class Item{
		long identifier;
		int payload;
	}
	
	class HashedValue{
		long keyUsed;
		int payload;
		boolean isUsed;
	}
	
	public MagicFinder() {
		NUM_ITEMS = 4096;
		NUM_SLOTS = 4096;
		KEY_WIDTH = 12;
		
		hashedValues = new HashedValue[NUM_SLOTS];
		inputItems = new Item[NUM_ITEMS];
		
		for(int i=0; i<NUM_ITEMS; ++i)
			inputItems[i]=new Item();
		for(int i=0; i<NUM_SLOTS; ++i)
			hashedValues[i]=new HashedValue();
		//placeholder value population
		Random ran_input = new Random(123123123);
		for(int i=0; i<NUM_ITEMS; ++i) {
			inputItems[i].identifier=ran_input.nextLong();
			inputItems[i].payload=ran_input.nextInt(100);
		}
	}
	
	int NUM_ITEMS;
	int NUM_SLOTS;
	int KEY_WIDTH;
	
	HashedValue[] hashedValues;
	Item[] inputItems;
	
	int getHashIndex(long identifier, long hashKey) {
		return (int)((identifier * hashKey)>>>(64-KEY_WIDTH));
	}
	

	void reset() {
		for(int i=0; i<NUM_SLOTS; ++i) {
			hashedValues[i].payload=0;
			hashedValues[i].keyUsed=0;
			hashedValues[i].isUsed=false;
		}
	}
	
	public void lookForMagic() {
		Random ran = new Random();	
		int hashIndex, matched, free, bestScore=0;
		long hashKey;
		for(int attempt=0; attempt<1000000; ++attempt) {
			reset();
			hashKey=ran.nextLong() & ran.nextLong();
			for(Item it : inputItems) {
				hashIndex=getHashIndex(it.identifier, hashKey);
				if(hashedValues[hashIndex].isUsed == false) {
					hashedValues[hashIndex].keyUsed=hashKey;
					hashedValues[hashIndex].payload=it.payload;
					hashedValues[hashIndex].isUsed=true;
				}
			}
			matched=0;
			for(Item it : inputItems) {
				hashIndex=getHashIndex(it.identifier, hashKey);
				if(hashedValues[hashIndex].keyUsed == hashKey && hashedValues[hashIndex].payload == it.payload)
					matched++;
			}
			free = NUM_SLOTS;
			for(HashedValue hv : hashedValues)
				if(hv.isUsed == true)
					free--;
			if(matched > bestScore) {
				bestScore=matched;
				double successRate = (double)matched/(double)inputItems.length;
				System.out.println("Try: "+ attempt +
						", matches: " + matched +
						", itemsRemaining: "+ (NUM_ITEMS-matched) +
						", slotsTaken: " + (NUM_SLOTS-free)+
						", slotsFree: " + free +
						", hashKey: " + hashKey +
						", success%: " + String.format("%.2f", successRate)
				);
			}
			
		}
	}

	public static void main(String[] args) {
		MagicFinder mf= new MagicFinder();
		mf.lookForMagic();
		
		
	}
}
