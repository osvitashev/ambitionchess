package util;

import gamestate.GlobalConstants.Player;

public class Utilities {	
	public static class OutcomeEnum{
		public static final int NEGATIVE = -1;
		public static final int NEUTRAL = 0;
		public static final int POSITIVE = 1;
		
		public static boolean validate(int outcome) {
			return outcome == -1 || outcome == 0 || outcome == 1;
		}
	}
}