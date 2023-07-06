package seecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
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
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
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
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
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
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x50880088500000L, seval.getSet(Player.BLACK, 0));
		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
		asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
		asData = AttackSetData.setSquare(asData, Square.E7);
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
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

		// case mostly blocked by friendly pieces. no doubled pawns
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
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(151355721252864L, seval.getSet(Player.BLACK, 0));

		// white has no pawn pushes
		brd = new Gamestate("5rk1/5pbp/3p1p2/7p/N2p4/P7/PRN5/K7 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));

		asData = 0;
		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x802880080000L, seval.getSet(Player.BLACK, 0));

		// no pawn pushes for either side
		brd = new Gamestate("8/1p4p1/1n1p2p1/3k2b1/4R3/2RNP3/3K4/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));

		// no pawns for either side
		brd = new Gamestate("8/3k4/8/8/8/8/1K6/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));

		// pawns pushing for last rank
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
		asData = AttackSetData.setPlayer(asData, Player.BLACK);// black
		assertEquals(asData, seval.getSetData(Player.BLACK, 0));
		assertEquals(0x82400010040L, seval.getSet(Player.BLACK, 0));
	}

	private int createPawnAttackSetData_TypePlayer(int type, int player) {
		int asData = 0;
		asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
		asData = AttackSetData.setAttackSetType(asData, type);
		asData = AttackSetData.setPlayer(asData, player);
		return asData;
	}

	@Test
	void testPopulatePawnAttacks() {
		Gamestate brd;
		SEEControlEvaluator seval = new SEEControlEvaluator();

		brd = new Gamestate("8/1nk5/2q5/8/8/3Q4/3KN3/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnPushes(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));

		brd = new Gamestate("8/1nk4p/2q4P/8/8/3Q3P/3KN3/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnAtacks(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.WHITE), seval.getSetData(Player.WHITE, 0));
		assertEquals(0x40000040000000L, seval.getSet(Player.WHITE, 0));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.BLACK), seval.getSetData(Player.BLACK, 0));
		assertEquals(0x400000000000L, seval.getSet(Player.BLACK, 0));

		brd = new Gamestate("8/pnk5/p1q5/P7/8/3Q4/3KN3/8 w - - 0 1");
		seval.initialize();
		seval.populatePawnAtacks(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.WHITE), seval.getSetData(Player.WHITE, 0));
		assertEquals(0x20000000000L, seval.getSet(Player.WHITE, 0));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.BLACK), seval.getSetData(Player.BLACK, 0));
		assertEquals(0x20200000000L, seval.getSet(Player.BLACK, 0));
		
		brd = new Gamestate("1r6/3qkppp/p1p1pn2/1pPp3B/3P2P1/1PP1PP1P/P3KQ2/3R4 w - - 0 1");
		seval.initialize();
		seval.populatePawnAtacks(brd);
		assertEquals(2, seval.getSetSize(Player.WHITE));
		assertEquals(2, seval.getSetSize(Player.BLACK));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.WHITE), seval.getSetData(Player.WHITE, 0));
		assertEquals(0x8906c020000L, seval.getSet(Player.WHITE, 0));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.WHITE), seval.getSetData(Player.WHITE, 1));
		assertEquals(0x2245b000000L, seval.getSet(Player.WHITE, 1));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.BLACK), seval.getSetData(Player.BLACK, 0));
		assertEquals(0xc02a14000000L, seval.getSet(Player.BLACK, 0));
		assertEquals(createPawnAttackSetData_TypePlayer(AttackSetType.PAWN_ATTACK, Player.BLACK), seval.getSetData(Player.BLACK, 1));
		assertEquals(0x700a05000000L, seval.getSet(Player.BLACK, 1));

	}
	
	/**
	 * does not work for pawn attacks, which do not have a unique asData identifier.
	 */
	private long getAttackSetByData(SEEControlEvaluator seeval, int asData) {
		int player = AttackSetData.getPlayer(asData);
		for(int i=0; i<seeval.getSetSize(player); ++i)
			if(asData == seeval.getAttackSetData(player, i))
				return seeval.getAttackSet(player, i);
		throw new RuntimeException("Failed to locate AttackSet: " + AttackSetData.toString(asData));
	}
	
	private int createASData(int asType, int pieceType, int square, int player, int sunkenCost, int oppSunkenCost) {
		int asData = 0;
		asData = AttackSetData.setAttackSetType(asData, asType);
		asData = AttackSetData.setPieceType(asData, pieceType);
		asData = AttackSetData.setSquare(asData, square);
		asData = AttackSetData.setPlayer(asData, player);
		asData = AttackSetData.setSunkenCost(asData, sunkenCost);
		asData = AttackSetData.setOppontntSunkenCost(asData, oppSunkenCost);
		return asData;
	}
	
	@Test
	void testPopulateRookAttacks() {
		Gamestate brd;
		SEEControlEvaluator seval = new SEEControlEvaluator();
		
		brd = new Gamestate("8/2pp4/3k4/8/8/5P2/1K2P3/8 w - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertEquals(0, seval.getSetSize(Player.WHITE));
		assertEquals(0, seval.getSetSize(Player.BLACK));
		//direct attacks; no batteries
		brd = new Gamestate("8/2pp4/2rk4/8/6R1/5P2/1K2P3/8 w - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(0x40404040bf404040L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.G4,
								Player.WHITE,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x40b0404040404L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.C6,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		
		brd = new Gamestate("8/2n1Pk2/8/1pr1B3/8/3nR1K1/4P3/8 w - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertEquals(1, seval.getSetSize(Player.WHITE));
		assertEquals(1, seval.getSetSize(Player.BLACK));
		assertEquals(0x1010681000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.E3,
								Player.WHITE,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x4041a04040404L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.C5,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		
		//two rooks per side, no batteries
		brd = new Gamestate("4q1r1/1Qpp1p2/1nrk4/8/5R2/2PQNP2/1K2P2R/8 b - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertEquals(2, seval.getSetSize(Player.WHITE));
		assertEquals(2, seval.getSetSize(Player.BLACK));
		assertEquals(0x8080808080807080L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.H2,
								Player.WHITE,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x202020df200000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.F4,
								Player.WHITE,
								0, //sunkenCost
								0)));//opponent sunken cost
		
		assertEquals(0x40a0404040000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.C6,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0xb040404040404040L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.G8,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		//two rooks
		brd = new Gamestate("k7/pp6/2b2rR1/8/8/5rN1/PP6/K7 w - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertEquals(2, seval.getSetSize(Player.WHITE));
		assertEquals(5, seval.getSetSize(Player.BLACK));
		assertEquals(0x4040a04040400000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.G6,
								Player.WHITE,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x1c0000000000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.INDIRECT,
								PieceType.ROOK,
								Square.G6,
								Player.WHITE,
								0, //sunkenCost
								AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK))));//opponent sunken cost
		assertEquals(0x20205c2020200000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.F6,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x2020205f2020L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.DIRECT,
								PieceType.ROOK,
								Square.F3,
								Player.BLACK,
								0, //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x800000000000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.INDIRECT,
								PieceType.ROOK,
								Square.F6,
								Player.BLACK,
								0, //sunkenCost
								AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK))));//opponent sunken cost
		assertEquals(0x2020L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.INDIRECT,
								PieceType.ROOK,
								Square.F6,
								Player.BLACK,
								AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), //sunkenCost
								0)));//opponent sunken cost
		assertEquals(0x2020000000000000L,
				getAttackSetByData(seval,
						createASData(
								AttackSetType.INDIRECT,
								PieceType.ROOK,
								Square.F3,
								Player.BLACK,
								AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), //sunkenCost
								0)));//opponent sunken cost
		
		
		
		///next: create a set comparison function for assertions!!!
	}

	@Test
	/**
	 * tests that every piece and every attack set type is represented in the
	 * evaluator
	 */
	void testSanity() {
		// also test re-initialization

	}
}
