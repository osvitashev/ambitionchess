package basicseeval;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Gamestate;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;

class BSEETest_evaluateQuietMoves {
	private Gamestate test_game = new Gamestate();
	private BasicStaticExchangeEvaluator test_eval = new BasicStaticExchangeEvaluator(test_game, 1);
	
	void assertTargets(String fen,long [][] neutral_targets, long [][] losing_targets) {
		test_game.loadFromFEN(fen);
		test_eval.initialize();
		test_eval.evaluateQuietMoves();
		long expected;
		//todo: there is currently no check on getOutput_target_isExchangeProcessed
		
		for (int player : Player.PLAYERS) {
			for (int attacker_type : PieceType.PIECE_TYPES) {
				expected=0;
				for(int i=0; i<neutral_targets.length; ++i) {
					if(neutral_targets[i][0] == player && neutral_targets[i][1]==attacker_type) {
						expected=neutral_targets[i][2];
						break;
					}
				}
				if(expected != test_eval.getOutput_quiet_neutral(player, attacker_type)) {
					assertEquals("", "Neutral Targets Failing [" + Player.toString(player) + " " + PieceType.toString(attacker_type) + "]: expected: " + expected
							+ ", actual: " + test_eval.getOutput_quiet_neutral(player, attacker_type));
				}
				
				expected=0;
				for(int i=0; i<losing_targets.length; ++i) {
					if(losing_targets[i][0] == player && losing_targets[i][1]==attacker_type) {
						expected=losing_targets[i][2];
						break;
					}
				}
				if(expected != test_eval.getOutput_quiet_losing(player, attacker_type)) {
					assertEquals("", "Losing Targets Failing [" + Player.toString(player) + " " + PieceType.toString(attacker_type) + "]: expected: " + expected
							+ ", actual: " + test_eval.getOutput_quiet_losing(player, attacker_type));
				}
			}
		}
	}
	
	@Test
	void getOutput_quiet_withPieceType_test() {
		//fail("this is wrong!!!");
		assertTargets("5r2/2nq1rbp/1R4p1/8/PPk5/2P3K1/1B1n2Q1/8 w - - 0 1",
				new long[][] {/* neutral */
					{ Player.WHITE, PieceType.PAWN, 0x300000000l },
					{ Player.WHITE, PieceType.ROOK, 0x2040000000000l },
					{ Player.WHITE, PieceType.BISHOP, 0x10005l },
					{ Player.WHITE, PieceType.QUEEN, 0x20400008090c0l },
					{ Player.WHITE, PieceType.KING, 0x80008000l },
					{ Player.BLACK, PieceType.PAWN, 0x80c000000000l },
					{ Player.BLACK, PieceType.ROOK, 0xdd10202020200020l },
					{ Player.BLACK, PieceType.KNIGHT, 0x1100100800220022l },
					{ Player.BLACK, PieceType.BISHOP, 0x8000a01000000000l },
					{ Player.BLACK, PieceType.QUEEN, 0x1c10002800080000l },
					{ Player.BLACK, PieceType.KING, 0xa0000l },
				},
				new long[][] {/* losing */
					{ Player.WHITE, PieceType.ROOK, 0x200390200000000l },
					{ Player.WHITE, PieceType.QUEEN, 0x100000810202020l },
					{ Player.WHITE, PieceType.KING, 0x60a02000l },
					{ Player.BLACK, PieceType.ROOK, 0x200000000002000l },
					{ Player.BLACK, PieceType.KNIGHT, 0x10210000000l },
					{ Player.BLACK, PieceType.BISHOP, 0x8000000l },
					{ Player.BLACK, PieceType.QUEEN, 0x1c0248800000l },
					{ Player.BLACK, PieceType.KING, 0xe08000000l },
				}
		);
		
//		assertCaptures("8/1k6/8/8/3P4/1n6/1K6/8 w - - 0 1",
//				new long[][] {/* winning */
//					{ Player.WHITE, PieceType.KING, 0x20000l },
//					{ Player.BLACK, PieceType.KNIGHT, 0x8000000l },
//				},
//				new long[][] {/* neutral */},
//				new long[][] {/* losing */}
//		);//assertWinningCaptures
		

	}

}
