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
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class StaticExchangeEvaluatorMockDataTest {

	// QuantitativeAnalyzer qa = new QuantitativeAnalyzer();

	@Test
	void testMockDataInjection() {
		StaticExchangeEvaluator myspy = Mockito.spy(new StaticExchangeEvaluator());

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
		StaticExchangeEvaluator myspy = Mockito.spy(new StaticExchangeEvaluator());
		Gamestate brd;

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
		
		myspy.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		myspy.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
				
		assertEquals("c1: White: 3  | Black: \r\n"
				+ "d1: White: 5  | Black: \r\n"
				+ "d2: White: 3 5  | Black: \r\n"
				+ "h2: White: 3  | Black: \r\n"
				+ "a3: White:  | Black: 3 \r\n"
				+ "b3: White: 5  | Black: \r\n"
				+ "c3: White: 5  | Black: \r\n"
				+ "e3: White: 3 5  | Black: \r\n"
				+ "f3: White: 5  | Black: \r\n"
				+ "g3: White: 3 5  | Black: \r\n"
				+ "h3: White: 5  | Black: \r\n"
				+ "a4: White: 1  | Black: \r\n"
				+ "b4: White:  | Black: 3 \r\n"
				+ "c4: White: 1  | Black: \r\n"
				+ "d4: White: 5  | Black: \r\n"
				+ "f4: White:  | Black: 3 3 \r\n"
				+ "h4: White:  | Black: 3 \r\n"
				+ "b5: White: 1 15  | Black: \r\n"
				+ "c5: White: 15  | Black: 3 \r\n"
				+ "d5: White: 5 15  | Black: 1 \r\n"
				+ "e5: White: 3  | Black: 1 3 3 \r\n"
				+ "f5: White:  | Black: 1 \r\n"
				+ "g5: White: 3  | Black: 1 \r\n"
				+ "b6: White: 15  | Black: \r\n"
				+ "d6: White: 3 5 15  | Black: \r\n"
				+ "e6: White:  | Black: 15 \r\n"
				+ "f6: White:  | Black: 1 15 \r\n"
				+ "g6: White: 1  | Black: 15 \r\n"
				+ "h6: White: 3  | Black: 1 \r\n"
				+ "b7: White: 15  | Black: \r\n"
				+ "c7: White: 15  | Black: 3 \r\n"
				+ "d7: White: 15  | Black: \r\n"
				+ "e7: White:  | Black: 3 3 15 \r\n"
				+ "g7: White:  | Black: 15 \r\n"
				+ "b8: White:  | Black: 3 \r\n"
				+ "e8: White:  | Black: 15 \r\n"
				+ "f8: White:  | Black: 3 3 15 \r\n"
				+ "g8: White:  | Black: 15 \r\n"
				+ "h8: White:  | Black: 3 \r\n"
				+ "", myspy.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));
		
		brd=new Gamestate("3Q3r/Nk3pn1/pnbb1qN1/q4p1p/PRpRr1pP/1PK1BQP1/8/5B2 w - - 0 1");
		doAnswer(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				myspy.addAttackSetPawn(Player.WHITE, Bitboard.initFromAlgebraicSquares("b5", "c4", "h4"));
				myspy.addAttackSetPawn(Player.WHITE, Bitboard.initFromAlgebraicSquares("a4", "f4", "g5"));
				myspy.addAttackSetPieceTypeSquare(PieceType.KING, Square.C3, Player.WHITE, BitboardGen.getKingSet(Square.C3));
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.D8, Player.WHITE, BitboardGen.getQueenSet(Square.D8, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.F3, Player.WHITE, BitboardGen.getQueenSet(Square.F3, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.B4, Player.WHITE, BitboardGen.getRookSet(Square.B4, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.D4, Player.WHITE, BitboardGen.getRookSet(Square.D4, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.A7, Player.WHITE, BitboardGen.getKnightSet(Square.A7));
				myspy.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.G6, Player.WHITE, BitboardGen.getKnightSet(Square.G6));
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.E3, Player.WHITE, BitboardGen.getBishopSet(Square.E3, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.F1, Player.WHITE, BitboardGen.getBishopSet(Square.F1, brd.getOccupied()));
				

				myspy.addAttackSetPawn(Player.BLACK, Bitboard.initFromAlgebraicSquares("b5", "d3", "g4", "h3", "g6"));
				myspy.addAttackSetPawn(Player.BLACK, Bitboard.initFromAlgebraicSquares("b3", "e4", "f3", "g4", "e6"));
				
				myspy.addAttackSetPieceTypeSquare(PieceType.KING, Square.B7, Player.BLACK, BitboardGen.getKingSet(Square.B7));
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.A5, Player.BLACK, BitboardGen.getQueenSet(Square.A5, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.QUEEN, Square.F6, Player.BLACK, BitboardGen.getQueenSet(Square.F6, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.E4, Player.BLACK, BitboardGen.getRookSet(Square.E4, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.ROOK, Square.H8, Player.BLACK, BitboardGen.getRookSet(Square.H8, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.B6, Player.BLACK, BitboardGen.getKnightSet(Square.B6));
				myspy.addAttackSetPieceTypeSquare(PieceType.KNIGHT, Square.G7, Player.BLACK, BitboardGen.getKnightSet(Square.G7));
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.C6, Player.BLACK, BitboardGen.getBishopSet(Square.C6, brd.getOccupied()));
				myspy.addAttackSetPieceTypeSquare(PieceType.BISHOP, Square.D6, Player.BLACK, BitboardGen.getBishopSet(Square.D6, brd.getOccupied()));
				
				
				myspy.sortSets();
				return null;
			}
		}).when(myspy).populateAttackSets();
		myspy.initializeBoardState(brd);
		
		myspy.populate_temp_serializedCaptureSets_pieceCosts(Player.WHITE);
		myspy.populate_temp_serializedCaptureSets_pieceCosts(Player.BLACK);
		
		System.out.println(myspy.toString_temp_serializedCaptureSets_pieceCosts());
		
		assertEquals("c1: White: 3  | Black: \r\n"
				+ "d1: White: 5 9  | Black: \r\n"
				+ "f1: White: 9  | Black: \r\n"
				+ "g1: White: 3  | Black: \r\n"
				+ "h1: White: 9  | Black: \r\n"
				+ "b2: White: 15  | Black: \r\n"
				+ "c2: White: 15  | Black: \r\n"
				+ "d2: White: 3 5 15  | Black: \r\n"
				+ "e2: White: 3 9  | Black: \r\n"
				+ "f2: White: 3 9  | Black: \r\n"
				+ "g2: White: 3 9  | Black: \r\n"
				+ "b3: White: 5 15  | Black: 1 \r\n"
				+ "d3: White: 3 5 15  | Black: 1 \r\n"
				+ "e3: White: 9  | Black: 5 \r\n"
				+ "f3: White:  | Black: 1 \r\n"
				+ "g3: White: 9  | Black: 3 \r\n"
				+ "h3: White: 3  | Black: 1 \r\n"
				+ "a4: White: 1 5  | Black: 3 3 9 \r\n"
				+ "b4: White: 15  | Black: 3 9 \r\n"
				+ "c4: White: 1 3 5 5 15  | Black: 3 \r\n"
				+ "d4: White: 3 15  | Black: 5 9 \r\n"
				+ "e4: White: 5 9  | Black: 1 3 \r\n"
				+ "f4: White: 1 3 3 9  | Black: 3 5 \r\n"
				+ "g4: White: 9  | Black: 1 1 5 \r\n"
				+ "h4: White: 1 3  | Black: 9 \r\n"
				+ "b5: White: 1 3 5  | Black: 1 3 9 \r\n"
				+ "c5: White:  | Black: 3 9 \r\n"
				+ "d5: White: 5  | Black: 3 3 9 \r\n"
				+ "e5: White: 3  | Black: 3 5 9 9 \r\n"
				+ "f5: White: 9  | Black: 3 9 9 \r\n"
				+ "g5: White: 1 3  | Black: 9 \r\n"
				+ "h5: White:  | Black: 3 5 \r\n"
				+ "a6: White:  | Black: 9 15 \r\n"
				+ "b6: White: 5 9  | Black: 9 15 \r\n"
				+ "c6: White: 3  | Black: 15 \r\n"
				+ "d6: White: 5 9  | Black: 9 \r\n"
				+ "e6: White:  | Black: 1 3 5 9 \r\n"
				+ "f6: White: 9  | Black: \r\n"
				+ "g6: White:  | Black: 1 9 \r\n"
				+ "h6: White: 3  | Black: 5 \r\n"
				+ "a7: White:  | Black: 15 \r\n"
				+ "b7: White:  | Black: 3 \r\n"
				+ "c7: White: 9  | Black: 3 15 \r\n"
				+ "d7: White: 9  | Black: 3 3 \r\n"
				+ "e7: White: 3 9  | Black: 3 5 9 \r\n"
				+ "f7: White:  | Black: 9 \r\n"
				+ "g7: White:  | Black: 9 \r\n"
				+ "h7: White:  | Black: 5 \r\n"
				+ "a8: White: 9  | Black: 3 15 \r\n"
				+ "b8: White: 9  | Black: 3 15 \r\n"
				+ "c8: White: 3 9  | Black: 3 15 \r\n"
				+ "d8: White:  | Black: 5 9 \r\n"
				+ "e8: White: 9  | Black: 3 3 5 5 \r\n"
				+ "f8: White: 3 9  | Black: 3 5 \r\n"
				+ "g8: White: 9  | Black: 5 \r\n"
				+ "h8: White: 3 9  | Black: \r\n"
				+ "", myspy.toString_temp_serializedCaptureSets_pieceCosts().replaceAll("\n", "\r\n"));


	}

}
