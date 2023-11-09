package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BasicStaticExchangeEvaluatorTest {
	
	void helper_test_getLeastValuableAttacker_mask(long expected, String fen, int sq_target, int player, long clearedLocationsMask) {
		assertEquals(expected, BasicStaticExchangeEvaluator.static_getLeastValuableAttacker_mask(new Gamestate(fen),
				sq_target,
				player, clearedLocationsMask));
	}

	@Test
	void getLeastValuableAttacker_mask_test() {
		helper_test_getLeastValuableAttacker_mask(0L, "8/4k3/8/8/8/2K5/8/8 w - - 0 1", Square.E5, Player.WHITE, 0L);
		
		//pawns: 0, 1, 2 attackers
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E3),//expected
				"8/4k3/8/6p1/8/1K2P3/8/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.H4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5", "g5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		//knights
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("g4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4", "d5")//cleared locations
		);
		//bishops
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E8),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7", "e8")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.A4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7", "b5", "e8")//cleared locations
		);
		//rooks
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.B5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"8/8/k7/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("b5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e5", "f5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("g5")//cleared locations
		);
		//queens
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C5),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F5),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c5", "f5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B6,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D4),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B6,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("c5")//cleared locations
		);
		//kings
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C6),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C6),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.B6,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.B6,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c7", "c6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C4),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C4),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C3,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("d2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C3,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("d2", "c4")//cleared locations
		);
		
		//order of attacks - no batteries
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E3),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E2),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F6),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F4),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.B6),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D3),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4", "b6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4", "b6", "d3")//cleared locations
		);
		//with batteries
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F3),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G2),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D8),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.C6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2", "d7", "d8")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2", "d7", "d8", "c6")//cleared locations
		);
	}
	
	@Test
	void initialize_test() {
		Gamestate game = new Gamestate();
		BasicStaticExchangeEvaluator eval = new BasicStaticExchangeEvaluator(game);
		
		game.loadFromFEN("1k1r4/pp1r1q2/2npb3/6B1/1R1R2b1/2KQN1NP/1P4P1/5Q2 w - - 0 1");
		eval.initialize();
		
		assertEquals(0x40a50000L, eval.getAttackedTargets(Player.WHITE, PieceType.PAWN));
		assertEquals(0xa8540054a8L, eval.getAttackedTargets(Player.WHITE, PieceType.KNIGHT));
		assertEquals(0x810a000a0100000L, eval.getAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x20a0a7f0a0200L, eval.getAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0xa061223c3c7cffL, eval.getAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0xe0a0e00L, eval.getAttackedTargets(Player.WHITE, PieceType.KING));
		
		assertEquals(0x71400000000L, eval.getAttackedTargets(Player.BLACK, PieceType.PAWN));
		assertEquals(0xa1100110a000000L, eval.getAttackedTargets(Player.BLACK, PieceType.KNIGHT));
		assertEquals(0x2810a844a21108L, eval.getAttackedTargets(Player.BLACK, PieceType.BISHOP));
		assertEquals(0xfe3e080000000000L, eval.getAttackedTargets(Player.BLACK, PieceType.ROOK));
		assertEquals(0x70d870a020202020L, eval.getAttackedTargets(Player.BLACK, PieceType.QUEEN));
		assertEquals(0x507000000000000L, eval.getAttackedTargets(Player.BLACK, PieceType.KING));
		
		//need to test a second position to verify the reset!
		game.loadFromFEN("8/8/8/k7/7K/8/8/8 w - - 0 1");
		eval.initialize();
		
		assertEquals(0x0L, eval.getAttackedTargets(Player.WHITE, PieceType.PAWN));
		assertEquals(0x0L, eval.getAttackedTargets(Player.WHITE, PieceType.KNIGHT));
		assertEquals(0x0L, eval.getAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x0L, eval.getAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0x0L, eval.getAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0xc040c00000L, eval.getAttackedTargets(Player.WHITE, PieceType.KING));
		
		assertEquals(0x0L, eval.getAttackedTargets(Player.BLACK, PieceType.PAWN));
		assertEquals(0x0L, eval.getAttackedTargets(Player.BLACK, PieceType.KNIGHT));
		assertEquals(0x0L, eval.getAttackedTargets(Player.BLACK, PieceType.BISHOP));
		assertEquals(0x0L, eval.getAttackedTargets(Player.BLACK, PieceType.ROOK));
		assertEquals(0x0L, eval.getAttackedTargets(Player.BLACK, PieceType.QUEEN));
		assertEquals(0x30203000000L, eval.getAttackedTargets(Player.BLACK, PieceType.KING));
	}

}
