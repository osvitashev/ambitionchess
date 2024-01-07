package basicseeval;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.AttackerType;

public class TargetStaticExchangeEvaluator {
	private final int[] pieceValues = { 100, 300, 300, 500, 800, 100000  };// index matches piece type.
	
	private final Gamestate game;
	
	private int var_evaluateTarget_attackTypeStack[]=new int[32];//pieceType - both players condensed to same stack.
	private int var_evaluateTarget_attackSquareStack[]=new int[32];
	private int var_evaluateTarget_gain [] =new int[32];//integer value change - needed for linear minimax
	
	private static final boolean ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS = false;
	
	///whether the last call to evaluateTargetExchange returned true. If it did not, none of the calls to getter methods are valid.
	private boolean lastCalltoEvaluateTargetExchange = false;
	
	private int var_evaluateTarget_attackStack_lastIndex, var_evaluateTargetExchange_occupierPieceType, var_evaluateTargetExchange_occupierPlayer,
		var_evaluateTargetExchange_principleLineLastIndex;
	
	TargetStaticExchangeEvaluator(Gamestate g){
		game = g;
	}
	
	private int getPieceValue(int pieceType) {
		assert PieceType.validate(pieceType);
		return pieceValues[pieceType];
	}

int get_evaluateTargetExchange_occupierPieceType() {
	assert lastCalltoEvaluateTargetExchange;
	assert PieceType.validate(var_evaluateTargetExchange_occupierPieceType);
	return var_evaluateTargetExchange_occupierPieceType;
}

int get_evaluateTargetExchange_occupierPlayer() {
	assert lastCalltoEvaluateTargetExchange;
	assert Player.validate(var_evaluateTargetExchange_occupierPlayer);
	return var_evaluateTargetExchange_occupierPlayer;
}

int get_evaluateTargetExchange_principleLineLastIndex() {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTargetExchange_principleLineLastIndex;
}

int getGain() {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTarget_gain[0];
}

int getFirstAttackerSquare() {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTarget_attackSquareStack[1];
}

int getFirstAttackerType() {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTarget_attackTypeStack[1];
}

int get_evaluateTarget_attackStack_lastIndex() {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTarget_attackStack_lastIndex;
}

int get_evaluateTarget_attackSquareStack(int i) {
	assert lastCalltoEvaluateTargetExchange;
	return var_evaluateTarget_attackSquareStack[i];
}

/**
 * returns square of the piece at the given index of the principle line of the exchange.
 * @param index
 * @return square
 */
int get_evaluateTargetExchange_principleLine_square(int index) {
	assert lastCalltoEvaluateTargetExchange;
	assert index <= get_evaluateTargetExchange_principleLineLastIndex() && index >=0;
	assert Square.validate(var_evaluateTarget_attackSquareStack[index]);
	return var_evaluateTarget_attackSquareStack[index];
}
	
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
		//todo: add a hint parameter - effectively the last value returned by this call. It is caller's responsibility to set it!
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
	 * 
	 * @param sq
	 * @param player
	 * @param clearedSquares
	 * @param forced_attacker_type PieceType. NO_PIECE indicates that we are evaluating natural attack order. Natural atack order is only supposerted for captures.
	 * @return whether there is an available capture. Available does not mean Viable! Returns false if there are no available attackers.
	 */
	boolean evaluateTargetExchange(int sq, int player, long clearedSquares,  int forced_attacker_type) {
		lastCalltoEvaluateTargetExchange = false;
		//todo: split evaluateTargetExchange into two functions so that forced attacker is no longer optional!!!!!
		
		//todo: need to handle pawn promotions: promoting to a queen is equivalent to losing 100 points and gaining 800!
		assert Square.validate(sq);
		assert Player.validate(player);
		assert (game.getPieceAt(sq) == PieceType.NO_PIECE ?
			(//bool expression - quiet move validation
				((forced_attacker_type == PieceType.NO_PIECE) ?
					(//natural attack order quiet move -  not currently supported
						false
					)
					:
					(//quiet move with a specified forced type
						true
						//forced_attacker_type != PieceType.PAWN && Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq)
						//||
						//forced_attacker_type == PieceType.PAWN && Bitboard.testBit(BitboardGen.getMultiplePawnPushSet(game.getPieces(player, PieceType.PAWN), player, game.getOccupied()), sq)
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
						game.getPlayerAt(sq) == Player.getOtherPlayer(player)
						//&& Bitboard.testBit(getAttackedTargets(player, forced_attacker_type), sq)
					)
				)
			)
		) : "State: fen(" + game.toFEN() + ") sq("+Square.toString(sq)+ ") player(" + Player.toString(player) + ") "+
			" " + clearedSquares + " " + forced_attacker_type;
		
		long initialClearedSquares=clearedSquares;//just used for logging
		var_evaluateTarget_attackStack_lastIndex=1;
		int currentPlayer=Player.getOtherPlayer(player);
		
		if(forced_attacker_type != PieceType.NO_PIECE) {
			var_evaluateTarget_attackStack_lastIndex++;
			currentPlayer = player;
		}
		var_evaluateTarget_attackTypeStack[0]=game.getPieceAt(sq);
		var_evaluateTarget_attackSquareStack[0]=sq;
		if(forced_attacker_type != PieceType.NO_PIECE)
			var_evaluateTarget_attackTypeStack[1]=forced_attacker_type;
		var_evaluateTarget_gain[0] = game.getPieceAt(sq) == PieceType.NO_PIECE ? 0 : getPieceValue(var_evaluateTarget_attackTypeStack[0]);
		if(forced_attacker_type != PieceType.NO_PIECE)
			var_evaluateTarget_gain[1] =getPieceValue(var_evaluateTarget_attackTypeStack[1]) - var_evaluateTarget_gain[0];

		if(forced_attacker_type == PieceType.PAWN && game.getPieceAt(sq) == PieceType.NO_PIECE) {
			int pawnSource = getPawnPushSourceSquare(sq,player, clearedSquares);
			if (pawnSource == Square.SQUARE_NONE)
				return false;
			var_evaluateTarget_attackSquareStack[1] = pawnSource;
			clearedSquares |= Bitboard.initFromSquare(pawnSource);
			//dont forget to update var_evaluateTarget_attackSquareStack!!!!
			//return false is there is no pawn available for the push!!!
		}
		else if(forced_attacker_type != PieceType.NO_PIECE) {
			int lva = getLeastValuableAttacker_withType(sq, currentPlayer, forced_attacker_type, clearedSquares);
			if (lva == AttackerType.nullValue())
				return false;
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
			var_evaluateTarget_attackTypeStack[var_evaluateTarget_attackStack_lastIndex] = nextAttackerType;
			var_evaluateTarget_attackSquareStack[var_evaluateTarget_attackStack_lastIndex] = AttackerType.getAttackerSquareFrom(leastValuableAttacker);
			var_evaluateTarget_gain[var_evaluateTarget_attackStack_lastIndex] = getPieceValue(var_evaluateTarget_attackTypeStack[var_evaluateTarget_attackStack_lastIndex])
					- var_evaluateTarget_gain[var_evaluateTarget_attackStack_lastIndex - 1];
			prevVictim = var_evaluateTarget_attackTypeStack[var_evaluateTarget_attackStack_lastIndex - 1];
			var_evaluateTarget_attackStack_lastIndex++;
			if (Math.max(-var_evaluateTarget_gain[var_evaluateTarget_attackStack_lastIndex - 2], var_evaluateTarget_gain[var_evaluateTarget_attackStack_lastIndex - 1]) < 0) {
				break;
			}
			if (prevVictim == PieceType.KING)//needed for the case if king is already captured and it is not valid to try to take the opponent's king as an attemp to save the score.
				break;
		} while (true);

if(ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS) {
System.out.print(game.toFEN() +" cleared:"+initialClearedSquares+ " [" +
		((forced_attacker_type == PieceType.NO_PIECE) ? "natural " : "forced ")+ Player.toShortString(player) + "??" +
		(game.getPieceAt(sq) == PieceType.NO_PIECE ? " - " : " x " )
		+ Square.toString(sq)+ "] sequence: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "()" :
			((player==Player.BLACK ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[0]))) 
		+ " ");
for(int i=1; i<var_evaluateTarget_attackStack_lastIndex;++i)
	System.out.print((player==Player.BLACK ^ i%2==1 ? "w" : "b") + PieceType.toString(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} values: {" + (game.getPieceAt(sq) == PieceType.NO_PIECE ? "0" : getPieceValue(var_evaluateTarget_attackTypeStack[0])) + " ");
for(int i=1; i<var_evaluateTarget_attackStack_lastIndex;++i)
	System.out.print(getPieceValue(var_evaluateTarget_attackTypeStack[i]) + " ");
System.out.print("} gains: ");
for(int i=0; i<var_evaluateTarget_attackStack_lastIndex-1;++i)
	System.out.print(var_evaluateTarget_gain[i] + " ");
}
		
		//minimax backtracking
		var_evaluateTargetExchange_principleLineLastIndex=var_evaluateTarget_attackStack_lastIndex-1;
		for (int i = var_evaluateTarget_attackStack_lastIndex-2; i>0; --i) {
			var_evaluateTarget_gain[i-1]= -Math.max(-var_evaluateTarget_gain[i-1], var_evaluateTarget_gain[i]);
			if(var_evaluateTarget_gain[i-1] != -var_evaluateTarget_gain[i])
				var_evaluateTargetExchange_principleLineLastIndex = i;
		}
		var_evaluateTargetExchange_occupierPlayer = var_evaluateTargetExchange_principleLineLastIndex%2 == 1 ? player : Player.getOtherPlayer(player);
		var_evaluateTargetExchange_occupierPieceType = var_evaluateTarget_attackTypeStack[var_evaluateTargetExchange_principleLineLastIndex];

				/**
		 * at this point temp_evaluateCapture_forcedAttacker_gain[0] is the expected exchange value IF the forced capture is taken.
		 */
if(ENABLE_EVALUATE_TARGET_EXCHANGE_DEBUG_STATEMENTS) {
System.out.println();
System.out.print("last attacker: "+ Player.toShortString(Player.getOtherPlayer(currentPlayer))
	+ PieceType.toString(var_evaluateTarget_attackTypeStack[var_evaluateTarget_attackStack_lastIndex-1]));
System.out.print(" | principal line: { ");
for(int i=0; i<=var_evaluateTargetExchange_principleLineLastIndex;++i)
	System.out.print(Square.toString(var_evaluateTarget_attackSquareStack[i]) + " ");

	System.out.print("} | last expected occupier: "+ Player.toShortString(var_evaluateTargetExchange_occupierPlayer)
	+ PieceType.toString(var_evaluateTargetExchange_occupierPieceType) + " ("+var_evaluateTargetExchange_principleLineLastIndex+")");
System.out.println(" returning: "+ var_evaluateTarget_gain[0]);
System.out.println();
}
		if(var_evaluateTargetExchange_principleLineLastIndex == 0) {
			return false;
		}
		else {
			lastCalltoEvaluateTargetExchange = true;
			return true;
		}
	}
}
