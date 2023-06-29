package gamestate;

public class GlobalConstants {
	public static final class Player {

		public static final int WHITE = 0;
		public static final int BLACK = 1;
		public static final int NO_PLAYER = 2;
		public static final int[] PLAYERS = { WHITE, BLACK };

		public static boolean isWhite(int player) {
			return player == WHITE;
		}

		public static int getPlayerFromBoolean(boolean isWhite) {
			// TODO: xor assignment instead of conditional
			return isWhite ? WHITE : BLACK;
		}

		public static int getOtherPlayer(int player) {
			// TODO: xor assignment instead of conditional
			return player == BLACK ? WHITE : BLACK;
		}

	}

	// TODO: All Enum classes should be interface-based and prvide a validation function.
	public static final class PieceType {
// IMPORTANT: NO_PIECE is not in the collection!
		public static final int[] PIECE_TYPES = { PieceType.PAWN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING };
		// it may be useful to have piece codes roughly approximating the order of value
		// of pieces: p<n<b<r<q<k
		public static final int PAWN = 0;
		public static final int ROOK = 3;
		public static final int KNIGHT = 1;
		public static final int BISHOP = 2;
		public static final int QUEEN = 4;
		public static final int KING = 5;
		public static final int NO_PIECE = 6;

		public static String toString(int pt) {
			DebugLibrary.validatePieceType(pt);
			String ret = "";
			if (pt == PieceType.PAWN)
				ret = "P";
			if (pt == PieceType.ROOK)
				ret = "R";
			if (pt == PieceType.KNIGHT)
				ret = "N";
			if (pt == PieceType.BISHOP)
				ret = "B";
			if (pt == PieceType.QUEEN)
				ret = "Q";
			if (pt == PieceType.KING)
				ret = "K";
			return ret;
		}
	}

	public static final class Square {

		public static final int A1 = 0;
		public static final int[] SQUARES = { A1, Square.B1, Square.C1, Square.D1, Square.E1, Square.F1, Square.G1, Square.H1, Square.A2, Square.B2, Square.C2, Square.D2, Square.E2,
				Square.F2, Square.G2, Square.H2, Square.A3, Square.B3, Square.C3, Square.D3, Square.E3, Square.F3, Square.G3, Square.H3, Square.A4, Square.B4, Square.C4, Square.D4,
				Square.E4, Square.F4, Square.G4, Square.H4, Square.A5, Square.B5, Square.C5, Square.D5, Square.E5, Square.F5, Square.G5, Square.H5, Square.A6, Square.B6, Square.C6,
				Square.D6, Square.E6, Square.F6, Square.G6, Square.H6, Square.A7, Square.B7, Square.C7, Square.D7, Square.E7, Square.F7, Square.G7, Square.H7, Square.A8, Square.B8,
				Square.C8, Square.D8, Square.E8, Square.F8, Square.G8, Square.H8 };
		public static final int A2 = 8;
		public static final int A3 = 16;
		public static final int A4 = 24;
		public static final int A5 = 32;
		public static final int A6 = 40;
		public static final int A7 = 48;
		public static final int A8 = 56;
		public static final int B1 = A1 + 1;
		public static final int B2 = A2 + 1;
		public static final int B3 = A3 + 1;
		public static final int B4 = A4 + 1;
		public static final int B5 = A5 + 1;
		public static final int B6 = A6 + 1;
		public static final int B7 = A7 + 1;
		public static final int B8 = A8 + 1;
		public static final int C1 = B1 + 1;
		public static final int C2 = B2 + 1;
		public static final int C3 = B3 + 1;
		public static final int C4 = B4 + 1;
		public static final int C5 = B5 + 1;
		public static final int C6 = B6 + 1;
		public static final int C7 = B7 + 1;
		public static final int C8 = B8 + 1;
		public static final int D1 = C1 + 1;
		public static final int D2 = C2 + 1;
		public static final int D3 = C3 + 1;
		public static final int D4 = C4 + 1;
		public static final int D5 = C5 + 1;
		public static final int D6 = C6 + 1;
		public static final int D7 = C7 + 1;
		public static final int D8 = C8 + 1;
		public static final int E1 = D1 + 1;
		public static final int E2 = D2 + 1;
		public static final int E3 = D3 + 1;
		public static final int E4 = D4 + 1;
		public static final int E5 = D5 + 1;
		public static final int E6 = D6 + 1;
		public static final int E7 = D7 + 1;
		public static final int E8 = D8 + 1;
		public static final int F1 = E1 + 1;
		public static final int F2 = E2 + 1;
		public static final int F3 = E3 + 1;
		public static final int F4 = E4 + 1;
		public static final int F5 = E5 + 1;
		public static final int F6 = E6 + 1;
		public static final int F7 = E7 + 1;
		public static final int F8 = E8 + 1;
		public static final int G1 = F1 + 1;
		public static final int G2 = F2 + 1;
		public static final int G3 = F3 + 1;
		public static final int G4 = F4 + 1;
		public static final int G5 = F5 + 1;
		public static final int G6 = F6 + 1;
		public static final int G7 = F7 + 1;
		public static final int G8 = F8 + 1;
		public static final int H1 = G1 + 1;
		public static final int H2 = G2 + 1;
		public static final int H3 = G3 + 1;
		public static final int H4 = G4 + 1;
		public static final int H5 = G5 + 1;
		public static final int H6 = G6 + 1;
		public static final int H7 = G7 + 1;
		public static final int H8 = G8 + 1;
		public static final String[] ALGEBRAIC_SQUARE_STRINGS = { "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3",
				"e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7",
				"b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", };

		public static int algebraicStringToSquare(String s) {
			for (int i = 0; i < ALGEBRAIC_SQUARE_STRINGS.length; ++i)
				if (ALGEBRAIC_SQUARE_STRINGS[i].equals(s))
					return i;
			return Square.SQUARE_NONE;
		}

		public static String squareToAlgebraicString(int sq) {
			return ALGEBRAIC_SQUARE_STRINGS[sq];
		}

		public static final int SQUARE_NONE = -1;

	}

	/**
	 * Care needs to be taken if new move types are added in addition to the initial
	 * 8. Currently castling is the only move that does not utilize squareFrom
	 * field. Special care must be taken to distinguish A0 and uninitialized bit
	 * field
	 */
	public static final class MoveType {
		// NOTICE: assigned value roughly indicate the desired move ordering.
		public static final int PROMO_CAPTURE = 0;
		public static final int PROMO = 1;
		public static final int CAPTURE = 2;
		public static final int ENPASSANT = 3;
		public static final int CASTLE_KING = 4;
		public static final int CASTLE_QUEEN = 5;
		public static final int NORMAL = 6;
		public static final int DOUBLE_PUSH = 7;
		public static final int[] MOVE_TYPES = { PROMO_CAPTURE, PROMO, CAPTURE, ENPASSANT, CASTLE_KING, CASTLE_QUEEN, NORMAL, DOUBLE_PUSH };

	}

	private GlobalConstants() {
	}

}
