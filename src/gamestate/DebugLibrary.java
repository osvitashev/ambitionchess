package gamestate;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

public class DebugLibrary {
	public static final boolean ENABLE_PRIMITIVE_TYPE_VALIDATION;

	static {
		String temp = System.getenv().get("ENABLE_PRIMITIVE_TYPE_VALIDATION");

		ENABLE_PRIMITIVE_TYPE_VALIDATION = "false".equals(temp) ? false : true;// defaults to true for now...

		System.out.println("Initialized ENABLE_PRIMITIVE_TYPE_VALIDATION to: " + ENABLE_PRIMITIVE_TYPE_VALIDATION);

	}

	/**
	 * Checks that an integer is a valid representation of a square on the board.
	 * Square.NO_SQUARE value is considered to be invalid.
	 * 
	 * @param sq
	 * @return
	 */
	public static int validateSquare(int sq) {
		if (ENABLE_PRIMITIVE_TYPE_VALIDATION) {
			if (Square.H8 < sq || sq < Square.A1)
				throw new RuntimeException("Argument is out of range for a Square type: " + sq);
		}
		return sq;
	}

	/**
	 * Checks that integer piece type is within the range of valid values.
	 * PieceType_NO_PIECE is not considered to be valid.
	 * 
	 * @param pt
	 * @return
	 */
	public static int validatePieceType(int pt) {
		if (ENABLE_PRIMITIVE_TYPE_VALIDATION) {
			if (PieceType.KING < pt || pt < PieceType.PAWN)
				throw new RuntimeException("Argument is out of range for a Piece type: " + pt);
		}
		return pt;
	}

	/**
	 * Checks that integer player is within the range of valid values.
	 * Player.NO_PLAYER is not considered to be valid.
	 * 
	 * @param p
	 * @return
	 */
	public static int validatePlayer(int p) {
		if (ENABLE_PRIMITIVE_TYPE_VALIDATION) {
			if (p != Player.WHITE && p != Player.BLACK)
				throw new RuntimeException("Argument is out of range for a Player type: " + p);
		}
		return p;
	}

	/**
	 * Checks that integer move type is within the range of valid values.
	 * 
	 * @param mt
	 * @return
	 */
	public static int validateMoveType(int mt) {
		if (ENABLE_PRIMITIVE_TYPE_VALIDATION) {
			if (GlobalConstants.MoveType.DOUBLE_PUSH < mt || mt < GlobalConstants.MoveType.PROMO_CAPTURE)
				throw new RuntimeException("Argument is out of range for a MoveType: " + mt);
		}
		return mt;
	}

//	/**
//	 * Checks that board is in valid state. No two pieces or players cooccupy same
//	 * square. Enpassant square can only be rank 3 or 6. Casling availability
//	 * implies king and rook having corresponding positions.
//	 * 
//	 * @param Board brd
//	 * @return
//	 */
//	public static void validateBoardIntegrity(Board brd) {
//		if (ENABLE_PRIMITIVE_TYPE_VALIDATION) {
//			// validate that player bitboards are disjointed.
//			if (Bitboard.popcount(brd.getPlayerPieces(Player.WHITE) & brd.getPlayerPieces(Player.BLACK)) != 0)
//				throw new RuntimeException("Two players are cooccupying a square");
//			// validate that piece types are disjointed.
//			for (int sq : Square.SQUARES) {
//				int temp = 0;
//				for (int pt : PieceType.PIECE_TYPES) {
//					if (Bitboard.testBit(brd.getPieces(pt), sq))
//						temp++;
//				}
//				if (temp > 1)
//					throw new RuntimeException("Two pieces are cooccupying a square " + Square.squareToAlgebraicString(sq));
//			}
//			// validate enpassant
//			if (brd.getEnpassantSquare() != Square.SQUARE_NONE) {
//				validateSquare(brd.getEnpassantSquare());
//				if (brd.getPlayerToMove() == Player.WHITE && 2 != (brd.getEnpassantSquare() / 8))
//					throw new RuntimeException("Invalid enpassant square");
//				if (brd.getPlayerToMove() == Player.BLACK && 5 != (brd.getEnpassantSquare() / 8))
//					throw new RuntimeException("Invalid enpassant square");
//			}
//			// validate castling availability
//			// Notice that this only concerns positions of king and rooks, but not whether
//			// king is on check or whether the squares between rook and king are free, as
//			// these conditions are subject to change
//			if (brd.getCastling_WK() && !(brd.testPieceAt(PieceType.KING, Player.WHITE, Square.E1) && brd.testPieceAt(PieceType.ROOK, Player.WHITE, Square.H1)))
//				throw new RuntimeException("Invalid castling state!");
//			if (brd.getCastling_WQ() && !(brd.testPieceAt(PieceType.KING, Player.WHITE, Square.E1) && brd.testPieceAt(PieceType.ROOK, Player.WHITE, Square.A1)))
//				throw new RuntimeException("Invalid castling state!");
//			if (brd.getCastling_BK() && !(brd.testPieceAt(PieceType.KING, Player.BLACK, Square.E8) && brd.testPieceAt(PieceType.ROOK, Player.BLACK, Square.H8)))
//				throw new RuntimeException("Invalid castling state!");
//			if (brd.getCastling_BQ() && !(brd.testPieceAt(PieceType.KING, Player.BLACK, Square.E8) && brd.testPieceAt(PieceType.ROOK, Player.BLACK, Square.A8)))
//				throw new RuntimeException("Invalid castling state!");
//		}
//	}

}
