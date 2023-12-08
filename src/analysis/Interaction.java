package analysis;

import static util.BitField32.setBits;
import static util.BitField32.getBits;

import gamestate.GlobalConstants.Square;

public class Interaction {
	
	private static final int TYPE_OVERPROTECTION = 1;
	private static final int TYPE_BARELY_ADEQUATE_GUARD = 2;
	private static final int TYPE_ADEQUATE_GUARD = 3;
	
	//types width: 6 bits!
	private static final int[] TYPES = {TYPE_OVERPROTECTION, TYPE_BARELY_ADEQUATE_GUARD, TYPE_ADEQUATE_GUARD};
	
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
		case TYPE_OVERPROTECTION:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " overprotects " + Square.toString(sq_target)+"}";
			break;
		case TYPE_BARELY_ADEQUATE_GUARD:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " barely adequately guards " + Square.toString(sq_target)+"}";
			break;
		case TYPE_ADEQUATE_GUARD:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " adequately guards " + Square.toString(sq_target)+"}";
			break;
		default:
			ret="NOT SUPPORTED";
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
	
	public static int createBarelyAdequateGuard(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_BARELY_ADEQUATE_GUARD, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createAdequateGuard(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_ADEQUATE_GUARD, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
}
