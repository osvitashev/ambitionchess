package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.HitCounter;

class BSEETest_evaluateQuiet_forced {
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
		test_game.loadFromFEN("1k3r2/8/8/3p4/8/6K1/3P4/3R4 w - - 0 1");
		test_eval.initialize();
		test(-100000, Square.F3, Player.WHITE, PieceType.KING);
		test(0, Square.H3, Player.WHITE, PieceType.KING);
		
		test_game.loadFromFEN("5r2/1k2r3/3b3p/6p1/3Q4/1N4P1/1PP3K1/8 w - - 0 1");
		test_eval.initialize();
		test(0, Square.A5, Player.WHITE, PieceType.KNIGHT);
		test(0, Square.C5, Player.WHITE, PieceType.KNIGHT);
		test(-800, Square.A7, Player.WHITE, PieceType.QUEEN);
		test(-500, Square.C5, Player.WHITE, PieceType.QUEEN);
		test(-800, Square.E5, Player.WHITE, PieceType.QUEEN);
		test(-800, Square.F4, Player.WHITE, PieceType.QUEEN);
		test(-700, Square.H4, Player.WHITE, PieceType.QUEEN);
		test(-300, Square.A3, Player.BLACK, PieceType.BISHOP);
		test(0, Square.C7, Player.BLACK, PieceType.BISHOP);
		test(-200, Square.F4, Player.BLACK, PieceType.BISHOP);
		test(0, Square.E1, Player.BLACK, PieceType.ROOK);
		test(-500, Square.E4, Player.BLACK, PieceType.ROOK);
		test(0, Square.E5, Player.BLACK, PieceType.ROOK);
		test(-400, Square.F4, Player.BLACK, PieceType.ROOK);
		
		//doomed running away case
		test_game.loadFromFEN("8/2k1q3/8/8/4R3/8/3B4/6K1 w - - 0 1");
		test_eval.initialize();
		test(0, Square.E1, Player.WHITE, PieceType.ROOK);
		test(-500, Square.E2, Player.WHITE, PieceType.ROOK);
		test(0, Square.E3, Player.WHITE, PieceType.ROOK);
		test(-500, Square.E6, Player.WHITE, PieceType.ROOK);
		test(0, Square.B4, Player.WHITE, PieceType.ROOK);
		test(-500, Square.H4, Player.WHITE, PieceType.ROOK);
		test(0, Square.B4, Player.WHITE, PieceType.BISHOP);
		test(-300, Square.G5, Player.WHITE, PieceType.BISHOP);
		
		test(-800, Square.B4, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.E8, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.E5, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.G5, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.H4, Player.BLACK, PieceType.QUEEN);
		test(0, Square.G7, Player.BLACK, PieceType.QUEEN);
		
		//pawn pushes
		test_game.loadFromFEN("8/3kp1pp/1P3p2/3r4/P7/8/1PQP4/2K5 w - - 0 1");
		test_eval.initialize();
		test(-100, Square.A5, Player.WHITE, PieceType.PAWN);
		test(0, Square.B7, Player.WHITE, PieceType.PAWN);
		test(0, Square.B3, Player.WHITE, PieceType.PAWN);
		test(0, Square.B4, Player.WHITE, PieceType.PAWN);
		test(0, Square.D3, Player.WHITE, PieceType.PAWN);
		test(-100, Square.D4, Player.WHITE, PieceType.PAWN);
		test(0, Square.E6, Player.BLACK, PieceType.PAWN);
		test(0, Square.E5, Player.BLACK, PieceType.PAWN);
		test(0, Square.F5, Player.BLACK, PieceType.PAWN);
		test(0, Square.G6, Player.BLACK, PieceType.PAWN);
		test(0, Square.G5, Player.BLACK, PieceType.PAWN);
		test(0, Square.H6, Player.BLACK, PieceType.PAWN);
		test(0, Square.H5, Player.BLACK, PieceType.PAWN);
		
		test_game.loadFromFEN("2k2q2/1q1pnppp/npp1p3/4n2b/2P4N/P1PB2R1/1PN1PPP1/2K5 w - - 0 1");
		test_eval.initialize();
		test(0, Square.B4, Player.WHITE, PieceType.PAWN);
		test(-100, Square.C5, Player.WHITE, PieceType.PAWN);
		test(0, Square.F3, Player.WHITE, PieceType.PAWN);
		test(0, Square.F4, Player.WHITE, PieceType.PAWN);
		
		test(0, Square.B5, Player.BLACK, PieceType.PAWN);
		test(0, Square.C5, Player.BLACK, PieceType.PAWN);
		test(0, Square.D5, Player.BLACK, PieceType.PAWN);
		test(0, Square.F5, Player.BLACK, PieceType.PAWN);
		test(0, Square.G6, Player.BLACK, PieceType.PAWN);
		test(-100, Square.G5, Player.BLACK, PieceType.PAWN);
		
		//generic
		test_game.loadFromFEN("8/ppq2p1k/3q3p/P2n4/N5R1/1K6/1Q1N2P1/6B1 w - - 0 1");
		test_eval.initialize();
		test(-100, Square.A6, Player.WHITE, PieceType.PAWN);
		test(-300, Square.B6, Player.WHITE, PieceType.KNIGHT);
		test(0, Square.C5, Player.WHITE, PieceType.KNIGHT);
		test(-99700, Square.B4, Player.WHITE, PieceType.KING);
		test(-99200, Square.C4, Player.WHITE, PieceType.KING);
		test(-99200, Square.C2, Player.WHITE, PieceType.KING);
		test(0, Square.A2, Player.WHITE, PieceType.KING);
		test(0, Square.C4, Player.WHITE, PieceType.KNIGHT);
		test(0, Square.E4, Player.WHITE, PieceType.KNIGHT);
		test(0, Square.A3, Player.WHITE, PieceType.QUEEN);
		test(-800, Square.C1, Player.WHITE, PieceType.QUEEN);
		test(-800, Square.F6, Player.WHITE, PieceType.QUEEN);
		test(0, Square.G7, Player.WHITE, PieceType.QUEEN);
		test(-800, Square.H8, Player.WHITE, PieceType.QUEEN);
		test(-300, Square.B6, Player.WHITE, PieceType.BISHOP);
		test(0, Square.C5, Player.WHITE, PieceType.BISHOP);
		test(-300, Square.H2, Player.WHITE, PieceType.BISHOP);
		test(0, Square.G3, Player.WHITE, PieceType.PAWN);
		test(-500, Square.G3, Player.WHITE, PieceType.ROOK);
		test(-500, Square.B4, Player.WHITE, PieceType.ROOK);
		test(0, Square.C4, Player.WHITE, PieceType.ROOK);
		test(-500, Square.F4, Player.WHITE, PieceType.ROOK);
		test(-500, Square.G6, Player.WHITE, PieceType.ROOK);
		test(0, Square.G7, Player.WHITE, PieceType.ROOK);
		test(-500, Square.G8, Player.WHITE, PieceType.ROOK);
		
		test(0, Square.A6, Player.BLACK, PieceType.PAWN);
		test(0, Square.B6, Player.BLACK, PieceType.PAWN);
		test(0, Square.B5, Player.BLACK, PieceType.PAWN);
		test(0, Square.F6, Player.BLACK, PieceType.PAWN);
		test(0, Square.F5, Player.BLACK, PieceType.PAWN);
		test(0, Square.H5, Player.BLACK, PieceType.PAWN);
		
		test(-100000, Square.G7, Player.BLACK, PieceType.KING);
		
		test(-700, Square.B6, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.C5, Player.BLACK, PieceType.QUEEN);
		test(-800, Square.C3, Player.BLACK, PieceType.QUEEN);
		test(-600, Square.B4, Player.BLACK, PieceType.QUEEN);
		test(-300, Square.G3, Player.BLACK, PieceType.QUEEN);
		test(-500, Square.H2, Player.BLACK, PieceType.QUEEN);
		test(-300, Square.F4, Player.BLACK, PieceType.QUEEN);
		test(0, Square.E5, Player.BLACK, PieceType.QUEEN);
		test(-300, Square.G6, Player.BLACK, PieceType.QUEEN);
		
		test_game.loadFromFEN("4r3/8/2k5/8/R2K4/8/5B2/8 w - - 0 1");
		test_eval.initialize();
		test(0, Square.C3, Player.WHITE, PieceType.KING);
		test(0, Square.C4, Player.WHITE, PieceType.KING);
		test(-100000, Square.C5, Player.WHITE, PieceType.KING);
		test(0, Square.D3, Player.WHITE, PieceType.KING);
		test(-100000, Square.D5, Player.WHITE, PieceType.KING);
		test(-100000, Square.E3, Player.WHITE, PieceType.KING);
		test(-100000, Square.E4, Player.WHITE, PieceType.KING);
		test(-100000, Square.E5, Player.WHITE, PieceType.KING);
		
		//long sequences
		test_game.loadFromFEN("8/2kr1bb1/3r4/1n1p4/1n3q2/1NPQ4/1K1R1BB1/3R4 b - - 3 2");
		test_eval.initialize();
		test(-100, Square.D4, Player.BLACK, PieceType.PAWN);
		
		test_game.loadFromFEN("8/2kr1bb1/3r1q2/1n1p4/8/1NPQ4/1KnR1BB1/3R4 b - - 3 2");
		test_eval.initialize();
		test(0, Square.D4, Player.BLACK, PieceType.PAWN);
		
		
	}

}
