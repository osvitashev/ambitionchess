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
		
		myspy.populate_temp_parallelCaptureSets(Player.WHITE);
		myspy.populate_temp_parallelCaptureSets(Player.BLACK);
				
		assertEquals("a1: White:  | Black: \r\n"
				+ "b1: White:  | Black: \r\n"
				+ "c1: White: B | Black: \r\n"
				+ "d1: White: R | Black: \r\n"
				+ "e1: White:  | Black: \r\n"
				+ "f1: White:  | Black: \r\n"
				+ "g1: White:  | Black: \r\n"
				+ "h1: White:  | Black: \r\n"
				+ "a2: White:  | Black: \r\n"
				+ "b2: White:  | Black: \r\n"
				+ "c2: White:  | Black: \r\n"
				+ "d2: White: BR | Black: \r\n"
				+ "e2: White:  | Black: \r\n"
				+ "f2: White:  | Black: \r\n"
				+ "g2: White:  | Black: \r\n"
				+ "h2: White: B | Black: \r\n"
				+ "a3: White:  | Black: B\r\n"
				+ "b3: White: R | Black: \r\n"
				+ "c3: White: R | Black: \r\n"
				+ "d3: White:  | Black: \r\n"
				+ "e3: White: BR | Black: \r\n"
				+ "f3: White: R | Black: \r\n"
				+ "g3: White: BR | Black: \r\n"
				+ "h3: White: R | Black: \r\n"
				+ "a4: White: P | Black: \r\n"
				+ "b4: White:  | Black: B\r\n"
				+ "c4: White: P | Black: \r\n"
				+ "d4: White: R | Black: \r\n"
				+ "e4: White:  | Black: \r\n"
				+ "f4: White:  | Black: NB\r\n"
				+ "g4: White:  | Black: \r\n"
				+ "h4: White:  | Black: N\r\n"
				+ "a5: White:  | Black: \r\n"
				+ "b5: White: PK | Black: \r\n"
				+ "c5: White: K | Black: B\r\n"
				+ "d5: White: RK | Black: P\r\n"
				+ "e5: White: B | Black: PNB\r\n"
				+ "f5: White:  | Black: P\r\n"
				+ "g5: White: B | Black: P\r\n"
				+ "h5: White:  | Black: \r\n"
				+ "a6: White:  | Black: \r\n"
				+ "b6: White: K | Black: \r\n"
				+ "c6: White:  | Black: \r\n"
				+ "d6: White: BRK | Black: \r\n"
				+ "e6: White:  | Black: K\r\n"
				+ "f6: White:  | Black: PK\r\n"
				+ "g6: White: P | Black: K\r\n"
				+ "h6: White: B | Black: P\r\n"
				+ "a7: White:  | Black: \r\n"
				+ "b7: White: K | Black: \r\n"
				+ "c7: White: K | Black: B\r\n"
				+ "d7: White: K | Black: \r\n"
				+ "e7: White:  | Black: NBK\r\n"
				+ "f7: White:  | Black: \r\n"
				+ "g7: White:  | Black: K\r\n"
				+ "h7: White:  | Black: \r\n"
				+ "a8: White:  | Black: \r\n"
				+ "b8: White:  | Black: B\r\n"
				+ "c8: White:  | Black: \r\n"
				+ "d8: White:  | Black: \r\n"
				+ "e8: White:  | Black: K\r\n"
				+ "f8: White:  | Black: NBK\r\n"
				+ "g8: White:  | Black: K\r\n"
				+ "h8: White:  | Black: N\r\n", myspy.toString_temp_parallelCaptureSets().replaceAll("\n", "\r\n"));
		
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
		
		myspy.populate_temp_parallelCaptureSets(Player.WHITE);
		myspy.populate_temp_parallelCaptureSets(Player.BLACK);
		
		System.out.println(myspy.toString_temp_parallelCaptureSets());
		
		assertEquals("a1: White:  | Black: \r\n"
				+ "b1: White:  | Black: \r\n"
				+ "c1: White: B | Black: \r\n"
				+ "d1: White: RQ | Black: \r\n"
				+ "e1: White:  | Black: \r\n"
				+ "f1: White: Q | Black: \r\n"
				+ "g1: White: B | Black: \r\n"
				+ "h1: White: Q | Black: \r\n"
				+ "a2: White:  | Black: \r\n"
				+ "b2: White: K | Black: \r\n"
				+ "c2: White: K | Black: \r\n"
				+ "d2: White: BRK | Black: \r\n"
				+ "e2: White: BQ | Black: \r\n"
				+ "f2: White: BQ | Black: \r\n"
				+ "g2: White: BQ | Black: \r\n"
				+ "h2: White:  | Black: \r\n"
				+ "a3: White:  | Black: \r\n"
				+ "b3: White: RK | Black: P\r\n"
				+ "c3: White:  | Black: \r\n"
				+ "d3: White: BRK | Black: P\r\n"
				+ "e3: White: Q | Black: R\r\n"
				+ "f3: White:  | Black: P\r\n"
				+ "g3: White: Q | Black: B\r\n"
				+ "h3: White: B | Black: P\r\n"
				+ "a4: White: PR | Black: NBQ\r\n"
				+ "b4: White: K | Black: BQ\r\n"
				+ "c4: White: PBRRK | Black: N\r\n"
				+ "d4: White: BK | Black: RQ\r\n"
				+ "e4: White: RQ | Black: PB\r\n"
				+ "f4: White: PNBQ | Black: BR\r\n"
				+ "g4: White: Q | Black: PPR\r\n"
				+ "h4: White: PN | Black: Q\r\n"
				+ "a5: White:  | Black: \r\n"
				+ "b5: White: PNR | Black: PBQ\r\n"
				+ "c5: White:  | Black: BQ\r\n"
				+ "d5: White: R | Black: NBQ\r\n"
				+ "e5: White: N | Black: BRQQ\r\n"
				+ "f5: White: Q | Black: NQQ\r\n"
				+ "g5: White: PB | Black: Q\r\n"
				+ "h5: White:  | Black: NR\r\n"
				+ "a6: White:  | Black: QK\r\n"
				+ "b6: White: RQ | Black: QK\r\n"
				+ "c6: White: N | Black: K\r\n"
				+ "d6: White: RQ | Black: Q\r\n"
				+ "e6: White:  | Black: PNRQ\r\n"
				+ "f6: White: Q | Black: \r\n"
				+ "g6: White:  | Black: PQ\r\n"
				+ "h6: White: B | Black: R\r\n"
				+ "a7: White:  | Black: K\r\n"
				+ "b7: White:  | Black: B\r\n"
				+ "c7: White: Q | Black: BK\r\n"
				+ "d7: White: Q | Black: NB\r\n"
				+ "e7: White: NQ | Black: BRQ\r\n"
				+ "f7: White:  | Black: Q\r\n"
				+ "g7: White:  | Black: Q\r\n"
				+ "h7: White:  | Black: R\r\n"
				+ "a8: White: Q | Black: NK\r\n"
				+ "b8: White: Q | Black: BK\r\n"
				+ "c8: White: NQ | Black: NK\r\n"
				+ "d8: White:  | Black: RQ\r\n"
				+ "e8: White: Q | Black: NBRR\r\n"
				+ "f8: White: NQ | Black: BR\r\n"
				+ "g8: White: Q | Black: R\r\n"
				+ "h8: White: NQ | Black: \r\n", myspy.toString_temp_parallelCaptureSets().replaceAll("\n", "\r\n"));


	}

}
