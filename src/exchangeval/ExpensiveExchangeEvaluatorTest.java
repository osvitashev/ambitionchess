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
		//sanity check: no target
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		//move, but no capture available
//		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/8/1P6/6PP/6PK/8 w - - 0 1"), Square.B5, Player.WHITE));
//		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/8/1P6/6PP/6PK/8 b - - 0 1"), Square.B5, Player.WHITE));
//		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/8/1P6/6PP/6PK/8 w - - 0 1"), Square.B5, Player.BLACK));
//		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/8/1P6/6PP/6PK/8 b - - 0 1"), Square.B5, Player.BLACK));
		//friendly piece or no player
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1pP5/1Pp3PP/6PK/8 b - - 0 1"), Square.C3, Player.BLACK));
		//basic cases
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.D4, Player.WHITE));
		assertEquals(false, eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/3q4/1NP1P1PP/6PK/8 w - - 0 1"), Square.C5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/N7/2p5/NB4PP/Q5PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2r5/2r3pk/2q3pp/8/2p5/NB4PP/Q5PK/8 b - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2r5/2r2Bpk/2q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2r5/2r2Bpk/b1q3pp/8/2p1QRR1/6PP/6PK/8 w - - 0 1"), Square.C4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q2n1pp/p1p5/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r3b2/1r4pk/1q4pp/p1pn4/1p3R1R/P1Pn2PP/N1NB2PK/4Q3 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2P3PP/3Q2PK/8 w - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/2n3pp/P7/1n6/2Q3PP/3P2PK/8 w - - 0 1"), Square.B4, Player.WHITE));
		//promotion
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("3n4/2P3pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(false, eval.toOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("3n4/2P2npk/6pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(false, eval.toOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/8/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("3n4/2P2npk/1q4pp/B7/8/6PP/3R2PK/8 w - - 0 1"), Square.D8, Player.WHITE));
		//evil stack
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRRrq1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false, eval.toOccupy(brd.loadFromFEN("8/5npk/6pp/8/pRrRrRq1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		//order actually matters = poisoned capture
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/2n3PP/R5PK/R7 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("R7/R2q2pk/2b3pp/1B6/p7/2n3PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("4Q3/3q2pk/2b3pp/1B6/p7/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		//basic cases
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/8/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/8/1N4PP/P5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/2p5/1N4PP/P5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/6pp/8/2p5/1N4PP/P2N2PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/4b1pp/8/2p5/1N4PP/P2N2PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r6/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/3B4 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r4q1/1r4pk/4b1pp/8/2p5/1N4PP/P1QN2PK/3B4 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r6/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3BQ1PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r3q2/1r2b1pk/3q2pp/1qp5/1N2RQR1/2P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r3q2/1r2b1pk/3q2pp/1qp5/1N2RQR1/P1P3PP/3B2PK/4Q3 b - - 0 1"), Square.B4, Player.BLACK));
		//promotion
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/1p4PK/2N5 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/1p2N1PK/2N5 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/b5PP/1p2N1PK/2N5 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/b5PP/1p2N1PK/2N1R3 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2r3pk/6pp/8/8/b5PP/1p2N1PK/2N1R3 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/2r3pk/6pp/8/8/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2r3pk/6pp/2q5/8/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/2r3pk/6pp/2q5/5B2/b5PP/1p2N1PK/Q1N1R3 b - - 0 1"), Square.C1, Player.BLACK));
		//evil stack
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/3b2pk/2B3pp/1p6/NrRqRqQ1/6PP/6PK/8 b - - 0 1"), Square.A4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("4Q3/3b2pk/2B3pp/1p6/NrRqRqQ1/6PP/6PK/8 w - - 0 1"), Square.A4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("4Q3/3b2pk/2B3pp/1p6/NrqRRqQ1/6PP/6PK/8 b - - 0 1"), Square.A4, Player.BLACK));
		//poisoned capture =  trap
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/b1b5/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/b5PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/3Q4/2b5/1N4PP/2b3PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5Qpk/4B1pp/n2Q4/2b5/1N2R1PP/2b3PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		//poisoned capture for opponent
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/6PK/8 b - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/B1b5/1N4PP/6PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/B5PK/8 w - - 0 1"), Square.B3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/5bpk/4q1pp/3Q4/2b5/1N4PP/2B3PK/8 b - - 0 1"), Square.B3, Player.BLACK));
	}
	
	@Test
	void testToMoveAndOccupyWithNoKing() {
		Board brd = new Board();
		ExpensiveExchangeEvaluator eval = new ExpensiveExchangeEvaluator();
		//sanity check: blockaded pawns
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B6, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 b - - 0 1"), Square.B6, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B6, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 b - - 0 1"), Square.B6, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 b - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B5, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/1p4pp/1P6/8/6PP/6PK/8 b - - 0 1"), Square.B5, Player.BLACK));
		//basic
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/1R4PP/6PK/8 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/r7/8/1R4PP/6PK/8 b - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/r7/8/1R4PP/6PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r7/8/1R4PP/6PK/1Q6 b - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r7/8/1R4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r2q4/8/1R4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r2q4/P7/1R4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r2q1r2/P7/1R4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/6pp/r2q1r2/P7/NR4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n5pk/p5pp/r2q1r2/P7/NR4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/p5pp/r2q1r2/P1P5/NR4PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n5pk/p1p3pp/r2q1r2/P1P5/NR4PP/1R4PK/1Q6 b - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/p1p3pp/r2q1r2/P1P5/NR1B2PP/1R4PK/1Q6 b - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n2q2pk/p1p3pp/r2q1r2/P1P5/NR1B2PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n5pk/p1p3pp/r2q1r2/P1P5/NR1B2PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/n2q2pk/p1p3pp/r2q1r2/P1P5/NR1B2PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/n2q2pk/p1p3pp/r2q1r2/P1P5/NRNB2PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/n2q2pk/p1p3pp/r2q1r2/P1P5/NRNB2PP/1R4PK/1Q6 w - - 0 1"), Square.B5, Player.WHITE));
		//pawn pushes
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/1p6/8/6PP/1P4PK/8 w - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/1p6/8/6PP/1P4PK/8 w - - 0 1"), Square.B3, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/8/6PP/1P4PK/8 b - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2p5/8/6PP/1P4PK/8 b - - 0 1"), Square.B4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/1p4pk/6pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B6, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/p5pk/6pp/1P6/8/6PP/6PK/8 w - - 0 1"), Square.B6, Player.WHITE));
		//move ordering matters = poisoned advance
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/3n2pp/8/1P6/N5PP/6PK/8 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2N3pk/3n2pp/8/1P6/6PP/6PK/8 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2N3pk/6pp/8/1P6/6PP/1q4PK/1r6 w - - 0 1"), Square.B5, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1P6/N5PP/1q4PK/1r6 w - - 0 1"), Square.B5, Player.WHITE));
		//promotion
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/1P4pk/6pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("3r4/1P4pk/6pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("3r4/1P4pk/3B2pp/8/8/6PP/6PK/8 b - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("3rr3/1P4pk/3B2pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("3rr3/1P4pk/3B2pp/8/5Q2/6PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2qrr3/1P4pk/3B2pp/8/5Q2/6PP/6PK/8 b - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2qrr3/PP4pk/3B2pp/8/5Q2/6PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2qrr3/PP4pk/n2B2pp/8/5Q2/6PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2qrr3/PP4pk/n2B2pp/8/5Q2/1Q4PP/6PK/8 w - - 0 1"), Square.B8, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2qrr3/PP4pk/n1nB2pp/8/5Q2/1Q4PP/6PK/8 b - - 0 1"), Square.B8, Player.WHITE));
		//evil stack
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1R6/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1Rr5/6PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1RrR4/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1RrRq3/6PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1RrRqR2/6PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1RrRqR1r/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1RrRRq1r/6PP/6PK/8 w - - 0 1"), Square.A4, Player.WHITE));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/1Rr1qRRr/6PP/6PK/8 b - - 0 1"), Square.A4, Player.WHITE));
		//basic
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/3b2pp/8/8/6PP/6PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/3b2pp/8/8/P5PP/6PK/8 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r6/6pk/3b2pp/8/8/P5PP/6PK/8 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/6pk/3b2pp/8/8/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r6/4q1pk/3b2pp/8/8/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/4q1pk/3b2pp/8/2R5/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r6/4q1pk/2nb2pp/8/2R5/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r6/4q1pk/2nb2pp/3N4/2R5/P5PP/N5PK/8 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/2nb2pp/3N4/2R5/P5PP/N5PK/8 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/2nb2pp/3N4/2R1R3/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/2nb2pp/2pN4/2R1R3/P5PP/N5PK/8 b - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/2nb2pp/2pN4/2R1RQ2/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/n1nb2pp/2pN4/2R1RQ2/P5PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("1r3q2/4q1pk/n1nb2pp/2pN4/2R1RQ2/P1P3PP/N5PK/8 w - - 0 1"), Square.B4, Player.BLACK));
		//pawn pushes
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/2P5/6PP/6PK/8 w - - 0 1"), Square.C6, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/2P5/6PP/6PK/8 w - - 0 1"), Square.C5, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/2P5/6PP/6PK/8 b - - 0 1"), Square.C6, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/2P5/6PP/6PK/8 b - - 0 1"), Square.C5, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/1P6/6PP/6PK/8 w - - 0 1"), Square.C6, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/2p3pk/6pp/8/1P6/6PP/6PK/8 w - - 0 1"), Square.C5, Player.BLACK));
		//poisoned advance
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2R5/2R3pk/2Q3pp/1n6/2p5/6PP/6PK/8 w - - 0 1"), Square.C3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2R5/2R3pk/2Q3pp/8/2p5/6PP/6PK/1n6 w - - 0 1"), Square.C3, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("2R5/2R3pk/2Q2Bpp/2R5/2pp4/6PP/6PK/8 b - - 0 1"), Square.C3, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("2R5/2R3pk/2Q2Bpp/b1R5/2pp4/6PP/6PK/8 w - - 0 1"), Square.C3, Player.BLACK));
		//promotions
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/6PP/2p3PK/8 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/6PP/2p3PK/R7 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/1n4PP/2p3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/Bn4PP/2p3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/Bn4PP/n1p3PK/R7 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Bpp/8/8/Bn4PP/nQp3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/Bn2q1PP/nQp3PK/R7 b - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2R5/8/Bn2q1PP/nQp3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/2R5/5b2/Bn2q1PP/nQp3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/2Q3pk/2R3pp/2R5/5b2/Bn2q1PP/nQp3PK/R7 w - - 0 1"), Square.C1, Player.BLACK));//best not to move the pawn!
		//evil stack
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/6PP/1b4PK/8 b - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/8/2Q3PP/1b4PK/8 b - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/8/3q4/2Q3PP/1b4PK/8 b - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/4Q3/3q4/2Q3PP/1b4PK/8 w - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Qpp/8/3q4/1nQ3PP/1b4PK/8 w - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Qpp/8/3q4/1nQ3PP/1bN3PK/8 w - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(true,  eval.toOccupy(brd.loadFromFEN("8/6pk/5Qpp/4Q3/3q4/1n4PP/1bN3PK/8 w - - 0 1"), Square.A1, Player.BLACK));
		assertEquals(false,  eval.toOccupy(brd.loadFromFEN("8/6pk/6pp/4q3/3Q4/1nQ3PP/1bN3PK/8 w - - 0 1"), Square.A1, Player.BLACK));
		
	}
	
	//TODO:Exchange involving KING
	
	//TODO: Validate exception is evaluator is called while in check

}
