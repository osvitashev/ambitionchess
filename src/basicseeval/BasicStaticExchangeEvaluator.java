package basicseeval;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.AttackerType;

/**
 * Takes a board state and performs square-centric static exchange evaluation.
 * This means we do not take into consideration discovered checks or removal of the guard on other targets.
 * 
 * 
 * A fundamental simplification of this approach is to group pieces of the same type together.
 * 
 * Suppose this as the input: 7r/7Q/1k5n/8/1K2n3/4Q3/4r3/8 w - - 0 1
 * 
 * see on Qxe4 and Qxh6 yield totally different results because we are forcing the forcing the first attacker of the matching type.
 * The hope is that such cases would not be any more rare than see being generally incorrect because of conditions elsewhere on the board.
 * I believe a prerequisite for this type of error is that there is a reverse battery/backstab.
 * 
 * */
public class BasicStaticExchangeEvaluator {
	private static final int[] BerlinerPieceValues = { 100, 320, 333, 510, 800, 100000  };
	private static final int[] simpleValues = { 100, 300, 300, 500, 800, 100000  };
	private final int[] pieceValues;// index matches piece type.
	
	private int getPieceValue(int pieceType) {
		assert PieceType.validate(pieceType);
		return pieceValues[pieceType];
	}

	private final Gamestate game;
	
	//should not be re-initialized. Just reset the game state.
	BasicStaticExchangeEvaluator(Gamestate g){
		pieceValues = simpleValues;
		game = g;
	}
	
	/**
	 * returns a mask with either exactly 1 or 0 bits set.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return AttackerType
	 */
	int getLeastValuableAttacker(int sq_target, int player, long clearedLocations) {
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
				return AttackerType.create(PieceType.PAWN, Bitboard.getFirstSquareIndex(candidate_pawns));
		}
		
		{
			long candidate_knights = BitboardGen.getKnightSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KNIGHT);
			if(!Bitboard.isEmpty(candidate_knights))
				return AttackerType.create(PieceType.KNIGHT, Bitboard.getFirstSquareIndex(candidate_knights));
		}
		//OPTIMIZE: once pawn and knight attack sets are exhausted, there is no point checking them again. This is not true for sliding pieces.
		{
			long candidate_bishops = BitboardGen.getBishopSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.BISHOP);
			if(!Bitboard.isEmpty(candidate_bishops))
				return AttackerType.create(PieceType.BISHOP, Bitboard.getFirstSquareIndex(candidate_bishops));
		}
		
		{
			long candidate_rooks = BitboardGen.getRookSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.ROOK);
			if(!Bitboard.isEmpty(candidate_rooks))
				return AttackerType.create(PieceType.ROOK, Bitboard.getFirstSquareIndex(candidate_rooks));
		}
		
		{
			long candidate_queens = BitboardGen.getQueenSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.QUEEN);
			if(!Bitboard.isEmpty(candidate_queens))
				return AttackerType.create(PieceType.QUEEN, Bitboard.getFirstSquareIndex(candidate_queens));
		}
		
		{
			long candidate_kings = BitboardGen.getKingSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KING);
			if(!Bitboard.isEmpty(candidate_kings))
				return AttackerType.create(PieceType.KING, Bitboard.getFirstSquareIndex(candidate_kings));
		}
		
		return AttackerType.nullValue();//fallback
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
				
				output_capture_enprise[player][type] =0;
			}
			var_combined_bitboard_attackedBy[player] =0;
			var_combined_bitboard_secondary_attackedBy[player] =0;
			var_combined_bitboard_secondary_battery_attackedBy[player] =0;
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
			}///rooks
			{//queens
				//batteries with bishops pieces
				suitableBlockers = game.getPieces(PieceType.BISHOP) | game.getPieces(PieceType.QUEEN);
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.QUEEN),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						
						direct = BitboardGen.getBishopSet(bi, game.getOccupied());
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & suitableBlockers)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |= indirect;
					}
				}
				//batteries with rooks
				suitableBlockers = game.getPieces(PieceType.ROOK) | game.getPieces(PieceType.QUEEN);
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.QUEEN),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						
						direct = BitboardGen.getRookSet(bi, game.getOccupied());
						indirect = BitboardGen.getRookSet(bi, game.getOccupied() & ~(direct & suitableBlockers)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |= indirect;
						
					}
				}
				//batteries with pawns
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.QUEEN),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);

						wPawns = BitboardGen.getBishopSet(bi, game.getOccupied()) & game.getPieces(Player.WHITE, PieceType.PAWN);
						bPawns = BitboardGen.getBishopSet(bi, game.getOccupied()) & game.getPieces(Player.BLACK, PieceType.PAWN);
						direct = BitboardGen.getBishopSet(bi, game.getOccupied());
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & wPawns)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(wPawns, Player.WHITE);
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & bPawns)) & ~direct;
						var_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(bPawns, Player.BLACK);
					}
				}
			}///queens
			
			for(int type : PieceType.PIECE_TYPES) {
				var_combined_bitboard_attackedBy[player] |= var_bitboard_attackedBy[player][type];
				var_combined_bitboard_secondary_attackedBy[player] |= var_bitboard_secondary_attackedBy[player][type];
				var_combined_bitboard_secondary_battery_attackedBy[player] |= var_bitboard_secondary_battery_attackedBy[player][type];
			}
			
		}//players loop
	}//initialize method
	
	/**
	 * Bitboard for direct attacks [player][attackerType]
	 * Populated by call to initialize() and is unchanged until the next call.
	 * For pawns this is attacks, NOT pushes
	 */
	private long var_bitboard_attackedBy [][]= new long[2][6];//[player][piece type]
	private long var_combined_bitboard_attackedBy []= new long[2];
	
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
	private long var_combined_bitboard_secondary_attackedBy []= new long[2];
	
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
	private long var_combined_bitboard_secondary_battery_attackedBy  []= new long[2];
	
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
	 * Direct attacks broken down by player and piece type. Multiple pieces of the same type are combined together.
	 * For pawns this is attacks AND NOT pushes.
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long getAttackedTargets(int player) {
		assert Player.validate(player);
		return var_combined_bitboard_attackedBy[player];
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
	 * Multiple pieces of the same type are combined together.
	 * Is only populated for B R Q
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long getSecondaryAttackedTargets(int player) {
		assert Player.validate(player);
		return var_combined_bitboard_secondary_attackedBy[player];
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
	public long getSecondaryBatteryAttackedTargets(int player) {
		assert Player.validate(player);
		return var_combined_bitboard_secondary_battery_attackedBy[player];
	}
	
	/**
	 * Captures where the target piece is unprotected and the defender does not have available re-captures.
	 * 
	 * [player][piece type]
	 */
	private long output_capture_enprise [][]= new long[2][6];
	
	/**
	 * Captures where the target piece is unprotected and the defender does not have available re-captures.
	 * Does not include quiet moves.
	 * 
	 * [player][piece type]
	 */
	public long getOutput_capture_enprise(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_capture_enprise[player][pieceType];
	}

	
	/**
	 * populates the attack stack. Does not minimax. there are weird cases with
	 * reverse batteries and backstabs. a player may be out of attacks, but new
	 * attacks may become available because of discovered attacks
	 * 
	 * The purpose of this function is to generate the sequence of attackers used in
	 * the exchange.
	 * 
	 * The function takes a player parameter AND resets attack stacks for BOTH players.
	 * This is needed for non-capture exchanges.
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
		int attacker;
		long clearedLocations = 0;
		boolean playerDone = false, otherPlayerDone=false;
		
		while(!(playerDone && otherPlayerDone)) {
			if(!playerDone) {
				attacker = getLeastValuableAttacker(sq, player, clearedLocations);
				if(attacker != AttackerType.nullValue()) {
					clearedLocations |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(attacker));
					temp_attack_stack[player][temp_attack_stack_size[player]++]=attacker;
				}
				else {
					playerDone=true;
				}
			}
			if(!otherPlayerDone) {
				attacker = getLeastValuableAttacker(sq, otherPlayer, clearedLocations);
				if(attacker != AttackerType.nullValue()) {
					clearedLocations |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(attacker));
					temp_attack_stack[otherPlayer][temp_attack_stack_size[otherPlayer]++]=attacker;
				}
				else {
					otherPlayerDone=true;
				}
			}
		}
	}
	
	private int temp_attack_stack[][]= new int [2][16];//AttackerType
	private int temp_attack_stack_size [] = new int[2];
	
	
	/**
	 * Returns AttackerType at given index, which contains PieceType and square origin.
	 * @param player
	 * @param index
	 * @return
	 */
	private int get_temp_evaluateCapture_attack_stack(int player, int index) {
		assert Player.validate(player);
		assert index < temp_attack_stack_size[player];
		return temp_attack_stack[player][index];
	}
	
	private int get_temp_evaluateCapture_attack_stack_size(int player) {
		assert Player.validate(player);
		return temp_attack_stack_size[player];
	}
	
	/**
	 * Should only be used for debugging and testing purposes.
	 * @return
	 */
	String debug_dump_temp_evaluateCapture_attack_stack() {		
		String ret = "White: ";
		for(int i=0; i<get_temp_evaluateCapture_attack_stack_size(0); ++i)
			ret+=PieceType.toString(AttackerType.getAttackerPieceType(get_temp_evaluateCapture_attack_stack(0, i)))+" ";
		ret+="| Black: ";
		for(int i=0; i<get_temp_evaluateCapture_attack_stack_size(1); ++i)
			ret+=PieceType.toString(AttackerType.getAttackerPieceType(get_temp_evaluateCapture_attack_stack(1, i)))+" ";
		return ret;
	}
	

	//TODO: this is useful for testing and debugging bu can be removed once unittests are in place.
	private int temp_evaluateCapture_forcedAttacker_pieceType_attackStack[]=new int[32];//pieceType - both players condensed to same stack.
	private int temp_evaluateCapture_forcedAttacker_gain [] =new int[32];//integer value change - needed for linear minimax
	
	//private int evaluateCapture_gain_stack_size;
	
	void evaluateCapture_forcedAttacker(int sq, int player, int forced_attacker_type) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert PieceType.validate(forced_attacker_type);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		assert Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq);
		//todo: this can also hold code for detecting over-protection
		
		int d_combinedAttackStackSize=0;
		//int victim=game.getPieceAt(sq);
		int currentPlayer=player;
		long clearedSquares =0;
		int leastValuableAttacker;
		boolean isForcedAttackerDetected=false;
		int nextAttackerType;
		
		temp_evaluateCapture_forcedAttacker_pieceType_attackStack[d_combinedAttackStackSize]=game.getPieceAt(sq);
		d_combinedAttackStackSize++;
		temp_evaluateCapture_forcedAttacker_pieceType_attackStack[d_combinedAttackStackSize]=forced_attacker_type;
		d_combinedAttackStackSize++;
		
		
		do {
			currentPlayer = Player.getOtherPlayer(currentPlayer);
			//todo: this is unnecessary - we already have the attacker stack pre-calculated.
			//evaluateCapture_occupation_history is different from attacker stack because the forced attacker is swapped to pos[0]
			leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
			
			
			if(leastValuableAttacker == AttackerType.nullValue())
				break;
			nextAttackerType=AttackerType.getAttackerPieceType(leastValuableAttacker);
			if(!isForcedAttackerDetected && nextAttackerType == forced_attacker_type && currentPlayer == player) {//add check for forced attacker
				isForcedAttackerDetected=true;
				//we want to keep same player when calling getLeastValuableAttacker_mask next time
				currentPlayer = Player.getOtherPlayer(currentPlayer);
				clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
				continue;
			}
			
			clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
			temp_evaluateCapture_forcedAttacker_pieceType_attackStack[d_combinedAttackStackSize]=nextAttackerType;
			d_combinedAttackStackSize++;
			
		}while(true);
		
		for (int i = 0; i < d_combinedAttackStackSize; ++i)
			temp_evaluateCapture_forcedAttacker_gain[i] = getPieceValue(temp_evaluateCapture_forcedAttacker_pieceType_attackStack[i]);
		
		// at this point evaluateCapture_occupation_stack contains all of the attackers
		// which would participate in the exchange while alternating sides.
		System.out.print(game.toFEN() + " | Potential Occupiers of ["+ Square.toString(sq)+"]: ");
		for (int i = 0; i < d_combinedAttackStackSize; ++i)
			System.out.print((i % 2 == 0 ? "+" : "-") + PieceType.toString(temp_evaluateCapture_forcedAttacker_pieceType_attackStack[i]) + " ");
		System.out.print(" abs values: ");
		for (int i = 0; i < d_combinedAttackStackSize; ++i)
			System.out.print(temp_evaluateCapture_forcedAttacker_gain[i] + " ");
		System.out.print(" | gain: ");
		{
			int gain=0;
			for (int i = 0; i < d_combinedAttackStackSize-1; ++i) {
				gain+= i%2==0 ? temp_evaluateCapture_forcedAttacker_gain[i] : -temp_evaluateCapture_forcedAttacker_gain[i];
				temp_evaluateCapture_forcedAttacker_gain[i]=gain;
			}
		}
		d_combinedAttackStackSize--;
		for (int i = 0; i < d_combinedAttackStackSize; ++i)
			System.out.print(temp_evaluateCapture_forcedAttacker_gain[i] + " ");
		//System.out.println(" | d_combinedAttackStackSize:"+ d_combinedAttackStackSize);
		
		for (int i = d_combinedAttackStackSize-1; i>0; --i) {
			if(i%2==1)
				temp_evaluateCapture_forcedAttacker_gain[i-1]=Math.min(temp_evaluateCapture_forcedAttacker_gain[i-1], temp_evaluateCapture_forcedAttacker_gain[i]);
			else
				temp_evaluateCapture_forcedAttacker_gain[i-1]=Math.max(temp_evaluateCapture_forcedAttacker_gain[i-1], temp_evaluateCapture_forcedAttacker_gain[i]);
		}
		
		System.out.println(" | val: "+temp_evaluateCapture_forcedAttacker_gain[0]);
		
		/**
		 * at this point temp_evaluateCapture_forcedAttacker_gain[0] is the expected exchange value OF the forced capture is taken.
		 */
		
	}
	
	/**
	 * updates the internal state variables using minimax...
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	void evaluateCaptures(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		
		initialize_temp_attack_stack(sq, player);

		for(int attacker_type : PieceType.PIECE_TYPES) {
			if(Bitboard.testBit(getAttackedTargets(player, attacker_type), sq))
				evaluateCapture_forcedAttacker(sq, player, attacker_type);
		}
		
		
	}
	
	/**
	 * initialize() is guaranteed to have been called earlier.
	 * calculates all of the outputs for all of the 64 squares
	 * Calculates captures only. NOT QUIET MOVES.
	 * @return
	 */
	public void evaluateCaptures() {
		long outstandingCaptureTargets =game.getPlayerPieces(Player.WHITE) & getAttackedTargets(Player.BLACK)
				| game.getPlayerPieces(Player.BLACK) & getAttackedTargets(Player.WHITE);
		
		{
			long unapposedAttacks_white = getAttackedTargets(Player.WHITE) & game.getPlayerPieces(Player.BLACK)
					& ~(getAttackedTargets(Player.BLACK) | getSecondaryBatteryAttackedTargets(Player.BLACK));
			long unapposedAttacks_black = getAttackedTargets(Player.BLACK) & game.getPlayerPieces(Player.WHITE)
					& ~(getAttackedTargets(Player.WHITE) | getSecondaryBatteryAttackedTargets(Player.WHITE));
			for(int type : PieceType.PIECE_TYPES) {
				output_capture_enprise[Player.WHITE][type] |= unapposedAttacks_white & getAttackedTargets(Player.WHITE, type);
				output_capture_enprise[Player.BLACK][type] |= unapposedAttacks_black & getAttackedTargets(Player.BLACK, type);
			}
			outstandingCaptureTargets &= ~unapposedAttacks_white;
			outstandingCaptureTargets &= ~unapposedAttacks_black;
		}
		
		//add a logger for the remaining target count
		for(int player : Player.PLAYERS) {
			for(int type : PieceType.PIECE_TYPES) {

			}
		}
		assert outstandingCaptureTargets==0 : "outstandingCaptureTargets is: " + outstandingCaptureTargets;
	}
	
	
	
}
