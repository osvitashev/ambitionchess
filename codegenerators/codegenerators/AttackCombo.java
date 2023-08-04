package codegenerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import gamestate.GlobalConstants.PieceType;

class AttackCombo implements Serializable {
	// list of AttackSet.AttackSetType
	ArrayList<Integer> attackers = new ArrayList<Integer>();
	ArrayList<Integer> attackersThroughEnemyPawn = new ArrayList<Integer>();
	int serializedLongKey = 0;

	/**
	 * Having two collections traverse in opposite directions is weird, but it is
	 * needed to get around: 1) Code for a pawn is 0 and this it cannot be
	 * represented at the beginning of the serialized value. 2) The serialized value
	 * can hold exactly 10 3-bit values. 11th causes an overflow with 33 bits.
	 * 
	 * An alternative would be to initialize the variable to 1 instead of 0. This is
	 * not quite NO_PIECE code, but there should be no case requiring
	 * de-serialization. We are just trying to give unique integer representation to
	 * attacks array.
	 */
	void setSerializedLongKey() {
		for (int a : attackersThroughEnemyPawn) {
			serializedLongKey <<= 3;// 3 bits
			serializedLongKey |= a;
		}
		if (attackers.isEmpty() || attackers.get(attackers.size() - 1) != PieceType.KING) {
			serializedLongKey <<= 3;// 3 bits
			serializedLongKey |= PieceType.NO_PIECE;
		}
		for (int i = attackers.size() - 1; i >= 0; --i) {
			serializedLongKey <<= 3;// 3 bits
			serializedLongKey |= attackers.get(i);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AttackCombo other = (AttackCombo) o;
		return attackers.equals(other.attackers) && attackersThroughEnemyPawn.equals(other.attackersThroughEnemyPawn);
		//return toCompressedAttackString().equals(other.toCompressedAttackString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(attackers, attackersThroughEnemyPawn);
	}

	public static String attackerListToString(ArrayList<Integer> attackers) {
		String ret="";
		for(Integer i : attackers)
			ret += PieceType.toString(i);
		return ret;
	}
	
	
	@Override
	public String toString() {
		String ret = "long=[" + attackerListToString(attackers) + "|"
				+ attackerListToString(attackersThroughEnemyPawn) + "] short=["+ toCompressedAttackString()
				+ "] serialized= " + Integer.toOctalString(serializedLongKey);
		return ret;
	}
	
	/**
	 * example: PMMRRQK|QM
	 */
	public String toCompressedAttackString() {
		
		String ret="";
		for(Integer i : attackers)
			ret += PieceType.toString(i);
		ret+="|";
		for(Integer i : attackersThroughEnemyPawn)
			ret += PieceType.toString(i);
		ret=ret.replace('N', 'M');
		ret=ret.replace('B', 'M');
		return ret;
	}
	
	public static int pieceCost(int pt) {
		assert PieceType.validate(pt);
		switch (pt) {
		case PieceType.PAWN:
			return 100;
		case PieceType.KNIGHT:
			return 300;
		case PieceType.BISHOP:
			return 300;
		case PieceType.ROOK:
			return 500;
		case PieceType.QUEEN:
			return 1000;
		case PieceType.KING:
			return 1000000;
		}
		throw new RuntimeException("Unexpected value!");
	}
}