package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import analysis.Interaction;
import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.Utilities.OutcomeEnum;

class BSEETest_evaluateBoundDefenders {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private void init(String fen) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateCaptures();
	}
	
	String getInteractionsString() {
		String s="";
		for(int i=0; i<test_eval.get_output_defenderInteractions_size();++i)
			s += Interaction.toString(test_eval.get_output_defenderInteractions(i)) + ", ";
		return s;
	}
	
	void assertDefenderInteractions(int [] expected) {
		System.out.println("interactions: " + getInteractionsString());
		String [] expectedStr = new String[test_eval.get_output_defenderInteractions_size()];
		String [] actualStr = new String[test_eval.get_output_defenderInteractions_size()];
		assertEquals(expected.length, test_eval.get_output_defenderInteractions_size());
		for(int i =0; i<test_eval.get_output_defenderInteractions_size();++i) {
			expectedStr[i]=Interaction.toString(expected[i]);
			actualStr[i]=Interaction.toString(test_eval.get_output_defenderInteractions(i));
		}
		Arrays.sort(expectedStr);
		Arrays.sort(actualStr);
		assertArrayEquals(expectedStr, actualStr);
	}
	
	@Test
	void test_individualTarget() {
		init("8/2k3n1/4b3/8/8/4Q3/8/4K3 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.E6, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.G7, Square.E6)
		});
		init("4r3/2k5/4b3/2n5/8/4Q3/8/4K3 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.E6, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//pawn capturing a piece - there are defenders available, but they are not changing the outcome...
		init("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.E4, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//many candidates
		init("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToNeutral(Square.F6, Square.D5),
				Interaction.createGuardBound_negativeToNeutral(Square.C7, Square.D5),
		});
		//single defender
		init("8/2k5/8/4p3/1K1b4/8/4N3/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D4, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.E5, Square.D4),
		});
		//capture is initially viable - removing the defender does not help it.
		init("8/2k5/8/4p3/3b4/3K4/4N3/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D4, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//no recapture available!
		init("8/1k6/4q3/8/6Q1/8/1K6/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.G4, Player.BLACK);
		assertDefenderInteractions(new int[] {});
		
		init("8/1k2n3/8/3r4/8/4N3/1K6/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		init("3r4/1k2n3/8/3r4/8/4N3/1K6/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		
		init("3r4/1k2n3/4p3/3r4/8/4N3/1K6/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		init("3r4/1k2n3/4p3/3b4/8/4N3/1K6/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//there are multiple redundant defenders, but only the first one is counted towards providing explicit over-protection.
		init("5n2/2k5/3r2r1/8/2nR4/8/8/2K5 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D6, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		init("8/1k6/b7/1b6/2q5/3q1R2/5N1K/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//edge case: skipping a defender actually improves the score for the defender (positive -> neutral)
		init("8/1k6/b7/1b6/2q5/3n1R2/5N1K/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//edge case: this would have been (positive -> negative), but because attacker is cheaper than victim, we are skipping the evaluation.
		init("8/1k6/b7/1b6/2q5/3r1R2/5N1K/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//edge case: skipping a defender actually improves the score for the defender (positive -> negative)
		init("8/1k6/b7/1b6/2q5/3p1R2/5N1K/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		
		//cases for the 9 possible transitions!
		//negative->negative
		init("8/1k1n4/8/r1r5/8/4Q3/5K2/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.C5, Player.WHITE);
		assertDefenderInteractions(new int[] {});
		//negative->neutral
		init("3r4/1p2n1k1/p2r4/3b1n2/8/n2Q4/3R4/3R2K1 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToNeutral(Square.E7, Square.D5),
		});
		//negative->positive
		init("8/8/8/4k3/3R4/4P3/4K3/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D4, Player.BLACK);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.E3, Square.D4),
		});
		//neutral->negative
		//this should not really be the case....
		
		//neutral->neutral
		init("8/5k2/8/3N4/1n2PN2/8/8/3K4 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.BLACK);
		assertDefenderInteractions(new int[] {});
		//neutral->positive
		init("8/4k3/3q4/8/8/2KQ4/8/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D6, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.E7, Square.D6),
		});
		//positive->negative
		//this should never happen.
		
		//positive->neutral
		//this should never happen
		
		//positive->positive
		init("8/1k4n1/4q3/8/8/8/3KR3/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.E6, Player.WHITE);
		assertDefenderInteractions(new int[] {
		});
		//attack with no defenders
		init("8/8/4k3/8/8/3b2R1/3K4/8 w - - 0 1");
		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
		assertDefenderInteractions(new int[] {
		});
		//no attackers - this throws as assertion error.
//		init("8/8/4k3/8/8/3b2R1/3K3P/6R1 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.G3, Player.BLACK);
//		assertDefenderInteractions(new int[] {
//		});
		

		
	}
	
	private void init2(String fen) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
	}
	
	@Test
	void test_position() {
		init2("1r6/3q4/6r1/1p3b2/k7/1r1Q3P/4P2K/6R1 w - - 0 1");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.A4, Square.B3),
				Interaction.createGuardBound_negativeToPositive(Square.D7, Square.F5),
				Interaction.createGuardBound_neutralToPositive(Square.F5, Square.D7),
				Interaction.createGuardBound_neutralToPositive(Square.H2, Square.G1),
		});
		init2("3r2k1/5rpp/2pq4/2p2bNn/p1n1n3/P1P3P1/1P3PBP/R2QKR2 b - - 0 1");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.A1, Square.D1),
				Interaction.createGuardBound_negativeToPositive(Square.B2, Square.C3),
		});
		init2("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.E8, Square.D6),
				Interaction.createGuardBound_negativeToPositive(Square.E7, Square.D6),
				Interaction.createGuardBound_negativeToPositive(Square.F2, Square.G3),
		});
		init2("r1bq1rk1/p3ppbp/1p3np1/2p1n3/3p1P2/N1PP2P1/PPQ1P1BP/R1B2RK1 w - - 0 11");
		assertDefenderInteractions(new int[] {
		});
		init2("5rk1/p3ppbp/6p1/2pB4/3p1Pn1/2Pb2P1/PP5P/R1B2RK1 w - - 0 19");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.G1, Square.H2),
		});
		init2("2rnn1k1/p3b1pp/1p1pPr2/8/2P2PP1/BP5P/P4PB1/3RR1K1 w - - 0 26");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.E8, Square.D6),
				Interaction.createGuardBound_negativeToPositive(Square.E7, Square.D6),
				Interaction.createGuardBound_negativeToPositive(Square.B3, Square.C4),
		});
		init2("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.G8, Square.H7),
		});
		init2("8/8/3k2p1/2n1bp2/1pBp1P2/1P2P1P1/P3K3/3N4 b - - 0 45");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.E5, Square.D4),
		});
		init2("2rr1bk1/5p1p/3np1p1/1p1p4/3P3P/2N1PB2/PPR2PP1/2R3K1 b - - 1 24");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.D6, Square.B5),
				Interaction.createGuardBound_negativeToPositive(Square.E6, Square.D5),
		});
		init2("5k2/2p2r2/1p4Q1/3P2pP/P1P1P3/5pqP/P4R2/5K2 w - - 2 46");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.G3, Square.G5),
				Interaction.createGuardBound_negativeToPositive(Square.C7, Square.B6),
				Interaction.createGuardBound_negativeToPositive(Square.F8, Square.F7),
				Interaction.createGuardBound_negativeToPositive(Square.F1, Square.F2),
		});
		init2("4Rnk1/6rp/4Q3/3p1pP1/P1b5/6q1/6B1/1R5K b - - 10 48");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.G8, Square.F8),
				Interaction.createGuardBound_negativeToPositive(Square.H1, Square.G2),
		});
		init2("r1b2bk1/ppnq1p1p/2n3p1/1BPp4/8/1NN2P2/PP1Q1BPP/4R2K w - - 3 20");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.C7, Square.D5),
				Interaction.createGuardBound_negativeToPositive(Square.D7, Square.D5),
				Interaction.createGuardBound_neutralToPositive(Square.C3, Square.B5),
		});
		init2("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.E4, Square.D3),
				Interaction.createGuardBound_neutralToPositive(Square.B5, Square.D3),
				Interaction.createGuardBound_negativeToPositive(Square.A7, Square.B6),
				Interaction.createGuardBound_negativeToPositive(Square.B1, Square.A2),
				Interaction.createGuardBound_neutralToPositive(Square.E3, Square.F4),
				Interaction.createGuardBound_negativeToPositive(Square.F4, Square.H5),
		});
		init2("r3r1k1/3q1pbp/2p3p1/4p3/p1P1P3/2Rp1P2/PP4PP/2NQ1RK1 b - - 1 22");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.A8, Square.A4),
		});
		init2("4rk2/2p1ppbp/1n1p4/3P1N2/rPPP1P2/p6P/B2R2PK/1R6 b - - 2 27");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.F8, Square.G7),
				Interaction.createGuardBound_negativeToPositive(Square.B1, Square.B4),
				Interaction.createGuardBound_negativeToPositive(Square.A2, Square.C4),
		});
		init2("r1b2rk1/ppp2p2/3p1q1p/3Pb1p1/2P1P1B1/2N1Q2P/PP3PP1/R3K2R b KQ - 4 18");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.A8, Square.A7),
				Interaction.createGuardBound_neutralToPositive(Square.B2, Square.C3),
				Interaction.createGuardBound_neutralToPositive(Square.E3, Square.C3),
				Interaction.createGuardBound_neutralToPositive(Square.H3, Square.G4),
		});
		init2("8/p2q2k1/1pnr1pp1/3Bp3/2P1P1P1/7R/P4PK1/3Q4 w - - 4 42");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.D1, Square.G4),
		});
		init2("2b1r2r/pp4k1/1bp2p2/3p1n2/8/2N2NPB/PP3PP1/3RRK2 b - - 1 30");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.C6, Square.D5),
				Interaction.createGuardBound_neutralToPositive(Square.C8, Square.F5),
				Interaction.createGuardBound_neutralToPositive(Square.H8, Square.E8),
				Interaction.createGuardBound_negativeToPositive(Square.F1, Square.F2),
				Interaction.createGuardBound_negativeToPositive(Square.F2, Square.G3),
				Interaction.createGuardBound_negativeToPositive(Square.G2, Square.H3),
		});
		init2("r3nrk1/pp3pbp/1q2p1p1/4B3/1n1P4/1BN2Q1P/PP3PP1/3RR1K1 w - - 6 17");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.B6, Square.B7),
		});
		init2("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.B4, Square.C5),
				Interaction.createGuardBound_negativeToPositive(Square.C5, Square.A6),
				Interaction.createGuardBound_negativeToPositive(Square.A8, Square.A6),
				Interaction.createGuardBound_negativeToPositive(Square.G8, Square.F7),
				Interaction.createGuardBound_negativeToPositive(Square.E2, Square.B2),
				Interaction.createGuardBound_negativeToPositive(Square.G1, Square.G2),
				Interaction.createGuardBound_negativeToPositive(Square.E2, Square.C4),
				Interaction.createGuardBound_negativeToPositive(Square.A1, Square.A5),
		});
		init2("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.H7, Square.G6),
				Interaction.createGuardBound_negativeToPositive(Square.D3, Square.D4),
				Interaction.createGuardBound_negativeToPositive(Square.F1, Square.F4),
		});
		init2("r2r2k1/1b2qppp/3p1b2/p1nP4/2R1PB2/Q4N1P/5PP1/1N2R1K1 w - - 0 26");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.A8, Square.A5),
				Interaction.createGuardBound_negativeToPositive(Square.D6, Square.C5),
				Interaction.createGuardBound_negativeToPositive(Square.E1, Square.E4),
				Interaction.createGuardBound_negativeToPositive(Square.C4, Square.E4),
				Interaction.createGuardBound_negativeToPositive(Square.E4, Square.D5),
		});
		init2("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.C2, Square.C3),
				Interaction.createGuardBound_negativeToPositive(Square.C3, Square.B5),
				Interaction.createGuardBound_negativeToPositive(Square.G8, Square.F7),
				Interaction.createGuardBound_negativeToPositive(Square.F1, Square.G2),
				Interaction.createGuardBound_negativeToPositive(Square.B3, Square.A3),
		});
		init2("3r2k1/ppp2pp1/2n4p/b1P5/7P/2PqQN2/P2B1PP1/5RK1 w - - 2 23");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.D8, Square.D3),
				Interaction.createGuardBound_negativeToPositive(Square.G7, Square.H6),
				Interaction.createGuardBound_negativeToPositive(Square.G1, Square.F1),
				Interaction.createGuardBound_negativeToPositive(Square.F3, Square.D2),
				Interaction.createGuardBound_negativeToPositive(Square.D2, Square.C3),
				Interaction.createGuardBound_negativeToPositive(Square.E3, Square.C3),
		});
		init2("r4r1k/pp1q2bp/3p2p1/2p2b2/2Pn4/2N1BPP1/PP3PBP/2RQR1K1 w - c6 0 19");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.C5, Square.D4),
				Interaction.createGuardBound_neutralToPositive(Square.G7, Square.D4),
		});
		init2("r1b1k2r/ppq2ppp/2nbp3/3pN3/3PnB2/2PB1N2/PP3PPP/R2QK2R b KQkq - 2 10");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_neutralToPositive(Square.D5, Square.E4),
				Interaction.createGuardBound_negativeToPositive(Square.E1, Square.F2),
				Interaction.createGuardBound_negativeToPositive(Square.B2, Square.C3),
				Interaction.createGuardBound_neutralToPositive(Square.D4, Square.E5),//note: e5 is barely adequately guarded and is vulnerable to additional pawn attack
				Interaction.createGuardBound_neutralToPositive(Square.F3, Square.E5),
				Interaction.createGuardBound_neutralToPositive(Square.F4, Square.E5),
		});
		init2("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.E8, Square.E6),
		});
		init2("3q1r1k/p5bp/br4p1/3B4/3NQ3/R2n2P1/PP1N1PP1/2R3K1 w - - 1 26");
		assertDefenderInteractions(new int[] {
				Interaction.createGuardBound_negativeToPositive(Square.B6, Square.A6),
				Interaction.createGuardBound_neutralToPositive(Square.E4, Square.D4),
				Interaction.createGuardBound_negativeToPositive(Square.E4, Square.D5),
		});
		
		
		
		//////////control case//////////
		init2("");
		assertDefenderInteractions(new int[] {
		});
		
	}

}
