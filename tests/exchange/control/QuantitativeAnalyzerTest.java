package exchange.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class QuantitativeAnalyzerTest {

	@Test
	void basicInitializationTest() {
		QuantitativeAnalyzer qa = new QuantitativeAnalyzer();
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
	
//	@Test
//	void reinitializeGainLossToPieceValuesTest() {
//		Gamestate brd;
//		QuantitativeAnalyzer qa = new QuantitativeAnalyzer();
//		brd = new Gamestate("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
//		qa.initializeBoardState(brd);
//		qa.reinitializeGainLossToPieceValues();
//		
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.A1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.B1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.C1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.QUEEN), qa.getTemp_exchangeGainLoss(Square.D1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E1));//king
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.F1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.G1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.H1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.A2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.B2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.C2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.D2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.E4));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.F2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.G2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.H2));
//		
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.A8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.B8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.C8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.QUEEN), qa.getTemp_exchangeGainLoss(Square.D8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E8));//king
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.F8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.G8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.H8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.A7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.B7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.C7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.D7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.E5));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.F7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.G7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.H7));
//		//empty
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E2));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E7));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.B3));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.C4));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.D6));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E6));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E3));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.H6));
//		
//		brd = new Gamestate("3r1rk1/pb1q1pbp/1pnp1np1/2p1p1B1/4P3/2NP1N2/PPPQBPPP/2KR3R w - - 0 1");
//		qa.initializeBoardState(brd);
//		qa.reinitializeGainLossToPieceValues();
//		
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.D1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.C3));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.E2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.QUEEN), qa.getTemp_exchangeGainLoss(Square.D2));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.C1));//king
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.G5));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.F3));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.H1));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.A2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.B2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.C2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.D3));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.E4));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.F2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.G2));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.H2));
//		
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.D8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.C6));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.B7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.QUEEN), qa.getTemp_exchangeGainLoss(Square.D7));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E8));//king
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.BISHOP), qa.getTemp_exchangeGainLoss(Square.G7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.KNIGHT), qa.getTemp_exchangeGainLoss(Square.F6));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.ROOK), qa.getTemp_exchangeGainLoss(Square.F8));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.A7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.B6));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.C5));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.D6));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.E5));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.F7));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.G6));
//		assertEquals(QuantitativeAnalyzer.getPieceValue(PieceType.PAWN), qa.getTemp_exchangeGainLoss(Square.H7));
//		
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.A1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.B1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.F1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.G1));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.A8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.B8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.C8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.H8));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.C7));
//		assertEquals(0, qa.getTemp_exchangeGainLoss(Square.E7));
//	}

}
