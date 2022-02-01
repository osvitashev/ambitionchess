package attackpalette;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.DebugLibrary;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

public class PlayerAttackSet {
	final private KnightAttackSet[] pool_knight = new KnightAttackSet[3];
	final private BishopAttackSet[] pool_bishop = new BishopAttackSet[3];
	final private RookAttackSet[] pool_rook = new RookAttackSet[3];
	final private QueenAttackSet[] pool_queen = new QueenAttackSet[3];

	private KingAttackSet king;
	private PawnsAttackSet pawns;

	private int numKnights;
	private int numBishops;
	private int numRooks;
	private int numQueens;

	public void initialize(Board brd, int player) {
		king.populateAttacks(brd, Bitboard.getLowSquareIndex(brd.getPieces(player, PieceType.KING)), player);
		pawns.populateAttacks(brd, player);
	}

}
