package analysis;

import static util.BitField32.setBits;
import static util.BitField32.getBits;

import gamestate.GlobalConstants.Square;

public class Interaction {
	
	private static final int TYPE_GUARD_TIED_UP = 1;
	private static final int TYPE_GUARD_OVERPROTECTS = 2;
	
	//types width: 6 bits!
	private static final int[] TYPES = {TYPE_GUARD_TIED_UP, TYPE_GUARD_OVERPROTECTS};
	
	public static int getType(int interaction) {
		int ret = getBits(interaction, 0, 6);
		assert 0 < ret && ret <=TYPES.length;
		return ret;
	}
	
	public static String toString(int interaction) {
		String ret;
		int type = getType(interaction);
		int sq_provider, sq_target;
		switch (type) {
		case TYPE_GUARD_TIED_UP:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards (is tied up with) " + Square.toString(sq_target)+"}";
			break;
		case TYPE_GUARD_OVERPROTECTS:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " overprotects " + Square.toString(sq_target)+"}";
			break;
		default:
			ret="NOT SUPPORTED";
			break;
		}
		return ret;
	}
	
	public static int createAdequateGuardTiedUp(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_GUARD_TIED_UP, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createGuardOverprotect(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_GUARD_OVERPROTECTS, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	

	
}
