package basicseeval;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import analysis.Interaction;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class Test_evaluateXrayInteractions {
	private Gamestate test_game = new Gamestate();
	private BroadStaticExchangeEvaluator test_eval = new BroadStaticExchangeEvaluator(test_game);
	
	private void init(String fen) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateCaptures();
	}
	
	String getInteractionsString() {
		String s="";
		for(int i=0; i<test_eval.get_output_xRayInteractions_size();++i)
			s += Interaction.toString(test_eval.get_output_xRayInteractions(i)) + ", ";
		return s;
	}
	
	void assert_xRayInteractions(int [] expected) {
		System.out.println("interactions: " + getInteractionsString());
		String [] expectedStr = new String[test_eval.get_output_xRayInteractions_size()];
		String [] actualStr = new String[test_eval.get_output_xRayInteractions_size()];
		assertEquals(expected.length, test_eval.get_output_xRayInteractions_size());
		for(int i =0; i<test_eval.get_output_xRayInteractions_size();++i) {
			expectedStr[i]=Interaction.toString(expected[i]);
			actualStr[i]=Interaction.toString(test_eval.get_output_xRayInteractions(i));
		}
		Arrays.sort(expectedStr);
		Arrays.sort(actualStr);
		assertArrayEquals(expectedStr, actualStr);
	}
	
	@Test
	void test_individual() {
		init("4k3/3r4/5n2/1Q3n1b/8/P2R3B/1PP5/1K3R2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.F1, Player.WHITE, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.F1, Square.F5, Square.F6),
		});
		
		init("4k3/3r4/5n2/1Q3n1b/8/P2R3B/1PP5/1K3R2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.H3, Player.WHITE, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.H3, Square.F5, Square.D7),
		});
		
		init("4k3/3r4/5n2/1Q3n1b/8/P2R3B/1PP5/1K3R2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.B5, Player.WHITE, PieceType.QUEEN);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.B5, Square.D7, Square.E8),
		});
		
		init("4k3/3r4/8/1Q3n1b/8/P2R3B/1PP5/1K3R2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.B5, Player.WHITE, PieceType.QUEEN);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.B5, Square.D7, Square.E8),
				Interaction.createPin_positive(Square.B5, Square.F5, Square.H5),
		});
		
		init("4k3/3r4/8/1Q3n1b/8/P2R3B/1PP5/1K3R2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.D7, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.B4, Player.WHITE, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.B4, Square.B5, Square.B7),
				Interaction.createPin_positive(Square.B4, Square.F4, Square.G4),
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.H5, Player.WHITE, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive(Square.H5, Square.G5, Square.E5),
		});

		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.E1, Player.WHITE, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive(Square.E1, Square.E2, Square.E5),
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.B5, Player.BLACK, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.B5, Square.E2, Square.F1),
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.F4, Player.BLACK, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.F4, Square.G5, Square.H6),
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.H2, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.E5, Player.BLACK, PieceType.QUEEN);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.E5, Square.E2, Square.E1),
		});
		
		init("8/1k6/7P/1b2q1PR/1R3bp1/8/4N2r/1q2RK2 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.B1, Player.BLACK, PieceType.QUEEN);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.B1, Square.E1, Square.F1),
		});
		
		init("r5k1/5ppp/8/1p6/P7/P7/1P3PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("r5k1/5ppp/8/8/Pp6/P7/1P3PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.A8, Square.A4, Square.A3),
		});
		
		init("r5k1/5ppp/8/8/Pp6/P7/1PN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("r5k1/5ppp/3b4/8/Pp6/P7/1PN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.A8, Square.A4, Square.A3),
		});
		
		init("r5k1/5ppp/3b4/8/Pp6/P3Q3/1PN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("r5k1/4qppp/3b4/8/Pp6/P3Q3/1PN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A8, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.A8, Square.A4, Square.A3),
		});
		
		init("6k1/r3qppp/3b4/8/Pp6/P3Q3/RPN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A7, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		
		init("r5k1/r3qppp/3b4/8/Pp6/P3Q3/RPN2PP1/6K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A7, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.A7, Square.A4, Square.A3),
		});
		
		init("2k2r1q/2P3Q1/n2P1Q2/1n2b3/5R2/6K1/1P6/8 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.E5, Player.BLACK, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.E5, Square.F4, Square.G3),
				Interaction.createPin_positive(Square.E5, Square.D6, Square.C7),
		});
		
		init("1Bk2r1q/2PR2Q1/3P1Q2/4b3/5Q2/6Q1/1P6/B1K5 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.E5, Player.BLACK, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive(Square.E5, Square.F4, Square.G3),
				Interaction.createPin_positive(Square.E5, Square.F6, Square.G7),
				Interaction.createPin_neutral(Square.E5, Square.B2, Square.A1),
		});
		
		init("Q2N3q/PP6/8/8/k6K/8/1R6/B6B w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.H8, Player.BLACK, PieceType.QUEEN);
		assert_xRayInteractions(new int[] {
				Interaction.createPin_neutral(Square.H8, Square.D8, Square.A8),
				Interaction.createPin_positive(Square.H8, Square.B2, Square.A1),
				Interaction.createPin_positive(Square.H8, Square.H4, Square.H1),
		});
		
		init("8/1k6/2n5/4n3/8/2Q5/8/B5K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A1, Player.WHITE, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_neutral(Square.A1, Square.C3, Square.E5),
		});
		
		init("8/1k6/2n5/4r3/8/2Q5/8/B5K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A1, Player.WHITE, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive(Square.A1, Square.C3, Square.E5),
		});
		
		init("8/1k6/2n5/4n3/8/2B5/8/B5K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A1, Player.WHITE, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
		});
		
		init("8/1k6/2n5/4r3/8/2B5/8/B5K1 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A1, Player.WHITE, PieceType.BISHOP);
		assert_xRayInteractions(new int[] {
		});
		
		init("8/8/1k6/8/r1q2Q2/5K1N/8/8 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A4, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive(Square.A4, Square.C4, Square.F4),
		});
		
		init("8/8/1k6/8/r1q2R2/5K1N/8/8 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A4, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_neutral(Square.A4, Square.C4, Square.F4),
		});
		
		init("8/8/1k6/8/r1q2R2/5K2/8/8 w - - 0 1");
		test_eval.evaluateTargetXRayInteractions(Square.A4, Player.BLACK, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});
		

		
		
		init("");
		test_eval.evaluateTargetXRayInteractions(Square.A1, Player.WHITE, PieceType.ROOK);
		assert_xRayInteractions(new int[] {
		});

	}
	
	@Test
	void test_position() {
		init("6n1/P4nk1/1P2B3/7p/1R1b2r1/1RQ5/1K3P2/6Q1 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("g1", "g4", "g7"),
				Interaction.createPin_positive("c3", "d4", "g7"),
				Interaction.createPin_positive("d4", "c3", "b2"),
				Interaction.createPin_positive("d4", "b6", "a7"),
				Interaction.createPin_neutral("e6", "f7", "g8"),
				Interaction.createDiscoveredThreat_neutral("g4", "d4", "b4"),
		});
		
		init("1r2n2N/5NR1/1qr5/2R1b2k/1R3P2/2P2PP1/1K5P/8 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_neutral("b4", "b6", "b8"),
				Interaction.createPin_positive("c5", "e5", "h5"),
				Interaction.createPin_positive("e5", "c3", "b2"),
				Interaction.createPin_neutral("e5", "g7", "h8"),
				Interaction.createPin_positive("b6", "b4", "b2"),
				Interaction.createPin_positive("c6", "c5", "c3"),
				Interaction.createDiscoveredThreat_neutral("b8", "b6", "b4"),
		});
		
		init("b1n4R/k7/1N1b4/2PN4/1N4q1/2n5/3KP3/B2R1N1r w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("d6", "c5", "b4"),
				Interaction.createPin_positive("h8", "c8", "a8"),
		});
		
		init("6r1/1r1b1p2/1ppk4/qq1Q1pr1/p3p3/1p2Rr1p/p2n3K/3n4 b - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("e3", "f3", "h3"),
				Interaction.createPin_positive("d5", "d2", "d1"),
				Interaction.createPin_positive("d5", "b3", "a2"),
				Interaction.createPin_positive("d5", "e4", "f3"),
				Interaction.createPin_neutral("d5", "b5", "a5"),
				Interaction.createPin_positive("d5", "c6", "b7"),
		});
		
		
		init("6r1/1r1b1p2/1ppk4/qq1B1pr1/p3p3/1p2Rr1p/p2n3K/3n4 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("e3", "f3", "h3"),
				Interaction.createPin_positive("d5", "c6", "b7"),
				Interaction.createPin_positive("d5", "b3", "a2"),
				Interaction.createPin_positive("d5", "f7", "g8"),
				Interaction.createPin_positive("d5", "e4", "f3"),
		});
		
		init("1k1r1N2/8/2Rq1B2/1p6/b1NQN3/b3P1R1/7K/8 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive("g3", "e3", "a3"),
				Interaction.createPin_positive("d6", "g3", "h2"),
				Interaction.createDiscoveredThreat_positive("d8", "d6", "d4"),
		});
		
		init("5K1k/p4p2/1n3rpN/n1B2Rp1/3q4/3rb3/pP1R2pP/8 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_neutral("c5", "d4", "e3"),
				Interaction.createDiscoveredThreat_positive("d2", "b2", "a2"),
				Interaction.createDiscoveredThreat_positive("e3", "g5", "h6"),
				Interaction.createDiscoveredThreat_positive("f5", "c5", "a5"),
				Interaction.createDiscoveredThreat_positive("f6", "g6", "h6"),
				Interaction.createDiscoveredThreat_positive("f6", "f7", "f8"),
		});
		
		init("1KB4Q/8/4b3/7P/8/R2r3q/6n1/1k3N2 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("a3", "d3", "h3"),
				Interaction.createPin_positive("h3", "h5", "h8"),
				Interaction.createPin_positive("c8", "e6", "h3"),
				Interaction.createDiscoveredThreat_positive("h3", "g2", "f1"),
				Interaction.createDiscoveredThreat_positive("h8", "h5", "h3"),
		});
		
		init("8/1k2p3/1n1q4/8/1r1P4/8/3Q1Bqr/1K6 w - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createPin_positive("g2", "f2", "d2"),
				Interaction.createPin_positive("d6", "d4", "d2"),
				Interaction.createDiscoveredThreat_neutral("d2", "f2", "g2"),
				Interaction.createDiscoveredThreat_neutral("d2", "d4", "d6"),
				Interaction.createDiscoveredThreat_neutral("f2", "d4", "b6"),
				
		});
		
		init("2k5/3B1br1/2P3q1/3n3N/6P1/1Nn4b/1K6/3rb1R1 b - - 0 1");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
				Interaction.createDiscoveredThreat_positive("d1", "e1", "g1"),
				Interaction.createPin_neutral("g1", "e1", "d1"),
				Interaction.createDiscoveredThreat_positive("g1", "g4", "g6"),
				Interaction.createPin_positive("h3", "g4", "d7"),
				Interaction.createPin_positive("g6", "g4", "g1"),
				Interaction.createDiscoveredThreat_positive("d7", "g4", "h3"),
				Interaction.createDiscoveredThreat_neutral("f7", "d5", "b3"),
				Interaction.createDiscoveredThreat_neutral("f7", "g6", "h5"),
		});
		
		init("");
		test_eval.evaluateXRayInteractions();
		assert_xRayInteractions(new int[] {
		});
	}

}
