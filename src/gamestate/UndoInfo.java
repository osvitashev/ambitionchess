package gamestate;

import static util.BitField32.*;

/**
 * Generated by Board.domove(move) contains position information which gets
 * irreversibly corrupted when a info is made For example: castling
 * availability, en passant square, and counter for 50-info rule.
 * 
//@formatter:off
  * 
  	memory packing: 
	start	length	name
	0		8		50rule halfinfo counter
	8		6		enpassant square
	14		1		castling wk
	15		1		castling wq
	16		1		castling bk
	17		1		castling bq
	18		1		is in check -- board state from before the move was done.
	19		1		is Enpassant available
	20???	
	
 	some of the space can be analyzed board state AFTER the info is made: isTrippleRepetition?
 	
 	Now that Move includes information on historical board state for Move.undo, it is important to be careful with info comparison
 	Does "e2".equal("e2") even if they have different castling conditions? or different quiet_halfinfo_counts?
 	=> "e2" giving check is different from "e2" without giving check when it comes to info ordering!!
 	
//@formatter:on
 *
 */

public class UndoInfo {

	/**
	 * 
	 * @param info
	 * @return
	 */
	public static int getHalfmoveCounter(int info) {
		return getBits(info, 0, 8);
	}

	public static int setHalfmoveCounter(int info, int val) {
		return setBits(info, val, 0, 8);
	}

	public static int getEnpassantSquare(int info) {
		return getBits(info, 8, 6);
	}

	public static int setEnpassantSquare(int info, int val) {
		DebugLibrary.validateSquare(val);
		return setBits(info, val, 8, 6);
	}

	public static boolean getCastlingWK(int info) {
		return getBoolean(info, 14);
	}

	public static int setCastlingWK(int info, boolean val) {
		return setBoolean(info, val, 14);
	}

	public static boolean getCastlingWQ(int info) {
		return getBoolean(info, 15);
	}

	public static int setCastlingWQ(int info, boolean val) {
		return setBoolean(info, val, 15);
	}

	public static boolean getCastlingBK(int info) {
		return getBoolean(info, 16);
	}

	public static int setCastlingBK(int info, boolean val) {
		return setBoolean(info, val, 16);
	}

	public static boolean getCastlingBQ(int info) {
		return getBoolean(info, 17);
	}

	public static int setCastlingBQ(int info, boolean val) {
		return setBoolean(info, val, 17);
	}

	public static boolean getIsCheck(int info) {
		return getBoolean(info, 18);
	}

	public static int setCheck(int info, boolean val) {
		return setBoolean(info, val, 18);
	}
	
	public static boolean getIsEnpassantAvailable(int info) {
		return getBoolean(info, 19);
	}

	public static int setIsEnpassantAvailable(int info, boolean val) {
		return setBoolean(info, val, 19);
	}

}
