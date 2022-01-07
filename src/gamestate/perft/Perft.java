package gamestate.perft;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.Move;
import gamestate.MoveGen;

class Perft {

	private static int[] test_movelist = new int[500];
	private int test_movelist_size = 0;
	private static Board test_board = new Board();

	public long testPerft(String fen, int depth) {

		test_board.loadFromFEN(fen);
		return perft(test_board, depth, 1);
	}

	private long perft(Board board, int depth, int ply) {

		if (depth == 0) {
			return 1;
		}
		long time = 0;
		if (ply == 1) {
			time = System.currentTimeMillis();
		}
		long nodes = 0;
		long partialNodes;

		int test_movelist_size_old = test_movelist_size;
		test_movelist_size = MoveGen.generateLegalMoves(test_board, test_movelist, test_movelist_size);
		
		if(depth == 1) {
			int ret = test_movelist_size - test_movelist_size_old;
			test_movelist_size = test_movelist_size_old;
			return ret;
		}

		for (int i = test_movelist_size_old; i < test_movelist_size; ++i) {
			int move = test_movelist[i];
			board.makeMove(move);

			partialNodes = perft(board, depth - 1, ply + 1);
			nodes += partialNodes;
	//		if (ply == 1)
	//			System.out.println(Move.moveToString(move) + ": " + partialNodes);
		
			board.unmakeMove(move);

		}
		test_movelist_size = test_movelist_size_old;
		if (ply == 1) {
		//	System.out.println("Node count: " + nodes);
		//	System.out.println("Time: " + (System.currentTimeMillis() - time) + " milliseconds");
			System.out.println("Speed: " + String.format("%.2f", ((double) nodes / (double) (System.currentTimeMillis() - time)) / 1000.0) + " Million Nodes/Second");

		}
		return nodes;
	}

	@Test
	void testPerft() {
		// see https://www.chessprogramming.org/Perft_Results
		
		//currently runs in about 45 seconds
		assertEquals(119060324L, testPerft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6));//starting 
		assertEquals(193690690L, testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5));//Kiwipete 
		assertEquals(178633661L, testPerft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 7));
		assertEquals(15833292L, testPerft("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ", 5));
		assertEquals(89941194L, testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 5));
		assertEquals(164075551L, testPerft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 5));
		
		//TalkChess PERFT Tests (by Martin Sedlak)
		//https://www.chessprogramming.net/perfect-perft/
//		assertEquals(1134888, testPerft("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1", 6));
//		assertEquals(1015133, testPerft("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1", 6));
//		assertEquals(1440467, testPerft("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1", 6));
//		assertEquals(661072, testPerft("5k2/8/8/8/8/8/8/4K2R w K - 0 1", 6));
//		assertEquals(803711, testPerft("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1", 6));
//		assertEquals(1274206, testPerft("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 4));
//		assertEquals(1720476, testPerft("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1", 4));
//		assertEquals(3821001, testPerft("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1", 6));
//		
//		assertEquals(1004658, testPerft("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1", 5));
//		assertEquals(217342, testPerft("4k3/1P6/8/8/8/8/K7/8 w - - 0 1", 6));
//		assertEquals(92683, testPerft("8/P1k5/K7/8/8/8/8/8 w - - 0 1", 6));
//		assertEquals(2217, testPerft("K1k5/8/P7/8/8/8/8/8 w - - 0 1", 6));
//		assertEquals(567584    , testPerft("8/k1P5/8/1K6/8/8/8/8 w - - 0 1", 7));
//		assertEquals(23527, testPerft("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1", 4));
		
	//	assertEquals(89941194L, testPerft("", 5));	
		
//		assertEquals(3195901860L, testPerft("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 7));//starting 
//		assertEquals(8031647685L, testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 6));//Kiwipete 
//		assertEquals(178633661L, testPerft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 7));
//		assertEquals(706045033L, testPerft("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ", 6));
//		assertEquals(89941194, testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 5));
//		assertEquals(6923051137L, testPerft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 6));
		
	}

}
