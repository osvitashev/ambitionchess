package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

class BasicStaticExchangeEvaluatorTest {
	
	void helper_test_getLeastValuableAttacker_mask(long expected, String fen, int sq_target, int player, long clearedLocationsMask) {
		assertEquals(expected, BasicStaticExchangeEvaluator.getLeastValuableAttacker_mask(new Gamestate(fen),
				sq_target,
				player, clearedLocationsMask));
	}

	@Test
	void getLeastValuableAttacker_mask_test() {
		helper_test_getLeastValuableAttacker_mask(0L, "8/4k3/8/8/8/2K5/8/8 w - - 0 1", Square.E5, Player.WHITE, 0L);
		
		//pawns: 0, 1, 2 attackers
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E3),//expected
				"8/4k3/8/6p1/8/1K2P3/8/8 w - - 0 1",
				Square.D4,//target
				Player.WHITE,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.H4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				0L//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G5),//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/4k3/8/4p1p1/8/1K2P3/8/8 w - - 0 1",
				Square.F4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("e5", "g5")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		//knights
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.G4),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares()//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0L,//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("g4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.F5),//expected
				"8/k7/7P/3n1n2/3p2N1/K3P3/8/8 w - - 0 1",
				Square.E3,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("d4", "d5")//cleared locations
		);
		//bishops
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.A4),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.E8),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				0l,//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.B5,//target
				Player.WHITE,
				Bitboard.initFromAlgebraicSquares("a7", "a4", "d7", "e8")//cleared locations
		);
		
		helper_test_getLeastValuableAttacker_mask(
				Bitboard.initFromSquare(Square.D7),//expected
				"4B3/nk1b4/8/1P4r1/B7/8/1KR5/7q w - - 0 1",
				Square.A4,//target
				Player.BLACK,
				Bitboard.initFromAlgebraicSquares("a7", "b5", "e8")//cleared locations
		);
		
		
		//test for priority override: knight should take precedence over bishop and so on.
	}

}
