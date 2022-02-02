package attackpalette;

import gamestate.Bitboard;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

public class PlayerAttackSetGenerator {
	private KingAttackSet king=new KingAttackSet();
	private PawnsAttackSet pawns = new PawnsAttackSet();
	//these are placeholders used to populate the attackset collection..
	private BishopAttackSet bishop = new BishopAttackSet();
	private KnightAttackSet knight = new KnightAttackSet();
	private RookAttackSet rook = new RookAttackSet();
	private QueenAttackSet queen = new QueenAttackSet();
	
	public void generateAttackSet(Board brd, int player, PlayerAttackSet attackSet) {
		//add error in check!!!
				
		pawns.populateAttacks(brd, player);
		if(pawns.getPawnEast() !=0L)
			attackSet.addPawnAttackSet(player, pawns.getPawnEast(), AttackSet.cost_pawn, 0);
		if(pawns.getPawnWest() !=0L)
			attackSet.addPawnAttackSet(player, pawns.getPawnWest(), AttackSet.cost_pawn, 0);
		
		{// knights
			int bi = 0;
			for (long zarg = brd.getPieces(player, PieceType.KNIGHT), barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.getLowSquareIndex(barg);
				knight.populateAttacks(brd, bi, player);
				attackSet.addAttackSet(player, knight.getKnightSet(), PieceType.KNIGHT, bi, AttackSet.cost_knight, 0);
			}
		}
		
		{// bishops
			int bi = 0;
			for (long zarg = brd.getPieces(player, PieceType.BISHOP), barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.getLowSquareIndex(barg);
				bishop.populateAttacks(brd, bi, player);
				attackSet.addAttackSet(player, bishop.getBishopSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, 0);
				if(bishop.getBishopPawnSet() !=0)
					attackSet.addAttackSet(player, bishop.getBishopPawnSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_pawn);
				if(bishop.getBishopQueenSet() !=0)
					attackSet.addAttackSet(player, bishop.getBishopQueenSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen);
				if(bishop.getBishopQueenPawnSet() !=0)
					attackSet.addAttackSet(player, bishop.getBishopQueenPawnSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen+AttackSet.cost_pawn);
				if(bishop.getBishopQueenQueenSet() !=0)
					attackSet.addAttackSet(player, bishop.getBishopQueenQueenSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen+AttackSet.cost_queen);
			}
		}
		
//		private long bishopSet;//pc=0--
//		private long bishopPawnSet;//pc=1---
//		private long bishopQueenSet;//pc=9--
//		private long bishopQueenPawnSet;//pc=10--
//		private long bishopQueenQueenSet;//pc=18
		
		
		king.populateAttacks(brd, Bitboard.getLowSquareIndex(brd.getPieces(player, PieceType.KING)), player);
		attackSet.addAttackSet(player, king.getKingSet(), PieceType.KING, king.getLocation(), AttackSet.cost_king, 0);// CONSIDER: should prevCost be zero here???		
	}
}
