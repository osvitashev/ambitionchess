package searcher;

import gamestate.Move;

/**
 * PV table implementation
 * 
 * @author sveta
 *
 */
public class PrincipalVariation {
	private static int MAX_POSSIBLE_DEPTH = 100;

	private int[][] pvTable = new int[MAX_POSSIBLE_DEPTH][];
	/**
	 * lengthAtDepth[0] is the length of principal variation.
	 */
	private int[] lengthAtDepth = new int[MAX_POSSIBLE_DEPTH];

	public PrincipalVariation() {
		for (int i = 0; i < MAX_POSSIBLE_DEPTH; ++i) {
			pvTable[i] = new int[MAX_POSSIBLE_DEPTH - i];
		}
	}

	void reset() {
		for (int i = 0; i < MAX_POSSIBLE_DEPTH; ++i)
			lengthAtDepth[i] = 0;
	}
	
	void resetAtDepth(int depth) {
		lengthAtDepth[depth] = 0;
	}

	void addMoveAtDepth(int move, int depth) {
		pvTable[depth][0] = move;
		for (int i = 0; i < lengthAtDepth[depth + 1]; ++i)
			pvTable[depth][i + 1] = pvTable[depth + 1][i];
		lengthAtDepth[depth] =lengthAtDepth[depth+1] + 1;
	}

	public String toString() {
		String ret = "{";
		for(int i=0; i<lengthAtDepth[0];++i)
			ret+=Move.toUCINotation(pvTable[0][i]) + " ";
		return ret+"}";
	}
}
