package basicseeval;

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
		
		// OPTIMIZE: once pawn and knight attack sets are exhausted, there is no point
				// checking them again. This is not true for sliding pieces.
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
	 * var_combined_bitboard_attackedBy,
	 * var_combined_bitboard_secondary_attackedBy,
	 * var_combined_bitboard_secondary_battery_attackedBy
	 * 
	 */
	public void initialize() {
		for(int player : Player.PLAYERS) {
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
	 * local to evaluateCapture_forced and evaluateQuiet_forced
	 */
	private int var_evaluateTarget_attackStack[]=new int[32];//pieceType - both players condensed to same stack.
	/**
	 * local to evaluateCapture_forced and evaluateQuiet_forced
	 */
	private int var_evaluateTarget_gain [] =new int[32];//integer value change - needed for linear minimax
		
	//combined implementation!!!
	int evaluateTargetExchange(int sq, int player, int forced_attacker_type) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert (game.getPieceAt(sq) == PieceType.NO_PIECE
				&& (forced_attacker_type != PieceType.PAWN && Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq)
						|| forced_attacker_type == PieceType.PAWN && Bitboard
								.testBit(BitboardGen.getMultiplePawnPushSet(game.getPieces(player, PieceType.PAWN), player, game.getOccupied()), sq)))
				|| (game.getPieceAt(sq) != PieceType.NO_PIECE && game.getPlayerAt(sq) == Player.getOtherPlayer(player) && Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq))
				: Square.toString(sq) + " " + Player.toString(player) + " " + PieceType.toString(forced_attacker_type);	
		
		int d_combinedAttackStackSize=2;
		int currentPlayer=player;
		long clearedSquares =0;
		
		//all of the temporary and output state variables can be shifted into a separate class using composition
		
		var_evaluateTarget_attackStack[0]=game.getPieceAt(sq);
		var_evaluateTarget_attackStack[1]=forced_attacker_type;
		var_evaluateTarget_gain[0] = game.getPieceAt(sq) == PieceType.NO_PIECE ? 0 : getPieceValue(var_evaluateTarget_attackStack[0]);
		var_evaluateTarget_gain[1] =getPieceValue(var_evaluateTarget_attackStack[1]) - var_evaluateTarget_gain[0];

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
		else
			clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(getLeastValuableAttacker_withType(sq, currentPlayer, forced_attacker_type, 0l)));

		int leastValuableAttacker;
		int nextAttackerType;
		do {
			currentPlayer = Player.getOtherPlayer(currentPlayer);
			leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
			if (leastValuableAttacker == AttackerType.nullValue())
				break;
			nextAttackerType = AttackerType.getAttackerPieceType(leastValuableAttacker);
			clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
			var_evaluateTarget_attackStack[d_combinedAttackStackSize] = nextAttackerType;
			var_evaluateTarget_gain[d_combinedAttackStackSize] =
					getPieceValue(var_evaluateTarget_attackStack[d_combinedAttackStackSize])
					- var_evaluateTarget_gain[d_combinedAttackStackSize - 1];
			
			//this is the short exit condition. we stop iteration if the last recapture did not make the score worthy of being selected.
			//existence of this condition does not change the return value being positive/zero/negative.
			if (Math.max(-var_evaluateTarget_gain[d_combinedAttackStackSize - 1],
					var_evaluateTarget_gain[d_combinedAttackStackSize]) < 0) {
				d_combinedAttackStackSize++;
				break;
			}
			d_combinedAttackStackSize++;
		} while (true);

//System.out.print(game.toFEN() + " ["+ Player.toShortString(player)+PieceType.toString(forced_attacker_type) +
//		(game.getPieceAt(sq) == PieceType.NO_PIECE ? " - " : " x " )
//		+ Square.toString(sq)+ "] sequence: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "()" : PieceType.toString(var_evaluateTarget_attackStack[0])) + " ");
//for(int i=1; i<d_combinedAttackStackSize;++i)
//	System.out.print(PieceType.toString(var_evaluateTarget_attackStack[i]) + " ");
//System.out.print("} values: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "0" : getPieceValue(var_evaluateTarget_attackStack[0])) + " ");
//for(int i=1; i<d_combinedAttackStackSize;++i)
//	System.out.print(getPieceValue(var_evaluateTarget_attackStack[i]) + " ");
//System.out.print("} gains: ");
//for(int i=0; i<d_combinedAttackStackSize-1;++i)
//	System.out.print(var_evaluateTarget_gain[i] + " ");

		//minimax backtracking
		for (int i = d_combinedAttackStackSize-2; i>0; --i) {
			var_evaluateTarget_gain[i-1]= -Math.max(-var_evaluateTarget_gain[i-1], var_evaluateTarget_gain[i]);
		}
		/**
		 * at this point temp_evaluateCapture_forcedAttacker_gain[0] is the expected exchange value IF the forced capture is taken.
		 */
		
//System.out.println();
//System.out.print("last attacker: "+ Player.toShortString(Player.getOtherPlayer(currentPlayer))
//	+ PieceType.toString(var_evaluateTarget_attackStack[d_combinedAttackStackSize-1]));
//System.out.println(" returning: "+ var_evaluateTarget_gain[0]);
//System.out.println();

		if(var_evaluateTarget_gain[0]>0)
			HitCounter.count("evaluateTarget - positive");
		if(var_evaluateTarget_gain[0]<0)
			HitCounter.count("evaluateTarget - negative");
		if(var_evaluateTarget_gain[0]==0)
			HitCounter.count("evaluateTarget - neutral");
		return var_evaluateTarget_gain[0];
	}
	
	/**
	 * assuming we have ran the forced capture routine for the given square and the return value is non-positive for all attackers.
	 * We now want to classify the defenders as either TIED or OVERPROTECTING
	 * in other words: can a given defender be lifted off the board without changing the predicted exchange outcome?
	 * 
	 */
	void evaluateTargetOverpotection(int sq, int player, int anticipatedOutcome) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert OutcomeEnum.validate(anticipatedOutcome);
		
		long processedDefendersBB=0l;
		
		
		/*
		 * similar to evaluateTarget, but the strategy for skipping a defender is
		 * different. might add a parameter to indetify index/ordinal of the defender to
		 * skip.
		 * 
		 * that does not work because the order of defenders is not static (because of
		 * discovered attacks.) will need to do this keeping track of the squares we
		 * already considered clearing
		 */
		
		//loop start here
		//while(true)
		{
			int candidateDefenderSquare = Square.SQUARE_NONE;
			
			int d_combinedAttackStackSize=1;
			int currentPlayer=Player.getOtherPlayer(player);
			long clearedSquares =0;
			
			//all of the temporary and output state variables can be shifted into a separate class using composition
			
			var_evaluateTarget_attackStack[0]=game.getPieceAt(sq);
			var_evaluateTarget_gain[0] = getPieceValue(var_evaluateTarget_attackStack[0]);
	
			int leastValuableAttacker;
			int nextAttackerType;
			do {
				currentPlayer = Player.getOtherPlayer(currentPlayer);
				if(currentPlayer == Player.getOtherPlayer(player) && candidateDefenderSquare == Square.SQUARE_NONE) {
					leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
					candidateDefenderSquare = AttackerType.getAttackerSquareFrom(leastValuableAttacker);
					clearedSquares |= Bitboard.initFromSquare(candidateDefenderSquare);
					
					leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
					if (leastValuableAttacker == AttackerType.nullValue())
						break;
				}
				else {
					leastValuableAttacker = getLeastValuableAttacker(sq, currentPlayer, clearedSquares);
					if (leastValuableAttacker == AttackerType.nullValue())
						break;
				}
				
				nextAttackerType = AttackerType.getAttackerPieceType(leastValuableAttacker);
				clearedSquares |= Bitboard.initFromSquare(AttackerType.getAttackerSquareFrom(leastValuableAttacker));
				var_evaluateTarget_attackStack[d_combinedAttackStackSize] = nextAttackerType;
				var_evaluateTarget_gain[d_combinedAttackStackSize] =
						getPieceValue(var_evaluateTarget_attackStack[d_combinedAttackStackSize])
						- var_evaluateTarget_gain[d_combinedAttackStackSize - 1];
				
				//this is the short exit condition. we stop iteration if the last recapture did not make the score worthy of being selected.
				//existence of this condition does not change the return value being positive/zero/negative.
				if (Math.max(-var_evaluateTarget_gain[d_combinedAttackStackSize - 1],
						var_evaluateTarget_gain[d_combinedAttackStackSize]) < 0) {
					d_combinedAttackStackSize++;
					break;
				}
				d_combinedAttackStackSize++;
			} while (true);
			
//			if(candidateDefenderSquare == Square.SQUARE_NONE)
//				break;
	
	System.out.print(game.toFEN() + " ["+ Player.toShortString(player) +
			(game.getPieceAt(sq) == PieceType.NO_PIECE ? " - " : " x " )
			+ Square.toString(sq)+ "] sequence: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "()" : PieceType.toString(var_evaluateTarget_attackStack[0])) + " ");
	for(int i=1; i<d_combinedAttackStackSize;++i)
		System.out.print(PieceType.toString(var_evaluateTarget_attackStack[i]) + " ");
	System.out.print("} values: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "0" : getPieceValue(var_evaluateTarget_attackStack[0])) + " ");
	for(int i=1; i<d_combinedAttackStackSize;++i)
		System.out.print(getPieceValue(var_evaluateTarget_attackStack[i]) + " ");
	System.out.print("} gains: ");
	for(int i=0; i<d_combinedAttackStackSize-1;++i)
		System.out.print(var_evaluateTarget_gain[i] + " ");
	
			//minimax backtracking
			for (int i = d_combinedAttackStackSize-2; i>0; --i) {
				var_evaluateTarget_gain[i-1]= -Math.max(-var_evaluateTarget_gain[i-1], var_evaluateTarget_gain[i]);
			}
			/**
			 * at this point temp_evaluateCapture_forcedAttacker_gain[0] is the expected exchange value IF the forced capture is taken.
			 */
			
	System.out.println();
	System.out.print("last attacker: "+ Player.toShortString(Player.getOtherPlayer(currentPlayer))
		+ PieceType.toString(var_evaluateTarget_attackStack[d_combinedAttackStackSize-1]));
	System.out.println(" returning: "+ var_evaluateTarget_gain[0]);
	
	
			String str = "evaluateTargetOverprotection of (" + Square.toString(sq)+") | candidate: "+
					Square.toString(candidateDefenderSquare) + " value: " + var_evaluateTarget_gain[0] + " | ";
			if(anticipatedOutcome == OutcomeEnum.NEUTRAL) {
				if(var_evaluateTarget_gain[0] == 0)
					str+= " NEUTRAL (unchanged) ";
				else if (var_evaluateTarget_gain[0] > 0)
					str+= " from NEUTRAL to POSITIVE ";
			}
			else if(anticipatedOutcome == OutcomeEnum.NEGATIVE) {
				if(var_evaluateTarget_gain[0] < 0)
					str+= " NEGATIVE (unchanged) ";
				else if (var_evaluateTarget_gain[0] == 0)
					str+= "from NEGATIVE to NEUTRAL ";
				else
					str+= "from NEGATIVE to POSITIVE ";
			}
			System.out.println(str);
			System.out.println();
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
							score = evaluateTargetExchange(bi, player, pieceType);
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
		//diagnostics!
		for (int player : Player.PLAYERS) {
			long temp=0;
			for (int pieceType : PieceType.PIECE_TYPES) {
				temp|=output_target_winning[player][pieceType];
				temp|=output_target_neutral[player][pieceType];
			}
			for(int sq : Square.SQUARES) {
				if(Bitboard.testBit(temp, sq))
					HitCounter.count("evaluateCaptures: non-negative");
				else
					HitCounter.count("evaluateCaptures: negative");
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
						score = evaluateTargetExchange(sq, player, pieceType);
						if(score < 0)
							output_target_losing[player][pieceType]|= Bitboard.setBit(output_target_losing[player][pieceType], sq);
						else//only two cases - quiet move would never result in positive score.
							output_target_neutral[player][pieceType]|= Bitboard.setBit(output_target_neutral[player][pieceType], sq);
					}
				}
			}
		}
	}
	

	
	
	
}
