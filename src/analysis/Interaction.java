package analysis;

import static util.BitField32.setBits;
import static util.BitField32.getBits;

import gamestate.GlobalConstants.Square;

public class Interaction {
	
	private static final int TYPE_OVERPROTECTION = 1;
	
	//types width: 6 bits!
	private static final int[] TYPES = {TYPE_OVERPROTECTION};
	
	public static int getType(int interaction) {
		int ret = getBits(interaction, 0, 6);
		assert 0 < ret && ret <=TYPES.length;
		return ret;
	}
	
	public static String toString(int interaction) {
		String ret;
		int type = getType(interaction);
		switch (type) {
		case TYPE_OVERPROTECTION:
			int sq_prodider = getBits(interaction, 6, 6);
			int sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_prodider) + " overprotects " + Square.toString(sq_target)+"}";
			break;
		default:
			ret="NOT SUPPOETED";
			break;
		}
		return ret;
	}
	
	public static int createOverprotection(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_OVERPROTECTION, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
}
