package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BSEETest_evaluateQuiet_forced {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void testOutcome_evaluateCapture_forcedAttacker() {
		int expectedOutcome;
		//basic
		test_game.loadFromFEN("1k3r2/8/8/3p4/8/6K1/3P4/3R4 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.F3, Player.WHITE, PieceType.KING);
		assertEquals(-100000, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H3, Player.WHITE, PieceType.KING);
		assertEquals(0, expectedOutcome);
		
		test_game.loadFromFEN("5r2/1k2r3/3b3p/6p1/3Q4/1N4P1/1PP3K1/8 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.A5, Player.WHITE, PieceType.KNIGHT);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.KNIGHT);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A7, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E5, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H4, Player.WHITE, PieceType.QUEEN);
		assertEquals(-700, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A3, Player.BLACK, PieceType.BISHOP);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C7, Player.BLACK, PieceType.BISHOP);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.BLACK, PieceType.BISHOP);
		assertEquals(-200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E1, Player.BLACK, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.BLACK, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E5, Player.BLACK, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.BLACK, PieceType.ROOK);
		assertEquals(-400, expectedOutcome);
		
		//doomed running away case
		test_game.loadFromFEN("8/2k1q3/8/8/4R3/8/3B4/6K1 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.E1, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E2, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E3, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E6, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H4, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.BISHOP);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.WHITE, PieceType.BISHOP);
		assertEquals(-300, expectedOutcome);
		
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E8, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E5, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H4, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G7, Player.BLACK, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);
		
		//pawn pushes
		test_game.loadFromFEN("8/3kp1pp/1P3p2/3r4/P7/8/1PQP4/2K5 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.A5, Player.WHITE, PieceType.PAWN);
		assertEquals(-100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B7, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B3, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.D3, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.D4, Player.WHITE, PieceType.PAWN);
		assertEquals(-100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		
		test_game.loadFromFEN("2k2q2/1q1pnppp/npp1p3/4n2b/2P4N/P1PB2R1/1PN1PPP1/2K5 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.PAWN);
		assertEquals(-100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F3, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		
		expectedOutcome = test_eval.evaluateTarget(Square.B5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.D5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G5, Player.BLACK, PieceType.PAWN);
		assertEquals(-100, expectedOutcome);
		
		//generic
		test_game.loadFromFEN("8/ppq2p1k/3q3p/P2n4/N5R1/1K6/1Q1N2P1/6B1 w - - 0 1");
		test_eval.initialize();
		expectedOutcome = test_eval.evaluateTarget(Square.A6, Player.WHITE, PieceType.PAWN);
		assertEquals(-100, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.WHITE, PieceType.KNIGHT);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.KNIGHT);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.KING);
		assertEquals(-99700, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C4, Player.WHITE, PieceType.KING);
		assertEquals(-99200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C2, Player.WHITE, PieceType.KING);
		assertEquals(-99200, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A2, Player.WHITE, PieceType.KING);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C4, Player.WHITE, PieceType.KNIGHT);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E4, Player.WHITE, PieceType.KNIGHT);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.A3, Player.WHITE, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C1, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F6, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G7, Player.WHITE, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H8, Player.WHITE, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.WHITE, PieceType.BISHOP);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.WHITE, PieceType.BISHOP);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H2, Player.WHITE, PieceType.BISHOP);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G3, Player.WHITE, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G3, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C4, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G6, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G7, Player.WHITE, PieceType.ROOK);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G8, Player.WHITE, PieceType.ROOK);
		assertEquals(-500, expectedOutcome);
		
		expectedOutcome = test_eval.evaluateTarget(Square.A6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F6, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H5, Player.BLACK, PieceType.PAWN);
		assertEquals(0, expectedOutcome);
		
		expectedOutcome = test_eval.evaluateTarget(Square.G7, Player.BLACK, PieceType.KING);
		assertEquals(-100000, expectedOutcome);
		
		expectedOutcome = test_eval.evaluateTarget(Square.B6, Player.BLACK, PieceType.QUEEN);
		assertEquals(-700, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C5, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.C3, Player.BLACK, PieceType.QUEEN);
		assertEquals(-800, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.B4, Player.BLACK, PieceType.QUEEN);
		assertEquals(-600, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G3, Player.BLACK, PieceType.QUEEN);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.H2, Player.BLACK, PieceType.QUEEN);
		assertEquals(-500, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.F4, Player.BLACK, PieceType.QUEEN);
		assertEquals(-300, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.E5, Player.BLACK, PieceType.QUEEN);
		assertEquals(0, expectedOutcome);
		expectedOutcome = test_eval.evaluateTarget(Square.G6, Player.BLACK, PieceType.QUEEN);
		assertEquals(-300, expectedOutcome);
		
	}

}
