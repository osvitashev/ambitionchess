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

class BSEETest_evaluateCaptures_perftChecksum {
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
		//System.out.println("collected checkSum: " + checkSum);
		System.out.println("test(\"" +fen +"\", "+ depth+", \""+ checkSum+ "\");");
	}
	
	@Test
	void testWithPerft_quick() {
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 1, "A5F49E5DBAF465AE9DCE3ACB");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 2, "100C6C5A228FEA769A4FE718");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 3, "9A944BD17F5C8A8EDE1823A2");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 4, "667716A46F1A5B702AC4413F");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 1, "4C0B6FC01EDED72C6328DDA9");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 2, "4274FA368C6DFEE4EBD30D12");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 3, "1E74D0EADE998CF9435A8BF0");
		test("3r2b1/ppnr4/1Np1knpp/3R1p2/1N6/1BKQ1qp1/PP4bP/3R4 w - - 0 1", 4, "25834752B48ADAAA5C059BE8");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 1, "AB17EFF87396A4820A21AC29");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 2, "16D2C64A85AFF23B0347FA5F");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 3, "4F3422E2D89A020022470491");
		test("1k6/1pp2npb/r2N1rq1/8/2p1N3/3R2P1/QP3P2/3R2K1 w - - 0 1", 4, "551A2B6E6D7601D5A2911962");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 1, "F64F3D965F41F7162348C89F");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 2, "FEBEBC537B1ED76C1DF71CDB");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 3, "A4028713B04A6479131F1648");
		test("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1", 4, "F4006DF1551082EFF20ED06D");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 1, "CE2C1C417A3CB2712F1D3592");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 2, "6B75A467269B5B024B88CE3B");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 3, "E76157EA400B1324E6202E87");
		test("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1", 4, "DB158CF41F6E31D0B583B3A9");
		test("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 3, "F9B094172EE79C127F62CF17");
		test("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4, "3F330006ADACB2F068A62CB1");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 1, "01004DF5BAF465AED900B10A");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 2, "BE328BA923FD4782114FA414");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 3, "C756FA83DAEFC2800D37F969");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 4, "A6E5860FC54559D2394E3D7D");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 1, "BAF465AEBAF465AE6E1CB653");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 2, "AEDEBE202787B35DDE1AA70E");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 3, "F153D50002B7B8CB1E55EB1A");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 4, "2FD6AF138E5ABC5B1D457460");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 1, "A6D3FDBBBAF465AE55E1B6DE");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 2, "AC1E35056C855EFDAF206020");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 3, "F48FDFBA7F9C961E24C46F94");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 4, "8BEE283EB480154ABB2F1E60");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 1, "BAF465AEC204B8EFB75B8D46");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 2, "2858192C092244F7D0CC5F51");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 3, "6412E246186CA082CA39F85C");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 4, "7C2B144CCE15914E8C8DC8B7");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 1, "6EBB389DAE0F930A5BB507F2");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 2, "1E06EC9CAC38044B2FEBD672");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 3, "551C6EDCFBE04544956BFAB1");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 4, "D22E135714D5F72928B6D4E6");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 1, "30CBBC2BBAF465AEF75594AC");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 2, "CF1D35A8D55895A9E2C18565");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 3, "FA975CE3F3172B738F194F71");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 4, "EDC68D3423C9B00B54E76F01");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 1, "3A257C26BAF465AEA217EBA0");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 2, "363B04ED5B3F99D4E33C18FC");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 3, "4BCFA218208328D6C020B258");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 4, "7C43A824C08BB5AA844822CD");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 1, "DCE47EC27E2E028DEC15DE60");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 2, "C2F463E3E88215538D8CD8E2");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 3, "3BD27A60C9CAA1B5C75D7120");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 4, "A2AD3530E049CA37982D6564");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 1, "D7E88ADA27421BF7322D112E");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 2, "00B891C7A2D0132F2889C0BE");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 3, "4DE0FE86584E46C8D5E9918D");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 4, "51D7C9B35E5326352411CABC");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 1, "BAF465AEBAF465AED279B078");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 2, "5545B450FB74E40022814D04");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 3, "0301BAA73F73DF68F4109787");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 4, "0ECD9057446FEAFFBDD4C9FE");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 1, "BAF465AEBAF465AEBAF465AE");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 2, "26AEFE1F6D16C9485908A0DD");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 3, "645435675ABA5259A3E62755");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 4, "CA1AD4EB911538427F28C4AE");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 1, "BAF465AE272FB31C86B4E7E4");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 2, "BE3C48B58AFB4D6F92CEB65C");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 3, "0B4AA0E808913ADD68D04C90");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 4, "3BFABA230EEFCFE528762833");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 1, "19AF0A0EBAF465AE2CA302C4");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 2, "60E36FAB36D109EACBDB9D63");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 3, "71A37F69A95F748766F4AD72");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 4, "2B24CF1DE721CA7C8691151C");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 1, "55A6D34FBAF465AEE27C00D4");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 2, "251975DBE9719EA062640E53");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 3, "E346C3084916165DDF97A3F0");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 4, "8E1B29720A2983BADC0F6ED6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 1, "B6E2968FBAF465AE0600B951");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 2, "44CB4D893A7A8EE2FFEDFDE9");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 3, "0689B5F746D9EC52077C0259");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 4, "2B92EA2AB0063FD69D17411E");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 1, "BAF465AEBAF465AEBAF465AE");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 2, "8AF9B677D0DDABC04A34291C");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 3, "BCCF153872B87ED024827959");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 4, "EBA9C4CB1ACC3EE63EEF7355");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 1, "00CCB226BAF465AE1CD5C3BF");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 2, "31F049A3C9B0BA713B35BD95");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 3, "741950D0300924B4E683EEBB");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 4, "9C96800ABDF720E9B9BEC91B");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 1, "6F4A213DBAF465AE326E360C");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 2, "E453F9154718593DA1B68A77");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 3, "ACC181FECF8C3A18C2E8FE97");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 4, "BC5D9C60C6F894CB6972D7E0");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 1, "BAF465AEBAF465AEBAF465AE");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 2, "F5A0F415F5A0F415F5A0F415");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 3, "10B8E4F410B8E4F410B8E4F4");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 4, "5DD3E2B45DD3E2B45DD3E2B4");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 1, "BAF465AEF1B75E2D6C5BFBAF");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 2, "F5293CEDD0C3B6DAF65BED14");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 3, "CA43A64B5BCDE9A63CEF16C5");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 4, "FC360F6DDCC014953BFB5AD2");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 1, "6B2D1C3A7F2BA012E64D9D00");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 2, "EC4F13C9EF632D084909B2B1");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 3, "9397D723C533833FE6212A12");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 4, "806C9A8EA913FBA3A3D83768");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 1, "659DB3B3272FB31C0CEF85DE");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 2, "7764FC5DC8A9DB255BCE686A");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 3, "E925858F5A32C5B7690898FA");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 4, "79775ED2F00CCDD73979DCA0");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 1, "BAF465AEBAF465AE9E58F779");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 2, "3384290D2436DCC03654AF9B");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 3, "1CD0A3DD83848D42008A67D1");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 4, "2E10C1CBEE153BA0A11756CA");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 1, "326E360CBAF465AE280CB279");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 2, "4BDAF1796D368DAB11ED96BA");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 3, "2CBD461558A522D3C2CA7AC8");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 4, "FC35F52E4EDB46D9A404524B");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 1, "F8647D03BAF465AE5A866710");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 2, "07E59A3818FFCAFC52C2F72B");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 3, "04F0C87EFAE355BF707DAA33");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 4, "52E9A29AD5CF672791B1DEC3");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 1, "BAF465AEAEF3FA6FEE432D04");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 2, "AC76D8B368DE7EFF36CC8AC0");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 3, "BAA6AF7022D9D2F3D3A22023");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 4, "30EBAC8D4A8F8223FF6FF6C1");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 1, "5C76262A34D38858CEC8531E");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 2, "2F2311172CC643EA12D6CEAC");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 3, "EA6C8B51426280BD7A342621");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 4, "84B675EB27FE5CEEDAD07859");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 1, "7809854ADAD8D0633EA1D491");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 2, "6CE63F323F6285B1AFAEB3F3");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 3, "FE4EF17734282877210203A7");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 4, "B8A6A811EB58C26AD832D514");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 1, "614DC4E9BAF465AEBAF465AE");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 2, "AA34F60C8D84135D0444E8E3");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 3, "32D3352484F72F1D22C9F024");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 4, "B7E7881113EA6B45B4DA3802");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 1, "E1F99596BAF465AE89704488");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 2, "E8AC95205F7CC5781DEC54CD");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 3, "ACC940A6B8521EA12D0ED2F4");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 4, "69273A9387EB0745AD751C88");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 1, "19516AC19C98B7721B8C830E");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 2, "96F057C5589F7BD63D6D2968");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 3, "08EE665F0A84A9DBC0EC0D37");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 4, "36BCE16340F5B9CE9545F1F5");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 1, "2110577767EC532CE1DB1EE9");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 2, "36A851FFA22851F19BB811CC");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 3, "8ECEE7F64F4382F03075A33C");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 4, "1B4B94B50E14FD03C49F5091");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 1, "BAF465AE27B695A55D5A8BD5");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 2, "DB2A52507A2939FBAB112FC7");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 3, "D707357B7333D601C26E4B9F");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 4, "BEAB781488AC2C9E49303D93");

		
		if(skipAssertions)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		
	}

}
