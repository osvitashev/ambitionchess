package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class StateTransitionTest {
	
	private static MovePool test_movepool = new MovePool();
	private static Gamestate test_board = new Gamestate();
	private static MoveGen test_move_generator = new MoveGen();
	
	void processMoveSequence(Gamestate game, List<SimpleEntry<String, String>> moveToFENMapping) {
		game.validateState();
		if (moveToFENMapping.size()==0) {
			return;
		}
		
		int test_movelist_size_old = test_movepool.size();
		test_move_generator.generateLegalMoves(test_board, test_movepool);
		boolean found = false;
		String oldFEN= game.toFEN();
		for (int i = test_movelist_size_old; i < test_movepool.size(); ++i) {
			if(Move.toUCINotation(test_movepool.get(i)).equals(moveToFENMapping.get(0).getKey())) {
				found = true;
				int move = test_movepool.get(i);
				//System.out.println("making: " + uci_moves.get(0));
				game.makeMove(move);
				assertEquals(moveToFENMapping.get(0).getValue(), game.toFEN());
				processMoveSequence(game, moveToFENMapping.subList(1, moveToFENMapping.size()));
				
				game.unmakeMove(move);
				assertEquals(oldFEN, game.toFEN());
				break;
			}
		}
		assertEquals(true, found);
		test_movepool.resize(test_movelist_size_old);
	}
	
	@Test
	void testTransitionsWithMakeUnmake(){
		//captures and quiet moves
		processMoveSequence(test_board.loadFromFEN("8/8/8/4k3/1pP1n3/8/1K6/8 b - - 0 2"), List.of(
	            new SimpleEntry<>("b4b3", "8/8/8/4k3/2P1n3/1p6/1K6/8 w - - 0 3"),
	            new SimpleEntry<>("b2b3", "8/8/8/4k3/2P1n3/1K6/8/8 b - - 0 3"),
	            new SimpleEntry<>("e4d6","8/8/3n4/4k3/2P5/1K6/8/8 w - - 1 4"),
	            new SimpleEntry<>("b3c3","8/8/3n4/4k3/2P5/2K5/8/8 b - - 2 4"),
	            new SimpleEntry<>("d6c4","8/8/8/4k3/2n5/2K5/8/8 w - - 0 5"),
	            new SimpleEntry<>("c3c4","8/8/8/4k3/2K5/8/8/8 b - - 0 5"),
	            new SimpleEntry<>("e5e4","8/8/8/8/2K1k3/8/8/8 w - - 1 6")
	            // more entries
	        ));
		
		//castling - successful
		processMoveSequence(test_board.loadFromFEN("r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R3K2R w KQkq - 0 1"), List.of(
	            new SimpleEntry<>("e1c1", "r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/2KR3R b kq - 1 1"),
	            new SimpleEntry<>("e8g8", "r4rk1/ppp2ppp/8/8/8/8/PPP2PPP/2KR3R w - - 2 2"),
	            new SimpleEntry<>("d1d6", "r4rk1/ppp2ppp/3R4/8/8/8/PPP2PPP/2K4R b - - 3 2"),
	            new SimpleEntry<>("c7d6", "r4rk1/pp3ppp/3p4/8/8/8/PPP2PPP/2K4R w - - 0 3")
	            // more entries
	        ));
		processMoveSequence(test_board.loadFromFEN("r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R3K2R w KQkq - 0 1"), List.of(
	            new SimpleEntry<>("e1g1", "r3k2r/ppp2ppp/8/8/8/8/PPP2PPP/R4RK1 b kq - 1 1"),
	            new SimpleEntry<>("e8c8", "2kr3r/ppp2ppp/8/8/8/8/PPP2PPP/R4RK1 w - - 2 2"),
	            new SimpleEntry<>("a1d1", "2kr3r/ppp2ppp/8/8/8/8/PPP2PPP/3R1RK1 b - - 3 2"),
	            new SimpleEntry<>("d8d1", "2k4r/ppp2ppp/8/8/8/8/PPP2PPP/3r1RK1 w - - 0 3")
	            // more entries
	        ));
		
		//castling - rejected because of rook and king movement
		processMoveSequence(test_board.loadFromFEN("r3k2r/1pp2pp1/8/1p4p1/6P1/1PP2PP1/8/R3K2R w KQkq - 0 1"), List.of(
	            new SimpleEntry<>("a1a8", "R3k2r/1pp2pp1/8/1p4p1/6P1/1PP2PP1/8/4K2R b Kk - 0 1")
	            ,new SimpleEntry<>("e8e7", "R6r/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/4K2R w K - 1 2")
	            ,new SimpleEntry<>("a8a1", "7r/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/R3K2R b K - 2 2")
	            ,new SimpleEntry<>("h8h1", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/R3K2r w - - 0 3")
	            ,new SimpleEntry<>("e1e2", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/4K3/R6r b - - 1 3")
	            ,new SimpleEntry<>("h1a1", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/4K3/r7 w - - 0 4")
	            // more entries
	        ));
		
		processMoveSequence(test_board.loadFromFEN("r3k2r/1pp2pp1/8/1p4p1/6P1/1PP2PP1/8/R3K2R w KQkq - 0 1"), List.of(
	            new SimpleEntry<>("h1h8", "r3k2R/1pp2pp1/8/1p4p1/6P1/1PP2PP1/8/R3K3 b Qq - 0 1")
	            ,new SimpleEntry<>("e8e7", "r6R/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/R3K3 w Q - 1 2")
	            ,new SimpleEntry<>("h8h1", "r7/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/R3K2R b Q - 2 2")
	            ,new SimpleEntry<>("a8a1", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/8/r3K2R w - - 0 3")
	            ,new SimpleEntry<>("e1e2", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/4K3/r6R b - - 1 3")
	            ,new SimpleEntry<>("a1h1", "8/1pp1kpp1/8/1p4p1/6P1/1PP2PP1/4K3/7r w - - 0 4")
	            // more entries
	        ));
		//promotion and capture promotion
		processMoveSequence(test_board.loadFromFEN("r1b1kb1r/1P1pp1P1/8/8/8/8/1p1PP1p1/R1B1KB1R w KQkq - 0 1"), List.of(
	            new SimpleEntry<>("g7g8r", "r1b1kbRr/1P1pp3/8/8/8/8/1p1PP1p1/R1B1KB1R b KQkq - 0 1")
	            ,new SimpleEntry<>("h8g8", "r1b1kbr1/1P1pp3/8/8/8/8/1p1PP1p1/R1B1KB1R w KQq - 0 2")
	            ,new SimpleEntry<>("b7c8b", "r1B1kbr1/3pp3/8/8/8/8/1p1PP1p1/R1B1KB1R b KQq - 0 2")
	            ,new SimpleEntry<>("b2b1q", "r1B1kbr1/3pp3/8/8/8/8/3PP1p1/RqB1KB1R w KQq - 0 3")
	            ,new SimpleEntry<>("h1g1", "r1B1kbr1/3pp3/8/8/8/8/3PP1p1/RqB1KBR1 b Qq - 1 3")
	            ,new SimpleEntry<>("g2f1n", "r1B1kbr1/3pp3/8/8/8/8/3PP3/RqB1KnR1 w Qq - 0 4")
	            // more entries
	        ));
		//enpassant
		processMoveSequence(test_board.loadFromFEN("8/1p1p2p1/2n1k3/p1P1P2P/p2P1p1p/2pK4/1P2P1PR/8 w - - 0 1"), List.of(
	            new SimpleEntry<>("g2g4", "8/1p1p2p1/2n1k3/p1P1P2P/p2P1pPp/2pK4/1P2P2R/8 b - g3 0 1")
	            ,new SimpleEntry<>("h4g3", "8/1p1p2p1/2n1k3/p1P1P2P/p2P1p2/2pK2p1/1P2P2R/8 w - - 0 2")
	            ,new SimpleEntry<>("b2b4", "8/1p1p2p1/2n1k3/p1P1P2P/pP1P1p2/2pK2p1/4P2R/8 b - b3 0 2")
	            ,new SimpleEntry<>("b7b5", "8/3p2p1/2n1k3/ppP1P2P/pP1P1p2/2pK2p1/4P2R/8 w - b6 0 3")
	            ,new SimpleEntry<>("e2e4", "8/3p2p1/2n1k3/ppP1P2P/pP1PPp2/2pK2p1/7R/8 b - e3 0 3")
	            ,new SimpleEntry<>("g7g5", "8/3p4/2n1k3/ppP1P1pP/pP1PPp2/2pK2p1/7R/8 w - g6 0 4")
	            ,new SimpleEntry<>("h5g6", "8/3p4/2n1k1P1/ppP1P3/pP1PPp2/2pK2p1/7R/8 b - - 0 4")
	            ,new SimpleEntry<>("a5b4", "8/3p4/2n1k1P1/1pP1P3/pp1PPp2/2pK2p1/7R/8 w - - 0 5")
	            // more entries
	        ));
		
		
		//todo: add zobrist hash check for transitions
		//todo: add zobrist checks with FEN load
	}

}
