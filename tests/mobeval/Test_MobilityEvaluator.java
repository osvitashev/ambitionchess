package mobeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import basicseeval.BroadStaticExchangeEvaluator;
import gamestate.Bitboard;
import gamestate.BitboardGen;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class Test_MobilityEvaluator {
	
	private Gamestate game = new Gamestate();
	private BroadStaticExchangeEvaluator seeval = new BroadStaticExchangeEvaluator(game);
	private BroadMobilityEvaluator meval = new BroadMobilityEvaluator(game, seeval);
	
	@Test
	void testBlockadedPawns() {
		game.loadFromFEN("8/pP1p1p2/p2k2pK/5ppP/1n1p1P2/1P4p1/PP1P2P1/8 w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals("{b2 g2 f4 }", Bitboard.toListString(meval.get_output_blockadedPawns(Player.WHITE)));
		assertEquals("{g3 f5 g6 a7 }", Bitboard.toListString(meval.get_output_blockadedPawns(Player.BLACK)));
	}
	
	@Test
	void testKnightFlooding() {
		game.loadFromFEN("rnq1k2r/1p2n1p1/2pR4/p6p/P6P/8/1P2BPP1/R3K2R w - - 0 1");
		seeval.initialize();
		meval.initialize();
		meval.doFloodFill_knight(Square.B8);
	}
	
	@Test
	void testRookFlooding() {
//		assertEquals(0xffff0cebff08fddfl,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("a2"),//initial seed
//						0xf31400f70220l,//occupied
//						0//beaten
//				)
//		);
//		assertEquals(0xffff0cebff08fddfl,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("a4"),//initial seed
//						0xf31400f70220l,//occupied
//						0//beaten
//				)
//		);
//		assertEquals(0xffff0cebff08fddfl,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("b7"),//initial seed
//						0xf31400f70220l,//occupied
//						0//beaten
//				)
//		);
//		assertEquals(0xffff0cebff08fddfl,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("g5"),//initial seed
//						0xf31400f70220l,//occupied
//						0//beaten
//				)
//		);
//		//adding beaten 
//		assertEquals(-0xf3f7fff70322l,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("c1"),//initial seed
//						0xf31400f70220l,//occupied
//						0x8000001l//beaten
//				)
//		);
//		//seed overlaps with occupied mask
//		assertEquals(0xa337000000l,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("c4"),//initial seed
//						0xf31404f70220l,//occupied
//						0x40c8000000l//beaten
//				)
//		);
//		//seed partially overlaps with the beaten mask
//		assertEquals(0x180e1d1b1f1b791fl,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("b8", "d8"),//initial seed
//						0x2011222420640660l,//occupied
//						0x700000000008000l//beaten
//				)
//		);
//		assertEquals(0x180e1d1b1f1b7800l,
//				BroadMobilityEvaluator.doFloodFill_rook(
//						Bitboard.initFromAlgebraicSquares("b8", "d8"),//initial seed
//						0x2011222420640660l,//occupied
//						0x70000000000811bl//beaten
//				)
//		);
		
		game.loadFromFEN("8/p3n3/1kb3pp/3r3P/1P1b1N2/2P2Q2/3P1PP1/4K2R w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals(0xc0c08060l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.H1, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserPieceTargets(Player.BLACK, PieceType.ROOK)//beaten
				)
		);
		
		assertEquals(-5022075999715328l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.D5, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserPieceTargets(Player.WHITE, PieceType.ROOK)//beaten
				)
		);
		
		game.loadFromFEN("8/p3n3/1kb3p1/3r3P/1P1b1N2/2P2Q2/3P1PP1/4K2R w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals(278111748192l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.H1, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserPieceTargets(Player.BLACK, PieceType.ROOK)//beaten
				)
		);
		
		
		/**
		 * consider adding 'attacked by more than one piece of this type'[player][type] array to SEEval.
		 * this make it easier to handle cases not caught by 'lesser defender' heuristic - i.e. to bulk evaluate basic exchanges!
		 */
		
	}

}
