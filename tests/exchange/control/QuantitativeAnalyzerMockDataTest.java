package exchange.control;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import gamestate.Bitboard;
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
	
	@Test
	void testParallelCaptureSets() {
		QuantitativeAnalyzer myspy = Mockito.spy(new QuantitativeAnalyzer());

		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				myspy.addAttackSetPieceTypeSquare(PieceType.KING, Square.C6, Player.WHITE, 0xe0a0e00000000l);
				myspy.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.D3, Player.WHITE, 0x80808f60808l);
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.F4, Player.WHITE, 0x885000508804l);
				myspy.addAttackSetPawn(Player.WHITE, Bitboard.initFromAlgebraicSquares("a4", "g6"));
				myspy.addAttackSetPawn(Player.WHITE, Bitboard.initFromAlgebraicSquares("b5", "c4"));
				
				myspy.addAttackSetPieceTypeSquare(PieceType.KING, Square.F7, Player.BLACK, 0x7050700000000000l);
				myspy.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.G6, Player.BLACK, 0xa0100010a0000000l);
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.D6, Player.BLACK, 0x2214001422010000l);
				myspy.addAttackSetPawn(Player.BLACK, Bitboard.initFromAlgebraicSquares("d5", "e5", "f6"));
				myspy.addAttackSetPawn(Player.BLACK, Bitboard.initFromAlgebraicSquares("f5", "g5", "h6"));
				myspy.sortSets();
				return null;
			}
		}).when(myspy).populateAttackSets();
		myspy.initializeBoardState(new Gamestate("8/5kp1/2Kbppn1/7P/P4B2/1P1R4/8/8 w - - 0 1"));
		
		myspy.populate_temp_parallelCaptureSets();
		
		System.out.println(myspy.toString_temp_parallelCaptureSets());
		
		assertEquals(1, 0);


	}

}
