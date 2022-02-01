package attackpalette;

import gamestate.Bitboard;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

public class PawnsAttackSet {
	private long pawnEast, pawnWest;

	// package private - used for testing.
	long getPawnEast() {
		return pawnEast;
	}

	long getPawnWest() {
		return pawnWest;
	}

	public void populateAttacks(Board brd, int player) {
		long pawns = brd.getPieces(player, PieceType.PAWN);
		if (player == Player.WHITE) {
			pawnEast = Bitboard.shiftEast(Bitboard.shiftNorth(pawns));
			pawnWest = Bitboard.shiftWest(Bitboard.shiftNorth(pawns));
		} else {
			pawnEast = Bitboard.shiftEast(Bitboard.shiftSouth(pawns));
			pawnWest = Bitboard.shiftWest(Bitboard.shiftSouth(pawns));
		}
	}
}
