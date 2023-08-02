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
		
		assertFalse(w.get(2765280));
		w.lock(2765280);
		thrown = assertThrows(RuntimeException.class, () -> w.setPayload(2765280), "index is unavailable!");
		assertTrue(thrown.getMessage().equals("index is unavailable!"));
		
		
	}

	@Test
	void testAttackComboOperations() {
		AttackCombo ac = new AttackCombo();
		ac.attackers.add(PieceType.KNIGHT);
		ac.attackers.add(PieceType.ROOK);
		ac.attackers.add(PieceType.KING);
		ac.setSerialized();
		assertEquals("direct= [NRK] conditional= [] serialized= 531", ac.toString());
		
		ac = new AttackCombo();
		ac.attackers.add(PieceType.PAWN);
		ac.attackers.add(PieceType.PAWN);
		ac.attackers.add(PieceType.ROOK);
		ac.attackers.add(PieceType.ROOK);
		ac.attackers.add(PieceType.QUEEN);
		ac.attackers.add(PieceType.KING);
		ac.attackersThroughEnemyPawn.add(PieceType.BISHOP);
		ac.attackersThroughEnemyPawn.add(PieceType.QUEEN);
		ac.setSerialized();
		assertEquals("direct= [PPRRQK] conditional= [BQ] serialized= 24543300", ac.toString());
		
		ac = new AttackCombo();
		ac.attackers.add(PieceType.KNIGHT);
		ac.attackers.add(PieceType.BISHOP);
		ac.attackers.add(PieceType.ROOK);
		ac.attackers.add(PieceType.QUEEN);
		ac.attackers.add(PieceType.ROOK);
		ac.attackers.add(PieceType.KING);
		ac.attackersThroughEnemyPawn.add(PieceType.QUEEN);
		ac.setSerialized();
		assertEquals("direct= [NBRQRK] conditional= [Q] serialized= 4534321", ac.toString());
		
		ac = new AttackCombo();
		ac.attackersThroughEnemyPawn.add(PieceType.QUEEN);
		ac.attackersThroughEnemyPawn.add(PieceType.BISHOP);
		ac.attackersThroughEnemyPawn.add(PieceType.QUEEN);
		ac.setSerialized();
		assertEquals("direct= [] conditional= [QBQ] serialized= 4247", ac.toString());
		
		ac = new AttackCombo();
		ac.setSerialized();
		assertEquals("direct= [] conditional= [] serialized= 7", ac.toString());

		ac = new AttackCombo();
		ac.attackers.add(PieceType.KING);
		ac.setSerialized();
		assertEquals("direct= [K] conditional= [] serialized= 5", ac.toString());
		
		ac = new AttackCombo();
		ac.attackers.add(PieceType.KING);
		ac.attackersThroughEnemyPawn.add(PieceType.BISHOP);
		ac.setSerialized();
		assertEquals("direct= [K] conditional= [B] serialized= 25", ac.toString());
		
		ac = new AttackCombo();
		ac.attackers.add(PieceType.PAWN);
		ac.attackers.add(PieceType.PAWN);
		ac.setSerialized();
		assertEquals("direct= [PP] conditional= [] serialized= 700", ac.toString());
	}
	
	
	AttackCombo populateAttackCombo(int ...attackers) {
		AttackCombo ac = new AttackCombo();
		for(int a : attackers) {
			assert PieceType.validate(a);
			ac.attackers.add(a);
		}
		return ac;
	}
	
	
	void helper_add_attackers(ArrayList<Integer> col, String str) {
		int a=-1;
		for(int i=0; i<str.length(); ++i) {
			if(str.charAt(i) == 'P')
				a=PieceType.PAWN;
			else if(str.charAt(i) == 'R')
				a=PieceType.ROOK;
			else if(str.charAt(i) == 'B')
				a=PieceType.BISHOP;
			else if(str.charAt(i) == 'N')
				a=PieceType.KNIGHT;
			else if(str.charAt(i) == 'Q')
				a=PieceType.QUEEN;
			else if(str.charAt(i) == 'K')
				a=PieceType.KING;
			else
				throw new RuntimeException("unexpected value!");
			assert PieceType.validate(a);
			col.add(a);
		}
	}
	
	AttackCombo populateAttackCombo(String attackers, String conditionalAttackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.attackers, attackers);
		helper_add_attackers(ac.attackersThroughEnemyPawn, conditionalAttackers);
		return ac;
	}
	
	AttackCombo populateAttackCombo(String attackers) {
		AttackCombo ac = new AttackCombo();
		helper_add_attackers(ac.attackers, attackers);
		return ac;
	}
	
	
	@Test
	void test_calculateGain_pureAttacks() {
		AttackCombo att, def;
		///tests fair captures and recaptures with no backstabbed pawns
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.ROOK);
		assertEquals(100 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.KNIGHT);
		def.attackers.add(PieceType.PAWN);
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.KNIGHT);
		att.attackers.add(PieceType.KING);
		def.attackers.add(PieceType.PAWN);
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.KNIGHT);
		att.attackers.add(PieceType.KING);
		def.attackers.add(PieceType.PAWN);
		def.attackers.add(PieceType.PAWN);
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.KNIGHT);
		att.attackers.add(PieceType.KING);
		def.attackers.add(PieceType.PAWN);
		def.attackers.add(PieceType.KING);
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.BISHOP);
		att.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=new AttackCombo();
		def=new AttackCombo();
		att.attackers.add(PieceType.KNIGHT);
		att.attackers.add(PieceType.BISHOP);
		def.attackers.add(PieceType.ROOK);
		def.attackers.add(PieceType.KING);
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo(PieceType.PAWN, PieceType.KING);
		def=populateAttackCombo(PieceType.QUEEN, PieceType.ROOK);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=populateAttackCombo(PieceType.PAWN ,PieceType.KING);
		def=populateAttackCombo(PieceType.QUEEN, PieceType.ROOK);
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo(PieceType.PAWN, PieceType.BISHOP ,PieceType.KING);
		def=populateAttackCombo(PieceType.QUEEN, PieceType.ROOK);
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo(PieceType.QUEEN, PieceType.ROOK);
		def=populateAttackCombo(PieceType.PAWN, PieceType.ROOK);
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 500));
		
		att=populateAttackCombo("NRQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(700 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 1000));
		
		att=populateAttackCombo("RQBQK");
		def=populateAttackCombo("NBQRQR");
		assertEquals(500 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 1000));
		
		att=populateAttackCombo("PRQQBRK");
		def=populateAttackCombo("PNQRR");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=populateAttackCombo("PRQQBRK");
		def=populateAttackCombo("PNQRR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("NNR");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("QRBR");
		def=populateAttackCombo("RRQ");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		///adding backstabbed pawns
		att=populateAttackCombo("P");
		def=populateAttackCombo("", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("PK");
		def=populateAttackCombo("", "B");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("P");
		def=populateAttackCombo("K", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("PK");
		def=populateAttackCombo("K", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("P", "Q");
		def=populateAttackCombo("PK", "B");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("R", "B");
		assertEquals(900 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 1000));
		
		att=populateAttackCombo("PN");
		def=populateAttackCombo("R", "Q");
		assertEquals(1000 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 1000));
		
		att=populateAttackCombo("PNB");
		def=populateAttackCombo("R", "Q");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		att=populateAttackCombo("RQ", "B");
		def=populateAttackCombo("PN");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		//edge case sanity check
		
		att=populateAttackCombo("");
		def=populateAttackCombo("");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=populateAttackCombo("", "B");
		def=populateAttackCombo("P");
		assertEquals(0 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 100));
		
		att=populateAttackCombo("P", "B");
		def=populateAttackCombo("P", "Q");
		assertEquals(200 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
		
		//Matchup sample: direct= [PPNNQBQRR] conditional= [] serialized= 7334241100 VS. direct= [PPQRK] conditional= [BQ] serialized= 2453400
		//natural attack sequence pay offs for targets: P=100 M=300 R=500 Q=1000 matchupKey=355612044000002453400
		att=populateAttackCombo("PPNNQBQRR");
		def=populateAttackCombo("PPQRK", "BQ");
		assertEquals(300 , MyLookupGenerator.calculateGain_pureAttacks(att, def, 300));
	}
}
