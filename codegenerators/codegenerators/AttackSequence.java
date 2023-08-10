package codegenerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

class AttackSequence implements Serializable {
	// list of AttackSet.AttackSetType
	ArrayList<Character> unconditionalAttackers = new ArrayList<Character>();
	ArrayList<Character> attackersThroughEnemyPawn = new ArrayList<Character>();
	int serializedIntKey = 0;

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
	
	static int getSerializedAttacker(char c) {
		if(c == 'P')
			return 1;
		if(c == 'N' ||c == 'B' ||c == 'M')
			return 2;
		if(c == 'R')
			return 3;
		if(c == 'Q')
			return 4;
		if(c == 'K')
			return 5;
		throw new RuntimeException("Boo: " + c);
	}
	
	void setSerializedIntKey() {
		//todo: use multiplication instead of bit shift for more condensed encoding.
		
		
		for (char c : attackersThroughEnemyPawn) {
			serializedIntKey *=6;
			serializedIntKey += getSerializedAttacker(c);
		}
		if (unconditionalAttackers.isEmpty() || unconditionalAttackers.get(unconditionalAttackers.size() - 1) != 'K') {
			serializedIntKey *=6;
			//insert blank
		}
		for (int i = unconditionalAttackers.size() - 1; i >= 0; --i) {
			serializedIntKey *=6;
			serializedIntKey += getSerializedAttacker(unconditionalAttackers.get(i));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AttackSequence other = (AttackSequence) o;
		return unconditionalAttackers.equals(other.unconditionalAttackers) && attackersThroughEnemyPawn.equals(other.attackersThroughEnemyPawn);
		//return toCompressedAttackString().equals(other.toCompressedAttackString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(unconditionalAttackers, attackersThroughEnemyPawn);
	}

	public static String attackerListToString(ArrayList<Character> attackers) {
		String ret="";
		for(Character c : attackers)
			ret += c;
		return ret;
	}
	
	
	@Override
	public String toString() {
		String ret = "long=[" + attackerListToString(unconditionalAttackers) + "|"
				+ attackerListToString(attackersThroughEnemyPawn) + "] short=["+ toCompressedAttackString()
				+ "] serialized= " + Integer.toOctalString(serializedIntKey);
		return ret;
	}
	
	/**
	 * example: PMMRRQK|QM
	 */
	public String toCompressedAttackString() {
		
		String ret="";
		for(Character c : unconditionalAttackers)
			ret += c;
		ret+="|";
		for(Character c : attackersThroughEnemyPawn)
			ret += c;
		ret=ret.replace('N', 'M');
		ret=ret.replace('B', 'M');
		return ret;
	}
	
	public static int pieceCost(Character pt) {
		switch (pt) {
		case 'P':
			return 100;
		case 'N':
			return 300;
		case 'B':
			return 300;
		case 'M':
			return 300;
		case 'R':
			return 500;
		case 'Q':
			return 1000;
		case 'K':
			return 1000000;
		}
		throw new RuntimeException("Unexpected value!");
	}
	
	static boolean isLesserAttacker(char a, char b) {
		if(a=='P') {
			return b!='P';
		}
		else if(a=='N' || a=='B' || a=='M') {
			return b=='R' || b=='Q' || b=='K';
		}
		else if(a=='R') {
			return b=='Q' || b=='K';
		}
		else if(a=='Q') {
			return b=='K';
		}
		return false;
	}
}