package codegenerators;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;


class MyLookupGeneratorTest {

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
	
	AttackCombo populateAttackCombo(String attackers) {
		AttackCombo ac = new AttackCombo();
		int a=-1;
		for(int i=0; i<attackers.length(); ++i) {
			if(attackers.charAt(i) == 'P')
				a=PieceType.PAWN;
			else if(attackers.charAt(i) == 'R')
				a=PieceType.ROOK;
			else if(attackers.charAt(i) == 'B')
				a=PieceType.BISHOP;
			else if(attackers.charAt(i) == 'N')
				a=PieceType.KNIGHT;
			else if(attackers.charAt(i) == 'Q')
				a=PieceType.QUEEN;
			else if(attackers.charAt(i) == 'K')
				a=PieceType.KING;
			else
				throw new RuntimeException("unexpected value!");
			assert PieceType.validate(a);
			ac.attackers.add(a);
		}
		return ac;
	}
	
	
	@Test
	void test_calculateGain_pureAttacks() {
		AttackCombo att, def;
		
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
	}
}
