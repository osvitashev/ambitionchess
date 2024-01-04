package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class BSEETest_debugUtil {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void test() {
		
		test_game.loadFromFEN("2k5/3B1br1/2P3q1/3n3N/6P1/1Nn4b/1K6/3rb1R1 b - - 0 1");
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
		test_eval.evaluateQuietMoves();
		test_eval.evaluateXRayInteractions();
		
		String expected = "2k5/3B1br1/2P3q1/3n3N/6P1/1Nn4b/1K6/3rb1R1 b - - 0 1\n"
				+ "direct attacks:\n"
				+ "WHITE: 0x144A34A568474DB7\n"
				+ "	wP = 0xA00A000000000\n"
				+ "	wR = 0x404040B0\n"
				+ "	wN = 0x40200528400805\n"
				+ "	wB = 0x1400142040000000\n"
				+ "	wK = 0x00070507\n"
				+ "BLACK: 0x5AFEFEEAFB5C7D3F\n"
				+ "	bR = 0x40A0400808080817\n"
				+ "	bN = 0x14220A3314110A\n"
				+ "	bB = 0x50005008C0446820\n"
				+ "	bQ = 0xE0BCE050080402\n"
				+ "	bK = 0xA0E000000000000\n"
				+ "secondary attacks:\n"
				+ "WHITE: 0x404201800008\n"
				+ "	wR = 0x404000000008\n"
				+ "	wB = 0x201800000\n"
				+ "BLACK: 0x50181BE146424060\n"
				+ "	bR = 0x18084040000060\n"
				+ "	bB = 0x810A106020000\n"
				+ "	bQ = 0x5000030000404040\n"
				+ "secondary batteries:\n"
				+ "WHITE: 0x00000000\n"
				+ "BLACK: 0x500000E040000000\n"
				+ "	bR = 0x4040000000\n"
				+ "	bB = 0xA000000000\n"
				+ "	bQ = 0x5000000000000000\n"
				+ "Static Exchange Evaluation:\n"
				+ "WHITE processed exchange targets: 0x1444306528454DB7\n"
				+ "	capture winning wN: 0x40000000000000\n"
				+ "	capture winning wB: 0x400000000000000\n"
				+ "	capture losing wR: 0x00000010\n"
				+ "	capture losing wK: 0x00040000\n"
				+ "	quiet neutral wP: 0x4000000000\n"
				+ "	quiet neutral wR: 0x00000080\n"
				+ "	quiet neutral wN: 0x500400005\n"
				+ "	quiet neutral wB: 0x2000000000\n"
				+ "	quiet neutral wK: 0x00010000\n"
				+ "	quiet losing wP: 0x4000000000000\n"
				+ "	quiet losing wR: 0x00404020\n"
				+ "	quiet losing wN: 0x200028000800\n"
				+ "	quiet losing wB: 0x1000100000000000\n"
				+ "	quiet losing wK: 0x00000507\n"
				+ "BLACK processed exchange targets: 0x5A9EBEE2FB587D27\n"
				+ "	capture losing bB: 0x40000000\n"
				+ "	capture losing bQ: 0x48040000000\n"
				+ "	capture losing bK: 0x8000000000000\n"
				+ "	quiet neutral bR: 0x4080000000080002\n"
				+ "	quiet neutral bN: 0x14220213101002\n"
				+ "	quiet neutral bB: 0x5000100080002800\n"
				+ "	quiet neutral bQ: 0x80884010080002\n"
				+ "	quiet neutral bK: 0xA04000000000000\n"
				+ "	quiet losing bR: 0x08000805\n"
				+ "	quiet losing bN: 0x20000100\n"
				+ "	quiet losing bB: 0x00404020\n"
				+ "	quiet losing bQ: 0x302000000400\n"
				+ "	quiet losing bK: 0x2000000000000\n"
				+ "<><> Defender Interactions: <><>\n"
				+ "To: e1\n"
				+ "{d1 guards e1 (- to +)} \n"
				+ "To: g4\n"
				+ "{d7 guards g4 (- to +)} {g1 guards g4 (- to +)} \n"
				+ "To: h5\n"
				+ "{g4 guards h5 (- to +)} \n"
				+ "To: c6\n"
				+ "{d7 guards c6 (- to +)} \n"
				+ "To: d7\n"
				+ "{c6 guards d7 (- to +)} \n"
				+ "<><> X-Ray Interactions: <><>\n"
				+ "To: d1\n"
				+ "{g1 pins e1 to d1 (0)} \n"
				+ "To: g1\n"
				+ "{d1 via e1 threatens g1 (+)} {g6 pins g4 to g1 (+)} \n"
				+ "To: b3\n"
				+ "{f7 via d5 threatens b3 (0)} \n"
				+ "To: h3\n"
				+ "{d7 via g4 threatens h3 (+)} \n"
				+ "To: h5\n"
				+ "{f7 via g6 threatens h5 (0)} \n"
				+ "To: g6\n"
				+ "{g1 via g4 threatens g6 (+)} \n"
				+ "To: d7\n"
				+ "{h3 pins g4 to d7 (+)} \n";
		
		//System.out.println(test_eval.debug_getAllOutputs());
		assertEquals(expected, test_eval.debug_getAllOutputs());
		//fail("Add the actual assetion, or just use this as a form of ducumentation...");
	}

}
