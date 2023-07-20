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
		qa.addAttackSetPieceTypeSquare(PieceType.PAWN, Square.E5, Player.BLACK, 0x854);
		
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
		qa.addAttackSetPieceTypeSquare(PieceType.PAWN, Square.A1, Player.WHITE, 0xff1);
		qa.addAttackSetPieceTypeSquare(PieceType.PAWN, Square.A1, Player.WHITE, 0xff2);
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
	
	
	@Test
	void testExchange() {
		StaticExchangeEvaluator see = new StaticExchangeEvaluator();
		
		see.initializeBoardState(new Gamestate("8/8/5k2/4n3/8/2KP4/8/8 w - - 0 1"));
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		assertEquals("b2: White: 15  | Black: \r\n"
				+ "c2: White: 15  | Black: \r\n"
				+ "d2: White: 15  | Black: \r\n"
				+ "b3: White: 15  | Black: \r\n"
				+ "d3: White: 15  | Black: 3 \r\n"
				+ "f3: White:  | Black: 3 \r\n"
				+ "b4: White: 15  | Black: \r\n"
				+ "c4: White: 1 15  | Black: 3 \r\n"
				+ "d4: White: 15  | Black: \r\n"
				+ "e4: White: 1  | Black: \r\n"
				+ "g4: White:  | Black: 3 \r\n"
				+ "e5: White:  | Black: 15 \r\n"
				+ "f5: White:  | Black: 15 \r\n"
				+ "g5: White:  | Black: 15 \r\n"
				+ "c6: White:  | Black: 3 \r\n"
				+ "e6: White:  | Black: 15 \r\n"
				+ "g6: White:  | Black: 3 15 \r\n"
				+ "d7: White:  | Black: 3 \r\n"
				+ "e7: White:  | Black: 15 \r\n"
				+ "f7: White:  | Black: 3 15 \r\n"
				+ "g7: White:  | Black: 15 \r\n", see.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
		//System.out.println(see.toString_temp_serializedCaptureSets_pieceCosts());
		
		see.initializeBoardState(new Gamestate("8/5k2/4n3/2p5/2KP1P2/2p5/8/8 w - - 0 1"));
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		assertEquals("b2: White:  | Black: 1 \r\n"
				+ "d2: White:  | Black: 1 \r\n"
				+ "b3: White: 15  | Black: \r\n"
				+ "c3: White: 15  | Black: \r\n"
				+ "d3: White: 15  | Black: \r\n"
				+ "b4: White: 15  | Black: 1 \r\n"
				+ "d4: White: 15  | Black: 1 3 \r\n"
				+ "f4: White:  | Black: 3 \r\n"
				+ "b5: White: 15  | Black: \r\n"
				+ "c5: White: 1 15  | Black: 3 \r\n"
				+ "d5: White: 15  | Black: \r\n"
				+ "e5: White: 1 1  | Black: \r\n"
				+ "g5: White: 1  | Black: 3 \r\n"
				+ "e6: White:  | Black: 15 \r\n"
				+ "f6: White:  | Black: 15 \r\n"
				+ "g6: White:  | Black: 15 \r\n"
				+ "c7: White:  | Black: 3 \r\n"
				+ "e7: White:  | Black: 15 \r\n"
				+ "g7: White:  | Black: 3 15 \r\n"
				+ "d8: White:  | Black: 3 \r\n"
				+ "e8: White:  | Black: 15 \r\n"
				+ "f8: White:  | Black: 3 15 \r\n"
				+ "g8: White:  | Black: 15 \r\n", see.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
		
		see.initializeBoardState(new Gamestate("8/5k2/4n3/2p5/2KB1N2/2p5/8/8 w - - 0 1"));
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		assertEquals("g1: White: 3  | Black: \r\n"
				+ "b2: White:  | Black: 1 \r\n"
				+ "d2: White:  | Black: 1 \r\n"
				+ "e2: White: 3  | Black: \r\n"
				+ "f2: White: 3  | Black: \r\n"
				+ "g2: White: 3  | Black: \r\n"
				+ "b3: White: 15  | Black: \r\n"
				+ "c3: White: 3 15  | Black: \r\n"
				+ "d3: White: 3 15  | Black: \r\n"
				+ "e3: White: 3  | Black: \r\n"
				+ "h3: White: 3  | Black: \r\n"
				+ "b4: White: 15  | Black: 1 \r\n"
				+ "d4: White: 15  | Black: 1 3 \r\n"
				+ "f4: White:  | Black: 3 \r\n"
				+ "b5: White: 15  | Black: \r\n"
				+ "c5: White: 3 15  | Black: 3 \r\n"
				+ "d5: White: 3 15  | Black: \r\n"
				+ "e5: White: 3  | Black: \r\n"
				+ "g5: White:  | Black: 3 \r\n"
				+ "h5: White: 3  | Black: \r\n"
				+ "e6: White: 3  | Black: 15 \r\n"
				+ "f6: White: 3  | Black: 15 \r\n"
				+ "g6: White: 3  | Black: 15 \r\n"
				+ "c7: White:  | Black: 3 \r\n"
				+ "e7: White:  | Black: 15 \r\n"
				+ "g7: White: 3  | Black: 3 15 \r\n"
				+ "d8: White:  | Black: 3 \r\n"
				+ "e8: White:  | Black: 15 \r\n"
				+ "f8: White:  | Black: 3 15 \r\n"
				+ "g8: White:  | Black: 15 \r\n"
				+ "h8: White: 3  | Black: \r\n", see.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
		
		see.initializeBoardState(new Gamestate("8/4r3/5k2/5rb1/8/3KQ3/5P2/8 w - - 0 1"));
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		assertEquals("c1: White: 9  | Black: \r\n"
				+ "e1: White: 9  | Black: \r\n"
				+ "c2: White: 15  | Black: \r\n"
				+ "d2: White: 9 15  | Black: \r\n"
				+ "e2: White: 9 15  | Black: \r\n"
				+ "f2: White: 9  | Black: 5 \r\n"
				+ "c3: White: 15  | Black: \r\n"
				+ "d3: White: 9  | Black: \r\n"
				+ "e3: White: 1 15  | Black: 3 5 \r\n"
				+ "f3: White: 9  | Black: 5 \r\n"
				+ "g3: White: 1 9  | Black: \r\n"
				+ "h3: White: 9  | Black: \r\n"
				+ "c4: White: 15  | Black: \r\n"
				+ "d4: White: 9 15  | Black: \r\n"
				+ "e4: White: 9 15  | Black: 5 \r\n"
				+ "f4: White: 9  | Black: 3 5 \r\n"
				+ "h4: White:  | Black: 3 \r\n"
				+ "a5: White:  | Black: 5 \r\n"
				+ "b5: White:  | Black: 5 \r\n"
				+ "c5: White: 9  | Black: 5 \r\n"
				+ "d5: White:  | Black: 5 \r\n"
				+ "e5: White: 9  | Black: 5 5 15 \r\n"
				+ "f5: White:  | Black: 15 \r\n"
				+ "g5: White: 9  | Black: 5 15 \r\n"
				+ "b6: White: 9  | Black: \r\n"
				+ "e6: White: 9  | Black: 5 15 \r\n"
				+ "f6: White:  | Black: 3 5 \r\n"
				+ "g6: White:  | Black: 15 \r\n"
				+ "h6: White:  | Black: 3 \r\n"
				+ "a7: White: 9  | Black: 5 \r\n"
				+ "b7: White:  | Black: 5 \r\n"
				+ "c7: White:  | Black: 5 \r\n"
				+ "d7: White:  | Black: 5 \r\n"
				+ "e7: White: 9  | Black: 15 \r\n"
				+ "f7: White:  | Black: 5 15 \r\n"
				+ "g7: White:  | Black: 5 15 \r\n"
				+ "h7: White:  | Black: 5 \r\n"
				+ "e8: White:  | Black: 5 \r\n", see.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
		
		see.initializeBoardState(new Gamestate("8/2k1q3/2b2N2/2n2p2/2R1R3/2BQ1P2/2K5/8 w - - 0 1"));
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		see.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		assertEquals("a1: White: 3  | Black: \r\n"
				+ "b1: White: 15  | Black: \r\n"
				+ "c1: White: 15  | Black: \r\n"
				+ "d1: White: 9 15  | Black: \r\n"
				+ "e1: White: 3 5  | Black: \r\n"
				+ "f1: White: 9  | Black: \r\n"
				+ "b2: White: 3 15  | Black: \r\n"
				+ "c2: White: 9  | Black: \r\n"
				+ "d2: White: 3 9 15  | Black: \r\n"
				+ "e2: White: 5 9  | Black: \r\n"
				+ "b3: White: 15  | Black: 3 \r\n"
				+ "c3: White: 5 9 15  | Black: \r\n"
				+ "d3: White: 15  | Black: 3 \r\n"
				+ "e3: White: 5 9  | Black: \r\n"
				+ "f3: White: 9  | Black: \r\n"
				+ "a4: White: 5  | Black: 3 3 \r\n"
				+ "b4: White: 3 5  | Black: \r\n"
				+ "c4: White: 5 9  | Black: \r\n"
				+ "d4: White: 3 5 5 9  | Black: \r\n"
				+ "e4: White: 1 3 5 9  | Black: 1 3 3 9 \r\n"
				+ "f4: White: 5  | Black: \r\n"
				+ "g4: White: 1 3 5  | Black: 1 \r\n"
				+ "h4: White: 5  | Black: \r\n"
				+ "a5: White: 3  | Black: \r\n"
				+ "b5: White:  | Black: 3 \r\n"
				+ "c5: White: 5  | Black: 9 \r\n"
				+ "d5: White: 3 9  | Black: 3 \r\n"
				+ "e5: White: 3 5  | Black: 9 \r\n"
				+ "h5: White: 3  | Black: \r\n"
				+ "a6: White:  | Black: 3 \r\n"
				+ "b6: White:  | Black: 15 \r\n"
				+ "c6: White:  | Black: 15 \r\n"
				+ "d6: White: 9  | Black: 9 15 \r\n"
				+ "e6: White: 5  | Black: 3 9 \r\n"
				+ "f6: White: 3  | Black: 9 \r\n"
				+ "b7: White:  | Black: 3 3 15 \r\n"
				+ "c7: White:  | Black: 9 \r\n"
				+ "d7: White: 3 9  | Black: 3 3 9 15 \r\n"
				+ "e7: White: 5  | Black: \r\n"
				+ "f7: White:  | Black: 9 \r\n"
				+ "g7: White:  | Black: 9 \r\n"
				+ "h7: White: 3  | Black: 9 \r\n"
				+ "a8: White:  | Black: 3 \r\n"
				+ "b8: White:  | Black: 15 \r\n"
				+ "c8: White:  | Black: 15 \r\n"
				+ "d8: White: 9  | Black: 9 15 \r\n"
				+ "e8: White: 3  | Black: 3 9 \r\n"
				+ "f8: White:  | Black: 9 \r\n"
				+ "g8: White: 3  | Black: \r\n", see.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
	}
}
