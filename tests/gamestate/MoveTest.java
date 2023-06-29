package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.BitField32;

class MoveTest {

	@Test
	void testBitManipulation() {
		int move = 0;
		move = BitField32.setBits(move, 5, 10, 3);
		assertEquals(5, BitField32.getBits(move, 10, 3));
		move = BitField32.setBits(move, 14, 0, 5);
		assertEquals(14, BitField32.getBits(move, 0, 5));
		assertEquals(5134, move);// this tests that setBits does not clear out values outside the range of its
									// own argument. In other words, setBits is additive.

		move = BitField32.setBits(move, 0, 0, 5);// this undoes the second call to setBits. Tests that passing a zero argument
		// acts as clearBits call.
		assertEquals(5, BitField32.getBits(move, 10, 3));

		move = ~0;
		assertEquals(0xFF, BitField32.getBits(move, 0, 8));
		assertEquals(0xFF, BitField32.getBits(move, 20, 8));

		move = BitField32.setBits(move, 0xE4, 10, 8);
		assertEquals(0xE4, BitField32.getBits(move, 10, 8));
		assertEquals(0xFF, BitField32.getBits(move, 20, 8));
		assertNotEquals(0xFF, BitField32.getBits(move, 14, 8));

		assertEquals(0xFF, BitField32.getBits(move, 22, 8));
		assertNotEquals(0xFF, BitField32.getBits(move, 13, 8));

		move = 0;
		move = BitField32.setBoolean(move, true, 0);
		move = BitField32.setBoolean(move, true, 2);
		assertEquals(5, move);
		assertEquals(true, BitField32.getBoolean(move, 0));
		assertEquals(false, BitField32.getBoolean(move, 1));
		assertEquals(true, BitField32.getBoolean(move, 2));
		assertEquals(false, BitField32.getBoolean(move, 3));

		move = BitField32.setBoolean(move, false, 0);
		assertEquals(false, BitField32.getBoolean(move, 0));
		assertEquals(false, BitField32.getBoolean(move, 1));
		assertEquals(true, BitField32.getBoolean(move, 2));
		assertEquals(false, BitField32.getBoolean(move, 3));

	}

	@Test
	void testGettersSetters() {
		// strategy to check that fields do not overlap:
		// set field to an 0xfff value. Test the value. Test the values of the fields
		// before and after the one being set.
		int move;
		// test SquareFrom
		move = 0;
		move = Move.setSquareFrom(move, GlobalConstants.Square.H8);
		assertEquals(GlobalConstants.Square.H8, Move.getSquareFrom(move));
		assertEquals(0, Move.getSquareTo(move));

		// test SquareTo
		move = 0;
		move = Move.setSquareTo(move, GlobalConstants.Square.H8);
		assertEquals(GlobalConstants.Square.H8, Move.getSquareTo(move));
		assertEquals(0, Move.getSquareFrom(move));
		assertEquals(0, Move.getPieceType(move));

		// test PieceType
		move = 0;
		move = Move.setPieceType(move, GlobalConstants.PieceType.KING);
		assertEquals(GlobalConstants.PieceType.KING, Move.getPieceType(move));
		assertEquals(0, Move.getSquareTo(move));
		assertEquals(0, Move.getPieceCapturedType(move));

		// test PieceCaptured
		move = 0;
		move = Move.setPieceCapturedType(move, GlobalConstants.PieceType.QUEEN);
		assertEquals(GlobalConstants.PieceType.QUEEN, Move.getPieceCapturedType(move));
		assertEquals(0, Move.getPieceType(move));
		assertEquals(0, Move.getPiecePromotedType(move));

		// test PiecePromoted
		move = 0;
		move = Move.setPiecePromotedType(move, GlobalConstants.PieceType.QUEEN);
		assertEquals(GlobalConstants.PieceType.QUEEN, Move.getPiecePromotedType(move));
		assertEquals(0, Move.getPieceType(move));
		assertEquals(0, Move.getMoveType(move));

		// test MoveType
		move = 0;
		move = Move.setMoveType(move, GlobalConstants.MoveType.DOUBLE_PUSH);
		assertEquals(GlobalConstants.MoveType.DOUBLE_PUSH, Move.getMoveType(move));
		assertEquals(0, Move.getPiecePromotedType(move));


		// test get player
		move = 0;
		move = Move.setPlayer(move, Player.BLACK);
		assertEquals(Player.BLACK, Move.getPlayer(move));
		assertEquals(false, Move.getCheck(move));

		// test check
		move = 0;
		move = Move.setCheck(move, true);
		assertEquals(true, Move.getCheck(move));
		assertEquals(0, Move.getPlayer(move));
	}

	@Test
	void testMoveToString() {
		assertEquals("b1", Square.squareToAlgebraicString(Square.B1));
		int move = Move.createCapture(Square.E7, Square.G6, PieceType.KNIGHT, PieceType.QUEEN, Player.WHITE);
		assertEquals("e7xg6", Move.moveToString(move));
		move = Move.createNormal(Square.E1, Square.E2, PieceType.KING, Player.WHITE);
		assertEquals("e1-e2", Move.moveToString(move));
	}
	
	@Test
	void testToUCINotation() {
		int move = Move.setCheck( Move.createCapture(Square.E7, Square.G6, PieceType.KNIGHT, PieceType.QUEEN, Player.WHITE));
		assertEquals("e7g6", Move.toUCINotation(move));
		
		move = Move.setCheck( Move.createCapturePromo(Square.D2, Square.C1, PieceType.KNIGHT, PieceType.ROOK, Player.BLACK));
		assertEquals("d2c1r", Move.toUCINotation(move));
		
		move = Move.setCheck( Move.createPromo(Square.D2, Square.D1, PieceType.KNIGHT, Player.BLACK));
		assertEquals("d2d1n", Move.toUCINotation(move));
		
		move = Move.createEnpassant(Square.B5, Square.C4, Player.WHITE);
		assertEquals("b5c4", Move.toUCINotation(move));
		
		move = Move.createNormal(Square.B5, Square.C4, PieceType.QUEEN, Player.WHITE);
		assertEquals("b5c4", Move.toUCINotation(move));
		
		move = Move.createDoublePush(Square.E7, Square.E5, Player.BLACK);
		assertEquals("e7e5", Move.toUCINotation(move));
		
		move = Move.createCastleKing(Player.WHITE);
		assertEquals("e1g1", Move.toUCINotation(move));
		
		move = Move.createCastleQueen(Player.WHITE);
		assertEquals("e1c1", Move.toUCINotation(move));
		
		move = Move.createCastleKing(Player.BLACK);
		assertEquals("e8g8", Move.toUCINotation(move));
		
		move = Move.createCastleQueen(Player.BLACK);
		assertEquals("e8c8", Move.toUCINotation(move));
	}

	void assertBoardLocation(Gamestate brd, int player, int piece, int sq) {
		assertEquals(player, brd.getPlayerAt(sq));
		assertEquals(piece, brd.getPieceAt(sq));
	}

	void makeDirtyMoveHelper(Gamestate brd, int move) {
		try {
			Method method = Gamestate.class.getDeclaredMethod("makeDirtyMove", int.class);
			method.setAccessible(true);
			method.invoke(brd, move);
		} catch (Exception e) {

		}
	}

	void unmakeDirtyMoveHelper(Gamestate brd, int move) {
		try {
			Method method = Gamestate.class.getDeclaredMethod("unmakeDirtyMove", int.class);
			method.setAccessible(true);
			method.invoke(brd, move);
		} catch (Exception e) {

		}
	}

	@Test
	void testDirtyMove_whtie() {
		// White
		Gamestate brd = new Gamestate("r1b5/pPp1N1k1/3p1pqr/2pPpP1Q/4P3/2p1B2p/6P1/R3K2R w KQ e6 17 30");
		assertEquals(Player.WHITE, brd.getPlayerToMove());
		assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.A8);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.B7);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.B8);
		assertBoardLocation(brd, Player.BLACK, PieceType.QUEEN, Square.G6);
		assertBoardLocation(brd, Player.WHITE, PieceType.KNIGHT, Square.E7);
		assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E2);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.G2);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G4);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E6);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.D5);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E5);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F1);
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.H1);
		assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G1);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.C1);
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.A1);
		assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D1);
		assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

		// capture_promo
		{
			int move = Move.createCapturePromo(Square.B7, Square.A8, PieceType.ROOK, PieceType.QUEEN, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.QUEEN, Square.A8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.B7);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.A8);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.B7);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// promo
		{
			int move = Move.createPromo(Square.B7, Square.B8, PieceType.ROOK, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.B8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.B7);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.B8);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.B7);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// capture
		{
			int move = Move.createCapture(Square.E7, Square.G6, PieceType.KNIGHT, PieceType.QUEEN, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.KNIGHT, Square.G6);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E7);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.QUEEN, Square.G6);
			assertBoardLocation(brd, Player.WHITE, PieceType.KNIGHT, Square.E7);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// en passant
		{
			int move = Move.createEnpassant(Square.D5, Square.E6, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.E6);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D5);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E5);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E6);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.D5);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E5);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// castle king
		{
			int move = Move.createCastleKing(Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E1);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.F1);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.G1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.H1);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.G1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F1);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.H1);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G1);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// castle queen
		{
			int move = Move.createCastleQueen(Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E1);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.D1);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.C1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.A1);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.C1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.C1);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.A1);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D1);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// normal
		{
			int move = Move.createNormal(Square.E1, Square.E2, PieceType.KING, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E2);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E1);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E2, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E2);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		// double_push
		{
			int move = Move.createDoublePush(Square.G2, Square.G4, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.G4);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G2);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.G2);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G4);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E1, brd.getKingSquare(Player.WHITE));
		}
		
		//king capture
		brd = new Gamestate("8/8/4k3/n7/K7/8/8/8 w - - 0 1");
		{
			int move = Move.createCapture(Square.A4, Square.A5, PieceType.KING, PieceType.KNIGHT, Player.WHITE);

			makeDirtyMoveHelper(brd, move);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.A5, brd.getKingSquare(Player.WHITE));

			unmakeDirtyMoveHelper(brd, move);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.A4, brd.getKingSquare(Player.WHITE));
		}
	}

	@Test
	void testDirtyMove_black() {
		Gamestate brd = new Gamestate("r3k2r/pp3pp1/1np2q2/8/2P2Pp1/1KQP3P/4p3/5R2 b kq f3 5 11");
		assertEquals(Player.BLACK, brd.getPlayerToMove());
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.F1);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E2);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E1);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E2);
		assertBoardLocation(brd, Player.BLACK, PieceType.QUEEN, Square.F6);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.F4);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.F4);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.G4);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F8);
		assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.H8);
		assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G8);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.C8);
		assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.A8);
		assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D8);
		assertBoardLocation(brd, Player.BLACK, PieceType.KNIGHT, Square.B6);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D5);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.A7);
		assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.A5);
		assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

		// capture_promo
		{
			int move = Move.createCapturePromo(Square.E2, Square.F1, PieceType.ROOK, PieceType.BISHOP, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.BISHOP, Square.F1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E2);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.F1);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E2);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// promo
		{
			int move = Move.createPromo(Square.E2, Square.E1, PieceType.KNIGHT, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.KNIGHT, Square.E1);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E2);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E1);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E2);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// capture
		{
			int move = Move.createCapture(Square.F6, Square.F4, PieceType.QUEEN, PieceType.PAWN, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.QUEEN, Square.F4);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F6);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.QUEEN, Square.F6);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.F4);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// en passant
		{
			int move = Move.createEnpassant(Square.G4, Square.F3, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.F3);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F4);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G4);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F3);
			assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.F4);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.G4);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// castle king
		{
			int move = Move.createCastleKing(Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E8);
			assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.F8);
			assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.G8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.H8);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.G8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.F8);
			assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.H8);
			assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.G8);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// castle queen
		{
			int move = Move.createCastleQueen(Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.E8);
			assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.D8);
			assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.C8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.A8);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.C8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.C8);
			assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.A8);
			assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D8);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// normal
		{
			int move = Move.createNormal(Square.B6, Square.D5, PieceType.KNIGHT, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.KNIGHT, Square.D5);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.B8);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.KNIGHT, Square.B6);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.D5);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		// double_push
		{
			int move = Move.createDoublePush(Square.A7, Square.A5, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.A5);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.A7);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.A7);
			assertBoardLocation(brd, Player.NO_PLAYER, PieceType.NO_PIECE, Square.A5);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.E8, brd.getKingSquare(Player.BLACK));
		}
		
		//king capture
		brd = new Gamestate("8/7k/7P/8/8/6K1/8/8 b - - 0 1");
		{
			int move = Move.createCapture(Square.H7, Square.H6, PieceType.KING, PieceType.PAWN, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.H6, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.H7, brd.getKingSquare(Player.BLACK));
		}
		{
			int move = Move.createNormal(Square.H7, Square.H8, PieceType.KING, Player.BLACK);

			makeDirtyMoveHelper(brd, move);
			assertEquals(Player.WHITE, brd.getPlayerToMove());
			assertEquals(Square.H8, brd.getKingSquare(Player.BLACK));

			unmakeDirtyMoveHelper(brd, move);
			assertEquals(Player.BLACK, brd.getPlayerToMove());
			assertEquals(Square.H7, brd.getKingSquare(Player.BLACK));
		}
	}

}
