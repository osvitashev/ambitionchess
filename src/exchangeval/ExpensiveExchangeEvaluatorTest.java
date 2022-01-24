package exchangeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class ExpensiveExchangeEvaluatorTest {

	@Test
	void testCaptureAndOccupyWithNoKing() {
		Board brd = new Board();
		ExpensiveExchangeEvaluator eval = new ExpensiveExchangeEvaluator();
		//sanity check: no attacker and friendly captures for either side:
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.BLACK));
		//basic cases
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.D4, Player.WHITE));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.C5, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/N7/2p5/NB4PP/Q5PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/8/2p5/NB4PP/Q5PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r2Bpk/2q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("2r5/2r2Bpk/b1q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q2n1pp/p1p5/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q4pp/p1pn4/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2P3PP/3Q2PK/8 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2Q3PP/3P2PK/8 w - - 0 1"), Square.B4, Player.WHITE));
		//promotion
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P3pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/B7/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		//evil stack
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRRrq1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false, eval.toCaptureAndOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRrRq1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		//order actually matters = poisoned capture
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/2n3PP/R5PK/R7 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("R7/R2q2pk/2b3pp/1B6/p7/2n3PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("4Q3/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		//basic cases
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/8/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/8/1N4PP/P5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/2p5/1N4PP/P5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/2p5/1N4PP/P2N2PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/4b1pp/8/2p5/1N4PP/P2N2PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r6/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r6/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/3B4 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r4q1/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/3B4 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r6/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3BQ1PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r6/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3q2/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("1r3q2/1r2b1pk/3q2pp/1qp5/1N2RQR1/P1P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		//promotion
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/1p4PK/2N5 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/1p2N1PK/2N5 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/b5PP/1p2N1PK/2N5 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/b5PP/1p2N1PK/2N1R3 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/2r3pk/6pp/8/8/b5PP/1p2N1PK/2N1R3 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/2r3pk/6pp/8/8/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/2r3pk/6pp/2q5/8/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/2r3pk/6pp/2q5/5B2/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		//evil stack
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/3b2pk/2B3pp/1p6/NrRqRqQ1/6PP/6PK/8 b - - 0 1"), Square.A4, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("4Q3/3b2pk/2B3pp/1p6/NrRqRqQ1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("4Q3/3b2pk/2B3pp/1p6/NrqRRqQ1/6PP/6PK/8 b - - 0 1"), Square.A4, Player.BLACK));
		//poisoned capture =  trap
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/b1b5/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/b5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/2b3PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/n2Q4/2b5/1N2R1PP/2b3PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		//poisoned capture for opponent
		assertEquals(true,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/B1b5/1N4PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/B5PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toCaptureAndOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/2B3PK/8 b - - 0 1"), Square.B3, Player.BLACK));
	}

}
