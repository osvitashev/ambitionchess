package exchange.control;

import gamestate.GlobalConstants.PieceType;

/**
 * Represents all attackers of a given square. Generally, player-specific with the exception of backstabbed pawns.
 * 
 *
 */
class PlayerAttackStack{
	
	//Other values are covered in PieceType: 0-5 = regular; 7 = no piece; All these fit onto 3 bits
	public static int ENEMY_PAWN=6;
	
	public static int initialize() {
		return PieceType.NO_PIECE;
	}
	
	public static int addRegularPiece(int attackCombo, int pieceType) {
		PieceType.validate(pieceType);
		return (attackCombo<<3) | pieceType;
	}
	
	public static int addEnemyPawn(int attackCombo) {
		return (attackCombo<<3) | ENEMY_PAWN;
	}
	
	public static boolean hasNext(int attackCombo) {
		return attackCombo != PieceType.NO_PIECE;
	}
	
	public static int getNext(int attackCombo) {
		return attackCombo & 7;//last 3 bits
	}
	
	public static int removeNext(int attackCombo) {
		return attackCombo>>3;
	}
	
	
}