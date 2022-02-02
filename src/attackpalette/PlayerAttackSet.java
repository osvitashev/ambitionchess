package attackpalette;

import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Board;
import gamestate.DebugLibrary;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

/**
 * 
 * Class used for SEE and Square Control Assessment.
 * The current implementation is blind to pins and backstabs.
 *
 */
public class PlayerAttackSet {
	final private PlayerAttackSetGenerator generator = new PlayerAttackSetGenerator();
	
	//both players can be contained within same class - to save on instantiating two generators.
	final private AttackSet attackSets[][] = new AttackSet[2][30];//outer index is the player
	private int attackSets_size[] = new int [2];
	
	public AttackSet getAttackSet(int player, int index) {
		DebugLibrary.validatePlayer(player);
		return attackSets[player][index];
	}
	
	public int length(int player) {
		DebugLibrary.validatePlayer(player);
		return attackSets_size[player];
	}
	
	public void addAttackSet(int player, long attacks, int type, int origin, int commitment, int prevCommitment) {
		DebugLibrary.validatePlayer(player);
		DebugLibrary.validatePieceType(type);
		DebugLibrary.validateSquare(origin);
		attackSets[player][attackSets_size[player]].setAttacks(attacks);
		attackSets[player][attackSets_size[player]].setType(type);
		attackSets[player][attackSets_size[player]].setOrigin(origin);
		attackSets[player][attackSets_size[player]].setCommitment(commitment);
		attackSets[player][attackSets_size[player]].setPrevCommitment(prevCommitment);
		attackSets_size[player]++;
	}
	
	public void addPawnAttackSet(int player, long attacks, int commitment, int prevCommitment) {
		DebugLibrary.validatePlayer(player);
		attackSets[player][attackSets_size[player]].setAttacks(attacks);
		attackSets[player][attackSets_size[player]].setType(PieceType.PAWN);
		attackSets[player][attackSets_size[player]].setOrigin(Square.SQUARE_NONE);
		attackSets[player][attackSets_size[player]].setCommitment(commitment);
		attackSets[player][attackSets_size[player]].setPrevCommitment(prevCommitment);
		attackSets_size[player]++;
	}


	public PlayerAttackSet() {
		for(int i=0;i<attackSets.length;++i)
			for(int j=0; j<attackSets[i].length; ++j)
				attackSets[i][j]=new AttackSet();
	}

	public void initialize(Board brd) {
		attackSets_size[0]=0;
		attackSets_size[1]=0;
		generator.generateAttackSet(brd, Player.WHITE, this);
		generator.generateAttackSet(brd, Player.BLACK, this);
	}

}
