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
		assertEquals(0xffff0cebff08fddfl,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("a2"),//initial seed
						0xf31400f70220l,//occupied
						0//beaten
				)
		);
		assertEquals(0xffff0cebff08fddfl,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("a4"),//initial seed
						0xf31400f70220l,//occupied
						0//beaten
				)
		);
		assertEquals(0xffff0cebff08fddfl,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("b7"),//initial seed
						0xf31400f70220l,//occupied
						0//beaten
				)
		);
		assertEquals(0xffff0cebff08fddfl,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("g5"),//initial seed
						0xf31400f70220l,//occupied
						0//beaten
				)
		);
		//adding beaten 
		assertEquals(-0xf3f7fff70322l,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("c1"),//initial seed
						0xf31400f70220l,//occupied
						0x8000001l//beaten
				)
		);
		//seed overlaps with occupied mask
		assertEquals(0xa337000000l,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("c4"),//initial seed
						0xf31404f70220l,//occupied
						0x40c8000000l//beaten
				)
		);
		//seed partially overlaps with the beaten mask
		assertEquals(0x180e1d1b1f1b791fl,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("b8", "d8"),//initial seed
						0x2011222420640660l,//occupied
						0x700000000008000l//beaten
				)
		);
		assertEquals(0x180e1d1b1f1b7800l,
				BroadMobilityEvaluator.doFloodFill_rook(
						Bitboard.initFromAlgebraicSquares("b8", "d8"),//initial seed
						0x2011222420640660l,//occupied
						0x70000000000811bl//beaten
				)
		);
		
		game.loadFromFEN("8/p3n3/1kb3pp/3r3P/1P1b1N2/2P2Q2/3P1PP1/4K2R w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals(0xc0c08060l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.H1, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserOrEqualPieceTargets(Player.BLACK, PieceType.ROOK)//beaten
				)
		);
		
		assertEquals(-5022078147231744l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.D5, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserOrEqualPieceTargets(Player.WHITE, PieceType.ROOK)//beaten
				)
		);
		
		game.loadFromFEN("8/p3n3/1kb3p1/3rp2P/1P1b1N2/2P2Q2/3P1PP1/4K2R w - - 0 1");
		seeval.initialize();
		meval.initialize();
		assertEquals(278111748192l,
				BroadMobilityEvaluator.doFloodFill_rook(
						BitboardGen.getRookSet(Square.H1, game.getOccupied()) & game.getEmpty(),//initial seed
						game.getOccupied(),//occupied
						seeval.get_output_attackedByLesserOrEqualPieceTargets(Player.BLACK, PieceType.ROOK)//beaten
				)
		);
		
		
		/**
		 * consider adding 'attacked by more than one piece of this type'[player][type] array to SEEval.
		 * this make it easier to handle cases not caught by 'lesser defender' heuristic - i.e. to bulk evaluate basic exchanges!
		 */
		
	}
	
	private void helper_progressiveRookMobility(String fen, int sq, long expected_1, long expected_2, long expected_3) {
		game.loadFromFEN(fen);
		seeval.initialize();
		seeval.evaluateQuietMoves();//todo: see if this can be dropped.
		meval.initialize();
		int player = game.getPlayerAt(sq);
		meval.processRook(player, sq);
		assertEquals(1, meval.get_output_mobCollection_size(player));
		assertEquals(sq, meval.get_output_mobCollection_sqFrom(player, 0));
		assertEquals(PieceType.ROOK, meval.get_output_mobCollection_pieceType(player, 0));
		assertEquals(expected_1, meval.get_output_mobCollection_safe_1(player, 0));
		assertEquals(expected_2, meval.get_output_mobCollection_safe_2(player, 0));
		assertEquals(expected_3, meval.get_output_mobCollection_safe_3(player, 0));
	}
	
	@Test
	void testProcessRook_progressiveMobility() {
		helper_progressiveRookMobility("1b2r3/1B3pp1/4n3/1K5k/2R3q1/8/PP1Nb3/8 w - - 0 1", Square.E8,
			0xe810000000000000l, //first step
			-1686448526868348928l, //second step
			-1686342973738450944l //third step
		);
		
		helper_progressiveRookMobility("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", Square.B1,
			525l, //first step
			2829l, //second step
			6925l //third step
		);
		
		helper_progressiveRookMobility("rnq1k2r/1p2n1p1/2pR4/p6p/P6P/8/1P2BPP1/R3K2R w - - 0 1", Square.A1,
			0x1010el, //first step
			17397910798l, //second step
			20153009933582l //third step
		);
		helper_progressiveRookMobility("rnq1k2r/1p2n1p1/2pR4/p6p/P6P/8/1P2BPP1/R3K2R w - - 0 1", Square.A8,
			281474976710656l, //first step
			281474976710656l, //second step
			281474976710656l //third step
		);
		helper_progressiveRookMobility("rnq1k2r/1p2n1p1/2pR4/p6p/P6P/8/1P2BPP1/R3K2R w - - 0 1", Square.H1,
			8421472l, //first step
			16744544l, //second step
			20153009933676l //third step
		);
		helper_progressiveRookMobility("rnq1k2r/1p2n1p1/2pR4/p6p/P6P/8/1P2BPP1/R3K2R w - - 0 1", Square.H8,
			6953557824660045824l, //first step
			6962565161890611200l, //second step
			6962565248091947008l //third step
		);
		
		helper_progressiveRookMobility("4r3/bB4p1/4n3/1K5k/2Rp2q1/8/PP1Nb3/5Q2 w - - 0 1", Square.E8,
			-1580763469207044096l, //first step
			-1542298016445104128l, //second step
			-1542227304103542784l //third step
		);
		
		//>>> in this position B6 should NOT be a safe destination!!!!!
		
		
//		helper_progressiveRookMobility("", Square.A1,
//			0l, //first step
//			0l, //second step
//			0l //third step
//		);
	}

}
