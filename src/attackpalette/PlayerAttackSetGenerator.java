package attackpalette;

import gamestate.Bitboard;
import gamestate.Board;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

public class PlayerAttackSetGenerator {
	private KingAttackSet king = new KingAttackSet();
	private PawnsAttackSet pawns = new PawnsAttackSet();
	// these are placeholders used to populate the attackset collection..
	private BishopAttackSet bishop = new BishopAttackSet();
	private KnightAttackSet knight = new KnightAttackSet();
	private RookAttackSet rook = new RookAttackSet();
	private QueenAttackSet queen = new QueenAttackSet();

	public void generateAttackSet(Board brd, int player, PlayerAttackSet attackSet) {
		// add error in check!!!

		pawns.populateAttacks(brd, player);
		if (pawns.getPawnEast() != 0L)
			attackSet.addPawnAttackSet(player, pawns.getPawnEast(), AttackSet.cost_pawn, 0);
		if (pawns.getPawnWest() != 0L)
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
				if (bishop.getBishopPawnSet() != 0)
					attackSet.addAttackSet(player, bishop.getBishopPawnSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_pawn);
				if (bishop.getBishopQueenSet() != 0)
					attackSet.addAttackSet(player, bishop.getBishopQueenSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen);
				if (bishop.getBishopQueenPawnSet() != 0)
					attackSet.addAttackSet(player, bishop.getBishopQueenPawnSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen + AttackSet.cost_pawn);
				if (bishop.getBishopQueenQueenSet() != 0)
					attackSet.addAttackSet(player, bishop.getBishopQueenQueenSet(), PieceType.BISHOP, bi, AttackSet.cost_bishop, AttackSet.cost_queen + AttackSet.cost_queen);
			}
		}

		{// rooks
			int bi = 0;
			for (long zarg = brd.getPieces(player, PieceType.ROOK), barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.getLowSquareIndex(barg);
				rook.populateAttacks(brd, bi, player);
				attackSet.addAttackSet(player, rook.getRookSet(), PieceType.ROOK, bi, AttackSet.cost_rook, 0);
				if (rook.getRookRookSet() != 0)
					attackSet.addAttackSet(player, rook.getRookRookSet(), PieceType.ROOK, bi, AttackSet.cost_rook, AttackSet.cost_rook);
				if (rook.getRookQueenSet() != 0)
					attackSet.addAttackSet(player, rook.getRookQueenSet(), PieceType.ROOK, bi, AttackSet.cost_rook, AttackSet.cost_queen);
				if (rook.getRookQueenRookSet() != 0)
					attackSet.addAttackSet(player, rook.getRookQueenRookSet(), PieceType.ROOK, bi, AttackSet.cost_rook, AttackSet.cost_queen + AttackSet.cost_rook);
				if (rook.getRookQueenQueenSet() != 0)
					attackSet.addAttackSet(player, rook.getRookQueenQueenSet(), PieceType.ROOK, bi, AttackSet.cost_rook, AttackSet.cost_queen + AttackSet.cost_queen);
				if (rook.getRookRookQueenSet() != 0)
					attackSet.addAttackSet(player, rook.getRookRookQueenSet(), PieceType.ROOK, bi, AttackSet.cost_rook, AttackSet.cost_rook + AttackSet.cost_queen);
			}
		}

		{// queens
			int bi = 0;
			for (long zarg = brd.getPieces(player, PieceType.QUEEN), barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
				bi = Bitboard.getLowSquareIndex(barg);
				queen.populateAttacks(brd, bi, player);
				attackSet.addAttackSet(player, queen.getQueenSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, 0);
				if (queen.getQueenPawnSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenPawnSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_pawn);
				if (queen.getQueenBishopSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenBishopSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_bishop);
				if (queen.getQueenBishopPawnSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenBishopPawnSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_bishop + AttackSet.cost_pawn);
				if (queen.getQueenQueenSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenQueenSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_queen);
				if (queen.getQueenQueenPawnSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenQueenPawnSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_queen + AttackSet.cost_pawn);
				if (queen.getQueenQueenBishopSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenQueenBishopSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_queen + AttackSet.cost_bishop);
				if (queen.getQueenBishopQueenSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenBishopQueenSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_bishop + AttackSet.cost_queen);
				if (queen.getQueenRookSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenRookSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_rook);
				if (queen.getQueenRookRookSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenRookRookSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_rook+AttackSet.cost_rook);
				if (queen.getQueenQueenRookSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenQueenRookSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_queen + AttackSet.cost_rook);
				if (queen.getQueenRookQueenSet() != 0)
					attackSet.addAttackSet(player, queen.getQueenRookQueenSet(), PieceType.QUEEN, bi, AttackSet.cost_queen, AttackSet.cost_rook + AttackSet.cost_queen);
			}
		}

//		queenSet;--
//		queenPawnSet;--
//		queenBishopSet;//
//		queenBishopPawnSet;--
//		queenQueenSet;--
//		queenQueenPawnSet;--
//		queenQueenBishopSet;--
//		queenBishopQueenSet;--
//		queenRookSet;--
//		queenQueenRookSet;--
//		queenRookQueenSet;--
//		queenRookRookSet--

		king.populateAttacks(brd, Bitboard.getLowSquareIndex(brd.getPieces(player, PieceType.KING)), player);
		attackSet.addAttackSet(player, king.getKingSet(), PieceType.KING, king.getLocation(), AttackSet.cost_king, 0);// CONSIDER: should prevCost be zero here???
	}
}
