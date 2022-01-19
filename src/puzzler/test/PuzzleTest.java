package puzzler.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.Move;
import puzzler.PuzzleSolver;

class PuzzleTest {

	@Test
	void test() {

		int[][] rr = { { 1, 2, 3 }, { 4 } };

		String[][] testCases = { { "6k1/8/6K1/2P5/5B2/R4N2/7r/8 w - - 0 1", "a3a8" }, };

		String fen = "6k1/8/6K1/2P5/5B2/R4N2/7r/8 w - - 0 1";
		Board brd = new Board(fen);
		PuzzleSolver slv = new PuzzleSolver();

		int move = slv.whiteToPlayAndWin(brd, 0);

		assertEquals("a3a8", Move.toUCINotation(move));

		System.out.println(Move.moveToString(move));
		
		
		fen = "7k/2r1n1p1/4Bp2/3P4/5Kp1/6P1/2p2PP1/R7 w - - 0 1";
		brd.loadFromFEN(fen);
		move = slv.whiteToPlayAndWin(brd, 0);
		assertEquals("a1h1", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "r2qk2r/p1p2ppp/Q7/1pb5/3nn3/2P4P/PP4P1/3R1BKR b kq - 0 1";// black instead of white!
		brd.loadFromFEN(fen);
		move = slv.whiteToPlayAndWin(brd, 0);
		assertEquals("d4f3", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
		fen = "1r3r2/6k1/2pp2pp/p7/1pP5/1P4qP/P1R1Qb2/3R1B1K b - - 0 1";// black instead of white!
		brd.loadFromFEN(fen);
		move = slv.whiteToPlayAndWin(brd, 0);
		assertEquals("g3g1", Move.toUCINotation(move));
		System.out.println(Move.moveToString(move));
		
	}

}
