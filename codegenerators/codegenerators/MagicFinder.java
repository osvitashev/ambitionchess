package codegenerators;

import java.util.ArrayList;
import java.util.Random;

public class MagicFinder {
	MagicFinder(int mod256) {
		MagicFinder.mod256=mod256;
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		myGenerator.generateMatchUpCollection();
		int howMany = 0;
		for (int i = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).to256Index() == mod256)
				howMany++;
		inputsPayloadsAndKeys = new PayloadKey[howMany];
		for (int i = 0, j = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).to256Index() == mod256)
				inputsPayloadsAndKeys[j++] = new PayloadKey(myGenerator.matchups.get(i).whatIfMatrix, myGenerator.matchups.get(i).matchupKey);

		BITS_IN_INDEX = intLog2RoundUp(inputsPayloadsAndKeys.length) - 1;// trying to condense the table a bit....
		TABLE_SIZE = 1 << BITS_IN_INDEX;/// this is a parameter.
		System.out.println("Table containing this many elements: " + TABLE_SIZE + " we try to covert it with an index key of this width: "
				+ BITS_IN_INDEX + " bits. Representing a table with " + inputsPayloadsAndKeys.length + " elements. Need to afhieve density of "+
				String.format("%.2f", (double)inputsPayloadsAndKeys.length/(double)TABLE_SIZE));

		hashTable = new int[TABLE_SIZE];
		isTaken_currentIteration = new boolean[TABLE_SIZE];
		isTaken_prevIteration = new boolean[TABLE_SIZE];
	}

	class PayloadKey {
		int payload;
		long key;

		public PayloadKey(int p, long k) {
			payload = p;
			key = k;
		}
	}

	static int hashTable[];
	static boolean isTaken_currentIteration[];
	static boolean isTaken_prevIteration[];
	static PayloadKey inputsPayloadsAndKeys[];
	static int TABLE_SIZE;
	static int BITS_IN_INDEX;
	static int mod256;

//	static void reset() {
//		for (int i = 0; i < isTaken.length; ++i)
//			isTaken[i] = false;
//	}
	

	static int getIndex(long key, long magic) {
		long t = key * magic;
		t = t >>> (64 - BITS_IN_INDEX);
		return (int) t;
	}

	static int intLog2RoundUp(int arg) {
		return (int) Math.ceil(Math.log(arg) / Math.log(2));
	}
	

	void lookForMagic(long prevMagic) {
		Random random = new Random();
		long magic;
		double mostOptimalScore=0;
		int numTotalCorrectValues, numPrevCorrectValues=0, constructiveCollisions, index = 0;
		ArrayList<Long> missingKeys = new ArrayList<>();
		if(prevMagic !=0) {
			for (PayloadKey pk : inputsPayloadsAndKeys) {
				index = getIndex(pk.key, prevMagic);
					hashTable[index] = pk.payload;
			}
			numPrevCorrectValues=0;
			for (PayloadKey pk : inputsPayloadsAndKeys){
				index = getIndex(pk.key, prevMagic);
				if(hashTable[index] == pk.payload) {
					numPrevCorrectValues++;
					isTaken_prevIteration[index]=true;
				}
				else
					missingKeys.add(pk.key);
			}
			/**
			 * It is NOT that there are any incorrect values in the hashTable at this point,
			 * There are missing values!!!!
			 * How to detect pk.key which got skipped?
			 * 
			 * >>> put it into a separate collection!
			 */
		}
		System.out.println("There are this many missing keys: "+ missingKeys.size());
		System.out.println("The table already containst this many correct elements from previous magic search: "+ numPrevCorrectValues);
		for (int tryy = 0; tryy < 1000000000; ++tryy) {//1000000000
			//reset
			for (int i = 0; i < isTaken_currentIteration.length; ++i)
				isTaken_currentIteration[i] = false;
			for (int i = 0; i < hashTable.length; ++i)//need a reset, to avoid getting the benefit from the previous random magic.
				hashTable[i]=0;
			if(prevMagic!=0)
				for (PayloadKey pk : inputsPayloadsAndKeys) {
					index = getIndex(pk.key, prevMagic);
						hashTable[index] = pk.payload;
				}
			
			
			magic = random.nextLong() & random.nextLong();
			constructiveCollisions=0;
			numTotalCorrectValues = numPrevCorrectValues;
			//calculate
			for (PayloadKey pk : inputsPayloadsAndKeys) 
				if(prevMagic==0 )
				{
					index = getIndex(pk.key, magic);
						hashTable[index] = pk.payload;
				}
			//reflect
			for (PayloadKey pk : inputsPayloadsAndKeys)
				if(prevMagic==0)
				{
					index = getIndex(pk.key, magic);
					if(hashTable[index] == pk.payload) {
						if(isTaken_currentIteration[index] == true)
							constructiveCollisions++;
						isTaken_currentIteration[index] = true;
						numTotalCorrectValues++;
					}
				}
			//evaluate
			{
				int free = 0;
				for(int i=0; i<TABLE_SIZE;++i) {
					if (!isTaken_currentIteration[i] && !isTaken_prevIteration[i]) {
		                free++;
		            }
				}
				
		        double completeness = ((double) numTotalCorrectValues / (double) inputsPayloadsAndKeys.length);
				double tableUtilization=1.0-((double) free / (double) TABLE_SIZE);
				double thisPassDensity = (double)(numTotalCorrectValues)/(double)(TABLE_SIZE-free);
				double neededNextPassDensity = (double)(inputsPayloadsAndKeys.length-numTotalCorrectValues)/(double)(free);
				int outstanding = (inputsPayloadsAndKeys.length-numTotalCorrectValues);
				if(completeness>mostOptimalScore)
				{
					mostOptimalScore=completeness;
					System.out.println("Try:" + tryy + ", correct:" + numTotalCorrectValues + 
							", constructive: " + constructiveCollisions + 
							", freeSlots: " + free + 
							", outstanding: " + outstanding +
							", magic: " + magic + 
							", comp: " + String.format("%.2f", completeness)+ 
							", util: "  + String.format("%.2f", tableUtilization)+
							", this.density: "+ String.format("%.2f", thisPassDensity)+
							", next.density**: "+ String.format("%.2f", neededNextPassDensity));
				}
				
			}
		}
	}
	

	public static void main(String[] args) {
		MagicFinder m = new MagicFinder(96);
		m.lookForMagic(0);
	}
}
