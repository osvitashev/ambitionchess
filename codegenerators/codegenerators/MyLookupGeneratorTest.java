package codegenerators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;

class MyLookupGeneratorTest {

	@Test
	void testWideBitfieldAccumulator() {
		WideBitfieldAccumulator w = new WideBitfieldAccumulator(370660);
		assertFalse(w.get(28));
		assertFalse(w.isUsed(28));
		w.setPayload(28);
		assertTrue(w.isUsed(28));
		assertTrue(w.get(28));
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> w.setPayload(28), "index is unavailable!");
		assertTrue(thrown.getMessage().equals("index is unavailable!"));

		assertFalse(w.get(310660));
		w.setPayload(310660);
		assertTrue(w.get(310660));

		assertFalse(w.get(270660));
		w.lock(270660);
		thrown = assertThrows(RuntimeException.class, () -> w.setPayload(270660), "index is unavailable!");
		assertTrue(thrown.getMessage().equals("index is unavailable!"));

	}

	@Test
	void testWhatIfOperations() {
		int wif = 0;
		assertFalse(ComboMatchUp.getWhatIf(wif, 'P', 'R'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'M', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'P', 'P'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'Q', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'P', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'Q', 'P'));

		wif = ComboMatchUp.setWhatIf(wif, 'P', 'R');
		wif = ComboMatchUp.setWhatIf(wif, 'M', 'Q');
		assertTrue(ComboMatchUp.getWhatIf(wif, 'P', 'R'));
		assertTrue(ComboMatchUp.getWhatIf(wif, 'M', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'P', 'P'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'Q', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'P', 'Q'));
		assertFalse(ComboMatchUp.getWhatIf(wif, 'Q', 'P'));

		assertEquals("  P M R Q  - attackers\n" + "P . . . . \n" + "M . . . . \n" + "R x . . . \n" + "Q . x . . \n",
				ComboMatchUp.whatIfToString(wif));

		System.out.println(ComboMatchUp.whatIfToString(wif));

	}

	@Test
	void testAttackComboOperations() {
		AttackCombo ac = new AttackCombo();
		ac.unconditionalAttackers.add('N');
		ac.unconditionalAttackers.add('R');
		ac.unconditionalAttackers.add('K');
		ac.setSerializedIntKey();
		assertEquals("long=[NRK|] short=[MRK|] serialized= 421", ac.toString());

		ac = new AttackCombo();
		ac.unconditionalAttackers.add('P');
		ac.unconditionalAttackers.add('P');
		ac.unconditionalAttackers.add('R');
		ac.unconditionalAttackers.add('R');
		ac.unconditionalAttackers.add('Q');
		ac.unconditionalAttackers.add('K');
		ac.attackersThroughEnemyPawn.add('B');
		ac.attackersThroughEnemyPawn.add('Q');
		ac.setSerializedIntKey();
		assertEquals("long=[PPRRQK|BQ] short=[PPRRQK|MQ] serialized= 13432200", ac.toString());

		ac = new AttackCombo();
		ac.unconditionalAttackers.add('N');
		ac.unconditionalAttackers.add('B');
		ac.unconditionalAttackers.add('R');
		ac.unconditionalAttackers.add('Q');
		ac.unconditionalAttackers.add('R');
		ac.unconditionalAttackers.add('K');
		ac.attackersThroughEnemyPawn.add('Q');
		ac.setSerializedIntKey();
		assertEquals("long=[NBRQRK|Q] short=[MMRQRK|Q] serialized= 3423211", ac.toString());

		ac = new AttackCombo();
		ac.attackersThroughEnemyPawn.add('Q');
		ac.attackersThroughEnemyPawn.add('B');
		ac.attackersThroughEnemyPawn.add('Q');
		ac.setSerializedIntKey();
		assertEquals("long=[|QBQ] short=[|QMQ] serialized= 3135", ac.toString());

		ac = new AttackCombo();
		ac.setSerializedIntKey();
		assertEquals("long=[|] short=[|] serialized= 5", ac.toString());

		ac = new AttackCombo();
		ac.unconditionalAttackers.add('K');
		ac.setSerializedIntKey();
		assertEquals("long=[K|] short=[K|] serialized= 4", ac.toString());

		ac = new AttackCombo();
		ac.unconditionalAttackers.add('K');
		ac.attackersThroughEnemyPawn.add('B');
		ac.setSerializedIntKey();
		assertEquals("long=[K|B] short=[K|M] serialized= 14", ac.toString());

		ac = new AttackCombo();
		ac.unconditionalAttackers.add('P');
		ac.unconditionalAttackers.add('P');
		ac.setSerializedIntKey();
		assertEquals("long=[PP|] short=[PP|] serialized= 500", ac.toString());
	}

	private AttackCombo populateAttackCombo(Character... attackers) {
		AttackCombo ac = new AttackCombo();
		for (Character a : attackers) {
			ac.unconditionalAttackers.add(a);
		}
		return ac;
	}

	private void helper_add_attackers(ArrayList<Character> col, String str) {
		Character a = null;
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) == 'P')
				a = 'P';
			else if (str.charAt(i) == 'R')
				a = 'R';
			else if (str.charAt(i) == 'B')
				a = 'B';
			else if (str.charAt(i) == 'N')
				a = 'N';
			else if (str.charAt(i) == 'Q')
				a = 'Q';
			else if (str.charAt(i) == 'K')
				a = 'K';
			else
				throw new RuntimeException("unexpected value!");
			col.add(a);
		}
	}

	private AttackCombo populateAttackCombo(String unconditionalAttackers, String conditionalAttackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.unconditionalAttackers, unconditionalAttackers);
		helper_add_attackers(ac.attackersThroughEnemyPawn, conditionalAttackers);
		return ac;
	}

	private AttackCombo populateAttackCombo(String attackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.unconditionalAttackers, attackers);
		return ac;
	}

	@Test
	void test_calculateGain_pureAttacks() {
		AttackCombo att, def;
		/// tests fair captures and recaptures with no backstabbed pawns
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		assertEquals(100, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('P');
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		assertEquals(300, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('P');
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('K');
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('B');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('B');
		def.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo('P', 'K');
		def = populateAttackCombo('Q', 'R');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = populateAttackCombo('P', 'K');
		def = populateAttackCombo('Q', 'R');
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo('P', 'B', 'K');
		def = populateAttackCombo('Q', 'R');
		assertEquals(300, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo('Q', 'R');
		def = populateAttackCombo('P', 'R');
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("NRQBQK");
		def = populateAttackCombo("NBQRQR");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = populateAttackCombo("NRQBQK");
		def = populateAttackCombo("NBQRQR");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("NRQBQK");
		def = populateAttackCombo("NBQRQR");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));

		att = populateAttackCombo("NRQBQK");
		def = populateAttackCombo("NBQRQR");
		assertEquals(700, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));

		att = populateAttackCombo("RQBQK");
		def = populateAttackCombo("NBQRQR");
		assertEquals(500, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));

		att = populateAttackCombo("PRQQBRK");
		def = populateAttackCombo("PNQRR");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = populateAttackCombo("PRQQBRK");
		def = populateAttackCombo("PNQRR");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("PN");
		def = populateAttackCombo("NNR");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("QRBR");
		def = populateAttackCombo("RRQ");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		/// adding backstabbed pawns
		att = populateAttackCombo("P");
		def = populateAttackCombo("", "B");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("PK");
		def = populateAttackCombo("", "B");
		assertEquals(300, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("P");
		def = populateAttackCombo("K", "B");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("PK");
		def = populateAttackCombo("K", "B");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("P", "Q");
		def = populateAttackCombo("PK", "B");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("PN");
		def = populateAttackCombo("R", "B");
		assertEquals(900, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));

		att = populateAttackCombo("PN");
		def = populateAttackCombo("R", "Q");
		assertEquals(1000, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));

		att = populateAttackCombo("PNB");
		def = populateAttackCombo("R", "Q");
		assertEquals(300, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		att = populateAttackCombo("RQ", "B");
		def = populateAttackCombo("PN");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		// edge case sanity check

		att = populateAttackCombo("");
		def = populateAttackCombo("");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = populateAttackCombo("", "B");
		def = populateAttackCombo("P");
		assertEquals(0, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));

		att = populateAttackCombo("P", "B");
		def = populateAttackCombo("P", "Q");
		assertEquals(200, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));

		// Matchup sample: direct= [PPNNQBQRR] conditional= [] serialized= 7334241100
		// VS. direct= [PPQRK] conditional= [BQ] serialized= 2453400
		// natural attack sequence pay offs for targets: P=100 M=300 R=500 Q=1000
		// matchupKey=355612044000002453400
		att = populateAttackCombo("PPNNQBQRR");
		def = populateAttackCombo("PPQRK", "BQ");
		assertEquals(300, MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn,
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
	}
	
	@Test
	void testMatchUps() {
		AttackCombo att, def;
		ComboMatchUp matchup;
		
		att = new AttackCombo();
		def = new AttackCombo();
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=0 R=0 Q=0 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . . . . \n"
				+ "Q . . . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P x . . . \n"
				+ "M x . . . \n"
				+ "R x . . . \n"
				+ "Q x . . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('B');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . x . . \n"
				+ "M . x . . \n"
				+ "R . x . . \n"
				+ "Q . x . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . x . . \n"
				+ "M . x . . \n"
				+ "R . x . . \n"
				+ "Q . x . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . x . \n"
				+ "M . . x . \n"
				+ "R . . x . \n"
				+ "Q . . x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('Q');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . x \n"
				+ "M . . . x \n"
				+ "R . . . x \n"
				+ "Q . . . x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('K');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . . . . \n"
				+ "Q . . . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));

		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('Q');
		att.unconditionalAttackers.add('K');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P x x x x \n"
				+ "M x x x x \n"
				+ "R x x x x \n"
				+ "Q x x x x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('Q');
		att.unconditionalAttackers.add('K');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P x x . x \n"
				+ "M x x . x \n"
				+ "R x x . x \n"
				+ "Q x x . x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
//attacks and defences
		
		att = new AttackCombo();//8/1k6/5n2/3r2r1/8/2NRN3/8/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . x . . \n"
				+ "M . x x . \n"
				+ "R . x x . \n"
				+ "Q . x x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/1k6/5n2/3b2r1/8/3RN3/3R4/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . x . . \n"
				+ "M . x x . \n"
				+ "R . x x . \n"
				+ "Q . x x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/7k/5n2/1Q1b2r1/8/3R4/3R4/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('Q');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=100 R=300 Q=800 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . x . \n"
				+ "R . . x . \n"
				+ "Q . . x x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/4n2k/5n2/1Q1b2r1/8/3R4/3R4/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('Q');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=0 R=0 Q=500 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . . . . \n"
				+ "Q . . x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//3r4/1b6/2p5/3b4/8/1BN1N3/8/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('B');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('B');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=0 R=200 Q=700 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . x . . \n"
				+ "Q . x . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//3r4/1B6/2p5/3b4/8/4N3/8/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.attackersThroughEnemyPawn.add('B');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=0 R=200 Q=700 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . x . . \n"
				+ "Q . x . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//Q2r4/1B6/2p5/3b4/8/4N3/8/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.attackersThroughEnemyPawn.add('B');
		att.attackersThroughEnemyPawn.add('Q');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('R');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=100 R=300 Q=800 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . x . . \n"
				+ "R . x . . \n"
				+ "Q . x . . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/8/8/1R1r4/4P3/5q2/6b1/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('R');
		def.attackersThroughEnemyPawn.add('Q');
		def.attackersThroughEnemyPawn.add('B');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P x . x . \n"
				+ "M x . x . \n"
				+ "R x . x . \n"
				+ "Q x . x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/8/8/1R1r4/4P3/5q2/6b1/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('R');
		def.attackersThroughEnemyPawn.add('B');
		def.attackersThroughEnemyPawn.add('Q');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=200 R=400 Q=900 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . x . \n"
				+ "M x . x . \n"
				+ "R x . x . \n"
				+ "Q x . x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/8/8/RQ1r4/4P3/8/6b1/7q w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('Q');
		att.unconditionalAttackers.add('R');
		def.attackersThroughEnemyPawn.add('B');
		def.attackersThroughEnemyPawn.add('Q');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=100 M=300 R=500 Q=1000 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P x . x x \n"
				+ "M x . x x \n"
				+ "R x . x x \n"
				+ "Q x . x x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/8/8/RQ1p4/4P3/4n3/6b1/7q w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('Q');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('N');
		def.attackersThroughEnemyPawn.add('B');
		def.attackersThroughEnemyPawn.add('Q');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=200 R=400 Q=900 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M x . . . \n"
				+ "R x . x . \n"
				+ "Q x . x x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//8/8/5N2/RQ1p4/4Pn2/4n3/6b1/7q w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('P');
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('Q');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('N');
		def.attackersThroughEnemyPawn.add('B');
		def.attackersThroughEnemyPawn.add('Q');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=200 R=400 Q=900 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M x x . . \n"
				+ "R x x x . \n"
				+ "Q x x x x \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
		
		att = new AttackCombo();//6q1/5b2/2k1p3/3r2RQ/8/4N3/8/8 w - - 0 1
		def = new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('R');
		att.unconditionalAttackers.add('Q');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('B');
		def.unconditionalAttackers.add('Q');
		def.unconditionalAttackers.add('K');
		matchup = new ComboMatchUp(att, def);
		assertEquals("natural attack payoffs per target: P=0 M=0 R=200 Q=700 matchupKey=0\n"
				+ "  P M R Q  - attackers\n"
				+ "P . . . . \n"
				+ "M . . . . \n"
				+ "R . x . . \n"
				+ "Q . x x . \n",
				matchup.toVerboseString().substring(matchup.toVerboseString().indexOf('\n') + 1));
	}
}
