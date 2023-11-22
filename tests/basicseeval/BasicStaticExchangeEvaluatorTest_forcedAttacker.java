package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

class BasicStaticExchangeEvaluatorTest_forcedAttacker {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void testOutcome_evaluateCapture_forcedAttacker() {
		int expectedOutcome;
		//basic
		test_game.loadFromFEN("8/8/1k3p2/6R1/3n2K1/2P5/8/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.D4, PieceType.PAWN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.G5, PieceType.PAWN);
		assertEquals(400, expectedOutcome);
		
		//one of each type
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.PAWN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.KNIGHT);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.BISHOP);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.ROOK);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.QUEEN);
		assertEquals(200, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.KING);
		assertEquals(-99000, expectedOutcome);//king captured. need a more graceful way to handle this.
		
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E2, PieceType.ROOK);
		assertEquals(0, expectedOutcome);//break-even
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.F3, PieceType.QUEEN);
		assertEquals(-700, expectedOutcome);
		
		//more cases
		test_game.loadFromFEN("6r1/7N/2R4K/1k6/4b3/5P2/6q1/4R1N1 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.F3, PieceType.BISHOP);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.F3, PieceType.QUEEN);
		assertEquals(-400, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C6, PieceType.BISHOP);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C6, PieceType.KING);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.PAWN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.ROOK);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.G1, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);//break-even
		
		
		test_game.loadFromFEN("3r4/1n1qp3/1b3p1p/Pk5K/3r2N1/5B2/8/3R4 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.H6, PieceType.KING);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.H6, PieceType.KNIGHT);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.B6, PieceType.PAWN);
		assertEquals(200, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.B7, PieceType.BISHOP);
		assertEquals(0, expectedOutcome);//break-even
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.D4, PieceType.ROOK);
		assertEquals(0, expectedOutcome);//break-even
		
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A5, PieceType.KING);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A5, PieceType.BISHOP);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A5, PieceType.KNIGHT);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.G4, PieceType.ROOK);
		assertEquals(-200, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.D1, PieceType.ROOK);
		assertEquals(300, expectedOutcome);
		
		//failing case
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n3/8/8/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);//break even
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/7K/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C5, PieceType.ROOK);
		assertEquals(-300, expectedOutcome);
		
		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/5Q1K/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C5, PieceType.ROOK);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C5, PieceType.QUEEN);
		assertEquals(-600, expectedOutcome);
		
		test_game.loadFromFEN("8/5k2/3p4/2r5/8/2R1B3/4K3/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C5, PieceType.ROOK);
		assertEquals(100, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C5, PieceType.BISHOP);
		assertEquals(300, expectedOutcome);
		
		test_game.loadFromFEN("8/8/rk2n3/1P6/2B5/1q6/Q3n3/5K2 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E6, PieceType.BISHOP);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A6, PieceType.PAWN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A6, PieceType.QUEEN);
		assertEquals(500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.C4, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.B3, PieceType.BISHOP);
		assertEquals(800, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.A2, PieceType.ROOK);
		assertEquals(800, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E2, PieceType.QUEEN);
		assertEquals(300, expectedOutcome);
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E2, PieceType.KING);
		assertEquals(300, expectedOutcome);
		
		
		
		
		//document known limitations
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n1Q1/4n3/3P1P2/2b5 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateCapture_forcedAttacker(Square.E4, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);//break even

		
		
//		consider: perhaps i do not really need the minimax score in most cases.
//		i only want to know whether a capture is profitable
//		using a rook to capture a bishop protected by a pawn is obvioulsy a bad SEE.
//		This heuristic can be used to avoif having to call ...forcedAttacker alltogether.
		
	}

}
