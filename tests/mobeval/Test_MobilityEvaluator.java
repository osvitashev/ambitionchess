package mobeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import basicseeval.BroadStaticExchangeEvaluator;
import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;

class Test_MobilityEvaluator {
	
	private Gamestate game = new Gamestate();
	private BroadStaticExchangeEvaluator seeval = new BroadStaticExchangeEvaluator(game);
	private MobilityEvaluator meval = new MobilityEvaluator(game, seeval);
	
	@Test
	void testBlockadedPawns() {
		game.loadFromFEN("8/pP1p1p2/p2k2pK/5ppP/1n1p1P2/1P4p1/PP1P2P1/8 w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals("{b2 g2 f4 }", Bitboard.toListString(meval.get_output_blockadedPawns(Player.WHITE)));
		assertEquals("{g3 f5 g6 a7 }", Bitboard.toListString(meval.get_output_blockadedPawns(Player.BLACK)));
	}

}
