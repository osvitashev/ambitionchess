package perft;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;

public class PerftTest {

	private static MovePool movepool = new MovePool();
	private static Gamestate test_board = new Gamestate();
	private static MoveGen test_move_generator = new MoveGen();

	public static long testPerft(String fen, int depth) {

		test_board.loadFromFEN(fen);
		return perft(test_board, depth, 1);
	}

	public static long perft(Gamestate board, int depth, int ply) {

		if (depth == 0) {
			return 1;
		}
		long time = 0;
		if (ply == 1) {
			time = System.currentTimeMillis();
		}
		long nodes = 0;
		long partialNodes;

		int test_movelist_size_old = movepool.size();
		test_move_generator.generateLegalMoves(test_board, movepool);

		if (depth == 1) {
			int ret = movepool.size() - test_movelist_size_old;
			movepool.resize(test_movelist_size_old);
			return ret;
		}

		for (int i = test_movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			board.makeMove(move);

			partialNodes = perft(board, depth - 1, ply + 1);
			nodes += partialNodes;
			// if (ply == 1)
			// System.out.println(Move.moveToString(move) + ": " + partialNodes);

			board.unmakeMove(move);

		}
		movepool.resize(test_movelist_size_old);
		if (ply == 1) {
			// System.out.println("Node count: " + nodes);
			// System.out.println("Time: " + (System.currentTimeMillis() - time) + "
			// milliseconds");
			String format = "%-75s%s%n";
			System.out.printf(format, board.toFEN(), ("Speed: " + String.format("%.2f", ((double) nodes / (double) (System.currentTimeMillis() - time)) / 1000.0) + " Million Nodes/Second"));

			// System.out.println(board.toFEN() +"\t\t\t\t" + "Speed: " +
			// String.format("%.2f", ((double) nodes / (double) (System.currentTimeMillis()
			// - time)) / 1000.0) + " Million Nodes/Second");

		}
		return nodes;
	}

	@Test
	void testBasicPerft() {
		// see https://www.chessprogramming.org/Perft_Results
		// currently runs in about 114 seconds for the below 10 positions.
		assertEquals(119060324L, testPerft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6));// starting
		assertEquals(193690690L, testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5));// Kiwipete
		assertEquals(178633661L, testPerft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 7));
		assertEquals(15833292L, testPerft("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ", 5));
		assertEquals(89941194L, testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 5));
		assertEquals(164075551L, testPerft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 5));
		// https://sites.google.com/site/numptychess/perft/position-2
		assertEquals(8103790L, testPerft("8/p7/8/1P6/K1k3p1/6P1/7P/8 w - - 0 1", 8));
		assertEquals(77054993, testPerft("r3k2r/p6p/8/B7/1pp1p3/3b4/P6P/R3K2R w KQkq - 0 1", 6));
		assertEquals(64451405, testPerft("8/5p2/8/2k3P1/p3K3/8/1P6/8 b - - 0 1", 8));
		assertEquals(867640754, testPerft("r3k2r/pb3p2/5npp/n2p4/1p1PPB2/6P1/P2N1PBP/R3K2R b KQkq - 0 1", 6));
	}

	@Test
	void testSedlakPerft() {
		// TalkChess PERFT Tests (by Martin Sedlak)
		// https://www.chessprogramming.net/perfect-perft/
		assertEquals(1134888, testPerft("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1", 6));
		assertEquals(1015133, testPerft("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1", 6));
		assertEquals(1440467, testPerft("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1", 6));
		assertEquals(661072, testPerft("5k2/8/8/8/8/8/8/4K2R w K - 0 1", 6));
		assertEquals(803711, testPerft("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1", 6));
		assertEquals(1274206, testPerft("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 4));
		assertEquals(1720476, testPerft("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1", 4));
		assertEquals(3821001, testPerft("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1", 6));

		assertEquals(1004658, testPerft("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1", 5));
		assertEquals(217342, testPerft("4k3/1P6/8/8/8/8/K7/8 w - - 0 1", 6));
		assertEquals(92683, testPerft("8/P1k5/K7/8/8/8/8/8 w - - 0 1", 6));
		assertEquals(5966690, testPerft("K1k5/8/P7/8/8/8/8/8 w - - 0 1", 10));
		assertEquals(567584, testPerft("8/k1P5/8/1K6/8/8/8/8 w - - 0 1", 7));
		assertEquals(23527, testPerft("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1", 4));
	}

	@Test
	void testMyPinsAndChecks() {
		// my own test with lots of pins on king
		assertEquals(179240304L, testPerft("1k3b2/qprp4/1PPP2p1/rPKPnpP1/1P1P4/8/5b1P/N7 b - - 0 1", 7));
		assertEquals(423636038L, testPerft("1n1R4/5B2/3bn3/3k1pR1/3p4/1r6/Q2PP3/K2R4 w - - 0 1", 6));
		assertEquals(374148198L, testPerft("1n1R4/5B2/3bn3/3k1pR1/3p4/1r6/Q2PP3/K2R4 b - - 0 1", 6));
		assertEquals(87497228L, testPerft("nq1k4/pr5r/6pp/5p2/1B6/5Q2/PPP2BPP/R3K2R w KQ - 0 1", 5));
		assertEquals(59469411L, testPerft("nq1k4/pr5r/6pp/5p2/1B6/5Q2/PPP2BPP/R3K2R b KQ - 0 1", 5));
//		//positions with emergent pins
		assertEquals(69528368L, testPerft("1k3r2/8/8/4KB2/8/2P5/b2N4/8 w - - 0 1", 6));
		assertEquals(1374435192L, testPerft("1k3r2/8/8/4KB2/8/2P5/b2N4/8 b - - 0 1", 7));
		assertEquals(175769900L, testPerft("4n3/b2R4/7k/4p3/1N6/r7/3P2K1/2B5 w - - 0 1", 6));
		assertEquals(215475897L, testPerft("4n3/b2R4/7k/4p3/1N6/r7/3P2K1/2B5 b - - 0 1", 6));

		assertEquals(7961114L, testPerft("8/8/2b1k3/8/8/3K4/8/3B4 w - - 0 1", 6));
		assertEquals(8511245L, testPerft("8/8/2b1k3/8/8/3K4/8/3B4 b - - 0 1", 6));
		assertEquals(8000133L, testPerft("8/kpb5/8/8/8/8/3BPK2/8 w - - 0 1", 6));
		assertEquals(7819044L, testPerft("8/kpb5/8/8/8/8/3BPK2/8 b - - 0 1", 6));
		assertEquals(5771045L, testPerft("8/2n5/2k5/8/4N3/8/4PKB1/8 w - - 0 1", 6));
		assertEquals(6829670L, testPerft("8/2n5/2k5/8/4N3/8/4PKB1/8 b - - 0 1", 6));
		assertEquals(78099677L, testPerft("8/3r2k1/8/4n3/3P4/R7/3K4/8 w - - 0 1", 6));
		assertEquals(77163234L, testPerft("8/3r2k1/8/4n3/3P4/R7/3K4/8 b - - 0 1", 6));
		assertEquals(166959286L, testPerft("8/b1Rp2k1/4B3/8/5r2/K7/8/8 w - - 0 1", 6));
		assertEquals(159000702L, testPerft("8/b1Rp2k1/4B3/8/5r2/K7/8/8 b - - 0 1", 6));
		assertEquals(1978612L, testPerft("k7/8/6b1/8/4p3/8/2KP2B1/8 w - - 0 1", 6));
		assertEquals(1928450L, testPerft("k7/8/6b1/8/4p3/8/2KP2B1/8 b - - 0 1", 6));
		assertEquals(26276289L, testPerft("8/4r3/8/8/R3p1k1/8/3P4/4K3 w - - 0 1", 6));
		assertEquals(26159197L, testPerft("8/4r3/8/8/R3p1k1/8/3P4/4K3 b - - 0 1", 6));

	}

}
