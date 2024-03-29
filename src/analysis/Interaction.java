package analysis;

import static util.BitField32.setBits;
import static util.BitField32.getBits;

import gamestate.GlobalConstants.Square;

public class Interaction {
	
	private static final int TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE = 1;//removing this defender improves attacker's score from negative to positive.
	private static final int TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL = 2;//removing this defender improves attacker's score from negative to neutral.
	private static final int TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE = 3;//removing this defender improves attacker's score from neutral to positive.
	
	private static final int TYPE_PIN_POSITIVE = 4;
	private static final int TYPE_PIN_NEUTRAL = 5;
	
	private static final int TYPE_DISCOVERED_THREAT_POSITIVE =6;
	private static final int TYPE_DISCOVERED_THREAT_NEUTRAL =7;
	
//	private static final int TYPE_SKEWER_POSITIVE = 6;
//	private static final int TYPE_SKEWER_NEUTRAL = 7;
	
	
	//types width: 6 bits!
	private static final int[] TYPES = {
			TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE,
			TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL,
			TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE,
			
			TYPE_PIN_POSITIVE,
			TYPE_PIN_NEUTRAL,
			
			TYPE_DISCOVERED_THREAT_POSITIVE,
			TYPE_DISCOVERED_THREAT_NEUTRAL
//			TYPE_SKEWER_POSITIVE,//todo: skewer is not the correct name here. What this actually is - a threat of discovered attack!
//			TYPE_SKEWER_NEUTRAL,
		};
	
	public static boolean validateInteraction(int interaction) {
		int val = getBits(interaction, 0, 6);
		return 0 < val && val <=TYPES.length;
	}
	
	public static int getType(int interaction) {
		validateInteraction(interaction);
		return getBits(interaction, 0, 6);
	}
	
	public static int debug_getVictimSquare(int interaction) {
		assert (
				getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE ||
				getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL ||
				getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE
			)
			||
			(
				getType(interaction) == TYPE_PIN_POSITIVE ||
				getType(interaction) == TYPE_PIN_NEUTRAL ||
				getType(interaction) == TYPE_DISCOVERED_THREAT_POSITIVE ||
				getType(interaction) == TYPE_DISCOVERED_THREAT_NEUTRAL
			);
		int ret=-1;
		if(
			getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE ||
			getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL ||
			getType(interaction) == TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE) {
			ret = getBits(interaction, 12, 6);
		}
		else if(
			getType(interaction) == TYPE_PIN_POSITIVE ||
			getType(interaction) == TYPE_PIN_NEUTRAL ||
			getType(interaction) == TYPE_DISCOVERED_THREAT_POSITIVE ||
			getType(interaction) == TYPE_DISCOVERED_THREAT_NEUTRAL
			) {
			ret = getBits(interaction, 18, 6);
		}
			
		assert Square.validate(ret);
		return ret;
	}
	
	public static String toString(int interaction) {
		assert validateInteraction(interaction);
		String ret;
		int type = getType(interaction);
		int sq_provider, sq_target;
		int sq_attacker, sq_pinned, sq_victim;
		
		switch (type) {
		case TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards " + Square.toString(sq_target)+" (- to +)}";
			break;
		case TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards " + Square.toString(sq_target)+" (- to 0)}";
			break;
		case TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE:
			sq_provider = getBits(interaction, 6, 6);
			sq_target = getBits(interaction, 12, 6);
			ret = "{"+Square.toString(sq_provider) + " guards " + Square.toString(sq_target)+" (0 to +)}";
			break;
		case TYPE_PIN_POSITIVE:
			sq_attacker = getBits(interaction, 6, 6);
			sq_pinned = getBits(interaction, 12, 6);
			sq_victim = getBits(interaction, 18, 6);
			ret = "{" + Square.toString(sq_attacker) + " pins " + Square.toString(sq_pinned) + " to " + Square.toString(sq_victim) + " (+)}";
			break;
		case TYPE_PIN_NEUTRAL:
			sq_attacker = getBits(interaction, 6, 6);
			sq_pinned = getBits(interaction, 12, 6);
			sq_victim = getBits(interaction, 18, 6);
			ret = "{" + Square.toString(sq_attacker) + " pins " + Square.toString(sq_pinned) + " to " + Square.toString(sq_victim) + " (0)}";
			break;
		case TYPE_DISCOVERED_THREAT_POSITIVE:
			sq_attacker = getBits(interaction, 6, 6);
			sq_pinned = getBits(interaction, 12, 6);
			sq_victim = getBits(interaction, 18, 6);
			ret = "{" + Square.toString(sq_attacker) + " via " + Square.toString(sq_pinned) + " threatens " + Square.toString(sq_victim) + " (+)}";
			break;
		case TYPE_DISCOVERED_THREAT_NEUTRAL:
			sq_attacker = getBits(interaction, 6, 6);
			sq_pinned = getBits(interaction, 12, 6);
			sq_victim = getBits(interaction, 18, 6);
			ret = "{" + Square.toString(sq_attacker) + " via " + Square.toString(sq_pinned) + " threatens " + Square.toString(sq_victim) + " (0)}";
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
		int ret=setBits(0, TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_POSITIVE, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createGuardBound_negativeToNeutral(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_BOUND_GUARD_SCORE_NEGATIVE_TO_NEUTRAL, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createGuardBound_neutralToPositive(int sq_provider, int sq_target) {
		assert Square.validate(sq_provider);
		assert Square.validate(sq_target);
		int ret=setBits(0, TYPE_BOUND_GUARD_SCORE_NEUTRAL_TO_POSITIVE, 0, 6);
		ret=setBits(ret, sq_provider, 6, 6);
		ret=setBits(ret, sq_target, 12, 6);
		return ret;
	}
	
	public static int createPin_positive(String sq_attacker, String sq_pinned, String sq_victim) {
		return createPin_positive(Square.algebraicStringToSquare(sq_attacker), Square.algebraicStringToSquare(sq_pinned),
				Square.algebraicStringToSquare(sq_victim));
	}
	
	public static int createPin_positive(int sq_attacker, int sq_pinned, int sq_victim) {
		assert Square.validate(sq_attacker);
		assert Square.validate(sq_pinned);
		assert Square.validate(sq_victim);
		int ret=setBits(0, TYPE_PIN_POSITIVE, 0, 6);
		ret=setBits(ret, sq_attacker, 6, 6);
		ret=setBits(ret, sq_pinned, 12, 6);
		ret=setBits(ret, sq_victim, 18, 6);
		return ret;
	}
	
	public static int createPin_neutral(String sq_attacker, String sq_pinned, String sq_victim) {
		return createPin_neutral(Square.algebraicStringToSquare(sq_attacker), Square.algebraicStringToSquare(sq_pinned),
				Square.algebraicStringToSquare(sq_victim));
	}
	
	public static int createPin_neutral(int sq_attacker, int sq_pinned, int sq_victim) {
		assert Square.validate(sq_attacker);
		assert Square.validate(sq_pinned);
		assert Square.validate(sq_victim);
		int ret=setBits(0, TYPE_PIN_NEUTRAL, 0, 6);
		ret=setBits(ret, sq_attacker, 6, 6);
		ret=setBits(ret, sq_pinned, 12, 6);
		ret=setBits(ret, sq_victim, 18, 6);
		return ret;
	}
	
	public static int createDiscoveredThreat_neutral(String sq_attacker, String sq_pinned, String sq_victim) {
		return createDiscoveredThreat_neutral(Square.algebraicStringToSquare(sq_attacker), Square.algebraicStringToSquare(sq_pinned),
				Square.algebraicStringToSquare(sq_victim));
	}
	
	public static int createDiscoveredThreat_neutral(int sq_attacker, int sq_pinned, int sq_victim) {
		assert Square.validate(sq_attacker);
		assert Square.validate(sq_pinned);
		assert Square.validate(sq_victim);
		int ret=setBits(0, TYPE_DISCOVERED_THREAT_NEUTRAL, 0, 6);
		ret=setBits(ret, sq_attacker, 6, 6);
		ret=setBits(ret, sq_pinned, 12, 6);
		ret=setBits(ret, sq_victim, 18, 6);
		return ret;
	}
	
	public static int createDiscoveredThreat_positive(String sq_attacker, String sq_pinned, String sq_victim) {
		return createDiscoveredThreat_positive(Square.algebraicStringToSquare(sq_attacker), Square.algebraicStringToSquare(sq_pinned),
				Square.algebraicStringToSquare(sq_victim));
	}
	
	public static int createDiscoveredThreat_positive(int sq_attacker, int sq_pinned, int sq_victim) {
		assert Square.validate(sq_attacker);
		assert Square.validate(sq_pinned);
		assert Square.validate(sq_victim);
		int ret=setBits(0, TYPE_DISCOVERED_THREAT_POSITIVE, 0, 6);
		ret=setBits(ret, sq_attacker, 6, 6);
		ret=setBits(ret, sq_pinned, 12, 6);
		ret=setBits(ret, sq_victim, 18, 6);
		return ret;
	}

	
}
