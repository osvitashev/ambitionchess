package attackpalette;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;

public class KingAttackSet {
	private int location;
	private long kingSet;
	
	//package private - used for testing.
	int getLocation() {
		return location;
	}

	long getKingSet() {
		return kingSet;
	}

	public void populateAttacks(Board brd, int bi, int player) {
		location = bi;
		kingSet = BitboardGen.getKingSet(Bitboard.getLowSquareIndex(brd.getPieces(player, PieceType.KING)));
	}
}
