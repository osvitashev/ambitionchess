package basicseeval;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/*
 * Takes a board state and performs square-centric static exchange evaluation.
 * This means we do not take into consideration discovered checks or removal of the guard on other targets.
 * */
public class BasicStaticExchangeEvaluator {
	//private static final int[] BerlinerPieceValues = { 100, 320, 333, 510, 800, 100000  };
	private static final int[] simpleValues = { 100, 300, 300, 500, 800, 100000  };
	private static final int[] pieceValues = simpleValues;// index matches piece type.
	
	private static int getPieceValue(int pieceType) {
		assert PieceType.validate(pieceType);
		return pieceValues[pieceType];
	}

	private Gamestate game;
	
	//should not be re-initialized. Just reset the game state.
	BasicStaticExchangeEvaluator(Gamestate g){
		game = g;
	}
	
	/**
	 * returns a mask with either exactly 1 or 0 bits set.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	long getLeastValuableAttacker_mask(int sq_target, int player, long clearedLocations) {
		assert Square.validate(sq_target);
		assert Player.validate(player);
		
		{
			long targetMask = Bitboard.initFromSquare(sq_target);
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
		//OPTIMIZE: once pawn and knight attack sets are exhausted, there is no point checking them again. This is not true for sliding pieces.
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
		
		{
			long candidate_kings = BitboardGen.getKingSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KING);
			if(!Bitboard.isEmpty(candidate_kings))
				return Bitboard.isolateLsb(candidate_kings);
		}
		
		return 0;//fallback
	}
	
	/**
	 * needs to be called after any changes to internal gamestate
	 */
	public void initialize() {
		for(int player : Player.PLAYERS) {
			for(int type : PieceType.PIECE_TYPES) {
				var_bitboard_attackedBy[player][type] =0;
				var_bitboard_secondary_attackedBy[player][type] =0;
				var_bitboard_secondary_battery_attackedBy[player][type] =0;
				//todo: reset the output variables here.
			}
		}
		
		long direct, indirect, suitableBlockers, wPawns, bPawns;
		
		for (int player : Player.PLAYERS) {
			var_bitboard_attackedBy[player][PieceType.PAWN] |= BitboardGen.getMultiplePawnAttackSet(game.getPieces(player, PieceType.PAWN), player);

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.KNIGHT),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					var_bitboard_attackedBy[player][PieceType.KNIGHT] |= BitboardGen.getKnightSet(bi);
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.BISHOP),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getBishopSet(bi, game.getOccupied());
					var_bitboard_attackedBy[player][PieceType.BISHOP] |= direct;
					indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~direct) & ~direct;
					var_bitboard_secondary_attackedBy[player][PieceType.BISHOP] |= indirect;
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.ROOK),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getRookSet(bi, game.getOccupied());
					var_bitboard_attackedBy[player][PieceType.ROOK] |= direct;
					indirect = BitboardGen.getRookSet(bi, game.getOccupied() & ~direct) & ~direct;
					var_bitboard_secondary_attackedBy[player][PieceType.ROOK] |= indirect;
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.QUEEN),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getQueenSet(bi, game.getOccupied());
					var_bitboard_attackedBy[player][PieceType.QUEEN] |= direct;
					indirect = BitboardGen.getQueenSet(bi, game.getOccupied() & ~direct) & ~direct;
					var_bitboard_secondary_attackedBy[player][PieceType.QUEEN] |= indirect;
				}
			}

			var_bitboard_attackedBy[player][PieceType.KING] = BitboardGen.getKingSet(game.getKingSquare(player));
			
			//populate batteries
			//todo: the next 3 sections can be skipped conditionally....
			
			{//bishops
				//batteries with sliding pieces
				suitableBlockers = game.getPieces(PieceType.BISHOP) | game.getPieces(PieceType.QUEEN);
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.BISHOP),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						
						direct = BitboardGen.getBishopSet(bi, game.getOccupied());
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & suitableBlockers)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |= indirect;
					}
				}
				//batteries with pawns
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.BISHOP),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);

						wPawns = BitboardGen.getBishopSet(bi, game.getOccupied()) & game.getPieces(Player.WHITE, PieceType.PAWN);
						bPawns = BitboardGen.getBishopSet(bi, game.getOccupied()) & game.getPieces(Player.BLACK, PieceType.PAWN);
						direct = BitboardGen.getBishopSet(bi, game.getOccupied());
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & wPawns)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(wPawns, Player.WHITE);
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & bPawns)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(bPawns, Player.BLACK);
					}
				}
				
			}///bishops
			{//rooks - does not take into account pawn pushes for now - so it is only sliders.
				//batteries with sliding pieces
				suitableBlockers = game.getPieces(PieceType.ROOK) | game.getPieces(PieceType.QUEEN);
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.ROOK),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						
						direct = BitboardGen.getRookSet(bi, game.getOccupied());
						indirect = BitboardGen.getRookSet(bi, game.getOccupied() & ~(direct & suitableBlockers)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.ROOK] |= indirect;
						
					}
				}
				
			}
			
		}//players loop
	}//initialize method
	
	/**
	 * Bitboard for direct attacks [player][attackerType]
	 * Populated by call to initialize() and is unchanged until the next call.
	 * For pawns this is attacks, NOT pushes
	 */
	private long var_bitboard_attackedBy [][]= new long[2][6];//[player][piece type]
	
	/**
	 * Attack sets resulting from lifting up the first blocker.
	 * Bitboard for potential discovered attacks [player][attackerType]
	 * Populated by call to initialize() and is unchanged until the next call.
	 * Only is populated for B R Q
	 * The blocker may be ANYTHING!!!
	 * 
	 * This MAY be useful for avoiding calling the whole SEE minimax routine for squares which are not contested.
	 * 
	 */
	private long var_bitboard_secondary_attackedBy [][]= new long[2][6];//[player][piece type]
	
	/**
	 * Sliding attack sets resulting from lifting up the first blocker, which is itself a sliding piece or a pawn (diagonal attacks).
	 * Bitboard for potential discovered attacks [player][attackerType]
	 * Populated by call to initialize() and is unchanged until the next call.
	 * Only is populated for B R Q
	 * The blocker can only be P B R Q
	 * 
	 * This MAY be useful for avoiding calling the whole SEE minimax routine for squares which are not contested.
	 * 
	 */
	private long var_bitboard_secondary_battery_attackedBy [][]= new long[2][6];//[player][piece type]
	
	/**
	 * Direct attacks broken down by player and piece type. Multiple pieces of the same type are combined together.
	 * For pawns this is attacks AND NOT pushes.
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long getAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return var_bitboard_attackedBy[player][pieceType];
	}
	
	/**
	 * Indirect attacks broken down by player and piece type after the first blocker is lifted.
	 * Multiple pieces of the same type are combined together.
	 * Is only populated for B R Q
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long getSecondaryAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		assert pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
		return var_bitboard_secondary_attackedBy[player][pieceType];
	}
	
	/**
	 * Indirect attacks broken down by player and piece type after the first blocker is lifted.
	 * The blockers considered are B R Q as well as PAWNS for diagonal attacks
	 * Multiple pieces of the same type are combined together.
	 * Is only populated for B R Q
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long getSecondaryBatteryAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		assert pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
		return var_bitboard_secondary_battery_attackedBy[player][pieceType];
	}
	
	/**
	 * denotes capture targets where a given piece type can win material.
	 * EXCLUDES PAWNS AND KINGS!!!!
	 * 
	 * Either the target is undefended, or the defender would not choose to re-capture.
	 */
	private long output_capture_uncontestedOrEnprise [][]= new long[2][6];//[player][piece type]
	

	
	/**
	 * populates the attack stack. Does not minimax. there are weird cases with
	 * reverse batteries and backstabs. a player may be out of attacks, but new
	 * attacks may become available because of discovered attacks
	 * 
	 * The purpose of this function is to generate the sequence of attackers used in
	 * the exchange.
	 * 
	 * @param sq
	 * @param player
	 */
	void initialize_temp_attack_stack(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		temp_attack_stack_size[0]=0;
		temp_attack_stack_size[1]=0;
		int otherPlayer = Player.getOtherPlayer(player);
		long bboard=0;
		long clearedLocations = 0;
		boolean playerDone = false, otherPlayerDone=false;
		int pieceType;
		
		while(!(playerDone && otherPlayerDone)) {
			if(!playerDone) {
				bboard = getLeastValuableAttacker_mask(sq, player, clearedLocations);
				if(bboard != 0l) {
					//TODO: figure out a way to return both type and location form getLeastValuableAttacker_mask to save the extra conversion.
					pieceType = game.getPieceAt(Bitboard.getFirstSquareIndex(bboard));
					clearedLocations |= bboard;
					add_temp_evaluateCapture_attack_stack(player, pieceType);
				}
				else {
					playerDone=true;
				}
			}
			if(!otherPlayerDone) {
				bboard = getLeastValuableAttacker_mask(sq, otherPlayer, clearedLocations);
				if(bboard != 0l) {
					//TODO: figure out a way to return both type and location form getLeastValuableAttacker_mask to save the extra conversion.
					pieceType = game.getPieceAt(Bitboard.getFirstSquareIndex(bboard));
					clearedLocations |= bboard;
					add_temp_evaluateCapture_attack_stack(otherPlayer, pieceType);
				}
				else {
					otherPlayerDone=true;
				}
			}
		}
	}
	
	private int temp_attack_stack[][]= new int [2][16];
	private int temp_attack_stack_size [] = new int[2];
	
	private void add_temp_evaluateCapture_attack_stack(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		temp_attack_stack[player][temp_attack_stack_size[player]++]=pieceType;
	}
	
	private int get_temp_evaluateCapture_attack_stack(int player, int index) {
		assert Player.validate(player);
		assert index < temp_attack_stack_size[player];
		return temp_attack_stack[player][index];
	}
	
	/**
	 * Should only be used for debugging and testing purposes.
	 * @return
	 */
	String debug_dump_temp_evaluateCapture_attack_stack() {
		String ret = "White: ";
		for(int i=0; i<temp_attack_stack_size[0]; ++i)
			ret+=PieceType.toString(get_temp_evaluateCapture_attack_stack(0, i))+" ";
		ret+="| Black: ";
		for(int i=0; i<temp_attack_stack_size[1]; ++i)
			ret+=PieceType.toString(get_temp_evaluateCapture_attack_stack(1, i))+" ";
		return ret;
	}
	
	
	/**
	 * updates the internal state variables using minimax...
	 * @param sq
	 * @param player
	 * @param attacker_type
	 */
	private void evaluateCapture_forcedAttacker(int sq, int player, int attacker_type) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert PieceType.validate(attacker_type);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		assert Bitboard.testBit(getAttackedTargets(player, attacker_type), sq);
		

		
	}
	
	/**
	 * updates the internal state variables using minimax...
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	void evaluateCapture(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		
		initialize_temp_attack_stack(sq, player);

		for(int attacker_type : PieceType.PIECE_TYPES) {
			if(Bitboard.testBit(getAttackedTargets(player, attacker_type), sq))
				evaluateCapture_forcedAttacker(sq, player, attacker_type);
		}
		
		
	}
	
	
	
}
