package puzzler;

import static gamestate.BitField32.*;

/**
 * Contains information needed for search to mate.
 * 

 * 
 //@formatter:off
 
 Functional requirement for comparison:
 
 BLACK_WIN(5) < BLACK_WIN(10)< WHITE_WIN(10) < WHITE_WIN(5)
 
  	memory packing: 
	start	length	name
	0		8		count-to-termination
	8		2		outcome type

	
 	
 //@formatter:on
 */

public class GameOutcome {

	public class OutcomeType {
		public static final int WHITE_WIN = 0;
		public static final int DRAW = 1;
		public static final int BLACK_WIN = 2;
	}
	
	public static int createWhiteWin(int n) {
		return setType(setDistance(0, n), OutcomeType.WHITE_WIN);
	}

	public static int getDistance(int go) {
		return getBits(go, 0, 8);
	}

	public static int setDistance(int go, int val) {
		return setBits(go, val, 0, 8);
	}

	public static int getType(int go) {
		return getBits(go, 8, 2);
	}

	public static int setType(int go, int val) {
		return setBits(go, val, 8, 2);
	}

}
