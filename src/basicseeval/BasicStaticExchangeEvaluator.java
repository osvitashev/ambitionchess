package basicseeval;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import gamestate.MoveGen;
import gamestate.MoveGen.LegalMoveGenerator;

/*
 * Takes a board state and performs square-centric static exchange evaluation.
 * This means we do not take into consideration discovered checks or removal of the guard on other targets.
 * */
public class BasicStaticExchangeEvaluator {
	private static final int[] BerlinerPieceValues = { 100, 320, 333, 510, 800, 100000  };
	private static final int[] pieceValues = BerlinerPieceValues;// index matches piece type.


	
	/**
	 * returns a mask with either exactly 1 or 0 bits set.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	static long getLeastValuableAttacker_mask(Gamestate game, int sq_target, int player, long clearedLocations) {
		assert Square.validate(sq_target);
		assert Player.validate(player);
		long targetMask = Bitboard.initFromSquare(sq_target);
		
		{
			long candidate_pawns=0;
			if(Player.isWhite(player))
				candidate_pawns= Bitboard.shiftSouth( Bitboard.shiftEast(targetMask)) |
					Bitboard.shiftSouth( Bitboard.shiftWest(targetMask));
			else
				candidate_pawns= Bitboard.shiftNorth( Bitboard.shiftEast(targetMask)) |
					Bitboard.shiftNorth( Bitboard.shiftWest(targetMask));
			candidate_pawns &= game.getPieces(player, PieceType.PAWN) & ~clearedLocations;
			if(!Bitboard.isEmpty(candidate_pawns))
				return Bitboard.isolateLsb(candidate_pawns);
		}
		
		{
			long candidate_knights = BitboardGen.getKnightSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KNIGHT);
			if(!Bitboard.isEmpty(candidate_knights))
				return Bitboard.isolateLsb(candidate_knights);
		}
		
		{
			long candidate_bishops = BitboardGen.getBishopSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.BISHOP);
			if(!Bitboard.isEmpty(candidate_bishops))
				return Bitboard.isolateLsb(candidate_bishops);
		}
		
		{
			long candidate_rooks = BitboardGen.getRookSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.ROOK);
			if(!Bitboard.isEmpty(candidate_rooks))
				return Bitboard.isolateLsb(candidate_rooks);
		}
		
		{
			long candidate_queens = BitboardGen.getQueenSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.QUEEN);
			if(!Bitboard.isEmpty(candidate_queens))
				return Bitboard.isolateLsb(candidate_queens);
		}
		
		return 0;
	}
	
	
	
	int evaluateCapture(Gamestate game, int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		
		return 1;
	}
	
	
	
	
}
