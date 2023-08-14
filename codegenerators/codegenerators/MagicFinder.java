package codegenerators;

import java.util.ArrayList;
import java.util.Random;

public class MagicFinder {
	MagicFinder(int mod256) {
		this.mod256=mod256;
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		myGenerator.generateMatchUpCollection();
		int howMany = 0;
		for (int i = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).to256Index() == mod256)
				howMany++;
		payloadsAndKeys = new PayloadKey[howMany];
		for (int i = 0, j = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).to256Index() == mod256)
				payloadsAndKeys[j++] = new PayloadKey(myGenerator.matchups.get(i).whatIfMatrix, myGenerator.matchups.get(i).matchupKey);

		BITS_IN_INDEX = intLog2RoundUp(payloadsAndKeys.length) - 1;// trying to condense the table a bit....
		TABLE_SIZE = 1 << BITS_IN_INDEX;/// this is a parameter.
		System.out.println("Table containing this many elements: " + TABLE_SIZE + " we try to covert it with an index key of this width: "
				+ BITS_IN_INDEX + " bits. Representing a table with " + payloadsAndKeys.length + " elements");

		payloadValues = new int[TABLE_SIZE];
		isTaken = new boolean[TABLE_SIZE];
	}

	class PayloadKey {
		int payload;
		long key;

		public PayloadKey(int p, long k) {
			payload = p;
			key = k;
		}
	}

	static int payloadValues[];
	static boolean isTaken[];
	static PayloadKey payloadsAndKeys[];
	static int TABLE_SIZE;
	static int BITS_IN_INDEX;
	static int mod256;

	static void reset() {
		for (int i = 0; i < isTaken.length; ++i)
			isTaken[i] = false;
	}

	static int getIndex(long key, long magic) {
		long t = key * magic;
		t = t >>> (64 - BITS_IN_INDEX);
		return (int) t;
	}

	static int intLog2RoundUp(int arg) {
		return (int) Math.ceil(Math.log(arg) / Math.log(2));
	}
	

	void lookForMagic() {
		Random random = new Random();
		long magic, bestMagic;
		int bestScore = 0, score, constructiveCollisions, index = 0;
		ArrayList<Long> magicsOver80Percent = new ArrayList<>();
		for (int tryy = 0; tryy < 1000000000; ++tryy) {
			reset();
			magic = random.nextLong() & random.nextLong();
			score = payloadsAndKeys.length;
			constructiveCollisions=0;
			for (PayloadKey pk : payloadsAndKeys) {
				index = getIndex(pk.key, magic);
				if (!isTaken[index]) {
					payloadValues[index] = pk.payload;
					isTaken[index] = true;
				} else if (isTaken[index] && payloadValues[index] == pk.payload) {
					constructiveCollisions++;
					// do nothing - we get the right value through a lucky key collision
				} else {
					score--;
					// break;
				}
			}
			if (score > bestScore) {
				int free = 0;
		        for (boolean taken : isTaken) {
		            if (!taken) {
		                free++;
		            }
		        }
				System.out.println("Try:" + tryy + ", score:" + score + ", constructiveCollisions: " + constructiveCollisions + ", freeSlots: " + free
						+ ", magic: " + magic + ", unitization: " + String.format("%.2f", ((double) score / (double) payloadsAndKeys.length)));
				bestScore = score;
			}
			if (((double) score / (double) payloadsAndKeys.length) >= 0.80) {
				magicsOver80Percent.add(magic);
				System.out.println("added candidate magic:" + magic);
			}
		}
		System.out.println("Got 100 candidate magics with score of 80+: " + magicsOver80Percent.toString());
	}
	
	void augmentMagic(long startingMagic) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		myGenerator.generateMatchUpCollection();

		
		int score, index;
		score = payloadsAndKeys.length;
		for (PayloadKey pk : payloadsAndKeys) {
			index = getIndex(pk.key, startingMagic);
			payloadValues[index] = pk.payload;
		}
		for (PayloadKey pk : payloadsAndKeys) {
			index = getIndex(pk.key, startingMagic);
			if(payloadValues[index] != pk.payload)
				score--;
		}
		System.out.println("Actual utilization of the magic:");
		System.out.println(score + ", " + startingMagic + ", " + String.format("%.2f", ((double) score / (double) payloadsAndKeys.length)));

		
	}
	

	public static void main(String[] args) {
		MagicFinder m = new MagicFinder(156);
		m.lookForMagic();
		//m.augmentMagic(157635336890527504l);
	}
}
