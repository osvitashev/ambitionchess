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

class BasicStaticExchangeEvaluatorTest_getCapturetarget_byPieceType_withPerft {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	ByteBuffer buffer1, buffer2, buffer3;
	CRC32 crc1, crc2, crc3;
	
	String checkSum;
	
	public long testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		crc1 = new CRC32();
		crc2 = new CRC32();
		crc3 = new CRC32();
		
		crc1.reset();
		crc2.reset();
		crc3.reset();
		
		buffer1 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.
		buffer2 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.
		buffer3 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.

		long t = perft(test_game, depth, 1);
		checkSum = getChecksum();
		return t;
	}
	
	
	private void updateHashValue() {
		test_eval.initialize();
		test_eval.evaluateCaptures();
		
		buffer1.clear();
		buffer2.clear();
		buffer3.clear();
		buffer1.mark();
		buffer2.mark();
		buffer3.mark();
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				buffer1.putLong(test_eval.getOutput_capture_winning(player, pieceType));
				buffer2.putLong(test_eval.getOutput_capture_neutral(player, pieceType));
				buffer3.putLong(test_eval.getOutput_capture_losing(player, pieceType));
			}
		}
		
		buffer1.reset();
		buffer2.reset();
		buffer3.reset();
		crc1.update(buffer1);
		crc2.update(buffer2);
		crc3.update(buffer3);
	}
	
	String getChecksum() {
		return String.format("%08X", crc1.getValue()) + String.format("%08X", crc2.getValue()) + String.format("%08X", crc3.getValue());
	}

	private long perft(Gamestate board, int depth, int ply) {
		updateHashValue();
		if (depth == 0) {
			return 1;
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
	
	static boolean skipAssertions = false;
	
	void test(String fen, int depth, String expectedChecksum) {
		testPerft(fen, depth);
		if(!skipAssertions)
			assertEquals(expectedChecksum, checkSum);
		System.out.println("collected checkSum: " + checkSum);
	}
	
	@Test
	void testWithPerft() {
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 1, "A5F49E5DBAF465AE9DCE3ACB");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 2, "100C6C5A228FEA769A4FE718");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 3, "9A944BD17F5C8A8EDE1823A2");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 4, "667716A46F1A5B702AC4413F");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 5, "E48A0D1EDEB8350598A60D8B");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 1, "4C0B6FC01EDED72C6328DDA9");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 2, "4274FA368C6DFEE4EBD30D12");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 3, "1E74D0EADE998CF9435A8BF0");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 4, "25834752B48ADAAA5C059BE8");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 5, "6D337D5C0228D775BAA48171");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 1, "AB17EFF87396A4820A21AC29");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 2, "16D2C64A85AFF23B0347FA5F");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 3, "4F3422E2D89A020022470491");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 4, "551A2B6E6D7601D5A2911962");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 5, "C9CF27DB6FA8645FFE05BC32");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 1, "F64F3D965F41F7162348C89F");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 2, "FEBEBC537B1ED76C1DF71CDB");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 3, "A4028713B04A6479131F1648");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 4, "F4006DF1551082EFF20ED06D");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 1, "CE2C1C417A3CB2712F1D3592");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 2, "6B75A467269B5B024B88CE3B");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 3, "E76157EA400B1324E6202E87");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 4, "DB158CF41F6E31D0B583B3A9");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 5, "AE8D53D82465C9C7BA9ADD3D");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 6, "A8CDA178250272C8F518DA8C");
		test("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, "F9B094172EE79C127F62CF17");
		test("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, "3F330006ADACB2F068A62CB1");
		test("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, "5BF3935CCE30BB531B6A61F8");
		
		if(skipAssertions)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		
	}

}
