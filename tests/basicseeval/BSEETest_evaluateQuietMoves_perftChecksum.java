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
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 1, "38DFE6E0A378FC55");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 2, "46674793A842756A");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 3, "478891DB6EDF9284");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 4, "7C1E63CB998448ED");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 1, "676C5D51FCF24A9E");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 2, "4D7EBC2E9A480E3B");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 3, "47F23B089849F093");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 4, "2A62B51B439DCF92");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 1, "46584BB7EA071758");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 2, "86101FE4E638FCBA");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 3, "B2C221228ADC244B");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 4, "83D61973904C96A4");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 1, "CFB6722BDC7FFAA8");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 2, "4A058343FBC83C36");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 3, "A19789F645B4AED5");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 4, "271D2F5DBEC65E0A");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 1, "208C902E09C2F3C8");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 2, "97A9442F52391A2E");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 3, "BB3B23C7BDDFDE24");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 4, "BC1563AB3C88D66A");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 1, "F8F4031D8A6B35CF");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 2, "72A6A25D6CE92D26");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 3, "20A72A016A0AA344");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 4, "16F3AF81DAEC77B7");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 1, "D7B6EE1284B9C4DE");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 2, "CFEA719BF5738D4F");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 3, "68202DE75875333B");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 4, "7259535E334D6F88");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 1, "D2733B30B9D9F621");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 2, "D544A6B12CAC87B1");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 3, "2EAE5D5D012558E2");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 4, "D36DA20139AAC24F");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 1, "0F7BF457E67595EB");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 2, "DB7C722801C2B037");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 3, "75DAB6CD086AD467");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 4, "530CC09CD84952D8");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 1, "E4FB9AC8DC29672C");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 2, "D5D95E027156B2C3");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 3, "93BC03F44DB12D23");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 4, "65C3C7801F4FD83B");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 1, "3D3B4272E8F51237");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 2, "D4A01A1736284215");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 3, "8DF2A83BF5227811");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 4, "8B6A77F57212A86B");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 1, "2ABC4C7D289D0F77");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 2, "1B947447499FFB2D");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 3, "D17D3AC0D41D9441");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 4, "B9302AA6EEA6654E");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 1, "33631577E08771D8");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 2, "C3126456A54C6351");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 3, "F86D66768A49EAA6");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 4, "34E6062C6B74D053");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 1, "9CB5D1449D05CEFF");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 2, "EC9B84E31121582F");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 3, "107BB36D35CC44D2");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 4, "43D087DDE28C8119");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 1, "CE441F06B0D492AC");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 2, "3101A9B9BBD27CE0");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 3, "EAB14C1170553AFB");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 4, "C0E44360A8236B72");
		
		
		
		
		
//		test("", 1, "");
//		test("", 2, "");
//		test("", 3, "");
//		test("", 4, "");
		
		
		if(skipAssertions)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		
	}

}
