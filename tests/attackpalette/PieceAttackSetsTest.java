package attackpalette;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Board;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class PieceAttackSetsTest {

	@Test
	void testBishopAttacks() {
		BishopAttackSet aset = new BishopAttackSet();
		aset.populateAttacks(new Board("8/1k6/8/8/8/3B4/6K1/8 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0x80412214001422L, aset.getBishopSet());
		aset.populateAttacks(new Board("8/1k6/8/2P1P3/3B4/2P1P3/6K1/8 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x1400140000L, aset.getBishopSet());
		assertEquals(0x220000000000L, aset.getBishopPawnSet());
		aset.populateAttacks(new Board("8/1k6/1P3P2/8/3B4/8/1P3PK1/8 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x221400142200L, aset.getBishopSet());
		assertEquals(0x41000000000000L, aset.getBishopPawnSet());
		aset.populateAttacks(new Board("6k1/6P1/1P3Q2/2Q5/3B4/2Q4K/5Q2/8 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x201400142000L, aset.getBishopSet());
		assertEquals(0x40020000000241L, aset.getBishopQueenSet());
		assertEquals(0x8001000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/6P1/1P3Q2/2Q5/3B4/2Q4K/5Q2/8 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x201400142000L, aset.getBishopSet());
		assertEquals(0x40020000000241L, aset.getBishopQueenSet());
		assertEquals(0x8001000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/P7/1Q3P2/4Q3/3B4/2Q1Q2K/1P3P2/8 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x21400140000L, aset.getBishopSet());
		assertEquals(0x1200000002200L, aset.getBishopQueenSet());
		assertEquals(0x40000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("8/1k6/5Q2/8/3Q4/8/1B2K3/8 w - - 0 1"), Square.B2, Player.WHITE);
		assertEquals(0x8040000000000000L, aset.getBishopQueenQueenSet());
		aset.populateAttacks(new Board("8/1k6/8/4Q3/8/2Q5/1B2K3/8 w - - 0 1"), Square.B2, Player.WHITE);
		assertEquals(0x8040200000000000L, aset.getBishopQueenQueenSet());
		aset.populateAttacks(new Board("8/1k6/8/4Q3/8/8/1B2K3/Q7 w - - 0 1"), Square.B2, Player.WHITE);
		assertEquals(0L, aset.getBishopQueenQueenSet());
		aset.populateAttacks(new Board("8/1k6/5P2/8/3B4/8/1P6/5K2 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x40000000000000L, aset.getBishopPawnSet());
		aset.populateAttacks(new Board("8/1k6/8/4P3/3B4/2P5/8/5K2 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x200000000000L, aset.getBishopPawnSet());
		aset.populateAttacks(new Board("8/1k4P1/8/8/3B4/2P5/8/5K2 w - - 0 1"), Square.D4, Player.WHITE);
		assertEquals(0x8000000000000000L, aset.getBishopPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1P6/6P1/3Q4/8/5B2/5K2 w - - 0 1"), Square.F2, Player.WHITE);
		assertEquals(0x1000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1P6/2Q3P1/8/8/5B2/5K2 w - - 0 1"), Square.F2, Player.WHITE);
		assertEquals(0x1000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1P6/6P1/8/4Q3/5B2/5K2 w - - 0 1"), Square.F2, Player.WHITE);
		assertEquals(0x1000000000000L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1B6/6P1/3Q4/8/5P2/5K2 w - - 0 1"), Square.B6, Player.WHITE);
		assertEquals(0L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1B6/6P1/8/4Q3/5P2/5K2 w - - 0 1"), Square.B6, Player.WHITE);
		assertEquals(0L, aset.getBishopQueenPawnSet());
		aset.populateAttacks(new Board("6k1/5pp1/1B6/2Q3P1/3P4/8/8/5K2 w - - 0 1"), Square.B6, Player.WHITE);
		assertEquals(0L, aset.getBishopQueenPawnSet());	
		
		aset.populateAttacks(new Board("1k6/8/8/8/8/2b5/1p6/1K6 w - - 0 1"), Square.C3, Player.BLACK);
		assertEquals(0x804020110a000a10L, aset.getBishopSet());
		assertEquals(0x1L, aset.getBishopPawnSet());
		
		aset.populateAttacks(new Board("bk6/8/2q2pP1/5Q2/8/1Pb5/1pB1pQp1/1K2B3 w - - 0 1"), Square.A8, Player.BLACK);
		assertEquals(0x2040000000000L, aset.getBishopSet());
		assertEquals(0x0L, aset.getBishopPawnSet());
		assertEquals(0x810204000L, aset.getBishopQueenSet());
		assertEquals(0x80L, aset.getBishopQueenPawnSet());
	}
	
	@Test
	void testRookAttacks() {
		RookAttackSet aset = new RookAttackSet();
		aset.populateAttacks(new Board("6k1/5pp1/8/3R4/8/3R1RQ1/8/5K2 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0x808370808L, aset.getRookSet());		
		assertEquals(0x808080000400000L, aset.getRookRookSet());
		assertEquals(0x800000L, aset.getRookRookQueenSet());
		aset.populateAttacks(new Board("6k1/3Q1pp1/8/8/3R4/2NRR3/3K4/8 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0x8140800L, aset.getRookSet());		
		assertEquals(0x8080800e00000L, aset.getRookRookSet());
		assertEquals(0x800000000000000L, aset.getRookRookQueenSet());
		aset.populateAttacks(new Board("6k1/3Q1pp1/8/8/8/1Q1R1N2/8/3B1K2 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0x8080808360808L, aset.getRookSet());		
		assertEquals(0L, aset.getRookRookSet());
		assertEquals(0L, aset.getRookRookQueenSet());
		aset.populateAttacks(new Board("6k1/5pp1/3R4/8/3Q4/3R2Q1/8/5K2 w - - 0 1"), Square.D3, Player.WHITE);	
		assertEquals(0x80800800000L, aset.getRookQueenSet());
		assertEquals(0L, aset.getRookQueenQueenSet());
		assertEquals(0x808000000000000L, aset.getRookQueenRookSet());
		aset.populateAttacks(new Board("3R1nk1/5pp1/3Q4/8/8/3RQQ2/8/5K2 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0x808000000200000L, aset.getRookQueenSet());
		assertEquals(0xc00000L, aset.getRookQueenQueenSet());
		assertEquals(0L, aset.getRookQueenRookSet());
		aset.populateAttacks(new Board("5nk1/5pp1/8/8/8/3R4/8/5K2 w - - 0 1"), Square.D3, Player.WHITE);
		assertEquals(0L, aset.getRookQueenSet());
		assertEquals(0L, aset.getRookQueenQueenSet());
		assertEquals(0L, aset.getRookQueenRookSet());
	}
	
	@Test
	void testQueenAttacks() {
		QueenAttackSet aset = new QueenAttackSet();
		//interactions with rooks
		aset.populateAttacks(new Board("7k/6pp/8/2R5/2Q5/8/2Q1R1Q1/5K2 w - - 0 1"), Square.C2, Player.WHITE);
		assertEquals(0x804020150e1b0eL, aset.getQueenSet());
		assertEquals(0x6000L, aset.getQueenRookSet());
		assertEquals(0x8000L, aset.getQueenRookQueenSet());
		assertEquals(0x404040000000000L, aset.getQueenQueenRookSet());
		aset.populateAttacks(new Board("7k/2Q3pp/8/8/8/2R5/2Q1QR2/5K2 w - - 0 1"), Square.C2, Player.WHITE);
		assertEquals(0x804020110e1b0eL, aset.getQueenSet());
		assertEquals(0x4040404000000L, aset.getQueenRookSet());
		assertEquals(0x400000000000000L, aset.getQueenRookQueenSet());
		assertEquals(0xc000L, aset.getQueenQueenRookSet());
		aset.populateAttacks(new Board("7k/6pp/2N5/2r5/R7/1B1Q1R2/2Q3r1/5K2 w - - 0 1"), Square.C2, Player.WHITE);
		assertEquals(0L, aset.getQueenRookSet());
		assertEquals(0L, aset.getQueenRookQueenSet());
		assertEquals(0L, aset.getQueenQueenRookSet());
		// interaction of two queens
		aset.populateAttacks(new Board("7k/3R2pp/8/2Q2Q2/7R/1Q6/2Q2Q2/2K5 w - - 0 1"), Square.C2, Player.WHITE);
		assertEquals(0x48444000100c000L, aset.getQueenQueenSet());
		//queen and pawns
		aset.populateAttacks(new Board("7k/6pp/8/8/3P1P2/4Q3/3P1P2/6K1 w - - 0 1"), Square.E3, Player.WHITE);
		assertEquals(0x4400000000L, aset.getQueenPawnSet());
		assertEquals(0L, aset.getQueenQueenPawnSet());
		aset.populateAttacks(new Board("7k/6pp/2P3P1/8/4Q3/8/2P3P1/6K1 w - - 0 1"), Square.E4, Player.WHITE);
		assertEquals(0x82000000000000L, aset.getQueenPawnSet());
		assertEquals(0L, aset.getQueenQueenPawnSet());
		aset.populateAttacks(new Board("7k/6pp/8/1P6/2Q1P1P1/5Q2/P3Q1P1/6K1 w - - 0 1"), Square.E2, Player.WHITE);
		assertEquals(0L, aset.getQueenPawnSet());
		assertEquals(0x18000000000L, aset.getQueenQueenPawnSet());
		aset.populateAttacks(new Board("7k/6pp/8/1P5P/4P3/3Q1Q2/P3Q1P1/6K1 w - - 0 1"), Square.E2, Player.WHITE);
		assertEquals(0L, aset.getQueenPawnSet());
		assertEquals(0x10000000000L, aset.getQueenQueenPawnSet());
		aset.populateAttacks(new Board("7k/4Q1pp/8/2Q3Q1/7P/P7/8/6K1 w - - 0 1"), Square.E7, Player.WHITE);
		assertEquals(0L, aset.getQueenPawnSet());
		assertEquals(0L, aset.getQueenQueenPawnSet());
		//interaction with bishop and pawns
		aset.populateAttacks(new Board("7k/B2P2pp/2B5/8/Q3B3/1B6/2P5/B5K1 w - - 0 1"), Square.A4, Player.WHITE);
		assertEquals(0x8000000000400L, aset.getQueenBishopSet());
		assertEquals(0x1000000000000000L, aset.getQueenBishopPawnSet());
		aset.populateAttacks(new Board("7k/5Ppp/6B1/7Q/6B1/8/4P3/6K1 w - - 0 1"), Square.H5, Player.WHITE);
		assertEquals(0x20000000201000L, aset.getQueenBishopSet());
		assertEquals(0x1000000000000000L, aset.getQueenBishopPawnSet());
		//QRR
		aset.populateAttacks(new Board("7k/6pp/8/8/8/1Q1R1R2/8/6K1 w - - 0 1"), Square.B3, Player.WHITE);
		assertEquals(0x4222120a070d070aL, aset.getQueenSet());
		assertEquals(0x300000L, aset.getQueenRookSet());
		assertEquals(0xc00000L, aset.getQueenRookRookSet());
	}
	
	@Test
	void testKingAttacks() {
		KingAttackSet aset = new KingAttackSet();
		aset.populateAttacks(new Board("7k/5Ppp/6B1/7Q/6B1/8/4P3/6K1 w - - 0 1"), Square.G1, Player.WHITE);
		assertEquals(0xe0a0L, aset.getKingSet());
	}
	
	@Test
	void testKnightAttacks() {
		KnightAttackSet aset = new KnightAttackSet();
		aset.populateAttacks(new Board("7k/5Ppp/6B1/7Q/N5B1/8/4P3/6K1 w - - 0 1"), Square.A4, Player.WHITE);
		assertEquals(0x20400040200L, aset.getKnightSet());
	}
	
	@Test
	void testPawnsAttackSet() {
		PawnsAttackSet aset = new PawnsAttackSet();
		aset.populateAttacks(new Board("7k/5Ppp/6B1/7Q/N5BP/P7/4P3/6K1 w - - 0 1"), Player.WHITE);
		assertEquals(0x4000000002200000L, aset.getPawnEast());
		assertEquals(0x1000004000080000L, aset.getPawnWest());
	}

}
