package gamestate;

import static gamestate.Bitboard.bitScanForward;

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
}
