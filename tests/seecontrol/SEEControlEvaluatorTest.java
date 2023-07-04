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
	void testPopulateKnightAttacks() {
		Gamestate brd;
		SEEControlEvaluator seval = new SEEControlEvaluator();

		brd = new Gamestate("r3r1q1/1pppnk1p/p5p1/5n2/8/1PPP2P1/P1K1B2P/R6R w - - 0 1");
		seval.initialize();
		seval.populateKnightAttacks(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(2, seval.getSetSize(Player.BLACK));
		int asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
		asData = AttackSetData.setSquare(asData, Square.F5);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x50880088500000L, seval.getSet(Player.BLACK, 0));
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
		asData = AttackSetData.setSquare(asData, Square.E7);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(asData, seval.getSetData(Player.BLACK, 1));
		assertEquals(0x4400442800000000L, seval.getSet(Player.BLACK, 1));
		
		brd = new Gamestate("3q3r/2bppk2/2p2p1p/2P3p1/N2PP3/2N1QP2/2K3B1/4R3 w - - 0 1");
		seval.initialize();
		seval.populateKnightAttacks(brd);
		assertEquals(2, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
		asData = AttackSetData.setSquare(asData, Square.C3);
		assertEquals(asData, seval.getSetData(Player.WHITE, 0));
		assertEquals(0xa1100110aL, seval.getSet(Player.WHITE, 0));
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
		asData = AttackSetData.setSquare(asData, Square.A4);
		assertEquals(asData, seval.getSetData(Player.WHITE, 1));
		assertEquals(0x20400040200L, seval.getSet(Player.WHITE, 1));
		
	}
	
	@Test
	void testPopulatePawnPushes() {
		Gamestate brd;
		SEEControlEvaluator seval = new SEEControlEvaluator();
		
		//case mostly blocked by friendly pieces. no doubled pawns
		brd = new Gamestate("3q3r/p2pp2p/2p1kp2/b1P3p1/N2PP3/2N1QPB1/PPK3P1/4R3 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		
		int asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		assertEquals(asData, seval.getSetData(Player.WHITE, 0));
		assertEquals(0x1822030000L, seval.getSet(Player.WHITE, 0));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(151355721252864L, seval.getSet(Player.BLACK, 0));
		
		//white has no pawn pushes
		brd = new Gamestate("5rk1/5pbp/3p1p2/7p/N2p4/P7/PRN5/K7 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x802880080000L, seval.getSet(Player.BLACK, 0));
		
		//no pawn pushes for either side
		brd = new Gamestate("8/1p4p1/1n1p2p1/3k2b1/4R3/2RNP3/3K4/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));
		
		//no pawns for either side
		brd = new Gamestate("8/3k4/8/8/8/8/1K6/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));
		
		//pawns pushing for last rank
		brd = new Gamestate("4rq2/3p3P/1np2p2/3k3p/p2P1P1P/1PK2P1N/P1P2Qp1/2R5 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		assertEquals(asData, seval.getSetData(Player.WHITE, 0));
		assertEquals(0x8000002002010000L, seval.getSet(Player.WHITE, 0));
		
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		asData = AttackSetData.setPlayer(asData);//black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x82400010040L, seval.getSet(Player.BLACK, 0));
	}
	
	@Test
	/**
	 * tests that every piece and every attack set type is represented in the evaluator
	 */
	void testSanity() {
		//also test re-initialization
		
	}
}
