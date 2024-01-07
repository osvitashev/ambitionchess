package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.Utilities.OutcomeEnum;

class BSEETest_evaluateQuiet_target {
	private Gamestate test_game = new Gamestate();
	private TargetStaticExchangeEvaluator test_eval = new TargetStaticExchangeEvaluator(test_game);
	//private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, tSee);
	
	static final boolean skipAssertions = false;
	
	void test(int expectedOutcome, int sq, int player, int pieceType) {
		test_eval.evaluateTargetExchange(sq, player, 0l, pieceType);
		int outcome = test_eval.get_output_ExpectedGain();
		if(!skipAssertions) {
			if(expectedOutcome > 0)
				fail();
			else if(expectedOutcome <0)
				assertTrue(outcome < 0);
			else
				assertEquals(0, outcome);
		}
	}
	
	@Test
	void testOutcome_evaluateQuiet_forcedAttacker() {
		//basic
		test_game.loadFromFEN("1k3r2/8/8/3p4/8/6K1/3P4/3R4 w - - 0 1");
		test(OutcomeEnum.NEGATIVE, Square.F3, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEUTRAL, Square.H3, Player.WHITE, PieceType.KING);
		
		test_game.loadFromFEN("5r2/1k2r3/3b3p/6p1/3Q4/1N4P1/1PP3K1/8 w - - 0 1");
		test(OutcomeEnum.NEUTRAL, Square.A5, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEUTRAL, Square.C5, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEGATIVE, Square.A7, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.E5, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.F4, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.H4, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.A3, Player.BLACK, PieceType.BISHOP);
		test(OutcomeEnum.NEUTRAL, Square.C7, Player.BLACK, PieceType.BISHOP);
		test(OutcomeEnum.NEGATIVE, Square.F4, Player.BLACK, PieceType.BISHOP);
		test(OutcomeEnum.NEUTRAL, Square.E1, Player.BLACK, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.E4, Player.BLACK, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.E5, Player.BLACK, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.F4, Player.BLACK, PieceType.ROOK);
		
		//doomed running away case
		test_game.loadFromFEN("8/2k1q3/8/8/4R3/8/3B4/6K1 w - - 0 1");
		test(OutcomeEnum.NEUTRAL, Square.E1, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.E2, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.E3, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.E6, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.B4, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.H4, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.B4, Player.WHITE, PieceType.BISHOP);
		test(OutcomeEnum.NEGATIVE, Square.G5, Player.WHITE, PieceType.BISHOP);
		
		test(OutcomeEnum.NEGATIVE, Square.B4, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.E8, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.E5, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.G5, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.H4, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEUTRAL, Square.G7, Player.BLACK, PieceType.QUEEN);
		
		//pawn pushes
		test_game.loadFromFEN("8/3kp1pp/1P3p2/3r4/P7/8/1PQP4/2K5 w - - 0 1");
		test(OutcomeEnum.NEGATIVE, Square.A5, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.B7, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.B3, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.B4, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.D3, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEGATIVE, Square.D4, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.E6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.E5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.G6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.G5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.H6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.H5, Player.BLACK, PieceType.PAWN);
		
		test_game.loadFromFEN("2k2q2/1q1pnppp/npp1p3/4n2b/2P4N/P1PB2R1/1PN1PPP1/2K5 w - - 0 1");
		test(OutcomeEnum.NEUTRAL, Square.B4, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F3, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F4, Player.WHITE, PieceType.PAWN);
		
		test(OutcomeEnum.NEUTRAL, Square.B5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.C5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.D5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.G6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEGATIVE, Square.G5, Player.BLACK, PieceType.PAWN);
		
		//generic
		test_game.loadFromFEN("8/ppq2p1k/3q3p/P2n4/N5R1/1K6/1Q1N2P1/6B1 w - - 0 1");
		test(OutcomeEnum.NEGATIVE, Square.A6, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEGATIVE, Square.B6, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEUTRAL, Square.C5, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEGATIVE, Square.B4, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.C4, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.C2, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEUTRAL, Square.A2, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEUTRAL, Square.C4, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEUTRAL, Square.E4, Player.WHITE, PieceType.KNIGHT);
		test(OutcomeEnum.NEUTRAL, Square.A3, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.C1, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.F6, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEUTRAL, Square.G7, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.H8, Player.WHITE, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.B6, Player.WHITE, PieceType.BISHOP);
		test(OutcomeEnum.NEUTRAL, Square.C5, Player.WHITE, PieceType.BISHOP);
		test(OutcomeEnum.NEGATIVE, Square.H2, Player.WHITE, PieceType.BISHOP);
		test(OutcomeEnum.NEUTRAL, Square.G3, Player.WHITE, PieceType.PAWN);
		test(OutcomeEnum.NEGATIVE, Square.G3, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.B4, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.C4, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.F4, Player.WHITE, PieceType.ROOK);
		test(-500, Square.G6, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEUTRAL, Square.G7, Player.WHITE, PieceType.ROOK);
		test(OutcomeEnum.NEGATIVE, Square.G8, Player.WHITE, PieceType.ROOK);
		
		test(OutcomeEnum.NEUTRAL, Square.A6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.B6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.B5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F6, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.F5, Player.BLACK, PieceType.PAWN);
		test(OutcomeEnum.NEUTRAL, Square.H5, Player.BLACK, PieceType.PAWN);
		
		test(OutcomeEnum.NEGATIVE, Square.G7, Player.BLACK, PieceType.KING);
		
		test(OutcomeEnum.NEGATIVE, Square.B6, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.C5, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.C3, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.B4, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.G3, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.H2, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.F4, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEUTRAL, Square.E5, Player.BLACK, PieceType.QUEEN);
		test(OutcomeEnum.NEGATIVE, Square.G6, Player.BLACK, PieceType.QUEEN);
		
		test_game.loadFromFEN("4r3/8/2k5/8/R2K4/8/5B2/8 w - - 0 1");
		test(OutcomeEnum.NEUTRAL, Square.C3, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEUTRAL, Square.C4, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.C5, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEUTRAL, Square.D3, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.D5, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.E3, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.E4, Player.WHITE, PieceType.KING);
		test(OutcomeEnum.NEGATIVE, Square.E5, Player.WHITE, PieceType.KING);
		
		//long sequences
		test_game.loadFromFEN("8/2kr1bb1/3r4/1n1p4/1n3q2/1NPQ4/1K1R1BB1/3R4 b - - 3 2");
		test(OutcomeEnum.NEGATIVE, Square.D4, Player.BLACK, PieceType.PAWN);
		
		test_game.loadFromFEN("8/2kr1bb1/3r1q2/1n1p4/8/1NPQ4/1KnR1BB1/3R4 b - - 3 2");
		test(OutcomeEnum.NEUTRAL, Square.D4, Player.BLACK, PieceType.PAWN);
		
		
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
	void testTrace_evaluateQuiet_forcedAttacker() {
		test_game.loadFromFEN("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.ROOK,// expectedFinalPieceType
				Square.E3, // sq
				Player.WHITE, 
				PieceType.ROOK, // pieceType
				new String[] { "e3", "e2", "e4"});
		
		test_game.loadFromFEN("8/6rk/6q1/8/Q1RQ4/2K5/8/8 w - - 0 1");
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.G4, // sq
				Player.WHITE, 
				PieceType.QUEEN, // pieceType
				new String[] {"g4", "d4", "g6", "c4", "g7", "a4"});
		
		test_game.loadFromFEN("1k6/pp1pp1p1/p1p4p/7Q/q7/1P3P2/P1P1PPPP/2K5 w - - 0 1");
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.A3, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"a3", "a2", "a4"});
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.B4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"b4", "b3", "a4"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.C4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"c4", "c2"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.C3, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"c3", "c2"});
		
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.E4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"e4", "e2"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.E3, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"e3", "e2"});
		
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.F4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"f4", "f3", "a4"});
		
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.G4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"g4", "g2"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.G3, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"g3", "g2"});
		
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.H4, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"h4", "h2"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.H3, // sq
				Player.WHITE, 
				PieceType.PAWN, // pieceType
				new String[] {"h3", "h2"});
		///black
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.A5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"a5", "a6"});
		
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.B6, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"b6", "b7"});
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.B5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"b5", "b7"});
		
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.C5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"c5", "c6", "h5"});
		
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.D6, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"d6", "d7"});
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.D5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"d5", "d7"});
		
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.E6, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"e6", "e7"});
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.E5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"e5", "e7", "h5"});
		
		testPlayerPieceType(Player.WHITE, // expectedFinalPlayer
				PieceType.QUEEN,// expectedFinalPieceType
				Square.G6, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"g6", "g7", "h5"});
		testPlayerPieceType(Player.BLACK, // expectedFinalPlayer
				PieceType.PAWN,// expectedFinalPieceType
				Square.G5, // sq
				Player.BLACK, 
				PieceType.PAWN, // pieceType
				new String[] {"g5", "g7"});
		
		test_game.loadFromFEN("8/pkp5/1p6/PK6/8/8/8/8 w - - 0 1");
		testPlayerPieceType(Player.BLACK, PieceType.PAWN, Square.A6, Player.BLACK, PieceType.PAWN,
				new String[] { "a6", "a7" });
		testPlayerPieceType(Player.BLACK, PieceType.PAWN, Square.C6, Player.BLACK, PieceType.PAWN,
				new String[] { "c6", "c7" });
		testPlayerPieceType(Player.BLACK, PieceType.PAWN, Square.C5, Player.BLACK, PieceType.PAWN,
				new String[] { "c5", "c7" });
		
		//test no available attackers
	}	
}
