package codegenerators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	
	static int ARG_MOD256 = 96;
	
	public MagicFinder() {
		//MyLookupGenerator myGenerator = new MyLookupGenerator();
		
        List<Integer> read_payload = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("etc/payload.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int value = Integer.parseInt(line);
                read_payload.add(value);
            }
            
            System.out.println("Payload loaded from CSV: " + read_payload.size());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        
        List<Long> read_identifier = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("etc/identifiers.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                long value = Long.parseLong(line);
                read_identifier.add(value);
            }
            
            System.out.println("Identifier loaded from CSV: " + read_identifier.size());
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
		
		int itemCount=0;
		for(long matchupKey : read_identifier)
			if(ComboMatchUp.to256Index(matchupKey) % 256 == ARG_MOD256)
				itemCount++;
		
		NUM_ITEMS = itemCount;
		NUM_SLOTS = 4096*2;
		KEY_WIDTH = 13;
		
		hashedValues = new HashedValue[NUM_SLOTS];
		inputItems = new Item[NUM_ITEMS];
		
		for(int i=0; i<NUM_ITEMS; ++i)
			inputItems[i]=new Item();
		for(int i=0; i<NUM_SLOTS; ++i)
			hashedValues[i]=new HashedValue();
//		//placeholder value population
//		Random ran_input = new Random(123123123);
//		for(int i=0; i<NUM_ITEMS; ++i) {
//			inputItems[i].identifier=ran_input.nextLong();
//			inputItems[i].payload=ran_input.nextInt(100);
//		}
		
		
		int insertionIndex=0;
		for(int i=0; i<read_identifier.size(); ++i)
		if(ComboMatchUp.to256Index(read_identifier.get(i)) % 256 == ARG_MOD256){
			inputItems[insertionIndex].identifier=read_identifier.get(i);
			inputItems[insertionIndex].payload= read_payload.get(i);
			++insertionIndex;
		}
		
		System.out.println(">> mapping " + NUM_ITEMS + " items to " + NUM_SLOTS + " slots using a key of width: "+KEY_WIDTH);
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
		for(int attempt=0; attempt<500000000; ++attempt) {
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
						", hashKey: " + Long.toHexString(hashKey) +
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
