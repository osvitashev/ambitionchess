package seecontrol;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.DebugLibrary;
import gamestate.Gamestate;
import gamestate.Move;
import gamestate.Bitboard.ShiftDirection;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
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
			asData = AttackSetData.setPlayer(asData, player);
			attackSets[player][attackSet_size[player]] = targetBitboard;
			attackSetData[player][attackSet_size[player]] = asData;
			attackSet_size[player]+=1;
		}
	}
	
	void populateKnightAttacks(Gamestate brd) {
		for(int player : Player.PLAYERS) {
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.KNIGHT),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					long targetBitboard = BitboardGen.getKnightSet(bi);
					int asData = 0;
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
					asData = AttackSetData.setPieceType(asData, PieceType.KNIGHT);
					asData = AttackSetData.setSquare(asData, bi);
					asData = AttackSetData.setPlayer(asData, player);
					attackSets[player][attackSet_size[player]] = targetBitboard;
					attackSetData[player][attackSet_size[player]] = asData;
					attackSet_size[player]+=1;
				}
			}
		}
	}
	
	void populatePawnAtacks(Gamestate brd) {
		//adds up to two sets per side
		for(int player : Player.PLAYERS) {
			long pawns = brd.getPieces(player, PieceType.PAWN);
			long temp;
			if(Player.isWhite(player)) {
				temp = Bitboard.shift(ShiftDirection.NORTH_EAST, pawns);
				if(!Bitboard.isEmpty(temp)) {
					int asData = 0;
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_ATTACK);
					asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
					attackSets[player][attackSet_size[player]] = temp;
					attackSetData[player][attackSet_size[player]] = asData;
					attackSet_size[player]+=1;
				}
				temp = Bitboard.shift(ShiftDirection.NORTH_WEST, pawns);
				if(!Bitboard.isEmpty(temp)) {
					int asData = 0;
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_ATTACK);
					asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
					attackSets[player][attackSet_size[player]] = temp;
					attackSetData[player][attackSet_size[player]] = asData;
					attackSet_size[player]+=1;
				}
			}
			else {
				temp = Bitboard.shift(ShiftDirection.SOUTH_EAST, pawns);
				if(!Bitboard.isEmpty(temp)) {
					int asData = 0;
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_ATTACK);
					asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
					asData = AttackSetData.setPlayer(asData, Player.BLACK);
					attackSets[player][attackSet_size[player]] = temp;
					attackSetData[player][attackSet_size[player]] = asData;
					attackSet_size[player]+=1;
				}
				temp = Bitboard.shift(ShiftDirection.SOUTH_WEST, pawns);
				if(!Bitboard.isEmpty(temp)) {
					int asData = 0;
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_ATTACK);
					asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
					asData = AttackSetData.setPlayer(asData, Player.BLACK);
					attackSets[player][attackSet_size[player]] = temp;
					attackSetData[player][attackSet_size[player]] = asData;
					attackSet_size[player]+=1;
				}
			}
		}
	}
	
	void populatePawnPushes(Gamestate brd) {
		//adds one set for both single and double pawn push
		for(int player : Player.PLAYERS) {
			long pawns = brd.getPieces(player, PieceType.PAWN);
			long temp =0;
			if(Player.isWhite(player)) {
				temp = Bitboard.shift(ShiftDirection.NORTH, pawns) & brd.getEmpty();
				temp |= Bitboard.shift(ShiftDirection.NORTH, temp & Bitboard.getRankMask(Square.A3)) & brd.getEmpty();
			}
			else {
				temp = Bitboard.shift(ShiftDirection.SOUTH, pawns) & brd.getEmpty();
				temp |= Bitboard.shift(ShiftDirection.SOUTH, temp& Bitboard.getRankMask(Square.A6)) & brd.getEmpty();
			}
			
			if(!Bitboard.isEmpty(temp)) {
				int asData = 0;
				asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
				asData = AttackSetData.setPieceType(asData, PieceType.PAWN);
				asData = AttackSetData.setPlayer(asData, player);
				attackSets[player][attackSet_size[player]] = temp;
				attackSetData[player][attackSet_size[player]] = asData;
				attackSet_size[player]+=1;
			}
		}
	}
	
	/// for sliding piece batteries we can probably revert to iterative generation of slide-by-one
	
	/**
	 * Used for sliding pieces
	 */
	private void addAttackSet(long as, int asData, int player) {
		attackSets[player][attackSet_size[player]] = as;
		attackSetData[player][attackSet_size[player]] = asData;
		attackSet_size[player]+=1;
	}
	
	//when invoking the next level, only mask one blocker type at a time
	private void populateRookAttacksRecoursively(Gamestate brd, int currentASData, long cumulativeAS, boolean didColorChange) {
		int player = AttackSetData.getPlayer(currentASData);
		int otherPlayer = Player.getOtherPlayer(player);
		long attackSet = BitboardGen.getRookSet(AttackSetData.getSquare(currentASData), brd.getOccupied() & ~cumulativeAS);
		long blockers = attackSet & brd.getOccupied();
		
		if(! Bitboard.isEmpty(attackSet)) {
			//add sunken cost, and pawnPushBattery flag
			addAttackSet(attackSet, currentASData, player);
			long friednly_R		= attackSet & brd.getPieces(player, PieceType.ROOK);
			long enemy_R		= attackSet & brd.getPieces(otherPlayer, PieceType.ROOK);
			long friednly_Q		= attackSet & brd.getPieces(player, PieceType.QUEEN);
			long enemy_Q		= attackSet & brd.getPieces(otherPlayer, PieceType.QUEEN);
			long friednly_P		= attackSet & brd.getPieces(player, PieceType.PAWN);
			long enemy_P		= attackSet & brd.getPieces(otherPlayer, PieceType.PAWN);
			
			if (!Bitboard.isEmpty(friednly_R) && !didColorChange) {
				int newASData = currentASData;
				newASData = AttackSetData.setSunkenCost(newASData,
						AttackSetData.getSunkenCost(newASData) + AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK));
				populateRookAttacksRecoursively(brd, newASData, cumulativeAS | attackSet, false);
			}
			
			//pawn cases will not make a recrsive call
		}
		
	}
	
	///TODO: rename the package to quantitativeanalysis!!!!!!!!!!!!!!!!
	 
	
	void populateRookAttacks(Gamestate brd) {
		for(int player : Player.PLAYERS) {
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.ROOK),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {// iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					
					int asData = 0;
					asData = AttackSetData.setPlayer(asData, player);
					asData = AttackSetData.setAttackSetType(asData, AttackSetType.DIRECT);
					asData = AttackSetData.setPieceType(asData, PieceType.ROOK);
					asData = AttackSetData.setSquare(asData, bi);
					//asData = AttackSetData.setSunkenCost(asData, AttackSetData.OrderingPieceWeights.getValue(PieceType.ROOK));
					
					populateRookAttacksRecoursively(brd, asData, 0L, false);

				}
			}
		}
	}

}
