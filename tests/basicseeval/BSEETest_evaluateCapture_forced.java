package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BSEETest_evaluateCapture_forced {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void testOutcome_evaluateCapture_forcedAttacker() {
		int expectedOutcome;
		//basic
		test_game.loadFromFEN("8/8/1k3p2/6R1/3n2K1/2P5/8/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.D4, Player.WHITE, PieceType.PAWN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.BLACK, PieceType.PAWN);
		assertEquals(400, expectedOutcome);
		
		//one of each type
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.PAWN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.KNIGHT);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.BISHOP);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.ROOK);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.QUEEN);
		assertEquals(200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.KING);
		assertEquals(-99000, expectedOutcome);//king captured. need a more graceful way to handle this.
		
		expectedOutcome = test_eval.evaluateTarget(Square.E2, Player.BLACK, PieceType.ROOK);
		assertEquals(0, expectedOutcome);//break-even
		expectedOutcome = test_eval.evaluateTarget(Square.F3, Player.BLACK, PieceType.QUEEN);
		assertEquals(-700, expectedOutcome);
		
		//more cases
		test_game.loadFromFEN("6r1/7N/2R4K/1k6/4b3/5P2/6q1/4R1N1 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.F3, Player.BLACK, PieceType.BISHOP);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F3, Player.BLACK, PieceType.QUEEN);
		assertEquals(-400, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C6, Player.BLACK, PieceType.BISHOP);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C6, Player.BLACK, PieceType.KING);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.PAWN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.ROOK);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G1, Player.BLACK, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);//break-even
		
		
		test_game.loadFromFEN("3r4/1n1qp3/1b3p1p/Pk5K/3r2N1/5B2/8/3R4 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.H6, Player.WHITE, PieceType.KING);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H6, Player.WHITE, PieceType.KNIGHT);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.WHITE, PieceType.PAWN);
		assertEquals(200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B7, Player.WHITE, PieceType.BISHOP);
		assertEquals(0, expectedOutcome);//break-even
		expectedOutcome = test_eval.evaluateTarget(Square.D4, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);//break-even
		
		expectedOutcome = test_eval.evaluateTarget(Square.A5, Player.BLACK, PieceType.KING);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A5, Player.BLACK, PieceType.BISHOP);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A5, Player.BLACK, PieceType.KNIGHT);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G4, Player.BLACK, PieceType.ROOK);
		assertEquals(-200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.D1, Player.BLACK, PieceType.ROOK);
		assertEquals(300, expectedOutcome);
		
		//failing case
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n3/8/8/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);//break even
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/7K/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.ROOK);
		assertEquals(-300, expectedOutcome);
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/5Q1K/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.ROOK);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.QUEEN);
		assertEquals(-600, expectedOutcome);
		
		test_game.loadFromFEN("8/5k2/3p4/2r5/8/2R1B3/4K3/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.ROOK);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.BISHOP);
		assertEquals(300, expectedOutcome);
		
		test_game.loadFromFEN("8/8/rk2n3/1P6/2B5/1q6/Q3n3/5K2 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E6, Player.WHITE, PieceType.BISHOP);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A6, Player.WHITE, PieceType.PAWN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A6, Player.WHITE, PieceType.QUEEN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C4, Player.BLACK, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B3, Player.WHITE, PieceType.BISHOP);
		assertEquals(800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A2, Player.BLACK, PieceType.ROOK);
		assertEquals(800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E2, Player.WHITE, PieceType.QUEEN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E2, Player.WHITE, PieceType.KING);
		assertEquals(300, expectedOutcome);
		
		//document known limitations
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n1Q1/4n3/3P1P2/2b5 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);//break even
		
		//cases in and into check
		test_game.loadFromFEN("8/8/6k1/3p4/p1p5/1K6/8/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.A4, Player.WHITE, PieceType.KING);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C4, Player.WHITE, PieceType.KING);
		assertEquals(-99900, expectedOutcome);
		
		test_game.loadFromFEN("8/2R5/1Nk2p2/2B3n1/5K2/8/8/8 b - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.C7, Player.BLACK, PieceType.KING);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.BLACK, PieceType.KING);
		assertEquals(-99700, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.BLACK, PieceType.KING);
		assertEquals(-99700, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.WHITE, PieceType.KING);
		assertEquals(-99700, expectedOutcome);
		
		test_game.loadFromFEN("8/4k1p1/4Pn2/R3b3/8/N7/b3R1K1/1n2q3 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E6, Player.BLACK, PieceType.BISHOP);
		assertEquals(100, expectedOutcome);
		
		test_game.loadFromFEN("4k3/N2p4/2r3q1/2p5/1Nr5/K7/2Q4r/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.C6, Player.WHITE, PieceType.KNIGHT);
		assertEquals(200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C2, Player.BLACK, PieceType.ROOK);
		assertEquals(600, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C2, Player.BLACK, PieceType.QUEEN);
		assertEquals(300, expectedOutcome);
		
//		consider: perhaps i do not really need the minimax score in most cases.
//		i only want to know whether a capture is profitable
//		using a rook to capture a bishop protected by a pawn is obvioulsy a bad SEE.
//		This heuristic can be used to avoif having to call ...forcedAttacker alltogether.
		
	}

}
