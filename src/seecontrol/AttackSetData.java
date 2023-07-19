package seecontrol;

import static util.BitField32.getBits;
import static util.BitField32.getBoolean;
import static util.BitField32.setBits;
import static util.BitField32.setBoolean;

import gamestate.DebugLibrary;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/**
 * Used for static exchange evaluation. Contains data regarding a given attack bit board.
 * 
 * If i am going to include extended data here such as the piece type and origin square, that would create a problem:
 * ideally i want to have only two bitboards to represent all pawn captures: left capture and right capture. In other words, pawns would not have an origin square.
 * 
 * Having piece/origin info might be very useful for detection condition like overextension and overprotection.
 * The pproach is to have an attack bitboard for every piece (except pawns)
 * and additionally two boards for left and right pawn attacks and one for pawn pushes.
 * 
 * ***for mixed-player batteries, we are only supporting one color change.
 * ***evaluation is greedy: we are assuming that evaluation is short-sighted and does not have a lokahead. The least valuable attacker is used first;
 * 		even if this is detrimental to the exchange overall.
 * 
 //@formatter:off
  * 
  	memory packing: 
	start	length	name
	0		2		AttackSetType
	2		3		PieceType - is set to PAWN for pawn attacks and pushes
	5		6		Square origin - is not meaningful for pawn attacks and pushes
	11		1		bool - isBatteryWithPawnPush - set for queen/rook attacking on a vertical line. Needed for correct exchange evaluation.
	12		8		int sunken cost	- cumulative cost of previous sliding pieces in the battery. This can be a value in the range of [0, 255]. For direct attacks it is 0.
	20		8		int opponent sunken cost - cumulative cost of previous sliding pieces of the opposite color in the battery. Is blank most of the time...
	28		1		bool - player flag
	
	***It might be pretty straight forward to extend this to cover batteries with exactly one color change: xpQB or xxQrr;
	* Cases with more than one color change are too rare to consider...
	* if we add 'opponentSunkenCost', then we can also handle cases such as xpqB and xxQRr.
 	
 //@formatter:on
 */

//TODO: isBatteryWithPawnPush is redundant: was supposed to be used in order to dintinguish between queen-pawn attack and queen-pawn push, but that is evident from the target square occupancy

public class AttackSetData {
	public static final class AttackSetType {
		public static final int DIRECT = 0; // sliding pieces, knight, king
		public static final int INDIRECT = 1; //used for sliding piece batteries
		public static final int PAWN_ATTACK = 2;
		public static final int PAWN_PUSH = 3;
		
		public static String toString(int asType) {
			switch(asType) {
			case DIRECT:
				return "DIRECT";
			case INDIRECT:
				return "INDIRECT";
			case PAWN_ATTACK:
				return "PAWN_ATTACK";
			}
			return "PAWN_PUSH";
		}
	}
	
	public static String toString(int asData) {
		String ret = "";
		ret += AttackSetType.toString(AttackSetData.getAttackSetType(asData)) + " ";
		if(Player.isWhite(AttackSetData.getPlayer(asData)))
			ret += "w";
		else
			ret += "b";
		ret += PieceType.toString(AttackSetData.getPieceType(asData)) + " ";
		ret += Square.toString(AttackSetData.getSquare(asData)) + " ";
		
		int temp = AttackSetData.getSunkenCost(asData);
		if(temp != 0)
			ret+="getSunkenCost = " + Integer.toString(temp) + " ";
		temp = AttackSetData.getOpponentSunkenCost(asData);
		if(temp != 0)
			ret+="getOpponentSunkenCost = " + Integer.toString(temp) + " ";
		if(AttackSetData.getIsBatteryWithPawnPush(asData))
			ret += "isBatteryWithPawnPush";
		return ret;
	}
	
	//only used for ordering. Does not represent exchange evaluation cost!
	public static final class OrderingPieceWeights {
		private static final int[] COSTS = { 1, 3, 3, 5, 10};
		
		/**
		 * Only used for ordering. Does not represent exchange evaluation cost!
		 * Notice that King does not have an assigned value!!!!
		 * @param pieceType
		 * @return
		 */
		public static int getValue(int pieceType) {
			assert PieceType.validate(pieceType);
			return COSTS[pieceType];
		}
	}
	
	public static int getAttackSetType(int asData) {
		return getBits(asData, 0, 2);
	}

	public static int setAttackSetType(int asData, int type) {
		//TODO: add argument validation
		return setBits(asData, type, 0, 2);
	}
	
	public static int getPieceType(int asData) {
		return getBits(asData, 2, 3);
	}

	public static int setPieceType(int asData, int type) {
		assert PieceType.validate(type);
		return setBits(asData, type, 2, 3);
	}
	
	public static int getSquare(int asData) {
		return getBits(asData, 5, 6);
	}

	public static int setSquare(int asData, int val) {
		assert Square.validate(val);
		return setBits(asData, val, 5, 6);
	}
	
	public static boolean getIsBatteryWithPawnPush(int asData) {
		return getBoolean(asData, 11);
	}

	public static int setIsBatteryWithPawnPush(int asData, boolean value) {
		return setBoolean(asData, value, 11);
	}
	
	public static int getSunkenCost(int asData) {
		return getBits(asData, 12, 8);
	}

	public static int setSunkenCost(int asData, int val) {
		// TODO: add parameter validation
		return setBits(asData, val, 12, 8);
	}
	
	public static int getOpponentSunkenCost(int asData) {
		return getBits(asData, 20, 8);
	}

	public static int setOppontntSunkenCost(int asData, int val) {
		// TODO: add parameter validation
		return setBits(asData, val, 20, 8);
	}
	
	public static int getPlayer(int asData) {
		return getBits(asData, 28, 1);
	}

	public static int setPlayer(int asData, int player) {
		assert Player.validate(player);
		return setBits(asData, player, 28, 1);
	}

}
