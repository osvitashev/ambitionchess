package basicseeval;

import java.util.Arrays;

import analysis.Interaction;
import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.AttackerType;
import util.HitCounter;
import util.Utilities.OutcomeEnum;

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
	BasicStaticExchangeEvaluator(Gamestate g, int pieceValuesCode){
		if(pieceValuesCode == 1)
			pieceValues = simpleValues;
		else
			pieceValues = BerlinerPieceValues;
		game = g;
	}
	
	private int getLeastValuableAttacker_withType(int sq_target, int player, int pieceType, long clearedLocations) {
		assert Square.validate(sq_target);
		assert Player.validate(player);
		assert PieceType.validate(pieceType);

		switch (pieceType) {
		case PieceType.PAWN: {
			long targetMask = Bitboard.initFromSquare(sq_target);
			long candidate_pawns = 0;
			if (Player.isWhite(player))
				candidate_pawns = Bitboard.shiftSouth(Bitboard.shiftEast(targetMask)) | Bitboard.shiftSouth(Bitboard.shiftWest(targetMask));
			else
				candidate_pawns = Bitboard.shiftNorth(Bitboard.shiftEast(targetMask)) | Bitboard.shiftNorth(Bitboard.shiftWest(targetMask));
			candidate_pawns &= game.getPieces(player, PieceType.PAWN) & ~clearedLocations;
			if (!Bitboard.isEmpty(candidate_pawns))
				return AttackerType.create(PieceType.PAWN, Bitboard.getFirstSquareIndex(candidate_pawns));
		}
			break;
		case PieceType.KNIGHT: {
			long candidate_knights = BitboardGen.getKnightSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KNIGHT);
			if (!Bitboard.isEmpty(candidate_knights))
				return AttackerType.create(PieceType.KNIGHT, Bitboard.getFirstSquareIndex(candidate_knights));
		}
			break;
		case PieceType.BISHOP: {
			long candidate_bishops = BitboardGen.getBishopSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.BISHOP);
			if (!Bitboard.isEmpty(candidate_bishops))
				return AttackerType.create(PieceType.BISHOP, Bitboard.getFirstSquareIndex(candidate_bishops));
		}
			break;
		case PieceType.ROOK: {
			long candidate_rooks = BitboardGen.getRookSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.ROOK);
			if (!Bitboard.isEmpty(candidate_rooks))
				return AttackerType.create(PieceType.ROOK, Bitboard.getFirstSquareIndex(candidate_rooks));
		}
			break;
		case PieceType.QUEEN: {
			long candidate_queens = BitboardGen.getQueenSet(sq_target, game.getOccupied() & ~clearedLocations) & ~clearedLocations
					& game.getPieces(player, PieceType.QUEEN);
			if (!Bitboard.isEmpty(candidate_queens))
				return AttackerType.create(PieceType.QUEEN, Bitboard.getFirstSquareIndex(candidate_queens));
		}
			break;
		case PieceType.KING: {
			long candidate_kings = BitboardGen.getKingSet(sq_target) & ~clearedLocations & game.getPieces(player, PieceType.KING);
			if (!Bitboard.isEmpty(candidate_kings))
				return AttackerType.create(PieceType.KING, Bitboard.getFirstSquareIndex(candidate_kings));
		}
		default:
			break;
		}
		return AttackerType.nullValue();// fallback
	}
	
	/**
	 * returns a mask with either exactly 1 or 0 bits set.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return AttackerType
	 */
	int getLeastValuableAttacker(int sq_target, int player, long clearedLocations) {
		//todo: right now these are calculated from game state. The calculation can instead be based on var_bitboard_attackedBy
		// OPTIMIZE: once pawn and knight attack sets are exhausted, there is no point
				// checking them again. This is not true for sliding pieces.
		//consider: another possibility is to remember the last returned type to jump to in on the consecutive call. For the purpose of this, the types can be thought of as a ring.
		assert Square.validate(sq_target);
		assert Player.validate(player);
		
		int attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.PAWN, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;
		
		attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.KNIGHT, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;		
		
		attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.BISHOP, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;
		
		attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.ROOK, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;
		
		attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.QUEEN, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;
		
		attacker = getLeastValuableAttacker_withType(sq_target, player, PieceType.KING, clearedLocations);
		if(attacker != AttackerType.nullValue())
			return attacker;
		
		return AttackerType.nullValue();//fallback
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
		
		for(int player : Player.PLAYERS) {
			output_target_isExchangeProcessed[player]=0;
			for(int type : PieceType.PIECE_TYPES) {
				var_bitboard_attackedBy[player][type] =0;
				var_bitboard_secondary_attackedBy[player][type] =0;
				var_bitboard_secondary_battery_attackedBy[player][type] =0;
				//todo: reset the output variables here.
				
				output_target_winning[player][type] =0;
				output_target_neutral[player][type] =0;
				output_target_losing[player][type] =0;
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
	 * @return a formatted string with all of the output variables
	 */
	public String debug_getAllOutputs() {
		String ret = game.toFEN() + "\n";
		//CONSIDER: check the popcount and if it is low, list out individual square strings instead of the hex long.
		ret += "direct attacks:\n";
		for (int player : Player.PLAYERS) {
			ret += (player == Player.WHITE ? "WHITE" : "BLACK") + ": " + "0x"
					+ String.format("%08X", getAttackedTargets(player)) + "\n";

			for (int pieceType : PieceType.PIECE_TYPES) {
				if (getAttackedTargets(player, pieceType) != 0l)
					ret += "\t" + Player.toShortString(player) + PieceType.toString(pieceType) + " = " + "0x"
							+ String.format("%08X", getAttackedTargets(player, pieceType)) + "\n";
			}
		}
		
		ret += "secondary attacks:\n";
		for (int player : Player.PLAYERS) {
		ret +=(player == Player.WHITE ? "WHITE" : "BLACK") + ": "
				+ "0x"+String.format("%08X", getSecondaryAttackedTargets(player)) + "\n";
		
			for (int pieceType : PieceType.SLIDING_PIECE_TYPES) {
				if(getSecondaryAttackedTargets(player,pieceType)!=0l)
				ret += "\t"+Player.toShortString(player) + PieceType.toString(pieceType) + " = "
						+ "0x"+String.format("%08X", getSecondaryAttackedTargets(player,pieceType)) + "\n";
			}
		}
		
		ret += "secondary batteries:\n";
		for (int player : Player.PLAYERS) {
		ret += (player == Player.WHITE ? "WHITE" : "BLACK") + ": "
				+ "0x"+String.format("%08X", getSecondaryBatteryAttackedTargets(player)) + "\n";
		
			for (int pieceType : PieceType.SLIDING_PIECE_TYPES) {
				if(getSecondaryBatteryAttackedTargets(player,pieceType)!=0l)
				ret += "\t"+Player.toShortString(player) + PieceType.toString(pieceType) + " = "
						+ "0x"+String.format("%08X", getSecondaryBatteryAttackedTargets(player,pieceType)) + "\n";
			}
		}
		ret+="Static Exchange Evaluation:\n";
		//exchanges
		for (int player : Player.PLAYERS) {
			ret += (player == Player.WHITE ? "WHITE" : "BLACK")+" processed exchange targets: " + "0x"
					+ String.format("%08X", getOutput_target_isExchangeProcessed(player)) + "\n";
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(getOutput_capture_winning(player, pieceType) !=0l)
					ret+="\tcapture winning "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", getOutput_capture_winning(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(getOutput_capture_neutral(player, pieceType) !=0l)
					ret+="\tcapture neutral "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", getOutput_capture_neutral(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(getOutput_capture_losing(player, pieceType) !=0l)
					ret+="\tcapture losing "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", getOutput_capture_losing(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(getOutput_quiet_neutral(player, pieceType) !=0l)
					ret+="\tquiet neutral "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", getOutput_quiet_neutral(player, pieceType)) + "\n";
			}
			for (int pieceType : PieceType.PIECE_TYPES) {
				if(getOutput_quiet_losing(player, pieceType) !=0l)
					ret+="\tquiet losing "+Player.toShortString(player) + PieceType.toString(pieceType)+
					": 0x"+String.format("%08X", getOutput_quiet_losing(player, pieceType)) + "\n";
			}
		}
		{//interactions
			ret+="Interactions:";
			int[] defenderInteractions = new int[get_output_defenderInteractions_size()];
			for(int i =0; i<get_output_defenderInteractions_size();++i) {
				defenderInteractions[i]=get_output_defenderInteractions(i);
			}
			Integer[] objectArray = Arrays.stream(defenderInteractions).boxed().toArray(Integer[]::new);
			Arrays.sort(objectArray, (a, b) -> Interaction.getTarget(a) - Interaction.getTarget(b));
			defenderInteractions = Arrays.stream(objectArray).mapToInt(Integer::intValue).toArray();
			int prevSquare=Square.SQUARE_NONE;
			for(int i =0; i<get_output_defenderInteractions_size();++i) {
				if(prevSquare != Interaction.getTarget(defenderInteractions[i]))
					ret+="\nTo "+Square.toString(Interaction.getTarget(defenderInteractions[i])) +"\n";
				prevSquare = Interaction.getTarget(defenderInteractions[i]);
				ret+=Interaction.toString(defenderInteractions[i]) +" ";
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
	
	private long output_target_winning [][]= new long[2][6];
	private long output_target_neutral [][]= new long[2][6];
	private long output_target_losing [][]= new long[2][6];
	
	private long output_target_isExchangeProcessed []= new long[2];
	
	/**
	 * true if evaluateTargetExchange has been called for a given square. Is a prerequisite for output_target_winning, output_target_neutral, output_target_losing.
	 * @param player
	 * @return
	 */
	public long getOutput_target_isExchangeProcessed(int player) {
		assert Player.validate(player);
		return output_target_isExchangeProcessed[player];
	}
	
	
	/**
	 * Captures with strictly positive score
	 */
	public long getOutput_capture_winning(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_winning[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * Captures with neutral score
	 */
	public long getOutput_capture_neutral(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_neutral[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * Captures with negative score
	 */
	public long getOutput_capture_losing(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_losing[player][pieceType] & game.getOccupied();
	}
	
	/**
	 * Quiet with neutral score
	 */
	public long getOutput_quiet_neutral(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_neutral[player][pieceType] & ~game.getOccupied();
	}
	
	/**
	 * Quiet with negative score
	 */
	public long getOutput_quiet_losing(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return output_target_losing[player][pieceType] & ~game.getOccupied();
	}

	/**
	 * local to all of the linear minimax swap functions such as evaluateTargetExchange
	 */
	private int var_evaluateTarget_attackTypeStack[]=new int[32];//pieceType - both players condensed to same stack.
	private int var_evaluateTarget_attackSquareStack[]=new int[32];
	/**
	 * local to all of the linear minimax swap functions such as evaluateTargetExchange
	 */
	private int var_evaluateTarget_gain [] =new int[32];//integer value change - needed for linear minimax
	
	private static final boolean ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS = false;
	private static final boolean ENABLE_EVALUATE_TARGET_BOUND_DEFENDERS_DEBUG_STATEMENTS = false;
	
	private int var_evaluateTargetExchange_score, var_evaluateTargetExchange_occupierPieceType, var_evaluateTargetExchange_occupierPlayer,
		var_evaluateTargetExchange_principleLineLastIndex;
	
	int get_evaluateTargetExchange_score() {
		return var_evaluateTargetExchange_score;
	}
	
	int get_evaluateTargetExchange_occupierPieceType() {
		assert PieceType.validate(var_evaluateTargetExchange_occupierPieceType);
		return var_evaluateTargetExchange_occupierPieceType;
	}
	
	int get_evaluateTargetExchange_occupierPlayer() {
		assert Player.validate(var_evaluateTargetExchange_occupierPlayer);
		return var_evaluateTargetExchange_occupierPlayer;
	}
	
	int get_evaluateTargetExchange_principleLineLastIndex() {
		return var_evaluateTargetExchange_principleLineLastIndex;
	}
	
	/**
	 * returns square of the piece at the given index of the principle line of the exchange.
	 * @param index
	 * @return square
	 */
	int get_evaluateTargetExchange_principleLine_square(int index) {
		assert index <= get_evaluateTargetExchange_principleLineLastIndex() && index >=0;
		assert Square.validate(var_evaluateTarget_attackSquareStack[index]);
		return var_evaluateTarget_attackSquareStack[index];
	}
	
	/**
	 * 
	 * @param sq
	 * @param player
	 * @param forced_attacker_type PieceType. NO_PIECE indicates that we are evaluating natural attack order.
	 * @return whether there is an available capture.
	 */
	boolean evaluateTargetExchange(int sq, int player, int forced_attacker_type) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPieceAt(sq) == PieceType.NO_PIECE ?
			(//bool expression - quiet move validation
				((forced_attacker_type == PieceType.NO_PIECE) ?
					(//natural attack order quiet move -  not currently supported
						false
					)
					:
					(//quiet move with a specified forced type
						forced_attacker_type != PieceType.PAWN && Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq)
						||
						forced_attacker_type == PieceType.PAWN && Bitboard.testBit(BitboardGen.getMultiplePawnPushSet(game.getPieces(player, PieceType.PAWN), player, game.getOccupied()), sq)
					)
				)
			)
			:
			(//bool expression - capture move validation
				((forced_attacker_type == PieceType.NO_PIECE) ?
					(//natural attack order capture move
						game.getPlayerAt(sq) == Player.getOtherPlayer(player)
					)
					:
					(//capture move with a specified forced type
						game.getPlayerAt(sq) == Player.getOtherPlayer(player) && Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq)
					)
				)
			);
		
		
		int d_combinedAttackStackSize=1;
		int currentPlayer=Player.getOtherPlayer(player);
		long clearedSquares =0;
		
		if(forced_attacker_type != PieceType.NO_PIECE) {
			d_combinedAttackStackSize++;
			currentPlayer = player;
		}
			
		
		//all of the temporary and output state variables can be shifted into a separate class using composition
		
		var_evaluateTarget_attackTypeStack[0]=game.getPieceAt(sq);
		var_evaluateTarget_attackSquareStack[0]=sq;
		if(forced_attacker_type != PieceType.NO_PIECE)
			var_evaluateTarget_attackTypeStack[1]=forced_attacker_type;
		var_evaluateTarget_gain[0] = game.getPieceAt(sq) == PieceType.NO_PIECE ? 0 : getPieceValue(var_evaluateTarget_attackTypeStack[0]);
		if(forced_attacker_type != PieceType.NO_PIECE)
			var_evaluateTarget_gain[1] =getPieceValue(var_evaluateTarget_attackTypeStack[1]) - var_evaluateTarget_gain[0];

		if(forced_attacker_type == PieceType.PAWN && game.getPieceAt(sq) == PieceType.NO_PIECE) {
			long targetbb = Bitboard.initFromSquare(sq);
			long pawns = game.getPieces(player, PieceType.PAWN);
			if(player == Player.WHITE) {
				targetbb = Bitboard.shiftSouth(targetbb);
				if((pawns & targetbb) != 0)
					clearedSquares |= targetbb;
				else
					clearedSquares |= Bitboard.shiftSouth(targetbb);
			}
			else {
				targetbb = Bitboard.shiftNorth(targetbb);
				if((pawns & targetbb) != 0)
					clearedSquares |= targetbb;
				else
					clearedSquares |= Bitboard.shiftNorth(targetbb);
			}
		}
		else if(forced_attacker_type != PieceType.NO_PIECE) {
			int lva = getLeastValuableAttacker_withType(sq, currentPlayer, forced_attacker_type, 0l);
			var_evaluateTarget_attackSquareStack[1] = AttackerType.getAttackerSquareFrom(lva);
			clearedSquares |= Bitboard.initFromSquare(var_evaluateTarget_attackSquareStack[1]);
		}
			
		int leastValuableAttacker;
		int nextAttackerType, prevVictim;
		do {
			currentPlayer = Player.getOtherPlayer(currentPlayer);
			leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
			if (leastValuableAttacker == AttackerType.nullValue())
				break;
			nextAttackerType = AttackerType.getAttackerPieceType(leastValuableAttacker);
			clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
			var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize] = nextAttackerType;
			var_evaluateTarget_attackSquareStack[d_combinedAttackStackSize] = AttackerType.getAttackerSquareFrom(leastValuableAttacker);
			var_evaluateTarget_gain[d_combinedAttackStackSize] = getPieceValue(var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize])
					- var_evaluateTarget_gain[d_combinedAttackStackSize - 1];
			prevVictim = var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize - 1];
			d_combinedAttackStackSize++;
			// this is the short exit condition. we stop iteration if the last recapture did
			// not make the score worthy of being selected.
			// existence of this condition does not change the return value being
			// positive/zero/negative.

			// pretty sure this condition is incorrect because of wrong offset
			if (Math.max(-var_evaluateTarget_gain[d_combinedAttackStackSize - 2], var_evaluateTarget_gain[d_combinedAttackStackSize - 1]) < 0) {
				break;
			}
			if (prevVictim == PieceType.KING)
				break;
		} while (true);
		
		
				

if(ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS) {
System.out.print(game.toFEN() + " [" +
		((forced_attacker_type == PieceType.NO_PIECE) ? "natural " : "forced ")+ Player.toShortString(player) + "??" +
		(game.getPieceAt(sq) == PieceType.NO_PIECE ? " - " : " x " )
		+ Square.toString(sq)+ "] sequence: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "()" :
			((player==Player.BLACK ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[0]))) 
		+ " ");
for(int i=1; i<d_combinedAttackStackSize;++i)
	System.out.print((player==Player.BLACK ^ i%2==1 ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} values: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "0" : getPieceValue(var_evaluateTarget_attackTypeStack[0])) + " ");
for(int i=1; i<d_combinedAttackStackSize;++i)
	System.out.print(getPieceValue(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} gains: ");
for(int i=0; i<d_combinedAttackStackSize-1;++i)
	System.out.print(var_evaluateTarget_gain[i] + " ");
}
		
		//minimax backtracking
		var_evaluateTargetExchange_principleLineLastIndex=d_combinedAttackStackSize-1;
		for (int i = d_combinedAttackStackSize-2; i>0; --i) {
			var_evaluateTarget_gain[i-1]= -Math.max(-var_evaluateTarget_gain[i-1], var_evaluateTarget_gain[i]);
			if(var_evaluateTarget_gain[i-1] != -var_evaluateTarget_gain[i])
				var_evaluateTargetExchange_principleLineLastIndex = i;
		}
		var_evaluateTargetExchange_score = var_evaluateTarget_gain[0];
		var_evaluateTargetExchange_occupierPlayer = var_evaluateTargetExchange_principleLineLastIndex%2 == 1 ? player : Player.getOtherPlayer(player);
		var_evaluateTargetExchange_occupierPieceType = var_evaluateTarget_attackTypeStack[var_evaluateTargetExchange_principleLineLastIndex];

				/**
		 * at this point temp_evaluateCapture_forcedAttacker_gain[0] is the expected exchange value IF the forced capture is taken.
		 */
if(ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS) {
System.out.println();
System.out.print("last attacker: "+ Player.toShortString(Player.getOtherPlayer(currentPlayer))
	+ PieceType.toString(var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize-1]));
System.out.print(" | principal line: { ");
for(int i=0; i<=var_evaluateTargetExchange_principleLineLastIndex;++i)
	System.out.print(Square.toString(var_evaluateTarget_attackSquareStack[i]) + " ");

	System.out.print("} | last expected occupier: "+ Player.toShortString(var_evaluateTargetExchange_occupierPlayer)
	+ PieceType.toString(var_evaluateTargetExchange_occupierPieceType) + " ("+var_evaluateTargetExchange_principleLineLastIndex+")");
System.out.println(" returning: "+ var_evaluateTarget_gain[0]);
System.out.println();
}
		if(var_evaluateTargetExchange_principleLineLastIndex == 0)
			return false;
		return true;
	}
	
	private int output_defenderInteractions[] = new int[100];
	private int output_defenderInteractions_size;
	
	public int get_output_defenderInteractions(int index) {
		assert index<output_defenderInteractions_size;
		return output_defenderInteractions[index];
	}
	
	public int get_output_defenderInteractions_size() {
		return output_defenderInteractions_size;
	}
	
	private String debug_attackStackToString(int len) {
		String ret="? ";
		for(int i=1; i<len;++i)
			ret+=PieceType.toString(var_evaluateTarget_attackTypeStack[i]) + " ";
		return ret;
	}
	
	/**
	 * lifts defenders off the board one by one to see if it results in a change of expected score.
	 * prerequisite: forced evaluation has been previously run for the target square!!!!
	 */
	void evaluateTargetBoundDefenders(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPieceAt(sq) != PieceType.NO_PIECE;
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		assert Bitboard.testBit(getOutput_target_isExchangeProcessed(player), sq) : "regular/forced exchange must be evaluated first.";
		//consider: adding assertion to filter out exchanges which are initially positive for the attacker.
		
		
		long processedDefendersBB=0l;
		int naturalExchangeOutcome;
		
		
		//loop start here
		while(true)
		{
			int candidateDefenderSquare = Square.SQUARE_NONE;
			
			int d_combinedAttackStackSize=1;
			int currentPlayer=Player.getOtherPlayer(player);
			long clearedSquares =0;
			var_evaluateTarget_attackTypeStack[0]=game.getPieceAt(sq);
			var_evaluateTarget_gain[0] = getPieceValue(var_evaluateTarget_attackTypeStack[0]);
			int leastValuableAttacker;
			int nextAttackerType, prevVictim;
			do {
				currentPlayer = Player.getOtherPlayer(currentPlayer);
				leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
				if (leastValuableAttacker == AttackerType.nullValue())
					break;
				if (currentPlayer == Player.getOtherPlayer(player) && candidateDefenderSquare == Square.SQUARE_NONE
						&& !Bitboard.testBit(processedDefendersBB, AttackerType.getAttackerSquareFrom(leastValuableAttacker))) {
					candidateDefenderSquare = AttackerType.getAttackerSquareFrom(leastValuableAttacker);

					clearedSquares |= Bitboard.initFromSquare(candidateDefenderSquare);
					processedDefendersBB |= Bitboard.initFromSquare(candidateDefenderSquare);
					leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
					if (leastValuableAttacker == AttackerType.nullValue())
						break;
				} else if (leastValuableAttacker == AttackerType.nullValue())
					break;

				nextAttackerType = AttackerType.getAttackerPieceType(leastValuableAttacker);
				clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
				var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize] = nextAttackerType;
				var_evaluateTarget_gain[d_combinedAttackStackSize] = getPieceValue(var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize])
						- var_evaluateTarget_gain[d_combinedAttackStackSize - 1];
				prevVictim = var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize - 1];
				d_combinedAttackStackSize++;
				// this is the short exit condition. we stop iteration if the last recapture did
				// not make the score worthy of being selected.
				// existence of this condition does not change the return value being
				// positive/zero/negative.
				if (Math.max(-var_evaluateTarget_gain[d_combinedAttackStackSize - 2], var_evaluateTarget_gain[d_combinedAttackStackSize - 1]) < 0)
					break;
				if (prevVictim == PieceType.KING)
					break;
			} while (true);
			if(candidateDefenderSquare == Square.SQUARE_NONE)
				break;
if(ENABLE_EVALUATE_TARGET_BOUND_DEFENDERS_DEBUG_STATEMENTS) {
System.out.println();
System.out.print(game.toFEN() + " ["+ Player.toShortString(player) + (game.getPieceAt(sq) == PieceType.NO_PIECE ? " - " : " x " ) + Square.toString(sq)+ "] sequence: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "()" :
	((player==Player.BLACK ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[0]))) + " ");
for(int i=1; i<d_combinedAttackStackSize;++i)
	System.out.print((player==Player.BLACK ^ i%2==1 ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} values: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "0" : getPieceValue(var_evaluateTarget_attackTypeStack[0])) + " ");
for(int i=1; i<d_combinedAttackStackSize;++i)
	System.out.print(getPieceValue(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} gains: ");
for(int i=0; i<d_combinedAttackStackSize-1;++i)
	System.out.print(var_evaluateTarget_gain[i] + " ");
}
			//minimax backtracking
			var_evaluateTargetExchange_principleLineLastIndex=d_combinedAttackStackSize-1;
			for (int i = d_combinedAttackStackSize-2; i>0; --i) {
				var_evaluateTarget_gain[i-1]= -Math.max(-var_evaluateTarget_gain[i-1], var_evaluateTarget_gain[i]);
				if(var_evaluateTarget_gain[i-1] != -var_evaluateTarget_gain[i])
					var_evaluateTargetExchange_principleLineLastIndex = i;
			}
			
			if(Bitboard.testBit(getOutput_capture_losing(player, var_evaluateTarget_attackTypeStack[1]), sq))
				naturalExchangeOutcome = OutcomeEnum.NEGATIVE;
			else if(Bitboard.testBit(getOutput_capture_neutral(player, var_evaluateTarget_attackTypeStack[1]), sq))
				naturalExchangeOutcome = OutcomeEnum.NEUTRAL;
			else
				naturalExchangeOutcome = OutcomeEnum.POSITIVE;
			
if(ENABLE_EVALUATE_TARGET_BOUND_DEFENDERS_DEBUG_STATEMENTS) {
System.out.println();
System.out.print("last attacker: "+ Player.toShortString(Player.getOtherPlayer(currentPlayer))
+ PieceType.toString(var_evaluateTarget_attackTypeStack[d_combinedAttackStackSize-1]) +
" | last expected occupier: "+ (player==Player.BLACK ^ var_evaluateTargetExchange_principleLineLastIndex%2==1 ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[var_evaluateTargetExchange_principleLineLastIndex]) + " ("+var_evaluateTargetExchange_principleLineLastIndex+")");System.out.println(" returning: "+ var_evaluateTarget_gain[0]);
String str = "evaluateTargetOverprotection of (" + Square.toString(sq)+") | natural exchange "+ OutcomeEnum.toString(naturalExchangeOutcome) + " | "+
		" lifted defender: "+ Square.toString(candidateDefenderSquare) + " value: " + var_evaluateTarget_gain[0] + " | ";
if(var_evaluateTarget_gain[0] < 0)
	str+= "new anticipated outcome: negative ";
else if (var_evaluateTarget_gain[0] == 0)
	str+= "new anticipated outcome: neutral ";
else
	str+= "new anticipated outcome: positive ";
System.out.println(str);
}
			if(naturalExchangeOutcome == OutcomeEnum.NEGATIVE) {
				if(var_evaluateTarget_gain[0] > 0) {
					output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_negativeToPositive(candidateDefenderSquare, sq);
				}
				else if(var_evaluateTarget_gain[0] == 0) {
					output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_negativeToNeutral(candidateDefenderSquare, sq);
				}
			}
			else if(naturalExchangeOutcome == OutcomeEnum.NEUTRAL) {
				if(var_evaluateTarget_gain[0] > 0) {
					output_defenderInteractions[output_defenderInteractions_size++]=Interaction.createGuardBound_neutralToPositive(candidateDefenderSquare, sq);
				}
			}
		}//candidate defender loop
		
	}
	

	void evaluateCaptures() {
		int score;
		long directAttackTargets;
		//consider: on one hand we want to be using heuristics to avoid calling forced attacker routine, on the other hand, that routine can implement sideeffects such as detecting overprotection.
		for (int player : Player.PLAYERS) {
			for (int pieceType : PieceType.PIECE_TYPES) {
				directAttackTargets = getAttackedTargets(player, pieceType);
				{
					int bi = 0;
					for (long zarg = directAttackTargets,
							barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
						bi = Bitboard.getFirstSquareIndex(barg);
						if (Bitboard.testBit(getAttackedTargets(player, pieceType), bi) && game.getPlayerAt(bi) == Player.getOtherPlayer(player)) {
							evaluateTargetExchange(bi, player, pieceType);
							score = get_evaluateTargetExchange_score();
							output_target_isExchangeProcessed[player] = Bitboard.setBit(output_target_isExchangeProcessed[player], bi);
							if(score < 0)
								output_target_losing[player][pieceType]|= Bitboard.setBit(output_target_losing[player][pieceType], bi);
							else if(score == 0)
								output_target_neutral[player][pieceType]|= Bitboard.setBit(output_target_neutral[player][pieceType], bi);
							else
								output_target_winning[player][pieceType]|= Bitboard.setBit(output_target_winning[player][pieceType], bi);
						}
					}
				}
			}
		}
	}
	
	void evaluateQuietMoves() {
		int score;
		//consider: on one hand we want to be using heuristics to avoid calling forced attacker routine, on the other hand, that routine can implement sideeffects such as detecting overprotection.
		for (int sq : Square.SQUARES) {
			for (int player : Player.PLAYERS) {
				for (int pieceType : PieceType.PIECE_TYPES) {
					//optimize: taking pawns out of this loop to simplify the condition
					if (game.getPieceAt(sq) == PieceType.NO_PIECE && (pieceType != PieceType.PAWN
							&& Bitboard.testBit(getAttackedTargets(player, pieceType), sq)
							|| pieceType == PieceType.PAWN && Bitboard.testBit(
									BitboardGen.getMultiplePawnPushSet(game.getPieces(player, PieceType.PAWN), player, game.getOccupied()), sq))) {
						//todo: set getOutput_target_isExchangeProcessed
						evaluateTargetExchange(sq, player, pieceType);
						score = get_evaluateTargetExchange_score();
						output_target_isExchangeProcessed[player] = Bitboard.setBit(output_target_isExchangeProcessed[player], sq);
						if(score < 0)
							output_target_losing[player][pieceType]|= Bitboard.setBit(output_target_losing[player][pieceType], sq);
						else//only two cases - quiet move would never result in positive score.
							output_target_neutral[player][pieceType]|= Bitboard.setBit(output_target_neutral[player][pieceType], sq);
					}
				}
			}
		}
	}
	
	void evaluateBoundDefenders() {
		for (int player : Player.PLAYERS) {
			{
				int bi = 0;
				for (long zarg = getOutput_target_isExchangeProcessed(player),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					
					evaluateTargetBoundDefenders(bi, player);
				}
			}
		}
	}
	
	
	
}
