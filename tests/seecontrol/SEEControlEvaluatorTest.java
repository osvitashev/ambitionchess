package seecontrol;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
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
	
	private RuntimeException comparePairwise(ArrayList<Pair<Integer, Long>> expected, ArrayList<Pair<Integer, Long>> actual) {
		for(Pair<Integer, Long> val : actual) {
			if(!expected.contains(val))
				return new RuntimeException("Unmatched element in the actual collection: {" + AttackSetData.toString(val.getValue0()) + " -> "
			+ Long.toHexString(val.getValue1()) + "}");
		}
		
		for(Pair<Integer, Long> val : expected) {
			if(!actual.contains(val))
				return new RuntimeException("Unmatched element in the expected collection: {" + AttackSetData.toString(val.getValue0()) + " -> " 
			+ Long.toHexString(val.getValue1()) + "}");
		}
		
		HashMap<Pair<Integer, Long>, Integer> expectedFrequency = new HashMap<>();
        for (Pair<Integer, Long> element : expected)
        	expectedFrequency.put(element, expectedFrequency.getOrDefault(element, 0) + 1);
        
        HashMap<Pair<Integer, Long>, Integer> actualFrequency = new HashMap<>();
        for (Pair<Integer, Long> element : actual)
        	actualFrequency.put(element, actualFrequency.getOrDefault(element, 0) + 1);
        
        if(!expectedFrequency.equals(actualFrequency))
        	return new RuntimeException("Unmatched element frequencies...");
		
		return null;
	}
	
	@SuppressWarnings("unchecked")//needed for Pair<Integer, Long> initialization
	@Test
	void testComparePairwise() {
		ArrayList<Pair<Integer, Long>> expected=new ArrayList<Pair<Integer, Long>>();
		ArrayList<Pair<Integer, Long>> actual=new ArrayList<Pair<Integer, Long>>();
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		assertNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		assertNotNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		assertNotNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(3, 1L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		assertNotNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(1, 2L));
		assertNotNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(5, 2L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		assertNotNull(comparePairwise(expected, actual));
		
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(3, 3L));
		actual.add(new Pair<Integer, Long>(5, 2L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		assertNotNull(comparePairwise(expected, actual));
		
		
		// {1, 2, 1} =? {1, 2, 2}
		expected.clear();
		actual.clear();
		expected.add(new Pair<Integer, Long>(1, 1L));
		expected.add(new Pair<Integer, Long>(2, 2L));
		expected.add(new Pair<Integer, Long>(1, 1L));
		actual.add(new Pair<Integer, Long>(1, 1L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		actual.add(new Pair<Integer, Long>(2, 2L));
		assertNotNull(comparePairwise(expected, actual));
	}
	
	/**
	 * THIS DOES NOT WORK FOR PAWN ATTACKS AND PAWN PUSHES, BECAUSE THE SQUARE_FROM IS MEANINGLESS!
	 */
	private void assertAttackSets(SEEControlEvaluator seeval, int square, int player, Pair<Integer, Long>[] expectedArray) {
		ArrayList<Pair<Integer, Long>> actual = new ArrayList<Pair<Integer, Long>>();
		ArrayList<Pair<Integer, Long>> expected = new ArrayList<Pair<Integer, Long>>(Arrays.asList(expectedArray));
		for(int i=0;i<seeval.getSetSize(player); ++i)
			if(AttackSetData.getSquare(seeval.getAttackSetData(player, i)) == square &&
					AttackSetData.getPieceType(seeval.getAttackSetData(player, i)) != PieceType.PAWN)//needed to prevent incorrect behavior of A1 aka Square(0)
				actual.add(new Pair<Integer, Long>(seeval.getAttackSetData(player, i), seeval.getAttackSet(player, i)));
		RuntimeException re = comparePairwise(expected, actual);
		if(re != null)
			throw re;
	}
	
	@SuppressWarnings("unchecked")//needed for Pair<Integer, Long> initialization
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
		brd = new Gamestate("4b1r1/1Qpp1p2/1nrk4/8/5R2/2PQNP2/1K2P2R/8 b - - 0 1");
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
		
		//better assertion pattern
		brd = new Gamestate("6r1/2k1pp2/3p2pp/1P3r2/2P2P2/3PP3/1K6/3R4 w - - 0 1");
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.F5, Player.BLACK, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.F5,
						Player.BLACK,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x2020de20000000L)
				});
		
		brd = new Gamestate("8/2k5/8/8/8/8/KR1r1r2/8 w - - 0 1");//Rrr
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x202020202020d02L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0x3000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK)+AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0xc000L),
				});
		
		brd = new Gamestate("8/2k5/8/8/8/8/KR1R1r2/8 w - - 0 1");//RRr
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x202020202020d02L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), /*sunkenCost */
						0 /*opponentSunkenCost */),
						0x3000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0xc000L),
				});
		
		brd = new Gamestate("8/2k5/8/8/8/8/KR1R1R2/8 w - - 0 1");//RRR
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x202020202020d02L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), /*sunkenCost */
						0 /*opponentSunkenCost */),
						0x3000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK)+AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK), /*sunkenCost */
						0 /*opponentSunkenCost */),
						0xc000L),
				});

		brd = new Gamestate("8/2k5/8/8/8/8/KR1r1R2/8 w - - 0 1");//RrR - special case
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x202020202020d02L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0x3000L),
				});
				
		brd = new Gamestate("8/2k5/8/8/8/8/KR1r1r2/8 w - - 0 1");//Rrr
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x202020202020d02L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0x3000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK)+AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */),
						0xc000L),
				});
		
		brd = new Gamestate("8/2k5/8/8/8/8/KR5r/8 w - - 0 1");//blocker on the edge of the board
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.B2, Player.WHITE, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.B2,
						Player.WHITE,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x20202020202fd02L),
				});
		//rooks with queens
		brd = new Gamestate("7k/1q4pp/r1Q1Q3/8/q1Q5/8/q5PP/7K w - - 0 1");//RQQ Rqq
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.A6, Player.BLACK, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x101060101000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) /*opponentSunkenCost */), 0x180000000000l),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN)+AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) /*opponentSunkenCost */), 0xe00000000000l),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						0/*opponentSunkenCost */), 0x10100l),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN)+AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						0/*opponentSunkenCost */), 0x1l),
				});
		
		brd = new Gamestate("7k/1q4pp/r1q1Q3/8/Q1Q5/8/q5PP/7K w - - 0 1");//RqQ RQq
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.A6, Player.BLACK, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x101060101000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						0 /*opponentSunkenCost */), 0x180000000000l),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) /*opponentSunkenCost */), 0xe00000000000l),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A6,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN)/*opponentSunkenCost */), 0x10100l),
				});
		
		brd = new Gamestate("7k/6pp/8/2R5/2Q5/2rRQ3/6PP/7K w - - 0 1");//Rrq Rqr
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.C3, Player.BLACK, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.C3,
						Player.BLACK,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x40b0404L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.C3,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) /*opponentSunkenCost */), 0x400000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.C3,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) +AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */), 0x404040000000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.C3,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */), 0x100000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.C3,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) +AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */), 0xe00000L)
				});
		
		///next
		brd = new Gamestate("7k/r1qR3p/8/8/Q7/8/r5PP/7K w - - 0 1");//RrR RQr
		seval.initialize();
		seval.populateRookAttacks(brd);
		assertAttackSets(seval, Square.A7, Player.BLACK, new Pair[] {
				Pair.with(createASData(
						AttackSetType.DIRECT,
						PieceType.ROOK,
						Square.A7,
						Player.BLACK,
						0, /*sunkenCost */
						0 /*opponentSunkenCost */), 0x106010101000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A7,
						Player.BLACK,
						0, /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN) /*opponentSunkenCost */), 0x10100L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A7,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						0 /*opponentSunkenCost */), 0x8000000000000L),
				Pair.with(createASData(
						AttackSetType.INDIRECT,
						PieceType.ROOK,
						Square.A7,
						Player.BLACK,
						AttackSetData.OrderingPieceWeights.getValue(PieceType.QUEEN), /*sunkenCost */
						AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK) /*opponentSunkenCost */), 0xf0000000000000L),
				});

		//use Black as the perspective for attacks through queen/pawns
		
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
