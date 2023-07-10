package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Square;

public class QuantitativeAnalyzer {
	//TODO: remove the class and replace it with two parallel arrays
	class AttackSet{
		private int data;
		private long attacks;
		
		private AttackSet(long attacks) {
			this.attacks = attacks;
		}

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
	
	/**
	 * 
	 
	  * 
	  	memory packing: 
		start	length	name
		0		3		PieceType - is set to PAWN for pawn attacks and pushes
		3		6		Square origin - is not meaningful for pawns
		9

	 @formatter:on
	 */
	final static class AttackSetUtil{

	}
	
	private AttackSet attackSets[][] = new AttackSet[2][30];
	private int length[] = new int[2];
	
	int size(int player) {
		return length[player];
	}
	

	void reset() {
		length[0] =0;
		length[1]=0;
	}
	
	void addAttackSetPieceTypeSquare(int pt, int sq, int player, long attacks) {
		AttackSet as = new AttackSet(attacks);
		as.setPieceType(pt);
		as.setSquare(sq);
		attackSets[player][length[player]++]=as;
	}
	
	void addAttackSetPawn( int player, long attacks) {
		AttackSet as = new AttackSet(attacks);
		attackSets[player][length[player]++]=as;
	}
	
	public String toString() {
		String ret="";
		ret += "White:\n";
		for(int i=0; i<length[0];++i)
			ret+=attackSets[0][i].toString()+"\n";
		ret += "Black:\n";
		for(int i=0; i<length[1];++i)
			ret+=attackSets[1][i].toString()+"\n";
		return ret;
	}
	
	//try using mockito for this
	public void generateFromPosition(final Gamestate brd) {
		
	}
	
	

}
