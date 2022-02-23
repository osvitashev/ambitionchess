package gamestate;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class StateTransitionTest {
	
	private static MovePool test_movepool = new MovePool();
	private static Gamestate test_board = new Gamestate();
	private static MoveGen test_move_generator = new MoveGen();

	public static void runTest(String fen, String correctFen, String... uci_moves) {
		test_board.loadFromFEN(fen);
		runTest(test_board,correctFen, Arrays.asList(uci_moves).subList(0, uci_moves.length));
		assertEquals(fen, test_board.toFEN());
	}

	public static void runTest(Gamestate board, String correctFen, List<String> uci_moves) {
		board.validateState();
		if (uci_moves.size()==0) {
			assertEquals(correctFen, test_board.toFEN());
			return;
		}
		int test_movelist_size_old = test_movepool.size();
		test_move_generator.generateLegalMoves(test_board, test_movepool);
		
		

		// find move such that its UCI string matches the one provided
		boolean found = false;
		for (int i = test_movelist_size_old; i < test_movepool.size(); ++i) {
			if(Move.toUCINotation(test_movepool.get(i)).equals(uci_moves.get(0))) {
				found = true;
				int move = test_movepool.get(i);
				//System.out.println("making: " + uci_moves.get(0));
				board.makeMove(move);
				
				runTest(board,correctFen, uci_moves.subList(1, uci_moves.size()));
				
				board.unmakeMove(move);
				break;
			}
		}
		assertEquals(true, found);
		test_movepool.resize(test_movelist_size_old);
	}

	@Test
	void test() {
		runTest("8/8/8/3k1n2/8/PP6/2P5/R3K3 w Q - 0 1", "8/8/2k5/8/2PR4/PP6/8/2K5 w - - 1 4", "e1c1", "f5d4", "c2c4", "d5d6", "d1d4", "d6c6");
	}

}
