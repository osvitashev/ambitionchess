package exchange.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class QuantitativeAnalyzerMockDataTest {

	// QuantitativeAnalyzer qa = new QuantitativeAnalyzer();

	@Test
	void testMockDataInjection() {
		QuantitativeAnalyzer myspy = Mockito.spy(new QuantitativeAnalyzer());

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.A1, Player.WHITE, 0x1);
				return null;
			}
		}).when(myspy).populateAttackSets();
		myspy.initializeBoardState(new Gamestate("8/5kp1/2Kbppn1/7P/P4B2/1P1R4/8/8 w - - 0 1"));
		assertEquals("White:\n" + "{Q a1 -> 0x1}\n" + "Black:\n" + "", myspy.toString());

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.A2, Player.WHITE, 0x22);
				myspy.addAttackSetPieceTypeSquare(PieceType.KING, Square.D6, Player.BLACK, 0x55);
				return null;
			}
		}).when(myspy).populateAttackSets();
		myspy.initializeBoardState(new Gamestate("8/5kp1/2Kbppn1/7P/P4B2/1P1R4/8/8 w - - 0 1"));
		assertEquals("White:\n" + 
				"{Q a2 -> 0x22}\n" + 
				"Black:\n" + 
				"{K d6 -> 0x55}\n", 
				myspy.toString());
	}

}
