package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class BasicStaticExchangeEvaluatorTest_getCapturetarget_byPieceType_withPerft {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	public long testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		hashValue=0;
		return perft(test_game, depth, 1);
	}
	
	long hashValue=0;
	
	private void updateHashValue() {
		test_eval.initialize();
		test_eval.evaluateCaptures();
		
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				hashValue ^= test_eval.getOutput_capture_winning(player, pieceType);
				hashValue ^= test_eval.getOutput_capture_neutral(player, pieceType);
				hashValue ^= test_eval.getOutput_capture_losing(player, pieceType);
			}
		}
		
	}

	private long perft(Gamestate board, int depth, int ply) {
		updateHashValue();
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
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 1);
		assertEquals(0x19401031041040l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 2);
		assertEquals(0x9003011001040l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 3);
		assertEquals(0x108509201400l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 4);
		assertEquals(0x630C0BDF4149DAD8l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 5);
		assertEquals(0xE6177895C7CFE174l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 6);
		assertEquals(0x5EF8DA9BA0B8D11l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3);
		assertEquals(0xA854513252AF0A8Cl, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4);
		assertEquals(0x2B8D35B7E4748920l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
		testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5);
		assertEquals(0x7BFC19AB2B9440C5l, hashValue);
		System.out.println("collected hashValue: " + String.format("0x%08X", hashValue));
		
	}

}
