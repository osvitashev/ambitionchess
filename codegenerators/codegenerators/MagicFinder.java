package codegenerators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MagicFinder {
	class Item{
		long identifier;
		int payload;
		boolean isMapped;
	}
	
	class HashedValue{
		long keyUsed;
		int payload;
		boolean isUsed;
	}
	
	static int ARG_MOD256 = 96;//96;
	int currentMatches=0;
	
	ArrayList<Long> magicsSoFar=new ArrayList<>();
	
	public MagicFinder(long ...initMagics) {
		//MyLookupGenerator myGenerator = new MyLookupGenerator();
		for(long m : initMagics)
			magicsSoFar.add(m);
		
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
		reset();
		applyPrevMagics();
		int free = NUM_SLOTS;
		for(HashedValue hv : hashedValues)
			if(hv.isUsed == true)
				free--;
		double successRate = (double)currentMatches/(double)inputItems.length;
		System.out.println(">>INITIAL matches: " + currentMatches +
				", itemsRemaining: "+ (NUM_ITEMS-currentMatches) +
				", slotsTaken: " + (NUM_SLOTS-free)+
				", slotsFree: " + free +
				", success%: " + String.format("%.2f", successRate)
		);
		showUnmappedInputs();
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
	

	/**
	 * performs a true reset on stateful variables - nothing else
	 */
	void reset() {
		currentMatches=0;
		for(int i=0; i<NUM_SLOTS; ++i) {
			hashedValues[i].payload=0;
			hashedValues[i].keyUsed=0;
			hashedValues[i].isUsed=false;
		}
		
		for(int i=0; i<NUM_ITEMS;++i)
			inputItems[i].isMapped=false;
	}
	
	void applyPrevMagics() {
		if(!magicsSoFar.isEmpty()) {
			long prevMagic =magicsSoFar.get(0);
			applyMagic(prevMagic);
		}
	}
	
	/**
	 * updates the hash table and match counts
	 * @param hashKey
	 */
	void applyMagic(long hashKey) {
		for(Item it : inputItems) {
			int hashIndex=getHashIndex(it.identifier, hashKey);
			if(it.isMapped ==false && hashedValues[hashIndex].isUsed == false) {
				hashedValues[hashIndex].keyUsed=hashKey;
				hashedValues[hashIndex].payload=it.payload;
				hashedValues[hashIndex].isUsed=true;
			}
		}
		for(Item it : inputItems) {
			int hashIndex=getHashIndex(it.identifier, hashKey);
			if(it.isMapped==false && hashedValues[hashIndex].keyUsed == hashKey && hashedValues[hashIndex].payload == it.payload) {
				currentMatches++;
				it.isMapped=true;
			}
		}
	}
	

	public long lookForMagic() {
		Random ran = new Random();	
		int free, bestScore=0;
		long hashKey, bestKey=0;
		
		long startTime = System.currentTimeMillis();
		long durationMinutes = 1;
        long duration = durationMinutes * 60 * 1000; // in milliseconds
		for(int attempt=0; ; ++attempt) {
			if(System.currentTimeMillis() - startTime > duration)
				break;
			
			reset();
			applyPrevMagics();
			
			hashKey=ran.nextLong() & ran.nextLong();
			applyMagic(hashKey);
			
			if(currentMatches > bestScore) {
				bestKey=hashKey;
				bestScore=currentMatches;
				free = NUM_SLOTS;
				for(HashedValue hv : hashedValues)
					if(hv.isUsed == true)
						free--;
				double successRate = (double)currentMatches/(double)inputItems.length;
				System.out.println("Try: "+ attempt +
						", matches: " + currentMatches +
						", itemsRemaining: "+ (NUM_ITEMS-currentMatches) +
						", slotsTaken: " + (NUM_SLOTS-free)+
						", slotsFree: " + free +
						", hashKey: 0x" + Long.toHexString(hashKey) +
						", success%: " + String.format("%.2f", successRate)
				);
				
			}
			
		}
		return bestKey;
	}
	
	void showUnmappedInputs() {
		SortedMap<Integer, Integer> sortedMap = new TreeMap<>();
		
		for(Item it : inputItems)
			if(it.isMapped)
				sortedMap.put(it.payload, sortedMap.getOrDefault(it.payload, 0));
			else
				sortedMap.put(it.payload, 1+sortedMap.getOrDefault(it.payload, 0));
		int total=0;
		for(int v : sortedMap.values())
			total+=v;
		
		System.out.println("input payload: "+sortedMap.size()+" distinct values with: "+ total + " unmapped items");
		System.out.println("input payload unmapped values frequency: " + sortedMap.values());
	}

	public static void main(String[] args) {
		
		MagicFinder mf= new MagicFinder(0x71c71400713100cl);//0x71c71400713100cl
		

		long bestMagic = mf.lookForMagic();
		System.out.println("Best magic found: " + Long.toHexString(bestMagic));
		mf.reset();
		mf.applyPrevMagics();
		mf.applyMagic(bestMagic);
		mf.showUnmappedInputs();
	}
}
