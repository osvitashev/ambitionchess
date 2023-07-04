package seecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.MovePool;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import seecontrol.AttackSetData.AttackSetType;

public class SEEControlEvaluatorTest {
	@Test
	void testPopulateKingAttacks() {
		Gamestate brd;
		SEEControlEvaluator seval = new SEEControlEvaluator();

		brd = new Gamestate("8/8/1kp1P3/1N3K2/8/8/8/8 w - - 0 1");
		seval.initialize();
		seval.populateKingAttacks(brd);
		
		int asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KING);
		asData = AttackSetData.setSquare(asData, Square.F5);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(asData, seval.getSetData(Player.WHITE, 0));
		assertEquals(0x705070000000L, seval.getSet(Player.WHITE, 0));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KING);
		asData = AttackSetData.setSquare(asData, Square.B6);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x7050700000000L, seval.getSet(Player.BLACK, 0));
		
		brd = new Gamestate("K7/pr6/5N2/8/6pk/7P/8/8 w - - 0 1");
		seval.initialize();
		seval.populateKingAttacks(brd);
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KING);
		asData = AttackSetData.setSquare(asData, Square.A8);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(asData, seval.getSetData(Player.WHITE, 0));
		assertEquals(0x203000000000000L, seval.getSet(Player.WHITE, 0));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KING);
		asData = AttackSetData.setSquare(asData, Square.H4);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0xc040c00000L, seval.getSet(Player.BLACK, 0));
	}
	
	@Test
	/**
	 * tests that every piece and every attack set type is represented in the evaluator
	 */
	void testSanity() {
		//also test re-initialization
		
	}
}
