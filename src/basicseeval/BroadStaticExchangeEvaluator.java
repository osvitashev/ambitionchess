package basicseeval;

import java.util.Arrays;

import analysis.Interaction;
import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

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
 * 
 * 
 * 
 * Member naming convention:
 * variables which follow the pattern of "output_***" are set once by their appropriate method and then should not be reassigned
 * 	except after a call to initialize()
 * 
 * variables which follow the pattern of "var_METHODNAME_***" are effectively local to that method. They are used to retrieve multiple return values
 * 	from a single method call. As such, successive calls to METHODNAME are expected to wipe out variable content.
 *
 */
public class BroadStaticExchangeEvaluator {
	private final Gamestate game;
	//private to this object. If we need a more light weight evaluator for static exchange - we can create a stand-alone instance of TargetStaticExchangeEvaluator
	private final TargetStaticExchangeEvaluator targetSEE;
	
	//should not be re-initialized. Just reset the game state.
	public BroadStaticExchangeEvaluator(Gamestate g){
		game = g;
		targetSEE = new TargetStaticExchangeEvaluator(g);
	}
	
	/**
	 * needs to be called after any changes to internal gamestate;
	 * 
	 * Populates:
	 * var_bitboard_attackedBy,
	 * var_bitboard_secondary_attackedBy,
	 * var_bitboard_secondary_battery_attackedBy
	 * as well as their aggregates for both players
	 * 
	 */
	public void initialize() {
		output_defenderInteractions_size=0;
		output_xRayInteractions_size=0;
		
		for(int player : Player.PLAYERS) {
			var_target_isExchangeProcessed[player]=0;
			for(int type : PieceType.PIECE_TYPES) {
				output_bitboard_attackedBy[player][type] =0;
				output_bitboard_secondary_attackedBy[player][type] =0;
				output_bitboard_secondary_battery_attackedBy[player][type] =0;
				
				output_target_winning[player][type] =0;
				output_target_neutral[player][type] =0;
				output_target_losing[player][type] =0;
			}
			output_combined_bitboard_attackedBy[player] =0;
			output_combined_bitboard_secondary_attackedBy[player] =0;
			output_combined_bitboard_secondary_battery_attackedBy[player] =0;
		}
		
		long direct, indirect, suitableBlockers, wPawns, bPawns;
		
		for (int player : Player.PLAYERS) {
			output_bitboard_attackedBy[player][PieceType.PAWN] |= BitboardGen.getMultiplePawnAttackSet(game.getPieces(player, PieceType.PAWN), player);

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.KNIGHT),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					output_bitboard_attackedBy[player][PieceType.KNIGHT] |= BitboardGen.getKnightSet(bi);
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.BISHOP),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getBishopSet(bi, game.getOccupied());
					output_bitboard_attackedBy[player][PieceType.BISHOP] |= direct;
					indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~direct) & ~direct;
					output_bitboard_secondary_attackedBy[player][PieceType.BISHOP] |= indirect;
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.ROOK),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getRookSet(bi, game.getOccupied());
					output_bitboard_attackedBy[player][PieceType.ROOK] |= direct;
					indirect = BitboardGen.getRookSet(bi, game.getOccupied() & ~direct) & ~direct;
					output_bitboard_secondary_attackedBy[player][PieceType.ROOK] |= indirect;
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.QUEEN),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					direct = BitboardGen.getQueenSet(bi, game.getOccupied());
					output_bitboard_attackedBy[player][PieceType.QUEEN] |= direct;
					indirect = BitboardGen.getQueenSet(bi, game.getOccupied() & ~direct) & ~direct;
					output_bitboard_secondary_attackedBy[player][PieceType.QUEEN] |= indirect;
				}
			}

			output_bitboard_attackedBy[player][PieceType.KING] = BitboardGen.getKingSet(game.getKingSquare(player));
			
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
						output_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |= indirect;
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
						output_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(wPawns, Player.WHITE);
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & bPawns)) & ~direct;
						output_bitboard_secondary_battery_attackedBy[player][PieceType.BISHOP] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(bPawns, Player.BLACK);
					}
				}
			}///bishops
			{//rooks - does not take into account pawn pushes for now - so it is only sliders.
				//todo: maybe include rook/queen batteries with pawn pushes?
				//batteries with sliding pieces
				suitableBlockers = game.getPieces(PieceType.ROOK) | game.getPieces(PieceType.QUEEN);
				{
					int bi = 0;
					for (long zarg = game.getPieces(player, PieceType.ROOK),
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						
						direct = BitboardGen.getRookSet(bi, game.getOccupied());
						indirect = BitboardGen.getRookSet(bi, game.getOccupied() & ~(direct & suitableBlockers)) & ~direct;
						output_bitboard_secondary_battery_attackedBy[player][PieceType.ROOK] |= indirect;
						
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
						output_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |= indirect;
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
						output_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |= indirect;
						
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
						output_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(wPawns, Player.WHITE);
						
						indirect = BitboardGen.getBishopSet(bi, game.getOccupied() & ~(direct & bPawns)) & ~direct;
						output_bitboard_secondary_battery_attackedBy[player][PieceType.QUEEN] |=
								indirect & BitboardGen.getMultiplePawnAttackSet(bPawns, Player.BLACK);
					}
				}
			}///queens
			for(int type : PieceType.PIECE_TYPES) {
				output_combined_bitboard_attackedBy[player] |= output_bitboard_attackedBy[player][type];
				output_combined_bitboard_secondary_attackedBy[player] |= output_bitboard_secondary_attackedBy[player][type];
				output_combined_bitboard_secondary_battery_attackedBy[player] |= output_bitboard_secondary_battery_attackedBy[player][type];
			}
		}//players loop
	}//initialize method
	
	
	/**
	 * 
	 * @param sq
	 * @param player
	 * @param clearedSquares
	 * @return given the target square and player, returns location of the pawn able to push into the target square. Or SQUARE_NONE if there is no pawn available.
	 */
	int getPawnPushSourceSquare(int sq, int player, long clearedSquares) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPieceAt(sq) == PieceType.NO_PIECE;
		long bb = Bitboard.initFromSquare(sq);
		long empty = game.getEmpty() | clearedSquares;
		long playerPawns = game.getPieces(player, PieceType.PAWN) & ~clearedSquares;
		
		if (player == Player.WHITE) {
			bb = Bitboard.shiftSouth(bb);
			if (0l != (bb & playerPawns))
				return sq - 8;
			else if (0l != (bb & 0xff0000l) && 0l != (bb & empty)) {
				bb = Bitboard.shiftSouth(bb);
				if (0l != (bb & playerPawns))
					return sq - 16;
			}
		} else {
			bb = Bitboard.shiftNorth(bb);
			if (0l != (bb & playerPawns))
				return sq + 8;
			else if (0l != (bb & 0xff0000000000l) && 0l != (bb & empty)) {
				bb = Bitboard.shiftNorth(bb);
				if (0l != (bb & playerPawns))
					return sq + 16;
			}
		}
		return Square.SQUARE_NONE;
	}
	
	/**
	 * @return a formatted string with all of the output variables
	 */
	public String debug_allOutputsToString() {
		String ret = game.toFEN() + "\n";
		//CONSIDER: check the popcount and if it is low, list out individual square strings instead of the hex long.
		ret += "direct attacks:\n";
		for (int player : Player.PLAYERS) {
			ret += (player == Player.WHITE ? "WHITE" : "BLACK") + ": " + "0x"
					+ String.format("%08X", get_output_attackedTargets(player)) + "\n";

			for (int pieceType : PieceType.PIECE_TYPES) {
				if (get_output_attackedTargets(player, pieceType) != 0l)
					ret += "\t" + Player.toShortString(player) + PieceType.toString(pieceType) + " = " + "0x"
							+ String.format("%08X", get_output_attackedTargets(player, pieceType)) + "\n";
			}
		}
		
		ret += "secondary attacks:\n";
		for (int player : Player.PLAYERS) {
		ret +=(player == Player.WHITE ? "WHITE" : "BLACK") + ": "
				+ "0x"+String.format("%08X", get_output_secondaryAttackedTargets(player)) + "\n";
		
			for (int pieceType : PieceType.SLIDING_PIECE_TYPES) {
				if(get_output_secondaryAttackedTargets(player,pieceType)!=0l)
				ret += "\t"+Player.toShortString(player) + PieceType.toString(pieceType) + " = "
						+ "0x"+String.format("%08X", get_output_secondaryAttackedTargets(player,pieceType)) + "\n";
			}
		}
		
		ret += "secondary batteries:\n";
		for (int player : Player.PLAYERS) {
		ret += (player == Player.WHITE ? "WHITE" : "BLACK") + ": "
				+ "0x"+String.format("%08X", get_output_secondaryBatteryAttackedTargets(player)) + "\n";
		
			for (int pieceType : PieceType.SLIDING_PIECE_TYPES) {
				if(get_output_secondaryBatteryAttackedTargets(player,pieceType)!=0l)
				ret += "\t"+Player.toShortString(player) + PieceType.toString(pieceType) + " = "
						+ "0x"+String.format("%08X", get_output_secondaryBatteryAttackedTargets(player,pieceType)) + "\n";
			}
		}
		ret+="Static Exchange Evaluation:\n";
		//exchanges
		for (int player : Player.PLAYERS) {
			ret += (player == Player.WHITE ? "WHITE" : "BLACK")+" processed exchange targets: " + "0x"
					+ String.format("%08X", get_var_target_isExchangeProcessed(player)) + "\n";
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(get_output_capture_winning(player, pieceType) !=0l)
					ret+="\tcapture winning "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", get_output_capture_winning(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(get_output_capture_neutral(player, pieceType) !=0l)
					ret+="\tcapture neutral "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", get_output_capture_neutral(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(get_output_capture_losing(player, pieceType) !=0l)
					ret+="\tcapture losing "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", get_output_capture_losing(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(get_output_quiet_neutral(player, pieceType) !=0l)
					ret+="\tquiet neutral "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", get_output_quiet_neutral(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(get_output_quiet_losing(player, pieceType) !=0l)
					ret+="\tquiet losing "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", get_output_quiet_losing(player, pieceType)) + "\n";
			}
		}
		{//defensive interactions
			ret+="<><> Defender Interactions: <><>";
			int[] tempInteractions = new int[get_output_defenderInteractions_size()];
			for(int i =0; i<get_output_defenderInteractions_size();++i) {
				tempInteractions[i]=get_output_defenderInteractions(i);
			}
			Integer[] objectArray = Arrays.stream(tempInteractions).boxed().toArray(Integer[]::new);
			Arrays.sort(objectArray, (a, b) -> Interaction.debug_getVictimSquare(a) - Interaction.debug_getVictimSquare(b));
			tempInteractions = Arrays.stream(objectArray).mapToInt(Integer::intValue).toArray();
			int prevSquare=Square.SQUARE_NONE;
			for(int i =0; i<get_output_defenderInteractions_size();++i) {
				if(prevSquare != Interaction.debug_getVictimSquare(tempInteractions[i]))
					ret+="\nTo: "+Square.toString(Interaction.debug_getVictimSquare(tempInteractions[i])) +"\n";
				prevSquare = Interaction.debug_getVictimSquare(tempInteractions[i]);
				ret+=Interaction.toString(tempInteractions[i]) +" ";
			}
			ret+="\n";
		}
		{//Xray interactions
			ret+="<><> X-Ray Interactions: <><>";
			int[] tempInteractions = new int[get_output_xRayInteractions_size()];
			for(int i =0; i<get_output_xRayInteractions_size();++i) {
				tempInteractions[i]=get_output_xRayInteractions(i);
			}
			Integer[] objectArray = Arrays.stream(tempInteractions).boxed().toArray(Integer[]::new);
			Arrays.sort(objectArray, (a, b) -> Interaction.debug_getVictimSquare(a) - Interaction.debug_getVictimSquare(b));
			tempInteractions = Arrays.stream(objectArray).mapToInt(Integer::intValue).toArray();
			int prevSquare=Square.SQUARE_NONE;
			for(int i =0; i<get_output_xRayInteractions_size();++i) {
				if(prevSquare != Interaction.debug_getVictimSquare(tempInteractions[i]))
					ret+="\nTo: "+Square.toString(Interaction.debug_getVictimSquare(tempInteractions[i])) +"\n";
				prevSquare = Interaction.debug_getVictimSquare(tempInteractions[i]);
				ret+=Interaction.toString(tempInteractions[i]) +" ";
			}
			ret+="\n";
		}
		return ret;
	}
	
	/**
	 * Bitboard for direct attacks [player][attackerType]
	 * Populated by call to initialize() and is unchanged until the next call.
	 * For pawns this is attacks, NOT pushes
	 */
	private long output_bitboard_attackedBy [][]= new long[2][6];//[player][piece type]
	private long output_combined_bitboard_attackedBy []= new long[2];
	
	
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
	private long output_bitboard_secondary_attackedBy [][]= new long[2][6];//[player][piece type]
	private long output_combined_bitboard_secondary_attackedBy []= new long[2];
	
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
	private long output_bitboard_secondary_battery_attackedBy [][]= new long[2][6];//[player][piece type]
	private long output_combined_bitboard_secondary_battery_attackedBy  []= new long[2];
	
	/**
	 * Direct attacks broken down by player and piece type. Multiple pieces of the same type are combined together.
	 * For pawns this is attacks AND NOT pushes.
	 * 
	 * The values are populated by calling initialize()
	 * @param player
	 * @param pieceType
	 * @return
	 */
	public long get_output_attackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_bitboard_attackedBy[player][pieceType];
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
	public long get_output_attackedTargets(int player) {
		assert Player.validate(player);
		return output_combined_bitboard_attackedBy[player];
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
	public long get_output_secondaryAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		assert pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
		return output_bitboard_secondary_attackedBy[player][pieceType];
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
	public long get_output_secondaryAttackedTargets(int player) {
		assert Player.validate(player);
		return output_combined_bitboard_secondary_attackedBy[player];
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
	public long get_output_secondaryBatteryAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		assert pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN;
		return output_bitboard_secondary_battery_attackedBy[player][pieceType];
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
	public long get_output_secondaryBatteryAttackedTargets(int player) {
		assert Player.validate(player);
		return output_combined_bitboard_secondary_battery_attackedBy[player];
	}
	
	private long output_target_winning [][]= new long[2][6];
	private long output_target_neutral [][]= new long[2][6];
	private long output_target_losing [][]= new long[2][6];
	
	private long var_target_isExchangeProcessed []= new long[2];
	
	/**
	 * true if evaluateTargetExchange has been called for a given square. Is a prerequisite for output_target_winning, output_target_neutral, output_target_losing.
	 * @param player
	 * @return
	 */
	long get_var_target_isExchangeProcessed(int player) {
		assert Player.validate(player);
		return var_target_isExchangeProcessed[player];
	}
	
	
	/**
	 * Captures with strictly positive score
	 */
	public long get_output_capture_winning(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_winning[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * union of capture targets with strictly positive score
	 */
	public long get_output_capture_winning_any(int player) {
		assert Player.validate(player);
		long ret=0;
		for(int type : PieceType.PIECE_TYPES)
			ret |= output_target_winning[player][type];
		return ret & game.getOccupied();
	}
	
	/**
	 * union of capture targets with strictly positive score
	 */
	public long get_output_capture_neutral_any(int player) {
		assert Player.validate(player);
		long ret=0;
		for(int type : PieceType.PIECE_TYPES)
			ret |= output_target_neutral[player][type];
		return ret & game.getOccupied();
	}
	
	/**
	 * Captures with neutral score
	 */
	public long get_output_capture_neutral(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_neutral[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * Captures with negative score
	 */
	public long get_output_capture_losing(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_losing[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * Quiet with neutral score
	 */
	public long get_output_quiet_neutral(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_neutral[player][pieceType] & ~game.getOccupied();
	}
	
	/**
	 * Quiet with negative score
	 */
	public long get_output_quiet_losing(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_losing[player][pieceType] & ~game.getOccupied();
	}
	
	private int output_defenderInteractions[] = new int[100];
	private int output_defenderInteractions_size;
	
	private int output_xRayInteractions[] = new int[100];
	private int output_xRayInteractions_size;
	
	public int get_output_defenderInteractions(int index) {
		assert index<output_defenderInteractions_size;
		return output_defenderInteractions[index];
	}
	
	public int get_output_defenderInteractions_size() {
		return output_defenderInteractions_size;
	}
	
	public int get_output_xRayInteractions(int index) {
		assert index<output_xRayInteractions_size;
		return output_xRayInteractions[index];
	}
	
	public int get_output_xRayInteractions_size() {
		return output_xRayInteractions_size;
	}
	
	private long getSlidingPieceAttackSet(int sq, int pieceType, long clearedSquares) {
		assert Square.validate(sq);
		assert PieceType.validate(pieceType);
		assert pieceType == PieceType.ROOK || pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN;
		long ret;
		switch (pieceType) {
		case PieceType.ROOK:
			ret=  BitboardGen.getRookSet(sq, game.getOccupied() & ~clearedSquares);
			break;
		case PieceType.BISHOP:
			ret=  BitboardGen.getBishopSet(sq, game.getOccupied() & ~clearedSquares);
			break;
		default:
			ret=  BitboardGen.getQueenSet(sq, game.getOccupied() & ~clearedSquares);
			break;
		}
		return ret;
	}
	
	/**
	 * Tests cases such as putting a knight onto a square attacked by a pawn, or using a queen to capture a bishop defended by a pawn.
	 * Does not guaranty the target is safe - is meant to be used as a soft optimization check!
	 * @param attackerPieceType
	 * @param targetSq
	 * @return
	 */
	boolean isTargetDefinitelyBad(int targetSq, int player, int attackerPieceType) {
		/**
		 * this shold not be using get_lva because get_lva will become stateful eventually?
		 * 
		 * although, get_lva might become 'soft stateful' using the cyclic check! in which case it should be ok to be used here.
		 * Also clearedSquares could become a parameter!
		 */
		//todo: implement this as an optimization check!
		return false;
	}
	
	/**
	 * lifts defenders off the board one by one to see if it results in a change of expected score.
	 * performs a natural order exchange first - and so does not rely on evaluateCaptures having been caleed first.
	 * @param sq - target being defended
	 * @param player - player threatening capture
	 */
	void evaluateTargetBoundDefenders(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPieceAt(sq) != PieceType.NO_PIECE;
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		
		boolean isExchange = targetSEE.evaluateTargetExchange(sq, player, 0l, PieceType.NO_PIECE);
		if(!isExchange)
			return;
		
		int oldScore=targetSEE.get_output_ExpectedGain();
		if(oldScore>0)
			return;
		
		long candidateLiftedSquares = targetSEE.get_output_attackStackSquares() & game.getPlayerPieces(Player.getOtherPlayer(player));
		{//iterate on bit indices
			int bi = 0;
			int liftedSquare;
			for (long zarg = candidateLiftedSquares, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
				bi = Bitboard.getFirstSquareIndex(barg);
				
				liftedSquare = bi;
				
				isExchange = targetSEE.evaluateTargetExchange(sq, player, barg, PieceType.NO_PIECE);
				if(!isExchange)
					continue;
				
				/**
				 * skip if the first attacker does not attack the target directly in natural exchange.
				 * isExchange check has passed, so we are guaranteed at least two items in the stack
				 */
				int candidateSquare = targetSEE.get_output_FirstAttackerSquare();
				int candidatePieceType = targetSEE.get_output_FirstAttackerType();
				long candidateAttackSet;
				switch (candidatePieceType) {
					case PieceType.PAWN:
						candidateAttackSet = BitboardGen.getPawnAttackSet(candidateSquare, player);
						break;
					case PieceType.KNIGHT:
						candidateAttackSet = BitboardGen.getKnightSet(candidateSquare);
						break;
					case PieceType.BISHOP:
						candidateAttackSet = BitboardGen.getBishopSet(candidateSquare, game.getOccupied());
						break;
					case PieceType.ROOK:
						candidateAttackSet = BitboardGen.getRookSet(candidateSquare, game.getOccupied());
						break;
					case PieceType.QUEEN:
						candidateAttackSet = BitboardGen.getQueenSet(candidateSquare, game.getOccupied());
						break;
					default:
						candidateAttackSet = BitboardGen.getKingSet(candidateSquare);
				}
				if(!Bitboard.testBit(candidateAttackSet, sq))
					continue;
				
				if(oldScore<0) {
					if(targetSEE.get_output_ExpectedGain() > 0) {
						output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_negativeToPositive(liftedSquare, sq);
					}
					else if(targetSEE.get_output_ExpectedGain() == 0) {
						output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_negativeToNeutral(liftedSquare, sq);
					}
				}
				else if(oldScore==0) {
					if(targetSEE.get_output_ExpectedGain() > 0) {
						output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_neutralToPositive(liftedSquare, sq);
					}
				}
			}
		} //iterate on bit indices
	}
	
	/**
	 * is evaluated from the perspective of a sliding piece attacker!
	 * @param sq the from_sq
	 * @param player player performing the attack
	 * @param pieceType bish/rook/queen
	 */
	void evaluateTargetXRayInteractions(int sq, int player, int pieceType) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		assert player == game.getPlayerAt(sq);
		assert pieceType == game.getPieceAt(sq);
		assert pieceType == PieceType.ROOK || pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN;
		
		int otherPlayer = Player.getOtherPlayer(player);
		long currentlyWinningCaptureTargets = get_output_capture_winning_any(player);
		long currentlyNeutralCaptureTargets = get_output_capture_neutral_any(player) & ~currentlyWinningCaptureTargets;
		long currentAttackSet = getSlidingPieceAttackSet(sq, pieceType, 0l);
		long candidatePinned = currentAttackSet;

		{//iterate on bit indices
			int bi = 0;
			for (long zarg = candidatePinned, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
				bi = Bitboard.getFirstSquareIndex(barg);
				long newAttackSet = getSlidingPieceAttackSet(sq, pieceType, barg) & ~currentAttackSet & game.getPlayerPieces(otherPlayer);
				if(newAttackSet == 0l)
					continue;
				
				assert Bitboard.hasOnly1Bit(newAttackSet);//lifting the pinned piece can only result in one new victim!
				if (0 != (newAttackSet & currentlyWinningCaptureTargets)) {
					//the victim is one where we already have a winning capture - do nothing!
					continue;
				}
				else if(0 != (newAttackSet & currentlyNeutralCaptureTargets)) {
					//there are no winning captures of the target before the blocking piece is lifted.
					//but there is a neutral one - proceed only if we are improving the score.
					int candidateVictimSq = Bitboard.getFirstSquareIndex(newAttackSet);
					boolean isExchange = targetSEE.evaluateTargetExchange(candidateVictimSq, player, barg, PieceType.NO_PIECE);
					assert(isExchange) : "This is a sanity check: candidateVictimSq is within the new attack set of a sliding piece";
					if (targetSEE.get_output_ExpectedGain() > 0) {
						output_xRayInteractions[output_xRayInteractions_size++] = (otherPlayer == game.getPlayerAt(bi))
								? Interaction.createPin_positive(sq, bi, candidateVictimSq)
								: Interaction.createDiscoveredThreat_positive(sq, bi, candidateVictimSq);
					}
				}
				else {
					//there are no winning or neutral captures of the target before the blocking piece is lifted.
					//there might be a losing capture, or no captures available - proceed depending on the new score.
					int candidateVictimSq = Bitboard.getFirstSquareIndex(newAttackSet);
					boolean isExchange = targetSEE.evaluateTargetExchange(candidateVictimSq, player, barg, PieceType.NO_PIECE);
					assert(isExchange) : "This is a sanity check: candidateVictimSq is within the new attack set of a sliding piece";
					if (targetSEE.get_output_ExpectedGain() > 0) {
						output_xRayInteractions[output_xRayInteractions_size++] = (otherPlayer == game.getPlayerAt(bi))
								? Interaction.createPin_positive(sq, bi, candidateVictimSq)
								: Interaction.createDiscoveredThreat_positive(sq, bi, candidateVictimSq);
					}
					else if (targetSEE.get_output_ExpectedGain() == 0) {
						output_xRayInteractions[output_xRayInteractions_size++] = (otherPlayer == game.getPlayerAt(bi))
								? Interaction.createPin_neutral(sq, bi, candidateVictimSq)
								: Interaction.createDiscoveredThreat_neutral(sq, bi, candidateVictimSq);
					}
				}
			}
		} //iterate on bit indices
	}
	
	public void evaluateCaptures() {
		///a brute force implementation  - used to verify correctness and assertions
		//todo: make the iteration smarter and avoid brute force!
		int score;
		boolean isAvailable;
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				for (int sq : Square.SQUARES) {
					if (game.getPlayerAt(sq) == Player.getOtherPlayer(player)) {
						isAvailable = targetSEE.evaluateTargetExchange(sq, player, 0l, pieceType);
						if (!isAvailable)
							continue;
						score = targetSEE.get_output_ExpectedGain();
						var_target_isExchangeProcessed[player] = Bitboard.setBit(var_target_isExchangeProcessed[player], sq);
						if (score < 0)
							output_target_losing[player][pieceType] |= Bitboard.setBit(output_target_losing[player][pieceType], sq);
						else if (score == 0)
							output_target_neutral[player][pieceType] |= Bitboard.setBit(output_target_neutral[player][pieceType], sq);
						else
							output_target_winning[player][pieceType] |= Bitboard.setBit(output_target_winning[player][pieceType], sq);
					}
				}
			}
		}
	}
	
	public void evaluateQuietMoves() {
		///a brute force implementation  - used to verify correctness and assertions
		//todo: avoid brute forcing approach
		int score;
		boolean isAvailable;
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				for (int sq : Square.SQUARES) {
					if (game.getPieceAt(sq) == PieceType.NO_PIECE) {
						isAvailable = targetSEE.evaluateTargetExchange(sq, player, 0l, pieceType);
						if (!isAvailable)
							continue;
						score = targetSEE.get_output_ExpectedGain();
						var_target_isExchangeProcessed[player] = Bitboard.setBit(var_target_isExchangeProcessed[player], sq);
						if (score < 0)
							output_target_losing[player][pieceType] |= Bitboard.setBit(output_target_losing[player][pieceType], sq);
						else if (score == 0)
							output_target_neutral[player][pieceType] |= Bitboard.setBit(output_target_neutral[player][pieceType], sq);
						else
							output_target_winning[player][pieceType] |= Bitboard.setBit(output_target_winning[player][pieceType], sq);
					}
				}
			}
		}
	}
	
	public void evaluateBoundDefenders() {
		for (int player : Player.PLAYERS) {
			{
				//todo: try using flat square iterator
				int bi = 0;
				for (long zarg = get_var_target_isExchangeProcessed(player),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					
					evaluateTargetBoundDefenders(bi, player);
				}
			}
		}
	}
	
	public void evaluateXRayInteractions() {
		int player, pieceType;
		for (int sq : Square.SQUARES) {
			player = game.getPlayerAt(sq);
			pieceType = game.getPieceAt(sq);
			if(pieceType == PieceType.BISHOP || pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN) {
				evaluateTargetXRayInteractions(sq, player, pieceType);
			}
		}
	}
	
	
}
