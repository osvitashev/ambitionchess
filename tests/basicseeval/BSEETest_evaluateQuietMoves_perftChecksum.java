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
import util.HitCounter;

class BSEETest_evaluateQuietMoves_perftChecksum {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	ByteBuffer buffer1, buffer2;
	CRC32 crc1, crc2;
	
	String checkSum;
	
	public void testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		crc1 = new CRC32();
		crc2 = new CRC32();
		crc1.reset();
		crc2.reset();
		buffer1 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.
		buffer2 = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 3 output types * 8 bytes per long.
		perft(test_game, depth, 1);
		checkSum = getChecksum();
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

	private void perft(Gamestate board, int depth, int ply) {
		updateHashValue();
		if (depth == 1)
			return;
		int test_movelist_size_old = movepool.size();
		test_move_generator.generateLegalMoves(test_game, movepool);
		
		for (int i = test_movelist_size_old; i < movepool.size(); ++i) {
			int move = movepool.get(i);
			board.makeMove(move);
			perft(board, depth - 1, ply + 1);
			board.unmakeMove(move);
		}
		movepool.resize(test_movelist_size_old);
	}
	
	static boolean skipAssertions = false;
	
	void test(String fen, int depth, String expectedChecksum) {
		testPerft(fen, depth);
		System.out.println("test(\"" +fen +"\", "+ depth+", \""+ checkSum+ "\");");
		if(!skipAssertions)
			assertEquals(expectedChecksum, checkSum);
		//System.out.println("collected checkSum: " + checkSum);
		
	}
	
	@Test
	void testWithPerft_quiet() {
		test("3q4/3rr1k1/1nn2bbp/1pp5/4N1N1/1P1P3P/1PK2QP1/3R1R2 w - - 0 1", 4, "F52FD94B52891E6D");
		test("8/2kr1bb1/3r1q2/1n1p4/1n6/1NPQ4/1K1R1BB1/3R4 w - - 0 1", 4, "117A315E08BAD040");
		test("4r1k1/p4pb1/1p4p1/2p2q2/7p/P1P1B2P/1P1R1PP1/3Q2K1 w - - 6 35", 4, "8C8F78AD2AAD4A29");
		test("r1bq1r2/4n1k1/2pp2pp/p1p1p3/4Pp2/P1NP1N1P/1PPQ1PP1/1R3RK1 w - - 0 15", 4, "EE232E0827669882");
		test("8/ppn2p2/2p1bk1p/2P4P/PP1PBKP1/5P2/8/8 w - - 1 38", 4, "148C48EA9EE42C1A");
		test("r5k1/p4p1p/2nR2p1/2p1r1N1/2b5/P1P1B2P/2P2PP1/4K2R b K - 0 20", 4, "889E4BC162B2F484");
		test("r2q2k1/ppn2pbp/2p2ppB/5B2/3P3Q/7R/PPP2PPP/6K1 b - - 4 19", 4, "460D01B413B82C41");
		test("2b1r1k1/1p3pp1/2p1pb1p/q2P4/p1P5/3B1Q2/PP3PPP/R3R1K1 b - - 0 23", 4, "8B58C6BEE27E525A");
		test("r1b1Rnk1/ppp2p1p/6pQ/5p2/3q4/3B2N1/PPP2PPP/6K1 b - - 1 16", 4, "F0F7242B64749D7D");
		test("4r3/p1q3k1/5r1p/3Bb1p1/2P1pp2/1P5P/PBQ2PP1/1R1R2K1 w - - 0 26", 4, "E033D89FE4924759");
		test("r3r1k1/pp1q1pp1/2p2p2/5b1p/3P1P2/2PBR1Q1/PP3P1P/R5K1 b - - 1 19", 4, "8B4958D9E03D7B87");
		test("r2qr2k/p6p/3ppnR1/2p5/1bpnPP2/2N4Q/PPPB3P/2KR4 w - - 0 21", 4, "23879EFEBA2CE4AC");
		test("6rk/1pp4p/pb1pNpq1/3P4/8/P4Q2/1PP3PP/R6K w - - 5 23", 4, "05F547CF63A49F35");
		test("5rk1/1p2Rpbp/3p2p1/3P4/5P2/3QB1KP/5RP1/q6r b - - 7 23", 4, "46372962AC70AC8B");
		test("r2qr1k1/p2n1pp1/1p3n1p/3p4/3P3B/3QPN2/P4PPP/R4RK1 w - - 0 16", 4, "5B157DB70D441B52");
		test("r2qr1k1/pp3pb1/2p1Rpp1/8/2PP1BQ1/6P1/1PB2P2/5RK1 w - - 1 27", 4, "5C94285B176FA9E1");
		test("8/1p3r2/3pk3/p1q2p2/P2b4/3P3Q/1P3RPP/5RK1 b - - 2 26", 4, "517A2000F98F9FBD");
		test("r2q1rk1/pp3pp1/2n2n1p/3p3b/7B/2PB1N1P/P1P2PP1/R2Q1RK1 w - - 1 14", 4, "3F22CD44B8A50491");
		test("3r1rk1/p4p2/2p3pp/2q5/4Q3/1PpN3P/P1P1R2K/R7 b - - 2 26", 4, "8FE05391612848A6");
		test("r1bk3r/1pqpb3/p3p2p/2p1NppQ/2P1N3/3P2PP/PP1B1P2/R4RK1 w - - 2 17", 4, "F2232B7C48BE7FF4");
		test("5rk1/pp1r1ppp/1qp1pn2/8/2PP4/1PQ2BP1/P4P1P/3RR1K1 w - - 5 19", 4, "9B28A15B697C4A06");
		test("b3r1k1/5p2/p3q2p/3p1Bp1/1P1Q2PP/P1R1PP2/6K1/8 b - - 1 35", 4, "47F5BD9A0503BA6E");
		test("2qr2k1/5rp1/1p1b1n1p/p2BB1p1/2P3P1/1P5P/P1Q2P2/3RR1K1 b - - 0 31", 4, "FF16252A0DEA6A71");
		test("2k4r/1p2b2p/1p4p1/1b2n3/3pN2N/8/PPP3PP/R3R1K1 w - - 2 24", 4, "9EB411C01D874EA3");
		test("4r1k1/p5pp/8/8/2qb1N2/4nQP1/P5BP/4R2K b - - 1 32", 4, "50FCDF4F8EE2D71D");
		test("3r2k1/p7/2p1p1Pp/2p5/2b3Pq/7P/P1P2RB1/4Q1K1 b - - 0 34", 4, "235F87A5FC6C45DA");
		test("6k1/1ppq1rp1/p1n4p/4pN1n/2b1P1P1/2P1Q2P/PP3RBK/8 w - - 0 26", 4, "DD17F4AE476929C6");
		test("r4qk1/pp1n2pp/2p5/3P4/3P1r2/3B3P/P2Q1PP1/R3R1K1 b - - 0 19", 4, "84005C73DC652EAA");
		test("2r2rk1/pp2bpp1/2n4p/3pPR1P/3P2P1/2B2N2/PP6/5RK1 b - - 2 24", 4, "75C6E7AC63331EAD");
		test("r2q2k1/pb2b1pp/1p6/2p1P3/5r2/1P2pP2/PB4BP/2RQ1RK1 w - - 0 19", 4, "BE9608B747D8F7B6");
		
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
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 1, "B61C086023E05D44");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 2, "4063C73D6F3A05D2");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 3, "6D9BB0B008947589");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 4, "D88CC70C75ACBA15");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 1, "F7E346C61F5F75DF");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 2, "82FF3412F036C828");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 3, "0B3CD33FF14AD5DD");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 4, "02DE44D76F874A6B");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 1, "854BDCDABC30C2FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 2, "8E932183DA48186E");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 3, "8C7571C7840AF7FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 4, "A8972D5CB066789E");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 1, "87DAC40295C5584A");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 2, "7819F67A2A913AB1");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 3, "860C2632004C0F2D");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 4, "4450A4DC5EAE07CB");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 1, "EDC8CAA75C6B7CE2");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 2, "E796CEC15C061F76");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 3, "7D7FF609F954CC0A");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 4, "BDEE3464469CF7C0");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 1, "E2D83420C072E82D");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 2, "A9ED540B0BE202AE");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 3, "6DA84022155F0125");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 4, "9F11134E8582C66C");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 1, "1AC1F93B4BD01D08");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 2, "C45D8C1812BFE4F9");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 3, "72C139AE116216AA");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 4, "04A8C89A7D798F10");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 1, "81A64F304BF1B43B");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 2, "3EF7B5487F0FBCC7");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 3, "979ADA35E95018FF");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 4, "3DC66E2705F236F6");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 1, "8CF49FB828285C4C");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 2, "DDF844135D44A464");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 3, "0E13190C72529D06");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 4, "6BA68C00775E0D53");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 1, "4064274600B0F09D");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 2, "9F77B613D9B7081F");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 3, "967E4FE3C314493B");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 4, "02E48A8EC258BE1F");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 1, "9D2E44F854D02CC6");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 2, "6D0FB74DF0EEB816");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 3, "27439F03E1BF0649");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 4, "4C900E88FECE855B");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 1, "B8D7852627FD64D2");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 2, "046511D445F46DAB");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 3, "91667916F04BCA93");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 4, "DF50E0D2FFFDDD80");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 1, "462792B29E44F5C0");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 2, "CB2E787B7B096BB6");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 3, "008E01070034B128");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 4, "726579365DBF6F41");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 1, "5A7113340871FBA4");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 2, "5C6F6A9A5029F472");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 3, "5374CBDC9BCF87A5");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 4, "71AE38D3B3BE8006");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 1, "5F1F2CFD480BAE12");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 2, "ABA6976A50DA8669");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 3, "D03438A60E33C862");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 4, "2D09BB3FEF42A26D");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 1, "191DB1B19FF9D874");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 2, "A9AFCB475E31C2B1");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 3, "6DFB2E66401A8A19");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 4, "90238DAD45C637B6");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 1, "AE3BAE3FD48A99C2");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 2, "4261CC243B66F73D");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 3, "6BC6C8C659DBBEA8");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 4, "DB8266A1D56ACEBA");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 1, "7949726B839C9E31");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 2, "BA0CCBD98C903EDC");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 3, "2016189378AEAA2F");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 4, "8FDA0F022B1F3FCD");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 1, "C0F612DB7F722F35");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 2, "ABE5FBB7B8898BAB");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 3, "04826DB14686DF7D");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 4, "8B43E19D9D5A99AE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 1, "D665AF42A9605905");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 2, "2E4388A214BFE1BF");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 3, "8723CCBB7F0FADCE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 4, "FB920040EB61C3E5");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 1, "A28CA685AE70835E");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 2, "4B2BD6615BB61918");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 3, "602E1866CCF074BD");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 4, "2B7BC69A45918AFD");
		
//		test("", 1, "");
//		test("", 2, "");
//		test("", 3, "");
//		test("", 4, "");
		System.out.println(HitCounter.dump());
		
		if(skipAssertions)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		
	}

}
