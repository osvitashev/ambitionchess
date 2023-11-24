package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class BasicStaticExchangeEvaluatorTest_capturesSpeedTest {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	
	public long testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		attackHash=0;
		long t = perft(test_game, depth, 1);
		System.out.println("crude hash is: " + Long.toHexString(attackHash));
		return t;
	}
	
	
	long attackHash;
	private void payload() {
		test_eval.initialize();
		test_eval.evaluateCaptures();
		
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				attackHash ^= test_eval.getOutput_capture_winning(player, pieceType);
				attackHash ^= test_eval.getOutput_capture_neutral(player, pieceType);
				attackHash ^= test_eval.getOutput_capture_losing(player, pieceType);
			}
		}

	}

	private long perft(Gamestate board, int depth, int ply) {
		payload();
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
		test_move_generator.generateLegalMoves(test_game, movepool);

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

			board.unmakeMove(move);

		}
		movepool.resize(test_movelist_size_old);
		return nodes;
	}	

	
	@Test
	void testWithPerft() {
		testPerft("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 1);
		testPerft("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 2);
		testPerft("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 3);
		testPerft("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 4);
		testPerft("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 5);
		testPerft("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 1);
		testPerft("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 2);
		testPerft("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 3);
		testPerft("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 4);
		testPerft("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 5);
		testPerft("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 1);
		testPerft("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 2);
		testPerft("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 3);
		testPerft("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 4);
		testPerft("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 5);
		testPerft("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 1);
		testPerft("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 2);
		testPerft("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 3);
		testPerft("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 4);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 1);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 2);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 3);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 4);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 5);
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 6);
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3);
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4);
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5);
		
		/**
		 * when profiling:
		 * 
		 * takes 163 seconds total.
		 * 
		 * MoveGen.generateLegalMoves -> 65 seconds -> 42% of runtime
		 * BasicStaticExchangeEvaluator.evaluateCaptures -> 69 seconds -> 44% of runtime
		 * 
		 * The absolute duration in seconds is a poor metric, because it varies with CPU and RAM load.
		 * However, it is useful to look at the ratio of generateLegalMoves to evaluateCaptures.
		 */
		
	}

}
