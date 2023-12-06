package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.HitCounter;

class BSEETest_evaluateCapture_forced {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	static final boolean skipAssertions = false;
	
	void test(int expectedOutcome, int sq, int player, int pieceType) {
		int outcome = test_eval.evaluateTargetExchange(sq, player, pieceType);
		if(!skipAssertions) {
			if(expectedOutcome > 0)
				assertTrue(expectedOutcome <= outcome);
			else if(expectedOutcome <0)
				assertTrue(expectedOutcome >= outcome);
			else
			assertEquals(expectedOutcome, outcome);
		}
	}
	
	@Test
	void testOutcome_evaluateCapture_forcedAttacker() {
		//basic
		test_game.loadFromFEN("8/8/1k3p2/6R1/3n2K1/2P5/8/8 w - - 0 1");
		test_eval.initialize();
		test(300, Square.D4, Player.WHITE, PieceType.PAWN);
		test(400, Square.G5, Player.BLACK, PieceType.PAWN);
		
		//one of each type
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		test_eval.initialize();
		test(500, Square.E4, Player.WHITE, PieceType.PAWN);
		test(500, Square.E4, Player.WHITE, PieceType.KNIGHT);
		test(500, Square.E4, Player.WHITE, PieceType.BISHOP);
		test(500, Square.E4, Player.WHITE, PieceType.ROOK);
		test(200, Square.E4, Player.WHITE, PieceType.QUEEN);
		test(-99000, Square.E4, Player.WHITE, PieceType.KING);
		
		test(0, Square.E2, Player.BLACK, PieceType.ROOK);
		test(-700, Square.F3, Player.BLACK, PieceType.QUEEN);
		
		//more cases
		test_game.loadFromFEN("6r1/7N/2R4K/1k6/4b3/5P2/6q1/4R1N1 w - - 0 1");
		test_eval.initialize();
		test(100, Square.F3, Player.BLACK, PieceType.BISHOP);
		test(-400, Square.F3, Player.BLACK, PieceType.QUEEN);
		test(500, Square.C6, Player.BLACK, PieceType.BISHOP);
		test(500, Square.C6, Player.BLACK, PieceType.KING);
		test(300, Square.E4, Player.WHITE, PieceType.PAWN);
		test(300, Square.E4, Player.WHITE, PieceType.ROOK);
		test(0, Square.G1, Player.BLACK, PieceType.QUEEN);
		
		
		test_game.loadFromFEN("3r4/1n1qp3/1b3p1p/Pk5K/3r2N1/5B2/8/3R4 w - - 0 1");
		test_eval.initialize();
		test(100, Square.H6, Player.WHITE, PieceType.KING);
		test(100, Square.H6, Player.WHITE, PieceType.KNIGHT);
		test(200, Square.B6, Player.WHITE, PieceType.PAWN);
		test(0, Square.B7, Player.WHITE, PieceType.BISHOP);
		test(0, Square.D4, Player.WHITE, PieceType.ROOK);
		
		test(100, Square.A5, Player.BLACK, PieceType.KING);
		test(100, Square.A5, Player.BLACK, PieceType.BISHOP);
		test(100, Square.A5, Player.BLACK, PieceType.KNIGHT);
		test(-200, Square.G4, Player.BLACK, PieceType.ROOK);
		test(300, Square.D1, Player.BLACK, PieceType.ROOK);
		
		//failing case
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n3/8/8/8 w - - 0 1");
		test_eval.initialize();
		test(-500, Square.E4, Player.WHITE, PieceType.QUEEN);
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/7K/8 w - - 0 1");
		test_eval.initialize();
		test(-300, Square.C5, Player.WHITE, PieceType.ROOK);
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/5Q1K/8 w - - 0 1");
		test_eval.initialize();
		test(-300, Square.C5, Player.WHITE, PieceType.ROOK);
		test(-600, Square.C5, Player.WHITE, PieceType.QUEEN);
		
		test_game.loadFromFEN("8/5k2/3p4/2r5/8/2R1B3/4K3/8 w - - 0 1");
		test_eval.initialize();
		test(100, Square.C5, Player.WHITE, PieceType.ROOK);
		test(300, Square.C5, Player.WHITE, PieceType.BISHOP);
		
		test_game.loadFromFEN("8/8/rk2n3/1P6/2B5/1q6/Q3n3/5K2 w - - 0 1");
		test_eval.initialize();
		test(300, Square.E6, Player.WHITE, PieceType.BISHOP);
		test(500, Square.A6, Player.WHITE, PieceType.PAWN);
		test(500, Square.A6, Player.WHITE, PieceType.QUEEN);
		test(-500, Square.C4, Player.BLACK, PieceType.QUEEN);
		test(800, Square.B3, Player.WHITE, PieceType.BISHOP);
		test(800, Square.A2, Player.BLACK, PieceType.ROOK);
		test(300, Square.E2, Player.WHITE, PieceType.QUEEN);
		test(300, Square.E2, Player.WHITE, PieceType.KING);
		
		//document known limitations
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n1Q1/4n3/3P1P2/2b5 w - - 0 1");
		test_eval.initialize();
		test(0, Square.E4, Player.WHITE, PieceType.QUEEN);
		
		//cases in and into check
		test_game.loadFromFEN("8/8/6k1/3p4/p1p5/1K6/8/8 w - - 0 1");
		test_eval.initialize();
		test(100, Square.A4, Player.WHITE, PieceType.KING);
		test(-99900, Square.C4, Player.WHITE, PieceType.KING);
		
		test_game.loadFromFEN("8/2R5/1Nk2p2/2B3n1/5K2/8/8/8 b - - 0 1");
		test_eval.initialize();
		test(500, Square.C7, Player.BLACK, PieceType.KING);
		test(-99700, Square.C5, Player.BLACK, PieceType.KING);
		test(-99700, Square.B6, Player.BLACK, PieceType.KING);
		test(-99700, Square.G5, Player.WHITE, PieceType.KING);
		
		test_game.loadFromFEN("8/4k1p1/4Pn2/R3b3/8/N7/b3R1K1/1n2q3 w - - 0 1");
		test_eval.initialize();
		test(100, Square.E6, Player.BLACK, PieceType.BISHOP);
		
		test_game.loadFromFEN("4k3/N2p4/2r3q1/2p5/1Nr5/K7/2Q4r/8 w - - 0 1");
		test_eval.initialize();
		test(200, Square.C6, Player.WHITE, PieceType.KNIGHT);
		test(600, Square.C2, Player.BLACK, PieceType.ROOK);
		test(300, Square.C2, Player.BLACK, PieceType.QUEEN);
		
		
		/**
		 * in terms of detecting overprotectoion: all three of the defenders are providing overprotection.
		 */
		test_game.loadFromFEN("8/1k1r4/8/2b2n2/3p4/8/1K2N3/8 w - - 0 1");
		test_eval.initialize();
		test(-200, Square.D4, Player.WHITE, PieceType.KNIGHT);
		
		System.out.println(HitCounter.dump());
		
		
		if(skipAssertions)
			fail("Assertions skipped! We must be running some experiments...");
		
//		consider: perhaps i do not really need the minimax score in most cases.
//		i only want to know whether a capture is profitable
//		using a rook to capture a bishop protected by a pawn is obvioulsy a bad SEE.
//		This heuristic can be used to avoif having to call ...forcedAttacker alltogether.
		
	}

}
