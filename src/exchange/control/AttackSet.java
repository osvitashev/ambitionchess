package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

//TODO: remove the class and replace it with two parallel arrays
/**
 * Contains two data fields: Attacks - a bitmap;
 * data - derialized information including origin square, piece type, attack set type and sunken cost needed for sorting.
 * 
 *
 */
public class AttackSet implements Comparable<AttackSet>{
	static class AttackSetType{
		public static boolean validate(int pst) {
			return pst>=PAWN_ATTACK && pst<=QUEEN_THROUGH_ENEMY_PAWN_PUSH;
		}
		public static int PAWN_ATTACK=0;
		public static int PAWN_PUSH=1;
		public static int KNIGHT=2;
		public static int BISH=3;
		public static int ROOK=4;
		public static int QUEEN=5;
		public static int KING=6;
		public static int BISH_THROUGH_ENEMY_PAWN_ATTACK=7;
		public static int ROOK_THROUGH_ENEMY_PAWN_PUSH=8;
		public static int QUEEN_THROUGH_ENEMY_PAWN_ATTACK=9;
		public static int QUEEN_THROUGH_ENEMY_PAWN_PUSH=10;
		
		public static String toString(int ast) {
			assert AttackSetType.validate(ast);
			String ret = "";
			if (ast == PAWN_ATTACK)
				ret = "PAWN_ATTACK";
			else if (ast == PAWN_PUSH)
				ret = "PAWN_PUSH";
			else if (ast == KNIGHT)
				ret = "KNIGHT";
			else if (ast == BISH)
				ret = "BISH";
			else if (ast == ROOK)
				ret = "ROOK";
			else if (ast == QUEEN)
				ret = "QUEEN";
			else if (ast == KING)
				ret = "KING";
			else if (ast == BISH_THROUGH_ENEMY_PAWN_ATTACK)
				ret = "BISH_THROUGH_ENEMY_PAWN_ATTACK";
			else if (ast == ROOK_THROUGH_ENEMY_PAWN_PUSH)
				ret = "ROOK_THROUGH_ENEMY_PAWN_PUSH";
			else if (ast == QUEEN_THROUGH_ENEMY_PAWN_ATTACK)
				ret = "QUEEN_THROUGH_ENEMY_PAWN_ATTACK";
			else if (ast == QUEEN_THROUGH_ENEMY_PAWN_PUSH)
				ret = "QUEEN_THROUGH_ENEMY_PAWN_PUSH";
			return ret;
		}
	}
	
	@Override
    public int compareTo(AttackSet as) {
        return -1;//this.getPieceType()-as.getPieceType();
    }
	
	private int data;
	private long attacks;
	
	AttackSet(long attacks) {
		this.attacks = attacks;
	}
	


	
	public String toString() {
		String ret = "{";
//		ret += PieceType.toString(getPieceType());
//		if(PieceType.PAWN != getPieceType())
//			ret += " " + Square.toString(getSquare());
		ret+= " -> 0x" + Long.toHexString(attacks);
		ret+="}";
		return ret;
	}
	
	long getAttacks() {
		return attacks;
	}
}