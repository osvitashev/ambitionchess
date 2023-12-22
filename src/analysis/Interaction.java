package analysis;

import static util.BitField32.setBits;
import static util.BitField32.getBits;

import gamestate.GlobalConstants.Square;

public class Interaction {
	
	private static final int TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_POSITIVE = 1;//removing this defender improves attacker's score from negative to positive.
	private static final int TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_NEUTRAL = 2;//removing this defender improves attacker's score from negative to neutral.
	private static final int TYPE_GUARD_BOUND_SCORE_NEUTRAL_TO_POSITIVE = 3;//removing this defender improves attacker's score from neutral to positive.

	
	//types width: 6 bits!
	private static final int[] TYPES = {
			TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_POSITIVE,
			TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_NEUTRAL,
			TYPE_GUARD_BOUND_SCORE_NEUTRAL_TO_POSITIVE,
		};
	
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
		case TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_POSITIVE:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards (bound to) " + Square.toString(sq_target)+" (score: negative->positive)}";
			break;
		case TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_NEUTRAL:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards (bound to) " + Square.toString(sq_target)+" (score: negative->neutral)}";
			break;
		case TYPE_GUARD_BOUND_SCORE_NEUTRAL_TO_POSITIVE:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards (bound to) " + Square.toString(sq_target)+" (score: neutral->positive)}";
			break;
		default:
			ret="NOT SUPPORTED";
			break;
		}
		return ret;
	}
	
	public static int createGuardBound_negativeToPositive(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_POSITIVE, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createGuardBound_negativeToNeutral(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_GUARD_BOUND_SCORE_NEGATIVE_TO_NEUTRAL, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createGuardBound_neutralToPositive(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_GUARD_BOUND_SCORE_NEUTRAL_TO_POSITIVE, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	

	
}
