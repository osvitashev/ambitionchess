package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import java.util.Arrays;

import javax.swing.text.MaskFormatter;

import gamestate.Bitboard;
import gamestate.DebugLibrary;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.ArithmeticVectorTranspose;
import util.BitwiseVectorTranspose;
import util.ResettableArray32;


/**
 * 
 * AttackSets are not going to be modified during the evaluation,  but this class deos contain other data fields which are used as temporary variables and which are going to be overwritten.
 *
 */

//This should have a reference to const board state. and separate initializer for permanent and non-permannet member data

public class StaticExchangeEvaluator {
	private static final int[] COSTS = { 1, 3, 3, 5, 9, 15};
	
	/**
	 * Only used for ordering. Does not represent exchange evaluation cost!
	 * Notice King is given a value for consistency, but it should not be used except for comparison.
	 * @param pieceType
	 * @return
	 */
	public static int getPieceCost(int pieceType) {
		DebugLibrary.validatePieceType(pieceType);
		return COSTS[pieceType];
	}
	
	
	//TODO: remove the class and replace it with two parallel arrays
	class AttackSet implements Comparable<AttackSet>{
		@Override
	    public int compareTo(AttackSet as) {
	        return this.getPieceType()-as.getPieceType();
	    }
		
		private int data;
		private long attacks;
		
		private AttackSet(long attacks) {
			this.attacks = attacks;
		}
		
		/**
		  	memory packing: 
			start	length	name
			0		3		PieceType - is set to PAWN for pawn attacks and pushes
			3		6		Square origin - is not meaningful for pawns
			9
		 */

		private int getPieceType() {
			return getBits(data, 0, 3);
		}

		private void setPieceType(int pt) {
			data=setBits(data, pt, 0, 3);
		}
		
		private int getSquare() {
			return getBits(data, 3, 6);
		}

		private void setSquare(int sq) {
			data=setBits(data, sq, 3, 6);
		}
		
		public String toString() {
			String ret = "{";
			ret += PieceType.toString(getPieceType());
			if(PieceType.PAWN != getPieceType())
				ret += " " + Square.toString(getSquare());
			ret+= " -> 0x" + Long.toHexString(attacks);
			ret+="}";
			return ret;
		}
		
		private long getAttacks() {
			return attacks;
		}
	}
	
	void sortSets() {
		Arrays.sort(attackSets[0], 0, length_attackSets[0]);
		Arrays.sort(attackSets[1], 0, length_attackSets[1]);
	}
	
	private Gamestate brd;
	private AttackSet attackSets[][] = new AttackSet[2][30];
	private int length_attackSets[] = new int[2];
	
	int size_attackSet(int player) {
		return length_attackSets[player];
	}
	
	/**
	 * is only used when a new object is created!
	 */
	private BitwiseVectorTranspose[] initialize_temp_serializedCaptureSets_pieceCosts() {
		///REFACTOR: this might as well be 10 b-bit vectors, but it would make equivalent of shoftLwftWhere more messy
		BitwiseVectorTranspose[] temp = new BitwiseVectorTranspose[2];
		temp[0]=new BitwiseVectorTranspose(40);//10 attackers with 4-bit encoding!
		temp[1]=new BitwiseVectorTranspose(40);
		return temp;
	}
	
	///captures only. no quiet pawn pushes and resulting batteries!
	//TODO: this variable is redundant and can be replaced with temp_parallelCaptureSets_pieceCosts
	private BitwiseVectorTranspose[] temp_serializedCaptureSets_pieceCosts = initialize_temp_serializedCaptureSets_pieceCosts();
	
	/// when evaluating quiet moves in the future, we can reuse the temp_parallelCaptureSets for the second player!
	
	//TODO: add a convention for METHOD_TEMP_*** variables which should only used in the method body or as a return value.
	
	//REFACTOR: this is a map of an attack set to a serialized attack set!
	void populate_temp_serializedCaptureSets_pieceCosts(int player) {
		Player.validate(player);
		temp_serializedCaptureSets_pieceCosts[player].resetTo0s();
		int pt;//piece type
		int pc;//piece cost
		long mask;
		for(int i=0; i<length_attackSets[player];++i) {
			mask=attackSets[player][i].getAttacks();
			temp_serializedCaptureSets_pieceCosts[player].leftShiftWhere(mask, 4);//this injects 0s to the end
			pt = attackSets[player][i].getPieceType();
			pc= getPieceCost(pt);
			if((pc & 1) !=0)
				temp_serializedCaptureSets_pieceCosts[player].setWhere(0, ~0l, mask);
			if((pc & 2) !=0)
				temp_serializedCaptureSets_pieceCosts[player].setWhere(1, ~0l, mask);
			if((pc & 4) !=0)
				temp_serializedCaptureSets_pieceCosts[player].setWhere(2, ~0l, mask);
			if((pc & 8) !=0)
				temp_serializedCaptureSets_pieceCosts[player].setWhere(3, ~0l, mask);
		}
	}
	
	String toString_temp_serializedCaptureSets_pieceCosts() {
		String[][] temp = new String[2][];
		String ret = "";
		long[] cost = new long[4];
		
		for(int player: Player.PLAYERS) {
			temp[player] = new String[64];
			for(int i=0;i<64;++i)
				temp[player][i]=new String("");;
			
			for(int i=temp_serializedCaptureSets_pieceCosts[player].size()-1; i>=0; i-=4) {
				
				cost[0]=temp_serializedCaptureSets_pieceCosts[player].get(i-3);//1
				cost[1]=temp_serializedCaptureSets_pieceCosts[player].get(i-2);//2
				cost[2]=temp_serializedCaptureSets_pieceCosts[player].get(i-1);//4
				cost[3]=temp_serializedCaptureSets_pieceCosts[player].get(i);//8
				

					
					
					//long maskWhere = ~cost[3] & cost[2] & ~cost[1] & cost[0];
					long maskWhere;
					for(int possibleCost=1; possibleCost<16; ++possibleCost){
						maskWhere = ~0l;
						if((possibleCost & 1)!=0)
							maskWhere &= cost[0];
						else
							maskWhere &= ~cost[0];
						
						if((possibleCost & 2)!=0)
							maskWhere &= cost[1];
						else
							maskWhere &= ~cost[1];
						
						if((possibleCost & 4)!=0)
							maskWhere &= cost[2];
						else
							maskWhere &= ~cost[2];
						
						if((possibleCost & 8)!=0)
							maskWhere &= cost[3];
						else
							maskWhere &= ~cost[3];
						
						//here: if costOption==5 it means that maskWhere is the bitmap of locations where the cumulative cost is 5
						{
							int bi = 0;
							for (long zarg = maskWhere, barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard
									.isolateLsb(zarg)) {//iterateOnBitIndices
								bi = Bitboard.getFirstSquareIndex(barg);
								temp[player][bi] += Integer.toString(possibleCost) + " ";
							}
						}
					}

				

				
			}
		}
		
		for(int i=0; i<64; ++i) {
			ret+=Square.toString(i) + ": White: " + temp[0][i] + " | Black: "+ temp[1][i] +"\n";
		}
		return ret;
	}

	
	private long output_winningCaptures;//target squares where a safe/winning capture can be made. Shared by both players
	private long output_neutralCaptures;//target squares where a break-even capture can be made. Shared by both players
	private long output_badCaptures;
	///TODO: question: should it be possible to get the good/bad capture map for just one player?
	//=> Yes! which the square-centric approach the additional cost is marginal.
	//or, NO. Precicely bacause it is marginal and when doing move ordering we will only care for captures of one side.
	
	
	/**
	 * PREREQUISITE: attacksSets are generated and sorted.
	 * performs a forward scan of attackSets[player]
	 */
//	AttackSet getNextAttacker(int sq, int player, int startingIndex) {
//		long targerBB = Bitboard.initFromSquare(sq);
//		for(int i=startingIndex; i<size_attackSet(player); ++i)
//			if((attackSets[player][i].getAttacks() & targerBB) != 0l)
//				return attackSets[player][i];
//		return null;
//	}
	

	
	void addAttackSetPieceTypeSquare(int pt, int sq, int player, long attacks) {
		assert attacks !=0l;
		////TODO =>>>>>>>> This is VERY wrong. We do not want to be creating objects on-demand. EVER!!!!!
		// THOUUGH, this will be refactored to not have this class anyways...
		AttackSet as = new AttackSet(attacks);
		as.setPieceType(pt);
		as.setSquare(sq);
		attackSets[player][length_attackSets[player]++]=as;
	}
	
	void addAttackSetPawn( int player, long attacks) {
		assert attacks !=0l;
		addAttackSetPieceTypeSquare(PieceType.PAWN, Square.A1, player, attacks);
	}
	
	public String toString() {
		String ret="";
		ret += "White:\n";
		for(int i=0; i<length_attackSets[0];++i)
			ret+=attackSets[0][i].toString()+"\n";
		ret += "Black:\n";
		for(int i=0; i<length_attackSets[1];++i)
			ret+=attackSets[1][i].toString()+"\n";
		return ret;
	}
	
	void populateAttackSets() {
		
		
	}
	
	/*
	 * Should be the first thing called when analyzing a new position.
	 * 
	 */
	public void initializeBoardState(final Gamestate brd) {
		this.brd = brd;
		length_attackSets[Player.WHITE]=0;
		length_attackSets[Player.BLACK]=0;
		populateAttackSets();
	}
	
	

}
