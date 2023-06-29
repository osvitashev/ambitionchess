package seecontrol;

import gamestate.DebugLibrary;

public class SEEConstants {
	
	public static final class PieceCosts {
		private static final int[] COSTS = { 1, 3, 3, 5, 10};
		
		/**
		 * Notice that King does not have an assigned value!!!!
		 * @param pieceType
		 * @return
		 */
		public static int getValue(int pieceType) {
			DebugLibrary.validatePieceType(pieceType);
			return COSTS[pieceType];
		}
	}

}
