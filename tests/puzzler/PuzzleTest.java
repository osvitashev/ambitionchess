package puzzler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.Move;

class PuzzleTest {

	@Test
	void test() {

		String fen = "6k1/8/6K1/2P5/5B2/R4N2/7r/8 w - - 0 1";
		Gamestate brd = new Gamestate(fen);
		PuzzleSolver slv = new PuzzleSolver();
		int move = slv.toPlayAndWin(brd, 1);
		assertEquals("a3a8", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "7k/2r1n1p1/4Bp2/3P4/5Kp1/6P1/2p2PP1/R7 w - - 0 1";
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 1);
		assertEquals("a1h1", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "r2qk2r/p1p2ppp/Q7/1pb5/3nn3/2P4P/PP4P1/3R1BKR b kq - 0 1";// black instead of white!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 1);
		assertEquals("d4f3", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "1r3r2/6k1/2pp2pp/p7/1pP5/1P4qP/P1R1Qb2/3R1B1K b - - 0 1";// black instead of white!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 1);
		assertEquals("g3g1", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "5q2/3r3k/6Rp/8/P2p1p2/1P2r3/2P3QP/6RK w - - 0 1";//mate in 2!!!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 2);
		assertEquals("g6h6", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "4R3/1p1r1ppk/2r3b1/8/7P/2B1Q3/1P3P2/6K1 w - - 0 1";//mate in 3!!!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 3);
		assertEquals("e8h8", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "4rrk1/p5pp/8/2P3Q1/5nn1/1N6/PP3qBP/2R3QK b - - 0 1";//mate in 3!!!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 3);
		assertEquals("f2g1", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1";//mate in 6!!!
		brd.loadFromFEN(fen);
		move = slv.toPlayAndWin(brd, 6);
		assertEquals("a1a6", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
	}

}
