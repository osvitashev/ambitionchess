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

}
