package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.Utilities.OutcomeEnum;

class BSEETest_evaluateCapture_target {
	private Gamestate test_game = new Gamestate();
	private TargetStaticExchangeEvaluator test_eval = new TargetStaticExchangeEvaluator(test_game);
	//private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, test_target_eval);

	static final boolean skipAssertions = false;

	private void testOutcome(int expectedOutcome, int sq, int player, int pieceType) {
		testOutcome_withClearedSquares(expectedOutcome, sq, player, pieceType, 0l);
	}
	
	private void testOutcome_withClearedSquares(int expectedOutcome, int sq, int player, int pieceType, long clearedSquares) {
		test_eval.evaluateTargetExchange(sq, player, clearedSquares, pieceType);
		int outcome = test_eval.get_output_ExpectedGain();
		if (!skipAssertions) {
			if (expectedOutcome > 0)
				assertTrue(outcome > 0);
			else if (expectedOutcome < 0)
				assertTrue(outcome < 0);
			else
				assertEquals(0, outcome);
		}
	}

	@Test
	void testScore_evaluateCapture_forcedAttacker() {
		// basic
//		test_game.loadFromFEN("8/8/1k3p2/6R1/3n2K1/2P5/8/8 w - - 0 1");
//		test_eval.initialize();
//		test(OutcomeEnum.POSITIVE, Square.D4, Player.WHITE, PieceType.PAWN);
//		test(OutcomeEnum.POSITIVE, Square.G5, Player.BLACK, PieceType.PAWN);

		// one of each type
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.PAWN);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.KNIGHT);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.BISHOP);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.QUEEN);
		testOutcome(OutcomeEnum.NEGATIVE, Square.E4, Player.WHITE, PieceType.KING);

		testOutcome(OutcomeEnum.NEUTRAL, Square.E2, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.NEGATIVE, Square.F3, Player.BLACK, PieceType.QUEEN);

		// more cases
		test_game.loadFromFEN("6r1/7N/2R4K/1k6/4b3/5P2/6q1/4R1N1 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.F3, Player.BLACK, PieceType.BISHOP);
		testOutcome(OutcomeEnum.NEGATIVE, Square.F3, Player.BLACK, PieceType.QUEEN);
		testOutcome(OutcomeEnum.POSITIVE, Square.C6, Player.BLACK, PieceType.BISHOP);
		testOutcome(OutcomeEnum.POSITIVE, Square.C6, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.PAWN);
		testOutcome(OutcomeEnum.POSITIVE, Square.E4, Player.WHITE, PieceType.ROOK);
		testOutcome(OutcomeEnum.NEUTRAL, Square.G1, Player.BLACK, PieceType.QUEEN);

		test_game.loadFromFEN("3r4/1n1qp3/1b3p1p/Pk5K/3r2N1/5B2/8/3R4 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.H6, Player.WHITE, PieceType.KING);
		testOutcome(OutcomeEnum.POSITIVE, Square.H6, Player.WHITE, PieceType.KNIGHT);
		testOutcome(OutcomeEnum.POSITIVE, Square.B6, Player.WHITE, PieceType.PAWN);
		testOutcome(OutcomeEnum.NEUTRAL, Square.B7, Player.WHITE, PieceType.BISHOP);
		testOutcome(OutcomeEnum.NEUTRAL, Square.D4, Player.WHITE, PieceType.ROOK);

		testOutcome(OutcomeEnum.POSITIVE, Square.A5, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.POSITIVE, Square.A5, Player.BLACK, PieceType.BISHOP);
		testOutcome(OutcomeEnum.POSITIVE, Square.A5, Player.BLACK, PieceType.KNIGHT);
		testOutcome(OutcomeEnum.NEGATIVE, Square.G4, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.D1, Player.BLACK, PieceType.ROOK);

		// failing case
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n3/8/8/8 w - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.E4, Player.WHITE, PieceType.QUEEN);

		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/7K/8 w - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.ROOK);

		test_game.loadFromFEN("8/8/kp6/2p1RR2/8/8/5Q1K/8 w - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.ROOK);
		testOutcome(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.QUEEN);

		test_game.loadFromFEN("8/5k2/3p4/2r5/8/2R1B3/4K3/8 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.C5, Player.WHITE, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.C5, Player.WHITE, PieceType.BISHOP);

		test_game.loadFromFEN("8/8/rk2n3/1P6/2B5/1q6/Q3n3/5K2 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.E6, Player.WHITE, PieceType.BISHOP);
		testOutcome(OutcomeEnum.POSITIVE, Square.A6, Player.WHITE, PieceType.PAWN);
		testOutcome(OutcomeEnum.POSITIVE, Square.A6, Player.WHITE, PieceType.QUEEN);
		testOutcome(OutcomeEnum.NEGATIVE, Square.C4, Player.BLACK, PieceType.QUEEN);
		testOutcome(OutcomeEnum.POSITIVE, Square.B3, Player.WHITE, PieceType.BISHOP);
		testOutcome(OutcomeEnum.POSITIVE, Square.A2, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.E2, Player.WHITE, PieceType.QUEEN);
		testOutcome(OutcomeEnum.POSITIVE, Square.E2, Player.WHITE, PieceType.KING);

		// document known limitations
		test_game.loadFromFEN("8/4k3/6K1/8/r1Q1n1Q1/4n3/3P1P2/2b5 w - - 0 1");
		testOutcome(OutcomeEnum.NEUTRAL, Square.E4, Player.WHITE, PieceType.QUEEN);

		// cases in and into check
		test_game.loadFromFEN("8/8/6k1/3p4/p1p5/1K6/8/8 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.A4, Player.WHITE, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.C4, Player.WHITE, PieceType.KING);

		test_game.loadFromFEN("8/2R5/1Nk2p2/2B3n1/5K2/8/8/8 b - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.C7, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.C5, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.B6, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.G5, Player.WHITE, PieceType.KING);

		test_game.loadFromFEN("8/4k1p1/4Pn2/R3b3/8/N7/b3R1K1/1n2q3 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.E6, Player.BLACK, PieceType.BISHOP);

		test_game.loadFromFEN("4k3/N2p4/2r3q1/2p5/1Nr5/K7/2Q4r/8 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.C6, Player.WHITE, PieceType.KNIGHT);
		testOutcome(OutcomeEnum.POSITIVE, Square.C2, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.C2, Player.BLACK, PieceType.QUEEN);

		/**
		 * in terms of detecting overprotectoion: all three of the defenders are
		 * providing overprotection.
		 */
		test_game.loadFromFEN("8/1k1r4/8/2b2n2/3p4/8/1K2N3/8 w - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.D4, Player.WHITE, PieceType.KNIGHT);

		test_game.loadFromFEN("8/8/2Pb4/2k5/1N6/1K6/8/2r5 w - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.B4, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.C6, Player.BLACK, PieceType.KING);

		test_game.loadFromFEN("8/1b6/8/3k4/2P1P3/3K4/8/8 b - - 0 1");
		testOutcome(OutcomeEnum.NEGATIVE, Square.C4, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.NEGATIVE, Square.E4, Player.BLACK, PieceType.KING);

		// long exchanges
		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2KR1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 0 2");
		testOutcome(OutcomeEnum.POSITIVE, Square.D5, Player.BLACK, PieceType.ROOK);
		test_game.loadFromFEN("3r2b1/ppnr4/1Np1k1pp/3n1p2/1N1R4/1BKQ1qp1/PP4bP/3R4 w - - 2 2");
		testOutcome(OutcomeEnum.NEGATIVE, Square.D5, Player.WHITE, PieceType.ROOK);
		test_game.loadFromFEN("3r2b1/ppnr4/1Np1knpp/2KR4/1N3p2/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		testOutcome(OutcomeEnum.NEUTRAL, Square.D5, Player.BLACK, PieceType.QUEEN);
		test_game.loadFromFEN("3r2b1/ppnr4/1Np1knpp/2KR4/1N3p2/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		testOutcome(OutcomeEnum.NEUTRAL, Square.D5, Player.BLACK, PieceType.QUEEN);
		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		testOutcome(OutcomeEnum.NEGATIVE, Square.D5, Player.WHITE, PieceType.KNIGHT);

		// no available recapture
		test_game.loadFromFEN("8/8/p1k5/1N4r1/8/6Q1/4K3/8 w - - 0 1");
		testOutcome(OutcomeEnum.POSITIVE, Square.B5, Player.BLACK, PieceType.PAWN);
		testOutcome(OutcomeEnum.POSITIVE, Square.B5, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.B5, Player.BLACK, PieceType.KING);
		testOutcome(OutcomeEnum.POSITIVE, Square.G3, Player.BLACK, PieceType.ROOK);
		testOutcome(OutcomeEnum.POSITIVE, Square.G5, Player.WHITE, PieceType.QUEEN);

		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		testOutcome(OutcomeEnum.NEGATIVE, Square.D5, Player.WHITE, PieceType.KNIGHT);
		test_game.loadFromFEN("3r2b1/ppnr4/1N2k1pp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		testOutcome(OutcomeEnum.NEUTRAL, Square.D5, Player.WHITE, PieceType.KNIGHT);

		test_game.loadFromFEN("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26");
		testOutcome(OutcomeEnum.POSITIVE, Square.F5, Player.WHITE, PieceType.PAWN);

		//////test with cleared squares
//		test_game.loadFromFEN("4rk2/4nppp/8/5n2/8/P2Q4/1PP5/1K6 w - - 0 1");
//		test_eval.initialize();
//		//fails because of attack validation!
//		testOutcome_withClearedSquares(OutcomeEnum.POSITIVE, Square.H7, Player.WHITE, PieceType.QUEEN, Bitboard.initFromAlgebraicSquares("f5"));

		if (skipAssertions)
			fail("Assertions skipped! We must be running some experiments...");

//		consider: perhaps i do not really need the minimax score in most cases.
//		i only want to know whether a capture is profitable
//		using a rook to capture a bishop protected by a pawn is obvioulsy a bad SEE.
//		This heuristic can be used to avoif having to call ...forcedAttacker alltogether.

	}

	private void testPlayerPieceType(int expectedFinalPlayer, int expectedFinalPieceType, int sq, int player, int pieceType,
			String[] principleLine) {
		test_eval.evaluateTargetExchange(sq, player, 0l, pieceType);
		int finalPlayer = test_eval.get_output_lastExpectedOccupier_player();
		int finalPieceType = test_eval.get_output_lastExpectedOccupier_pieceType();
		if (!skipAssertions) {
			assertEquals(expectedFinalPlayer, finalPlayer);
			assertEquals(expectedFinalPieceType, finalPieceType);
			assertEquals(principleLine.length, test_eval.get_output_principalLineLastIndex() + 1);
			for (int i = 0; i < principleLine.length; ++i)
				assertEquals(Square.algebraicStringToSquare(principleLine[i]),
						test_eval.get_output_principalLine_square(i));
		}
	}

	@Test
	void testTrace_evaluateCapture_forcedAttacker() {
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN, Square.E4, // expectedFinalPieceType
				Player.WHITE, // sq
				PieceType.PAWN, // pieceType
				new String[] { "e4", "f3" });
		assertEquals("{f3 g3 e8 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testPlayerPieceType(Player.WHITE, PieceType.PAWN, Square.E4, Player.WHITE, PieceType.ROOK,
				new String[] { "e4", "e2", "e8", "f3" });
		assertEquals("{e2 f3 e8 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testPlayerPieceType(Player.BLACK, PieceType.ROOK, Square.E4, Player.WHITE, PieceType.KING,
				new String[] { "e4", "d3", "e8" });
		assertEquals("{d3 e8 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2KR1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 0 2");
		testPlayerPieceType(Player.BLACK, PieceType.BISHOP, Square.D5, Player.BLACK, PieceType.ROOK,
				new String[] { "d5", "d7", "b4", "f6", "b6", "c7", "b3", "d8", "d3", "f3", "d1", "g2" });
		assertEquals("{d1 g2 b3 d3 f3 b4 c5 b6 e6 f6 c7 d7 d8 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		test_game.loadFromFEN("3r2b1/ppnr4/1Np1k1pp/3n1p2/1N1R4/1BKQ1qp1/PP4bP/3R4 w - - 2 2");
		testPlayerPieceType(Player.BLACK, PieceType.PAWN, Square.D5, Player.WHITE, PieceType.ROOK,
				new String[] { "d5", "d4", "c6" });
		assertEquals("{d4 c6 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));
		

		// no available recapture
		test_game.loadFromFEN("8/8/p1k5/1N4r1/8/6Q1/4K3/8 w - - 0 1");
		testPlayerPieceType(Player.BLACK, PieceType.PAWN, Square.B5, Player.BLACK, PieceType.PAWN,
				new String[] { "b5", "a6" });
		assertEquals("{a6 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		testPlayerPieceType(Player.BLACK, PieceType.ROOK, Square.B5, Player.BLACK, PieceType.ROOK,
				new String[] { "b5", "g5" });
		assertEquals("{g5 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		testPlayerPieceType(Player.BLACK, PieceType.KING, Square.B5, Player.BLACK, PieceType.KING,
				new String[] { "b5", "c6" });
		assertEquals("{c6 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		testPlayerPieceType(Player.BLACK, PieceType.ROOK, Square.G3, Player.BLACK, PieceType.ROOK,
				new String[] { "g3", "g5" });
		assertEquals("{g5 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		testPlayerPieceType(Player.WHITE, PieceType.QUEEN, Square.G5, Player.WHITE, PieceType.QUEEN,
				new String[] { "g5", "g3" });
		assertEquals("{g3 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));
		
		test_game.loadFromFEN("2k5/3B1br1/2P3q1/3n3N/6P1/1Nn4b/1K6/3rb1R1 b - - 0 1");
		test_naturalOrder_noExhange(Square.B2, Player.BLACK, PieceType.PAWN);
		test_naturalOrder_noExhange(Square.B4, Player.BLACK, PieceType.PAWN);

		if (skipAssertions)
			fail("Assertions skipped! We must be running some experiments...");
	}

	private void testOutcome_naturalOrder(int sq, int player, int expectedOutcome, String[] principleLine) {
		test_eval.evaluateTargetExchange(sq, player, 0l, PieceType.NO_PIECE);
		int outcome = test_eval.get_output_ExpectedGain();
		if (!skipAssertions) {
			if (expectedOutcome > 0)
				assertTrue(outcome > 0);
			else if (expectedOutcome < 0)
				assertTrue(outcome < 0);
			else
				assertEquals(0, outcome);
			assertEquals(principleLine.length, test_eval.get_output_principalLineLastIndex() + 1);
			for (int i = 0; i < principleLine.length; ++i)
				assertEquals(Square.algebraicStringToSquare(principleLine[i]),
						test_eval.get_output_principalLine_square(i));
		}
	}
	
	private void test_naturalOrder_noExhange(int sq, int player, int pieceType) {
		boolean outcome = test_eval.evaluateTargetExchange(sq, player, 0l, pieceType);
		assertEquals(false, outcome);
	}

	@Test
	void testScore_evaluateCapture_naturalOrder() {
		// basic - with available attackers
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testOutcome_naturalOrder(Square.E4, Player.WHITE, OutcomeEnum.POSITIVE,new String[] {"e4", "f3"});

		test_game.loadFromFEN("8/3k2n1/4q3/7p/5N2/1p2R2P/2PK3P/8 w - - 0 1");
		testOutcome_naturalOrder(Square.B3, Player.WHITE, OutcomeEnum.POSITIVE,new String[] {"b3", "c2"});
		assertEquals("{c2 e3 e6 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		testOutcome_naturalOrder(Square.H5, Player.WHITE, OutcomeEnum.NEGATIVE,new String[] {"h5", "f4", "g7"});
		testOutcome_naturalOrder(Square.E6, Player.WHITE, OutcomeEnum.POSITIVE,new String[] {"e6", "f4" });
		testOutcome_naturalOrder(Square.E3, Player.BLACK, OutcomeEnum.NEGATIVE,new String[] {"e3", "e6", "d2"  });
		testOutcome_naturalOrder(Square.C2, Player.BLACK, OutcomeEnum.NEUTRAL,new String[] {"c2", "b3", "d2"});
		// no available attackers
		test_naturalOrder_noExhange(Square.G7, Player.WHITE, PieceType.NO_PIECE);
		test_naturalOrder_noExhange(Square.H2, Player.BLACK, PieceType.NO_PIECE);
		test_game.loadFromFEN("4rk2/4nppp/8/5n2/8/P2Q4/1PP5/1K6 w - - 0 1");
		test_naturalOrder_noExhange(Square.C2, Player.BLACK, PieceType.NO_PIECE);
		
		test_game.loadFromFEN("r5k1/5ppp/8/8/1pP5/P7/1PN2PP1/6K1 w - - 0 1");
		testOutcome_naturalOrder(Square.A3, Player.BLACK, OutcomeEnum.NEUTRAL,new String[] {"a3", "b4", "b2"});
		assertEquals("{b2 c2 b4 a8 }", Bitboard.toListString(test_eval.get_output_attackStackSquares()));

		
		
		if (skipAssertions)
			fail("Assertions skipped! We must be running some experiments...");
	}

}
