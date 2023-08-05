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
	
	
	AttackCombo populateAttackCombo(Character ...attackers) {
		AttackCombo ac = new AttackCombo();
		for(Character a : attackers) {
			ac.unconditionalAttackers.add(a);
		}
		return ac;
	}
	
	
	void helper_add_attackers(ArrayList<Character> col, String str) {
		Character a = null;
		for(int i=0; i<str.length(); ++i) {
			if(str.charAt(i) == 'P')
				a='P';
			else if(str.charAt(i) == 'R')
				a='R';
			else if(str.charAt(i) == 'B')
				a='B';
			else if(str.charAt(i) == 'N')
				a='N';
			else if(str.charAt(i) == 'Q')
				a='Q';
			else if(str.charAt(i) == 'K')
				a='K';
			else
				throw new RuntimeException("unexpected value!");
			col.add(a);
		}
	}
	
	AttackCombo populateAttackCombo(String attackers, String conditionalAttackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.unconditionalAttackers, attackers);
		helper_add_attackers(ac.attackersThroughEnemyPawn, conditionalAttackers);
		return ac;
	}
	
	AttackCombo populateAttackCombo(String attackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.unconditionalAttackers, attackers);
		return ac;
	}
	
	
	@Test
	void test_calculateGain_pureAttacks() {
		AttackCombo att, def;
		///tests fair captures and recaptures with no backstabbed pawns
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('R');
		assertEquals(100 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, 
				def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('N');
		def.unconditionalAttackers.add('P');
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('P');
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('K');
		def.unconditionalAttackers.add('P');
		def.unconditionalAttackers.add('K');
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('B');
		att.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.unconditionalAttackers.add('N');
		att.unconditionalAttackers.add('B');
		def.unconditionalAttackers.add('R');
		def.unconditionalAttackers.add('K');
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo('P', 'K');
		def=populateAttackCombo('Q', 'R');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=populateAttackCombo('P' ,'K');
		def=populateAttackCombo('Q', 'R');
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo('P', 'B' ,'K');
		def=populateAttackCombo('Q', 'R');
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo('Q', 'R');
		def=populateAttackCombo('P', 'R');
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 500));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(700 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));
		
		att=populateAttackCombo("RQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(500 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));
		
		att=populateAttackCombo("PRQQBRK");
		def=populateAttackCombo("PNQRR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=populateAttackCombo("PRQQBRK");
		def=populateAttackCombo("PNQRR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("NNR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("QRBR");
		def=populateAttackCombo("RRQ");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		///adding backstabbed pawns
		att=populateAttackCombo("P");
		def=populateAttackCombo("", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("PK");
		def=populateAttackCombo("", "B");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("P");
		def=populateAttackCombo("K", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("PK");
		def=populateAttackCombo("K", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("P", "Q");
		def=populateAttackCombo("PK", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("R", "B");
		assertEquals(900 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("R", "Q");
		assertEquals(1000 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 1000));
		
		att=populateAttackCombo("PNB");
		def=populateAttackCombo("R", "Q");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		att=populateAttackCombo("RQ", "B");
		def=populateAttackCombo("PN");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		//edge case sanity check
		
		att=populateAttackCombo("");
		def=populateAttackCombo("");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=populateAttackCombo("", "B");
		def=populateAttackCombo("P");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 100));
		
		att=populateAttackCombo("P", "B");
		def=populateAttackCombo("P", "Q");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
		
		//Matchup sample: direct= [PPNNQBQRR] conditional= [] serialized= 7334241100 VS. direct= [PPQRK] conditional= [BQ] serialized= 2453400
		//natural attack sequence pay offs for targets: P=100 M=300 R=500 Q=1000 matchupKey=355612044000002453400
		att=populateAttackCombo("PPNNQBQRR");
		def=populateAttackCombo("PPQRK", "BQ");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att.unconditionalAttackers, att.attackersThroughEnemyPawn, def.unconditionalAttackers, def.attackersThroughEnemyPawn, 300));
	}
}
