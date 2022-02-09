package attackpalette;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.Assert;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Board;
import gamestate.DebugLibrary;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class PlayerAttackSetTest {

	int countTotalAttackers(PlayerAttackSet aset, int player, int targetSquare) {
		DebugLibrary.validatePlayer(player);
		DebugLibrary.validateSquare(targetSquare);
		int t = 0;
		for (int i = 0; i < aset.length(player); ++i)
			if ((aset.getAttackSet(player, i).getAttacks() & Bitboard.initFromSquare(targetSquare)) != 0L)
				++t;
		return t;
	}

	int countTotalAttackers(PlayerAttackSet aset, int type, int player, int targetSquare) {
		DebugLibrary.validatePieceType(type);
		DebugLibrary.validatePlayer(player);
		DebugLibrary.validateSquare(targetSquare);
		int t = 0;
		for (int i = 0; i < aset.length(player); ++i)
			if ((aset.getAttackSet(player, i).getType() == type) && (aset.getAttackSet(player, i).getAttacks() & Bitboard.initFromSquare(targetSquare)) != 0L)
				++t;
		return t;
	}

	@Test
	void testTotalAttackCounts() {
		PlayerAttackSet aset = new PlayerAttackSet();
		// pawns
		aset.initialize(new Board("6k1/8/8/4p3/7p/4P1P1/8/2K5 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, Player.WHITE, Square.F2));
		assertEquals(1, countTotalAttackers(aset, Player.WHITE, Square.H4));
		assertEquals(2, countTotalAttackers(aset, Player.WHITE, Square.F4));
		assertEquals(1, countTotalAttackers(aset, Player.BLACK, Square.F4));
		// kings
		aset.initialize(new Board("8/8/6k1/8/8/2K5/8/8 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, Player.WHITE, Square.B5));
		assertEquals(1, countTotalAttackers(aset, Player.WHITE, Square.C2));
		assertEquals(0, countTotalAttackers(aset, Player.BLACK, Square.C2));
		assertEquals(1, countTotalAttackers(aset, Player.BLACK, Square.F6));
		// knights: 0,1,2 knights, knight+king, opposite sides
		aset.initialize(new Board("8/8/3n4/2n5/5k2/8/5N2/2K5 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, Player.WHITE, Square.E2));
		assertEquals(1, countTotalAttackers(aset, Player.WHITE, Square.H1));
		assertEquals(2, countTotalAttackers(aset, Player.WHITE, Square.D1));
		assertEquals(2, countTotalAttackers(aset, Player.BLACK, Square.B7));
		assertEquals(3, countTotalAttackers(aset, Player.BLACK, Square.E4));
		// bishops
		aset.initialize(new Board("bk6/8/2q5/8/8/8/8/1K6 w - - 0 1"));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.D5));
		aset.initialize(new Board("bk6/8/2q2pP1/5Q2/8/1Pb5/1pB1pQp1/1K2B3 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.G7));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.D1));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.D2));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.A1));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.A4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.E4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.D5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.G6));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.H7));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.H1));
		// test BQQ, BP and BQP out of range
		aset.initialize(new Board("3k4/1pppp3/1p3P2/3b1P2/8/2Q5/1B3q2/1B1K2b1 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.B1));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.C2));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.D3));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.E4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.F5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.G6));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.H7));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.B2));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.C3));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.F6));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.G7));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.H8));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.A7));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.B6));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.C5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.E3));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.F2));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.G1));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.A8));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.B7));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.C6));
		aset.initialize(new Board("1b4k1/5ppp/3q1Q2/8/3Q4/6q1/PBP5/1K6 w - - 0 1"));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.A1));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.B2));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.C3));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.F6));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.G7));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.WHITE, Square.H8));
		assertEquals(0, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.B8));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.C7));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.D6));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.F4));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.G3));
		assertEquals(1, countTotalAttackers(aset, PieceType.BISHOP, Player.BLACK, Square.H2));
		// rooks
		aset.initialize(new Board("7k/6pp/1Q2r3/8/1R6/4q3/1R2r1PP/7K w - - 0 1"));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B1));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B2));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B3));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B4));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B5));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B6));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B7));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.B8));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.A4));
		assertEquals(0, countTotalAttackers(aset, PieceType.ROOK, Player.WHITE, Square.A5));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E1));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E2));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E3));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E4));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E6));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E7));
		assertEquals(2, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E8));
		aset.initialize(new Board("7k/4q1pp/8/8/4q3/8/4r1PP/7K w - - 0 1"));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E1));
		assertEquals(0, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E2));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E3));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E4));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E6));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E7));
		assertEquals(1, countTotalAttackers(aset, PieceType.ROOK, Player.BLACK, Square.E8));
		// queen
		aset.initialize(new Board("6k1/5ppp/1q6/8/3q4/Q2R1R2/5bPP/7K w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.A3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.B3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.C3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.D3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.E3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.F3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.G3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.H3));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.A7));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.B6));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.C5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.D4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.E3));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.F2));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.G1));
		aset.initialize(new Board("6k1/5ppp/2P5/2pQ4/3qQ3/4qP2/PP3p2/K7 w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.A8));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.B7));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.C6));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.D5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.E4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.F3));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.G2));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.H1));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.A7));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.B6));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.C5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.E3));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.F2));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.G1));
		aset.initialize(new Board("1k6/ppp3P1/5Bp1/4Qb2/3Bq3/2Pb4/2p3PP/7K w - - 0 1"));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.A1));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.B2));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.C3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.D4));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.E5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.F6));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.G7));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.H8));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.B1));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.C2));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.D3));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.E4));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.F5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.G6));
		assertEquals(0, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.H7));
		aset.initialize(new Board("1k6/ppp5/5Bq1/4Q3/3Qb3/8/2q3PP/7K w - - 0 1"));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.A1));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.B2));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.C3));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.D4));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.E5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.F6));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.G7));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.H8));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.B1));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.C2));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.D3));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.E4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.F5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.G6));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.H7));
		aset.initialize(new Board("1k6/ppp5/8/1Q1Q1R2/2q1r1q1/8/6PP/7K w - - 0 1"));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.A5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.B5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.C5));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.D5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.E5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.F5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.G5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.WHITE, Square.H5));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.A4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.B4));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.C4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.D4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.E4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.F4));
		assertEquals(1, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.G4));
		assertEquals(2, countTotalAttackers(aset, PieceType.QUEEN, Player.BLACK, Square.H4));
	}

	private int[] extractAttackerTypesInOrder(PlayerAttackSet aset, int player, int targetSquare) {
		int num = 0;
		int temp[] = new int[10];
		for (int i = 0; i < aset.length(Player.WHITE); ++i) {
			if ((aset.getAttackSet(Player.WHITE, i).getAttacks() & Bitboard.initFromSquare(Square.D4)) != 0L)
				temp[num++] = aset.getAttackSet(Player.WHITE, i).getType();
		}
		int ret[] = new int[num];
		for (int i = 0; i < num; ++i)
			ret[i] = temp[i];
		return ret;
	}

	private int[] extractAttackerLocationsInOrder(PlayerAttackSet aset, int player, int targetSquare) {
		int num = 0;
		int temp[] = new int[10];
		for (int i = 0; i < aset.length(player); ++i) {
			if ((aset.getAttackSet(player, i).getAttacks() & Bitboard.initFromSquare(targetSquare)) != 0L)
				temp[num++] = aset.getAttackSet(player, i).getOrigin();
		}
		int ret[] = new int[num];
		for (int i = 0; i < num; ++i)
			ret[i] = temp[i];
		return ret;
	}
	
	private AttackSet[] extractAttackersInOrder(PlayerAttackSet aset, int player, int targetSquare) {
		int num = 0;
		AttackSet temp[] = new AttackSet[10];
		for (int i = 0; i < aset.length(player); ++i) {
			if ((aset.getAttackSet(player, i).getAttacks() & Bitboard.initFromSquare(targetSquare)) != 0L)
				temp[num++] = aset.getAttackSet(player, i);
		}
		AttackSet ret[] = new AttackSet[num];
		for (int i = 0; i < num; ++i)
			ret[i] = temp[i];
		return ret;
	}

	@Test
	void testAttacOrdering() {
		PlayerAttackSet aset = new PlayerAttackSet();
		// pawns
		aset.initialize(new Board("1k6/ppp5/8/2K5/3n2Q1/4P3/1B1RN1PP/8 w - - 0 1"));
		assertArrayEquals(new int[] { PieceType.PAWN, PieceType.KNIGHT, PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN, PieceType.KING },
				extractAttackerTypesInOrder(aset, Player.WHITE, Square.D4));
		assertArrayEquals(new int[] { Square.SQUARE_NONE, Square.E2, Square.B2, Square.D2, Square.G4, Square.C5 }, extractAttackerLocationsInOrder(aset, Player.WHITE, Square.D4));

		aset.initialize(new Board("1k6/pppR4/5Q2/5N2/3n4/2PKP3/1B4PP/8 w - - 0 1"));
		assertArrayEquals(new int[] { Square.SQUARE_NONE, Square.SQUARE_NONE, Square.F5, Square.B2, Square.D7, Square.F6, Square.D3 },
				extractAttackerLocationsInOrder(aset, Player.WHITE, Square.D4));

		aset.initialize(new Board("1k6/ppp5/8/5N2/3n1Q1R/2PK4/1B4PP/Q7 w - - 0 1"));
		assertArrayEquals(new int[] { Square.SQUARE_NONE, Square.F5, Square.B2, Square.F4, Square.H4, Square.A1, Square.D3 }, extractAttackerLocationsInOrder(aset, Player.WHITE, Square.D4));
	}
}
