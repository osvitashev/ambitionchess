package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Test;

import analysis.Interaction;
import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;
import gamestate.GlobalConstants.MoveType;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import gamestate.Move;
import util.HitCounter;
import util.SimplePRNG;

class BSEETest_evaluatePerftChecksum {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, new TargetStaticExchangeEvaluator(test_game));
	
	private MovePool movepool = new MovePool();
	private MoveGen test_move_generator = new MoveGen();
	
	ByteBuffer buffer_capture_winning, buffer_capture_neutral, buffer_capture_losing, buffer_boundGuard_interactions, buffer_xray_interactions;
	CRC32 crc_capture_winning, crc_capture_neutral, crc_capture_losing, crc_boundGuard_interactions, crc_xray_interactions;
	ByteBuffer buffer_quiet_neutral, buffer_quiet_losing;
	CRC32 crc_quiet_neutral, crc_quiet_losing;
	
	private int[] boundGuards = new int[15];
	private int[] xrayInteractions = new int[15];
	
	String checkSum;
	static boolean ENABLE_LOGGING = false;
	
	public void testPerft(String fen, int depth) {
		test_game.loadFromFEN(fen);
		crc_capture_winning = new CRC32();
		crc_capture_neutral = new CRC32();
		crc_capture_losing = new CRC32();
		crc_boundGuard_interactions = new CRC32();
		crc_xray_interactions = new CRC32();
		crc_capture_winning.reset();
		crc_capture_neutral.reset();
		crc_capture_losing.reset();
		crc_boundGuard_interactions.reset();
		crc_xray_interactions.reset();
		buffer_capture_winning = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 8 bytes per long.
		buffer_capture_neutral = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 8 bytes per long.
		buffer_capture_losing = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 8 bytes per long.
		buffer_boundGuard_interactions = ByteBuffer.allocate(15 * 4);//an arbitrary number we do not expect to exceed.
		buffer_xray_interactions = ByteBuffer.allocate(15 * 4);//an arbitrary number we do not expect to exceed.
		crc_quiet_neutral = new CRC32();
		crc_quiet_losing = new CRC32();
		crc_quiet_neutral.reset();
		crc_quiet_losing.reset();
		buffer_quiet_neutral = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 8 bytes per long.
		buffer_quiet_losing = ByteBuffer.allocate(2 * 6 * 8);// 2 players * 6 piece types * 8 bytes per long.
		perft(test_game, depth, 1);
		checkSum = getChecksum();
	}
	
	
	private void updateHashValue() {
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
		test_eval.evaluateQuietMoves();
		buffer_capture_winning.clear();
		buffer_capture_neutral.clear();
		buffer_capture_losing.clear();
		buffer_capture_winning.mark();
		buffer_capture_neutral.mark();
		buffer_capture_losing.mark();
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				buffer_capture_winning.putLong(test_eval.getOutput_capture_winning(player, pieceType));
				buffer_capture_neutral.putLong(test_eval.getOutput_capture_neutral(player, pieceType));
				buffer_capture_losing.putLong(test_eval.getOutput_capture_losing(player, pieceType));
			}
		}
		buffer_capture_winning.reset();
		buffer_capture_neutral.reset();
		buffer_capture_losing.reset();
		crc_capture_winning.update(buffer_capture_winning);
		crc_capture_neutral.update(buffer_capture_neutral);
		crc_capture_losing.update(buffer_capture_losing);
		
		{
			buffer_boundGuard_interactions.clear();
			buffer_boundGuard_interactions.mark();
			int numBoundGuards = 0;
			for(int i =0; i<test_eval.get_output_defenderInteractions_size();++i) {
				boundGuards[numBoundGuards++]=test_eval.get_output_defenderInteractions(i);
			}
			Arrays.sort(boundGuards, 0, numBoundGuards);
			for(int i =0; i<test_eval.get_output_defenderInteractions_size();++i) {
				buffer_boundGuard_interactions.putInt(boundGuards[i]);
			}
			buffer_boundGuard_interactions.reset();
			crc_boundGuard_interactions.update(buffer_boundGuard_interactions);
		}
		//////////////
		{
			buffer_xray_interactions.clear();
			buffer_xray_interactions.mark();
			int numxrays = 0;
			for(int i =0; i<test_eval.get_output_xRayInteractions_size();++i) {
				xrayInteractions[numxrays++]=test_eval.get_output_xRayInteractions(i);
			}
			Arrays.sort(xrayInteractions, 0, numxrays);
			for(int i =0; i<test_eval.get_output_xRayInteractions_size();++i) {
				buffer_xray_interactions.putInt(xrayInteractions[i]);
			}
			buffer_xray_interactions.reset();
			crc_xray_interactions.update(buffer_xray_interactions);
		}
		///////////////
		buffer_quiet_neutral.clear();
		buffer_quiet_losing.clear();
		buffer_quiet_neutral.mark();
		buffer_quiet_losing.mark();
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				buffer_quiet_neutral.putLong(test_eval.getOutput_quiet_neutral(player, pieceType));
				buffer_quiet_losing.putLong(test_eval.getOutput_quiet_losing(player, pieceType));
			}
		}
		buffer_quiet_neutral.reset();
		buffer_quiet_losing.reset();
		crc_quiet_neutral.update(buffer_quiet_neutral);
		crc_quiet_losing.update(buffer_quiet_losing);
		
		if(ENABLE_LOGGING) {
			System.out.println(test_game.toFEN() + " | " + getDefenderInteractions());
		}
			
	}
	
	String getDefenderInteractions() {
		String ret = "";
		for(int i =0; i<test_eval.get_output_defenderInteractions_size();++i) {
			ret += Interaction.toString(test_eval.get_output_defenderInteractions(i));
		}
		return ret;
	}
	
	String getChecksum() {
		String ret = "";
		ret += "captures:" + String.format("%08X", crc_capture_winning.getValue()) + String.format("%08X", crc_capture_neutral.getValue()) + String.format("%08X", crc_capture_losing.getValue());
		ret += "|boundDefenders:" + String.format("%08X", crc_boundGuard_interactions.getValue());
		ret += "|xRays:" + String.format("%08X", crc_xray_interactions.getValue());
		ret +="|quiet:"+String.format("%08X", crc_quiet_neutral.getValue()) + String.format("%08X", crc_quiet_losing.getValue());
		return ret;
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
	
	static final boolean SKIP_ASSERTIONS = false;
	static final int DEPTH_LIMIT = -1;//max depth of cases to execute. intended to quick sanity tests. -1 is for unlimited.
	
	void test(String fen, int depth, String expectedChecksum) {
		if(DEPTH_LIMIT != -1 && depth > DEPTH_LIMIT)return;
		testPerft(fen, depth);
		System.out.println("test(\"" +fen +"\", "+ depth+", \""+ checkSum+ "\");");
		if(!SKIP_ASSERTIONS)
			assertEquals(expectedChecksum, checkSum);
	}
	
	/**
	 * intended for easier tracing of the other test cases...
	 * Migth be useful to limit the max depth as well...
	 */
	@Test
	void testWithTracing() {
		//ENABLE_LOGGING = true;
		//test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 2, "captures:CF1D35A8D55895A9E2C18565|boundDefenders:295C45C0|quiet:28A13E76BF0C9F6E");
	}
	
	@Test
	void testWithPerft() {
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 2, "captures:AC1E35056C855EFDAF206020|boundDefenders:098ED3A7|xRays:7E034814|quiet:97B887A9AA208DB5");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 3, "captures:F48FDFBA7F9C961E24C46F94|boundDefenders:327454D9|xRays:551CBDAE|quiet:FB3C333F59EAF807");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 4, "captures:8BEE283EB480154ABB2F1E60|boundDefenders:F675B760|xRays:3E81DC7A|quiet:E03DDDC70BB60386");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 1, "captures:BAF465AEC204B8EFB75B8D46|boundDefenders:01C3D83C|xRays:04128908|quiet:9D779E8953C9C682");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 2, "captures:2858192C092244F7D0CC5F51|boundDefenders:ED53C8A0|xRays:585684C3|quiet:7451C3E2DBEBFAD9");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 3, "captures:6412E246186CA082CA39F85C|boundDefenders:BDDC5C68|xRays:04C6A328|quiet:31AD36803D0C228C");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 4, "captures:7C2B144CCE15914E8C8DC8B7|boundDefenders:1E496192|xRays:FD0E945A|quiet:2B24DE53FBA721EE");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 1, "captures:6EBB389DAE0F930A5BB507F2|boundDefenders:04128908|xRays:04128908|quiet:5CCB9EEB4C0C59E9");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 2, "captures:1E06EC9CAC38044B2FEBD672|boundDefenders:D3A1E3FF|xRays:2AB7342B|quiet:BE97DCBCC052DF33");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 3, "captures:551C6EDCFBE04544956BFAB1|boundDefenders:FF5AAC8A|xRays:05BF7CD9|quiet:9838D6D71AFA8F1B");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 4, "captures:D22E135714D5F72928B6D4E6|boundDefenders:AF108B64|xRays:0225C9E0|quiet:151182243C1253C0");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 1, "captures:30CBBC2BBAF465AEF75594AC|boundDefenders:31E21129|xRays:04128908|quiet:18FA504A609AE621");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 2, "captures:CF1D35A8D55895A9E2C18565|boundDefenders:557B02D1|xRays:9A8017CC|quiet:28A13E76BF0C9F6E");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 3, "captures:FA975CE3F3172B738F194F71|boundDefenders:222E89D2|xRays:097A59A2|quiet:A0BD7E0E1F6B2B99");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 4, "captures:EDC68D3423C9B00B54E76F01|boundDefenders:510FADF7|xRays:D132BA7C|quiet:0657983CDC8CCB8A");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 1, "captures:3A257C26BAF465AEA217EBA0|boundDefenders:6AD2536C|xRays:04128908|quiet:2A857E55BAC34C9F");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 2, "captures:363B04ED5B3F99D4E33C18FC|boundDefenders:2358CA79|xRays:37628A9D|quiet:ACF4178BB77557E8");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 3, "captures:4BCFA218208328D6C020B258|boundDefenders:968C0AF3|xRays:4BB1EE09|quiet:76E5F1AE767E8555");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 4, "captures:7C43A824C08BB5AA844822CD|boundDefenders:5A128868|xRays:F0CC0164|quiet:7712F1508DD98405");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 1, "captures:DCE47EC27E2E028DEC15DE60|boundDefenders:5998BA6C|xRays:04128908|quiet:AF657F41D551B4FA");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 2, "captures:C2F463E3E88215538D8CD8E2|boundDefenders:FD40A63F|xRays:53810A0A|quiet:A63827200713A5D6");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 3, "captures:3BD27A60C9CAA1B5C75D7120|boundDefenders:4949C250|xRays:17F98310|quiet:5A98D1C588B1F34E");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 4, "captures:A2AD3530E049CA37982D6564|boundDefenders:9F7006C6|xRays:719AE7B0|quiet:55A0E36BF99335D4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 1, "captures:D7E88ADA27421BF7322D112E|boundDefenders:77666532|xRays:04128908|quiet:AC744126681A5CF4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 2, "captures:00B891C7A2D0132F2889C0BE|boundDefenders:C34D4306|xRays:976306B1|quiet:FF02460B6DE9EDAF");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 3, "captures:4DE0FE86584E46C8D5E9918D|boundDefenders:2BCF53B7|xRays:FE156354|quiet:417EB52A93C3613D");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 4, "captures:51D7C9B35E5326352411CABC|boundDefenders:D2CAC1D7|xRays:A252B92A|quiet:06427B7340FDBE43");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 1, "captures:BAF465AEBAF465AED279B078|boundDefenders:84C9D423|xRays:04128908|quiet:AD9F1F66F1C181FA");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 2, "captures:5545B450FB74E40022814D04|boundDefenders:6136F775|xRays:BBB77BB5|quiet:B53EB265F51C67E7");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 3, "captures:0301BAA73F73DF68F4109787|boundDefenders:365530F4|xRays:8E96B403|quiet:2B13DBE256E6CEAC");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 4, "captures:0ECD9057446FEAFFBDD4C9FE|boundDefenders:1538EC97|xRays:41A5AB96|quiet:61CEE2250E3A370C");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:210C5EEF90918B36");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 2, "captures:26AEFE1F6D16C9485908A0DD|boundDefenders:4AB78C56|xRays:67BBAF86|quiet:F285B6DD9C0D6183");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 3, "captures:5F7701325ABA525998C51300|boundDefenders:CC8FB378|xRays:39DBF118|quiet:430559CBACB00D25");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 4, "captures:5314D13A0DE56DCE7AD694F3|boundDefenders:8B51DFC3|xRays:964F580E|quiet:4F819292ECBF7FBB");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 1, "captures:BAF465AE272FB31C86B4E7E4|boundDefenders:3116393A|xRays:04128908|quiet:7A4259182C21EA0F");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 2, "captures:BE3C48B58AFB4D6F92CEB65C|boundDefenders:FE5DBA94|xRays:BBB77BB5|quiet:EB2F021866716E41");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 3, "captures:0B4AA0E808913ADD68D04C90|boundDefenders:A0E0AAAC|xRays:8338C829|quiet:6ED425D3B49BFC37");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 4, "captures:3BFABA230EEFCFE528762833|boundDefenders:0EFB91BE|xRays:EA2B1406|quiet:856AC6B5E26C1A7F");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 1, "captures:19AF0A0EBAF465AE2CA302C4|boundDefenders:68454A1A|xRays:04128908|quiet:CF603DC02C722788");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 2, "captures:60E36FAB36D109EACBDB9D63|boundDefenders:125D201C|xRays:A3960553|quiet:D25F648A6F8BAC9C");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 3, "captures:71A37F69A95F748766F4AD72|boundDefenders:36BDEDD1|xRays:1177C109|quiet:B311D4355AD69EC5");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 4, "captures:2B24CF1DE721CA7C8691151C|boundDefenders:CD700912|xRays:167185E3|quiet:5BA36B5044B2DEFB");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 1, "captures:55A6D34FBAF465AEE27C00D4|boundDefenders:001CAD47|xRays:04128908|quiet:3D6A6AF9BAA3EF00");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 2, "captures:251975DBE9719EA062640E53|boundDefenders:7787BEFB|xRays:A3960553|quiet:C699F8BE036E53D5");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 3, "captures:E346C3084916165DDF97A3F0|boundDefenders:00E97743|xRays:2A54474B|quiet:7B84305574A47DE9");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 4, "captures:8E1B29720A2983BADC0F6ED6|boundDefenders:1287D4D1|xRays:1508F267|quiet:F69F58154C1763B5");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 1, "captures:B6E2968FBAF465AE0600B951|boundDefenders:49080F24|xRays:04128908|quiet:9E5531CCB56EB8F6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 2, "captures:44CB4D893A7A8EE2FFEDFDE9|boundDefenders:3B3BEF23|xRays:BBB77BB5|quiet:4F4CF55655FB63C6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 3, "captures:0689B5F746D9EC52077C0259|boundDefenders:DB557355|xRays:6DCF47DF|quiet:5704152DF8D9EE63");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 4, "captures:2B92EA2AB0063FD69D17411E|boundDefenders:AE308430|xRays:7AE94530|quiet:43A42F5C68EDD29F");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:8AAFFF859A63CB27");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 2, "captures:8AF9B677D0DDABC04A34291C|boundDefenders:9A8EBA81|xRays:BEF15E93|quiet:68E85107FD81510D");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 3, "captures:BCCF153872B87ED024827959|boundDefenders:F5E2627D|xRays:671027AD|quiet:7BA52C593CE775D4");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 4, "captures:EBA9C4CB1ACC3EE63EEF7355|boundDefenders:F2341280|xRays:CB8D7845|quiet:EB18B86F1921306F");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 1, "captures:00CCB226BAF465AE1CD5C3BF|boundDefenders:0B875365|xRays:04128908|quiet:F5DDAE138516D6F9");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 2, "captures:31F049A3C9B0BA713B35BD95|boundDefenders:FA9FF6A2|xRays:49283716|quiet:EB5749ABC102A5E1");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 3, "captures:741950D0300924B4E683EEBB|boundDefenders:67553C3E|xRays:E94725E4|quiet:A07357E358521788");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 4, "captures:9C96800ABDF720E9B9BEC91B|boundDefenders:A4707FF5|xRays:D1333447|quiet:F98984C81A984D4D");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 1, "captures:6F4A213DBAF465AE326E360C|boundDefenders:14473372|xRays:04128908|quiet:1F2A5D2B79AD9A0B");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 2, "captures:B4BADF1F4718593DF15FAC7D|boundDefenders:C03CBFFE|xRays:AA11169A|quiet:7AF234BB4DD388BD");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 3, "captures:A6E61DCACF8C3A18C8CF62A3|boundDefenders:91393F5D|xRays:B1442EB7|quiet:031087FAABCBCF78");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 4, "captures:F55C8079C6F894CB2073CBF9|boundDefenders:32E7D437|xRays:A1240666|quiet:282AEF433246E280");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:38DFE6E0A378FC55");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 2, "captures:F5A0F415F5A0F415F5A0F415|boundDefenders:FBBAC38D|xRays:FBBAC38D|quiet:46674793A842756A");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 3, "captures:10B8E4F410B8E4F410B8E4F4|boundDefenders:975C6E7B|xRays:975C6E7B|quiet:478891DB6EDF9284");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 4, "captures:5DD3E2B45DD3E2B45DD3E2B4|boundDefenders:9FA53724|xRays:9FA53724|quiet:7C1E63CB998448ED");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 1, "captures:BAF465AEF1B75E2D6C5BFBAF|boundDefenders:14B0A656|xRays:04128908|quiet:676C5D51FCF24A9E");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 2, "captures:F5293CEDD0C3B6DAF65BED14|boundDefenders:309716DA|xRays:BBB77BB5|quiet:4D7EBC2E9A480E3B");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 3, "captures:CA43A64B5BCDE9A63CEF16C5|boundDefenders:248BB1DF|xRays:54ECD1AD|quiet:47F23B089849F093");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 4, "captures:FC360F6DDCC014953BFB5AD2|boundDefenders:707ACF52|xRays:57DB1B92|quiet:2A62B51B439DCF92");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 1, "captures:6B2D1C3A7F2BA012E64D9D00|boundDefenders:F06F06FE|xRays:04128908|quiet:46584BB7EA071758");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 2, "captures:EC4F13C9EF632D084909B2B1|boundDefenders:D6C06A4F|xRays:585684C3|quiet:86101FE4E638FCBA");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 3, "captures:9397D723C533833FE6212A12|boundDefenders:CE011C7B|xRays:9A664BCB|quiet:B2C221228ADC244B");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 4, "captures:806C9A8EA913FBA3A3D83768|boundDefenders:96D145FC|xRays:290DCE2F|quiet:83D61973904C96A4");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 1, "captures:659DB3B3272FB31C0CEF85DE|boundDefenders:5E11123E|xRays:04128908|quiet:CFB6722BDC7FFAA8");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 2, "captures:7764FC5DC8A9DB255BCE686A|boundDefenders:0103C1F6|xRays:1E224F84|quiet:4A058343FBC83C36");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 3, "captures:E925858F5A32C5B7690898FA|boundDefenders:D8D5B2AD|xRays:8B4743BE|quiet:A19789F645B4AED5");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 4, "captures:79775ED2F00CCDD73979DCA0|boundDefenders:A283D29F|xRays:AF9FBDC7|quiet:271D2F5DBEC65E0A");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 1, "captures:BAF465AEBAF465AE9E58F779|boundDefenders:AF45E1AA|xRays:04128908|quiet:208C902E09C2F3C8");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 2, "captures:3384290D2436DCC03654AF9B|boundDefenders:1FE8FACC|xRays:044D19C2|quiet:97A9442F52391A2E");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 3, "captures:1CD0A3DD83848D42008A67D1|boundDefenders:6D555735|xRays:C4A4A5A6|quiet:BB3B23C7BDDFDE24");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 4, "captures:2E10C1CBEE153BA0A11756CA|boundDefenders:3DB0C62E|xRays:0E348D1B|quiet:BC1563AB3C88D66A");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 1, "captures:326E360CBAF465AE280CB279|boundDefenders:353C5033|xRays:04128908|quiet:F8F4031D8A6B35CF");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 2, "captures:4BDAF1796D368DAB11ED96BA|boundDefenders:C37E5C34|xRays:611E3E6C|quiet:72A6A25D6CE92D26");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 3, "captures:2CBD461558A522D3C2CA7AC8|boundDefenders:21043EF5|xRays:5D7A337D|quiet:20A72A016A0AA344");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 4, "captures:FC35F52E4EDB46D9A404524B|boundDefenders:8CFCE2BD|xRays:A467B25D|quiet:16F3AF81DAEC77B7");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 1, "captures:F8647D03BAF465AE5A866710|boundDefenders:13AFD3D4|xRays:04128908|quiet:D7B6EE1284B9C4DE");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 2, "captures:07E59A3818FFCAFC52C2F72B|boundDefenders:63C5CA85|xRays:9A8017CC|quiet:CFEA719BF5738D4F");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 3, "captures:04F0C87EFAE355BF707DAA33|boundDefenders:3AD8F5E2|xRays:37DDE841|quiet:68202DE75875333B");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 4, "captures:52E9A29AD5CF672791B1DEC3|boundDefenders:880D8084|xRays:4DE93A43|quiet:7259535E334D6F88");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 1, "captures:BAF465AEAEF3FA6FEE432D04|boundDefenders:D61EC5A0|xRays:04128908|quiet:D2733B30B9D9F621");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 2, "captures:AC76D8B368DE7EFF36CC8AC0|boundDefenders:0C77F43D|xRays:9D4BB32A|quiet:D544A6B12CAC87B1");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 3, "captures:BAA6AF7022D9D2F3D3A22023|boundDefenders:1C1FAACB|xRays:F00440E0|quiet:2EAE5D5D012558E2");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 4, "captures:30EBAC8D4A8F8223FF6FF6C1|boundDefenders:4461F255|xRays:A8ED0E2D|quiet:D36DA20139AAC24F");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 1, "captures:5C76262A34D38858CEC8531E|boundDefenders:D4809902|xRays:04128908|quiet:0F7BF457E67595EB");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 2, "captures:2F2311172CC643EA12D6CEAC|boundDefenders:E8D2B43C|xRays:B9DC87A8|quiet:DB7C722801C2B037");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 3, "captures:EA6C8B51426280BD7A342621|boundDefenders:6C25ACC8|xRays:C74B0EAC|quiet:75DAB6CD086AD467");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 4, "captures:84B675EB27FE5CEEDAD07859|boundDefenders:D42F3BA2|xRays:6DE48B03|quiet:530CC09CD84952D8");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 1, "captures:7809854ADAD8D0633EA1D491|boundDefenders:92D61E92|xRays:04128908|quiet:E4FB9AC8DC29672C");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 2, "captures:6CE63F323F6285B1AFAEB3F3|boundDefenders:ED8A7B81|xRays:431529DC|quiet:D5D95E027156B2C3");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 3, "captures:FE4EF17734282877210203A7|boundDefenders:18277BDA|xRays:ADD6BE80|quiet:93BC03F44DB12D23");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 4, "captures:B8A6A811EB58C26AD832D514|boundDefenders:F296EEF2|xRays:34100837|quiet:FA8CA27B8000BDC0");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 1, "captures:614DC4E9BAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:3D3B4272E8F51237");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 2, "captures:AA34F60C8D84135D0444E8E3|boundDefenders:9A960B45|xRays:8DC28F29|quiet:D4A01A1736284215");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 3, "captures:32D3352484F72F1D22C9F024|boundDefenders:CEC2CE02|xRays:FC36F61D|quiet:8DF2A83BF5227811");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 4, "captures:B7E7881113EA6B45B4DA3802|boundDefenders:3CEFAFEC|xRays:C67685BC|quiet:8B6A77F57212A86B");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 1, "captures:E1F99596BAF465AE89704488|boundDefenders:B8166CA2|xRays:04128908|quiet:2ABC4C7D289D0F77");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 2, "captures:E8AC95205F7CC5781DEC54CD|boundDefenders:512FE5F8|xRays:1B966466|quiet:1B947447499FFB2D");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 3, "captures:ACC940A6B8521EA12D0ED2F4|boundDefenders:3EAD8420|xRays:B537F79C|quiet:D17D3AC0D41D9441");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 4, "captures:69273A9387EB0745AD751C88|boundDefenders:6FBC84B9|xRays:BAAA4A57|quiet:B9302AA6EEA6654E");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 1, "captures:19516AC19C98B7721B8C830E|boundDefenders:40A3FF2B|xRays:04128908|quiet:33631577E08771D8");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 2, "captures:96F057C5589F7BD63D6D2968|boundDefenders:64EECC24|xRays:D04EC558|quiet:C3126456A54C6351");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 3, "captures:08EE665F0A84A9DBC0EC0D37|boundDefenders:C735C2AC|xRays:26DD1EC3|quiet:F86D66768A49EAA6");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 4, "captures:36BCE16340F5B9CE9545F1F5|boundDefenders:725130AD|xRays:B04CFD44|quiet:34E6062C6B74D053");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 1, "captures:2110577767EC532CE1DB1EE9|boundDefenders:DB47E532|xRays:04128908|quiet:9CB5D1449D05CEFF");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 2, "captures:36A851FFA22851F19BB811CC|boundDefenders:115C05CC|xRays:7E034814|quiet:EC9B84E31121582F");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 3, "captures:8ECEE7F64F4382F03075A33C|boundDefenders:FCDA19B4|xRays:812D3DF3|quiet:107BB36D35CC44D2");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 4, "captures:1B4B94B50E14FD03C49F5091|boundDefenders:BED6D1E3|xRays:99C799CC|quiet:43D087DDE28C8119");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 1, "captures:BAF465AE27B695A55D5A8BD5|boundDefenders:13DE8150|xRays:04128908|quiet:CE441F06B0D492AC");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 2, "captures:DB2A52507A2939FBAB112FC7|boundDefenders:19A9F8BF|xRays:DA865B0D|quiet:3101A9B9BBD27CE0");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 3, "captures:D707357B7333D601C26E4B9F|boundDefenders:6CDF17F8|xRays:BF3FBDF7|quiet:EAB14C1170553AFB");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 4, "captures:BEAB781488AC2C9E49303D93|boundDefenders:1FE676EE|xRays:2366874F|quiet:C0E44360A8236B72");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 1, "captures:8A97D25E0F1E64DF51D58AE6|boundDefenders:DD94E6F9|xRays:04128908|quiet:B61C086023E05D44");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 2, "captures:8926656F78CD8F2DBDB3308F|boundDefenders:B7214688|xRays:044D19C2|quiet:4063C73D6F3A05D2");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 3, "captures:CF5D5F4CF1EDC61E112121C2|boundDefenders:8CB80FA8|xRays:5EC8B5A9|quiet:6D9BB0B008947589");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 4, "captures:17379A41E264F066234FDDCC|boundDefenders:053D2973|xRays:8CA9956D|quiet:D88CC70C75ACBA15");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 1, "captures:ABA1AA0F4DEC4901A86AFBA4|boundDefenders:4DA102DC|xRays:04128908|quiet:F7E346C61F5F75DF");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 2, "captures:F57711A1055E36D66524B74D|boundDefenders:0F3F272F|xRays:B8D77D2A|quiet:82FF3412F036C828");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 3, "captures:DF99C82162CAA25C8FC62D12|boundDefenders:82AFAFB6|xRays:87B7BF96|quiet:0B3CD33FF14AD5DD");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 4, "captures:A4C2D47F91DF14CDBD0BA29D|boundDefenders:F62E0299|xRays:5EE02C98|quiet:02DE44D76F874A6B");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 1, "captures:9D5AE446BAF465AE4DB454A0|boundDefenders:E7D4174C|xRays:04128908|quiet:854BDCDABC30C2FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 2, "captures:02D216AA2370A84661BDDA16|boundDefenders:C9C0F5AD|xRays:92E7D2DB|quiet:8E932183DA48186E");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 3, "captures:5D2CFD27F4DDEC43DB9142DA|boundDefenders:69477D37|xRays:3C0B9D8B|quiet:8C7571C7840AF7FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 4, "captures:873718F2BDA06152164A0CD8|boundDefenders:2B086878|xRays:347FB8DC|quiet:A8972D5CB066789E");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 1, "captures:26BD0B8B7E306BB01090BBF9|boundDefenders:B308EE50|xRays:04128908|quiet:87DAC40295C5584A");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 2, "captures:AA67D058BDF88448BDF94E60|boundDefenders:C26A416B|xRays:483ED70A|quiet:7819F67A2A913AB1");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 3, "captures:425E9AC1B5DF96EC27AACCA9|boundDefenders:A6DA5731|xRays:649CD007|quiet:860C2632004C0F2D");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 4, "captures:E5D43E4D9D187281CC4EE876|boundDefenders:8BF51FC4|xRays:A2EE625F|quiet:4450A4DC5EAE07CB");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 1, "captures:BAF465AE4DEC49014B385DF9|boundDefenders:36051C48|xRays:04128908|quiet:EDC8CAA75C6B7CE2");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 2, "captures:1FCA380508562D73EF0D99C7|boundDefenders:F0FC9A19|xRays:37628A9D|quiet:E796CEC15C061F76");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 3, "captures:809CBD83AA274E5F7BE163F2|boundDefenders:6707C096|xRays:49E2B4A1|quiet:7D7FF609F954CC0A");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 4, "captures:4AB41F96628DFAB9B74CE3A9|boundDefenders:9765C3F0|xRays:C6BB362A|quiet:BDEE3464469CF7C0");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 1, "captures:2F44804FBAF465AE35AB3ADD|boundDefenders:0BE9CA79|xRays:04128908|quiet:E2D83420C072E82D");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 2, "captures:D9044B8436A6579AFF55387A|boundDefenders:65F62826|xRays:611E3E6C|quiet:A9ED540B0BE202AE");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 3, "captures:1565AA7C88F16B6919C4FD7D|boundDefenders:CDC6662B|xRays:1012EEB2|quiet:6DA84022155F0125");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 4, "captures:860A51B15B574F1DF222E6DC|boundDefenders:1BCA12DE|xRays:280A9320|quiet:9F11134E8582C66C");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 1, "captures:BAF465AE5F98F8CB02DEB84C|boundDefenders:9E15B817|xRays:04128908|quiet:1AC1F93B4BD01D08");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 2, "captures:68721D2DC4049335E9A23D37|boundDefenders:EB8ACA4A|xRays:37628A9D|quiet:C45D8C1812BFE4F9");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 3, "captures:E87CAAEFE1D8B701FB8FA375|boundDefenders:27918DD7|xRays:73618B79|quiet:72C139AE116216AA");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 4, "captures:03259707B5E007AEA01804CF|boundDefenders:B8631CC7|xRays:15575447|quiet:04A8C89A7D798F10");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 1, "captures:0F08EEADBAF465AE25DC18F1|boundDefenders:40EF4BC4|xRays:04128908|quiet:81A64F304BF1B43B");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 2, "captures:AE3D0F30391BD073F8EA2AA2|boundDefenders:9CC684C3|xRays:1B966466|quiet:3EF7B5487F0FBCC7");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 3, "captures:01F1455BA8BBDD55307A896E|boundDefenders:BB403C68|xRays:B1805255|quiet:979ADA35E95018FF");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 4, "captures:C39E41EEB9A5BCC313114C36|boundDefenders:53034AA6|xRays:06A364D6|quiet:3DC66E2705F236F6");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 1, "captures:BAF465AEDBC3BD44010054D0|boundDefenders:F00405A0|xRays:04128908|quiet:8CF49FB828285C4C");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 2, "captures:2D8517759A8CDBB0FA6D8FF3|boundDefenders:53906A83|xRays:37628A9D|quiet:DDF844135D44A464");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 3, "captures:5AFC2A0254F5D971A99E0FE8|boundDefenders:A1F919D8|xRays:F7C7F997|quiet:0E13190C72529D06");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 4, "captures:11010AA4F0C229E6E3488B73|boundDefenders:EDBB56D4|xRays:403FFE2C|quiet:6BA68C00775E0D53");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 1, "captures:E6FA81A6BAF465AEC5B62789|boundDefenders:9C9232DB|xRays:04128908|quiet:4064274600B0F09D");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 2, "captures:312346FACA60E58E53FC7CE1|boundDefenders:2E6A0BEA|xRays:585684C3|quiet:9F77B613D9B7081F");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 3, "captures:D0648F5D1139550DF7F69A98|boundDefenders:38F085F8|xRays:D2BD26A2|quiet:967E4FE3C314493B");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 4, "captures:99CCB165CA05B9FBF628EAFF|boundDefenders:A24B8F53|xRays:490ECD44|quiet:02E48A8EC258BE1F");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 1, "captures:BAF465AEBAF465AEC382B402|boundDefenders:81B3FB8F|xRays:04128908|quiet:9D2E44F854D02CC6");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 2, "captures:AC6A3C35F75B98D81B4B885F|boundDefenders:B67597AA|xRays:37628A9D|quiet:6D0FB74DF0EEB816");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 3, "captures:72605A7435CCA9BD97B1C424|boundDefenders:6AD637A3|xRays:CD73C6D5|quiet:27439F03E1BF0649");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 4, "captures:9E16FE631ADCC632D0F21F76|boundDefenders:FCF295CB|xRays:5EA2C44A|quiet:4C900E88FECE855B");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 1, "captures:BAF465AE4215481002E9ADBE|boundDefenders:7C248ACE|xRays:04128908|quiet:B8D7852627FD64D2");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 2, "captures:C125718CC784389E2A431AA3|boundDefenders:4CDDA21D|xRays:AA11169A|quiet:046511D445F46DAB");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 3, "captures:55F9EDBE7F07FC42186E661F|boundDefenders:2E86A359|xRays:107AAD8D|quiet:91667916F04BCA93");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 4, "captures:D8E4C36CF54C5B76CF8A98F8|boundDefenders:9559EECC|xRays:E752DBA6|quiet:DF50E0D2FFFDDD80");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 1, "captures:7F404D41F1107742DC67BF7A|boundDefenders:6994A430|xRays:04128908|quiet:462792B29E44F5C0");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 2, "captures:F043193AF12D6CA0F774D2FA|boundDefenders:4BDD4407|xRays:7C298C23|quiet:CB2E787B7B096BB6");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 3, "captures:EE7995AF7402812B960AAF52|boundDefenders:7CC292D9|xRays:65B4911F|quiet:9AE8B2339A52021C");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 4, "captures:7BD7E6EEB964D9BAA6A85961|boundDefenders:7C125E8D|xRays:4E21CCFA|quiet:4F76370360AC2174");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 1, "captures:CE8A475EBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:5A7113340871FBA4");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 2, "captures:0A51E0384217AE37B1C106C2|boundDefenders:2E0B53CD|xRays:8DC28F29|quiet:5C6F6A9A5029F472");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 3, "captures:12DA9FAFC9C23E0995491926|boundDefenders:CC82B734|xRays:B98E90D3|quiet:5374CBDC9BCF87A5");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 4, "captures:69C234C470A3ECD7F76E8CB8|boundDefenders:F822C87E|xRays:4F5CCFE8|quiet:71AE38D3B3BE8006");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 1, "captures:D2205151BAF465AEEBFD415B|boundDefenders:272521FD|xRays:04128908|quiet:5F1F2CFD480BAE12");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 2, "captures:0DB300AACAD93467DFF3E775|boundDefenders:4815D627|xRays:9D4BB32A|quiet:ABA6976A50DA8669");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 3, "captures:CCC2B05817B1C18AB8D46EBB|boundDefenders:0AB04D7C|xRays:908D7DD9|quiet:D03438A60E33C862");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 4, "captures:921F62D2707FFF236950E00A|boundDefenders:3F29BBE0|xRays:7F2FCB8B|quiet:2D09BB3FEF42A26D");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 1, "captures:F3146E83BAF465AEC4ECC380|boundDefenders:01FC95AB|xRays:04128908|quiet:191DB1B19FF9D874");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 2, "captures:A45EDAB6980377F8B3F57258|boundDefenders:59D11839|xRays:9F0882E0|quiet:A9AFCB475E31C2B1");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 3, "captures:FC2A29D68CF6F4E9036FA789|boundDefenders:CEDE142D|xRays:12F0328E|quiet:6DFB2E66401A8A19");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 4, "captures:EA7DCE093D3BA7ACCE4BBE4B|boundDefenders:D9B86703|xRays:04151220|quiet:90238DAD45C637B6");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 1, "captures:5ADD0E93BAF465AEE8AE22C7|boundDefenders:8C09F4C6|xRays:04128908|quiet:AE3BAE3FD48A99C2");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 2, "captures:AEAB961CC37C147A26E1FC35|boundDefenders:63064C42|xRays:976306B1|quiet:4261CC243B66F73D");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 3, "captures:F7E0072879CE6C3025F1258F|boundDefenders:6A9DB271|xRays:D4070808|quiet:6BC6C8C659DBBEA8");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 4, "captures:80D9D8BCFC5EB2F4134FEA08|boundDefenders:51D330E0|xRays:6DBE210B|quiet:DB8266A1D56ACEBA");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 1, "captures:BAF465AEBAF465AED076DB90|boundDefenders:04128908|xRays:04128908|quiet:7949726B839C9E31");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 2, "captures:73E915756C34E326CA4B0BF9|boundDefenders:4F830163|xRays:A3960553|quiet:BA0CCBD98C903EDC");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 3, "captures:B77B2DFF89D8A36F1E800DD4|boundDefenders:D509CFCE|xRays:90AE01E7|quiet:2016189378AEAA2F");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 4, "captures:D4DB519B8D00BD41E226E763|boundDefenders:B26FA1A8|xRays:504E6A52|quiet:8FDA0F022B1F3FCD");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 1, "captures:BAF465AEBAF465AE7057DFB7|boundDefenders:95B2F1AF|xRays:04128908|quiet:C0F612DB7F722F35");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 2, "captures:68783DE117E346F5F7C4036D|boundDefenders:0D18252F|xRays:BBB77BB5|quiet:ABE5FBB7B8898BAB");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 3, "captures:F01877F159E72717FC6D6F76|boundDefenders:23508DF4|xRays:08334A05|quiet:04826DB14686DF7D");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 4, "captures:265A0FD3B6A98B91081B15F3|boundDefenders:DE10063E|xRays:FD9505C8|quiet:8B43E19D9D5A99AE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 1, "captures:BAF465AE1F3CF44BAE0F7F01|boundDefenders:7C64D6BC|xRays:04128908|quiet:D665AF42A9605905");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 2, "captures:E3B0A3BF8B51DED60B485265|boundDefenders:B42CAB8A|xRays:0C5929F0|quiet:2E4388A214BFE1BF");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 3, "captures:FC47B489FDA5D1CF3935BB8F|boundDefenders:98D88104|xRays:E1BFB99D|quiet:8723CCBB7F0FADCE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 4, "captures:B725AC69845D835463DA6DFE|boundDefenders:61ABEFC8|xRays:012C0A35|quiet:FB920040EB61C3E5");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 1, "captures:BAF465AE4DEC4901AA33CBE6|boundDefenders:04128908|xRays:04128908|quiet:A28CA685AE70835E");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 2, "captures:8A24B9CBCDF897F651006AFA|boundDefenders:5FB05F5E|xRays:044D19C2|quiet:4B2BD6615BB61918");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 3, "captures:D88628375A020F869F87F8D1|boundDefenders:57E32FC9|xRays:69084716|quiet:602E1866CCF074BD");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 4, "captures:0F96CD712B26C4F45EA72A2E|boundDefenders:3C5B0066|xRays:A58806E0|quiet:2B7BC69A45918AFD");
		test("1r6/3q4/6r1/1p3b2/k7/1r1Q3P/4P2K/6R1 w - - 0 1", 1, "captures:2DFED68B83323BC72AA6679B|boundDefenders:DA09AF74|xRays:04128908|quiet:9972F5353005D6FA");
		test("1r6/3q4/6r1/1p3b2/k7/1r1Q3P/4P2K/6R1 w - - 0 1", 2, "captures:E00BD598B84AEDC5CF46DBC8|boundDefenders:2CB7BF4C|xRays:976306B1|quiet:D06D36F6E2F8B185");
		test("1r6/3q4/6r1/1p3b2/k7/1r1Q3P/4P2K/6R1 w - - 0 1", 3, "captures:5DFFA8A140D082C9DBF55E10|boundDefenders:A3CB7302|xRays:DFD727FD|quiet:0F06298420B52E51");
		test("1r6/3q4/6r1/1p3b2/k7/1r1Q3P/4P2K/6R1 w - - 0 1", 4, "captures:C16ECDA533F7DAE84FC7DDBB|boundDefenders:BC060EDF|xRays:498514C5|quiet:B1A6886E9746E684");
		test("3r2k1/5rpp/2pq4/2p2bNn/p1n1n3/P1P3P1/1P3PBP/R2QKR2 b - - 0 1", 1, "captures:3E15B81AB1A4D7F1DD6E1FD3|boundDefenders:B39CD210|xRays:04128908|quiet:D80D1307E4A01878");
		test("3r2k1/5rpp/2pq4/2p2bNn/p1n1n3/P1P3P1/1P3PBP/R2QKR2 b - - 0 1", 2, "captures:5CC625155C14693B72B15A92|boundDefenders:0E97285D|xRays:1E224F84|quiet:447411C6395DB6E2");
		test("3r2k1/5rpp/2pq4/2p2bNn/p1n1n3/P1P3P1/1P3PBP/R2QKR2 b - - 0 1", 3, "captures:452C8F8F0F9067124B1C485A|boundDefenders:CC2C0E90|xRays:F052EB85|quiet:657D023783A64B12");
		test("3r2k1/5rpp/2pq4/2p2bNn/p1n1n3/P1P3P1/1P3PBP/R2QKR2 b - - 0 1", 4, "captures:44D953C3AF6C8AEDD6EE6147|boundDefenders:40EFCE7D|xRays:0A582D02|quiet:146D9DE0369A50B9");
		test("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", 1, "captures:CA1859ACBAF465AE9F1765B5|boundDefenders:DA2F50E6|xRays:04128908|quiet:0C6902CB86C6D757");
		test("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", 2, "captures:D0509A8EB1EFBE028B1EE535|boundDefenders:FE58B955|xRays:044D19C2|quiet:BEC30179A3D7A482");
		test("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", 3, "captures:77C967313F8839807DD97A60|boundDefenders:E7353EA1|xRays:75727CFE|quiet:6410221364BE8868");
		test("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", 4, "captures:B7C240BB6D80BA74D314531F|boundDefenders:F8E0BE99|xRays:F33059BC|quiet:C36C391C8D107783");
		test("r1bq1rk1/p3ppbp/1p3np1/2p1n3/3p1P2/N1PP2P1/PPQ1P1BP/R1B2RK1 w - - 0 11", 1, "captures:0849846DFD1BA18B7876B324|boundDefenders:04128908|xRays:04128908|quiet:B5239966807EC72E");
		test("r1bq1rk1/p3ppbp/1p3np1/2p1n3/3p1P2/N1PP2P1/PPQ1P1BP/R1B2RK1 w - - 0 11", 2, "captures:793C1F642B3A27E304B5FBC4|boundDefenders:C0E6891D|xRays:9A8017CC|quiet:6BBB008BC2069021");
		test("r1bq1rk1/p3ppbp/1p3np1/2p1n3/3p1P2/N1PP2P1/PPQ1P1BP/R1B2RK1 w - - 0 11", 3, "captures:A012575F4D63874FA3DDC930|boundDefenders:8D75FF78|xRays:2C43D535|quiet:A002D2E57D323670");
		test("r1bq1rk1/p3ppbp/1p3np1/2p1n3/3p1P2/N1PP2P1/PPQ1P1BP/R1B2RK1 w - - 0 11", 4, "captures:F0250703B3351FD18BD2F0DF|boundDefenders:5A18FA53|xRays:B10E9828|quiet:64FE50C6024FBE21");
		test("5rk1/p3ppbp/6p1/2pB4/3p1Pn1/2Pb2P1/PP5P/R1B2RK1 w - - 0 19", 1, "captures:53698AF93A257C26AEAF0BFC|boundDefenders:2689B645|xRays:04128908|quiet:5545CCB51F260F74");
		test("5rk1/p3ppbp/6p1/2pB4/3p1Pn1/2Pb2P1/PP5P/R1B2RK1 w - - 0 19", 2, "captures:9E7306DC0E00F225721D3D14|boundDefenders:68693A90|xRays:B3592A4D|quiet:F665F59921A7EC63");
		test("5rk1/p3ppbp/6p1/2pB4/3p1Pn1/2Pb2P1/PP5P/R1B2RK1 w - - 0 19", 3, "captures:225C55EDBE90CCE0126CC927|boundDefenders:0AF720D1|xRays:AB2F4E5A|quiet:5BBBEBA232099BA8");
		test("5rk1/p3ppbp/6p1/2pB4/3p1Pn1/2Pb2P1/PP5P/R1B2RK1 w - - 0 19", 4, "captures:BA01EE2978F1AF49DDD3517A|boundDefenders:2852E472|xRays:D086D2EA|quiet:A332D142B56607E8");
		test("2rnn1k1/p3b1pp/1p1pPr2/8/2P2PP1/BP5P/P4PB1/3RR1K1 w - - 0 26", 1, "captures:66596838BAF465AE2A817274|boundDefenders:FF74A5F5|xRays:04128908|quiet:E7FD0A359D668DCC");
		test("2rnn1k1/p3b1pp/1p1pPr2/8/2P2PP1/BP5P/P4PB1/3RR1K1 w - - 0 26", 2, "captures:423F1A7B770410D1DE25C840|boundDefenders:D83DBBBC|xRays:9F0882E0|quiet:9D77C01FD87F2439");
		test("2rnn1k1/p3b1pp/1p1pPr2/8/2P2PP1/BP5P/P4PB1/3RR1K1 w - - 0 26", 3, "captures:1CBFA844BFA448DABA50DDF6|boundDefenders:4049BD5C|xRays:DF89A8C8|quiet:004644DE175BE44C");
		test("2rnn1k1/p3b1pp/1p1pPr2/8/2P2PP1/BP5P/P4PB1/3RR1K1 w - - 0 26", 4, "captures:0A781F31BD5F8C1975E18D81|boundDefenders:529902F7|xRays:7F896D91|quiet:1CFBFBD0AD5DD98E");
		test("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", 1, "captures:0D73C74423F66A337AE38D3C|boundDefenders:CF408014|xRays:04128908|quiet:536024331C2AAA96");
		test("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", 2, "captures:ACB139F698BEE40DC85EB075|boundDefenders:2E6AACF7|xRays:660B6370|quiet:CFF89999200B886A");
		test("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", 3, "captures:8FE84AE6C8184AC61D9C3A82|boundDefenders:E122993F|xRays:252F710F|quiet:CB785EB63E9D9B53");
		test("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", 4, "captures:A657491273C0673E107AA28D|boundDefenders:107DA0A0|xRays:8C7FF5B8|quiet:35F5813D7B628C4E");
		test("8/8/3k2p1/2n1bp2/1pBp1P2/1P2P1P1/P3K3/3N4 b - - 0 45", 1, "captures:A642D7F9903C06107D100327|boundDefenders:A063DF75|xRays:04128908|quiet:8F31BC00720893D7");
		test("8/8/3k2p1/2n1bp2/1pBp1P2/1P2P1P1/P3K3/3N4 b - - 0 45", 2, "captures:53E5800A6729C11DD5800D98|boundDefenders:E37DD38F|xRays:0C4E4A69|quiet:6E8B822EF6DE5263");
		test("8/8/3k2p1/2n1bp2/1pBp1P2/1P2P1P1/P3K3/3N4 b - - 0 45", 3, "captures:76A8F54B885EA8050EC2A294|boundDefenders:40535714|xRays:34FB5E3F|quiet:BB7840BC5C2BD27B");
		test("8/8/3k2p1/2n1bp2/1pBp1P2/1P2P1P1/P3K3/3N4 b - - 0 45", 4, "captures:82382E8A7904BCC21E7519B0|boundDefenders:B6D70F60|xRays:76578CDE|quiet:8C5433BFC9D3CB69");
		test("2rr1bk1/5p1p/3np1p1/1p1p4/3P3P/2N1PB2/PPR2PP1/2R3K1 b - - 1 24", 1, "captures:BAF465AEBAF465AECD909549|boundDefenders:73D39560|xRays:04128908|quiet:A3893349C1A01250");
		test("2rr1bk1/5p1p/3np1p1/1p1p4/3P3P/2N1PB2/PPR2PP1/2R3K1 b - - 1 24", 2, "captures:FA1FB4E1DF8BC88D64119BC7|boundDefenders:FBCF19E8|xRays:431529DC|quiet:CC47A59824CCF9A9");
		test("2rr1bk1/5p1p/3np1p1/1p1p4/3P3P/2N1PB2/PPR2PP1/2R3K1 b - - 1 24", 3, "captures:C4111290DE74191E8D9A5094|boundDefenders:2D986881|xRays:67D14C11|quiet:5C5FFDE990DD44D2");
		test("2rr1bk1/5p1p/3np1p1/1p1p4/3P3P/2N1PB2/PPR2PP1/2R3K1 b - - 1 24", 4, "captures:AC9C05AB5CDE59787954BDE8|boundDefenders:E852B1FB|xRays:C7A7CF57|quiet:B3E07F84F68CE675");
		test("5k2/2p2r2/1p4Q1/3P2pP/P1P1P3/5pqP/P4R2/5K2 w - - 2 46", 1, "captures:CD155031BAF465AE32066202|boundDefenders:7943B377|xRays:04128908|quiet:26A100F84175E180");
		test("5k2/2p2r2/1p4Q1/3P2pP/P1P1P3/5pqP/P4R2/5K2 w - - 2 46", 2, "captures:64CF22DC26756C64420B86D4|boundDefenders:AC278B8C|xRays:7E034814|quiet:5455068023839B21");
		test("5k2/2p2r2/1p4Q1/3P2pP/P1P1P3/5pqP/P4R2/5K2 w - - 2 46", 3, "captures:B1AEFCB52F1F6601D4192E03|boundDefenders:976265AB|xRays:A6D740FF|quiet:6F70C47F0FDD2E28");
		test("5k2/2p2r2/1p4Q1/3P2pP/P1P1P3/5pqP/P4R2/5K2 w - - 2 46", 4, "captures:F46C1DD0024E3DB092BEAA9D|boundDefenders:AB0F4BF1|xRays:ECCBB968|quiet:8D2998FC37CFFD57");
		test("4Rnk1/6rp/4Q3/3p1pP1/P1b5/6q1/6B1/1R5K b - - 10 48", 1, "captures:97F70909BAF465AE84C123DA|boundDefenders:A4ED78B3|xRays:04128908|quiet:374F4F892CC45BCE");
		test("4Rnk1/6rp/4Q3/3p1pP1/P1b5/6q1/6B1/1R5K b - - 10 48", 2, "captures:9016BC65EBF26A52E7C152FB|boundDefenders:77A68D9C|xRays:35495BFE|quiet:476592AC9B5EE1E9");
		test("4Rnk1/6rp/4Q3/3p1pP1/P1b5/6q1/6B1/1R5K b - - 10 48", 3, "captures:5A63F03E379BB6BA23495785|boundDefenders:626D2860|xRays:871A19B2|quiet:4792081B5E5EB0EC");
		test("4Rnk1/6rp/4Q3/3p1pP1/P1b5/6q1/6B1/1R5K b - - 10 48", 4, "captures:7F690128434BB46C00CC61AD|boundDefenders:5C681227|xRays:92FF6C96|quiet:A06F01AC332B0A6E");
		test("r1b2bk1/ppnq1p1p/2n3p1/1BPp4/8/1NN2P2/PP1Q1BPP/4R2K w - - 3 20", 1, "captures:BAF465AEC212300B494AE3FA|boundDefenders:7985657A|xRays:04128908|quiet:318349164DE91986");
		test("r1b2bk1/ppnq1p1p/2n3p1/1BPp4/8/1NN2P2/PP1Q1BPP/4R2K w - - 3 20", 2, "captures:C04E6F6037EEE2A1B546B6C4|boundDefenders:CC2DE08D|xRays:0E8A02BA|quiet:91BEF812ACEF6EFD");
		test("r1b2bk1/ppnq1p1p/2n3p1/1BPp4/8/1NN2P2/PP1Q1BPP/4R2K w - - 3 20", 3, "captures:85A7F0B005B090A850E43AD0|boundDefenders:C50BA733|xRays:91E5A8EE|quiet:8B04B6F90C0C9DAA");
		test("r1b2bk1/ppnq1p1p/2n3p1/1BPp4/8/1NN2P2/PP1Q1BPP/4R2K w - - 3 20", 4, "captures:AD7357C0F478E1147991E0E4|boundDefenders:30D5CEFD|xRays:21DFFEC7|quiet:391A18820CA13F48");
		test("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", 1, "captures:3521D51F0379E3E228C92146|boundDefenders:7257061A|xRays:04128908|quiet:A85BDC9FCFBC8421");
		test("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", 2, "captures:CE72D382282B642A2C2F8201|boundDefenders:1F4EAEB0|xRays:AD6E8901|quiet:EEE29FEDCE16B899");
		test("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", 3, "captures:F3F8D55BF3A1B9827B8F9B64|boundDefenders:97C10846|xRays:2C0922AD|quiet:BC15627D45AF8C6E");
		test("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", 4, "captures:39BF8DC500E991AA6D78E1F8|boundDefenders:2BA4E3A4|xRays:251D4E27|quiet:9311180E326099D5");
		test("r3r1k1/3q1pbp/2p3p1/4p3/p1P1P3/2Rp1P2/PP4PP/2NQ1RK1 b - - 1 22", 1, "captures:FABAC4B8BAF465AE7027EB21|boundDefenders:27E966DF|xRays:04128908|quiet:9BAAEF27159BD852");
		test("r3r1k1/3q1pbp/2p3p1/4p3/p1P1P3/2Rp1P2/PP4PP/2NQ1RK1 b - - 1 22", 2, "captures:B8E7344A791238E67A712984|boundDefenders:B916277F|xRays:9D4BB32A|quiet:2242ADA97D4F1A51");
		test("r3r1k1/3q1pbp/2p3p1/4p3/p1P1P3/2Rp1P2/PP4PP/2NQ1RK1 b - - 1 22", 3, "captures:99032417F5A5F22B1355F839|boundDefenders:12EE8955|xRays:E7B06447|quiet:AE753688A8698DD4");
		test("r3r1k1/3q1pbp/2p3p1/4p3/p1P1P3/2Rp1P2/PP4PP/2NQ1RK1 b - - 1 22", 4, "captures:38F269926B8801D7999CD03E|boundDefenders:90E0B1CD|xRays:56C11CFF|quiet:DD365F7BA676BBF2");
		test("4rk2/2p1ppbp/1n1p4/3P1N2/rPPP1P2/p6P/B2R2PK/1R6 b - - 2 27", 1, "captures:BAF465AEF340E1B79AD9E51B|boundDefenders:DFF0C883|xRays:04128908|quiet:9D0F0710D0D4892A");
		test("4rk2/2p1ppbp/1n1p4/3P1N2/rPPP1P2/p6P/B2R2PK/1R6 b - - 2 27", 2, "captures:C613358B16B820DB67D9B782|boundDefenders:9345BCD7|xRays:7E034814|quiet:CD274932D872CCF2");
		test("4rk2/2p1ppbp/1n1p4/3P1N2/rPPP1P2/p6P/B2R2PK/1R6 b - - 2 27", 3, "captures:FF2DA0F72882462887999DFF|boundDefenders:AEF8159A|xRays:B63212FA|quiet:35B72C6675B56DF2");
		test("4rk2/2p1ppbp/1n1p4/3P1N2/rPPP1P2/p6P/B2R2PK/1R6 b - - 2 27", 4, "captures:6F414FC6F9BE43568ED84668|boundDefenders:1CFB87AA|xRays:52A96D67|quiet:A3A2D79493844C05");
		test("r1b2rk1/ppp2p2/3p1q1p/3Pb1p1/2P1P1B1/2N1Q2P/PP3PP1/R3K2R b KQ - 4 18", 1, "captures:BAF465AEB3DA8CC90CCA2DB1|boundDefenders:9C921EDA|xRays:04128908|quiet:C37A8C9B2726FB4C");
		test("r1b2rk1/ppp2p2/3p1q1p/3Pb1p1/2P1P1B1/2N1Q2P/PP3PP1/R3K2R b KQ - 4 18", 2, "captures:5D23BABABA509E8CFBD13F5A|boundDefenders:F7D0BEE9|xRays:A3960553|quiet:5FC2A0EFCFCF91CF");
		test("r1b2rk1/ppp2p2/3p1q1p/3Pb1p1/2P1P1B1/2N1Q2P/PP3PP1/R3K2R b KQ - 4 18", 3, "captures:A5B6BA66680B83F4F858CAF0|boundDefenders:041CF30C|xRays:06B15AD2|quiet:1BEDD56BF327157F");
		test("r1b2rk1/ppp2p2/3p1q1p/3Pb1p1/2P1P1B1/2N1Q2P/PP3PP1/R3K2R b KQ - 4 18", 4, "captures:E3E95FF566CDB05FC549A18D|boundDefenders:0575DE30|xRays:8F4AF27D|quiet:B5FB9DADEACBD21E");
		test("8/p2q2k1/1pnr1pp1/3Bp3/2P1P1P1/7R/P4PK1/3Q4 w - - 4 42", 1, "captures:BAF465AE041D824356317F20|boundDefenders:EE55D6D4|xRays:04128908|quiet:1806B9195883548B");
		test("8/p2q2k1/1pnr1pp1/3Bp3/2P1P1P1/7R/P4PK1/3Q4 w - - 4 42", 2, "captures:8AB053B9235B174574D9CC61|boundDefenders:4E40CC69|xRays:53810A0A|quiet:A20827D27FC6EDD4");
		test("8/p2q2k1/1pnr1pp1/3Bp3/2P1P1P1/7R/P4PK1/3Q4 w - - 4 42", 3, "captures:874160B286280B539BDDEF43|boundDefenders:F6468ABA|xRays:A10F8B84|quiet:F1169D4DF8F4BA98");
		test("8/p2q2k1/1pnr1pp1/3Bp3/2P1P1P1/7R/P4PK1/3Q4 w - - 4 42", 4, "captures:D3C23C81CDF59AC4B69623D2|boundDefenders:2829A6BC|xRays:3B10D50A|quiet:3A089B902C521DC9");
		test("2b1r2r/pp4k1/1bp2p2/3p1n2/8/2N2NPB/PP3PP1/3RRK2 b - - 1 30", 1, "captures:BAF465AE654706DFB2915D8D|boundDefenders:9E3F8DB0|xRays:04128908|quiet:03AC6C899030FAC1");
		test("2b1r2r/pp4k1/1bp2p2/3p1n2/8/2N2NPB/PP3PP1/3RRK2 b - - 1 30", 2, "captures:3A2E916F3137ECD1E797632B|boundDefenders:1B23BBE0|xRays:92E7D2DB|quiet:FFEF32BDC23413CB");
		test("2b1r2r/pp4k1/1bp2p2/3p1n2/8/2N2NPB/PP3PP1/3RRK2 b - - 1 30", 3, "captures:18DE5A13A39D2BDA8C4CE8D0|boundDefenders:41FC980D|xRays:C7E40919|quiet:467B9EA4A7258D4E");
		test("2b1r2r/pp4k1/1bp2p2/3p1n2/8/2N2NPB/PP3PP1/3RRK2 b - - 1 30", 4, "captures:1740EF168EEC29D21A64781D|boundDefenders:0EE5AA4F|xRays:79C5F35D|quiet:00CE7EE146E61F47");
		test("r3nrk1/pp3pbp/1q2p1p1/4B3/1n1P4/1BN2Q1P/PP3PP1/3RR1K1 w - - 6 17", 1, "captures:BAF465AE7C2D3BE87A748906|boundDefenders:E21E56F1|xRays:04128908|quiet:C507C991BE8A584D");
		test("r3nrk1/pp3pbp/1q2p1p1/4B3/1n1P4/1BN2Q1P/PP3PP1/3RR1K1 w - - 6 17", 2, "captures:A94E33421A660536EAEF7386|boundDefenders:E789B1B5|xRays:0C5929F0|quiet:C9D4F0C4CCACE0EA");
		test("r3nrk1/pp3pbp/1q2p1p1/4B3/1n1P4/1BN2Q1P/PP3PP1/3RR1K1 w - - 6 17", 3, "captures:6507B34484B2D9C766CCD573|boundDefenders:C6256AE8|xRays:184FCCEB|quiet:83F2362FD9B1F06F");
		test("r3nrk1/pp3pbp/1q2p1p1/4B3/1n1P4/1BN2Q1P/PP3PP1/3RR1K1 w - - 6 17", 4, "captures:CA42BB60AB3F9DBDD0FFFB77|boundDefenders:D462568F|xRays:4C8DCC1E|quiet:F0EECBE09EF7B1EE");
		test("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27", 1, "captures:6CD7BD8B580398DC6924FB46|boundDefenders:F7108C6F|xRays:04128908|quiet:C6C89BDAB1799A0D");
		test("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27", 2, "captures:9C2DF1C5EDB9C90E463AFE94|boundDefenders:089F2CF5|xRays:9D4BB32A|quiet:071E09989945481B");
		test("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27", 3, "captures:E8403834BB4AA0474FEBC562|boundDefenders:ECCB678C|xRays:F48B5328|quiet:E616D1943F45EDF7");
		test("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27", 4, "captures:9D998E7DE2B1293C23B787D2|boundDefenders:62025792|xRays:F01DA31B|quiet:DDF0FB63BC90FC35");
		test("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26", 1, "captures:158F8C59BAF465AEAFB3FDEF|boundDefenders:63A69BD0|xRays:04128908|quiet:5F08CC0EA509C27B");
		test("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26", 2, "captures:9E80556F0F02E53D83F8DA17|boundDefenders:EB7B51AE|xRays:585684C3|quiet:E3E465F14E161E04");
		test("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26", 3, "captures:D251FE3D3ACE0E9111B77903|boundDefenders:D31F9BED|xRays:C4FF0761|quiet:3DBA5839083E2821");
		test("r5k1/1q2r2p/6p1/p2pbp2/1p1PnPPN/1PpQR3/P1P4P/5RK1 w - - 0 26", 4, "captures:CDC3F016892DAC44D173789D|boundDefenders:50C96AFD|xRays:9DEDE8DB|quiet:47C2E110090E8412");
		test("r2r2k1/1b2qppp/3p1b2/p1nP4/2R1PB2/Q4N1P/5PP1/1N2R1K1 w - - 0 26", 1, "captures:BAF465AEBAF465AE516D94E9|boundDefenders:D585274F|xRays:04128908|quiet:2483F6017D39B5F8");
		test("r2r2k1/1b2qppp/3p1b2/p1nP4/2R1PB2/Q4N1P/5PP1/1N2R1K1 w - - 0 26", 2, "captures:3B22C231485C0136D266FBC9|boundDefenders:CD5F1DBE|xRays:AD6E8901|quiet:0E7E0214628F0141");
		test("r2r2k1/1b2qppp/3p1b2/p1nP4/2R1PB2/Q4N1P/5PP1/1N2R1K1 w - - 0 26", 3, "captures:F167B0BC64ECE135A8BCB19C|boundDefenders:AEF40A37|xRays:19C6DBCD|quiet:9A4BEAA5AF188A53");
		test("r2r2k1/1b2qppp/3p1b2/p1nP4/2R1PB2/Q4N1P/5PP1/1N2R1K1 w - - 0 26", 4, "captures:41C214EAF6EC1DACDFAFF18D|boundDefenders:113DB7D3|xRays:18A9DC00|quiet:FD3A14A586ED77F8");
		test("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29", 1, "captures:BAF465AEBAF465AE242C3F3C|boundDefenders:01C3B7D8|xRays:04128908|quiet:8F65098657722F6A");
		test("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29", 2, "captures:38F8BB6FD5BABA0D0443AF05|boundDefenders:0A44284B|xRays:0C4E4A69|quiet:89886D1FD4BA28B5");
		test("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29", 3, "captures:7CD07F2B357508F32676F5E6|boundDefenders:BDF3B57C|xRays:B562C810|quiet:4524EB289D279C56");
		test("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29", 4, "captures:CFC123535E35DCF7DA0DD1BA|boundDefenders:83D1BE07|xRays:D298D0A5|quiet:6C56EFCEE2F54359");
		test("3r2k1/ppp2pp1/2n4p/b1P5/7P/2PqQN2/P2B1PP1/5RK1 w - - 2 23", 1, "captures:BAF465AEE880826FA13DFC26|boundDefenders:B4EB9CF7|xRays:04128908|quiet:518AD5649B1A48F8");
		test("3r2k1/ppp2pp1/2n4p/b1P5/7P/2PqQN2/P2B1PP1/5RK1 w - - 2 23", 2, "captures:17E39427E16C54B31FD0E50C|boundDefenders:BC63A610|xRays:A3960553|quiet:F6B4E4C4FF9687B9");
		test("3r2k1/ppp2pp1/2n4p/b1P5/7P/2PqQN2/P2B1PP1/5RK1 w - - 2 23", 3, "captures:525396AE57EA0AA8CDB275F3|boundDefenders:1427E55C|xRays:584D2F85|quiet:26267203D08245AD");
		test("3r2k1/ppp2pp1/2n4p/b1P5/7P/2PqQN2/P2B1PP1/5RK1 w - - 2 23", 4, "captures:A5596917BE4CF07AF0C47121|boundDefenders:C111F05C|xRays:6EE65C56|quiet:8691CD70899E75BB");
		test("r4r1k/pp1q2bp/3p2p1/2p2b2/2Pn4/2N1BPP1/PP3PBP/2RQR1K1 w - c6 0 19", 1, "captures:BAF465AE25FAA83CE6345839|boundDefenders:87C770A6|xRays:04128908|quiet:6280F842EB42CB60");
		test("r4r1k/pp1q2bp/3p2p1/2p2b2/2Pn4/2N1BPP1/PP3PBP/2RQR1K1 w - c6 0 19", 2, "captures:95ADE141B7F830B0AB12B3AD|boundDefenders:3ADB4CB4|xRays:9A8017CC|quiet:AC50CDEB00FA0CFE");
		test("r4r1k/pp1q2bp/3p2p1/2p2b2/2Pn4/2N1BPP1/PP3PBP/2RQR1K1 w - c6 0 19", 3, "captures:8D755330F4B139C141876A18|boundDefenders:6A944A31|xRays:D4CA03DE|quiet:4FBA0FB1DD07C3F2");
		test("r4r1k/pp1q2bp/3p2p1/2p2b2/2Pn4/2N1BPP1/PP3PBP/2RQR1K1 w - c6 0 19", 4, "captures:CACF3D7AE7D4A714EFA486AF|boundDefenders:65609BB8|xRays:9AC76238|quiet:5B1EB59E6259A149");
		test("r1b1k2r/ppq2ppp/2nbp3/3pN3/3PnB2/2PB1N2/PP3PPP/R2QK2R b KQkq - 2 10", 1, "captures:BAF465AED2DA333490AD979E|boundDefenders:4E2BCCFF|xRays:04128908|quiet:6399808EE172880A");
		test("r1b1k2r/ppq2ppp/2nbp3/3pN3/3PnB2/2PB1N2/PP3PPP/R2QK2R b KQkq - 2 10", 2, "captures:AC1B91730EFE8C525190230B|boundDefenders:44CDC877|xRays:37628A9D|quiet:1DB8470B99AF1586");
		test("r1b1k2r/ppq2ppp/2nbp3/3pN3/3PnB2/2PB1N2/PP3PPP/R2QK2R b KQkq - 2 10", 3, "captures:AD299952C738163D0FB6B763|boundDefenders:53CAD5CF|xRays:46A6A41F|quiet:AEAB0A63C4E1F61C");
		test("r1b1k2r/ppq2ppp/2nbp3/3pN3/3PnB2/2PB1N2/PP3PPP/R2QK2R b KQkq - 2 10", 4, "captures:A098896802B5E9B8B4126295|boundDefenders:291E3D0F|xRays:FBE6B7A3|quiet:20E5438648713501");
		test("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", 1, "captures:BAF465AEBAF465AE603C33B7|boundDefenders:D0D0D3C6|xRays:04128908|quiet:201D30B8FE4D15E8");
		test("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", 2, "captures:AE8A89F7368013EC1B9335C5|boundDefenders:2FEEBF79|xRays:7C298C23|quiet:DA7D3DF9B4D6C236");
		test("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", 3, "captures:F0252CA6F77ECF3BDB8D08B4|boundDefenders:BEB185B3|xRays:6638060B|quiet:31135FFA8F6CAA2F");
		test("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", 4, "captures:E3B8FB9E4ADA7530911A1D5B|boundDefenders:57192870|xRays:74A2DCF1|quiet:B15C15CEC57875B1");
		test("3q1r1k/p5bp/br4p1/3B4/3NQ3/R2n2P1/PP1N1PP1/2R3K1 w - - 1 26", 1, "captures:EA40E251272FB31C02D6365C|boundDefenders:A2F26781|xRays:04128908|quiet:2469996D9E56E3AA");
		test("3q1r1k/p5bp/br4p1/3B4/3NQ3/R2n2P1/PP1N1PP1/2R3K1 w - - 1 26", 2, "captures:D6E67CFD205770B8A1D2EF7A|boundDefenders:BDA331EC|xRays:EC93225C|quiet:2CD9CDD54C78A3C0");
		test("3q1r1k/p5bp/br4p1/3B4/3NQ3/R2n2P1/PP1N1PP1/2R3K1 w - - 1 26", 3, "captures:D4BAA2A60615CF12689075CE|boundDefenders:5E3124CC|xRays:BBE8377E|quiet:9EC0760A4255E310");
		test("3q1r1k/p5bp/br4p1/3B4/3NQ3/R2n2P1/PP1N1PP1/2R3K1 w - - 1 26", 4, "captures:8A0627AB9C78EE7F286A6E96|boundDefenders:672A22D5|xRays:D3BBCCC8|quiet:AB6DC01CD481DE9B");
		test("3q4/3rr1k1/1nn2bbp/1pp5/4N1N1/1P1P3P/1PK2QP1/3R1R2 w - - 0 1", 4, "captures:77EF35DE828245048717D440|boundDefenders:44EF14C4|xRays:95CA3300|quiet:F52FD94B52891E6D");
		test("8/2kr1bb1/3r1q2/1n1p4/1n6/1NPQ4/1K1R1BB1/3R4 w - - 0 1", 4, "captures:4FF4AFA5D866ECC23BE2AE97|boundDefenders:D1A67CDF|xRays:DC073C68|quiet:117A315E08BAD040");
		test("4r1k1/p4pb1/1p4p1/2p2q2/7p/P1P1B2P/1P1R1PP1/3Q2K1 w - - 6 35", 4, "captures:2FB18E39D3D43C2775735F71|boundDefenders:5356A55F|xRays:68CB584E|quiet:8C8F78AD2AAD4A29");
		test("r1bq1r2/4n1k1/2pp2pp/p1p1p3/4Pp2/P1NP1N1P/1PPQ1PP1/1R3RK1 w - - 0 15", 4, "captures:A5D69871044D5F2DDC1E5D4B|boundDefenders:B169C490|xRays:6E335116|quiet:EE232E0827669882");
		test("8/ppn2p2/2p1bk1p/2P4P/PP1PBKP1/5P2/8/8 w - - 1 38", 4, "captures:F76FE1CF2DF73936E1C625C6|boundDefenders:3DE3B7D0|xRays:B6C0362D|quiet:30E6AA8FBA8ECE7F");
		test("r5k1/p4p1p/2nR2p1/2p1r1N1/2b5/P1P1B2P/2P2PP1/4K2R b K - 0 20", 4, "captures:A5F1DBBFC41C8EE6E662B38A|boundDefenders:67819116|xRays:1883C843|quiet:889E4BC162B2F484");
		test("r2q2k1/ppn2pbp/2p2ppB/5B2/3P3Q/7R/PPP2PPP/6K1 b - - 4 19", 4, "captures:75C903AF4A04407F717A1E9D|boundDefenders:7C9ACB3B|xRays:E2129B57|quiet:460D01B413B82C41");
		test("2b1r1k1/1p3pp1/2p1pb1p/q2P4/p1P5/3B1Q2/PP3PPP/R3R1K1 b - - 0 23", 4, "captures:5F1536220DADEE4D8D0E53B0|boundDefenders:218B1A94|xRays:5E771FC4|quiet:8B58C6BEE27E525A");
		test("r1b1Rnk1/ppp2p1p/6pQ/5p2/3q4/3B2N1/PPP2PPP/6K1 b - - 1 16", 4, "captures:D1674E9B0FF2876EC5CCE6D3|boundDefenders:FC2BCA8D|xRays:565A36C0|quiet:F0F7242B64749D7D");
		test("4r3/p1q3k1/5r1p/3Bb1p1/2P1pp2/1P5P/PBQ2PP1/1R1R2K1 w - - 0 26", 4, "captures:110866FBA68ED8FB71C261DC|boundDefenders:A094434F|xRays:5356827C|quiet:E033D89FE4924759");
		test("r3r1k1/pp1q1pp1/2p2p2/5b1p/3P1P2/2PBR1Q1/PP3P1P/R5K1 b - - 1 19", 4, "captures:817074D52E54DB356D1E9F66|boundDefenders:37E2B9F1|xRays:1AC25E27|quiet:8B4958D9E03D7B87");
		test("r2qr2k/p6p/3ppnR1/2p5/1bpnPP2/2N4Q/PPPB3P/2KR4 w - - 0 21", 4, "captures:2B5E3AE6DCDB5804DA73688C|boundDefenders:FF338A65|xRays:4789DB58|quiet:23879EFEBA2CE4AC");
		test("6rk/1pp4p/pb1pNpq1/3P4/8/P4Q2/1PP3PP/R6K w - - 5 23", 4, "captures:4193771499C2EB03A2778B1E|boundDefenders:BDF41300|xRays:8EEBB95A|quiet:05F547CF63A49F35");
		test("5rk1/1p2Rpbp/3p2p1/3P4/5P2/3QB1KP/5RP1/q6r b - - 7 23", 4, "captures:2FD74DB1990B0B3DBF6D5A67|boundDefenders:40D64717|xRays:49FD7F79|quiet:46372962AC70AC8B");
		test("r2qr1k1/p2n1pp1/1p3n1p/3p4/3P3B/3QPN2/P4PPP/R4RK1 w - - 0 16", 4, "captures:4CDC553E158237EB2BF3488E|boundDefenders:FAB08288|xRays:72A09A19|quiet:5B157DB70D441B52");
		test("r2qr1k1/pp3pb1/2p1Rpp1/8/2PP1BQ1/6P1/1PB2P2/5RK1 w - - 1 27", 4, "captures:BE5CF795AC72BAA080DB5366|boundDefenders:3550C6EC|xRays:F31CDEE2|quiet:5C94285B176FA9E1");
		test("8/1p3r2/3pk3/p1q2p2/P2b4/3P3Q/1P3RPP/5RK1 b - - 2 26", 4, "captures:2A61484ADE6B039BA86764F4|boundDefenders:89DD5486|xRays:71817B65|quiet:517A2000F98F9FBD");
		test("r2q1rk1/pp3pp1/2n2n1p/3p3b/7B/2PB1N1P/P1P2PP1/R2Q1RK1 w - - 1 14", 4, "captures:A56BD4C3907292B59246FFB1|boundDefenders:9F0EAF96|xRays:F86DD70E|quiet:3F22CD44B8A50491");
		test("3r1rk1/p4p2/2p3pp/2q5/4Q3/1PpN3P/P1P1R2K/R7 b - - 2 26", 4, "captures:F1B96830D9B96A55405BB458|boundDefenders:57592D36|xRays:16E545FF|quiet:8FE05391612848A6");
		test("r1bk3r/1pqpb3/p3p2p/2p1NppQ/2P1N3/3P2PP/PP1B1P2/R4RK1 w - - 2 17", 4, "captures:768993C8ED9D63C1EA0904D5|boundDefenders:2A2244A7|xRays:57E93437|quiet:F2232B7C48BE7FF4");
		test("5rk1/pp1r1ppp/1qp1pn2/8/2PP4/1PQ2BP1/P4P1P/3RR1K1 w - - 5 19", 4, "captures:DDCCDFED0D5734EBA7072006|boundDefenders:9127D0A3|xRays:A27DC637|quiet:9B28A15B697C4A06");
		test("b3r1k1/5p2/p3q2p/3p1Bp1/1P1Q2PP/P1R1PP2/6K1/8 b - - 1 35", 4, "captures:854600E100213738F2D17131|boundDefenders:AFB17172|xRays:B599C3DB|quiet:47F5BD9A0503BA6E");
		test("2qr2k1/5rp1/1p1b1n1p/p2BB1p1/2P3P1/1P5P/P1Q2P2/3RR1K1 b - - 0 31", 4, "captures:85E28A1076AE423CABD78237|boundDefenders:CE7AF12B|xRays:F71D74F3|quiet:FF16252A0DEA6A71");
		test("2k4r/1p2b2p/1p4p1/1b2n3/3pN2N/8/PPP3PP/R3R1K1 w - - 2 24", 4, "captures:7B7F8C28AE1504ABD9BA0A73|boundDefenders:19187BB8|xRays:54375AEA|quiet:9EB411C01D874EA3");
		test("4r1k1/p5pp/8/8/2qb1N2/4nQP1/P5BP/4R2K b - - 1 32", 4, "captures:29F4CBF379A768612A409AF7|boundDefenders:D4A0C164|xRays:DC189F1F|quiet:50FCDF4F8EE2D71D");
		test("3r2k1/p7/2p1p1Pp/2p5/2b3Pq/7P/P1P2RB1/4Q1K1 b - - 0 34", 4, "captures:E17B94605EAD2DB94E7032AA|boundDefenders:BC177904|xRays:2E255FC6|quiet:235F87A5FC6C45DA");
		test("6k1/1ppq1rp1/p1n4p/4pN1n/2b1P1P1/2P1Q2P/PP3RBK/8 w - - 0 26", 4, "captures:378368B649C33899CD874B92|boundDefenders:581DF2B0|xRays:DA95BFB0|quiet:DD17F4AE476929C6");
		test("r4qk1/pp1n2pp/2p5/3P4/3P1r2/3B3P/P2Q1PP1/R3R1K1 b - - 0 19", 4, "captures:8295910C04CC1C0332E2B0E1|boundDefenders:4F64E789|xRays:3108EDB5|quiet:84005C73DC652EAA");
		test("2r2rk1/pp2bpp1/2n4p/3pPR1P/3P2P1/2B2N2/PP6/5RK1 b - - 2 24", 4, "captures:5419BAC8C272B1C93014E81A|boundDefenders:8D1AA6C9|xRays:0A513F98|quiet:75C6E7AC63331EAD");
		test("r2q2k1/pb2b1pp/1p6/2p1P3/5r2/1P2pP2/PB4BP/2RQ1RK1 w - - 0 19", 4, "captures:CF01DFAD1332C61527B354CB|boundDefenders:725826D6|xRays:A1C41A08|quiet:BE9608B747D8F7B6");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 1, "captures:A5F49E5DBAF465AE9DCE3ACB|boundDefenders:6021254A|xRays:04128908|quiet:0855D79288935B83");
		test("qk1n1r2/pb2P3/3n4/8/p1P1Q1P1/7P/1R3KQ1/1R5B w - - 0 1", 2, "captures:100C6C5A228FEA769A4FE718|boundDefenders:D8DE8722|xRays:A5715121|quiet:4EAA953D79962EF1");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 1, "captures:66A5593037BA0608B635C425|boundDefenders:6A623DFE|xRays:04128908|quiet:05E0039CCD446B8C");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 2, "captures:054B65D1A9EE36EB38187611|boundDefenders:3833662A|xRays:9A8017CC|quiet:0497B27F6F67A437");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 3, "captures:E9DB453EDD9795C5B6CC3157|boundDefenders:6B3A2E0E|xRays:F8A49E7B|quiet:B84AED1F9F7357F9");
		test("1k1r2q1/pbp3qp/1b1r2q1/1P2n3/1N3nQ1/1P5B/PBP1NPRP/2K1Q1RB w - - 0 1", 4, "captures:4081D095389E2E63B7A76E52|boundDefenders:84423762|xRays:141BC3BD|quiet:FCB9A938222F694B");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 1, "captures:DB063BEE1C56AC35BAF465AE|boundDefenders:DE6E6A76|xRays:04128908|quiet:D1D23DAB548709AE");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 2, "captures:ACB65D628DB09B4F081CD0DC|boundDefenders:0C42FBB1|xRays:EA2E6577|quiet:18D53AE08C4A08CE");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 3, "captures:B3BF9E0D2FFFD3389DB09952|boundDefenders:69F7D5B3|xRays:3F816849|quiet:DB5B291EB905D6DF");
		test("2r5/1k1q4/3n4/b7/8/2N1Q3/4K2B/5R2 w - - 0 1", 4, "captures:B8E86F953609EECF5D475AD9|boundDefenders:936994CF|xRays:49D616CE|quiet:F5F763777E7FF333");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 1, "captures:B15CB2A81C56AC35BAF465AE|boundDefenders:EBDAE96B|xRays:04128908|quiet:6A4CFA328F862B74");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 2, "captures:40C3DCD58E06790BC4CE879D|boundDefenders:2DDC1FD7|xRays:976306B1|quiet:9114B74A2BC95358");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 3, "captures:CAEE9C38F2493D2DC814296E|boundDefenders:3AA30EBA|xRays:3ADE41DA|quiet:F9C18204BB71BD5E");
		test("6r1/4qppb/k2np3/pp1p3P/2p3P1/P1P2P2/1PNPPQ1B/4K1R1 w - - 0 1", 4, "captures:41B4E3B3FA4042DD99246D14|boundDefenders:8E940D28|xRays:32E86D9D|quiet:2A1803BC2BF9D2EC");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 1, "captures:E4E9150FD611E477589B334F|boundDefenders:1D8EB87C|xRays:04128908|quiet:3FE9C22BE8AC4734");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 2, "captures:4E7D21DFA6017735F27CAE6D|boundDefenders:A846A185|xRays:FFF1FC58|quiet:E4DF76B366843BBB");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 3, "captures:3F6839C935790793D40C169C|boundDefenders:AFFD8B36|xRays:37105BFC|quiet:C0861FB1B9C8151D");
		test("8/2kp1rp1/1np4p/7P/3q4/1PNR1q2/1P1r2B1/1K1R3Q b - - 0 1", 4, "captures:3D936835BA74283D294D6519|boundDefenders:A6EE9498|xRays:8B5349C1|quiet:7884ED40A1481CD9");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 1, "captures:01004DF5BAF465AED900B10A|boundDefenders:11F41CB1|xRays:04128908|quiet:50F5F9B50BC1FD5F");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 2, "captures:BE328BA923FD4782114FA414|boundDefenders:6CE1031A|xRays:A3960553|quiet:A27AB0C4B0D10D95");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 3, "captures:C756FA83DAEFC2800D37F969|boundDefenders:7C09F8BB|xRays:788C5CF1|quiet:1D471C49C92B6413");
		test("3q3k/p2pP1pp/3Q4/2Pp1p2/R4P1P/Br6/5K2/7R b - - 2 32", 4, "captures:A6E5860FC54559D2394E3D7D|boundDefenders:C896A93C|xRays:6A2F185C|quiet:097B190CC18732E5");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 1, "captures:BAF465AEBAF465AE6E1CB653|boundDefenders:6B03979F|xRays:04128908|quiet:72667DD09B085263");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 2, "captures:AEDEBE202787B35DDE1AA70E|boundDefenders:9E4472AA|xRays:264767CB|quiet:7E3DCB43E4B12E53");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 3, "captures:F153D50002B7B8CB1E55EB1A|boundDefenders:70D334AA|xRays:62CDE83C|quiet:9EA31AAA47608A6F");
		test("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", 4, "captures:2FD6AF138E5ABC5B1D457460|boundDefenders:9ADF8EE7|xRays:F3E859E0|quiet:FED68C588505B583");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 1, "captures:A6D3FDBBBAF465AE55E1B6DE|boundDefenders:FB6A7F28|xRays:04128908|quiet:717930C077DF746D");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 2, "captures:AC1E35056C855EFDAF206020|boundDefenders:098ED3A7|xRays:7E034814|quiet:97B887A9AA208DB5");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 3, "captures:F48FDFBA7F9C961E24C46F94|boundDefenders:327454D9|xRays:551CBDAE|quiet:FB3C333F59EAF807");
		test("r1bq1rk1/1pNp1ppp/p1nQpn2/8/4P3/2N5/PPP2PPP/2KR1B1R b - - 1 12", 4, "captures:8BEE283EB480154ABB2F1E60|boundDefenders:F675B760|xRays:3E81DC7A|quiet:E03DDDC70BB60386");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 1, "captures:BAF465AEC204B8EFB75B8D46|boundDefenders:01C3D83C|xRays:04128908|quiet:9D779E8953C9C682");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 2, "captures:2858192C092244F7D0CC5F51|boundDefenders:ED53C8A0|xRays:585684C3|quiet:7451C3E2DBEBFAD9");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 3, "captures:6412E246186CA082CA39F85C|boundDefenders:BDDC5C68|xRays:04C6A328|quiet:31AD36803D0C228C");
		test("2kr3r/ppp3pp/2nq1p2/3n3b/3P4/4BN1P/PP2BPP1/R2Q1RK1 w - - 2 15", 4, "captures:7C2B144CCE15914E8C8DC8B7|boundDefenders:1E496192|xRays:FD0E945A|quiet:2B24DE53FBA721EE");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 1, "captures:6EBB389DAE0F930A5BB507F2|boundDefenders:04128908|xRays:04128908|quiet:5CCB9EEB4C0C59E9");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 2, "captures:1E06EC9CAC38044B2FEBD672|boundDefenders:D3A1E3FF|xRays:2AB7342B|quiet:BE97DCBCC052DF33");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 3, "captures:551C6EDCFBE04544956BFAB1|boundDefenders:FF5AAC8A|xRays:05BF7CD9|quiet:9838D6D71AFA8F1B");
		test("r1bqr1k1/p1p2ppp/2p2n2/3p4/4P3/2BB4/PPP2PPP/R2QR1K1 b - - 0 11", 4, "captures:D22E135714D5F72928B6D4E6|boundDefenders:AF108B64|xRays:0225C9E0|quiet:151182243C1253C0");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 1, "captures:30CBBC2BBAF465AEF75594AC|boundDefenders:31E21129|xRays:04128908|quiet:18FA504A609AE621");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 2, "captures:CF1D35A8D55895A9E2C18565|boundDefenders:557B02D1|xRays:9A8017CC|quiet:28A13E76BF0C9F6E");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 3, "captures:FA975CE3F3172B738F194F71|boundDefenders:222E89D2|xRays:097A59A2|quiet:A0BD7E0E1F6B2B99");
		test("5Bk1/1R3ppp/p1r5/4p3/3pP1b1/P2P2Pq/2P2Q1P/5RK1 b - - 0 33", 4, "captures:EDC68D3423C9B00B54E76F01|boundDefenders:510FADF7|xRays:D132BA7C|quiet:0657983CDC8CCB8A");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 1, "captures:3A257C26BAF465AEA217EBA0|boundDefenders:6AD2536C|xRays:04128908|quiet:2A857E55BAC34C9F");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 2, "captures:363B04ED5B3F99D4E33C18FC|boundDefenders:2358CA79|xRays:37628A9D|quiet:ACF4178BB77557E8");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 3, "captures:4BCFA218208328D6C020B258|boundDefenders:968C0AF3|xRays:4BB1EE09|quiet:76E5F1AE767E8555");
		test("r4rk1/pp1qbppp/3p1n2/2p1p3/2Pn4/P1NPP1PP/1P3PB1/R1BQ1RK1 b - - 0 12", 4, "captures:7C43A824C08BB5AA844822CD|boundDefenders:5A128868|xRays:F0CC0164|quiet:7712F1508DD98405");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 1, "captures:DCE47EC27E2E028DEC15DE60|boundDefenders:5998BA6C|xRays:04128908|quiet:AF657F41D551B4FA");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 2, "captures:C2F463E3E88215538D8CD8E2|boundDefenders:FD40A63F|xRays:53810A0A|quiet:A63827200713A5D6");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 3, "captures:3BD27A60C9CAA1B5C75D7120|boundDefenders:4949C250|xRays:17F98310|quiet:5A98D1C588B1F34E");
		test("1k1q4/1p1rpb2/2p2n2/8/5n2/3QN1P1/5PBP/3R2K1 w - - 0 1", 4, "captures:A2AD3530E049CA37982D6564|boundDefenders:9F7006C6|xRays:719AE7B0|quiet:55A0E36BF99335D4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 1, "captures:D7E88ADA27421BF7322D112E|boundDefenders:77666532|xRays:04128908|quiet:AC744126681A5CF4");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 2, "captures:00B891C7A2D0132F2889C0BE|boundDefenders:C34D4306|xRays:976306B1|quiet:FF02460B6DE9EDAF");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 3, "captures:4DE0FE86584E46C8D5E9918D|boundDefenders:2BCF53B7|xRays:FE156354|quiet:417EB52A93C3613D");
		test("2r1k2r/pp5p/8/1b1pB3/1q2nP2/3BK1P1/P6P/R2R4 w k - 0 30", 4, "captures:51D7C9B35E5326352411CABC|boundDefenders:D2CAC1D7|xRays:A252B92A|quiet:06427B7340FDBE43");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 1, "captures:BAF465AEBAF465AED279B078|boundDefenders:84C9D423|xRays:04128908|quiet:AD9F1F66F1C181FA");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 2, "captures:5545B450FB74E40022814D04|boundDefenders:6136F775|xRays:BBB77BB5|quiet:B53EB265F51C67E7");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 3, "captures:0301BAA73F73DF68F4109787|boundDefenders:365530F4|xRays:8E96B403|quiet:2B13DBE256E6CEAC");
		test("rnb1k1nr/ppp2ppp/1q1bp3/8/5P2/P1N2QP1/1PPPN2P/R1B1KB1R b KQkq - 0 8", 4, "captures:0ECD9057446FEAFFBDD4C9FE|boundDefenders:1538EC97|xRays:41A5AB96|quiet:61CEE2250E3A370C");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:210C5EEF90918B36");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 2, "captures:26AEFE1F6D16C9485908A0DD|boundDefenders:4AB78C56|xRays:67BBAF86|quiet:F285B6DD9C0D6183");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 3, "captures:5F7701325ABA525998C51300|boundDefenders:CC8FB378|xRays:39DBF118|quiet:430559CBACB00D25");
		test("5r2/6p1/pppk1p2/6p1/PPKP4/4RP1P/6P1/8 b - b3 0 35", 4, "captures:5314D13A0DE56DCE7AD694F3|boundDefenders:8B51DFC3|xRays:964F580E|quiet:4F819292ECBF7FBB");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 1, "captures:BAF465AE272FB31C86B4E7E4|boundDefenders:3116393A|xRays:04128908|quiet:7A4259182C21EA0F");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 2, "captures:BE3C48B58AFB4D6F92CEB65C|boundDefenders:FE5DBA94|xRays:BBB77BB5|quiet:EB2F021866716E41");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 3, "captures:0B4AA0E808913ADD68D04C90|boundDefenders:A0E0AAAC|xRays:8338C829|quiet:6ED425D3B49BFC37");
		test("5rk1/1prq2pp/p3p1bn/2bpP3/P2N1PP1/2BP4/1PP1BR1K/R4Q2 b - - 6 23", 4, "captures:3BFABA230EEFCFE528762833|boundDefenders:0EFB91BE|xRays:EA2B1406|quiet:856AC6B5E26C1A7F");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 1, "captures:19AF0A0EBAF465AE2CA302C4|boundDefenders:68454A1A|xRays:04128908|quiet:CF603DC02C722788");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 2, "captures:60E36FAB36D109EACBDB9D63|boundDefenders:125D201C|xRays:A3960553|quiet:D25F648A6F8BAC9C");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 3, "captures:71A37F69A95F748766F4AD72|boundDefenders:36BDEDD1|xRays:1177C109|quiet:B311D4355AD69EC5");
		test("1k4B1/3r1Qrp/3pp3/6P1/4P2n/5b2/6Q1/6K1 w - - 0 1", 4, "captures:2B24CF1DE721CA7C8691151C|boundDefenders:CD700912|xRays:167185E3|quiet:5BA36B5044B2DEFB");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 1, "captures:55A6D34FBAF465AEE27C00D4|boundDefenders:001CAD47|xRays:04128908|quiet:3D6A6AF9BAA3EF00");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 2, "captures:251975DBE9719EA062640E53|boundDefenders:7787BEFB|xRays:A3960553|quiet:C699F8BE036E53D5");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 3, "captures:E346C3084916165DDF97A3F0|boundDefenders:00E97743|xRays:2A54474B|quiet:7B84305574A47DE9");
		test("1k6/1q6/8/1rpb1Q2/8/2P5/P1KN2p1/8 w - - 0 1", 4, "captures:8E1B29720A2983BADC0F6ED6|boundDefenders:1287D4D1|xRays:1508F267|quiet:F69F58154C1763B5");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 1, "captures:B6E2968FBAF465AE0600B951|boundDefenders:49080F24|xRays:04128908|quiet:9E5531CCB56EB8F6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 2, "captures:44CB4D893A7A8EE2FFEDFDE9|boundDefenders:3B3BEF23|xRays:BBB77BB5|quiet:4F4CF55655FB63C6");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 3, "captures:0689B5F746D9EC52077C0259|boundDefenders:DB557355|xRays:6DCF47DF|quiet:5704152DF8D9EE63");
		test("6k1/6b1/8/rQ1R1q1R/8/8/1n6/2K1N3 w - - 0 1", 4, "captures:2B92EA2AB0063FD69D17411E|boundDefenders:AE308430|xRays:7AE94530|quiet:43A42F5C68EDD29F");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:8AAFFF859A63CB27");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 2, "captures:8AF9B677D0DDABC04A34291C|boundDefenders:9A8EBA81|xRays:BEF15E93|quiet:68E85107FD81510D");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 3, "captures:BCCF153872B87ED024827959|boundDefenders:F5E2627D|xRays:671027AD|quiet:7BA52C593CE775D4");
		test("4k3/2p1pp2/n4p1r/1p1P3p/3P4/1P1P3P/2N4R/1K6 w - - 0 1", 4, "captures:EBA9C4CB1ACC3EE63EEF7355|boundDefenders:F2341280|xRays:CB8D7845|quiet:EB18B86F1921306F");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 1, "captures:00CCB226BAF465AE1CD5C3BF|boundDefenders:0B875365|xRays:04128908|quiet:F5DDAE138516D6F9");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 2, "captures:31F049A3C9B0BA713B35BD95|boundDefenders:FA9FF6A2|xRays:49283716|quiet:EB5749ABC102A5E1");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 3, "captures:741950D0300924B4E683EEBB|boundDefenders:67553C3E|xRays:E94725E4|quiet:A07357E358521788");
		test("1k6/b7/1q6/6p1/1PPB3p/4q3/5B2/2K3Q1 w - - 0 1", 4, "captures:9C96800ABDF720E9B9BEC91B|boundDefenders:A4707FF5|xRays:D1333447|quiet:F98984C81A984D4D");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 1, "captures:6F4A213DBAF465AE326E360C|boundDefenders:14473372|xRays:04128908|quiet:1F2A5D2B79AD9A0B");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 2, "captures:B4BADF1F4718593DF15FAC7D|boundDefenders:C03CBFFE|xRays:AA11169A|quiet:7AF234BB4DD388BD");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 3, "captures:A6E61DCACF8C3A18C8CF62A3|boundDefenders:91393F5D|xRays:B1442EB7|quiet:031087FAABCBCF78");
		test("2r5/8/B2n1n2/4pkn1/8/2NK4/4P3/4R3 w - - 0 1", 4, "captures:F55C8079C6F894CB2073CBF9|boundDefenders:32E7D437|xRays:A1240666|quiet:282AEF433246E280");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 1, "captures:BAF465AEBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:38DFE6E0A378FC55");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 2, "captures:F5A0F415F5A0F415F5A0F415|boundDefenders:FBBAC38D|xRays:FBBAC38D|quiet:46674793A842756A");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 3, "captures:10B8E4F410B8E4F410B8E4F4|boundDefenders:975C6E7B|xRays:975C6E7B|quiet:478891DB6EDF9284");
		test("8/5k2/8/3K4/8/8/8/8 w - - 0 1", 4, "captures:5DD3E2B45DD3E2B45DD3E2B4|boundDefenders:9FA53724|xRays:9FA53724|quiet:7C1E63CB998448ED");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 1, "captures:BAF465AEF1B75E2D6C5BFBAF|boundDefenders:14B0A656|xRays:04128908|quiet:676C5D51FCF24A9E");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 2, "captures:F5293CEDD0C3B6DAF65BED14|boundDefenders:309716DA|xRays:BBB77BB5|quiet:4D7EBC2E9A480E3B");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 3, "captures:CA43A64B5BCDE9A63CEF16C5|boundDefenders:248BB1DF|xRays:54ECD1AD|quiet:47F23B089849F093");
		test("r1bqk2r/pp1n1ppp/2pbpn2/3p4/2PP4/2NBPN2/PPQ2PPP/R1B1K2R b KQkq - 4 7", 4, "captures:FC360F6DDCC014953BFB5AD2|boundDefenders:707ACF52|xRays:57DB1B92|quiet:2A62B51B439DCF92");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 1, "captures:6B2D1C3A7F2BA012E64D9D00|boundDefenders:F06F06FE|xRays:04128908|quiet:46584BB7EA071758");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 2, "captures:EC4F13C9EF632D084909B2B1|boundDefenders:D6C06A4F|xRays:585684C3|quiet:86101FE4E638FCBA");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 3, "captures:9397D723C533833FE6212A12|boundDefenders:CE011C7B|xRays:9A664BCB|quiet:B2C221228ADC244B");
		test("6k1/p4p2/1p4n1/2pP2Q1/2P2N1p/3b3P/Pq3P2/5BK1 b - - 4 38", 4, "captures:806C9A8EA913FBA3A3D83768|boundDefenders:96D145FC|xRays:290DCE2F|quiet:83D61973904C96A4");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 1, "captures:659DB3B3272FB31C0CEF85DE|boundDefenders:5E11123E|xRays:04128908|quiet:CFB6722BDC7FFAA8");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 2, "captures:7764FC5DC8A9DB255BCE686A|boundDefenders:0103C1F6|xRays:1E224F84|quiet:4A058343FBC83C36");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 3, "captures:E925858F5A32C5B7690898FA|boundDefenders:D8D5B2AD|xRays:8B4743BE|quiet:A19789F645B4AED5");
		test("2b5/rp1n1kpp/p2B1b2/8/3N4/3Q4/6PP/2R4K w - - 4 31", 4, "captures:79775ED2F00CCDD73979DCA0|boundDefenders:A283D29F|xRays:AF9FBDC7|quiet:271D2F5DBEC65E0A");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 1, "captures:BAF465AEBAF465AE9E58F779|boundDefenders:AF45E1AA|xRays:04128908|quiet:208C902E09C2F3C8");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 2, "captures:3384290D2436DCC03654AF9B|boundDefenders:1FE8FACC|xRays:044D19C2|quiet:97A9442F52391A2E");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 3, "captures:1CD0A3DD83848D42008A67D1|boundDefenders:6D555735|xRays:C4A4A5A6|quiet:BB3B23C7BDDFDE24");
		test("2r1k2r/1b2pp1p/p2p1np1/4n3/1q1NP3/2N2P2/1PP1B1PP/2QRK2R b Kk - 1 17", 4, "captures:2E10C1CBEE153BA0A11756CA|boundDefenders:3DB0C62E|xRays:0E348D1B|quiet:BC1563AB3C88D66A");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 1, "captures:326E360CBAF465AE280CB279|boundDefenders:353C5033|xRays:04128908|quiet:F8F4031D8A6B35CF");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 2, "captures:4BDAF1796D368DAB11ED96BA|boundDefenders:C37E5C34|xRays:611E3E6C|quiet:72A6A25D6CE92D26");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 3, "captures:2CBD461558A522D3C2CA7AC8|boundDefenders:21043EF5|xRays:5D7A337D|quiet:20A72A016A0AA344");
		test("r5k1/5pp1/2p1b1qp/P7/2pPN3/PrQ5/5PPP/2R1R1K1 w - - 3 29", 4, "captures:FC35F52E4EDB46D9A404524B|boundDefenders:8CFCE2BD|xRays:A467B25D|quiet:16F3AF81DAEC77B7");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 1, "captures:F8647D03BAF465AE5A866710|boundDefenders:13AFD3D4|xRays:04128908|quiet:D7B6EE1284B9C4DE");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 2, "captures:07E59A3818FFCAFC52C2F72B|boundDefenders:63C5CA85|xRays:9A8017CC|quiet:CFEA719BF5738D4F");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 3, "captures:04F0C87EFAE355BF707DAA33|boundDefenders:3AD8F5E2|xRays:37DDE841|quiet:68202DE75875333B");
		test("r4rk1/pb2bpp1/7p/q1pnB3/Np5P/1P4P1/2Q2PB1/3R1RK1 b - - 0 20", 4, "captures:52E9A29AD5CF672791B1DEC3|boundDefenders:880D8084|xRays:4DE93A43|quiet:7259535E334D6F88");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 1, "captures:BAF465AEAEF3FA6FEE432D04|boundDefenders:D61EC5A0|xRays:04128908|quiet:D2733B30B9D9F621");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 2, "captures:AC76D8B368DE7EFF36CC8AC0|boundDefenders:0C77F43D|xRays:9D4BB32A|quiet:D544A6B12CAC87B1");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 3, "captures:BAA6AF7022D9D2F3D3A22023|boundDefenders:1C1FAACB|xRays:F00440E0|quiet:2EAE5D5D012558E2");
		test("3r1r2/p4pkp/2npbqp1/2p1n3/PpP1P3/1P3N1P/2BNQPP1/3R1RK1 b - - 6 17", 4, "captures:30EBAC8D4A8F8223FF6FF6C1|boundDefenders:4461F255|xRays:A8ED0E2D|quiet:D36DA20139AAC24F");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 1, "captures:5C76262A34D38858CEC8531E|boundDefenders:D4809902|xRays:04128908|quiet:0F7BF457E67595EB");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 2, "captures:2F2311172CC643EA12D6CEAC|boundDefenders:E8D2B43C|xRays:B9DC87A8|quiet:DB7C722801C2B037");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 3, "captures:EA6C8B51426280BD7A342621|boundDefenders:6C25ACC8|xRays:C74B0EAC|quiet:75DAB6CD086AD467");
		test("1Q3r1k/p6p/3p1pp1/2pP1N2/2p5/1P1n4/P4P1P/4R1RK b - - 0 27", 4, "captures:84B675EB27FE5CEEDAD07859|boundDefenders:D42F3BA2|xRays:6DE48B03|quiet:530CC09CD84952D8");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 1, "captures:7809854ADAD8D0633EA1D491|boundDefenders:92D61E92|xRays:04128908|quiet:E4FB9AC8DC29672C");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 2, "captures:6CE63F323F6285B1AFAEB3F3|boundDefenders:ED8A7B81|xRays:431529DC|quiet:D5D95E027156B2C3");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 3, "captures:FE4EF17734282877210203A7|boundDefenders:18277BDA|xRays:ADD6BE80|quiet:93BC03F44DB12D23");
		test("r2r4/8/1pk1pbp1/1pp2n2/5N1p/P1P3P1/1PN1K2P/R1B5 w - - 0 29", 4, "captures:B8A6A811EB58C26AD832D514|boundDefenders:F296EEF2|xRays:34100837|quiet:FA8CA27B8000BDC0");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 1, "captures:614DC4E9BAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:3D3B4272E8F51237");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 2, "captures:AA34F60C8D84135D0444E8E3|boundDefenders:9A960B45|xRays:8DC28F29|quiet:D4A01A1736284215");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 3, "captures:32D3352484F72F1D22C9F024|boundDefenders:CEC2CE02|xRays:FC36F61D|quiet:8DF2A83BF5227811");
		test("5rk1/p4pbp/8/8/P7/1P1r1BP1/5P2/2R2RK1 w - - 1 23", 4, "captures:B7E7881113EA6B45B4DA3802|boundDefenders:3CEFAFEC|xRays:C67685BC|quiet:8B6A77F57212A86B");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 1, "captures:E1F99596BAF465AE89704488|boundDefenders:B8166CA2|xRays:04128908|quiet:2ABC4C7D289D0F77");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 2, "captures:E8AC95205F7CC5781DEC54CD|boundDefenders:512FE5F8|xRays:1B966466|quiet:1B947447499FFB2D");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 3, "captures:ACC940A6B8521EA12D0ED2F4|boundDefenders:3EAD8420|xRays:B537F79C|quiet:D17D3AC0D41D9441");
		test("r1b2rk1/5ppp/2p1p3/1p6/pqnP4/3Q2R1/P1B2PPP/2K3NR b - - 5 21", 4, "captures:69273A9387EB0745AD751C88|boundDefenders:6FBC84B9|xRays:BAAA4A57|quiet:B9302AA6EEA6654E");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 1, "captures:19516AC19C98B7721B8C830E|boundDefenders:40A3FF2B|xRays:04128908|quiet:33631577E08771D8");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 2, "captures:96F057C5589F7BD63D6D2968|boundDefenders:64EECC24|xRays:D04EC558|quiet:C3126456A54C6351");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 3, "captures:08EE665F0A84A9DBC0EC0D37|boundDefenders:C735C2AC|xRays:26DD1EC3|quiet:F86D66768A49EAA6");
		test("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", 4, "captures:36BCE16340F5B9CE9545F1F5|boundDefenders:725130AD|xRays:B04CFD44|quiet:34E6062C6B74D053");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 1, "captures:2110577767EC532CE1DB1EE9|boundDefenders:DB47E532|xRays:04128908|quiet:9CB5D1449D05CEFF");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 2, "captures:36A851FFA22851F19BB811CC|boundDefenders:115C05CC|xRays:7E034814|quiet:EC9B84E31121582F");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 3, "captures:8ECEE7F64F4382F03075A33C|boundDefenders:FCDA19B4|xRays:812D3DF3|quiet:107BB36D35CC44D2");
		test("4r1k1/p3Bp2/1p6/2n1R1pb/5p2/1BP5/PP6/6K1 b - - 3 32", 4, "captures:1B4B94B50E14FD03C49F5091|boundDefenders:BED6D1E3|xRays:99C799CC|quiet:43D087DDE28C8119");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 1, "captures:BAF465AE27B695A55D5A8BD5|boundDefenders:13DE8150|xRays:04128908|quiet:CE441F06B0D492AC");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 2, "captures:DB2A52507A2939FBAB112FC7|boundDefenders:19A9F8BF|xRays:DA865B0D|quiet:3101A9B9BBD27CE0");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 3, "captures:D707357B7333D601C26E4B9F|boundDefenders:6CDF17F8|xRays:BF3FBDF7|quiet:EAB14C1170553AFB");
		test("r2krb2/ppq5/2p5/5Q2/3PR3/2P1N2P/PP3PP1/R5K1 w - - 3 29", 4, "captures:BEAB781488AC2C9E49303D93|boundDefenders:1FE676EE|xRays:2366874F|quiet:C0E44360A8236B72");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 1, "captures:8A97D25E0F1E64DF51D58AE6|boundDefenders:DD94E6F9|xRays:04128908|quiet:B61C086023E05D44");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 2, "captures:8926656F78CD8F2DBDB3308F|boundDefenders:B7214688|xRays:044D19C2|quiet:4063C73D6F3A05D2");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 3, "captures:CF5D5F4CF1EDC61E112121C2|boundDefenders:8CB80FA8|xRays:5EC8B5A9|quiet:6D9BB0B008947589");
		test("3r1rk1/1bpq1pp1/5bnp/1p1n4/pN4N1/P1B3P1/1P1QRPBP/4R1K1 w - - 0 1", 4, "captures:17379A41E264F066234FDDCC|boundDefenders:053D2973|xRays:8CA9956D|quiet:D88CC70C75ACBA15");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 1, "captures:ABA1AA0F4DEC4901A86AFBA4|boundDefenders:4DA102DC|xRays:04128908|quiet:F7E346C61F5F75DF");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 2, "captures:F57711A1055E36D66524B74D|boundDefenders:0F3F272F|xRays:B8D77D2A|quiet:82FF3412F036C828");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 3, "captures:DF99C82162CAA25C8FC62D12|boundDefenders:82AFAFB6|xRays:87B7BF96|quiet:0B3CD33FF14AD5DD");
		test("2k5/1bnpp3/2q2n1p/6rr/PP6/NB2RN2/1B1R1KPP/4Q3 w - - 0 1", 4, "captures:A4C2D47F91DF14CDBD0BA29D|boundDefenders:F62E0299|xRays:5EE02C98|quiet:02DE44D76F874A6B");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 1, "captures:9D5AE446BAF465AE4DB454A0|boundDefenders:E7D4174C|xRays:04128908|quiet:854BDCDABC30C2FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 2, "captures:02D216AA2370A84661BDDA16|boundDefenders:C9C0F5AD|xRays:92E7D2DB|quiet:8E932183DA48186E");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 3, "captures:5D2CFD27F4DDEC43DB9142DA|boundDefenders:69477D37|xRays:3C0B9D8B|quiet:8C7571C7840AF7FD");
		test("2r1r1k1/1QP2p1p/p3q1p1/3R1p2/8/1P4P1/P3P2P/2R3K1 w - - 1 35", 4, "captures:873718F2BDA06152164A0CD8|boundDefenders:2B086878|xRays:347FB8DC|quiet:A8972D5CB066789E");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 1, "captures:26BD0B8B7E306BB01090BBF9|boundDefenders:B308EE50|xRays:04128908|quiet:87DAC40295C5584A");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 2, "captures:AA67D058BDF88448BDF94E60|boundDefenders:C26A416B|xRays:483ED70A|quiet:7819F67A2A913AB1");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 3, "captures:425E9AC1B5DF96EC27AACCA9|boundDefenders:A6DA5731|xRays:649CD007|quiet:860C2632004C0F2D");
		test("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", 4, "captures:E5D43E4D9D187281CC4EE876|boundDefenders:8BF51FC4|xRays:A2EE625F|quiet:4450A4DC5EAE07CB");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 1, "captures:BAF465AE4DEC49014B385DF9|boundDefenders:36051C48|xRays:04128908|quiet:EDC8CAA75C6B7CE2");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 2, "captures:1FCA380508562D73EF0D99C7|boundDefenders:F0FC9A19|xRays:37628A9D|quiet:E796CEC15C061F76");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 3, "captures:809CBD83AA274E5F7BE163F2|boundDefenders:6707C096|xRays:49E2B4A1|quiet:7D7FF609F954CC0A");
		test("r2q1rk1/1pp2p2/p1npbn2/2b1p1B1/4P1P1/2NP3P/PPP2PB1/R2QK2R b KQ - 0 12", 4, "captures:4AB41F96628DFAB9B74CE3A9|boundDefenders:9765C3F0|xRays:C6BB362A|quiet:BDEE3464469CF7C0");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 1, "captures:2F44804FBAF465AE35AB3ADD|boundDefenders:0BE9CA79|xRays:04128908|quiet:E2D83420C072E82D");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 2, "captures:D9044B8436A6579AFF55387A|boundDefenders:65F62826|xRays:611E3E6C|quiet:A9ED540B0BE202AE");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 3, "captures:1565AA7C88F16B6919C4FD7D|boundDefenders:CDC6662B|xRays:1012EEB2|quiet:6DA84022155F0125");
		test("r3r1k1/p2q1ppp/bp1p1B2/2pP1n2/2P1pQ2/4P3/PP1N2PP/1BR1R1K1 b - - 0 20", 4, "captures:860A51B15B574F1DF222E6DC|boundDefenders:1BCA12DE|xRays:280A9320|quiet:9F11134E8582C66C");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 1, "captures:BAF465AE5F98F8CB02DEB84C|boundDefenders:9E15B817|xRays:04128908|quiet:1AC1F93B4BD01D08");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 2, "captures:68721D2DC4049335E9A23D37|boundDefenders:EB8ACA4A|xRays:37628A9D|quiet:C45D8C1812BFE4F9");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 3, "captures:E87CAAEFE1D8B701FB8FA375|boundDefenders:27918DD7|xRays:73618B79|quiet:72C139AE116216AA");
		test("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", 4, "captures:03259707B5E007AEA01804CF|boundDefenders:B8631CC7|xRays:15575447|quiet:04A8C89A7D798F10");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 1, "captures:0F08EEADBAF465AE25DC18F1|boundDefenders:40EF4BC4|xRays:04128908|quiet:81A64F304BF1B43B");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 2, "captures:AE3D0F30391BD073F8EA2AA2|boundDefenders:9CC684C3|xRays:1B966466|quiet:3EF7B5487F0FBCC7");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 3, "captures:01F1455BA8BBDD55307A896E|boundDefenders:BB403C68|xRays:B1805255|quiet:979ADA35E95018FF");
		test("5r1k/3Rb1rp/2p1p3/2P1P3/pP2Q3/q3B2P/5P1K/4R3 b - - 2 34", 4, "captures:C39E41EEB9A5BCC313114C36|boundDefenders:53034AA6|xRays:06A364D6|quiet:3DC66E2705F236F6");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 1, "captures:BAF465AEDBC3BD44010054D0|boundDefenders:F00405A0|xRays:04128908|quiet:8CF49FB828285C4C");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 2, "captures:2D8517759A8CDBB0FA6D8FF3|boundDefenders:53906A83|xRays:37628A9D|quiet:DDF844135D44A464");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 3, "captures:5AFC2A0254F5D971A99E0FE8|boundDefenders:A1F919D8|xRays:F7C7F997|quiet:0E13190C72529D06");
		test("r4rk1/3q2pp/2n2p2/3p4/2pP3P/2P1N3/2Q2P1P/R3R1K1 b - - 0 24", 4, "captures:11010AA4F0C229E6E3488B73|boundDefenders:EDBB56D4|xRays:403FFE2C|quiet:6BA68C00775E0D53");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 1, "captures:E6FA81A6BAF465AEC5B62789|boundDefenders:9C9232DB|xRays:04128908|quiet:4064274600B0F09D");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 2, "captures:312346FACA60E58E53FC7CE1|boundDefenders:2E6A0BEA|xRays:585684C3|quiet:9F77B613D9B7081F");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 3, "captures:D0648F5D1139550DF7F69A98|boundDefenders:38F085F8|xRays:D2BD26A2|quiet:967E4FE3C314493B");
		test("r3r3/1p1kb1pp/4R3/3pR3/n1pP4/PpP3B1/1P3PPP/3N2K1 b - - 2 27", 4, "captures:99CCB165CA05B9FBF628EAFF|boundDefenders:A24B8F53|xRays:490ECD44|quiet:02E48A8EC258BE1F");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 1, "captures:BAF465AEBAF465AEC382B402|boundDefenders:81B3FB8F|xRays:04128908|quiet:9D2E44F854D02CC6");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 2, "captures:AC6A3C35F75B98D81B4B885F|boundDefenders:B67597AA|xRays:37628A9D|quiet:6D0FB74DF0EEB816");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 3, "captures:72605A7435CCA9BD97B1C424|boundDefenders:6AD637A3|xRays:CD73C6D5|quiet:27439F03E1BF0649");
		test("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", 4, "captures:9E16FE631ADCC632D0F21F76|boundDefenders:FCF295CB|xRays:5EA2C44A|quiet:4C900E88FECE855B");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 1, "captures:BAF465AE4215481002E9ADBE|boundDefenders:7C248ACE|xRays:04128908|quiet:B8D7852627FD64D2");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 2, "captures:C125718CC784389E2A431AA3|boundDefenders:4CDDA21D|xRays:AA11169A|quiet:046511D445F46DAB");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 3, "captures:55F9EDBE7F07FC42186E661F|boundDefenders:2E86A359|xRays:107AAD8D|quiet:91667916F04BCA93");
		test("r1bq1r1k/p3bn1p/2pp1p1N/2p1p2P/2P5/2NP4/PP3PP1/R1BQR1K1 b - - 6 16", 4, "captures:D8E4C36CF54C5B76CF8A98F8|boundDefenders:9559EECC|xRays:E752DBA6|quiet:DF50E0D2FFFDDD80");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 1, "captures:7F404D41F1107742DC67BF7A|boundDefenders:6994A430|xRays:04128908|quiet:462792B29E44F5C0");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 2, "captures:F043193AF12D6CA0F774D2FA|boundDefenders:4BDD4407|xRays:7C298C23|quiet:CB2E787B7B096BB6");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 3, "captures:EE7995AF7402812B960AAF52|boundDefenders:7CC292D9|xRays:65B4911F|quiet:9AE8B2339A52021C");
		test("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", 4, "captures:7BD7E6EEB964D9BAA6A85961|boundDefenders:7C125E8D|xRays:4E21CCFA|quiet:4F76370360AC2174");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 1, "captures:CE8A475EBAF465AEBAF465AE|boundDefenders:04128908|xRays:04128908|quiet:5A7113340871FBA4");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 2, "captures:0A51E0384217AE37B1C106C2|boundDefenders:2E0B53CD|xRays:8DC28F29|quiet:5C6F6A9A5029F472");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 3, "captures:12DA9FAFC9C23E0995491926|boundDefenders:CC82B734|xRays:B98E90D3|quiet:5374CBDC9BCF87A5");
		test("8/5kb1/3q3p/p2p1PpP/1PpP4/5P2/PP6/1KN1Q3 b - - 0 35", 4, "captures:69C234C470A3ECD7F76E8CB8|boundDefenders:F822C87E|xRays:4F5CCFE8|quiet:71AE38D3B3BE8006");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 1, "captures:D2205151BAF465AEEBFD415B|boundDefenders:272521FD|xRays:04128908|quiet:5F1F2CFD480BAE12");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 2, "captures:0DB300AACAD93467DFF3E775|boundDefenders:4815D627|xRays:9D4BB32A|quiet:ABA6976A50DA8669");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 3, "captures:CCC2B05817B1C18AB8D46EBB|boundDefenders:0AB04D7C|xRays:908D7DD9|quiet:D03438A60E33C862");
		test("5r1k/6b1/pp2R1pp/3pP3/3P2Nq/5r1P/PP2QPK1/7R b - - 1 29", 4, "captures:921F62D2707FFF236950E00A|boundDefenders:3F29BBE0|xRays:7F2FCB8B|quiet:2D09BB3FEF42A26D");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 1, "captures:F3146E83BAF465AEC4ECC380|boundDefenders:01FC95AB|xRays:04128908|quiet:191DB1B19FF9D874");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 2, "captures:A45EDAB6980377F8B3F57258|boundDefenders:59D11839|xRays:9F0882E0|quiet:A9AFCB475E31C2B1");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 3, "captures:FC2A29D68CF6F4E9036FA789|boundDefenders:CEDE142D|xRays:12F0328E|quiet:6DFB2E66401A8A19");
		test("5rk1/5ppp/5b2/3P4/8/Q2p1P2/P3qP1P/3R1RK1 b - - 1 22", 4, "captures:EA7DCE093D3BA7ACCE4BBE4B|boundDefenders:D9B86703|xRays:04151220|quiet:90238DAD45C637B6");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 1, "captures:5ADD0E93BAF465AEE8AE22C7|boundDefenders:8C09F4C6|xRays:04128908|quiet:AE3BAE3FD48A99C2");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 2, "captures:AEAB961CC37C147A26E1FC35|boundDefenders:63064C42|xRays:976306B1|quiet:4261CC243B66F73D");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 3, "captures:F7E0072879CE6C3025F1258F|boundDefenders:6A9DB271|xRays:D4070808|quiet:6BC6C8C659DBBEA8");
		test("2rqrbk1/5pp1/p2p3p/np6/3pN3/P4P1P/BP1Q1P2/R1B3K1 b - - 2 23", 4, "captures:80D9D8BCFC5EB2F4134FEA08|boundDefenders:51D330E0|xRays:6DBE210B|quiet:DB8266A1D56ACEBA");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 1, "captures:BAF465AEBAF465AED076DB90|boundDefenders:04128908|xRays:04128908|quiet:7949726B839C9E31");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 2, "captures:73E915756C34E326CA4B0BF9|boundDefenders:4F830163|xRays:A3960553|quiet:BA0CCBD98C903EDC");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 3, "captures:B77B2DFF89D8A36F1E800DD4|boundDefenders:D509CFCE|xRays:90AE01E7|quiet:2016189378AEAA2F");
		test("rnbq1rk1/2p3b1/pp1p2pp/3Ppn2/2P5/2N4P/PP1BBPPN/R2Q1RK1 b - - 1 13", 4, "captures:D4DB519B8D00BD41E226E763|boundDefenders:B26FA1A8|xRays:504E6A52|quiet:8FDA0F022B1F3FCD");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 1, "captures:BAF465AEBAF465AE7057DFB7|boundDefenders:95B2F1AF|xRays:04128908|quiet:C0F612DB7F722F35");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 2, "captures:68783DE117E346F5F7C4036D|boundDefenders:0D18252F|xRays:BBB77BB5|quiet:ABE5FBB7B8898BAB");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 3, "captures:F01877F159E72717FC6D6F76|boundDefenders:23508DF4|xRays:08334A05|quiet:04826DB14686DF7D");
		test("2rq1rk1/1p1bppbp/p2p1np1/n7/3NP3/P1N1BP2/1PPQB1PP/1K1R3R w - - 5 13", 4, "captures:265A0FD3B6A98B91081B15F3|boundDefenders:DE10063E|xRays:FD9505C8|quiet:8B43E19D9D5A99AE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 1, "captures:BAF465AE1F3CF44BAE0F7F01|boundDefenders:7C64D6BC|xRays:04128908|quiet:D665AF42A9605905");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 2, "captures:E3B0A3BF8B51DED60B485265|boundDefenders:B42CAB8A|xRays:0C5929F0|quiet:2E4388A214BFE1BF");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 3, "captures:FC47B489FDA5D1CF3935BB8F|boundDefenders:98D88104|xRays:E1BFB99D|quiet:8723CCBB7F0FADCE");
		test("r3r1k1/1ppq1p1p/p1n2bp1/3p1b2/3P4/BBP2N1P/P2Q1PPK/4RR2 b - - 3 18", 4, "captures:B725AC69845D835463DA6DFE|boundDefenders:61ABEFC8|xRays:012C0A35|quiet:FB920040EB61C3E5");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 1, "captures:BAF465AE4DEC4901AA33CBE6|boundDefenders:04128908|xRays:04128908|quiet:A28CA685AE70835E");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 2, "captures:8A24B9CBCDF897F651006AFA|boundDefenders:5FB05F5E|xRays:044D19C2|quiet:4B2BD6615BB61918");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 3, "captures:D88628375A020F869F87F8D1|boundDefenders:57E32FC9|xRays:69084716|quiet:602E1866CCF074BD");
		test("r2q1rk1/p4ppp/2p1pn2/2bp4/8/1P1QPN2/PB3PPP/R3K2R w KQ - 0 14", 4, "captures:0F96CD712B26C4F45EA72A2E|boundDefenders:3C5B0066|xRays:A58806E0|quiet:2B7BC69A45918AFD");

//		test("", 1, "");
//		test("", 2, "");
//		test("", 3, "");
//		test("", 4, "");
		
		if(SKIP_ASSERTIONS)
			fail(">>>skipped all asserions! This is intentional ONLY IF we are gathering correct values for new testcases.");
		if(DEPTH_LIMIT != -1)
			fail(">>>passed all executed test cases, but the depth limit was set to: " + DEPTH_LIMIT);
		
	}

}
