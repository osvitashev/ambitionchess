package attackpalette;

import gamestate.BitboardGen;
import gamestate.Board;

public class KnightAttackSet {
	private int location;
	private long knightSet;
	
	//package private - used for testing.
	int getLocation() {
		return location;
	}

	long getKnightSet() {
		return knightSet;
	}

	public void populateAttacks(Board brd, int bi, int player) {
		location = bi;
		knightSet = BitboardGen.getKnightSet(bi);
	}
}
