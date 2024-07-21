package gamestate;

import static gamestate.GlobalConstants.PieceType.*;
import static gamestate.GlobalConstants.Player.*;
import static gamestate.GlobalConstants.Square.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class MoveGenTest {
	private static MovePool assertMovePool = new MovePool();
	private static Gamestate assertMovelist_brd = new Gamestate();
	private static MoveGen test_move_generator = new MoveGen();
	
	/**
	 * TODO: refactor this away. A legacy Performs dirty move make/unmake. Checks if
	 * either king is in check after performing a given move.
	 * 
	 * @param candidateMoves      Collection of movepool to examine
	 * @param candidateMoves_size Number of movepool to validate
	 * @param returnMoves         Collection to be appended with valid movepool from
	 *                            the first argument
	 * @param returnMoves_begin   index of first insertion
	 * @return number of movepool added to the return collection
	 */
	private int filterValidMovesAndSetCheck(Gamestate brd, int[] candidateMoves, int candidateMoves_size, int[] returnMoves, int returnMoves_begin) {
		MovePool mp = new MovePool();
		for (int i = 0; i < candidateMoves_size; ++i) {
			int move = candidateMoves[i];
			test_move_generator.addToMovePoolAndSetCheckIfValid(brd, mp, move);
		}
		
		for(int i=0;i<mp.size();++i)
			returnMoves[returnMoves_begin+i]=mp.get(i);
		
		return mp.size();
	}

	@Test
	void testFilterValidMovesAndSetCheck() {
		{
			// white move - king in check
			Gamestate brd = new Gamestate("3N4/4kb2/8/2p2Q2/1pK5/8/8/8 w - - 0 1");
			int m0 = Move.createNormal(Square.D8, Square.B7, PieceType.KNIGHT, Player.WHITE);
			int m1 = Move.createNormal(Square.D8, Square.C6, PieceType.KNIGHT, Player.WHITE);
			int m2 = Move.createNormal(Square.D8, Square.E6, PieceType.KNIGHT, Player.WHITE);// +
			int m3 = Move.createCapture(Square.D8, Square.F7, PieceType.KNIGHT, PieceType.BISHOP, Player.WHITE);// +

			int m4 = Move.createNormal(Square.C4, Square.B5, PieceType.KING, Player.WHITE);// +
			int m5 = Move.createNormal(Square.C4, Square.B3, PieceType.KING, Player.WHITE);
			int m6 = Move.createNormal(Square.C4, Square.D5, PieceType.KING, Player.WHITE);
			int m7 = Move.createNormal(Square.C4, Square.C3, PieceType.KING, Player.WHITE);
			int m8 = Move.createNormal(Square.C4, Square.D4, PieceType.KING, Player.WHITE);
			int m9 = Move.createNormal(Square.C4, Square.D3, PieceType.KING, Player.WHITE);// +
			int m10 = Move.createCapture(Square.C4, Square.B4, PieceType.KING, PieceType.PAWN, Player.WHITE);
			int m11 = Move.createCapture(Square.C4, Square.C5, PieceType.KING, PieceType.PAWN, Player.WHITE);// +

			int m12 = Move.createNormal(Square.F5, Square.D7, PieceType.QUEEN, Player.WHITE);
			int m13 = Move.createNormal(Square.F5, Square.E6, PieceType.QUEEN, Player.WHITE);// + and check!
			int m14 = Move.createNormal(Square.F5, Square.D5, PieceType.QUEEN, Player.WHITE);// +
			int m15 = Move.createNormal(Square.F5, Square.E5, PieceType.QUEEN, Player.WHITE);
			int m16 = Move.createNormal(Square.F5, Square.F6, PieceType.QUEEN, Player.WHITE);
			int m17 = Move.createNormal(Square.F5, Square.F3, PieceType.QUEEN, Player.WHITE);
			int m18 = Move.createCapture(Square.F5, Square.F7, PieceType.QUEEN, PieceType.BISHOP, Player.WHITE);// + and check!

			int[] candidates = { m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12, m13, m14, m15, m16, m17, m18 };
			int[] answers = new int[20];
			// set checks!
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 0);
			// for (int i = 0; i < answerCount; ++i)
			// System.out.print(Move.moveToString(answers[i]) + " ");

			int[] rightAnswers = { m2, m3, m4, m9, m11, Move.setCheck(m13, true), m14, Move.setCheck(m18, true) };
			assertEquals(rightAnswers.length, answerCount);
			System.out.println();
			for (int i = 0; i < answerCount; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// white - king in double check
		{
			Gamestate brd = new Gamestate("3r4/2p3b1/1p3Q2/8/2n5/1P6/k2K3R/5B2 w - - 0 1");
			int[] candidates = { Move.createNormal(Square.D2, Square.C3, PieceType.KING, Player.WHITE), // and check!
					Move.createNormal(Square.D2, Square.C2, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.C1, PieceType.KING, Player.WHITE), // and check!
					Move.createNormal(Square.D2, Square.E2, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.E1, PieceType.KING, Player.WHITE), // and check!

					Move.createNormal(Square.D2, Square.D3, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.D1, PieceType.KING, Player.WHITE),
					Move.createNormal(Square.D2, Square.E3, PieceType.KING, Player.WHITE), Move.createCapture(Square.B3, Square.C4, PieceType.PAWN, PieceType.KNIGHT, Player.WHITE),
					Move.createNormal(Square.B3, Square.B4, PieceType.PAWN, Player.WHITE), Move.createCapture(Square.F1, Square.C4, PieceType.BISHOP, PieceType.KNIGHT, Player.WHITE),
					Move.createNormal(Square.F1, Square.D3, PieceType.BISHOP, Player.WHITE), Move.createCapture(Square.F6, Square.D8, PieceType.QUEEN, PieceType.ROOK, Player.WHITE),
					Move.createNormal(Square.F6, Square.D4, PieceType.QUEEN, Player.WHITE), Move.createNormal(Square.F6, Square.D6, PieceType.QUEEN, Player.WHITE), };
			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.setCheck(Move.createNormal(Square.D2, Square.C3, PieceType.KING, Player.WHITE), true), // and check!
					Move.createNormal(Square.D2, Square.C2, PieceType.KING, Player.WHITE), Move.setCheck(Move.createNormal(Square.D2, Square.C1, PieceType.KING, Player.WHITE), true), // and
																																														// check!
					Move.createNormal(Square.D2, Square.E2, PieceType.KING, Player.WHITE), Move.setCheck(Move.createNormal(Square.D2, Square.E1, PieceType.KING, Player.WHITE), true), // and
																																														// check!};
					0, 0, 0, 0, 0, };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(5, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// white - king not in check
		{
			Gamestate brd = new Gamestate("k2r4/8/8/3R3n/1pp5/8/3K2n1/8 w - - 0 1");
			int[] candidates = { Move.createNormal(Square.D2, Square.C3, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.C2, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.C1, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.D3, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.D1, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.E3, PieceType.KING, Player.WHITE), Move.createNormal(Square.D2, Square.E2, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.E1, PieceType.KING, Player.WHITE), Move.createNormal(Square.D5, Square.D6, PieceType.ROOK, Player.WHITE), // +
					Move.createNormal(Square.D5, Square.C5, PieceType.ROOK, Player.WHITE), Move.createNormal(Square.D5, Square.E5, PieceType.ROOK, Player.WHITE),
					Move.createNormal(Square.D5, Square.D4, PieceType.ROOK, Player.WHITE), // ++
					Move.createCapture(Square.D5, Square.D8, PieceType.ROOK, PieceType.ROOK, Player.WHITE),// ++ and check
			};
			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.createNormal(Square.D2, Square.C2, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.C1, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.D1, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D2, Square.E2, PieceType.KING, Player.WHITE), // +
					Move.createNormal(Square.D5, Square.D6, PieceType.ROOK, Player.WHITE), // +
					Move.createNormal(Square.D5, Square.D4, PieceType.ROOK, Player.WHITE), // +
					Move.setCheck(Move.createCapture(Square.D5, Square.D8, PieceType.ROOK, PieceType.ROOK, Player.WHITE), true), 0, 0, 0, 0, 0 };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(7, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// black - king in check
		{
			Gamestate brd = new Gamestate("4r3/8/8/3k4/4Pp2/6N1/1PP5/R3K3 b Q e3 0 10");
			int[] candidates = { Move.createNormal(Square.D5, Square.C6, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.C5, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.C4, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.D6, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.D4, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.E6, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.E5, PieceType.KING, Player.BLACK), Move.createCapture(Square.D5, Square.E4, PieceType.KING, PieceType.PAWN, Player.BLACK), // --
					Move.createEnpassant(Square.F4, Square.E3, Player.BLACK), Move.createNormal(Square.F4, Square.F3, PieceType.PAWN, Player.BLACK), // --
					Move.createCapture(Square.F4, Square.G3, PieceType.PAWN, PieceType.KNIGHT, Player.BLACK), // --
					Move.createNormal(Square.E8, Square.E7, PieceType.ROOK, Player.BLACK), // --
					Move.createCapture(Square.E8, Square.E4, PieceType.ROOK, PieceType.PAWN, Player.BLACK), };
			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.createNormal(Square.D5, Square.C6, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.C5, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.C4, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.D6, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.D4, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.D5, Square.E6, PieceType.KING, Player.BLACK), Move.createNormal(Square.D5, Square.E5, PieceType.KING, Player.BLACK),
					Move.createEnpassant(Square.F4, Square.E3, Player.BLACK), Move.setCheck(Move.createCapture(Square.E8, Square.E4, PieceType.ROOK, PieceType.PAWN, Player.BLACK), true), 0,
					0, 0, 0, 0 };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(9, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// black - double check
		{
			Gamestate brd = new Gamestate("8/8/4k3/1q6/1n6/4R3/B6K/5R2 b - - 0 1");
			int[] candidates = { Move.createNormal(Square.E6, Square.D7, PieceType.KING, Player.BLACK), // +
					Move.createNormal(Square.E6, Square.D6, PieceType.KING, Player.BLACK), // +
					Move.createNormal(Square.E6, Square.D5, PieceType.KING, Player.BLACK), Move.createNormal(Square.E6, Square.E7, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.E6, Square.E5, PieceType.KING, Player.BLACK), Move.createNormal(Square.E6, Square.F7, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.E6, Square.F6, PieceType.KING, Player.BLACK), Move.createNormal(Square.E6, Square.F5, PieceType.KING, Player.BLACK),
					Move.createCapture(Square.B4, Square.A2, PieceType.KNIGHT, PieceType.BISHOP, Player.BLACK), Move.createNormal(Square.B4, Square.D5, PieceType.KNIGHT, Player.BLACK),
					Move.createNormal(Square.B5, Square.E5, PieceType.QUEEN, Player.BLACK), };

			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.createNormal(Square.E6, Square.D7, PieceType.KING, Player.BLACK), // +
					Move.createNormal(Square.E6, Square.D6, PieceType.KING, Player.BLACK), // +
					0, 0, 0, 0, 0 };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(2, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// black - not in check
		{
			Gamestate brd = new Gamestate("4k2r/4p1p1/8/8/7N/8/3R1K2/8 b k - 0 1");
			int[] candidates = { Move.createNormal(Square.E8, Square.D8, PieceType.KING, Player.BLACK), Move.createNormal(Square.E8, Square.D7, PieceType.KING, Player.BLACK),
					Move.createNormal(Square.E8, Square.F8, PieceType.KING, Player.BLACK), // +
					Move.createNormal(Square.E8, Square.F7, PieceType.KING, Player.BLACK), // +
					Move.createCastleKing(Player.BLACK), // ++
					Move.createNormal(Square.H8, Square.H6, PieceType.ROOK, Player.BLACK),// ++
			};
			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.createNormal(Square.E8, Square.F8, PieceType.KING, Player.BLACK), // +
					Move.createNormal(Square.E8, Square.F7, PieceType.KING, Player.BLACK), // +
					Move.setCheck(Move.createCastleKing(Player.BLACK), true), // ++
					Move.createNormal(Square.H8, Square.H6, PieceType.ROOK, Player.BLACK), // ++
					0, 0, 0, 0, 0 };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(4, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
		// promotion when king is in check
		{
			Gamestate brd = new Gamestate("8/8/3B4/8/K7/8/2p1p2R/3R2k1 b - - 15 59");
			int[] candidates = { Move.createNormal(Square.G1, Square.F2, PieceType.KING, Player.BLACK),
					Move.createCapture(Square.G1, Square.H2, PieceType.KING, PieceType.ROOK, Player.BLACK), Move.createPromo(Square.C2, Square.C1, PieceType.ROOK, Player.BLACK),
					Move.createPromo(Square.E2, Square.E1, PieceType.ROOK, Player.BLACK), // ++
					Move.createCapturePromo(Square.C2, Square.D1, PieceType.ROOK, PieceType.BISHOP, Player.BLACK), // ++ and check
					Move.createCapturePromo(Square.E2, Square.D1, PieceType.ROOK, PieceType.BISHOP, Player.BLACK),// ++
			};
			int[] answers = new int[20];
			int[] rightAnswers = { 0, 0, 0, 0, 0, Move.createPromo(Square.E2, Square.E1, PieceType.ROOK, Player.BLACK), // ++
					Move.setCheck(Move.createCapturePromo(Square.C2, Square.D1, PieceType.ROOK, PieceType.BISHOP, Player.BLACK), true), // ++
					Move.createCapturePromo(Square.E2, Square.D1, PieceType.ROOK, PieceType.BISHOP, Player.BLACK), // ++
					0, 0, 0, 0, 0 };
			int answerCount = filterValidMovesAndSetCheck(brd, candidates, candidates.length, answers, 5);
			assertEquals(3, answerCount);
			for (int i = 5; i < answerCount + 5; ++i) {
				// System.out.println(Move.moveToString(rightAnswers[i]) + " vs " +
				// Move.moveToString(answers[i]) + " ");
				assertEquals(rightAnswers[i], answers[i]);
			}
		}
	}

	private void assertMoveListsEqual(int[] expected, MovePool movepool_actual, int discard) {// TODO: add a sanity check test
		java.util.Arrays.sort(expected);
		int actual_size = movepool_actual.size();
		int[] actual = new int[0];
		
		try {
			Field fld = MovePool.class.getDeclaredField("movepool");
			fld.setAccessible(true);
			actual=(int[]) fld.get(movepool_actual);
			//method.invoke(brd, move);
		} catch (Exception e) {

		}
		
		java.util.Arrays.sort(actual, 0, actual_size);

		StringBuilder b = new StringBuilder();
		b.append("Expected: (");
		for (int i = 0; i < expected.length; ++i) {
			b.append(Move.moveToString(expected[i]));
			b.append(", ");
		}
		b.append(") Actual: (");
		for (int i = 0; i < actual_size; ++i) {
			b.append(Move.moveToString(actual[i]));
			b.append(", ");
		}
		b.append(")");

		String msg = b.toString();

		assertTrue(msg, expected.length == actual_size);

		for (int i = 0; i < actual_size; ++i)
			assertTrue(msg, expected[i] == actual[i]);

	}

	@Test
	void testGenerateEnpassant() {
		Gamestate brd;
		MovePool movepool = new MovePool();
		//int[] movepool = new int[100];

		brd = new Gamestate("8/1k6/8/3PpP2/8/8/1K6/8 w - e6 0 1");
		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.D5, Square.E6, Player.WHITE), Move.createEnpassant(Square.F5, Square.E6, Player.WHITE) }, movepool,
				test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();

		brd = new Gamestate("8/1k6/8/3PpP2/8/8/1K6/8 w - - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();

		brd = new Gamestate("4R3/8/7B/4PpP1/8/1K2k3/8/8 w - f6 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createEnpassant(Square.E5, Square.F6, Player.WHITE)), Move.setCheck(Move.createEnpassant(Square.G5, Square.F6, Player.WHITE)) },
				movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/1k6/3P4/4pP2/8/8/1K6/8 w - e6 0 1");
		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.F5, Square.E6, Player.WHITE) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/1k6/8/3ppP2/3P4/5p2/1K6/8 w - e6 0 1");
		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.F5, Square.E6, Player.WHITE) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/1k1P1P2/3p4/3ppP2/8/4P3/1K6/8 w - e6 0 1");
		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.F5, Square.E6, Player.WHITE) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/k7/8/Pp6/2K5/8/8/8 w - b6 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createEnpassant(Square.A5, Square.B6, Player.WHITE)) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		// open checks preventing en passant
		brd = new Gamestate("8/b7/7k/2Pp4/3K4/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/b7/7k/2Pp4/2K5/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] { (Move.createEnpassant(Square.C5, Square.D6, Player.WHITE)) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("2r5/b7/7k/2Pp4/2K5/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("2r5/b7/7k/2Pp4/4K3/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] { (Move.createEnpassant(Square.C5, Square.D6, Player.WHITE)) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("2r5/1b6/7k/2Pp4/4K3/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("2r5/1b6/7k/2PpP3/4K3/8/8/8 w - d6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("4r2k/8/8/3pP3/8/8/4K3/8 w - d6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		// starts in check
		brd = new Gamestate("2k5/8/8/5PpP/3n4/1K6/8/8 w - g6 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		/// black
		brd = new Gamestate("8/8/6K1/8/1pPp4/6k1/8/8 b - c3 0 1");
		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.B4, Square.C3, Player.BLACK), Move.createEnpassant(Square.D4, Square.C3, Player.BLACK) }, movepool,
				test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/6K1/8/1pPp4/6k1/8/8 b - - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/6K1/8/p1P1p3/6k1/1p1p4/8 b - c3 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/8/3K4/1pPp4/6k1/b7/3r4 b - c3 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createEnpassant(Square.B4, Square.C3, Player.BLACK)), Move.setCheck(Move.createEnpassant(Square.D4, Square.C3, Player.BLACK)) },
				movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("6k1/3K4/8/8/1pPp4/8/Q7/3r4 b - c3 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/1k1K4/8/8/1pPp4/8/1Q6/3r4 b - c3 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createEnpassant(Square.D4, Square.C3, Player.BLACK)) }, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/8/8/1pPp4/1k6/3K4/4r3 b - c3 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createEnpassant(Square.B4, Square.C3, Player.BLACK)), Move.setCheck(Move.createEnpassant(Square.D4, Square.C3, Player.BLACK)) },
				movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		// open checks
		brd = new Gamestate("8/8/5K2/2k5/1pPp4/Q7/8/4r1B1 b - c3 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("4k3/8/2K3Q1/8/1pPp4/8/8/4r1B1 b - c3 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/8/3k4/1pPp4/4N3/8/1K6 b - c3 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateEnpassant(brd, movepool));
		movepool.clear();
		
//		brd = new Board("");
//		assertMoveListsEqual(new int[] { Move.createEnpassant(Square.G5, Square.H6, Player.WHITE) },
//				movepool, test_move_generator.generateEnpassant(brd, movepool));
	}

	@Test
	void testGenerateCastling() {
		MovePool movepool = new MovePool();
		// white
		Gamestate brd = new Gamestate("8/8/8/4k3/1pPp4/4N3/8/R3K2R w KQ - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE), Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		brd = new Gamestate("8/8/8/4k3/1pPp4/4N3/8/R3K2R w - - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();

		brd = new Gamestate("r3k2r/ppp2ppp/8/8/1pPp4/4N3/8/R3K2R w kq - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/8/8/8/8/R3K2R w Qkq - 0 1");// fen condition
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/r7/8/8/8/R3K2R w KQkq - 0 1");// castling is allowed even rook is attacked
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE), Move.createCastleQueen(WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/1r6/8/8/8/R3K2R w KQkq - 0 1");//castling when square passed by rook is attacked
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE), Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/2r5/8/8/8/R3K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/3r4/8/8/8/R3K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/4r3/8/8/8/R3K2R w KQkq - 0 1");// check!
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/5r2/8/8/8/R3K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/6r1/8/8/8/R3K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/ppp2ppp/6Q1/7r/8/8/8/R3K2R w KQkq - 0 1");// can castle even when rook is attacked
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE), Move.createCastleKing(WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k1nr/ppp2ppp/6Q1/8/8/8/8/RN2K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k1nr/ppp2ppp/6Q1/8/8/8/8/R1N1K2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k1nr/ppp2ppp/6Q1/8/8/8/8/R2NK2R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k1nr/ppp2ppp/6Q1/8/8/8/8/R3KN1R w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k1nr/ppp2ppp/6Q1/8/8/8/8/R3K1NR w KQkq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.WHITE) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		// castling leads to check
		brd = new Gamestate("8/5k2/8/8/8/8/1q4PP/4K2R w K - 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createCastleKing(Player.WHITE)) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("8/8/3k4/8/q7/8/PPP3PP/R3K3 w Q - 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createCastleQueen(Player.WHITE)) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		//////// BLACK
		brd = new Gamestate("r3k2r/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK), Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/8/8/4K3 b q - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/8/8/4K3 b - - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("4k3/8/8/8/8/8/8/R3K2R b KQ - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		// attacked square
		brd = new Gamestate("r3k2r/8/8/8/8/7R/8/4K3 b kq - 0 1");// can castle even when rook is attacked
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK), Move.createCastleKing(BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/6R1/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/5R2/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/4R3/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] {}, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/3R4/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/2R5/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/1R6/8/4K3 b kq - 0 1");//castling when rook has to pass through an attacked square
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK), Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k2r/8/8/8/8/R7/8/4K3 b kq - 0 1");// can castle even when rook is attacked;
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK), Move.createCastleQueen(BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		// blocked square
		brd = new Gamestate("r3k1nr/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3kn1r/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleQueen(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r2nk2r/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r1n1k2r/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("rn2k2r/8/8/8/8/8/8/4K3 b kq - 0 1");
		assertMoveListsEqual(new int[] { Move.createCastleKing(Player.BLACK) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		// check from castling
		brd = new Gamestate("4k2r/ppp3pp/8/8/8/8/5K2/8 b k - 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createCastleKing(Player.BLACK)) }, movepool, test_move_generator.generateCastling(brd, movepool));
		movepool.clear();
		
		brd = new Gamestate("r3k3/ppp3pp/8/8/8/8/3K4/8 b q - 0 1");
		assertMoveListsEqual(new int[] { Move.setCheck(Move.createCastleQueen(Player.BLACK)) }, movepool, test_move_generator.generateCastling(brd, movepool));
	}

	private void assertMovelist(String fen, MoveGen.LegalMoveGenerator generator, int[] expectedMoves) {
		assertMovePool.clear();
		assertMovelist_brd.loadFromFEN(fen);
		int nummoves = generator.generateLegalMoves(assertMovelist_brd, assertMovePool);
		assertMoveListsEqual(expectedMoves, assertMovePool, nummoves);
	}

	private void assertMovelistLength(String fen, int expectedMoves) {
		assertMovePool.clear();
		assertMovelist_brd.loadFromFEN(fen);
		int nummoves = test_move_generator.generateLegalMoves(assertMovelist_brd, assertMovePool);
		StringBuilder b = new StringBuilder();
		b.append("Expected: (#");
		b.append(expectedMoves);
		b.append(") Actual: (#");

		b.append(nummoves);

		b.append("): (");
		for (int i = 0; i < nummoves; ++i) {
			b.append(Move.moveToString(assertMovePool.get(i)));
			b.append(", ");
		}
		b.append(")");

		String msg = b.toString();

		assertTrue(msg, expectedMoves == nummoves);
	}

	@Test
	void testGenerateKingMoves() {
		// White
		assertMovelist("8/5k2/8/8/1K6/8/8/8 w - - 0 1", test_move_generator::generateKingMoves,
				new int[] { Move.createNormal(B4, B5, KING, WHITE), Move.createNormal(B4, C5, KING, WHITE), Move.createNormal(B4, C4, KING, WHITE), Move.createNormal(B4, C3, KING, WHITE),
						Move.createNormal(B4, B3, KING, WHITE), Move.createNormal(B4, A3, KING, WHITE), Move.createNormal(B4, A4, KING, WHITE), Move.createNormal(B4, A5, KING, WHITE) });

		assertMovelist("7k/8/7K/8/8/7R/8/8 w - - 0 1", test_move_generator::generateKingMoves,
				new int[] { Move.setCheck(Move.createNormal(H6, G6, KING, WHITE)), Move.setCheck(Move.createNormal(H6, G5, KING, WHITE)), Move.createNormal(H6, H5, KING, WHITE), });

		assertMovelist("1k6/2p5/4p3/3K4/2r5/8/8/8 w - - 0 1", test_move_generator::generateKingMoves, new int[] { Move.createNormal(D5, E5, KING, WHITE), });

		assertMovelist("8/5k2/8/8/8/8/6PP/3r2K1 w - - 0 1", test_move_generator::generateKingMoves, new int[] { Move.createNormal(G1, F2, KING, WHITE), });

		assertMovelist("8/b5k1/4p3/8/2PK4/8/8/B7 w - - 0 1", test_move_generator::generateKingMoves, new int[] { Move.createNormal(D4, E5, KING, WHITE),
				Move.setCheck(Move.createNormal(D4, E4, KING, WHITE)), Move.setCheck(Move.createNormal(D4, D3, KING, WHITE)), Move.createNormal(D4, C3, KING, WHITE), });

		// Black
		assertMovelist("r3k2r/8/8/8/1b6/8/2K5/8 b kq - 0 1", test_move_generator::generateKingMoves, new int[] { Move.createNormal(E8, D8, KING, BLACK), Move.createNormal(E8, D7, KING, BLACK),
				Move.createNormal(E8, E7, KING, BLACK), Move.createNormal(E8, F7, KING, BLACK), Move.createNormal(E8, F8, KING, BLACK), });

		assertMovelist("8/4q3/5R2/3pk3/8/2P5/4K2B/8 b - - 0 1", test_move_generator::generateKingMoves, new int[] { Move.createNormal(E5, E4, KING, BLACK), });

		assertMovelist("8/4q3/5R2/3pk3/8/3P4/4K2B/8 b - - 0 1", test_move_generator::generateKingMoves, new int[] { Move.setCheck(Move.createNormal(E5, D4, KING, BLACK)), });
	}

	@Test
	void testGenerateKingCaptures() {
		assertMovelist("8/6k1/2p5/2Np4/3K4/2np4/3P4/2R5 w - - 0 1", test_move_generator::generateKingCaptures,
				new int[] { Move.createCapture(D4, C3, KING, KNIGHT, WHITE), Move.createCapture(D4, D3, KING, PAWN, WHITE) });

		assertMovelist("8/8/3k4/8/7n/6Kr/6nB/8 w - - 0 1", test_move_generator::generateKingCaptures, new int[] { Move.setCheck(Move.createCapture(G3, H3, KING, ROOK, WHITE)), });

		assertMovelist("8/8/8/8/5k2/2pKp3/8/8 w - - 0 1", test_move_generator::generateKingCaptures, new int[] { Move.createCapture(D3, C3, KING, PAWN, WHITE), });

		assertMovelist("8/5k2/8/3n4/2nK4/2b5/8/3r4 w - - 0 1", test_move_generator::generateKingCaptures, new int[] { Move.createCapture(D4, C4, KING, KNIGHT, WHITE), });

		assertMovelist("8/6k1/3q4/2rKR3/2p1b3/3r4/8/8 w - - 0 1", test_move_generator::generateKingCaptures, new int[] { Move.createCapture(D5, E4, KING, BISHOP, WHITE) });

		assertMovelist("2N5/pk6/1PB5/P7/8/6K1/8/8 b - - 0 1", test_move_generator::generateKingCaptures,
				new int[] { Move.createCapture(B7, C6, KING, BISHOP, BLACK), Move.createCapture(B7, C8, KING, KNIGHT, BLACK) });

		assertMovelist("8/7q/4Q3/5k2/6R1/8/8/1K6 b - - 0 1", test_move_generator::generateKingCaptures, new int[] { Move.setCheck(Move.createCapture(F5, E6, KING, QUEEN, BLACK)), });

		assertMovelist("8/6k1/8/8/8/8/5K2/8 b - - 0 1", test_move_generator::generateKingCaptures, new int[] {});
	}

	@Test
	void testGenerateKnightMoves() {
		assertMovelist("8/4k3/8/2n1p3/5p2/3N4/1K3P2/4R3 w - - 0 1", test_move_generator::generateKnightMoves, // basic
				new int[] { Move.createNormal(D3, B4, KNIGHT, WHITE), Move.createNormal(D3, C1, KNIGHT, WHITE) });

		assertMovelist("8/2k2b2/8/8/8/1K2N3/4B3/8 w - - 0 1", test_move_generator::generateKnightMoves, // checks
				new int[] { Move.createNormal(E3, C4, KNIGHT, WHITE), Move.setCheck(Move.createNormal(E3, D5, KNIGHT, WHITE)) });

		assertMovelist("8/2k2b2/8/8/8/1K6/4B3/8 w - - 0 1", test_move_generator::generateKnightMoves, // no knight
				new int[] {});

		assertMovelist("5k2/5N2/8/8/8/K4r2/8/8 w - - 0 1", test_move_generator::generateKnightMoves, // in check which cannot be blocked
				new int[] {});

		assertMovelist("5k2/8/8/8/8/K1N2r2/8/8 w - - 0 1", test_move_generator::generateKnightMoves, // pinned knight
				new int[] {});

		assertMovelist("N7/6k1/1r6/8/8/8/5P1q/1r1NK2N w - - 0 1", test_move_generator::generateKnightMoves, // multiple knights
				new int[] { Move.createNormal(A8, C7, KNIGHT, WHITE), Move.createNormal(H1, G3, KNIGHT, WHITE) });
		// black
		assertMovelist("2r1r3/1k3b2/3n4/5P2/4P3/8/1K6/8 b - - 0 1", test_move_generator::generateKnightMoves, // basic + check
				new int[] { Move.createNormal(D6, B5, KNIGHT, BLACK), Move.setCheck(Move.createNormal(D6, C4, KNIGHT, BLACK)) });

		assertMovelist("8/5k2/8/6n1/5n2/8/B7/2n2R1K b - - 0 1", test_move_generator::generateKnightMoves, // multiple knights, king in check and a pin
				new int[] { Move.createNormal(C1, B3, KNIGHT, BLACK), Move.createNormal(G5, E6, KNIGHT, BLACK) });
	}

	@Test
	void testGenerateKnightCaptures() {
		assertMovelist("8/8/6k1/3p4/4p3/2N5/K7/8 w - - 0 1", test_move_generator::generateKnightCaptures, // basic
				new int[] { Move.createCapture(C3, D5, KNIGHT, PAWN, WHITE), Move.createCapture(C3, E4, KNIGHT, PAWN, WHITE) });

		assertMovelist("3r2bk/2r3p1/4N3/2p3B1/5p2/8/K7/8 w - - 0 1", test_move_generator::generateKnightCaptures, // pinned knight
				new int[] {});

		assertMovelist("5r2/5qn1/4k3/1Q6/5P2/5B2/3K4/5R2 w - - 0 1", test_move_generator::generateKnightCaptures, // no knight
				new int[] {});

		assertMovelist("5k2/3q4/2n5/4N3/8/5P2/6PP/7K w - - 0 1", test_move_generator::generateKnightCaptures, // check
				new int[] { Move.setCheck(Move.createCapture(E5, D7, KNIGHT, QUEEN, WHITE)), Move.createCapture(E5, C6, KNIGHT, KNIGHT, WHITE) });

		assertMovelist("5k2/5r2/1n4q1/4N3/2N5/4bP2/6PP/7K w - - 0 1", test_move_generator::generateKnightCaptures, // two knights
				new int[] { Move.setCheck(Move.createCapture(E5, G6, KNIGHT, QUEEN, WHITE)), Move.createCapture(E5, F7, KNIGHT, ROOK, WHITE),
						Move.createCapture(C4, B6, KNIGHT, KNIGHT, WHITE), Move.createCapture(C4, E3, KNIGHT, BISHOP, WHITE), });
		// black
		assertMovelist("3k4/1n6/3r4/N1P5/3P1N2/8/PPP5/1K6 b - - 0 1", test_move_generator::generateKnightCaptures, // basic
				new int[] { Move.createCapture(B7, A5, KNIGHT, KNIGHT, BLACK), Move.createCapture(B7, C5, KNIGHT, PAWN, BLACK) });

		assertMovelist("6k1/2R5/4n3/8/5P2/4N1N1/B7/1K6 b - - 0 1", test_move_generator::generateKnightCaptures, // pinned knight
				new int[] {});

		assertMovelist("5r2/5q2/4k3/1Q6/5P2/1N3B2/3K4/5R2 b - - 0 1", test_move_generator::generateKnightCaptures, // no knight
				new int[] {});

		assertMovelist("2r5/4k2p/5ppb/8/2n5/4R3/2KQ4/8 b - - 0 1", test_move_generator::generateKnightCaptures, // check and open check
				new int[] { Move.setCheck(Move.createCapture(C4, E3, KNIGHT, ROOK, BLACK)) });

		assertMovelist("2r5/6kp/5ppb/8/2n5/4R3/2KQ4/8 b - - 0 1", test_move_generator::generateKnightCaptures, // check and open check
				new int[] { Move.setCheck(Move.createCapture(C4, D2, KNIGHT, QUEEN, BLACK)), Move.setCheck(Move.createCapture(C4, E3, KNIGHT, ROOK, BLACK)) });

		assertMovelist("8/2kr4/8/3n1B2/5pb1/2P1N3/6n1/1K6 b - - 0 1", test_move_generator::generateKnightCaptures, // two knights
				new int[] { Move.setCheck(Move.createCapture(D5, C3, KNIGHT, PAWN, BLACK)), Move.createCapture(D5, E3, KNIGHT, KNIGHT, BLACK),
						Move.createCapture(G2, E3, KNIGHT, KNIGHT, BLACK), });
	}

	@Test
	void testGenerateRookMoves() {
		assertMovelist("8/6n1/3K2R1/8/k7/8/6r1/8 w - - 0 1", test_move_generator::generateRookMoves, // basic with possibility of check
				new int[] { Move.createNormal(G6, F6, ROOK, WHITE), Move.createNormal(G6, E6, ROOK, WHITE), Move.createNormal(G6, H6, ROOK, WHITE), Move.createNormal(G6, G5, ROOK, WHITE),
						Move.setCheck(Move.createNormal(G6, G4, ROOK, WHITE)), Move.createNormal(G6, G3, ROOK, WHITE), });

		assertMovelist("1k6/8/4K3/4R3/8/8/8/4q3 w - - 0 1", test_move_generator::generateRookMoves, // pinned
				new int[] { Move.createNormal(E5, E4, ROOK, WHITE), Move.createNormal(E5, E3, ROOK, WHITE), Move.createNormal(E5, E2, ROOK, WHITE), });

		assertMovelist("1k6/8/4K3/3R4/8/8/b7/8 w - - 0 1", test_move_generator::generateRookMoves, // pinned
				new int[] {});

		assertMovelist("1k6/2n4R/4K3/8/7R/8/2R5/3R4 w - - 0 1", test_move_generator::generateRookMoves, // knight check
				new int[] {});

		assertMovelist("1k6/2n5/4K3/8/2Q5/8/8/8 w - - 0 1", test_move_generator::generateRookMoves, // no rooks
				new int[] {});

		assertMovelist("3r3b/4n3/q7/6k1/8/8/PP2P1PP/2K1R2R w - - 0 1", test_move_generator::generateRookMoves, // multiple rooks
				new int[] { Move.createNormal(H1, G1, ROOK, WHITE), Move.createNormal(H1, F1, ROOK, WHITE), Move.createNormal(E1, D1, ROOK, WHITE), Move.createNormal(E1, F1, ROOK, WHITE),
						Move.createNormal(E1, G1, ROOK, WHITE), });
		// black
		assertMovelist("8/8/4k3/2r5/8/6K1/B7/1r6 b - - 0 1", test_move_generator::generateRookMoves, // check with multiple rooks
				new int[] { Move.setCheck(Move.createNormal(B1, B3, ROOK, BLACK)), Move.createNormal(C5, C4, ROOK, BLACK), Move.createNormal(C5, D5, ROOK, BLACK), });
	}

	@Test
	void testGenerateRookCaptures() {
		assertMovelist("2B1r1n1/8/6k1/8/7n/4R1p1/1K6/7R w - - 0 1", test_move_generator::generateRookCaptures, // basic with two rooks
				new int[] { Move.createCapture(E3, E8, ROOK, ROOK, WHITE), Move.setCheck(Move.createCapture(E3, G3, ROOK, PAWN, WHITE)), Move.createCapture(H1, H4, ROOK, KNIGHT, WHITE), });

		assertMovelist("6q1/k6n/8/2R5/3B4/2p1R3/1K2N1R1/6r1 w - - 0 1", test_move_generator::generateRookCaptures, // two rooks, and check
				new int[] { Move.createCapture(E3, C3, ROOK, PAWN, WHITE), Move.setCheck(Move.createCapture(C5, C3, ROOK, PAWN, WHITE)), });

		assertMovelist("6q1/k6n/8/2R5/3B4/p1n1R3/1K2N1R1/6r1 w - - 0 1", test_move_generator::generateRookCaptures, // Inescapable check
				new int[] {});

		// black
		assertMovelist("3R4/6r1/8/8/Q2r2P1/Br6/1P4K1/3k4 b - - 0 1", test_move_generator::generateRookCaptures, // two rooks, and check
				new int[] { Move.createCapture(D4, D8, ROOK, ROOK, BLACK), Move.setCheck(Move.createCapture(G7, G4, ROOK, PAWN, BLACK)), });

		assertMovelist("4b2q/p7/4p1k1/5p2/1P6/1KP3N1/4B1R1/8 b - - 0 1", test_move_generator::generateRookCaptures, // no rooks
				new int[] {});
	}

	@Test
	void testGenerateBishopMoves() {
		assertMovelist("8/6k1/8/2R5/8/6n1/5B2/1K6 w - - 0 1", test_move_generator::generateBishopMoves, // basic
				new int[] { Move.createNormal(F2, E1, BISHOP, WHITE), Move.createNormal(F2, G1, BISHOP, WHITE), Move.createNormal(F2, E3, BISHOP, WHITE),
						Move.setCheck(Move.createNormal(F2, D4, BISHOP, WHITE)), });

		assertMovelist("8/4r3/8/6B1/k1B5/8/8/4K3 w - - 0 1", test_move_generator::generateBishopMoves, // king in check
				new int[] { Move.createNormal(C4, E6, BISHOP, WHITE), Move.createNormal(C4, E2, BISHOP, WHITE), Move.createNormal(G5, E3, BISHOP, WHITE), });

		assertMovelist("1q6/k7/8/4B3/r1B2K2/8/8/8 w - - 0 1", test_move_generator::generateBishopMoves, // pins
				new int[] { Move.createNormal(E5, D6, BISHOP, WHITE), Move.createNormal(E5, C7, BISHOP, WHITE), });

		// black
		assertMovelist("2r2b1K/1b2p3/2p5/8/2k5/6P1/4PP2/8 b - - 0 1", test_move_generator::generateBishopMoves, // open check
				new int[] { Move.createNormal(B7, A8, BISHOP, BLACK), Move.createNormal(B7, A6, BISHOP, BLACK), Move.setCheck(Move.createNormal(F8, G7, BISHOP, BLACK)),
						Move.setCheck(Move.createNormal(F8, H6, BISHOP, BLACK)), });

		assertMovelist("7r/4bppp/5k2/5Q2/8/1P3b2/1KP5/R7 b - - 0 1", test_move_generator::generateBishopMoves, // inescapable check
				new int[] {});
	}

	@Test
	void testGenerateGenerateBishopCaptures() {
		assertMovelist("7k/n7/6K1/1pB5/1R6/3B4/2q1rn2/8 w - - 0 1", test_move_generator::generateBishopCaptures, // basic and open check
				new int[] { Move.createCapture(C5, A7, BISHOP, KNIGHT, WHITE), Move.createCapture(C5, F2, BISHOP, KNIGHT, WHITE), Move.createCapture(D3, C2, BISHOP, QUEEN, WHITE), });

		assertMovelist("7r/3p1p2/1B2B2k/8/8/1r2q3/5K2/8 w - - 0 1", test_move_generator::generateBishopCaptures, // king in check leading to check
				new int[] { Move.setCheck(Move.createCapture(B6, E3, BISHOP, QUEEN, WHITE)), });
		// black
		assertMovelist("6r1/2k2b2/3p4/4b3/8/1PP4K/P2P2p1/8 w - - 0 1", test_move_generator::generateBishopCaptures, // inescapable check
				new int[] {});
	}

	@Test
	void testGenerageQueenMoves() {
		assertMovelist("7k/8/1Q2r3/8/8/4n3/1b6/7K w - - 0 1", test_move_generator::generateQueenMoves, // basic and check
				new int[] { Move.createNormal(B6, A7, QUEEN, WHITE), Move.createNormal(B6, A6, QUEEN, WHITE), Move.createNormal(B6, A5, QUEEN, WHITE),
						Move.setCheck(Move.createNormal(B6, B8, QUEEN, WHITE)), Move.createNormal(B6, B7, QUEEN, WHITE), Move.createNormal(B6, B5, QUEEN, WHITE),
						Move.createNormal(B6, B4, QUEEN, WHITE), Move.createNormal(B6, B3, QUEEN, WHITE), Move.createNormal(B6, C7, QUEEN, WHITE),
						Move.setCheck(Move.createNormal(B6, D8, QUEEN, WHITE)), Move.createNormal(B6, C6, QUEEN, WHITE), Move.createNormal(B6, D6, QUEEN, WHITE),
						Move.createNormal(B6, C5, QUEEN, WHITE), Move.setCheck(Move.createNormal(B6, D4, QUEEN, WHITE)), });

		assertMovelist("7k/5ppp/2Q5/8/r5K1/8/4Q3/8 w - - 0 1", test_move_generator::generateQueenMoves, // two queens and king in check
				new int[] { Move.createNormal(C6, C4, QUEEN, WHITE), Move.createNormal(C6, E4, QUEEN, WHITE), Move.createNormal(E2, E4, QUEEN, WHITE),
						Move.createNormal(E2, C4, QUEEN, WHITE), });
		// black
		assertMovelist("8/8/R3qk2/8/3q4/8/1Q6/K7 b - - 0 1", test_move_generator::generateQueenMoves, // pins
				new int[] { Move.createNormal(E6, D6, QUEEN, BLACK), Move.createNormal(E6, C6, QUEEN, BLACK), Move.createNormal(E6, B6, QUEEN, BLACK),
						Move.createNormal(D4, E5, QUEEN, BLACK), Move.createNormal(D4, C3, QUEEN, BLACK), });
	}

	@Test
	void testGenerageQueenCaptures() {
		assertMovelist("1kq1n2Q/p6b/8/2Q5/3p4/3Q4/2K5/8 w - - 0 1", test_move_generator::generateQueenCaptures, // multiple queens and pins
				new int[] { Move.setCheck(Move.createCapture(C5, C8, QUEEN, QUEEN, WHITE)), Move.createCapture(D3, H7, QUEEN, BISHOP, WHITE),
						Move.createCapture(H8, E8, QUEEN, KNIGHT, WHITE), Move.createCapture(H8, D4, QUEEN, PAWN, WHITE), Move.createCapture(H8, H7, QUEEN, BISHOP, WHITE), });
		// black
		assertMovelist("K1R5/1P6/Pq5p/8/N2P4/3N4/2q2k2/8 b - - 0 1", test_move_generator::generateQueenCaptures, // king in check
				new int[] { Move.createCapture(C2, D3, QUEEN, KNIGHT, BLACK), });
	}

	@Test
	void testPawnMoves() {
		assertMovelist("1k5b/6P1/3r1P2/3P4/4p3/2p5/KPPPP3/8 w - - 0 1", test_move_generator::generatePawnMoves, // basic
				new int[] { Move.createDoublePush(B2, B4, WHITE), Move.createDoublePush(D2, D4, WHITE), Move.createNormal(F6, F7, PAWN, WHITE), Move.createNormal(E2, E3, PAWN, WHITE),
						Move.createNormal(D2, D3, PAWN, WHITE), Move.createNormal(B2, B3, PAWN, WHITE), });

		assertMovelist("8/8/8/6k1/8/7P/2K2P2/8 w - - 0 1", test_move_generator::generatePawnMoves, // declares check
				new int[] { Move.setCheck(Move.createDoublePush(F2, F4, WHITE)), Move.setCheck(Move.createNormal(H3, H4, PAWN, WHITE)), Move.createNormal(F2, F3, PAWN, WHITE), });

		assertMovelist("5n1k/4P3/8/2P5/1K4r1/3P4/PP3P1P/8 w - - 0 1", test_move_generator::generatePawnMoves, // in check - rook
				new int[] { Move.createDoublePush(F2, F4, WHITE), Move.createNormal(D3, D4, PAWN, WHITE), });

		assertMovelist("6k1/b2P4/4P3/5P2/2P5/1P4P1/3P1K2/8 w - - 0 1", test_move_generator::generatePawnMoves, // in check - bishop
				new int[] { Move.createDoublePush(D2, D4, WHITE), Move.createNormal(C4, C5, PAWN, WHITE), });

		assertMovelist("k7/5p2/P4P1b/8/p4P2/P6p/r1PK2PP/8 w - - 0 1", test_move_generator::generatePawnMoves, // pins
				new int[] { Move.createDoublePush(G2, G4, WHITE), Move.createNormal(G2, G3, PAWN, WHITE), Move.createNormal(A6, A7, PAWN, WHITE), });

		assertMovelist("8/1P6/1P2p2B/3p2P1/2p5/6Pp/Q1Pk1PRP/7K w - - 0 1", test_move_generator::generatePawnMoves, // open checks
				new int[] { Move.setCheck(Move.createDoublePush(F2, F4, WHITE)), Move.createNormal(G3, G4, PAWN, WHITE), Move.setCheck(Move.createNormal(C2, C3, PAWN, WHITE)),
						Move.setCheck(Move.createNormal(F2, F3, PAWN, WHITE)), Move.setCheck(Move.createNormal(G5, G6, PAWN, WHITE)), });
		// black
		assertMovelist("8/1pk2p1p/2p2P2/6pP/6K1/p4p2/5p2/8 b - - 0 1", test_move_generator::generatePawnMoves, // basic
				new int[] { Move.createDoublePush(B7, B5, BLACK), Move.createNormal(B7, B6, PAWN, BLACK), Move.createNormal(C6, C5, PAWN, BLACK), Move.createNormal(H7, H6, PAWN, BLACK),
						Move.createNormal(A3, A2, PAWN, BLACK), });

		assertMovelist("1k6/3p4/5p2/8/1rp1K3/8/8/8 b - - 0 1", test_move_generator::generatePawnMoves, // declares check
				new int[] { Move.setCheck(Move.createDoublePush(D7, D5, BLACK)), Move.setCheck(Move.createNormal(F6, F5, PAWN, BLACK)), Move.createNormal(D7, D6, PAWN, BLACK),
						Move.setCheck(Move.createNormal(C4, C3, PAWN, BLACK)), });

		assertMovelist("8/6p1/5p2/1p2k2R/2p5/3p4/4p3/6K1 b - - 0 1", test_move_generator::generatePawnMoves, // in check - rook
				new int[] { Move.createDoublePush(G7, G5, BLACK), Move.createNormal(F6, F5, PAWN, BLACK), });

		assertMovelist("8/4ppQ1/2p5/1p6/p7/2k3p1/8/6K1 b - - 0 1", test_move_generator::generatePawnMoves, // in check - bishop
				new int[] { Move.createDoublePush(E7, E5, BLACK), Move.createNormal(F7, F6, PAWN, BLACK), });

		assertMovelist("8/5kpR/8/3p4/8/1Q3p2/8/2K2Q2 b - - 0 1", test_move_generator::generatePawnMoves, // pins
				new int[] { Move.createNormal(F3, F2, PAWN, BLACK), });

		assertMovelist("b7/1p6/5P2/8/q1p1K3/8/6k1/8 b - - 0 1", test_move_generator::generatePawnMoves, // open checks
				new int[] { Move.setCheck(Move.createDoublePush(B7, B5, BLACK)), Move.setCheck(Move.createNormal(B7, B6, PAWN, BLACK)),
						Move.setCheck(Move.createNormal(C4, C3, PAWN, BLACK)), });
	}

	@Test
	void testGeneratePawnCaptures() {
		assertMovelist("2r5/1P5p/6p1/5k2/R1n3r1/1P1P1p1P/K1Q1P3/8 w - - 0 1", test_move_generator::generatePawnCaptures, // basic
				new int[] { Move.setCheck(Move.createCapture(D3, C4, PAWN, KNIGHT, WHITE)), Move.setCheck(Move.createCapture(H3, G4, PAWN, ROOK, WHITE)),
						Move.createCapture(B3, C4, PAWN, KNIGHT, WHITE), Move.createCapture(E2, F3, PAWN, PAWN, WHITE), });

		assertMovelist("6n1/1q3P2/R1Pk4/n3p3/1P1P4/B7/PP6/K7 w - - 0 1", test_move_generator::generatePawnCaptures, // declares check
				new int[] { Move.setCheck(Move.createCapture(C6, B7, PAWN, QUEEN, WHITE)), Move.setCheck(Move.createCapture(B4, A5, PAWN, KNIGHT, WHITE)),
						Move.setCheck(Move.createCapture(D4, E5, PAWN, PAWN, WHITE)), });

		assertMovelist("k7/8/1b6/P7/8/8/5K2/R7 w - - 0 1", test_move_generator::generatePawnCaptures, // starts in check
				new int[] { Move.setCheck(Move.createCapture(A5, B6, PAWN, BISHOP, WHITE)), });

		assertMovelist("1k1r4/7q/4n1b1/3P1P2/8/1p1K4/2P5/1q6 w - - 0 1", test_move_generator::generatePawnCaptures, // pins
				new int[] { Move.createCapture(F5, G6, PAWN, BISHOP, WHITE), });
		// black
		assertMovelist("8/2kpR1p1/1p2B1pB/Q1P5/3p4/4N3/5pK1/2R3N1 b - - 0 1", test_move_generator::generatePawnCaptures, // pins
				new int[] { Move.createCapture(B6, A5, PAWN, QUEEN, BLACK), Move.createCapture(G7, H6, PAWN, BISHOP, BLACK), Move.createCapture(D4, E3, PAWN, KNIGHT, BLACK), });

		assertMovelist("8/8/5k2/1pp3p1/Q2B3N/2K5/8/8 b - - 0 1", test_move_generator::generatePawnCaptures, // starts in check
				new int[] { Move.setCheck(Move.createCapture(C5, D4, PAWN, BISHOP, BLACK)), });
	}

	@Test
	void testGeneratePawnPromotions() {
		assertMovelist("1nn5/1P4P1/8/8/5k2/1K6/8/8 w - - 0 1", test_move_generator::generatePawnPromotionsAndCapturePromotions, // basic
				new int[] { Move.createPromo(G7, G8, ROOK, WHITE), Move.createPromo(G7, G8, KNIGHT, WHITE), Move.createPromo(G7, G8, BISHOP, WHITE), Move.createPromo(G7, G8, QUEEN, WHITE),
						Move.createCapturePromo(B7, C8, KNIGHT, ROOK, WHITE), Move.createCapturePromo(B7, C8, KNIGHT, KNIGHT, WHITE), Move.createCapturePromo(B7, C8, KNIGHT, BISHOP, WHITE),
						Move.createCapturePromo(B7, C8, KNIGHT, QUEEN, WHITE), });

		assertMovelist("2n1b3/3P2k1/8/1K6/8/1P1p3r/P1P3P1/8 w - - 0 1", test_move_generator::generatePawnPromotionsAndCapturePromotions, // pin and check
				new int[] { Move.createCapturePromo(D7, E8, BISHOP, ROOK, WHITE), Move.setCheck(Move.createCapturePromo(D7, E8, BISHOP, KNIGHT, WHITE)),
						Move.createCapturePromo(D7, E8, BISHOP, BISHOP, WHITE), Move.createCapturePromo(D7, E8, BISHOP, QUEEN, WHITE), });

		assertMovelist("1brq4/2P5/8/7k/4P3/2K2P2/1p6/8 w - - 0 1", test_move_generator::generatePawnPromotionsAndCapturePromotions, // blocked and pinned
				new int[] {});
		// black
		assertMovelist("8/1p4pK/2B5/7p/8/8/kpRp2p1/N1n1bQQ1 b - - 0 1", test_move_generator::generatePawnPromotionsAndCapturePromotions, // mixed
				new int[] { Move.createPromo(D2, D1, ROOK, BLACK), Move.createPromo(D2, D1, KNIGHT, BLACK), Move.createPromo(D2, D1, BISHOP, BLACK), Move.createPromo(D2, D1, QUEEN, BLACK),
						Move.createCapturePromo(G2, F1, QUEEN, ROOK, BLACK), Move.createCapturePromo(G2, F1, QUEEN, KNIGHT, BLACK), Move.createCapturePromo(G2, F1, QUEEN, BISHOP, BLACK),
						Move.createCapturePromo(G2, F1, QUEEN, QUEEN, BLACK), });
	}

	@Test
	void testGenerateLegalMoves() {

		assertMovelistLength("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 20);// starting position

		assertMovelistLength("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 48);// Kiwipete position

		assertMovelistLength("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 44);
		
		//verified with Python Chess
		assertMovelistLength("r3k2r/2p2ppp/1pn3B1/Q1b5/p1pPpqb1/PN3N1P/6P1/R1B1K2R b KQkq d3 0 1", 60);//en passant and castling
		
		assertMovelistLength("6r1/1p2p3/2p2p1p/r4bN1/2kbQ2K/1n3P2/2pnqpP1/1BR1R3 b - - 0 1", 63);//pins and promotions
		
		assertMovelistLength("3rr3/2N1PPpp/5Nk1/PpPq4/1bQ2b2/2B3P1/5P1P/R3K2R w KQ b6 0 1", 55);//promotions and castling
		
		assertMovelistLength("r3k2r/pppN2pp/8/8/8/8/PPP2PPP/R3K2R b KQkq - 0 1", 20);//queen side castling condition
		
		assertMovelistLength("8/1K6/8/4k3/4nn2/6Q1/8/4R3 b - - 0 1", 6);//pinned knights
		
	}
}
