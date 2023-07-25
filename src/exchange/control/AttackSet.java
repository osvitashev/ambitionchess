package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

//TODO: remove the class and replace it with two parallel arrays
/**
 * Contains two data fields: Attacks - a bitmap;
 * data - derialized information including origin square, piece type, attack set type and sunken cost needed for sorting.
 * 
 *
 */
class AttackSet implements Comparable<AttackSet>{
	@Override
    public int compareTo(AttackSet as) {
        return this.getPieceType()-as.getPieceType();
    }
	
	private int data;
	private long attacks;
	
	AttackSet(long attacks) {
		this.attacks = attacks;
	}
	
	/**
	  	memory packing: 
		start	length	name
		0		3		PieceType - is set to PAWN for pawn attacks and pushes
		3		6		Square origin - is not meaningful for pawns
		9
	 */

	int getPieceType() {
		return getBits(data, 0, 3);
	}

	void setPieceType(int pt) {
		data=setBits(data, pt, 0, 3);
	}
	
	int getSquare() {
		return getBits(data, 3, 6);
	}

	void setSquare(int sq) {
		data=setBits(data, sq, 3, 6);
	}
	
	public String toString() {
		String ret = "{";
		ret += PieceType.toString(getPieceType());
		if(PieceType.PAWN != getPieceType())
			ret += " " + Square.toString(getSquare());
		ret+= " -> 0x" + Long.toHexString(attacks);
		ret+="}";
		return ret;
	}
	
	long getAttacks() {
		return attacks;
	}
}