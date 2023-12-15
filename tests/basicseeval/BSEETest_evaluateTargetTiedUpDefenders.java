package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import analysis.Interaction;
import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.Utilities.OutcomeEnum;

class BSEETest_evaluateTargetTiedUpDefenders {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private void init(String fen) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateCaptures();
	}
	
	static final boolean skipAssertions = false;
	
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
	void test() {
//		init("8/2k3n1/4b3/8/8/4Q3/8/4K3 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.E6, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createAdequateGuardTiedUp(Square.G7, Square.E6)
//		});
//		init("4r3/2k5/4b3/2n5/8/4Q3/8/4K3 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.E6, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createGuardOverprotect(Square.C5, Square.E6)
//		});
//		//pawn capturing a piece - there are defenders available, but they are not changing the outcome...
//		init("4r3/1B5k/6b1/5q2/Q3r3/3K1PN1/4RR2/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.E4, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
		//many candidates
		init("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
		assertDefenderInteractions(new int[] {
				Interaction.createAdequateGuardTiedUp(Square.F6, Square.D5),
				Interaction.createAdequateGuardTiedUp(Square.C7, Square.D5),
				Interaction.createGuardOverprotect(Square.D7, Square.D5),
				Interaction.createGuardOverprotect(Square.D8, Square.D5),
				Interaction.createGuardOverprotect(Square.F3, Square.D5),
				Interaction.createGuardOverprotect(Square.G2, Square.D5),
		});
//		//single defender
//		init("8/2k5/8/4p3/1K1b4/8/4N3/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D4, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createAdequateGuardTiedUp(Square.E5, Square.D4),
//		});
//		//capture is initially viable - removing the defender does not help it.
//		init("8/2k5/8/4p3/3b4/3K4/4N3/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D4, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		//no recapture available!
//		init("8/1k6/4q3/8/6Q1/8/1K6/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.G4, Player.BLACK);
//		assertDefenderInteractions(new int[] {});
//		
//		init("8/1k2n3/8/3r4/8/4N3/1K6/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		init("3r4/1k2n3/8/3r4/8/4N3/1K6/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		
//		init("3r4/1k2n3/4p3/3r4/8/4N3/1K6/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		init("3r4/1k2n3/4p3/3b4/8/4N3/1K6/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D5, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createGuardOverprotect(Square.E6, Square.D5),
//		});
//		//there are multiple redundant defenders, but only the first one is counted towards providing explicit over-protection.
//		init("5n2/2k5/3r2r1/8/2nR4/8/8/2K5 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D6, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createGuardOverprotect(Square.C4, Square.D6),
//		});
//		init("8/1k6/b7/1b6/2q5/3q1R2/5N1K/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		//edge case: skipping a defender actually improves the score for the defender (positive -> neutral)
//		init("8/1k6/b7/1b6/2q5/3n1R2/5N1K/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		//edge case: this would have been (positive -> negative), but because attacker is cheaper than victim, we are skipping the evaluation.
//		init("8/1k6/b7/1b6/2q5/3r1R2/5N1K/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		//edge case: skipping a defender actually improves the score for the defender (positive -> negative)
//		init("8/1k6/b7/1b6/2q5/3p1R2/5N1K/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.D3, Player.WHITE);
//		assertDefenderInteractions(new int[] {});
//		
		
		//add cases for the 9 possible transitions!
		
//		//negative->negative
//		init("8/1k1n4/8/r1r5/8/4Q3/5K2/8 w - - 0 1");
//		test_eval.evaluateTargetTiedUpDefenders(Square.C5, Player.WHITE);
//		assertDefenderInteractions(new int[] {
//				Interaction.createGuardOverprotect(Square.D7, Square.C5),
//		});
		//negative->neutral
		
		//double check 3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2 [w x d5]
		
		
//		init("3r4/1p2n1k1/p2r4/3b1n2/8/n2Q4/3R4/3R2K1 w - - 0 1");
//		test(Square.D5, Player.WHITE);

		
		//a lot of candidates generated
//		test_game.loadFromFEN("3r2b1/ppnr4/1N2knpp/2Kp1p2/1N6/1B1Q1qp1/PP4bP/3R4 b - - 1 2");
//		test_eval.initialize();
//		test_eval.evaluateCaptures();
//		test(Square.D5, Player.WHITE);
	}

}
