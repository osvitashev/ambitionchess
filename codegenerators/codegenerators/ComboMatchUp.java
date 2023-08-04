package codegenerators;

import java.io.Serializable;

import exchange.control.AttackSet;

public class ComboMatchUp implements Serializable, Comparable<ComboMatchUp> {
	AttackCombo attacker, defender;
	long matchupKey;
	
	int naturalOutcomeTargetPawn, naturalOutcomeTargetMinor, naturalOutcomeTargetRook, naturalOutcomeTargetQueen;
	
	public ComboMatchUp(AttackCombo a, AttackCombo b) {
		attacker=a;
		defender=b;
		naturalOutcomeTargetPawn=MyLookupGenerator.calculateGain_pureAttacks(attacker, defender, 100);
		naturalOutcomeTargetMinor=MyLookupGenerator.calculateGain_pureAttacks(attacker, defender, 300);
		naturalOutcomeTargetRook=MyLookupGenerator.calculateGain_pureAttacks(attacker, defender, 500);
		naturalOutcomeTargetQueen=MyLookupGenerator.calculateGain_pureAttacks(attacker, defender, 1000);
		
		matchupKey=(((long)attacker.serializedLongKey)<<32) | ((long)defender.serializedLongKey);
		// TODO Auto-generated constructor stub
	}
	
	String toStringNaturalAttacks() {
		String ret="";
		ret+=attacker.toString() + " VS. " + defender.toString()+ "\n";
		ret+= "natural attack payoffs per target: " + getStringNaturalAttacks();
		ret+="matchupKey="+Long.toOctalString(matchupKey);
		return ret;
	}
	
	String getStringNaturalAttacks() {
		String ret="";
		ret+= "P=" + naturalOutcomeTargetPawn+ " ";
		ret+= "M=" + naturalOutcomeTargetMinor+ " ";
		ret+= "R=" + naturalOutcomeTargetRook+ " ";
		ret+= "Q=" + naturalOutcomeTargetQueen+ " ";
		return ret;
	}
	
	@Override
    public int compareTo(ComboMatchUp cmu) {
        if(matchupKey-cmu.matchupKey <0L)
        	return -1;
        else if (matchupKey-cmu.matchupKey >0L)
    		return 1;
		return 0;
    }
}
