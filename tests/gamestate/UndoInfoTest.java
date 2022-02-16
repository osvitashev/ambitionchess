package gamestate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.BitField32;

class UndoInfoTest {

	@Test
	void testBitManipulation() {
		int info = 0;
		info = BitField32.setBits(info, 5, 10, 3);
		assertEquals(5, BitField32.getBits(info, 10, 3));
		info = BitField32.setBits(info, 14, 0, 5);
		assertEquals(14, BitField32.getBits(info, 0, 5));
		assertEquals(5134, info);// this tests that setBits does not clear out values outside the range of its
									// own argument. In other words, setBits is additive.

		info = BitField32.setBits(info, 0, 0, 5);// this undoes the second call to setBits. Tests that passing a zero argument
		// acts as clearBits call.
		assertEquals(5, BitField32.getBits(info, 10, 3));

		info = ~0;
		assertEquals(0xFF, BitField32.getBits(info, 0, 8));
		assertEquals(0xFF, BitField32.getBits(info, 20, 8));

		info = BitField32.setBits(info, 0xE4, 10, 8);
		assertEquals(0xE4, BitField32.getBits(info, 10, 8));
		assertEquals(0xFF, BitField32.getBits(info, 20, 8));
		assertNotEquals(0xFF, BitField32.getBits(info, 14, 8));

		assertEquals(0xFF, BitField32.getBits(info, 22, 8));
		assertNotEquals(0xFF, BitField32.getBits(info, 13, 8));

		info = 0;
		info = BitField32.setBoolean(info, true, 0);
		info = BitField32.setBoolean(info, true, 2);
		assertEquals(5, info);
		assertEquals(true, BitField32.getBoolean(info, 0));
		assertEquals(false, BitField32.getBoolean(info, 1));
		assertEquals(true, BitField32.getBoolean(info, 2));
		assertEquals(false, BitField32.getBoolean(info, 3));

		info = BitField32.setBoolean(info, false, 0);
		assertEquals(false, BitField32.getBoolean(info, 0));
		assertEquals(false, BitField32.getBoolean(info, 1));
		assertEquals(true, BitField32.getBoolean(info, 2));
		assertEquals(false, BitField32.getBoolean(info, 3));

	}

	@Test
	void testGettersSetters() {
		int info;
		// test halfmove counter
		info = 0;
		info = UndoInfo.setHalfmoveCounter(info, 121);
		assertEquals(121, UndoInfo.getHalfmoveCounter(info));
		assertEquals(0, UndoInfo.getEnpassantSquare(info));

		info = 0;
		info = UndoInfo.setEnpassantSquare(info, Square.B3);
		assertEquals(0, UndoInfo.getHalfmoveCounter(info));
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWK(info));

		info = 0;
		info = UndoInfo.setCastlingWK(info, true);
		assertEquals(0, UndoInfo.getEnpassantSquare(info));
		assertEquals(true, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWQ(info));
		assertEquals(false, UndoInfo.getCastlingBK(info));
		assertEquals(false, UndoInfo.getCastlingBQ(info));

		info = 0;
		info = UndoInfo.setCastlingWQ(info, true);
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(true, UndoInfo.getCastlingWQ(info));
		assertEquals(false, UndoInfo.getCastlingBK(info));
		assertEquals(false, UndoInfo.getCastlingBQ(info));

		info = 0;
		info = UndoInfo.setCastlingBK(info, true);
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWQ(info));
		assertEquals(true, UndoInfo.getCastlingBK(info));
		assertEquals(false, UndoInfo.getCastlingBQ(info));

		info = 0;
		info = UndoInfo.setCastlingBQ(info, true);
		assertEquals(false, UndoInfo.getCastlingWK(info));
		assertEquals(false, UndoInfo.getCastlingWQ(info));
		assertEquals(false, UndoInfo.getCastlingBK(info));
		assertEquals(true, UndoInfo.getCastlingBQ(info));
	}

	void assertBoardLocation(Gamestate brd, int player, int piece, int sq) {
		assertEquals(player, brd.getPlayerAt(sq));
		assertEquals(piece, brd.getPieceAt(sq));
	}

	@Test
	void testMakeUnmakeMove() {
		String fen = "rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P2/PPP3P1/R3K2R w KQkq e6 11 23";
		Gamestate brd = new Gamestate(fen);

		// assert initial board state
		assertEquals(Player.WHITE, brd.getPlayerToMove());
		assertEquals(true, brd.getCastling_WK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BQ());
		assertEquals(false, brd.isCheck());
		assertEquals(Square.E6, brd.getEnpassantSquare());
		assertEquals(11, brd.getQuietHalfmoveClock());
		assertEquals(44, brd.getGamePlyCount());
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.A1);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.C2);
		assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.H1);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.C7);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.D5);
		assertBoardLocation(brd, Player.WHITE, PieceType.QUEEN, Square.H6);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.G7);
		assertBoardLocation(brd, Player.BLACK, PieceType.BISHOP, Square.B8);
		assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
		assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.H8);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E5);

		int move = 0;
		move = Move.setCheck(Move.createCapturePromo(Square.C7, Square.B8, PieceType.BISHOP, PieceType.ROOK, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("rR2k2r/5pP1/7Q/3Pp2P/6pn/5P2/PPP3P1/R3K2R b KQkq - 0 23", brd.toFEN());
		assertEquals(Player.BLACK, brd.getPlayerToMove());
		assertEquals(true, brd.getCastling_WK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BQ());
		assertEquals(true, brd.isCheck());
		assertEquals(Square.SQUARE_NONE, brd.getEnpassantSquare());
		assertEquals(0, brd.getQuietHalfmoveClock());
		assertEquals(45, brd.getGamePlyCount());

		brd.unmakeMove(move);
		// assert initial board state
		assertEquals(fen, brd.toFEN());
		assertEquals(Player.WHITE, brd.getPlayerToMove());
		assertEquals(true, brd.getCastling_WK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BK());
		assertEquals(true, brd.getCastling_BQ());
		assertEquals(false, brd.isCheck());
		assertEquals(Square.E6, brd.getEnpassantSquare());
		assertEquals(11, brd.getQuietHalfmoveClock());
		assertEquals(44, brd.getGamePlyCount());
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.A1);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.C2);
		assertBoardLocation(brd, Player.WHITE, PieceType.KING, Square.E1);
		assertBoardLocation(brd, Player.WHITE, PieceType.ROOK, Square.H1);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.C7);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.D5);
		assertBoardLocation(brd, Player.WHITE, PieceType.QUEEN, Square.H6);
		assertBoardLocation(brd, Player.WHITE, PieceType.PAWN, Square.G7);
		assertBoardLocation(brd, Player.BLACK, PieceType.BISHOP, Square.B8);
		assertBoardLocation(brd, Player.BLACK, PieceType.KING, Square.E8);
		assertBoardLocation(brd, Player.BLACK, PieceType.ROOK, Square.H8);
		assertBoardLocation(brd, Player.BLACK, PieceType.PAWN, Square.E5);

		move = Move.createCapturePromo(Square.G7, Square.H8, PieceType.ROOK, PieceType.KNIGHT, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2N/2P2p2/7Q/3Pp2P/6pn/5P2/PPP3P1/R3K2R b KQq - 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createPromo(Square.G7, Square.G8, PieceType.ROOK, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k1Rr/2P2p2/7Q/3Pp2P/6pn/5P2/PPP3P1/R3K2R b KQkq - 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCapture(Square.F3, Square.G4, PieceType.PAWN, PieceType.PAWN, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6Pn/8/PPP3P1/R3K2R b KQkq - 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createEnpassant(Square.D5, Square.E6, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/4P2Q/7P/6pn/5P2/PPP3P1/R3K2R b KQkq - 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCastleKing(Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P2/PPP3P1/R4RK1 b kq - 12 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCastleQueen(Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P2/PPP3P1/2KR3R b kq - 12 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createNormal(Square.A2, Square.A3, PieceType.PAWN, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/P4P2/1PP3P1/R3K2R b KQkq - 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.setCheck(Move.createNormal(Square.H6, Square.C6, PieceType.QUEEN, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/2Q5/3Pp2P/6pn/5P2/PPP3P1/R3K2R b KQkq - 12 23", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createNormal(Square.H1, Square.H3, PieceType.ROOK, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P1R/PPP3P1/R3K3 b Qkq - 12 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createNormal(Square.A1, Square.B1, PieceType.ROOK, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P2/PPP3P1/1R2K2R b Kkq - 12 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createNormal(Square.E1, Square.D2, PieceType.KING, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/6pn/5P2/PPPK2P1/R6R b kq - 12 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createDoublePush(Square.C2, Square.C4, Player.WHITE);
		brd.makeMove(move);
		assertEquals("rb2k2r/2P2pP1/7Q/3Pp2P/2P3pn/5P2/PP4P1/R3K2R b KQkq c3 0 23", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		fen = "r3k3/8/1N6/8/R7/8/8/R3K3 w Qq - 0 1";
		brd = new Gamestate(fen);
		assertEquals(false, brd.isCheck());
		move = Move.setCheck(Move.createCapture(Square.A4, Square.A8, PieceType.ROOK, PieceType.ROOK, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("R3k3/8/1N6/8/8/8/8/R3K3 b Q - 0 1", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// king starts in check, which is blocked by en passant
		fen = "2k5/8/8/b7/8/8/1P6/4K3 w - - 0 1";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.createDoublePush(Square.B2, Square.B4, Player.WHITE);
		brd.makeMove(move);
		assertEquals("2k5/8/8/b7/1P6/8/8/4K3 b - b3 0 1", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		// king starts in check, which leads to another check
		fen = "8/7b/8/1k6/8/3K4/8/5Q2 w - - 0 1";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.setCheck(Move.createNormal(Square.D3, Square.C3, PieceType.KING, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("8/7b/8/1k6/8/2K5/8/5Q2 b - - 1 1", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		move = Move.setCheck(Move.createNormal(Square.F1, Square.F5, PieceType.QUEEN, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("8/7b/8/1k3Q2/8/3K4/8/8 b - - 1 1", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		fen = "8/8/2k5/3pP3/2K1P3/8/8/8 w - d6 0 15";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.setCheck(Move.createCapture(Square.E4, Square.D5, PieceType.PAWN, PieceType.PAWN, Player.WHITE), true);
		brd.makeMove(move);
		assertEquals("8/8/2k5/3PP3/2K5/8/8/8 b - - 0 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		move = Move.createEnpassant(Square.E5, Square.D6, Player.WHITE);
		brd.makeMove(move);
		assertEquals("8/8/2kP4/8/2K1P3/8/8/8 b - - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		// basic black position
		fen = "r3k2r/1pq2pp1/2p5/8/3pPp2/8/1p4p1/R3KB1R b KQkq c3 11 14";
		brd = new Gamestate(fen);
		assertEquals(false, brd.isCheck());
		// capture promo
		move = Move.setCheck(Move.createCapturePromo(Square.B2, Square.A1, PieceType.ROOK, PieceType.QUEEN, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3pPp2/8/6p1/q3KB1R w Kkq - 0 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCapturePromo(Square.G2, Square.H1, PieceType.ROOK, PieceType.QUEEN, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3pPp2/8/1p6/R3KB1q w Qkq - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCapturePromo(Square.G2, Square.F1, PieceType.BISHOP, PieceType.KNIGHT, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3pPp2/8/1p6/R3Kn1R w KQkq - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// promo
		move = Move.setCheck(Move.createPromo(Square.B2, Square.B1, PieceType.ROOK, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3pPp2/8/6p1/Rr2KB1R w KQkq - 0 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createPromo(Square.G2, Square.G1, PieceType.ROOK, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3pPp2/8/1p6/R3KBrR w KQkq - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// capture
		move = Move.setCheck(Move.createCapture(Square.A8, Square.A1, PieceType.ROOK, PieceType.ROOK, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("4k2r/1pq2pp1/2p5/8/3pPp2/8/1p4p1/r3KB1R w Kk - 0 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.setCheck(Move.createCapture(Square.H8, Square.H1, PieceType.ROOK, PieceType.ROOK, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("r3k3/1pq2pp1/2p5/8/3pPp2/8/1p4p1/R3KB1r w Qq - 0 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// enpasant
		move = Move.createEnpassant(Square.D4, Square.E3, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/5p2/4p3/1p4p1/R3KB1R w KQkq - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createEnpassant(Square.F4, Square.E3, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/1pq2pp1/2p5/8/3p4/4p3/1p4p1/R3KB1R w KQkq - 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// castling
		move = Move.createCastleKing(Player.BLACK);
		brd.makeMove(move);
		assertEquals("r4rk1/1pq2pp1/2p5/8/3pPp2/8/1p4p1/R3KB1R w KQ - 12 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.createCastleQueen(Player.BLACK);
		brd.makeMove(move);
		assertEquals("2kr3r/1pq2pp1/2p5/8/3pPp2/8/1p4p1/R3KB1R w KQ - 12 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// Normal move
		move = Move.createNormal(Square.E8, Square.D8, PieceType.KING, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r2k3r/1pq2pp1/2p5/8/3pPp2/8/1p4p1/R3KB1R w KQ - 12 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());
		
		move = Move.createNormal(Square.H8, Square.H7, PieceType.ROOK, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k3/1pq2ppr/2p5/8/3pPp2/8/1p4p1/R3KB1R w KQq - 12 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		move = Move.setCheck(Move.createNormal(Square.C7, Square.A5, PieceType.QUEEN, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("r3k2r/1p3pp1/2p5/q7/3pPp2/8/1p4p1/R3KB1R w KQkq - 12 15", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// double push
		move = Move.createDoublePush(Square.B7, Square.B5, Player.BLACK);
		brd.makeMove(move);
		assertEquals("r3k2r/2q2pp1/2p5/1p6/3pPp2/8/1p4p1/R3KB1R w KQkq b6 0 15", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());

		// black king starts in check
		fen = "8/3K1p2/8/8/2P5/8/4p3/1k3R2 b - - 0 1";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.createCapturePromo(Square.E2, Square.F1, PieceType.ROOK, PieceType.KNIGHT, Player.BLACK);
		brd.makeMove(move);
		assertEquals("8/3K1p2/8/8/2P5/8/8/1k3n2 w - - 0 2", brd.toFEN());
		assertEquals(false, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());

		// black king starts in check which leads to check
		fen = "8/5p2/8/5K2/2P5/8/4p3/1k3R2 b - - 10 1";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.setCheck(Move.createCapturePromo(Square.E2, Square.F1, PieceType.ROOK, PieceType.QUEEN, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("8/5p2/8/5K2/2P5/8/8/1k3q2 w - - 0 2", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());
		
		fen = "8/2b5/8/3k4/4Pp2/8/7K/8 b - e3 0 1";
		brd = new Gamestate(fen);
		assertEquals(true, brd.isCheck());
		move = Move.setCheck(Move.createEnpassant(Square.F4, Square.E3, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("8/2b5/8/3k4/8/4p3/7K/8 w - - 0 2", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(true, brd.isCheck());
		
		//misc
		fen = "8/8/8/6r1/8/3k4/8/R2K4 b - - 0 1";
		brd = new Gamestate(fen);
		assertEquals(false, brd.isCheck());
		move = Move.setCheck(Move.createNormal(Square.G5, Square.G1, PieceType.ROOK, Player.BLACK), true);
		brd.makeMove(move);
		assertEquals("8/8/8/8/8/3k4/8/R2K2r1 w - - 1 2", brd.toFEN());
		assertEquals(true, brd.isCheck());
		brd.unmakeMove(move);
		assertEquals(fen, brd.toFEN());
		assertEquals(false, brd.isCheck());
	}

}
