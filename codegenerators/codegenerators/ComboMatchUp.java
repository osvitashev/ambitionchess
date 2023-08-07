package codegenerators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exchange.control.AttackSet;

public class ComboMatchUp implements Serializable, Comparable<ComboMatchUp> {
	AttackCombo attacker, defender;
	long matchupKey;
	int whatIfMatrix=0;

	int naturalOutcomeTargetPawn, naturalOutcomeTargetMinor, naturalOutcomeTargetRook, naturalOutcomeTargetQueen;

	public ComboMatchUp(AttackCombo a, AttackCombo b) {
		attacker = a;
		defender = b;
		naturalOutcomeTargetPawn = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost('P'));
		naturalOutcomeTargetMinor = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost('M'));
		naturalOutcomeTargetRook = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost('R'));
		naturalOutcomeTargetQueen = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost('Q'));

		matchupKey = (((long) attacker.serializedIntKey) << 32) | ((long) defender.serializedIntKey);
		
		/**
		 * note: N and B are represented by their own codes instead of M
		 * because M is not going to be in the Unconditional attackers collection.
		 */
		char[] targets = {'P', 'B', 'N', 'R', 'Q'};
		char[] forcedAttackers = {'P', 'B', 'N', 'R', 'Q'};
		List<Character> copyWithoutFirstMatch;
		int index, tempEval;

		for(char t: targets) {
			for(char fa: forcedAttackers) {
				if(-1 != attacker.unconditionalAttackers.indexOf(fa)) {//given attacker is present in the attack combo's unconditional portion
					if(0 == attacker.unconditionalAttackers.indexOf(fa)) {//given attacker is the first in the stack
						if(t=='P' && naturalOutcomeTargetPawn>0)
							whatIfMatrix=setWhatIf(whatIfMatrix, fa, t);
						else if((t=='N' || t=='B') && naturalOutcomeTargetMinor>0)
							whatIfMatrix=setWhatIf(whatIfMatrix, fa, t);
						else if(t=='R' && naturalOutcomeTargetRook>0)
							whatIfMatrix=setWhatIf(whatIfMatrix, fa, t);
						else if(t=='Q' && naturalOutcomeTargetQueen>0)
							whatIfMatrix=setWhatIf(whatIfMatrix, fa, t);
					}
					else {//the given attacker is not at the top of the stack AND is not a pawn, as pawn are first to be considered in normal evaluation. 
						copyWithoutFirstMatch = new ArrayList<>(attacker.unconditionalAttackers);
						index = copyWithoutFirstMatch.indexOf(fa);
						copyWithoutFirstMatch.subList(index, index + 1).clear();
						tempEval = MyLookupGenerator.calculateGain_pureAttacks(defender.unconditionalAttackers, defender.attackersThroughEnemyPawn,
								copyWithoutFirstMatch, attacker.attackersThroughEnemyPawn, AttackCombo.pieceCost(fa));
	//This is not correct - temp eval is missing one last call to minimax!!!!
						tempEval=Math.max(0, AttackCombo.pieceCost(t)-tempEval);
						
						if(tempEval>0)
							whatIfMatrix=setWhatIf(whatIfMatrix, fa, t);
					}
				}
			}
		}
		
		
	}

	String toVerboseString() {
		String ret = "";
		ret += attacker.toString() + " VS. " + defender.toString() + "\n";
		ret += "natural attack payoffs per target: " + getStringNaturalAttacks();
		ret += "matchupKey=" + Long.toOctalString(matchupKey) + "\n";
		ret+= whatIfToString(whatIfMatrix);
		return ret;
	}

	String getStringNaturalAttacks() {
		String ret = "";
		ret += "P=" + naturalOutcomeTargetPawn + " ";
		ret += "M=" + naturalOutcomeTargetMinor + " ";
		ret += "R=" + naturalOutcomeTargetRook + " ";
		ret += "Q=" + naturalOutcomeTargetQueen + " ";
		return ret;
	}

	@Override
	public int compareTo(ComboMatchUp cmu) {
		if (matchupKey - cmu.matchupKey < 0L)
			return -1;
		else if (matchupKey - cmu.matchupKey > 0L)
			return 1;
		return 0;
	}
	
	static boolean getWhatIf(int whatIfMatrix, Character forcedAttacker, Character target) {
		//not considering king!
		assert target.equals('P')|target.equals('N')|target.equals('B')|target.equals('M')
			|target.equals('R')|target.equals('Q');
		assert forcedAttacker.equals('P')|forcedAttacker.equals('N')|forcedAttacker.equals('B')
			|forcedAttacker.equals('M')|forcedAttacker.equals('R')|forcedAttacker.equals('Q');
		int index=AttackCombo.getSerializedAttacker(target)*4+AttackCombo.getSerializedAttacker(forcedAttacker);
		return (whatIfMatrix & (1<<index)) !=0;
	}
	
	static int setWhatIf(int whatIfMatrix, Character forcedAttacker, Character target) {
		//not considering king!
		assert target.equals('P')|target.equals('N')|target.equals('B')|target.equals('M')
			|target.equals('R')|target.equals('Q');
		assert forcedAttacker.equals('P')|forcedAttacker.equals('N')|forcedAttacker.equals('B')
			|forcedAttacker.equals('M')|forcedAttacker.equals('R')|forcedAttacker.equals('Q');
		int index=AttackCombo.getSerializedAttacker(target)*4+AttackCombo.getSerializedAttacker(forcedAttacker);
		return (whatIfMatrix | (1<<index));
	}
	
	static String whatIfToString(int whatIfMatrix) {
		String ret="  ";
		char[] targets = {'P', 'M', 'R', 'Q'};
		char[] forcedAttackers = {'P', 'M', 'R', 'Q'};
		
		for(char fa: forcedAttackers)
			ret+=fa + " ";
		ret+=" - attackers\n";
		for(char t: targets) {
			ret+=t + " ";
			for(char fa: forcedAttackers) {
				if(getWhatIf(whatIfMatrix, fa, t))
					ret+="x ";
				else
					ret+=". ";
			}
			ret+="\n";
		}
		return ret;
	}
}
