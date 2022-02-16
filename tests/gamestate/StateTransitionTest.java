package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StateTransitionTest {
	
	static void applyMoves(Gamestate brd, int[] moves) {
		for(int move : moves)
			brd.makeMove(move);
		
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
