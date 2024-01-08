package mobeval;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

public class MobilityEvaluator {
	 private final Gamestate game;
	 
	 public MobilityEvaluator(Gamestate g) {
		 game=g;
	 }
	 
	 /**
	  * should be called any time internal game state changes
	  */
	 void initialize() {
		{//initialize blockaded pawns
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
	  * pawns where forward movement is blocked by another pawn (friend or foe). Only checks single pushes.
	  * @param player
	  * @return
	  */
	 long get_output_blockadedPawns(int player) {
		assert Player.validate(player);
		return output_blockadedPawns[player];
	 }
}
