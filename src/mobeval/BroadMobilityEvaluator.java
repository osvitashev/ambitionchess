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
	 * set in initialize(). Should be checked in all public getters. Is intended to prevent Gamestate and ExchangeEvaluator getting out of sync!
	 */
	private long hashValue = 0;
	
	private final Gamestate game;
	private final BroadStaticExchangeEvaluator seeval;

	public BroadMobilityEvaluator(Gamestate g, BroadStaticExchangeEvaluator see) {
		game = g;
		seeval=see;
	}

	/**
	 * should be called any time internal game state changes
	 * assumes that seeval has been initialized!
	 * todo: how to enforce that? -> would be easier once Gamestate has a zobrist code implemented.
	 */
	void initialize() {
		hashValue = game.getZobristHash();
		
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
			/**
			 * if we introduce another intermediate variable instead of doing ret |=... and only update ret after all 4 loops finish,
			 * it would be easier to isolate the 'step' routine. This would be useful in the 'speed' aka fixed iteration flood fill evaluation.
			 */
			ret = doFloodFill_step_rook(ret, occupied, beaten);
			

			if (prevCycle == ret)
				break;
		}
		return ret;
	}
	
	private static long doFloodFill_step_rook(long currentFloodSet, long occupied, long beaten) {
		long tempDirectionwise;
		while (true) {
			tempDirectionwise = currentFloodSet;
			currentFloodSet |= Bitboard.shiftNorth(currentFloodSet) & ~occupied;
			if (tempDirectionwise == currentFloodSet)
				break;
		}
		while (true) {
			tempDirectionwise = currentFloodSet;
			currentFloodSet |= Bitboard.shiftSouth(currentFloodSet) & ~occupied;
			if (tempDirectionwise == currentFloodSet)
				break;
		}
		currentFloodSet &= ~beaten;// clear beaten mask before changing flood direction.

		while (true) {
			tempDirectionwise = currentFloodSet;
			currentFloodSet |= Bitboard.shiftEast(currentFloodSet) & ~occupied;
			if (tempDirectionwise == currentFloodSet)
				break;
		}
		while (true) {
			tempDirectionwise = currentFloodSet;
			currentFloodSet |= Bitboard.shiftWest(currentFloodSet) & ~occupied;
			if (tempDirectionwise == currentFloodSet)
				break;
		}
		currentFloodSet &= ~beaten;// clear beaten mask before changing flood direction.
		return currentFloodSet;
	}
	

}
