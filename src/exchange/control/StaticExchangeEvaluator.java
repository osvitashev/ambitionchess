package exchange.control;

import static util.BitField32.getBits;
import static util.BitField32.setBits;

import java.util.Arrays;

import javax.swing.text.MaskFormatter;

import gamestate.Bitboard;
import gamestate.Bitboard.ShiftDirection;
import gamestate.BitboardGen;
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
		assert PieceType.validate(pieceType);
		return COSTS[pieceType];
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
	
	private int attackStacks[][] = new int [2][64];
	
	//TODO: add a convention for METHOD_TEMP_*** variables which should only used in the method body or as a return value.
	
	private long output_winningCaptures;//target squares where a safe/winning capture can be made. Shared by both players
	private long output_neutralCaptures;//target squares where a break-even capture can be made. Shared by both players
	private long output_badCaptures;
	///TODO: question: should it be possible to get the good/bad capture map for just one player?
	//=> Yes! which the square-centric approach the additional cost is marginal.
	//or, NO. Precicely bacause it is marginal and when doing move ordering we will only care for captures of one side.
	
	
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
		long as, temp;
		
		temp = brd.getPieces(Player.WHITE, PieceType.PAWN);
		as= Bitboard.shift(ShiftDirection.NORTH_EAST, temp);
		if(as != 0)
			addAttackSetPawn(Player.WHITE, as);
		temp = brd.getPieces(Player.WHITE, PieceType.PAWN);
		as= Bitboard.shift(ShiftDirection.NORTH_WEST, temp);
		if(as != 0)
			addAttackSetPawn(Player.WHITE, as);
		
		temp = brd.getPieces(Player.BLACK, PieceType.PAWN);
		as= Bitboard.shift(ShiftDirection.SOUTH_EAST, temp);
		if(as != 0)
			addAttackSetPawn(Player.BLACK, as);
		temp = brd.getPieces(Player.BLACK, PieceType.PAWN);
		as= Bitboard.shift(ShiftDirection.SOUTH_WEST, temp);
		if(as != 0)
			addAttackSetPawn(Player.BLACK, as);
		
		for(int player : Player.PLAYERS) {
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.KNIGHT),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					addAttackSetPieceTypeSquare(PieceType.KNIGHT, bi, player, BitboardGen.getKnightSet(bi));
				}
			}
			
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.BISHOP),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					addAttackSetPieceTypeSquare(PieceType.BISHOP, bi, player, BitboardGen.getBishopSet(bi, brd.getOccupied()));
				}
			}
			
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.ROOK),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					addAttackSetPieceTypeSquare(PieceType.ROOK, bi, player, BitboardGen.getRookSet(bi, brd.getOccupied()));
				}
			}
			
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.QUEEN),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					addAttackSetPieceTypeSquare(PieceType.QUEEN, bi, player, BitboardGen.getQueenSet(bi, brd.getOccupied()));
				}
			}
			
			{
				int bi = 0;
				for (long zarg = brd.getPieces(player, PieceType.KING),
						barg = Bitboard.isolateLsb(zarg); zarg != 0L; zarg = Bitboard.extractLsb(zarg), barg = Bitboard.isolateLsb(zarg)) {//iterateOnBitIndices
					bi = Bitboard.getFirstSquareIndex(barg);
					addAttackSetPieceTypeSquare(PieceType.KING, bi, player, BitboardGen.getKingSet(bi));
				}
			}
			
			
		}
		//next: pupulate attack sets without batteris or pawn pushes. Aka the 'regular' attacks
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
