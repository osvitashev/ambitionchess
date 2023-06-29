package gamestate;

import static util.BitField32.*;

import gamestate.GlobalConstants.MoveType;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/**
 * Contains all information needed to correctly make a move, but not necessarily to restore game state upon undoing it.
 * 

 * 
 //@formatter:off
  * 
  	memory packing: 
	start	length	name
	0		6		square_from
	6		6		square_to
	12		3		piece_type
	15		3		piece_captured
	18		3		piece_promoted
	21		3		move_type
 	24		1		player
 	25		1		check = is set by move validation
	
	IDEA: Zobriest hashing can be implemented for moves in order to implement Killer move heuristic
	
	IDEA: Refactor move type to incorporate claiming draw under 50 move rule and triple repetition and possibly null move.

 	some of the space can be analyzed board state AFTER the move is made: isTrippleRepetition?
 	
 	Now that Move includes information on historical board state for Move.undo, it is important to be careful with move comparison
 	Does "e2".equal("e2") even if they have different castling conditions? or different quiet_halfmove_counts?
 	=> "e2" giving check is different from "e2" without giving check when it comes to move ordering!!
 	
 //@formatter:on
 */

public class Move {

	public static int getSquareFrom(int move) {
		return getBits(move, 0, 6);
	}

	public static int setSquareFrom(int move, int val) {
		DebugLibrary.validateSquare(val);
		return setBits(move, val, 0, 6);
	}

	public static int getSquareTo(int move) {
		return getBits(move, 6, 6);
	}

	public static int setSquareTo(int move, int val) {
		DebugLibrary.validateSquare(val);
		return setBits(move, val, 6, 6);
	}

	public static int getPieceType(int move) {
		return getBits(move, 12, 3);
	}

	public static int setPieceType(int move, int type) {
		DebugLibrary.validatePieceType(type);
		return setBits(move, type, 12, 3);
	}

	public static int getPieceCapturedType(int move) {
		return getBits(move, 15, 3);
	}

	public static int setPieceCapturedType(int move, int type) {
		DebugLibrary.validatePieceType(type);
		return setBits(move, type, 15, 3);
	}

	public static int getPiecePromotedType(int move) {
		return getBits(move, 18, 3);
	}

	public static int setPiecePromotedType(int move, int type) {
		DebugLibrary.validatePieceType(type);
		return setBits(move, type, 18, 3);
	}

	public static int getMoveType(int move) {
		return getBits(move, 21, 3);
	}

	public static int setMoveType(int move, int type) {
		DebugLibrary.validateMoveType(type);
		return setBits(move, type, 21, 3);
	}

	public static int getPlayer(int move) {
		return getBits(move, 24, 1);
	}

	public static int setPlayer(int move, int pl) {
		DebugLibrary.validatePlayer(pl);
		return setBits(move, pl, 24, 1);
	}

	public static boolean getCheck(int move) {
		return getBoolean(move, 25);
	}

	public static int setCheck(int move, boolean val) {
		return setBoolean(move, val, 25);
	}

	public static int setCheck(int move) {
		return setBoolean(move, true, 25);
	}

	public static String moveToString(int move) {
		String ret = "NOT YET IMPLEMENTED!";
		switch (Move.getMoveType(move)) {
		case MoveType.PROMO_CAPTURE:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "x" + Square.squareToAlgebraicString(Move.getSquareTo(move)) + "="
					+ PieceType.toString(Move.getPiecePromotedType(move));
			break;
		case MoveType.PROMO:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "-" + Square.squareToAlgebraicString(Move.getSquareTo(move)) + "="
					+ PieceType.toString(Move.getPiecePromotedType(move));
			break;
		case MoveType.CAPTURE:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "x" + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		case MoveType.ENPASSANT:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "x" + Square.squareToAlgebraicString(Move.getSquareTo(move)) + " e.p.";
			break;
		case MoveType.CASTLE_KING:
			ret = "O-O";
			break;
		case MoveType.CASTLE_QUEEN:
			ret = "O-O-O";
			break;
		case MoveType.NORMAL:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "-" + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		case MoveType.DOUBLE_PUSH:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + "-" + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		}
		if (Move.getCheck(move))
			ret += "+";
		return ret;
	}

	public static String toUCINotation(int move) {
		String ret = "NOT YET IMPLEMENTED!";
		switch (Move.getMoveType(move)) {
		case MoveType.PROMO_CAPTURE:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move))
					+ PieceType.toString(Move.getPiecePromotedType(move)).toLowerCase();
			break;
		case MoveType.PROMO:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move))
			+ PieceType.toString(Move.getPiecePromotedType(move)).toLowerCase();
			break;
		case MoveType.CAPTURE:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		case MoveType.ENPASSANT:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		case MoveType.CASTLE_KING:
			if(Move.getPlayer(move) == Player.WHITE)
				ret = "e1g1";
			else
				ret="e8g8";
			break;
		case MoveType.CASTLE_QUEEN:
			if(Move.getPlayer(move) == Player.WHITE)
				ret = "e1c1";
			else
				ret="e8c8";
			break;
		case MoveType.NORMAL:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		case MoveType.DOUBLE_PUSH:
			ret = Square.squareToAlgebraicString(Move.getSquareFrom(move)) + Square.squareToAlgebraicString(Move.getSquareTo(move));
			break;
		}
		return ret;
	}

	public static int createCapturePromo(int squareFrom, int squareTo, int pieceCapturedType, int piecePromotedType, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePieceType(pieceCapturedType);
		DebugLibrary.validatePieceType(piecePromotedType);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setPieceCapturedType(move, pieceCapturedType);
		move = Move.setPiecePromotedType(move, piecePromotedType);
		move = Move.setMoveType(move, MoveType.PROMO_CAPTURE);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createPromo(int squareFrom, int squareTo, int piecePromotedType, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePieceType(piecePromotedType);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setPiecePromotedType(move, piecePromotedType);
		move = Move.setMoveType(move, MoveType.PROMO);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createCapture(int squareFrom, int squareTo, int pieceType, int pieceCapturedType, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePieceType(pieceType);
		DebugLibrary.validatePieceType(pieceCapturedType);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setPieceType(move, pieceType);
		move = Move.setPieceCapturedType(move, pieceCapturedType);
		move = Move.setMoveType(move, MoveType.CAPTURE);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createEnpassant(int squareFrom, int squareTo, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setMoveType(move, MoveType.ENPASSANT);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createCastleKing(int player) {
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setMoveType(move, MoveType.CASTLE_KING);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createCastleQueen(int player) {
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setMoveType(move, MoveType.CASTLE_QUEEN);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createNormal(int squareFrom, int squareTo, int pieceType, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePieceType(pieceType);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setPieceType(move, pieceType);
		move = Move.setMoveType(move, MoveType.NORMAL);
		move = Move.setPlayer(move, player);
		return move;
	}

	public static int createDoublePush(int squareFrom, int squareTo, int player) {
		DebugLibrary.validateSquare(squareFrom);
		DebugLibrary.validateSquare(squareTo);
		DebugLibrary.validatePlayer(player);
		int move = 0;
		move = Move.setSquareFrom(move, squareFrom);
		move = Move.setSquareTo(move, squareTo);
		move = Move.setMoveType(move, MoveType.DOUBLE_PUSH);
		move = Move.setPlayer(move, player);
		return move;
	}
}
