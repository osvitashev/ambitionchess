package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class BSEETest_debugUtil {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	@Test
	void test() {
		
		test_game.loadFromFEN("r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27");
		test_eval.initialize();
		test_eval.evaluateCaptures();
		test_eval.evaluateBoundDefenders();
		test_eval.evaluateQuietMoves();
		
		String expected = "r3r1k1/5pbp/p5p1/P1n1p3/1qB5/4Bb1P/1P2QPP1/R3R1K1 w - - 0 27\n"
				+ "direct attacks:\n"
				+ "WHITE: 0x20934F6DFFFFFF\n"
				+ "	wP = 0x20040F50000\n"
				+ "	wR = 0x10101117F\n"
				+ "	wB = 0x20914E280A3904\n"
				+ "	wQ = 0x04382E38\n"
				+ "	wK = 0x0000E0A0\n"
				+ "BLACK: 0xFFFBF7BF7D0F5A10\n"
				+ "	bP = 0x50A228000000\n"
				+ "	bR = 0x7F11111000000000\n"
				+ "	bN = 0xA1100110A0000\n"
				+ "	bB = 0xA102A49850005000\n"
				+ "	bQ = 0x202020705070A10\n"
				+ "	bK = 0xA0E0000000000000\n"
				+ "secondary attacks:\n"
				+ "WHITE: 0x40010392501041E0\n"
				+ "	wR = 0x100001000E0\n"
				+ "	wB = 0x4001020000000060\n"
				+ "	wQ = 0x19250004100\n"
				+ "BLACK: 0xE0100801F814028A\n"
				+ "	bR = 0xE000000110100000\n"
				+ "	bB = 0x08040288\n"
				+ "	bQ = 0x20100800F8000002\n"
				+ "secondary batteries:\n"
				+ "WHITE: 0x18240100060\n"
				+ "	wR = 0x00100060\n"
				+ "	wB = 0x00000020\n"
				+ "	wQ = 0x18240000000\n"
				+ "BLACK: 0x6000000008000008\n"
				+ "	bR = 0x6000000000000000\n"
				+ "	bB = 0x08000008\n"
				+ "Static Exchange Evaluation:\n"
				+ "WHITE processed exchange targets: 0x20914EE96B8DAE\n"
				+ "	capture winning wP: 0x00200000\n"
				+ "	capture winning wQ: 0x00200000\n"
				+ "	capture neutral wB: 0x400000000\n"
				+ "	capture losing wB: 0x20010000000000\n"
				+ "	quiet neutral wP: 0xC0400000\n"
				+ "	quiet neutral wR: 0x0001012E\n"
				+ "	quiet neutral wB: 0x4000080904\n"
				+ "	quiet neutral wQ: 0x00000C20\n"
				+ "	quiet neutral wK: 0x000080A0\n"
				+ "	quiet losing wP: 0x00020000\n"
				+ "	quiet losing wR: 0x01000000\n"
				+ "	quiet losing wB: 0x900A28020000\n"
				+ "	quiet losing wQ: 0x00080008\n"
				+ "BLACK processed exchange targets: 0xAE1BB6EB550F5A10\n"
				+ "	capture winning bB: 0x00001000\n"
				+ "	capture losing bB: 0x00004000\n"
				+ "	capture losing bQ: 0x104000210\n"
				+ "	quiet neutral bP: 0xA0A010000000\n"
				+ "	quiet neutral bR: 0x2E11000000000000\n"
				+ "	quiet neutral bN: 0xA100011020000\n"
				+ "	quiet neutral bB: 0xA002248010000000\n"
				+ "	quiet neutral bQ: 0x202000000000000\n"
				+ "	quiet neutral bK: 0xA000000000000000\n"
				+ "	quiet losing bP: 0x4000000000\n"
				+ "	quiet losing bR: 0x100000000000\n"
				+ "	quiet losing bN: 0x00080000\n"
				+ "	quiet losing bB: 0x800840000000\n"
				+ "	quiet losing bQ: 0x20201070800\n"
				+ "Interactions:\n"
				+ "To b2\n"
				+ "{e2 guards (bound to) b2 (score: negative->positive)} \n"
				+ "To g2\n"
				+ "{g1 guards (bound to) g2 (score: negative->positive)} \n"
				+ "To c4\n"
				+ "{e2 guards (bound to) c4 (score: negative->positive)} \n"
				+ "To a5\n"
				+ "{a1 guards (bound to) a5 (score: negative->positive)} \n"
				+ "To c5\n"
				+ "{b4 guards (bound to) c5 (score: neutral->positive)} \n"
				+ "To a6\n"
				+ "{c5 guards (bound to) a6 (score: negative->positive)} {a8 guards (bound to) a6 (score: negative->positive)} \n"
				+ "To f7\n"
				+ "{g8 guards (bound to) f7 (score: negative->positive)} \n";
		assertEquals(expected, test_eval.debug_getAllOutputs());
		//System.out.println(test_eval.debug_getAllOutputs());
		//fail("Add the actual assetion, or just use this as a form of ducumentation...");
	}

}
