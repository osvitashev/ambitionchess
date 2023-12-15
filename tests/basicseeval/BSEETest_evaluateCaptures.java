package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class BSEETest_evaluateCaptures {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	void assertCaptures(String fen, long [][] winning_targets, long [][] neutral_targets, long [][] losing_targets) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateCaptures();
		assertEquals(test_eval.getAttackedTargets(Player.WHITE) & test_game.getPlayerPieces(Player.BLACK),
				test_eval.getOutput_target_isExchangeProcessed(Player.WHITE));
		assertEquals(test_eval.getAttackedTargets(Player.BLACK) & test_game.getPlayerPieces(Player.WHITE),
				test_eval.getOutput_target_isExchangeProcessed(Player.BLACK));

		long expected;
		for (int player : Player.PLAYERS) {
			for (int attacker_type : PieceType.PIECE_TYPES) {
				expected=0;
				for(int i=0; i<winning_targets.length; ++i) {
					if(winning_targets[i][0] == player && winning_targets[i][1]==attacker_type) {
						expected=winning_targets[i][2];
						break;
					}
				}
				if(expected != test_eval.getOutput_capture_winning(player, attacker_type)) {
					assertEquals("", "Winning Captures Failing  [" + Player.toString(player) + " " + PieceType.toString(attacker_type) + "]: expected: " + expected
							+ ", actual: " + test_eval.getOutput_capture_winning(player, attacker_type));
				}
				
				expected=0;
				for(int i=0; i<neutral_targets.length; ++i) {
					if(neutral_targets[i][0] == player && neutral_targets[i][1]==attacker_type) {
						expected=neutral_targets[i][2];
						break;
					}
				}
				if(expected != test_eval.getOutput_capture_neutral(player, attacker_type)) {
					assertEquals("", "Neutral Captures Failing [" + Player.toString(player) + " " + PieceType.toString(attacker_type) + "]: expected: " + expected
							+ ", actual: " + test_eval.getOutput_capture_neutral(player, attacker_type));
				}
				
				expected=0;
				for(int i=0; i<losing_targets.length; ++i) {
					if(losing_targets[i][0] == player && losing_targets[i][1]==attacker_type) {
						expected=losing_targets[i][2];
						break;
					}
				}
				if(expected != test_eval.getOutput_capture_losing(player, attacker_type)) {
					assertEquals("", "Losing Captures Failing [" + Player.toString(player) + " " + PieceType.toString(attacker_type) + "]: expected: " + expected
							+ ", actual: " + test_eval.getOutput_capture_losing(player, attacker_type));
				}
				
				
			}
		}
	}
	
	@Test
	void getOutput_capture_withPieceType_test() {
		assertCaptures("2r5/1k6/6n1/8/8/P3Q3/1K5b/8 w - - 0 1", new long[][] {}, new long[][] {}, new long[][] {});
		
		assertCaptures("8/1k6/8/8/3P4/1n6/1K6/8 w - - 0 1",
				new long[][] {/* winning */
					{ Player.WHITE, PieceType.KING, 0x20000l },
					{ Player.BLACK, PieceType.KNIGHT, 0x8000000l },
				},
				new long[][] {/* neutral */},
				new long[][] {/* losing */}
		);//assertWinningCaptures
		
		assertCaptures("2r5/2r5/4kn2/5r1p/6P1/3K4/2Q5/8 w - - 0 1",
				new long[][] {/* winning */
					{ Player.WHITE, PieceType.PAWN, Bitboard.initFromAlgebraicSquares("f5") },
					{ Player.BLACK, PieceType.PAWN, Bitboard.initFromAlgebraicSquares("g4") },
					{ Player.BLACK, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("c2") },
					{ Player.BLACK, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("g4") },
				},
				new long[][] {/* neutral */
					{ Player.WHITE, PieceType.PAWN, Bitboard.initFromAlgebraicSquares("h5") },
				},
				new long[][] {/* losing */
					{ Player.WHITE, PieceType.QUEEN, Bitboard.initFromAlgebraicSquares("c7") },
				}
		);//assertWinningCaptures
		
		assertCaptures("8/4k1p1/4Pn2/R3b3/8/N7/b3R1K1/1n2q3 w - - 0 1",
				new long[][] {/* winning */
					{ Player.WHITE, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("a2", "e5", "e1") },
					{ Player.BLACK, PieceType.QUEEN, Bitboard.initFromAlgebraicSquares("e2", "a5") },
					{ Player.BLACK, PieceType.BISHOP, Bitboard.initFromAlgebraicSquares("e6") },
					{ Player.BLACK, PieceType.KING, Bitboard.initFromAlgebraicSquares("e6") },
				},
				new long[][] {/* neutral */
					{ Player.WHITE, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("b1") },
					{ Player.BLACK, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("a3") },
				},
				new long[][] {/* losing */
				}
		);//assertWinningCaptures
		
		assertCaptures("8/q2rN2p/6p1/4pR1b/R2RkN2/P1Q5/1P2n1K1/6b1 b - - 0 1",
				new long[][] {/* winning */
					{ Player.WHITE, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("h5") },
					{ Player.WHITE, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("a7", "e4") },
					{ Player.BLACK, PieceType.PAWN, Bitboard.initFromAlgebraicSquares("f5", "d4", "f4") },
					{ Player.BLACK, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("c3", "d4", "f4") },
					{ Player.BLACK, PieceType.BISHOP, Bitboard.initFromAlgebraicSquares("d4") },
					{ Player.BLACK, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("d4", "e7") },
					{ Player.BLACK, PieceType.QUEEN, Bitboard.initFromAlgebraicSquares("d4") },
				},
				new long[][] {/* neutral */
					{ Player.WHITE, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("e2") },
					{ Player.WHITE, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("d7") },
				},
				new long[][] {/* losing */
					{ Player.WHITE, PieceType.KNIGHT, Bitboard.initFromAlgebraicSquares("g6") },
					{ Player.WHITE, PieceType.ROOK, Bitboard.initFromAlgebraicSquares("e5", "h5") },
					{ Player.WHITE, PieceType.KING, Bitboard.initFromAlgebraicSquares("g1") },
					{ Player.BLACK, PieceType.QUEEN, Bitboard.initFromAlgebraicSquares("a4") },
					{ Player.BLACK, PieceType.KING, Bitboard.initFromAlgebraicSquares("d4", "f4", "f5") },
				}
		);//assertWinningCaptures
	}

}
