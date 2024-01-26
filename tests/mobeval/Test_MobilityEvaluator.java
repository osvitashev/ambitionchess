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
			-1542225105080287232l //third step
		);//>>> in this position B6 should NOT be a safe destination!!!!!
		
		/**
		 * the cases below are a list of permutations of one defender and one attacker
		 */
		
		helper_progressiveRookMobility("8/8/8/6k1/p7/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0xfd0100l, //second step
				-4846968191886426636l //third step
		);// 0 - 0
		
		helper_progressiveRookMobility("8/8/8/8/p3p3/7k/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				1376512, //second step
				289360691353425172l //third step
		);// 0 - p
		
		helper_progressiveRookMobility("8/8/2p2k2/6n1/p7/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0x5d0100l, //second step
				1731642871278034260l //third step
		);// 0 - n
		
		helper_progressiveRookMobility("8/8/2p2k2/8/p7/8/1P3b2/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0xad0100l, //second step
				-8608480603766157948l //third step
		);// 0 - b
		
		helper_progressiveRookMobility("8/6n1/2p2kr1/8/p7/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0xbd0100l, //second step
				-8608621871279260236l //third step
		);// 0 - r
		
		helper_progressiveRookMobility("8/6n1/2p2k2/8/p5q1/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				1900800l, //second step
				576469565578222868l //third step
		);// 0 - q
		
		helper_progressiveRookMobility("8/6n1/2p2k2/8/p5q1/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				1900800l, //second step
				576469565578222868l //third step
		);// 0 - q
		
		helper_progressiveRookMobility("8/6n1/2p2k2/8/p5q1/8/1P5P/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0x5d0100l, //second step
				576469565582417172l //third step
		);// p - q
		
		helper_progressiveRookMobility("8/6n1/2p2k2/5N2/p5q1/8/1P6/RN1K4 w - - 0 1", Square.A1,
				0x10100l, //first step
				0x5d0100l, //second step
				583224965157690644l //third step
		);// n - q
		
		helper_progressiveRookMobility("8/6n1/2pk4/8/p5q1/8/1P4Q1/RNK5 w - - 0 1", Square.A1,
				0x10100l, //first step
				0xfd0100l, //second step
				-6872317109221868104l //third step
		);// q - q
		
		helper_progressiveRookMobility("8/6n1/2p5/8/p5k1/8/1P5B/RNK5 w - - 0 1", Square.A1,
				0x10100l, //first step
				0x5d0100l, //second step
				583225034212728152l //third step
		);// b - k
		
		helper_progressiveRookMobility("4r3/bB4p1/8/1K2n2k/2Rp2q1/8/PP1Nb3/5Q2 w - - 0 1", Square.E8,
				-3886588886234693632l, //first step
				-3839043804426403840l, //second step
				-3839043391572672512l //third step
		);// b - k
		
		helper_progressiveRookMobility("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", Square.G8,
				4341540410603077632l, //first step
				4357470135603167232l, //second step
				4357470273042120704l //third step
		);
		
		helper_progressiveRookMobility("1k4rr/1pp4p/1p3p2/1P2p2q/P1P4P/3P1Np1/4RPK1/6R1 w - - 0 29", Square.E2,
				269487888l, //first step
				303501119l, //second step
				303501119l //third step
		);
		
		helper_progressiveRookMobility("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", Square.E6,
				1157429502280728576l, //first step
				-571952754629541888l, //second step
				-535923957610577920l //third step
		);
		helper_progressiveRookMobility("2r5/1p1k1pp1/1p2rn1p/1Nqp4/P2P1Q2/2P4P/1P4P1/R3R2K b - - 0 23", Square.E1,
				110l, //first step
				2632814l, //second step
				6827118l //third step
		);
		
		helper_progressiveRookMobility("r2r2k1/ppq2pb1/4b1pp/nP1np3/B3N3/B1PP1NP1/2Q2P1P/1R2R1K1 b - - 0 18", Square.D8,
				1587518868648099840l, //first step
				1587518868648099840l, //second step
				1587518868648099840l //third step
		);
		
		helper_progressiveRookMobility("8/8/2Rb3p/1P1kN3/3P1P2/7P/2p3PK/2r5 w - - 1 38", Square.C6,
				288233674753966080l, //first step
				-2521168063475351296l, //second step
				-2455795499060544768l //third step
		);
		
		helper_progressiveRookMobility("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", Square.B8,
				288230376151711744l, //first step
				289356276058554368l, //second step
				289356276058554368l //third step
		);
		
		helper_progressiveRookMobility("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", Square.F8,
				35321811042304l, //first step
				35875861823488l, //second step
				177712861806592l //third step
		);
		
		helper_progressiveRookMobility("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", Square.A1,
				14l, //first step
				789006l, //second step
				3938830l //third step
		);
		
		helper_progressiveRookMobility("1r1nnrk1/p3b1pp/1p1pP1q1/8/2PQ1P2/BP4PP/P4PB1/R3R1K1 w - - 3 23", Square.E1,
				269488174l, //first step
				272374318l, //second step
				272374318l //third step
		);
		
		helper_progressiveRookMobility("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", Square.B8,
				72620552581283840l, //first step
				80501856224149504l, //second step
				80501856291258625l //third step
		);
		
		helper_progressiveRookMobility("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", Square.C8,
				1125899973951488l, //first step
				8444249368428544l, //second step
				8444257958363136l //third step
		);
		
		helper_progressiveRookMobility("1rrqn1k1/5p1p/b2p2pQ/3P4/1p1BP1P1/2p2P1P/1PB5/3N2RK w - - 0 31", Square.G1,
				4210704l, //first step
				5300240l, //second step
				5300240l //third step
		);
		
		helper_progressiveRookMobility("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", Square.D6,
				576531155407339520l, //first step
				1080934313677029376l, //second step
				1080934313685417984l //third step
		);
		
		helper_progressiveRookMobility("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", Square.D1,
				2272l, //first step
				2155912416l, //second step
				2160106720l //third step
		);
		
		helper_progressiveRookMobility("8/p3rqpk/1p1R3p/1bp2p1P/4pN2/2QnP3/PP3PP1/1K1R4 w - - 1 27", Square.E7,
				1154610423186587648l, //first step
				-646829427761610752l, //second step
				-646829427761610752l //third step
		);
		
		helper_progressiveRookMobility("6k1/5pp1/1p2p2p/rp1pN3/1R1P2P1/PRn1PP2/2r3P1/5K2 w - - 0 29", Square.B3,
				0l, //first step
				0l, //second step
				0l //third step
		);
		
		helper_progressiveRookMobility("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", Square.E8,
				941252322120433664l, //first step
				961801099377967104l, //second step
				5573487117805355008l //third step
		);
		
		helper_progressiveRookMobility("4rb2/2k5/2b1p1p1/2p1PpNp/2P2P1P/6P1/P2K4/1R2R3 b - - 0 33", Square.E1,
				1052780l, //first step
				2077804l, //second step
				284777817028204l //third step
		);
		
		helper_progressiveRookMobility("5rk1/pp1r1ppp/1qp1pn2/8/2PP4/1PQ2BP1/P4P1P/3RR1K1 w - - 5 19", Square.E1,
				68720529440l, //first step
				365073800736l, //second step
				365090643494l //third step
		);
		
		helper_progressiveRookMobility("r2q1rk1/p2p1ppp/2pNp1n1/2P1P3/R4PP1/B5K1/7P/3Q1B1R w - - 1 23", Square.A8,
				144115188075855872l, //first step
				144115188075855872l, //second step
				144115188075855872l //third step
		);
		
		helper_progressiveRookMobility("5rk1/pb3ppp/1p5r/3p2q1/3Nn3/P3PBP1/1PQ2P1P/3RR1K1 b - - 0 21", Square.F8,
				2233785415175766016l, //first step
				2240549696676298752l, //second step
				2240655254087532544l //third step
		);
		
		helper_progressiveRookMobility("3q2B1/P3r1Bp/p3P3/Q2N2k1/nPR5/1K2N2p/7P/2r4b w - - 0 1", Square.C4,
				1744830464l, //first step
				2305878332774295552l, //second step
				2306019138982131968l //third step
		);
		
//		helper_progressiveRookMobility("", Square.A1,
//			0l, //first step
//			0l, //second step
//			0l //third step
//		);
	}

}
