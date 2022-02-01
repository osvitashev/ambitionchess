package attackpalette;

import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;

public class QueenAttackSet {
	// TODO: when i get to covering capture ordering, need to make sure this
	// 5Q2/8/8/k1n5/5R2/5n2/5Q2/7K w - - 0 1

	private int location;

	private long queenSet;
	private long queenPawnSet;
	private long queenBishopSet;
	private long queenBishopPawnSet;
	private long queenQueenSet;
	private long queenQueenPawnSet;
	private long queenQueenBishopSet;
	private long queenBishopQueenSet;
	private long queenRookSet;
	private long queenQueenRookSet;
	private long queenRookQueenSet;

	// package private for testing.
	int getLocation() {
		return location;
	}

	long getQueenSet() {
		return queenSet;
	}

	long getQueenPawnSet() {
		return queenPawnSet;
	}

	long getQueenBishopSet() {
		return queenBishopSet;
	}

	long getQueenBishopPawnSet() {
		return queenBishopPawnSet;
	}

	long getQueenQueenSet() {
		return queenQueenSet;
	}

	long getQueenQueenPawnSet() {
		return queenQueenPawnSet;
	}

	long getQueenQueenBishopSet() {
		return queenQueenBishopSet;
	}

	long getQueenBishopQueenSet() {
		return queenBishopQueenSet;
	}

	long getQueenRookSet() {
		return queenRookSet;
	}

	long getQueenQueenRookSet() {
		return queenQueenRookSet;
	}

	long getQueenRookQueenSet() {
		return queenRookQueenSet;
	}

	public void populateAttacks(Board brd, int bi, int player) {
		location = bi;

		long bishopSet = BitboardGen.getBishopSet(bi, brd.getOccupied());
		long rookSet = BitboardGen.getRookSet(bi, brd.getOccupied());
		queenSet = bishopSet | rookSet;
		queenPawnSet = BishopAttackSet.augmentWithPawnAttacks(brd, player, bishopSet, bi);
		long attackedBishops = bishopSet & brd.getPieces(player, PieceType.BISHOP);
		queenBishopSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedBishops) & ~queenSet;
		queenBishopPawnSet = BishopAttackSet.augmentWithPawnAttacks(brd, player, queenBishopSet, bi) & ~queenSet;
		long attackedQueens2 = queenBishopSet & brd.getPieces(player, PieceType.QUEEN);
		queenBishopQueenSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedBishops & ~attackedQueens2) & ~queenSet & ~queenBishopSet;
		long attackedRooks = rookSet & brd.getPieces(player, PieceType.ROOK);
		queenRookSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedRooks) & ~queenSet;
		attackedQueens2 = queenRookSet & brd.getPieces(player, PieceType.QUEEN);
		queenRookQueenSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedRooks & ~attackedQueens2) & ~queenSet & ~queenRookSet;
		// treating blocker queen as bishop for now...
		long attackedQueens = bishopSet & brd.getPieces(player, PieceType.QUEEN);
		queenQueenSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedQueens) & ~queenSet;
		queenQueenPawnSet = BishopAttackSet.augmentWithPawnAttacks(brd, player, queenQueenSet, bi) & ~queenSet;
		long attackedBishops2 = queenQueenSet & brd.getPieces(player, PieceType.BISHOP);
		queenQueenBishopSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedQueens & ~attackedBishops2) & ~queenSet & ~queenQueenSet;
		attackedQueens = rookSet & brd.getPieces(player, PieceType.QUEEN);
		long temp = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedQueens) & ~queenSet;// = QQ as rook
		long attackedRooks2 = temp & brd.getPieces(player, PieceType.ROOK);
		queenQueenRookSet = BitboardGen.getRookSet(bi, brd.getOccupied() & ~attackedQueens & ~attackedRooks2) & ~queenSet & ~temp;
		queenQueenSet = queenQueenSet | temp;
	}
}
