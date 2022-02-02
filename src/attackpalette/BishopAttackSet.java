package attackpalette;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

public class BishopAttackSet {
	private int location;
	
	private long bishopSet;
	private long bishopPawnSet;
	private long bishopQueenSet;
	private long bishopQueenPawnSet;
	private long bishopQueenQueenSet;
	
	//package private: currently used for testing only
	int getLocation() {
		return location;
	}
	long getBishopSet() {
		return bishopSet;
	}
	long getBishopPawnSet() {
		return bishopPawnSet;
	}
	long getBishopQueenSet() {
		return bishopQueenSet;
	}
	long getBishopQueenPawnSet() {
		return bishopQueenPawnSet;
	}
	long getBishopQueenQueenSet() {
		return bishopQueenQueenSet;
	}
	
	
	static long augmentWithPawnAttacks(Board brd, int player, long currentAttackSet, int sqOrigin) {
		long attackedPawns = currentAttackSet & brd.getPieces(player, PieceType.PAWN);
		long attackedPawnAttackSet;
		{// pawns
			if (player == Player.WHITE)
				attackedPawnAttackSet = Bitboard.shiftEast(Bitboard.shiftNorth(attackedPawns)) | Bitboard.shiftWest(Bitboard.shiftNorth(attackedPawns));
			else
				attackedPawnAttackSet = Bitboard.shiftEast(Bitboard.shiftSouth(attackedPawns)) | Bitboard.shiftWest(Bitboard.shiftSouth(attackedPawns));
		}
		long newAttackSet = BitboardGen.getBishopSetEmptyBoard(sqOrigin) & attackedPawnAttackSet & ~currentAttackSet;
		return newAttackSet;
	}
	
	public void populateAttacks(Board brd, int bi, int player) {
		location = bi;
		bishopSet = BitboardGen.getBishopSet(bi, brd.getOccupied());
		bishopPawnSet = augmentWithPawnAttacks(brd, player, bishopSet, bi);
		long attackedQueens = bishopSet & brd.getPieces(player, PieceType.QUEEN);
		bishopQueenSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedQueens) & ~bishopSet;
		bishopQueenPawnSet = augmentWithPawnAttacks(brd, player, bishopQueenSet, bi) & ~bishopSet;
		long attackedQueens2 = bishopQueenSet & brd.getPieces(player, PieceType.QUEEN);
		bishopQueenQueenSet = BitboardGen.getBishopSet(bi, brd.getOccupied() & ~attackedQueens & ~attackedQueens2) & ~bishopSet & ~bishopQueenSet;
	}
}
