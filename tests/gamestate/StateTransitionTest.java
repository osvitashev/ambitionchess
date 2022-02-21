package gamestate;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class StateTransitionTest {
	
	private static MovePool test_movepool = new MovePool();
	private static Gamestate test_board = new Gamestate();

	public static void runTest(String fen, String... uci_moves) {

		test_board.loadFromFEN(fen);
		runTest(test_board, Arrays.asList(uci_moves).subList(0, uci_moves.length));
	}

	public static void runTest(Gamestate board, List<String> uci_moves) {
		board.validateState();
		if (uci_moves.size()==0) {
			return;
		}
		int test_movelist_size_old = test_movepool.size();
		MoveGen.generateLegalMoves(test_board, test_movepool);
		
		

		// find move such that its UCI string matches the one provided
		boolean found = false;
		for (int i = test_movelist_size_old; i < test_movepool.size(); ++i) {
			if(Move.toUCINotation(test_movepool.get(i)).equals(uci_moves.get(0))) {
				found = true;
				int move = test_movepool.get(i);
				board.makeMove(move);
				
				runTest(board, uci_moves.subList(1, uci_moves.size()));
				
				board.unmakeMove(move);
				break;
			}
		}
		assertEquals(true, found);
		test_movepool.resize(test_movelist_size_old);
	}

	@Test
	void test() {
		runTest("8/8/8/3k1n2/8/PP6/2P5/R3K3 w Q - 0 1", "e1c1", "f5d4", "c2c4", "d5d6", "d1d4", "d6c6");
	}

}
