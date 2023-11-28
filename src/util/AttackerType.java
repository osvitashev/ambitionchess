package util;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

/**
 * 
 * intentionally does not have Player as a stored value.
 * The intention is to use this in player-separate lists.
 * 
 * Null values sets sets the attacker to be NO_PIECE.
 * There is no easy way to set square to SQUARE_NONE because 64 is a powed of two...
 *
 */
public class AttackerType {
	/**
	 * PieceValue is set to no_piece.
	 * Unfortunately, there is no way to do the same with no-square.
	 * @return
	 */
	public static int nullValue() {
		int val = setBits(0, PieceType.NO_PIECE, 0, 3);
		return val;
	}
	
	public static int create(int pieceType, int sq) {
		assert PieceType.validate(pieceType);
		assert Square.validate(pieceType);
		int val = setBits(0, pieceType, 0, 3);
		return setBits(val, sq, 3, 6);
	}
	
	
	public static int getAttackerPieceType(int attacker) {
		return getBits(attacker, 0, 3);
	}
	
	public static int getAttackerSquareFrom(int attacker) {
		assert getAttackerPieceType(attacker) != PieceType.NO_PIECE;
		return getBits(attacker, 3, 6);
	}
	
}


