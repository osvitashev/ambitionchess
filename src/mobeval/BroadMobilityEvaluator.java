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
	

}
