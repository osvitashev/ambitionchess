package puzzler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.Move;
class MateInX {

	@Test
	void testMateIn3() {
		String[][] tests = {
				{ "2r1k3/1b3ppp/p3p3/1p1n4/4q3/PQ2P1P1/1P2BP1P/5RK1 b - - 0 1", "e4g2" },
				{ "r1q2r1k/p4b2/1p2pP2/3p3p/1P1B1P2/2PB4/P3Q2P/6RK w - - 0 1", "e2h5" },
				{ "1k1r4/ppq2pp1/4p3/1PP5/Q5n1/7r/P4PB1/R1R3K1 b - - 0 1", "h3h1" },
				{ "1B6/NK6/3p4/p2R4/Pk6/1P1P4/B7/8 w - - 0 1", "d5c5" },
				{ "6B1/3N4/2qpN3/P2k4/4pQP1/8/P1P3nK/2b5 w - - 0 1", "f4e3" },
				{ "7b/8/r7/3B4/8/8/8/1kBK2Q1 w - - 0 1", "d1e2" },
				{ "8/1pkn4/p1p4K/7b/P6P/5p2/6r1/5R2 b - - 0 1", "d7f6" },
				{ "6k1/5ppp/Q1b3q1/2P5/PP6/5NB1/3rr1PP/5RK1 b - - 0 1", "e2g2" },
				{ "1bN5/2p2p1Q/4PB2/3k3P/8/P4N1K/P7/n4R2 w - - 0 1", "a3a4" },
				{ "3r3k/p5bp/2p3p1/2q1n3/PQ4P1/2P2pBP/3r1P2/2R1KBR1 b - - 0 1", "c5e3" },
				{ "1r5k/R6p/4pN2/8/3np3/2r3P1/P4KP1/8 b - - 0 1", "b8b2" },
				{ "1r5r/NRpk1ppp/p2bp3/7b/3P1P2/P1N4P/2q2PB1/4QRK1 w - - 1 0", "g2c6" },
				{ "4r2k/p3NR1p/3pb1pB/1p6/2rbP3/8/PPP5/2K4R w - - 1 0", "f7h7" },
				{ "2q1rbk1/8/4np1B/p2pPBp1/bp1P2P1/5NK1/5P2/1Q5R w - - 1 0", "f5e6" },
				{ "4r1k1/3n1ppp/4r3/3n3q/Q2P4/5P2/PP2BP1P/R1B1R1K1 b - - 0 1", "e6g6" },
				{ "4r2k/4R1pp/7N/p6n/qp6/6QP/5PPK/8 w - - 0 1", "g3b3" },
				{ "8/5p1k/3p2q1/3Pp3/4Pn1r/R4Qb1/1P5B/5B1K b - - 0 1", "h4h2" },
				{ "3r1n2/3q1Q2/pp2p1pk/4P3/3PN2b/P7/1B4K1/5R2 w - - 1 0", "b2c1" },
		};
		int move;
		Board brd = new Board();
		PuzzleSolver slv = new PuzzleSolver(3);

		for (int t = 0; t < tests.length; ++t) {
			brd.loadFromFEN(tests[t][0]);
			move = slv.toPlayAndWin(brd);
			assertEquals(tests[t][1], Move.toUCINotation(move));
			System.out.println(Move.moveToString(move));

		}
	}
	
	@Test
	void testMateInX() {
		String[][] tests = {
				{ "r1k3r1/p3RN2/2p3p1/8/1BP5/8/PP4P1/2K5 w - - 1 0", "4", "b4a5" },
				{ "5rk1/pp6/2p1bpp1/2P5/3QN1P1/5PK1/q7/7R w - - 1 0", "4", "e4f6" },
				{ "8/p1p2p2/P4k2/3p1p2/3PPK2/7r/7p/7R b - - 0 1", "4", "d5e4" },
				{ "8/6Qp/1pq3b1/3p1pk1/3P4/P3N1P1/1P3P1K/8 w - - 0 1", "5", "f2f4" },	
				{ "8/P5pp/5p2/3p4/3P4/3K1P2/1kp1n1PP/8 b - - 0 1", "5", "e2f4" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
//				{ "", "", "" },
				
				//slow
				//{ "r5r1/pq3kBp/1p1Q4/2P5/8/7P/5PP1/3R2K1 w - - 1 0", "4", "d6f6" },
				//{ "1k4r1/1p4r1/PPp5/2bbN3/4np1p/7P/6BK/R1R1BB2 b - - 0 1", "5", "g7g2" },
				
		};
		int move;
		Board brd = new Board();
		PuzzleSolver slv = new PuzzleSolver();

		for (int t = 0; t < tests.length; ++t) {
			brd.loadFromFEN(tests[t][0]);
			move = slv.toPlayAndWin(brd, Integer.valueOf(tests[t][1]));
			assertEquals(tests[t][2], Move.toUCINotation(move));
			System.out.println(Move.moveToString(move));

		}
	}

}
