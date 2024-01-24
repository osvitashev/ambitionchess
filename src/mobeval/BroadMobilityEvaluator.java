package mobeval;

import basicseeval.BroadStaticExchangeEvaluator;
import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

public class BroadMobilityEvaluator {
	/**
	 * max conceivable player piece count. Pawns excluded. needs to be more than 8 to account for possible promotions.
	 */
	private static final int MAX_PLAYER_PIECE_COUNT = 12;
	/**
	 * set in initialize(). Should be checked in all public getters. Is intended to prevent Gamestate and ExchangeEvaluator getting out of sync!
	 */
	private long hashValue = 0;
	
	private final Gamestate game;
	private final BroadStaticExchangeEvaluator seeval;

	public BroadMobilityEvaluator(Gamestate g, BroadStaticExchangeEvaluator see) {
		game = g;
		seeval=see;
	}
	
	private int [] mobCollection_size = new int [2];
	
	private int [][] mobCollection_sqFrom = new int [2][MAX_PLAYER_PIECE_COUNT];
	private int [][] mobCollection_pieceType = new int [2][MAX_PLAYER_PIECE_COUNT];
	
	/**
	 * mobCollection_safe_X are all subsets of game.occupied! I.e. only contain empty squares.
	 * The intention is that vulnerability evaluation can work backwards from potential victim.
	 */
	private long [][] mobCollection_safe_1 = new long [2][MAX_PLAYER_PIECE_COUNT];
	private long [][] mobCollection_safe_2 = new long [2][MAX_PLAYER_PIECE_COUNT];
	private long [][] mobCollection_safe_3 = new long [2][MAX_PLAYER_PIECE_COUNT];
	private long [][] mobCollection_safe_inf = new long [2][MAX_PLAYER_PIECE_COUNT];
	
	public int get_output_mobCollection_size(int player) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		return mobCollection_size[player];
	}
	
	public int get_output_mobCollection_sqFrom(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_sqFrom[player][i];
	}
	
	public int get_output_mobCollection_pieceType(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_pieceType[player][i];
	}
	
	public long get_output_mobCollection_safe_1(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_safe_1[player][i];
	}
	
	public long get_output_mobCollection_safe_2(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_safe_2[player][i];
	}
	
	public long get_output_mobCollection_safe_3(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_safe_3[player][i];
	}
	
	public long get_output_mobCollection_safe_inf(int player, int i) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		assert i < mobCollection_size[player];
		return mobCollection_safe_inf[player][i];
	}
	

	/**
	 * should be called any time internal game state changes
	 * assumes that seeval has been initialized!
	 * todo: how to enforce that? -> would be easier once Gamestate has a zobrist code implemented.
	 */
	void initialize() {
		hashValue = game.getZobristHash();
		mobCollection_size[0]=0;
		mobCollection_size[1]=0;
		
		{// initialize blockaded pawns
			long allPawns = game.getPieces(PieceType.PAWN);
			output_blockadedPawns[Player.WHITE] = game.getPieces(Player.WHITE, PieceType.PAWN) & Bitboard.shiftSouth(allPawns);
			output_blockadedPawns[Player.BLACK] = game.getPieces(Player.BLACK, PieceType.PAWN) & Bitboard.shiftNorth(allPawns);
		}
	}

	/**
	 * a pawn blocked by another pawn in front of it (whether ours' or opponents')
	 */
	private long output_blockadedPawns[] = new long[2];

	/**
	 * pawns where forward movement is blocked by another pawn (friend or foe). Only
	 * checks single pushes.
	 * 
	 * @param player
	 * @return
	 */
	public long get_output_blockadedPawns(int player) {
		assert Player.validate(player);
		assert hashValue == game.getZobristHash();
		return output_blockadedPawns[player];
	}
	
	void processRook(int player, int sq) {
		assert Player.validate(player);
		assert Square.validate(sq);
		assert player == game.getPlayerAt(sq);
		assert PieceType.ROOK == game.getPieceAt(sq);
		int nextIndex = mobCollection_size[player];
		mobCollection_sqFrom[player][nextIndex] = sq;
		mobCollection_pieceType[player][nextIndex] = PieceType.ROOK;
		long likelyUnSafeEmptySquares = seeval.get_output_attackedByLesserOrEqualPieceTargets(Player.getOtherPlayer(player), PieceType.ROOK);
		
		mobCollection_safe_1[player][nextIndex] =
				BitboardGen.getRookSet(sq, game.getOccupied()) &
				game.getEmpty() &
				//todo: reevaluate whether quiet_neutral is needed here... for now it is here because we can calculate the first move more reliably.
				seeval.get_output_quiet_neutral(player, PieceType.ROOK) &
				~likelyUnSafeEmptySquares;
		
//		likelyUnSafeEmptySquares |= (seeval.get_output_attackedTargets(player, PieceType.ROOK)
//				| seeval.get_output_attackedTargets(player, PieceType.QUEEN)) & ~seeval.get_output_attackedTargets(Player.getOtherPlayer(player));
		
		mobCollection_safe_2[player][nextIndex] = mobCollection_safe_1[player][nextIndex]
				| fillStep_rook(mobCollection_safe_1[player][nextIndex], game.getOccupied(), likelyUnSafeEmptySquares);
		
//		mobCollection_safe_3[player][nextIndex] = mobCollection_safe_1[player][nextIndex]
//				| fillStep_rook(mobCollection_safe_2[player][nextIndex], game.getOccupied(), likelyUnSafeEmptySquares);
		
		mobCollection_size[player]++;
	}

	//does a flood fill. Potentially stops after 3? iterations....
	long doFloodFill_knight(int sq_from) {
		long attacks = BitboardGen.getKnightSet(sq_from);
		
		System.out.println("Knight attacks: " + attacks);
		attacks = attacks | BitboardGen.getMultipleKnightSet(attacks & game.getEmpty());
		
		System.out.println("Knight attacks: " + attacks);
		attacks = attacks | BitboardGen.getMultipleKnightSet(attacks & game.getEmpty());
		
		System.out.println("Knight attacks: " + attacks);
		
		return attacks;
	}
	
	private long fillStep_rook(long currentAttackSet, long occupied, long beaten) {
		long ret =0, before, after;
		{
			after=0;
			before = currentAttackSet;
			while (true) {
				after |= Bitboard.shiftNorth(before) & ~occupied;
				if (before == after)
					break;
				else
					before = after;
			}
			ret |= after;
		}
		{
			after=0;
			before = currentAttackSet;
			while (true) {
				after |= Bitboard.shiftSouth(before) & ~occupied;
				if (before == after)
					break;
				else
					before = after;
			}
			ret |= after;
		}
		{
			after=0;
			before = currentAttackSet;
			while (true) {
				after |= Bitboard.shiftEast(before) & ~occupied;
				if (before == after)
					break;
				else
					before = after;
			}
			ret |= after;
		}
		{
			after=0;
			before = currentAttackSet;
			while (true) {
				after |= Bitboard.shiftWest(before) & ~occupied;
				if (before == after)
					break;
				else
					before = after;
			}
			ret |= after;
		}
		return ret & ~beaten;
	}
	
	/**
	 * initial and beaten mask are not supposed to intersect, initial & ~beaten is what's return if flood cycles do not produce new values!
	 * 
	 * BroadMobilityEvaluator.doFloodFill_rook(
	 *		Bitboard.initFromAlgebraicSquares("c4"),//initial seed
	 *		0x0l,//occupied
	 *		Bitboard.initFromAlgebraicSquares("c4")//beaten
	 *)
	 *
	 *It is valid to invoke this method for a single square seed, but care need to be taken if it overlaps with the beaten mask.
	 *In such case, the example above would return 0.
	 *
	 *Intended usage:
	 *BroadMobilityEvaluator.doFloodFill_rook(
	 *		BitboardGen.getRookSet(Square.H1, game.getOccupied()) & game.getEmpty(),//initial seed
	 *		game.getOccupied(),//occupied
	 *		seeval.get_output_attackedByLesserPieceTargets(Player.BLACK, PieceType.ROOK)//beaten
	 *)
	 *
	 * @param initial - initial mask seed
	 * @param occupied - occupied squares
	 * @param beaten - 'empty' squares which can be jumped over by sliding pieces, but cannot be stopped. As such, they allow to continuing moving in the same direction but not taking turns.
	 * @return
	 */
	static long doFloodFill_rook(long initial, long occupied, long beaten) {
		// question: is it allowed for initial and beaten masks to intersect???
		long ret = initial & ~beaten;
		long prevCycle, tempDirectionwise;
		System.out.println("new invocation: ");
		while (true) {
			prevCycle = ret;
			System.out.println("checkpoint: " + ret);
			while (true) {
				tempDirectionwise = ret;
				ret |= Bitboard.shiftNorth(ret) & ~occupied;
				if (tempDirectionwise == ret)
					break;
			}
			while (true) {
				tempDirectionwise = ret;
				ret |= Bitboard.shiftSouth(ret) & ~occupied;
				if (tempDirectionwise == ret)
					break;
			}
			ret &= ~beaten;// clear beaten mask before changing flood direction.
			
			while (true) {
				tempDirectionwise = ret;
				ret |= Bitboard.shiftEast(ret) & ~occupied;
				if (tempDirectionwise == ret)
					break;
			}
			while (true) {
				tempDirectionwise = ret;
				ret |= Bitboard.shiftWest(ret) & ~occupied;
				if (tempDirectionwise == ret)
					break;
			}
			ret &= ~beaten;// clear beaten mask before changing flood direction.
			/**
			 * if we introduce another intermediate variable instead of doing ret |=... and only update ret after all 4 loops finish,
			 * it would be easier to isolate the 'step' routine. This would be useful in the 'speed' aka fixed iteration flood fill evaluation.
			 */			

			if (prevCycle == ret)
				break;
		}
		return ret;
	}
	
	/**
	 * the current implementation is using overlapping generations and therefor cannot differentiate locations accessible in 2 moves and 3 moves.
	 * The up side of that is that it takes fewer iterations to finish the flood fill.
	 * maybe it is worthwhile to have two different implementations of this: one to calculate the first 2-3 steps, then use the other one to efficiently finish the infinite fill.
	 * @param currentFloodSet
	 * @param occupied
	 * @param beaten
	 * @return
	 */
	

}
