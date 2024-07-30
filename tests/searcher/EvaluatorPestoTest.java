package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;

class EvaluatorPestoTest {

	@Test
	void positionDifferentialTest() {
		Gamestate b1=new Gamestate(), b2=new Gamestate();
		EvaluatorPesto pesto = new EvaluatorPesto();
		
		//material balance - but different piece placement

		assertTrue(
			pesto.evaluate(b1.loadFromFEN("k7/pp6/8/8/4K3/8/PP6/8 w - - 0 1"))
			>
			pesto.evaluate(b2.loadFromFEN("k7/pp6/8/8/8/8/PP6/K7 w - - 0 1"))
		);
		assertTrue(
			pesto.evaluate(b1.loadFromFEN("k7/pp6/8/8/4K3/8/PP6/8 b - - 0 1"))
			<
			pesto.evaluate(b2.loadFromFEN("k7/pp6/8/8/8/8/PP6/K7 b - - 0 1"))
		);
		
		assertTrue(
			pesto.evaluate(b1.loadFromFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w - - 0 1"))
			>
			pesto.evaluate(b2.loadFromFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/7N/PPPP1PPP/RNBQKB1R w - - 0 1"))
		);
		assertTrue(
			pesto.evaluate(b1.loadFromFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b - - 0 1"))
			<
			pesto.evaluate(b2.loadFromFEN("rnbqkbnr/pppp1ppp/8/4p3/4P3/7N/PPPP1PPP/RNBQKB1R b - - 0 1"))
		);

	}
}