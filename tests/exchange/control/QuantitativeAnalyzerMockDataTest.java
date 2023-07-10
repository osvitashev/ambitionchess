package exchange.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class QuantitativeAnalyzerMockDataTest {

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
		
		System.out.println(qa.toString());
		
	}

}
