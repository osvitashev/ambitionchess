package codegenerators;

import java.io.Serializable;

import exchange.control.AttackSet;
import gamestate.GlobalConstants.PieceType;

public class ComboMatchUp implements Serializable, Comparable<ComboMatchUp> {
	AttackCombo attacker, defender;
	long matchupKey;

	int naturalOutcomeTargetPawn, naturalOutcomeTargetMinor, naturalOutcomeTargetRook, naturalOutcomeTargetQueen;

	public ComboMatchUp(AttackCombo a, AttackCombo b) {
		attacker = a;
		defender = b;
		naturalOutcomeTargetPawn = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost(PieceType.PAWN));
		naturalOutcomeTargetMinor = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost(PieceType.KNIGHT));
		naturalOutcomeTargetRook = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost(PieceType.ROOK));
		naturalOutcomeTargetQueen = MyLookupGenerator.calculateGain_pureAttacks(attacker.unconditionalAttackers, attacker.attackersThroughEnemyPawn,
				defender.unconditionalAttackers, defender.attackersThroughEnemyPawn, AttackCombo.pieceCost(PieceType.QUEEN));

		matchupKey = (((long) attacker.serializedIntKey) << 32) | ((long) defender.serializedIntKey);
		// TODO Auto-generated constructor stub
	}

	String toStringNaturalAttacks() {
		String ret = "";
		ret += attacker.toString() + " VS. " + defender.toString() + "\n";
		ret += "natural attack payoffs per target: " + getStringNaturalAttacks();
		ret += "matchupKey=" + Long.toOctalString(matchupKey);
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
}
