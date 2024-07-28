package searcher;

import static util.BitField64.*;

/**
 * 
 * Represents the return value of alpha-beta (or similar) search.
 * Contains score along with some metadata
 * 
 * The score segment is representable with a short data type with the range of [-32,768 to 32,767]
 * This would allow the traditional evaluation function to use tricentipawn as the basic unit.
 * i.e. a pawn would have the cost of 300 unit points.
 * This allows to represent the maximum possible material discrepancy on 9 queens.
 * This does not allow to give a king a proportionally larger cost.
 * However, it is still possible to use this representation for pseudolegal gameplay.
 * The only precaution needed is to make sure that new moves are not generated if the king is not on the board.
 * Also, if the king is not on the board, the score has to be calculated as a checkmate, rather than the materual sum.
 * 
 * Traditionally, large values imply a desirable outcome.
 * Short.MAX_VALUE as the score would imply checkmating the opponent.
 * Short.MAX_VALUE - 3 would imply checkmate in 3 plies.
 * 
 *  * 
 //@formatter:off
  * 
  	memory packing: 
	start	length	name
	0		16		score (short)
	16		8		depth (number of plies) - starts from the beginning of the search. NOT THE GAME!
	24		1		is checkmate?
	25		1		is stalemate? (side to play has no legal moves)
	26		1		is draw for other reason (triple repetition, insufficient material, triple move rule...)
	
 	
 	There is a lot of room for potential future improvements:
 	1. move (or move index) to be returned
 	2. some measurement of evaluation confidence
 	
 //@formatter:on
 *
 */
public class SearchOutcome {
	public static int FAVOURABLE_DRAW_FOR_MAXIMIZING_PLAYER = 31000;
	public static int WIN =  Short.MAX_VALUE;
	public static int LOSS = Short.MIN_VALUE+1;//+1 is needed to achieve symmetry abound 0
	
	/**
	 * Returns the score intended for comparisons in alpha-beta search.
	 * NOTE: It is not guaranteed that a draw would have a zero score. That mapping is determined elsewhere.
	 * @param rez
	 * @return
	 */
	public static int getScore(long rez) {
		return (short)(rez & 0xFFFFL);
	}
	
	public static long setScore(long rez, int score) {
		assert Short.MAX_VALUE >= score : "got: " + score;
		assert Short.MIN_VALUE <= score : "got: " + score;
		rez &= ~0xFFFFL;
		rez |= ((long)score) & 0xFFFFL;;
		return rez;
	}
	
	public static int getDepth(long rez) {
		return getBits(rez, 16, 8);
	}
	
	public static long setDepth(long rez, int val) {
		return setBits(rez, val, 16, 8);
	}
	
	public static boolean isCheckmate(long rez) {
		return getBoolean(rez, 24);
	}
	
	public static long setCheckmate(long rez) {
		return setBoolean(rez, true, 24);
	}
	
	/**
	 * Specifically situation where side to play has no legal moves.
	 * @param rez
	 * @return
	 */
	public static boolean isStalemate(long rez) {
		return getBoolean(rez, 25);
	}
	
	public static long setStalemate(long rez) {
		return setBoolean(rez, true, 25);
	}
	
	/**
	 * is draw for reason other than technical stalemate (triple repetition, insufficient material, triple move rule...)
	 * @param rez
	 * @return
	 */
	public static boolean isOtherDraw(long rez) {
		return getBoolean(rez, 26);
	}
	
	public static long setOtherDraw(long rez) {
		return setBoolean(rez, true, 26);
	}
	
	/**
	 * Negation is required to take advantage of max(a,b)=-min(-a,-b)
	 * Only negates the score component. Leaves other metadata untouched.
	 * @param rez
	 * @return
	 */
	public static long negateScore(long rez) {
		return setScore(rez, -getScore(rez));
	}
	
	/**
	 * Acts as an implementation of the less_than operator
	 * @param rez1
	 * @param rez2
	 * @return
	 */
	public static boolean isScoreLess(long rez1, long rez2) {
		return getScore(rez1) < getScore(rez2);
	}
	
	public static boolean isScoreGreater(long rez1, long rez2) {
		return getScore(rez1) > getScore(rez2);
	}
	
	public static boolean isScoreLessOrEqual(long rez1, long rez2) {
		return getScore(rez1) <= getScore(rez2);
	}
	
	public static boolean isScoreGreaterOrEqual(long rez1, long rez2) {
		return getScore(rez1) >= getScore(rez2);
	}
	
	public static long createCheckmate(int distance) {
		long ret = setCheckmate(0L);
		ret = setDepth(ret, distance);
		ret = setScore(ret, LOSS+distance);
		return ret;
	}
	
	public static long createStalemate(int depth, int score) {
		long ret = setStalemate(0L);
		ret = setDepth(ret, depth);
		ret = setScore(ret, score);
		return ret;
	}
	
	public static long createWithDepthAndScore(int depth, int score) {
		long ret = 0;
		ret = setDepth(ret, depth);
		ret = setScore(ret, score);
		return ret;
	}
	
	public static String outcomeToString(long outcome) {
		String ret = "{depth=";
		ret+=getDepth(outcome);
		ret+=", score=";
		ret+=getScore(outcome);
		if(isCheckmate(outcome))
			ret+= ", CHECKMATE!";
		if(isStalemate(outcome))
			ret+= ", STALEMATE!";
		if(isOtherDraw(outcome))
			ret+= ", OTHER_DRAW!";
		return ret + "}";
	}
	
	public static String outcomeToStringInMaximixerPerspective(long outcome, boolean isMaximizer) {
		String ret = "{depth=";
		ret+=getDepth(outcome);
		ret+=", maximizerScore=";
		ret+=isMaximizer ? getScore(outcome) : -getScore(outcome);
		if(isCheckmate(outcome))
			ret+= ", CHECKMATE!";
		if(isStalemate(outcome))
			ret+= ", STALEMATE!";
		if(isOtherDraw(outcome))
			ret+= ", OTHER_DRAW!";
		return ret + "}";
	}
	
}
