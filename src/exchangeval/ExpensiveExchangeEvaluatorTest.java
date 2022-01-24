package exchangeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.GlobalConstants.Square;
import gamestate.Move;
import puzzler.PuzzleSolver;

class ExpensiveExchangeEvaluatorTest {

	@Test
	void test() {
		Board brd = new Board();
		ExpensiveExchangeEvaluator eval = new ExpensiveExchangeEvaluator();
		// basic cases - white's turn. target is occupied by opponent.
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/n5pp/8/1P6/P1Q3PP/6PK/8 w - - 0 1"), Square.B4));//no friendly capture.
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/n5pp/8/1p6/6PP/P1Q3PK/8 w - - 0 1"), Square.B4));//no attacker.
		
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.D4));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.C5));
		
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/N7/2p5/NB4PP/Q5PK/8 w - - 0 1"), Square.C4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/8/2p5/NB4PP/Q5PK/8 w - - 0 1"), Square.C4));
		
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r2Bpk/2q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r2Bpk/b1q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4));
		
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q2n1pp/p1p5/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q4pp/p1pn4/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4));
		
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2P3PP/3Q2PK/8 w - - 0 1"), Square.B4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2Q3PP/3P2PK/8 w - - 0 1"), Square.B4));
		
		//promotion
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P3pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.D8));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.D8));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/B7/8/6PP/3R2PK/8 w - - 0 1"), Square.D8));
		//evil stack
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRRrq1/6PP/6PK/8 w - - 0 1"), Square.A4));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRrRq1/6PP/6PK/8 w - - 0 1"), Square.A4));
		//order actually matters!
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/2n3PP/R5PK/R7 w - - 0 1"), Square.A4));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("R7/R2q2pk/2b3pp/1B6/p7/2n3PP/6PK/8 w - - 0 1"), Square.A4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("4Q3/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4));
		
	}

}
