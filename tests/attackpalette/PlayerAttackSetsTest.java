package attackpalette;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import attackpalette.PlayerAttackSet.SetType;
import gamestate.Board;
import gamestate.GlobalConstants.Player;

class PlayerAttackSetsTest {

	@Test
	void testValueInitiation() {
		PlayerAttackSet pas = new PlayerAttackSet();
		// king
		pas.initialize(new Board("8/6pk/b5pp/8/2n5/6PP/2R3PK/8 w - - 0 1"), Player.WHITE);
		assertEquals(0xc040c0L, pas.get(SetType.KingSet));
		// knights
		pas.initialize(new Board("8/5npk/b5pp/8/8/6PP/2R3PK/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.KnightSet0));
		assertEquals(0L, pas.get(SetType.KnightSet1));
		pas.initialize(new Board("8/5npk/b5pp/8/3N4/6PP/2R3PK/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x142200221400L, pas.get(SetType.KnightSet0));
		assertEquals(0L, pas.get(SetType.KnightSet1));
		pas.initialize(new Board("8/5npk/b5pp/8/3N4/6PP/2R3PK/4N3 w - - 0 1"), Player.WHITE);
		assertEquals(0x142200221400L, pas.get(SetType.KnightSet0));
		assertEquals(0x284400L, pas.get(SetType.KnightSet1));
		pas.initialize(new Board("N7/5npk/b5pp/8/3N4/6PP/2R3PK/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x4020000000000L, pas.get(SetType.KnightSet0));
		assertEquals(0x142200221400L, pas.get(SetType.KnightSet1));
		// pawns
		pas.initialize(new Board("N7/5npk/b5pp/8/P2N4/6PP/2RP2PK/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x280900000L, pas.get(SetType.PawnSetEast));
		assertEquals(0x60240000L, pas.get(SetType.PawnSetWest));
		// bishop
		pas.initialize(new Board("5k2/8/5N2/8/3B4/7K/1R6/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x1221400142240L, pas.get(SetType.BishopSet0));
		pas.initialize(new Board("5k2/8/5P2/2P5/3B4/4P2K/1P6/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x201400140200L, pas.get(SetType.BishopSet0));
		assertEquals(0x40020000000000L, pas.get(SetType.BishopPawnSet0));
		pas.initialize(new Board("5k2/8/1P6/4P3/3B4/2P4K/5P2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x21400142000L, pas.get(SetType.BishopSet0));
		assertEquals(0x1200000000000L, pas.get(SetType.BishopPawnSet0));
		pas.initialize(new Board("5k2/8/2p3p1/8/4B3/7K/2p3p1/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x442800284400L, pas.get(SetType.BishopSet0));
		assertEquals(0L, pas.get(SetType.BishopPawnSet0));
		pas.initialize(new Board("5k2/3n4/4Q3/5B1Q/8/K2Q4/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x80500050880000L, pas.get(SetType.BishopSet0));
		assertEquals(0x8000000000402L, pas.get(SetType.BishopQueenSet0));
		pas.initialize(new Board("8/7k/5P2/4Q3/1P5p/2B5/3Q3K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x40000000000000L, pas.get(SetType.BishopQueenPawnSet0));
		pas.initialize(new Board("8/6Pk/1P6/2Q1Q3/3B3p/2Q1Q3/1P3P1K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x8001000000000000L, pas.get(SetType.BishopQueenPawnSet0));
		pas.initialize(new Board("8/2Q1P2k/3B4/2Q5/1P3Q1p/8/1P4KP/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.BishopQueenPawnSet0));
		pas.initialize(new Board("7k/8/5K2/2P5/8/4Q3/8/6B1 w - - 0 1"), Player.WHITE);
		assertEquals(0x20000000000L, pas.get(SetType.BishopQueenPawnSet0));
		pas.initialize(new Board("7k/8/5K2/8/3Q4/4Q3/8/6B1 w - - 0 1"), Player.WHITE);
		assertEquals(0x10a000L, pas.get(SetType.BishopSet0));
		assertEquals(0x8000000L, pas.get(SetType.BishopQueenSet0));
		assertEquals(0x1020400000000L, pas.get(SetType.BishopQueenQueenSet0));
		pas.initialize(new Board("7k/8/1Q3K2/8/3Q4/8/8/6B1 w - - 0 1"), Player.WHITE);
		assertEquals(0x810a000L, pas.get(SetType.BishopSet0));
		assertEquals(0x20400000000L, pas.get(SetType.BishopQueenSet0));
		assertEquals(0x1000000000000L, pas.get(SetType.BishopQueenQueenSet0));
		//bishop pair
		pas.initialize(new Board("7k/1P3P1p/2Q2K2/3BQ3/8/2B2Q1R/6P1/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x20140014220100L, pas.get(SetType.BishopSet0));
		assertEquals(0x4000000000000000L, pas.get(SetType.BishopPawnSet0));
		assertEquals(0x2000000004000L, pas.get(SetType.BishopQueenSet0));
		assertEquals(0x100000000000000L, pas.get(SetType.BishopQueenPawnSet0));
		assertEquals(0L, pas.get(SetType.BishopQueenQueenSet0));
		assertEquals(0x110a000a11L, pas.get(SetType.BishopSet1));
		assertEquals(0L, pas.get(SetType.BishopPawnSet1));
		assertEquals(0x200000000000L, pas.get(SetType.BishopQueenSet1));
		assertEquals(0L, pas.get(SetType.BishopQueenPawnSet1));
		assertEquals(0L, pas.get(SetType.BishopQueenQueenSet1));
		pas.initialize(new Board("8/8/1P6/2Q3P1/K7/4Q3/kP6/2B3B1 w - - 0 1"), Player.WHITE);
		assertEquals(0x10a000L, pas.get(SetType.BishopSet0));
		assertEquals(0L, pas.get(SetType.BishopPawnSet0));
		assertEquals(0x408000000L, pas.get(SetType.BishopQueenSet0));
		assertEquals(0L, pas.get(SetType.BishopQueenPawnSet0));
		assertEquals(0x20000000000L, pas.get(SetType.BishopQueenQueenSet0));
		assertEquals(0x100a00L, pas.get(SetType.BishopSet1));
		assertEquals(0x10000L, pas.get(SetType.BishopPawnSet1));
		assertEquals(0x4020000000L, pas.get(SetType.BishopQueenSet1));
		assertEquals(0x800000000000L, pas.get(SetType.BishopQueenPawnSet1));
		assertEquals(0L, pas.get(SetType.BishopQueenQueenSet1));
		//rook
		pas.initialize(new Board("8/5n1k/8/8/8/8/3K1R2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x2020202020d820L, pas.get(SetType.RookSet0));
		pas.initialize(new Board("8/2n4k/8/8/2R5/8/2RK4/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x4L, pas.get(SetType.RookRookSet0));
		pas.initialize(new Board("8/2n4k/8/8/8/2QR1K2/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x30000L, pas.get(SetType.RookQueenSet0));
		pas.initialize(new Board("8/7k/2Q5/2R5/2R5/8/7K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.RookRookQueenSet0));
		assertEquals(0x404000000000000L, pas.get(SetType.RookRookQueenSet1));
		pas.initialize(new Board("8/7k/8/8/8/1RQ2R2/7K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x10000L, pas.get(SetType.RookQueenRookSet0));
		assertEquals(0xc00000L, pas.get(SetType.RookQueenRookSet1));
		pas.initialize(new Board("8/7k/2R5/2Q5/8/R1Q3Q1/7K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x404L, pas.get(SetType.RookQueenQueenSet0));
		assertEquals(0x800000L, pas.get(SetType.RookQueenQueenSet1));
		//rook pair
		pas.initialize(new Board("8/7k/8/1RQ1R3/8/8/4Q2K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x101010ec10101000L, pas.get(SetType.RookSet0));
		assertEquals(0L, pas.get(SetType.RookRookSet0));
		assertEquals(0x200000010L, pas.get(SetType.RookQueenSet0));
		assertEquals(0x100000000L, pas.get(SetType.RookQueenRookSet0));
		assertEquals(0L, pas.get(SetType.RookQueenQueenSet0));
		assertEquals(0x202020502020202L, pas.get(SetType.RookSet1));
		assertEquals(0L, pas.get(SetType.RookRookSet1));
		assertEquals(0x1800000000L, pas.get(SetType.RookQueenSet1));
		assertEquals(0xe000000000L, pas.get(SetType.RookQueenRookSet1));
		assertEquals(0L, pas.get(SetType.RookQueenQueenSet1));
		pas.initialize(new Board("7k/8/8/3R4/8/3Q4/3Q3K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x80808f708080000L, pas.get(SetType.RookSet0));
		assertEquals(0L, pas.get(SetType.RookRookSet0));
		assertEquals(0x800L, pas.get(SetType.RookQueenSet0));
		assertEquals(0L, pas.get(SetType.RookQueenRookSet0));
		assertEquals(0x8L, pas.get(SetType.RookQueenQueenSet0));
		assertEquals(0L, pas.get(SetType.RookSet1));
		assertEquals(0L, pas.get(SetType.RookRookSet1));
		assertEquals(0L, pas.get(SetType.RookQueenSet1));
		assertEquals(0L, pas.get(SetType.RookQueenRookSet1));
		assertEquals(0L, pas.get(SetType.RookQueenQueenSet1));
		pas.initialize(new Board("7k/8/8/3R1R2/8/3Q4/3Q3K/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x202020d820202020L, pas.get(SetType.RookSet0));
		assertEquals(0x700000000L, pas.get(SetType.RookRookSet0));
		assertEquals(0L, pas.get(SetType.RookQueenSet0));
		assertEquals(0L, pas.get(SetType.RookQueenRookSet0));
		assertEquals(0L, pas.get(SetType.RookQueenQueenSet0));
		assertEquals(0x808083708080000L, pas.get(SetType.RookSet1));
		assertEquals(0xc000000000L, pas.get(SetType.RookRookSet1));
		assertEquals(0x800L, pas.get(SetType.RookQueenSet1));
		assertEquals(0L, pas.get(SetType.RookQueenRookSet1));
		assertEquals(0x8L, pas.get(SetType.RookQueenQueenSet1));
		//queen
		pas.initialize(new Board("8/6k1/1Q1K4/1N6/3b4/8/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0xa070d0708000000L, pas.get(SetType.QueenSet0));
		pas.initialize(new Board("8/1P1P1Pk1/8/1P1Q1P2/6K1/1P1P1P2/6N1/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x2a1c361c2a0000L, pas.get(SetType.QueenSet0));
		assertEquals(0x4100000000000000L, pas.get(SetType.QueenPawnSet0));
		pas.initialize(new Board("8/6k1/2PPP3/2PQP3/2PPP1K1/8/6N1/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x1c141c000000L, pas.get(SetType.QueenSet0));
		assertEquals(0x22000000000000L, pas.get(SetType.QueenPawnSet0));
		pas.initialize(new Board("3k4/8/8/4B3/8/2Q3K1/1B6/4B3 w - - 0 1"), Player.WHITE);
		assertEquals(0x40404150e7b0e14L, pas.get(SetType.QueenSet0));
		assertEquals(0x8040200000000001L, pas.get(SetType.QueenBishopSet0));
		assertEquals(0L, pas.get(SetType.QueenBishopPawnSet0));
		pas.initialize(new Board("2k5/7K/1P1P1P2/2B1B3/1B1Q1B1P/2B1B1P1/1P1P1P2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x81c361c0800L, pas.get(SetType.QueenSet0));
		assertEquals(0x220000002200L, pas.get(SetType.QueenBishopSet0));
		assertEquals(0x41000000000000L, pas.get(SetType.QueenBishopPawnSet0));
		pas.initialize(new Board("6k1/2Q5/8/2R5/8/2Q2R2/8/2K5 w - - 0 1"), Player.WHITE);
		assertEquals(0x4040000L, pas.get(SetType.QueenRookSet0));
		assertEquals(0x404L, pas.get(SetType.QueenRookQueenSet0));
		assertEquals(0x4040000c00000L, pas.get(SetType.QueenRookSet1));
		assertEquals(0x400000000000000L, pas.get(SetType.QueenRookQueenSet1));
		pas.initialize(new Board("8/3k4/5Q2/4B3/8/2Q5/5K2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x8040000L, pas.get(SetType.QueenBishopSet0));
		assertEquals(0x201L, pas.get(SetType.QueenBishopQueenSet0));
		assertEquals(0x200000000000L, pas.get(SetType.QueenBishopSet1));
		assertEquals(0x8040000000000000L, pas.get(SetType.QueenBishopQueenSet1));
		
		pas.initialize(new Board("8/8/k2P1P2/4Q3/3Q4/2B5/5K2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.QueenQueenPawnSet0));
		assertEquals(0x201L, pas.get(SetType.QueenQueenBishopSet0));
		assertEquals(0x40000000000000L, pas.get(SetType.QueenQueenPawnSet1));
		assertEquals(0L, pas.get(SetType.QueenQueenBishopSet1));
		
		pas.initialize(new Board("8/8/k2P1P2/4Q3/3Q4/2B5/5K2/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.QueenQueenPawnSet0));
		assertEquals(0x201L, pas.get(SetType.QueenQueenBishopSet0));
		assertEquals(0x40000000000000L, pas.get(SetType.QueenQueenPawnSet1));
		assertEquals(0L, pas.get(SetType.QueenQueenBishopSet1));
		
		pas.initialize(new Board("k7/4P3/1P1B1P2/2P1Q1P1/3P1Q2/1K4P1/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.QueenQueenPawnSet0));
		assertEquals(0L, pas.get(SetType.QueenQueenBishopSet0));
		assertEquals(0L, pas.get(SetType.QueenQueenPawnSet1));
		assertEquals(0x204000000000000L, pas.get(SetType.QueenQueenBishopSet1));
		
		pas.initialize(new Board("6k1/8/5K2/8/8/Q1Q1R3/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0L, pas.get(SetType.QueenQueenRookSet0));
		assertEquals(0xe00000L, pas.get(SetType.QueenQueenRookSet1));
		
		pas.initialize(new Board("5K1k/6pp/2Q5/8/4Q3/8/8/8 w - - 0 1"), Player.WHITE);
		assertEquals(0x150efb0e15040404L, pas.get(SetType.QueenSet0));
		assertEquals(0x204080L, pas.get(SetType.QueenQueenSet0));
		assertEquals(0x10905438ef385492L, pas.get(SetType.QueenSet1));
		assertEquals(0x102000000000000L, pas.get(SetType.QueenQueenSet1));
		


	}
	// when i get to covering natural capture ordering, need to make sure this
	// works:
	// 5Q2/8/8/k1n5/5R2/5n2/5Q2/7K w - - 0 1
}
