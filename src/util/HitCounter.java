package util;

import java.util.HashMap;

/**
 * Used for diagnostic analysis. Should not be used with multi-threading!!!
 * 
 *
 */

@Deprecated
public class HitCounter {
	private static HashMap<String, Long> hitCounts = new HashMap<String, Long>();
	// OPTIMIZE: Split this into two: hashmap leading to an integer key which is an
	// index of the array of primitive ints containing the actual hit count.

	public static void clear() {
		hitCounts.clear();
	}

	public static void count(String key) {
		hitCounts.put(key, hitCounts.getOrDefault(key, 0L) + 1);
	}

	public static long get(String key) {
		return hitCounts.getOrDefault(key, 0L);
	}

	public static String dump() {
		String ret = "";
		for (String key : hitCounts.keySet()) {
			ret += key + ": " + get(key) + "\n";
		}
		return ret;
	}
}
