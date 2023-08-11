package codegenerators;

import java.util.ArrayList;
import java.util.Random;

public class MagicFinder {
	MagicFinder() {
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

	void lookForMagic(int mod256) {
		MyLookupGenerator myGenerator = new MyLookupGenerator();
		myGenerator.generateMatchUpCollection();

		Random random = new Random();
		long magic, bestMagic;
		int bestScore = 0, score, index = 0;

		int howMany = 0;
		for (int i = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).matchupKey % 256 == mod256)
				howMany++;
		payloadsAndKeys = new PayloadKey[howMany];
		for (int i = 0, j = 0; i < myGenerator.matchups.size(); ++i)
			if (myGenerator.matchups.get(i).matchupKey % 256 == mod256)
				payloadsAndKeys[j++] = new PayloadKey(myGenerator.matchups.get(i).whatIfMatrix, myGenerator.matchups.get(i).matchupKey);

		BITS_IN_INDEX = intLog2RoundUp(payloadsAndKeys.length) - 1;// trying to condense the table a bit....
		TABLE_SIZE = 1 << BITS_IN_INDEX;/// this is a parameter.
		System.out.println("Table containing this many elements: " + TABLE_SIZE + " we try to covert it with an index key of this width: "
				+ BITS_IN_INDEX + " bits. Representing a table with " + payloadsAndKeys.length + " elements");

		payloadValues = new int[TABLE_SIZE];
		isTaken = new boolean[TABLE_SIZE];

		ArrayList<Long> magicsOver80Percent = new ArrayList<>();

		for (int tryy = 0; tryy < 1000000000; ++tryy) {
			reset();
			magic = random.nextLong() & random.nextLong();
			score = payloadsAndKeys.length;
			for (PayloadKey pk : payloadsAndKeys) {
				index = getIndex(pk.key, magic);
				if (!isTaken[index]) {
					payloadValues[index] = pk.payload;
					isTaken[index] = true;
				} else if (isTaken[index] && payloadValues[index] == pk.payload) {
					// do nothing - we get the right value through a lucky key collision
				} else {
					score--;
					// break;
				}
			}
			if (score > bestScore) {
				System.out.println(
						tryy + ", " + score + ", " + magic + ", " + String.format("%.2f", ((double) score / (double) payloadsAndKeys.length)));
				bestScore = score;
			}
			if (((double) score / (double) payloadsAndKeys.length) >= 0.80) {
				magicsOver80Percent.add(magic);
				System.out.println("added candidate magic:" + magic);
			}
				
			if (magicsOver80Percent.size() == 50)
				break;
		}
		System.out.println("Got 100 candidate magics with score of 80+: " + magicsOver80Percent.toString());

		int v1, v2, v3;// payloads extracted with one the the three magic numbers.
		for (int i = 0; i < magicsOver80Percent.size(); i++)
			for (int j = 0; j < magicsOver80Percent.size(); j++)
				for (int k = 0; k < magicsOver80Percent.size(); k++) {
					score = payloadsAndKeys.length;
					for (PayloadKey pk : payloadsAndKeys) {
						v1 = payloadValues[getIndex(pk.key, magicsOver80Percent.get(i))];
						v2 = payloadValues[getIndex(pk.key, magicsOver80Percent.get(j))];
						v3 = payloadValues[getIndex(pk.key, magicsOver80Percent.get(k))];

						if (v1 == pk.payload && v2 == pk.payload || v1 == pk.payload && v3 == pk.payload || v2 == pk.payload && v3 == pk.payload) {
							// do nothing - we got the correct value by voting 2/3!
						} else {
							score--;
						}
					}
					if (score > bestScore) {
						System.out.println(magicsOver80Percent.get(i) + "," + magicsOver80Percent.get(j) + "," + magicsOver80Percent.get(k) + ","
								+ score + ", " + String.format("%.2f", ((double) score / (double) payloadsAndKeys.length)));
						bestScore = score;
					}
				}

	}

	public static void main(String[] args) {
		MagicFinder m = new MagicFinder();
		m.lookForMagic(44);

	}
}
