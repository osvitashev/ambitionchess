package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

	@Test
	void testGetOccupied() {
		Gamestate brd = new Gamestate();
		assertEquals(0xffff00000000ffffL, brd.getOccupied());
	}

	@Test
	void testGetEmpty() {
		Gamestate brd = new Gamestate();
		assertEquals(0xffffffff0000L, brd.getEmpty());
	}

	@Test
	void testGetPieces() {
		assertEquals(0x852a501048a700L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.PAWN));
		assertEquals(0x4100000000000048L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.ROOK));
		assertEquals(0x8400000000000048L, new Gamestate("2r1k2r/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w k - 0 1").getPieces(PieceType.ROOK));// test
																																						// rook
																																						// on
																																						// cursed
																																						// h8
		assertEquals(0x1000000000000004L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.KING));
		assertEquals(0x8800000200000L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.KNIGHT));
		assertEquals(0x2000000004800L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.BISHOP));
		assertEquals(0x10000000001000L, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPieces(PieceType.QUEEN));
	}

	@Test
	void testGetPlayerPieces() {
		assertEquals(0x949faa5000000000L, new Gamestate("2r1k2r/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w k - 0 1").getPlayerPieces(Player.BLACK));
		assertEquals(0x1068ff4cL, new Gamestate("r3k1r1/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/2KR2R1 w q - 0 1").getPlayerPieces(Player.WHITE));
	}

	@Test
	void testSquareComposition() {
		assertEquals(64, Square.SQUARES.length);
		for (int i = 0; i < Square.SQUARES.length; ++i)
			assertEquals(i, Square.SQUARES[i]);
	}

	@Test
	void testBitAccess() {
		Gamestate brd = new Gamestate("2r1k2r/pbpnq2p/3p3n/1p2p1p1/4P3/1P1PQNP1/P1P3BP/1R1K2R1 w k - 0 1");
		assertEquals(Player.WHITE, brd.getPlayerAt(Square.D3));
		assertEquals(Player.BLACK, brd.getPlayerAt(Square.H8));
		assertEquals(Player.NO_PLAYER, brd.getPlayerAt(Square.E6));

		assertEquals(PieceType.PAWN, brd.getPieceAt(Square.C7));
		assertEquals(PieceType.ROOK, brd.getPieceAt(Square.G1));
		assertEquals(PieceType.KNIGHT, brd.getPieceAt(Square.H6));
		assertEquals(PieceType.BISHOP, brd.getPieceAt(Square.B7));
		assertEquals(PieceType.QUEEN, brd.getPieceAt(Square.E3));
		assertEquals(PieceType.KING, brd.getPieceAt(Square.E8));
		assertEquals(PieceType.NO_PIECE, brd.getPieceAt(Square.A1));

		try {
			Method method = Gamestate.class.getDeclaredMethod("clearPieceAt", int.class, int.class);
			method.setAccessible(true);
			method.invoke(brd, PieceType.QUEEN, Square.E3);// brd.clearPieceAt(Square.E3);
			// method.invoke(brd, Square.A1);// brd.clearPieceAt(Square.A1);
		} catch (Exception e) {

		}
		assertEquals(PieceType.NO_PIECE, brd.getPieceAt(Square.E3));
		// assertEquals(PieceType.NO_PIECE, brd.getPieceAt(Square.A1));
		// 2r1k2r/pbpnq2p/3p3n/1p2p1p1/4P3/1P1P1NP1/P1P3BP/1R1K2R1 w KQkq - 0 1

		try {
			Method method = Gamestate.class.getDeclaredMethod("putPieceAt", int.class, int.class, int.class);
			method.setAccessible(true);
			method.invoke(brd, PieceType.QUEEN, Player.WHITE, Square.A1);// brd.putPieceAt(PieceType.QUEEN, Player.WHITE, Square.A1);
			method.invoke(brd, PieceType.PAWN, Player.BLACK, Square.F7);// brd.putPieceAt(PieceType.PAWN, Player.BLACK, Square.F7);
		} catch (Exception e) {

		}

		// 2r1k2r/pbpnqp1p/3p3n/1p2p1p1/4P3/1P1P1NP1/P1P3BP/QR1K2R1 w KQkq - 0 1
		assertTrue(brd.testPieceAt(PieceType.QUEEN, Player.WHITE, Square.A1));
		assertTrue(brd.testPieceAt(PieceType.PAWN, Player.BLACK, Square.F7));

		assertFalse(brd.testPieceAt(PieceType.QUEEN, Player.BLACK, Square.A1));
		assertFalse(brd.testPieceAt(PieceType.KING, Player.WHITE, Square.A1));
		assertFalse(brd.testPieceAt(PieceType.QUEEN, Player.WHITE, Square.B1));

	}

	@Test
	void testToString() {
		Gamestate brd = new Gamestate("8/4b3/4P3/1k4P1/8/ppK5/8/4R3 b - - 1 45");
		String ans = new String("   a |  b |  c |  d |  e |  f |  g |  h \r\n" + "-------------------------------------------\r\n" + "8| . |  . |  . |  . |  . |  . |  . |  . | 8\r\n"
				+ "-------------------------------------------\r\n" + "7| . |  . |  . |  . |  b |  . |  . |  . | 7\r\n" + "-------------------------------------------\r\n"
				+ "6| . |  . |  . |  . |  P |  . |  . |  . | 6\r\n" + "-------------------------------------------\r\n" + "5| . |  k |  . |  . |  . |  . |  P |  . | 5\r\n"
				+ "-------------------------------------------\r\n" + "4| . |  . |  . |  . |  . |  . |  . |  . | 4\r\n" + "-------------------------------------------\r\n"
				+ "3| p |  p |  K |  . |  . |  . |  . |  . | 3\r\n" + "-------------------------------------------\r\n" + "2| . |  . |  . |  . |  . |  . |  . |  . | 2\r\n"
				+ "-------------------------------------------\r\n" + "1| . |  . |  . |  . |  R |  . |  . |  . | 1\r\n" + "-------------------------------------------\r\n"
				+ "   a |  b |  c |  d |  e |  f |  g |  h " + "");
		System.out.println(brd.toString());
		assertEquals(ans, brd.toString());
	}

	@Test
	void testClear() {
		Gamestate brd = new Gamestate("2r1k2r/pbpnq2p/1p1p1p1n/4p1p1/4P3/3P1NP1/PPPBQPBP/1R1K2R1 w k g6 5 3");
		try {
			Method method = Gamestate.class.getDeclaredMethod("clear");
			method.setAccessible(true);
			method.invoke(brd);// brd.clear();
		} catch (Exception e) {

		}

		assertEquals(0, brd.getPieces(PieceType.PAWN));
		assertEquals(0, brd.getPieces(PieceType.ROOK));
		assertEquals(0, brd.getPieces(PieceType.KING));
		assertEquals(0, brd.getPieces(PieceType.KNIGHT));
		assertEquals(0, brd.getPieces(PieceType.BISHOP));
		assertEquals(0, brd.getPieces(PieceType.QUEEN));
		assertEquals(0, brd.getPlayerPieces(Player.BLACK));
		assertEquals(0, brd.getPlayerPieces(Player.WHITE));

		assertEquals(Player.NO_PLAYER, brd.getPlayerToMove());
		assertEquals(0, brd.getQuietHalfmoveClock());
		assertEquals(0, brd.getGamePlyCount());

		assertEquals(false, brd.getCastling_BK());
		assertEquals(false, brd.getCastling_BQ());
		assertEquals(false, brd.getCastling_WK());
		assertEquals(false, brd.getCastling_WQ());
		assertEquals(Square.SQUARE_NONE, brd.getEnpassantSquare());

		brd = new Gamestate("8/1kr5/8/8/8/2K5/8/8 w - - 0 1");
		assertEquals(Square.C3, brd.getKingSquare(Player.WHITE));
		assertEquals(Square.B7, brd.getKingSquare(Player.BLACK));
		assertEquals(true, brd.getIsCheck());
		try {
			Method method = Gamestate.class.getDeclaredMethod("clear");
			method.setAccessible(true);
			method.invoke(brd);// brd.clear();
		} catch (Exception e) {

		}
		assertEquals(false, brd.getIsCheck());
		assertEquals(0, brd.getKingSquare(Player.WHITE));
		assertEquals(0, brd.getKingSquare(Player.BLACK));
	}

	@Test
	void testLoadFromFEN() {
		Gamestate brd = new Gamestate("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		assertEquals(0, brd.getQuietHalfmoveClock());
		assertEquals(0, brd.getGamePlyCount());

		brd = new Gamestate("rnbqkbnr/pppppppp/8/8/8/2N5/PPPPPPPP/R1BQKBNR b KQkq - 1 1");
		assertEquals(1, brd.getQuietHalfmoveClock());
		assertEquals(1, brd.getGamePlyCount());

		brd = new Gamestate("rnbqkbnr/ppp1pppp/8/3p4/8/2N5/PPPPPPPP/R1BQKBNR w KQkq - 0 2");
		assertEquals(0, brd.getQuietHalfmoveClock());
		assertEquals(2, brd.getGamePlyCount());

		brd = new Gamestate("rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 1");
		assertEquals(Player.WHITE, brd.getPlayerToMove());
		assertEquals(true, brd.getCastling_WK());
		assertEquals(true, brd.getCastling_WQ());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BQ());
		assertEquals(Square.SQUARE_NONE, brd.getEnpassantSquare());
		assertEquals(4, brd.getQuietHalfmoveClock());
		assertEquals(0, brd.getGamePlyCount());
		assertEquals(false, brd.getIsCheck());

		brd.loadFromFEN("rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 b kq - 5 1");
		assertEquals(Player.BLACK, brd.getPlayerToMove());
		assertEquals(false, brd.getCastling_WK());
		assertEquals(false, brd.getCastling_WQ());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BQ());
		assertEquals(Square.SQUARE_NONE, brd.getEnpassantSquare());
		assertEquals(5, brd.getQuietHalfmoveClock());
		assertEquals(1, brd.getGamePlyCount());
		assertEquals(false, brd.getIsCheck());

		brd.loadFromFEN("rnbq1rk1/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 w - d6 11 32");
		assertEquals(Player.WHITE, brd.getPlayerToMove());
		assertEquals(false, brd.getCastling_WK());
		assertEquals(false, brd.getCastling_WQ());
		assertEquals(false, brd.getCastling_BK());
		assertEquals(false, brd.getCastling_BQ());
		assertEquals(Square.D6, brd.getEnpassantSquare());
		assertEquals(11, brd.getQuietHalfmoveClock());
		assertEquals(62, brd.getGamePlyCount());
		assertEquals(false, brd.getIsCheck());

		brd.loadFromFEN("r6r/4k1pp/1pp2p2/p7/1Q4P1/1P3P1P/P1PKP1B1/8 b - - 2 43");
		assertEquals(true, brd.getIsCheck());
		assertEquals(Square.D2, brd.getKingSquare(Player.WHITE));
		assertEquals(Square.E7, brd.getKingSquare(Player.BLACK));
		
		brd.loadFromFEN("8/8/8/8/8/1k6/1pp5/1K6 w - - 0 76");
		assertEquals(true, brd.getIsCheck());

		brd.loadFromFEN("8/3k4/2P5/8/4K3/8/2R5/8 b - - 0 19");
		assertEquals(true, brd.getIsCheck());
	}

	@Test
	void testGetFen() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		Gamestate brd = new Gamestate(fen);
		assertEquals(fen, brd.toFEN());

		fen = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQK2R w KQkq - 4 1";
		brd = new Gamestate(fen);
		assertEquals(fen, brd.toFEN());

		fen = "rnbqk2r/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 b kq - 5 1";
		brd = new Gamestate(fen);
		assertEquals(fen, brd.toFEN());

		fen = "rnbq1rk1/ppp1b1pp/5p1n/3p4/8/3B1N2/PPPP1PPP/RNBQ1RK1 w - d6 11 32";
		brd = new Gamestate(fen);
		assertEquals(fen, brd.toFEN());

		fen = "r6r/4k1pp/1pp2p2/p7/1Q4P1/1P3P1P/P1PKP1B1/8 b - - 2 43";
		brd = new Gamestate(fen);
		assertEquals(fen, brd.toFEN());
	}

	void assertThrowsOnLoad(String fen) {
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> new Gamestate(fen), "Expected a throw, but it didn't");
		assertTrue(thrown.getMessage().equals("Invalid Game State!"));
	}

	@Test
	void testIsValid() {
		// tests missing/extra kings and kings being too close to each other.
		assertThrowsOnLoad("8/8/5n2/8/4P3/8/8/1Kk5 w - - 0 1");
		assertThrowsOnLoad("8/8/5n2/8/4P3/8/8/1Kk5 b - - 0 1");
		
		assertThrowsOnLoad("8/8/8/8/8/8/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/8/8/8/8/8 b - - 0 1");
		
		assertThrowsOnLoad("8/8/8/8/8/8/3K4/8 w - - 0 1");
		assertThrowsOnLoad("8/8/2k5/8/5K2/8/3K4/8 w - - 0 1");
		assertThrowsOnLoad("8/8/2k5/8/5K2/8/3K4/8 w - - 0 1");
		assertThrowsOnLoad("8/8/2k5/8/8/8/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/2k5/5k2/8/8/8/8 w - - 0 1");
		
		assertThrowsOnLoad("8/8/2k5/5k2/8/2K5/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/8/3k4/3K4/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/8/4k3/3K4/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/8/8/3Kk3/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/4K3/5k2/8/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/8/4K3/4k3/8/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/7K/6k1/8/8/8/8 w - - 0 1");
		assertThrowsOnLoad("6kK/8/8/8/8/8/8/8 w - - 0 1");
		assertThrowsOnLoad("8/8/6k1/7K/8/8/8/8 w - - 0 1");
		assertEquals(true, new Gamestate("8/8/8/8/8/6k1/8/7K w - - 0 1").validateKingExposure());
		assertEquals(true, new Gamestate("8/2K5/k7/8/8/8/8/8 w - - 0 1").validateKingExposure());
		// king en prise
		assertThrowsOnLoad("8/8/5n2/3k4/4P3/6K1/8/8 w - - 0 1");
		assertEquals(true, new Gamestate("8/8/5n2/3k4/4P3/6K1/8/8 b - - 0 1").validateKingExposure());
		assertThrowsOnLoad("8/6r1/2P2n2/3k4/4N3/6K1/7P/8 b - - 0 1");
		assertEquals(true, new Gamestate("8/6r1/2P2n2/3k4/4N3/6K1/7P/8 w - - 0 1").validateKingExposure());
		assertThrowsOnLoad("8/5r2/2P2n2/3k4/4N3/1B4K1/7P/8 w - - 0 1");
		assertEquals(true, new Gamestate("8/5r2/2P2n2/3k4/4N3/1B4K1/7P/8 b - - 0 1").validateKingExposure());
		assertThrowsOnLoad("8/4kr2/2P5/5n2/4N3/6K1/7P/8 b - - 0 1");
		assertEquals(true, new Gamestate("8/4kr2/2P5/5n2/4N3/6K1/7P/8 w - - 0 1").validateKingExposure());
		assertThrowsOnLoad("8/4kr2/2P5/3n2Q1/4N3/6K1/7P/8 w - - 0 1");
		assertEquals(true, new Gamestate("8/4kr2/2P5/3n2Q1/4N3/6K1/7P/8 b - - 0 1").validateKingExposure());
	}

	@Test
	void testIsSquareAttackedBy() {
		// king
		assertEquals(true, new Gamestate("8/1k6/5n2/4K3/8/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.WHITE));
		assertEquals(true, new Gamestate("8/6K1/5n2/5k2/8/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.WHITE));
		assertEquals(false, new Gamestate("8/8/2K2nk1/8/8/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.WHITE));
		assertEquals(true, new Gamestate("8/8/2K2nk1/8/8/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.BLACK));
		assertEquals(true, new Gamestate("8/4K3/5nk1/8/8/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.BLACK));
		assertEquals(false, new Gamestate("8/4K3/5n2/8/7k/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.F6, Player.BLACK));
		// pawns
		assertEquals(false, new Gamestate("8/1k6/8/1P6/4K3/8/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.WHITE));
		assertEquals(false, new Gamestate("8/1k6/8/3P4/4K3/8/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.WHITE));
		assertEquals(true, new Gamestate("8/1k6/8/8/4K3/3P4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.WHITE));
		assertEquals(true, new Gamestate("8/1k6/8/8/4K3/1P6/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.WHITE));
		assertEquals(true, new Gamestate("8/1k6/8/3pK3/8/8/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.BLACK));
		assertEquals(true, new Gamestate("8/1k6/8/1p2K3/8/8/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.BLACK));
		assertEquals(false, new Gamestate("8/1k6/8/3PK3/8/1p6/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.BLACK));
		assertEquals(false, new Gamestate("8/1k6/8/1P2K3/8/3p4/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.C4, Player.BLACK));
		// rook
		assertEquals(true, new Gamestate("8/8/1R1N3k/8/8/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("3R4/8/3N2rk/8/8/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(false, new Gamestate("4R3/8/3N2rk/8/8/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("8/8/3N2rk/8/8/8/1K6/3R4 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(true, new Gamestate("8/8/3N2rk/8/8/8/1K2R3/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(false, new Gamestate("8/6r1/2RN3k/8/8/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		// bishop
		assertEquals(true, new Gamestate("8/8/3N3k/8/8/6B1/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("8/4b3/3N3k/8/1B6/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(false, new Gamestate("1b6/8/3N3k/8/2B5/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("1b6/8/3N3k/8/2B5/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(true, new Gamestate("8/4B3/3N3k/8/1b6/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(false, new Gamestate("8/2B5/3N3k/1b6/8/8/1K6/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		// queen
		assertEquals(true, new Gamestate("8/7k/1Q1n4/8/8/7K/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("8/7k/3n4/8/8/Q6K/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(false, new Gamestate("1q6/7k/3n4/8/Q7/7K/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("1q6/7k/3n4/8/Q7/7K/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(true, new Gamestate("8/7k/3n1q2/8/Q7/7K/8/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(false, new Gamestate("6q1/7k/3n4/8/8/7K/8/3Q4 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		// knight
		assertEquals(true, new Gamestate("6k1/8/3n4/8/4N3/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("2n3k1/8/3n4/8/4N3/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(false, new Gamestate("2n3k1/8/3n4/4N3/8/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.WHITE));
		assertEquals(true, new Gamestate("6k1/8/3n4/1n2N3/8/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(true, new Gamestate("6k1/8/3n4/1n6/2N5/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
		assertEquals(false, new Gamestate("6k1/8/3n4/2n5/2N5/8/6K1/8 w - - 0 1").calculateIsSquareAttackedBy(Square.D6, Player.BLACK));
	}
	
	@Test
	void testCalculateSquareAttackers() {
		assertEquals(0x42020000L, new Gamestate("8/2q2k2/8/1P1p4/1Kn3R1/1P1p4/8/8 w - - 0 1").calculateSquareAttackers(Square.C4, Player.WHITE));
		assertEquals(0x4000800000000L, new Gamestate("8/2q2k2/8/1P1p4/1Kn3R1/1P1p4/8/8 w - - 0 1").calculateSquareAttackers(Square.C4, Player.BLACK));
		assertEquals(0x80000800400L, new Gamestate("5r2/7b/3N4/5pk1/7n/7B/2Q5/1K6 w - - 0 1").calculateSquareAttackers(Square.F5, Player.WHITE));
		assertEquals(0x2080004080000000L, new Gamestate("5r2/7b/3N4/5pk1/7n/7B/2Q5/1K6 w - - 0 1").calculateSquareAttackers(Square.F5, Player.BLACK));
	}
	
	@Test
	void testCalculatePinsSkewersAndDiscoveredAttacks() {
		//rooks
		assertEquals(0x8000000L, new Gamestate("6r1/8/6n1/8/2RK2k1/8/8/8 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.G4, Player.WHITE));
		assertEquals(0x4000000L, new Gamestate("k5r1/pp4R1/8/8/1Rr3n1/8/PP4Q1/K7 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.G4, Player.WHITE));
		assertEquals(0x40000000400000L, new Gamestate("k5R1/pp4R1/8/8/q1r3n1/6P1/PP4Q1/K7 b - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.G4, Player.WHITE));
		assertEquals(0x80000000800000L, new Gamestate("k6R/pp5R/8/8/q1r4n/7P/PP5Q/K7 b - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.H4, Player.WHITE));
		assertEquals(0x12000000000L, new Gamestate("r6k/6pp/p7/P4bq1/8/N7/Q5PP/7K w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.A5, Player.BLACK));
		assertEquals(0x10000010000L, new Gamestate("7k/r5pp/P7/P4bQ1/8/N7/q5PP/7K w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.A5, Player.BLACK));
		assertEquals(0x800000000L, new Gamestate("7k/r5pp/8/P2rqbQ1/8/8/6PP/7K w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.A5, Player.BLACK));
		assertEquals(0x0L, new Gamestate("7k/r5pp/8/P4bQ1/8/8/6PP/7K w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.A5, Player.BLACK));
		//bishops
		assertEquals(0x200000L, new Gamestate("7k/1q4pp/2b5/3n4/8/1B3N2/R5QP/7K w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D5, Player.WHITE));
		assertEquals(0x4004000L, new Gamestate("7k/1q4pp/2b5/3n4/2b5/1B6/R5KP/7Q w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D5, Player.WHITE));
		assertEquals(0x2000000000000L, new Gamestate("Q5qk/1B4pp/4b3/3n4/2b5/1R6/K6P/8 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D5, Player.WHITE));
		assertEquals(0x0L, new Gamestate("b5qk/1B4pp/4b3/3n4/2b5/1R6/K6P/8 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D5, Player.WHITE));
		assertEquals(0x200000000000L, new Gamestate("1k5q/pp6/5b2/8/3n4/1PQ5/PBP5/1K6 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D4, Player.BLACK));
		assertEquals(0x40000000040000L, new Gamestate("1k5q/pp4B1/8/8/3n4/1PQ5/PbP5/1K6 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D4, Player.BLACK));
		assertEquals(0x20000000200000L, new Gamestate("1k2q3/pp3B2/8/7n/8/1PQ2R2/P1P1b3/1K6 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.H5, Player.BLACK));
		assertEquals(0x400000000000L, new Gamestate("1k2q3/pp2R3/6b1/7n/8/1P3B2/P1P1Q3/1K6 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.H5, Player.BLACK));
		//add pinned 7th rank pawns
		assertEquals(0x1c000000000000L, new Gamestate("8/2PPP2k/8/Q7/7B/8/3R4/2K5 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D8, Player.WHITE));
		assertEquals(0x1c000000000000L, new Gamestate("8/2ppp2k/8/Q7/7B/8/3R4/2K5 w - - 0 1").calculatePinsSkewersAndDiscoveredAttacks(Square.D8, Player.WHITE));
	}
}
