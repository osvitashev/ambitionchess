package seecontrol;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.DebugLibrary;
import gamestate.Gamestate;
import gamestate.Move;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import seecontrol.AttackSetData.AttackSetType;

public class SEEControlEvaluator {
	private static final int SET_MAX_SIZE = 100;

	private int[][] attackSetData = new int[2][SET_MAX_SIZE];
	private long[][] attackSets = new long[2][SET_MAX_SIZE];
	private int[] attackSet_size = new int[2];
	
	int getSetData(int player, int index) {
		// TODO: ADD validation
		DebugLibrary.validatePlayer(player);
		return attackSetData[player][index];
	}
	
	long getSet(int player, int index) {
		// TODO: add validation
		DebugLibrary.validatePlayer(player);
		return attackSets[player][index];
	}
	
	int getSetSize(int player) {
		DebugLibrary.validatePlayer(player);
		return attackSet_size[player];
	}

	public void initialize() {
		attackSet_size[Player.WHITE] = 0;
		attackSet_size[Player.BLACK] = 0;
	}
	
	int getAttackSetData(int player, int i) {
		DebugLibrary.validatePlayer(player);
		//TODO: add validation for index
		return attackSetData[player][i];
	}

	long getAttackSet(int player, int i) {
		DebugLibrary.validatePlayer(player);
		//TODO: add validation for index
		return attackSets[player][i];
	}
	
	void populateKingAttacks(Gamestate brd) {
		for(int player : Player.PLAYERS) {
			int sq_from = brd.getKingSquare(player);
			long targetBitboard = BitboardGen.getKingSet(sq_from);
			int asData = 0;
			
			asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
			asData = AttackSetData.setPieceType(asData, PieceType.KING);
			asData = AttackSetData.setSquare(asData, sq_from);
			if(!Player.isWhite(player))
				asData = AttackSetData.setPlayer(asData);
			
			attackSets[player][attackSet_size[player]] = targetBitboard;
			attackSetData[player][attackSet_size[player]] = asData;
			attackSet_size[player]+=1;
		}
	}

}
