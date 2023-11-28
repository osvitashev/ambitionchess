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

class BSEETest_evaluateQuietMoves_perftChecksum {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	ByteBuffer buffer1, buffer2;
	CRC32 crc1, crc2;
	
	String checkSum;
	
	public long testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		crc1 = new CRC32();
		crc2 = new CRC32();
		
		crc1.reset();
		crc2.reset();
		
		buffer1 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.
		buffer2 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.

		long t = perft(test_game, depth, 1);
		checkSum = getChecksum();
		return t;
	}
	
	
	private void updateHashValue() {
		test_eval.initialize();
		test_eval.evaluateQuietMoves();
		
		buffer1.clear();
		buffer2.clear();
		buffer1.mark();
		buffer2.mark();
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				buffer1.putLong(test_eval.getOutput_quiet_neutral(player, pieceType));
				buffer2.putLong(test_eval.getOutput_quiet_losing(player, pieceType));
			}
		}
		
		buffer1.reset();
		buffer2.reset();
		crc1.update(buffer1);
		crc2.update(buffer2);
	}
	
	String getChecksum() {
		return String.format("%08X", crc1.getValue()) + String.format("%08X", crc2.getValue());
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
	
	/**
	 * todo: investigate why perft(0) and perft(1) are returning same checksum...
	 */
	
	@Test
	void testWithPerft_quick() {
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 1, "0855D79288935B83");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 2, "4EAA953D79962EF1");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 1, "05E0039CCD446B8C");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 2, "0497B27F6F67A437");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 3, "B84AED1F9F7357F9");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 4, "FCB9A938222F694B");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 1, "D1D23DAB548709AE");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 2, "18D53AE08C4A08CE");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 3, "DB5B291EB905D6DF");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 4, "0E52D21885DA425C");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 1, "6A4CFA328F862B74");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 2, "9114B74A2BC95358");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 3, "F9C18204BB71BD5E");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 4, "2A1803BC2BF9D2EC");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 1, "3FE9C22BE8AC4734");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 2, "E4DF76B366843BBB");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 3, "C0861FB1B9C8151D");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 4, "7884ED40A1481CD9");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 1, "50F5F9B50BC1FD5F");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 2, "A27AB0C4B0D10D95");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 3, "1D471C49C92B6413");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 4, "097B190CC18732E5");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 1, "72667DD09B085263");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 2, "7E3DCB43E4B12E53");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 3, "9EA31AAA47608A6F");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 4, "FED68C588505B583");	
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 1, "717930C077DF746D");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 2, "97B887A9AA208DB5");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 3, "FB3C333F59EAF807");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 4, "E03DDDC70BB60386");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 1, "9D779E8953C9C682");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 2, "7451C3E2DBEBFAD9");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 3, "31AD36803D0C228C");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 4, "2B24DE53FBA721EE");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 1, "5CCB9EEB4C0C59E9");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 2, "BE97DCBCC052DF33");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 3, "9838D6D71AFA8F1B");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 4, "151182243C1253C0");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 1, "18FA504A609AE621");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 2, "28A13E76BF0C9F6E");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 3, "A0BD7E0E1F6B2B99");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 4, "0657983CDC8CCB8A");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 1, "2A857E55BAC34C9F");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 2, "ACF4178BB77557E8");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 3, "76E5F1AE767E8555");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 4, "7712F1508DD98405");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 1, "AF657F41D551B4FA");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 2, "A63827200713A5D6");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 3, "5A98D1C588B1F34E");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 4, "55A0E36BF99335D4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 1, "AC744126681A5CF4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 2, "FF02460B6DE9EDAF");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 3, "417EB52A93C3613D");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 4, "06427B7340FDBE43");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 1, "AD9F1F66F1C181FA");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 2, "B53EB265F51C67E7");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 3, "2B13DBE256E6CEAC");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 4, "61CEE2250E3A370C");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 1, "117C152EA0E1C0F7");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 2, "9A754D41F4FD9A1F");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 3, "4B603384A4D5676A");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 4, "311013F2922EFEDB");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 1, "7A4259182C21EA0F");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 2, "EB2F021866716E41");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 3, "6ED425D3B49BFC37");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 4, "856AC6B5E26C1A7F");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 1, "CF603DC02C722788");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 2, "D25F648A6F8BAC9C");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 3, "B311D4355AD69EC5");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 4, "5BA36B5044B2DEFB");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 1, "3D6A6AF9BAA3EF00");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 2, "C699F8BE036E53D5");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 3, "7B84305574A47DE9");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 4, "F69F58154C1763B5");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 1, "9E5531CCB56EB8F6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 2, "4F4CF55655FB63C6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 3, "5704152DF8D9EE63");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 4, "43A42F5C68EDD29F");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 1, "8AAFFF859A63CB27");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 2, "68E85107FD81510D");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 3, "7BA52C593CE775D4");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 4, "EB18B86F1921306F");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 1, "F5DDAE138516D6F9");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 2, "EB5749ABC102A5E1");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 3, "A07357E358521788");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 4, "F98984C81A984D4D");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 1, "1F2A5D2B79AD9A0B");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 2, "0A00C91C3D21751A");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 3, "9534C5153DEF8D97");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 4, "E0D05DF1FABC5032");
		
	//	test("", 1, "");
		
		if(skipAssertions)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		
	}

}
