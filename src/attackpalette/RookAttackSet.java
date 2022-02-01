package attackpalette;

import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;

public class RookAttackSet {
	private int location;

	// for now: go with piece values of 1,3,3,5,9

	// PC=prevous commitment
	private long rookSet;
	private long rookRookSet;
	private long rookQueenSet;
	private long rookQueenRookSet;
	private long rookQueenQueenSet;
	private long rookRookQueenSet;

	// package private - used for testing.
	int getLocation() {
		return location;
	}

	long getRookSet() {
		return rookSet;
	}

	long getRookRookSet() {
		return rookRookSet;
	}

	long getRookQueenSet() {
		return rookQueenSet;
	}

	long getRookQueenRookSet() {
		return rookQueenRookSet;
	}

	long getRookQueenQueenSet() {
		return rookQueenQueenSet;
	}

	long getRookRookQueenSet() {
		return rookRookQueenSet;
	}

	public void populateAttacks(Board brd, int bi, int player) {
		location = bi;

		rookSet = BitboardGen.getRookSet(bi, brd.getOccupied());
		long attackedRooks = rookSet & brd.getPieces(player, PieceType.ROOK);
		rookRookSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedRooks) & ~rookSet;
		long attackedQueens = rookSet & brd.getPieces(player, PieceType.QUEEN);
		rookQueenSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedQueens) & ~rookSet;
		long attackedRooks2 = rookQueenSet & brd.getPieces(player, PieceType.ROOK);
		rookQueenRookSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedQueens & ~attackedRooks2) & ~rookSet & ~rookQueenSet;
		long attackedQueens2 = rookQueenSet & brd.getPieces(player, PieceType.QUEEN);
		rookQueenQueenSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedQueens & ~attackedQueens2) & ~rookSet & ~rookQueenSet;
		attackedQueens = rookRookSet & brd.getPieces(player, PieceType.QUEEN);
		rookRookQueenSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedRooks & ~attackedQueens) & ~rookSet & ~rookRookSet;

	}
}
