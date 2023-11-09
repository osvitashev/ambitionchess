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
	
	/*
	 * this needs to be broken down by player because for quiet moves same target can be available for both players
	 */
	private long [] calculatedMask = new long[2];// [player] - set for squares where the calculation has been done and output can be retrieved
	private int [][] output_exchange_outcome= new int[2][64];//player/square
	
	/**
	 * returns a mask with either exactly 1 or 0 bits set.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	static long static_getLeastValuableAttacker_mask(Gamestate game, int sq_target, int player, long clearedLocations) {
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
				input_attacks[player][type] =0;
				
				outputCaptureTarget_winning[player][type] =0;
				outputCaptureTarget_neutral_occupation[player][type] =0;
				outputCaptureTarget_neutral_nonoccupation[player][type] =0;
			}
			output_is_calculated[player] =0;
		}
		
		for (int player : Player.PLAYERS) {
			input_attacks[player][PieceType.PAWN] |= BitboardGen.getMultiplePawnAttackSet(game.getPieces(player, PieceType.PAWN), player);

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.KNIGHT),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					input_attacks[player][PieceType.KNIGHT] |= BitboardGen.getKnightSet(bi);
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.BISHOP),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					input_attacks[player][PieceType.BISHOP] |= BitboardGen.getBishopSet(bi, game.getOccupied());
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.ROOK),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					input_attacks[player][PieceType.ROOK] |= BitboardGen.getRookSet(bi, game.getOccupied());
				}
			}

			{
				int bi = 0;
				for (long zarg = game.getPieces(player, PieceType.QUEEN),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					input_attacks[player][PieceType.QUEEN] |= BitboardGen.getQueenSet(bi, game.getOccupied());
				}
			}

			input_attacks[player][PieceType.KING] = BitboardGen.getKingSet(game.getKingSquare(player));

		}
		
	}
	
	//for pawns this is attacks, NOT pushes
	private long input_attacks [][]= new long[2][6];//[player][piece type]
	
	public long getAttackedTargets(int player, int pieceType) {
		assert Player.validate(player);
		assert PieceType.validate(pieceType);
		return input_attacks[player][pieceType];
	}
	
	private long output_is_calculated [] = new long [2];//breakdown by player
	/**
	 * denotes capture targets where a given piece type can win material.
	 * EXCLUDES PAWNS AND KINGS!!!!
	 */
	private long outputCaptureTarget_winning [][]= new long[2][6];//[player][piece type]
	
	/**
	 * denotes capture targets which break even and attacker holds the target square.
	 * 
	 * EXCLUDES PAWNS AND KINGS!!!
	 */
	private long outputCaptureTarget_neutral_occupation [][]= new long[2][6];//[player][piece type]
	
	/**
	 * denotes capture targets which break even and defender holds the target square.
	 * 
	 * EXCLUDES PAWNS AND KINGS!!!
	 */
	private long outputCaptureTarget_neutral_nonoccupation [][]= new long[2][6];//[player][piece type]
	
	/**
	 * updates the internal state variables.
	 * @param sq
	 * @param player - player making the capture. NOT THE TARGET
	 * @param clearedLocations
	 * @return
	 */
	void evaluateCapture(int sq, int player) {
		assert Square.validate(sq);
		assert Player.validate(player);
		assert game.getPlayerAt(sq) == Player.getOtherPlayer(player);
		
		/*
		 * Need to re-consider the output of this class.
		 * The question is am answering is whether a given square is a safe target for a given attacker type.
		 * >this can be restated as: what is the cheapest attacker that can occupy a given target?
		 * 
		 */
	}
	
	
	
}
