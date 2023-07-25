package exchange.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class StaticExchangeEvaluatorTest {

	@Test
	void basicInitializationTest() {
		StaticExchangeEvaluator qa = new StaticExchangeEvaluator();
		qa.addAttackSetPieceTypeSquare(PieceType.KING, Square.A2, Player.WHITE, 0x33);
		qa.addAttackSetPieceTypeSquare(PieceType.KING, Square.G7, Player.BLACK, 0x787878);
		qa.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.A3, Player.WHITE, 0x65);
		qa.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.F5, Player.BLACK, 0xc8);
		qa.addAttackSetPawn(Player.BLACK, 0x854);
		
		assertEquals("White:\n"
				+ "{K a2 -> 0x33}\n"
				+ "{N a3 -> 0x65}\n"
				+ "Black:\n"
				+ "{K g7 -> 0x787878}\n"
				+ "{N f5 -> 0xc8}\n"
				+ "{P -> 0x854}\n"
				+ "", qa.toString());
		
		qa.sortSets();
		assertEquals("White:\n"
				+ "{N a3 -> 0x65}\n"
				+ "{K a2 -> 0x33}\n"
				+ "Black:\n"
				+ "{P -> 0x854}\n"
				+ "{N f5 -> 0xc8}\n"
				+ "{K g7 -> 0x787878}\n"
				+ "", qa.toString());
		
		qa.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.A4, Player.WHITE, 0x1);
		qa.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.A5, Player.WHITE, 0x2);
		qa.addAttackSetPawn(Player.WHITE, 0xff1);
		qa.addAttackSetPawn(Player.WHITE, 0xff2);
		qa.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.A6, Player.WHITE, 0x3);
		
		qa.sortSets();
		assertEquals("White:\n"
				+ "{P -> 0xff1}\n"
				+ "{P -> 0xff2}\n"
				+ "{N a3 -> 0x65}\n"
				+ "{B a5 -> 0x2}\n"
				+ "{R a6 -> 0x3}\n"
				+ "{Q a4 -> 0x1}\n"
				+ "{K a2 -> 0x33}\n"
				+ "Black:\n"
				+ "{P -> 0x854}\n"
				+ "{N f5 -> 0xc8}\n"
				+ "{K g7 -> 0x787878}\n"
				+ "", qa.toString());
		//System.out.println(qa.toString());
	}
	
	@Test
	void testPlayerAttackStackOperations(){
		int attackStack=PlayerAttackStack.initialize();
		assertFalse(PlayerAttackStack.hasNext(attackStack));
		attackStack = PlayerAttackStack.addEnemyPawn(attackStack);
		assertTrue(PlayerAttackStack.hasNext(attackStack));
		attackStack = PlayerAttackStack.addRegularPiece(attackStack, PieceType.PAWN);
		attackStack = PlayerAttackStack.addRegularPiece(attackStack, PieceType.BISHOP);
		attackStack = PlayerAttackStack.addRegularPiece(attackStack, PieceType.KING);
		int correctValues[] = {PieceType.KING, PieceType.BISHOP, PieceType.PAWN, PlayerAttackStack.ENEMY_PAWN};
		int index=0;
		int iterableAttacks = attackStack;
		while(PlayerAttackStack.hasNext(iterableAttacks)) {
			assertEquals(correctValues[index], PlayerAttackStack.getNext(iterableAttacks));
			index++;
			iterableAttacks=PlayerAttackStack.removeNext(iterableAttacks);
		}
		assertTrue(PlayerAttackStack.hasNext(attackStack));
	}
	

	/**
	 * normal flow:
	 * QuantitativeAnalyzer qa = new QuantitativeAnalyzer();
	 * qa.initializeFromGamestate(... gamestate);
	 * ... at this point all of the *output* variables are populated
	 * 
	 * public void initializeFromGamestate(Gamestate brd){
	 * 	populateAttackSets();
	 * 	resetTemp();
	 * 	calculateCapturesBothPlayers();
	 * 	resetTemp;
	 * 	calculateQuietMoves(white);
	 * 	resetTemp;
	 * 	calculateQuietMoves(black);
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 */
	
	
}
