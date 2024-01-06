package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.AttackerType;

class BSEETest_initialize {
	
	private Gamestate test_game = new Gamestate();
	TargetStaticExchangeEvaluator tSee = new TargetStaticExchangeEvaluator(test_game);
	
	void helper_test_getLeastValuableAttacker(long expected, String fen, int sq_target, int player, long clearedLocationsMask) {
		test_game.loadFromFEN(fen);
		
		assertEquals(expected, tSee.getLeastValuableAttacker(sq_target,
				player, clearedLocationsMask));
	}

	@Test
	void getLeastValuableAttacker_mask_test() {
		helper_test_getLeastValuableAttacker(AttackerType.nullValue(), "8/4k3/8/8/8/2K5/8/8 w - - 0 1", Square.E5, Player.WHITE, 0L);
		
		//pawns: 0, 1, 2 attackers
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.E3),//expected
				"8/4k3/8/6p1/8/1K2P3/8/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.H4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.E5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5", "g5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.D4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		//knights
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KNIGHT, Square.G4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("g4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KNIGHT, Square.D5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KNIGHT, Square.F5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4", "d5")//cleared locations
		);
		//bishops
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.E8),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7", "e8")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.A4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7", "b5", "e8")//cleared locations
		);
		//rooks
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.B5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/8/k7/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("b5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.E5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.F5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e5", "f5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.F5),//expected
				"2q5/8/1k6/1rp1RRp1/8/6K1/8/8 w - - 0 1",
				Square.H5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("g5")//cleared locations
		);
		//queens
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.C5),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.F5),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c5", "f5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B6,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.D4),//expected
				"7k/1p4pp/1P6/1Pq2q2/1p1Q4/8/K7/8 w - - 0 1",
				Square.B6,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("c5")//cleared locations
		);
		//kings
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.C6),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.C6),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.B6,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.B6,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("c7", "c6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.C4),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.C4),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C3,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("d2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"8/2n5/2k5/8/2K5/8/3P4/8 b - - 0 1",
				Square.C3,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("d2", "c4")//cleared locations
		);
		
		//order of attacks - no batteries
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.E3),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KNIGHT, Square.E2),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.F6),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.F4),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.B6),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.D3),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4", "b6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
				"6k1/8/1Q3B2/8/3P1R2/3KP3/4N3/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("e3", "e2", "f6", "f4", "b6", "d3")//cleared locations
		);
		//with batteries
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.PAWN, Square.E6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KNIGHT, Square.C7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.BISHOP, Square.F7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.D6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.F3),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.G2),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.QUEEN, Square.D7),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.ROOK, Square.D8),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.create(PieceType.KING, Square.C6),//expected
				"3r4/K1nq1b2/2krp3/3p4/8/5q2/6q1/8 w - - 0 1",
				Square.D5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e6", "c7", "f7", "d6", "f3", "g2", "d7", "d8")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker(
				AttackerType.nullValue(),//expected
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
		
		//secondary targets for sliding pieces
		game.loadFromFEN("3b2k1/1q4rr/p7/N3Q3/P2B4/2Kn4/2P1R1q1/8 w - - 0 1");
		eval.initialize();
		
		assertEquals(0x2000a0000L, eval.getAttackedTargets(Player.WHITE, PieceType.PAWN));
		assertEquals(0x2040004020000L, eval.getAttackedTargets(Player.WHITE, PieceType.KNIGHT));
		assertEquals(0x1021400142040L, eval.getAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x40200000000201L, eval.getSecondaryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x1010106c10L, eval.getAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0x1010100000008300L, eval.getSecondaryAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0x125438ef38509000L, eval.getAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x8000000000040010L, eval.getSecondaryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0xe0a0e00L, eval.getAttackedTargets(Player.WHITE, PieceType.KING));
		
		assertEquals(0x200000000L, eval.getAttackedTargets(Player.BLACK, PieceType.PAWN));
		assertEquals(0x1422002214L, eval.getAttackedTargets(Player.BLACK, PieceType.KNIGHT));
		assertEquals(0x14224180000000L, eval.getAttackedTargets(Player.BLACK, PieceType.BISHOP));
		assertEquals(0x0L, eval.getSecondaryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		assertEquals(0xc0fec0c0c0c0c080L, eval.getAttackedTargets(Player.BLACK, PieceType.ROOK));
		assertEquals(0x3f000000000040L, eval.getSecondaryAttackedTargets(Player.BLACK, PieceType.ROOK));
		assertEquals(0x77f474a52e2f2e2L, eval.getAttackedTargets(Player.BLACK, PieceType.QUEEN));
		assertEquals(0x4180000000000c80L, eval.getSecondaryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		assertEquals(0xa0e0000000000000L, eval.getAttackedTargets(Player.BLACK, PieceType.KING));
		
		
		//bish
		game.loadFromFEN("k7/1pb5/3B4/4B3/1B6/2B3P1/6P1/4B2K w - - 0 1");
		eval.initialize();
		assertEquals(0xa05428152a452a11L, eval.getAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0xa2542001a2418a11L, eval.getSecondaryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		
		//rooks
		game.loadFromFEN("k7/pp1p1p1p/5r2/3r3r/5r2/7r/PP5r/K7 w - - 0 1");
		eval.initialize();
		assertEquals(0xa8fffffffffea8L, eval.getAttackedTargets(Player.BLACK, PieceType.ROOK));
		assertEquals(0xa8a080878020a1a0L, eval.getSecondaryAttackedTargets(Player.BLACK, PieceType.ROOK));
		
		//queens
		game.loadFromFEN("6K1/8/1Q3QQ1/8/3q4/8/5q2/4k3 w - - 0 1");
		eval.initialize();
		assertEquals(0xfaf7fff7fa6a6642L, eval.getAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0xdf0000142221L, eval.getSecondaryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		
		assertEquals(0x8082a3cff7cff79L, eval.getAttackedTargets(Player.BLACK, PieceType.QUEEN));
		assertEquals(0xa061020400000040L, eval.getSecondaryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		//secondary batteries
		
		//bishops and sliding pieces
		game.loadFromFEN("8/3q3k/2b3R1/1R6/4B3/8/1KQ3q1/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x102000000000082L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x1000000000204000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		game.loadFromFEN("8/2kn1p2/2b5/1b1B4/8/5B2/4nK2/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x102040000004080L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x8000011200000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));

		game.loadFromFEN("6k1/4b3/8/6n1/3Q3b/B3B1P1/5K2/8 w - - 0 1");
		eval.initialize();
		assertEquals(2306126700393529344L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x0L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		//bishops and pawns
		//point-blank
		game.loadFromFEN("8/1B1K4/3p1p2/1P1Pb2B/2Bp1p2/1P1P3k/8/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x110000000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x440000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		game.loadFromFEN("2bK4/8/3p1p2/1P1PB3/2bp1p1B/1P1P3k/8/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x440000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x110000000000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		//distant
		game.loadFromFEN("6k1/1p3p2/1P1B1P2/3b4/3B1P2/1p3p2/1P1K1P2/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x41000000000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x4100L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		game.loadFromFEN("6k1/1p2pp2/1P3P2/3B1B2/3b1P2/1p1P1p2/1P1K1P2/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x4100L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.BISHOP));
		assertEquals(0x41000000000000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.BISHOP));
		
		//rooks and sliding pieces
		game.loadFromFEN("7k/6p1/3Q4/1bQ3r1/1q1R2R1/8/3n3K/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x848400087000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0x200404040L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.ROOK));
		
		game.loadFromFEN("2rr1rk1/8/8/8/2nQ1R2/8/5BK1/8 w - - 0 1");
		eval.initialize();
		assertEquals(0x4000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.ROOK));
		assertEquals(0x7700000000282808L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.ROOK));
		
		//queens and sliding pieces
		game.loadFromFEN("8/2kq1r2/6n1/1R1b1B2/8/1q1Q2P1/3p4/1Q3RK1 w - - 0 1");
		eval.initialize();
		assertEquals(0x402212010040L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x2e2120040f00000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		game.loadFromFEN("5K2/4Q1Q1/1b2b3/4r3/2Q2N2/6R1/1q2q3/1k6 w - - 0 1");
		eval.initialize();
		assertEquals(0x40af000000004060L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x11020000e100L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		//queens and pawns
		//point blank
		game.loadFromFEN("3k4/4ppp1/4pqp1/1PPPppp1/1PQP4/1PPP4/8/5K2 b - - 0 1");
		eval.initialize();
		assertEquals(0x110000000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x88000000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		game.loadFromFEN("3k4/4ppp1/4pQp1/1PPPppp1/1PqP4/1PPP4/8/5K2 w - - 0 1");
		eval.initialize();
		assertEquals(0x88000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x110000000000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		//distant pawns
		game.loadFromFEN("8/8/kP1P1P2/8/1P1Q1P2/1q6/1P1P1PQ1/4K3 w - - 0 1");
		eval.initialize();
		assertEquals(0x41000000000000L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x0L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		game.loadFromFEN("8/6q1/kP1P1P2/8/1P1q1P2/1Q6/1P1P1P2/4K3 w - - 0 1");
		eval.initialize();
		assertEquals(0x0L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x41000000000000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		game.loadFromFEN("8/kp1p1p2/8/1p1Q1p2/8/1p1p1p2/5K2/2Q5 w - - 0 1");
		eval.initialize();
		assertEquals(0x4100L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x0L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		game.loadFromFEN("8/kpqp1p2/8/1p1q1p2/8/1p1p1p2/5K2/2r2NQ1 w - - 0 1");
		eval.initialize();
		assertEquals(0x0L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE, PieceType.QUEEN));
		assertEquals(0x4100L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK, PieceType.QUEEN));
		
		//combined values
		game.loadFromFEN("1k6/3P4/2b1q1r1/1P2Rp2/2B3N1/1P6/1P2Q1P1/2K5 w - - 0 1");
		eval.initialize();
		assertEquals(0x1400b53e55bffe3aL, eval.getAttackedTargets(Player.WHITE));
		assertEquals(0x557ffc7a54204000L, eval.getAttackedTargets(Player.BLACK));
		assertEquals(0x503011c300008130L, eval.getSecondaryAttackedTargets(Player.WHITE));
		assertEquals(0x14008f0051525080L, eval.getSecondaryAttackedTargets(Player.BLACK));
		assertEquals(0x5030110200000030L, eval.getSecondaryBatteryAttackedTargets(Player.WHITE));
		assertEquals(0x14008c0050121000L, eval.getSecondaryBatteryAttackedTargets(Player.BLACK));
	}
	
	@Test
	void getPawnPushSource_test() {
		Gamestate game = new Gamestate();
		BasicStaticExchangeEvaluator eval = new BasicStaticExchangeEvaluator(game);
		
		game.loadFromFEN("5r2/2nq1rbp/1R4p1/8/PPk5/2P3K1/1B1n2Q1/8 w - - 0 1");
		eval.initialize();
		assertEquals(Square.A4, eval.getPawnPushSourceSquare(Square.A5, Player.WHITE, 0l));
		assertEquals(Square.B4, eval.getPawnPushSourceSquare(Square.B5, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A6, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A5, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A6, Player.BLACK, 0l));
		assertEquals(Square.G6, eval.getPawnPushSourceSquare(Square.G5, Player.BLACK, 0l));
		assertEquals(Square.H7, eval.getPawnPushSourceSquare(Square.H6, Player.BLACK, 0l));
		assertEquals(Square.H7, eval.getPawnPushSourceSquare(Square.H5, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A1, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A1, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A8, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A8, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.H1, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.H1, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.H8, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.H8, Player.BLACK, 0l));
		
		game.loadFromFEN("5k2/4pppP/1pp1p3/pp3P2/5PPp/1PPNN3/P1P1P2P/2K5 w - - 0 1");
		eval.initialize();
		assertEquals(Square.A2, eval.getPawnPushSourceSquare(Square.A3, Player.WHITE, 0l));
		assertEquals(Square.A2, eval.getPawnPushSourceSquare(Square.A4, Player.WHITE, 0l));
		assertEquals(Square.B3, eval.getPawnPushSourceSquare(Square.B4, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.B2, Player.WHITE, 0l));
		assertEquals(Square.C3, eval.getPawnPushSourceSquare(Square.C4, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.D5, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.D6, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.E4, Player.WHITE, 0l));
		assertEquals(Square.F5, eval.getPawnPushSourceSquare(Square.F6, Player.WHITE, 0l));
		assertEquals(Square.G4, eval.getPawnPushSourceSquare(Square.G5, Player.WHITE, 0l));
		assertEquals(Square.H7, eval.getPawnPushSourceSquare(Square.H8, Player.WHITE, 0l));
		assertEquals(Square.H2, eval.getPawnPushSourceSquare(Square.H3, Player.WHITE, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A3, Player.BLACK, 0l));
		assertEquals(Square.A5, eval.getPawnPushSourceSquare(Square.A4, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.A6, Player.BLACK, 0l));
		assertEquals(Square.B5, eval.getPawnPushSourceSquare(Square.B4, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.B7, Player.BLACK, 0l));
		assertEquals(Square.C6, eval.getPawnPushSourceSquare(Square.C5, Player.BLACK, 0l));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.C4, Player.BLACK, 0l));
		assertEquals(Square.E6, eval.getPawnPushSourceSquare(Square.E5, Player.BLACK, 0l));
		assertEquals(Square.F7, eval.getPawnPushSourceSquare(Square.F6, Player.BLACK, 0l));
		assertEquals(Square.G7, eval.getPawnPushSourceSquare(Square.G6, Player.BLACK, 0l));
		assertEquals(Square.G7, eval.getPawnPushSourceSquare(Square.G5, Player.BLACK, 0l));
		assertEquals(Square.H4, eval.getPawnPushSourceSquare(Square.H3, Player.BLACK, 0l));
		
		//test cleared squares
		assertEquals(Square.E2, eval.getPawnPushSourceSquare(Square.E4, Player.WHITE, Bitboard.initFromSquare(Square.E3)));
		assertEquals(Square.E7, eval.getPawnPushSourceSquare(Square.E5, Player.BLACK, Bitboard.initFromSquare(Square.E6)));
		
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.F6, Player.WHITE, Bitboard.initFromSquare(Square.F5)));
		assertEquals(Square.SQUARE_NONE, eval.getPawnPushSourceSquare(Square.B4, Player.BLACK, Bitboard.initFromSquare(Square.B5)));
		
	}
}
