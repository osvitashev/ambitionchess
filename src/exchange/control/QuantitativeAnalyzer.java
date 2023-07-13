package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import java.util.Arrays;

import gamestate.Bitboard;
import gamestate.DebugLibrary;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;
import util.BitvectorTranspose;


/**
 * 
 * AttackSets are not going to be modified during the evaluation,  but this class deos contain other data fields which are used as temporary variables and which are going to be overwritten.
 *
 */

//This should have a reference to const board state. and separate initializer for permanent and non-permannet member data

public class QuantitativeAnalyzer {
	private static final int[] COSTS = { 1, 3, 3, 5, 9};
	
	/**
	 * Only used for ordering. Does not represent exchange evaluation cost!
	 * Notice that King does not have an assigned value!!!!
	 * @param pieceType
	 * @return
	 */
	public static int getPieceValue(int pieceType) {
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
	}
	
	void sortSets() {
		Arrays.sort(attackSets[0], 0, length_attackSets[0]);
		Arrays.sort(attackSets[1], 0, length_attackSets[1]);
	}
	
	private Gamestate brd;
	private AttackSet attackSets[][] = new AttackSet[2][30];
	private int length_attackSets[] = new int[2];
	private BitvectorTranspose temp_exchangeGainLoss = new BitvectorTranspose();
	
	int size_attackSet(int player) {
		return length_attackSets[player];
	}
	
	/**
	 * A getter. Is only meant to be used for testing/debigging.
	 * @param sq
	 * @return
	 */
	int getTemp_exchangeGainLoss(int sq) {
		return temp_exchangeGainLoss.indexToScalar(sq);
	}
	
	
	/**
	 * Populates Exchange gain/loss parallel scalar with the weights of pieces currently on the board.
	 * Positive values for both players.
	 */
	void reinitializeGainLossToPieceValues() {
		temp_exchangeGainLoss.clear();
		
		long mask = brd.getPieces(PieceType.ROOK);
		temp_exchangeGainLoss.addScalar(mask, getPieceValue(PieceType.ROOK) );
		
		mask = brd.getPieces(PieceType.KNIGHT);
		temp_exchangeGainLoss.addScalar(mask, getPieceValue(PieceType.KNIGHT));
		
		mask = brd.getPieces(PieceType.BISHOP);
		temp_exchangeGainLoss.addScalar(mask, getPieceValue(PieceType.BISHOP));
		
		mask = brd.getPieces(PieceType.QUEEN);
		temp_exchangeGainLoss.addScalar(mask, getPieceValue(PieceType.QUEEN));
		
		mask = brd.getPieces(PieceType.PAWN);
		temp_exchangeGainLoss.addScalar(mask, getPieceValue(PieceType.PAWN));
	}
	

	
	private long output_winningCaptures;//target squares where a safe/winning capture can be made. Shared by both players
	private long output_neutralCaptures;//target squares where a break-even capture can be made. Shared by both players
	private long output_badCaptures;
	public void populateCapturesBothPlayers() {
		long temp_captureHappened=0;//used to distinguish between 'White Knight at f4' and 'White Knight at f4 is taken'. Needed for after the exchange calculation.
		
		
		
	}
	
	
	void addAttackSetPieceTypeSquare(int pt, int sq, int player, long attacks) {
		////TODO =>>>>>>>> This is VERY wrong. We do not want to be creating objects on-demand. EVER!!!!!
		// THOUUGH, this will be refactored to not have this class anyways...
		AttackSet as = new AttackSet(attacks);
		as.setPieceType(pt);
		as.setSquare(sq);
		attackSets[player][length_attackSets[player]++]=as;
	}
	
	void addAttackSetPawn( int player, long attacks) {
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
