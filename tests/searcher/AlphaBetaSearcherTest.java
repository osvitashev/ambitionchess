package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class AlphaBetaSearcherTest {

	@Test
	void testMateIn3() {
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(new Evaluator(Evaluator.Builder.newInstance()));
		searcher.setFullDepthSearchLimit(5);
		String[][] tests = {
				{ "2r1k3/1b3ppp/p3p3/1p1n4/4q3/PQ2P1P1/1P2BP1P/5RK1 b - - 0 1", "{e4g2 g1g2 d5f4 g2g1 f4h3}" },
				{ "r1q2r1k/p4b2/1p2pP2/3p3p/1P1B1P2/2PB4/P3Q2P/6RK w - - 0 1", "{e2h5 f7h5 f6f7 e6e5 d4e5}" },
				{ "1k1r4/ppq2pp1/4p3/1PP5/Q5n1/7r/P4PB1/R1R3K1 b - - 0 1", "{h3h1 g2h1 c7h2 g1f1 h2f2}" },
				{ "1B6/NK6/3p4/p2R4/Pk6/1P1P4/B7/8 w - - 0 1", "{d5c5 d6d5 c5c2 d5d4 b8d6}" }, // has two solutions! {d5c5 d6d5 c5c2 b4a3 b8d6 } and {d5c5 d6c5 a7b5 c5c4 b8d6 } 
				{ "6B1/3N4/2qpN3/P2k4/4pQP1/8/P1P3nK/2b5 w - - 0 1", "{f4e3 g2e3 e6f4 d5d4 f4e2}" },
				{ "7b/8/r7/3B4/8/8/8/1kBK2Q1 w - - 0 1", "{d1e2 a6a1 d5e4 b1a2 g1g8}" },
				{ "8/1pkn4/p1p4K/7b/P6P/5p2/6r1/5R2 b - - 0 1", "{d7f6 f1g1 g2g1 a4a5 g1g6}" },
				{ "6k1/5ppp/Q1b3q1/2P5/PP6/5NB1/3rr1PP/5RK1 b - - 0 1", "{e2g2 g1h1 g2h2 g3h2 g6g2}" },
				{ "1bN5/2p2p1Q/4PB2/3k3P/8/P4N1K/P7/n4R2 w - - 0 1", "{a3a4 c7c6 h7d3 d5c5 d3d4}" },
				{ "3r3k/p5bp/2p3p1/2q1n3/PQ4P1/2P2pBP/3r1P2/2R1KBR1 b - - 0 1", "{c5e3 f2e3 f3f2 g3f2 e5f3}" },
				{ "1r5k/R6p/4pN2/8/3np3/2r3P1/P4KP1/8 b - - 0 1", "{b8b2 f2g1 d4f3 g2f3 c3c1}" },
				{ "1r5r/NRpk1ppp/p2bp3/7b/3P1P2/P1N4P/2q2PB1/4QRK1 w - - 1 0", "{g2c6 d7e7 c3d5 e7d8 b7b8}" },
				{ "4r2k/p3NR1p/3pb1pB/1p6/2rbP3/8/PPP5/2K4R w - - 1 0", "{f7h7 h8h7 h6f8 e6h3 h1h3}" },
				{ "2q1rbk1/8/4np1B/p2pPBp1/bp1P2P1/5NK1/5P2/1Q5R w - - 1 0", "{f5e6 e8e6 b1g6 f8g7 g6g7}" },
				{ "4r1k1/3n1ppp/4r3/3n3q/Q2P4/5P2/PP2BP1P/R1B1R1K1 b - - 0 1", "{e6g6 c1g5 h5g5 g1f1 g5g1}" },
				{ "4r2k/4R1pp/7N/p6n/qp6/6QP/5PPK/8 w - - 0 1", "{g3b3 g7g6 b3b2 h5f6 b2f6}" },
				{ "8/5p1k/3p2q1/3Pp3/4Pn1r/R4Qb1/1P5B/5B1K b - - 0 1", "{h4h2 h1g1 g3f2 g1h2 g6g1}" },
				{ "3r1n2/3q1Q2/pp2p1pk/4P3/3PN2b/P7/1B4K1/5R2 w - - 1 0", "{b2c1 g6g5 f1f6 f8g6 f7g6}" },
		};
		
		for (int t = 0; t < tests.length; ++t) {
			searcher.getBrd().loadFromFEN(tests[t][0]);
			long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
			System.out.println(searcher.getPrincipalVariation().toString());
			assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
			assertTrue(SearchOutcome.isCheckmate(outcome));
		}
	}
	
	/**
	 * same test set as testMateIn3 bun with increased max depth. What we want to verify is that the solver returns the shortest path to a solution.
	 */
	@Test
	void testMateIn3WithUnderestimatedDepth() {
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(new Evaluator(Evaluator.Builder.newInstance()));
		searcher.setFullDepthSearchLimit(7);

		String[][] tests = {
				{ "2r1k3/1b3ppp/p3p3/1p1n4/4q3/PQ2P1P1/1P2BP1P/5RK1 b - - 0 1", "{e4g2 g1g2 d5f4 g2g1 f4h3}" },
				{ "r1q2r1k/p4b2/1p2pP2/3p3p/1P1B1P2/2PB4/P3Q2P/6RK w - - 0 1", "{e2h5 f7h5 f6f7 e6e5 d4e5}" },
				{ "1k1r4/ppq2pp1/4p3/1PP5/Q5n1/7r/P4PB1/R1R3K1 b - - 0 1", "{h3h1 g2h1 c7h2 g1f1 h2f2}" },
				{ "1B6/NK6/3p4/p2R4/Pk6/1P1P4/B7/8 w - - 0 1", "{d5c5 d6d5 c5c2 d5d4 b8d6}" }, // has two solutions! {d5c5 d6d5 c5c2 b4a3 b8d6 } and {d5c5 d6c5 a7b5 c5c4 b8d6 } 
				{ "6B1/3N4/2qpN3/P2k4/4pQP1/8/P1P3nK/2b5 w - - 0 1", "{f4e3 g2e3 e6f4 d5d4 f4e2}" },
				{ "7b/8/r7/3B4/8/8/8/1kBK2Q1 w - - 0 1", "{d1e2 a6a1 d5e4 b1a2 g1g8}" },
				{ "8/1pkn4/p1p4K/7b/P6P/5p2/6r1/5R2 b - - 0 1", "{d7f6 f1g1 g2g1 a4a5 g1g6}" },
				{ "6k1/5ppp/Q1b3q1/2P5/PP6/5NB1/3rr1PP/5RK1 b - - 0 1", "{e2g2 g1h1 g2h2 g3h2 g6g2}" },
				{ "1bN5/2p2p1Q/4PB2/3k3P/8/P4N1K/P7/n4R2 w - - 0 1", "{a3a4 c7c6 h7d3 d5c5 d3d4}" },
				{ "3r3k/p5bp/2p3p1/2q1n3/PQ4P1/2P2pBP/3r1P2/2R1KBR1 b - - 0 1", "{c5e3 f2e3 f3f2 g3f2 e5f3}" },
				{ "1r5k/R6p/4pN2/8/3np3/2r3P1/P4KP1/8 b - - 0 1", "{b8b2 f2g1 d4f3 g2f3 c3c1}" },
				{ "1r5r/NRpk1ppp/p2bp3/7b/3P1P2/P1N4P/2q2PB1/4QRK1 w - - 1 0", "{g2c6 d7e7 c3d5 e7d8 b7b8}" },
				{ "4r2k/p3NR1p/3pb1pB/1p6/2rbP3/8/PPP5/2K4R w - - 1 0", "{f7h7 h8h7 h6f8 e6h3 h1h3}" },
				{ "2q1rbk1/8/4np1B/p2pPBp1/bp1P2P1/5NK1/5P2/1Q5R w - - 1 0", "{f5e6 e8e6 b1g6 f8g7 g6g7}" },
				{ "4r1k1/3n1ppp/4r3/3n3q/Q2P4/5P2/PP2BP1P/R1B1R1K1 b - - 0 1", "{e6g6 c1g5 h5g5 g1f1 g5g1}" },
				{ "4r2k/4R1pp/7N/p6n/qp6/6QP/5PPK/8 w - - 0 1", "{g3b3 g7g6 b3b2 h5f6 b2f6}" },
				{ "8/5p1k/3p2q1/3Pp3/4Pn1r/R4Qb1/1P5B/5B1K b - - 0 1", "{h4h2 h1g1 g3f2 g1h2 g6g1}" },
				{ "3r1n2/3q1Q2/pp2p1pk/4P3/3PN2b/P7/1B4K1/5R2 w - - 1 0", "{b2c1 g6g5 f1f6 f8g6 f7g6}" },
		};
		
		for (int t = 0; t < tests.length; ++t) {
			searcher.getBrd().loadFromFEN(tests[t][0]);
			long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
			System.out.println(searcher.getPrincipalVariation().toString());
			assertTrue(SearchOutcome.isCheckmate(outcome));
			assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
		}
	}
	
	@Test
	void testMateWithIterativeDeepening() {
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(new Evaluator(Evaluator.Builder.newInstance()));

		String[][] tests = {
				{ "2r1k3/1b3ppp/p3p3/1p1n4/4q3/PQ2P1P1/1P2BP1P/5RK1 b - - 0 1", "{e4g2 g1g2 d5f4 g2g1 f4h3}" },
				{ "r1q2r1k/p4b2/1p2pP2/3p3p/1P1B1P2/2PB4/P3Q2P/6RK w - - 0 1", "{e2h5 f7h5 f6f7 e6e5 d4e5}" },
				{ "2q1nk1r/4Rp2/1ppp1P2/6Pp/3p1B2/3P3P/PPP1Q3/6K1 w - - 0 1", "{e7e8 c8e8 f4d6 e8e7 e2e7 f8g8 g5g6 h5h4 e7d8}" },//mate in 5
				
				
				{ "4r3/7q/nb2prRp/pk1p3P/3P4/P7/1P2N1P1/1K1B1N2 w - - 0 1", "{d1a4 b5c4 b2b3 c4d3 a4b5 d3e4 g6g4 f6f4 g4f4}" },
		};
		
		
		for (int t = 0; t < tests.length; t+=1) {
			for(int moves=0; ; moves+=1) {
				searcher.getBrd().loadFromFEN(tests[t][0]);
				searcher.setFullDepthSearchLimit(moves);
				long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
				System.out.println("Trying depth: " + moves);
				if(SearchOutcome.isCheckmate(outcome)) {
					System.out.println(searcher.getPrincipalVariation().toString());
					assertTrue(SearchOutcome.isCheckmate(outcome));
					assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
					break;
				}
				
			}
			
		}
	}
	
	@Test
	void testDraw() {
		AlphaBetaSearcher searcher = new AlphaBetaSearcher(new Evaluator(
				Evaluator.Builder.newInstance()
				.setDrawAsVictoryForMaximixingPlayer()
				.setEvaluator((mysearcher) -> {
					
					int ret = 0;
					int player = Player.WHITE;
					ret+=100*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.PAWN));
					ret+=300*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.KNIGHT));
					ret+=300*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.BISHOP));
					ret+=500*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.ROOK));
					ret+=900*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.QUEEN));
					player = Player.BLACK;
					ret-=100*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.PAWN));
					ret-=300*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.KNIGHT));
					ret-=300*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.BISHOP));
					ret-=500*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.ROOK));
					ret-=900*Bitboard.popcount(mysearcher.getBrd().getPieces(player, PieceType.QUEEN));
					ret= mysearcher.getBrd().getPlayerToMove() == Player.WHITE ? ret : -ret;
					
					//player who is behind on material is considered to be losing and thus favors the stalemate.
					if(ret > 0 )
						return SearchOutcome.WIN - mysearcher.getDepth();
					else if(ret<0)
						return SearchOutcome.LOSS + mysearcher.getDepth();
					else
						return 0;
				})//setEvaluator
		));
		searcher.setFullDepthSearchLimit(6);
		String[][] tests = {
				//side-to-move needs to be actually down on material in order to prioritize the stalemate
				{ "5brq/4p1pn/4PpP1/1K3Pp1/6p1/1Q6/8/7k w - - 0 1", "{b3g3}" },
				{ "K7/8/6k1/7p/6pP/5pPR/5PRQ/1q4BN b - - 0 1", "{b1b6}" },
				{ "8/8/8/5k2/7K/2R2n2/8/5q2 w - - 0 1", "{c3f3 f1f3}" },// odd case where the stalemate is achieved in an even number of moves. (the last one is forced!)
				{ "8/8/8/5K2/7k/2r2N2/8/5Q2 b - - 0 1", "{c3f3 f1f3}" },

		};
		
		for (int t = 0; t < tests.length; ++t) {
			searcher.getBrd().loadFromFEN(tests[t][0]);
			long outcome = searcher.doSearch(SearchOutcome.createLowerBound(SearchOutcome.LOSS), SearchOutcome.createUpperBound(SearchOutcome.WIN));
			System.out.println("final PV: " + searcher.getPrincipalVariation().toString());
			System.out.println("final outcome: " + SearchOutcome.outcomeToString(outcome, true));
			assertTrue(SearchOutcome.isStalemate(outcome));
			assertEquals(tests[t][1], searcher.getPrincipalVariation().toString());
			
		}
	}
}
