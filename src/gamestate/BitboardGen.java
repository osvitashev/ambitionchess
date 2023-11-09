package gamestate;

import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

public class BitboardGen {

	private final static long[] KING_SETS = { 0x0000000000000302L, 0x0000000000000705L, 0x0000000000000e0aL, 0x0000000000001c14L, 0x0000000000003828L,
			0x0000000000007050L, 0x000000000000e0a0L, 0x000000000000c040L, 0x0000000000030203L, 0x0000000000070507L, 0x00000000000e0a0eL, 0x00000000001c141cL,
			0x0000000000382838L, 0x0000000000705070L, 0x0000000000e0a0e0L, 0x0000000000c040c0L, 0x0000000003020300L, 0x0000000007050700L, 0x000000000e0a0e00L,
			0x000000001c141c00L, 0x0000000038283800L, 0x0000000070507000L, 0x00000000e0a0e000L, 0x00000000c040c000L, 0x0000000302030000L, 0x0000000705070000L,
			0x0000000e0a0e0000L, 0x0000001c141c0000L, 0x0000003828380000L, 0x0000007050700000L, 0x000000e0a0e00000L, 0x000000c040c00000L, 0x0000030203000000L,
			0x0000070507000000L, 0x00000e0a0e000000L, 0x00001c141c000000L, 0x0000382838000000L, 0x0000705070000000L, 0x0000e0a0e0000000L, 0x0000c040c0000000L,
			0x0003020300000000L, 0x0007050700000000L, 0x000e0a0e00000000L, 0x001c141c00000000L, 0x0038283800000000L, 0x0070507000000000L, 0x00e0a0e000000000L,
			0x00c040c000000000L, 0x0302030000000000L, 0x0705070000000000L, 0x0e0a0e0000000000L, 0x1c141c0000000000L, 0x3828380000000000L, 0x7050700000000000L,
			0xe0a0e00000000000L, 0xc040c00000000000L, 0x0203000000000000L, 0x0507000000000000L, 0x0a0e000000000000L, 0x141c000000000000L, 0x2838000000000000L,
			0x5070000000000000L, 0xa0e0000000000000L, 0x40c0000000000000L };

	private final static long[] KNIGHT_SETS = { 0x0000000000020400L, 0x0000000000050800L, 0x00000000000a1100L, 0x0000000000142200L, 0x0000000000284400L,
			0x0000000000508800L, 0x0000000000a01000L, 0x0000000000402000L, 0x0000000002040004L, 0x0000000005080008L, 0x000000000a110011L, 0x0000000014220022L,
			0x0000000028440044L, 0x0000000050880088L, 0x00000000a0100010L, 0x0000000040200020L, 0x0000000204000402L, 0x0000000508000805L, 0x0000000a1100110aL,
			0x0000001422002214L, 0x0000002844004428L, 0x0000005088008850L, 0x000000a0100010a0L, 0x0000004020002040L, 0x0000020400040200L, 0x0000050800080500L,
			0x00000a1100110a00L, 0x0000142200221400L, 0x0000284400442800L, 0x0000508800885000L, 0x0000a0100010a000L, 0x0000402000204000L, 0x0002040004020000L,
			0x0005080008050000L, 0x000a1100110a0000L, 0x0014220022140000L, 0x0028440044280000L, 0x0050880088500000L, 0x00a0100010a00000L, 0x0040200020400000L,
			0x0204000402000000L, 0x0508000805000000L, 0x0a1100110a000000L, 0x1422002214000000L, 0x2844004428000000L, 0x5088008850000000L, 0xa0100010a0000000L,
			0x4020002040000000L, 0x0400040200000000L, 0x0800080500000000L, 0x1100110a00000000L, 0x2200221400000000L, 0x4400442800000000L, 0x8800885000000000L,
			0x100010a000000000L, 0x2000204000000000L, 0x0004020000000000L, 0x0008050000000000L, 0x00110a0000000000L, 0x0022140000000000L, 0x0044280000000000L,
			0x0088500000000000L, 0x0010a00000000000L, 0x0020400000000000L };

	private final static long[][] PAWN_SETS = { { 0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L, 0x0000000000001400L, 0x0000000000002800L,
			0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L, 0x0000000000020000L, 0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L,
			0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L, 0x0000000000400000L, 0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L,
			0x0000000014000000L, 0x0000000028000000L, 0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L, 0x0000000200000000L, 0x0000000500000000L,
			0x0000000a00000000L, 0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L, 0x0000020000000000L,
			0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L, 0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L, 0x0000400000000000L,
			0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L, 0x0028000000000000L, 0x0050000000000000L, 0x00a0000000000000L,
			0x0040000000000000L, 0x0200000000000000L, 0x0500000000000000L, 0x0a00000000000000L, 0x1400000000000000L, 0x2800000000000000L, 0x5000000000000000L,
			0xa000000000000000L, 0x4000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
			0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L },
			{ 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L, 0x0000000000000000L,
					0x0000000000000000L, 0x0000000000000002L, 0x0000000000000005L, 0x000000000000000aL, 0x0000000000000014L, 0x0000000000000028L,
					0x0000000000000050L, 0x00000000000000a0L, 0x0000000000000040L, 0x0000000000000200L, 0x0000000000000500L, 0x0000000000000a00L,
					0x0000000000001400L, 0x0000000000002800L, 0x0000000000005000L, 0x000000000000a000L, 0x0000000000004000L, 0x0000000000020000L,
					0x0000000000050000L, 0x00000000000a0000L, 0x0000000000140000L, 0x0000000000280000L, 0x0000000000500000L, 0x0000000000a00000L,
					0x0000000000400000L, 0x0000000002000000L, 0x0000000005000000L, 0x000000000a000000L, 0x0000000014000000L, 0x0000000028000000L,
					0x0000000050000000L, 0x00000000a0000000L, 0x0000000040000000L, 0x0000000200000000L, 0x0000000500000000L, 0x0000000a00000000L,
					0x0000001400000000L, 0x0000002800000000L, 0x0000005000000000L, 0x000000a000000000L, 0x0000004000000000L, 0x0000020000000000L,
					0x0000050000000000L, 0x00000a0000000000L, 0x0000140000000000L, 0x0000280000000000L, 0x0000500000000000L, 0x0000a00000000000L,
					0x0000400000000000L, 0x0002000000000000L, 0x0005000000000000L, 0x000a000000000000L, 0x0014000000000000L, 0x0028000000000000L,
					0x0050000000000000L, 0x00a0000000000000L, 0x0040000000000000L } };

	/**
	 * Generates *diagonal* 'attack set' for a pawn on a given square. Disregards
	 * target square occupancy.
	 * 
	 * @param sq
	 * @return bitboard
	 */
	public static long getPawnAttackSet(int sq, int pl) {
		assert Square.validate(sq);
		assert Player.validate(pl);
		return PAWN_SETS[pl][sq];
	}
	
	/**
	 * Generates *diagonal* 'attack set' for group of pawns. Disregards
	 * target square occupancy.
	 * 
	 * @return bitboard
	 */
	public static long getMultiplePawnAttackSet(long pawns, int pl) {
		assert Player.validate(pl);
		if(Player.isWhite(pl))
			return Bitboard.shiftNorth( Bitboard.shiftEast(pawns)) |
				Bitboard.shiftNorth( Bitboard.shiftWest(pawns));
		else
			return Bitboard.shiftSouth( Bitboard.shiftEast(pawns)) |
				Bitboard.shiftSouth( Bitboard.shiftWest(pawns));
	}

	/**
	 * Generates 'attack set' for a king on a given square. Disregards target
	 * squares occupancy.
	 * 
	 * @param sq
	 * @return bitboard
	 */
	public static long getKingSet(int sq) {
		assert Square.validate(sq);
		return KING_SETS[sq];
	}

	/**
	 * Generates 'attack set' for a knight on a given square. Disregards target
	 * square occupancy.
	 * 
	 * @param sq
	 * @return bitboard
	 */
	public static long getKnightSet(int sq) {
		assert Square.validate(sq);
		return KNIGHT_SETS[sq];
	}

	// taken form import nl.s22k.chess.Util; for now...
	private static class Magic {

		long movementMask;
		long magicNumber;
		int shift;
		long[] magicMoves;

		public Magic(long magicNumber) {
			this.magicNumber = magicNumber;
		}

	}

	// rook-size: 800kb
	// bishop-size: 40kb

	// CONSIDER smaller and more dense tables?
	private static final long[] rookMagicNumbers = { 0xa180022080400230L, 0x40100040022000L, 0x80088020001002L, 0x80080280841000L, 0x4200042010460008L,
			0x4800a0003040080L, 0x400110082041008L, 0x8000a041000880L, 0x10138001a080c010L, 0x804008200480L, 0x10011012000c0L, 0x22004128102200L,
			0x200081201200cL, 0x202a001048460004L, 0x81000100420004L, 0x4000800380004500L, 0x208002904001L, 0x90004040026008L, 0x208808010002001L,
			0x2002020020704940L, 0x8048010008110005L, 0x6820808004002200L, 0xa80040008023011L, 0xb1460000811044L, 0x4204400080008ea0L, 0xb002400180200184L,
			0x2020200080100380L, 0x10080080100080L, 0x2204080080800400L, 0xa40080360080L, 0x2040604002810b1L, 0x8c218600004104L, 0x8180004000402000L,
			0x488c402000401001L, 0x4018a00080801004L, 0x1230002105001008L, 0x8904800800800400L, 0x42000c42003810L, 0x8408110400b012L, 0x18086182000401L,
			0x2240088020c28000L, 0x1001201040c004L, 0xa02008010420020L, 0x10003009010060L, 0x4008008008014L, 0x80020004008080L, 0x282020001008080L,
			0x50000181204a0004L, 0x102042111804200L, 0x40002010004001c0L, 0x19220045508200L, 0x20030010060a900L, 0x8018028040080L, 0x88240002008080L,
			0x10301802830400L, 0x332a4081140200L, 0x8080010a601241L, 0x1008010400021L, 0x4082001007241L, 0x211009001200509L, 0x8015001002441801L,
			0x801000804000603L, 0xc0900220024a401L, 0x1000200608243L };
	private static final long[] bishopMagicNumbers = { 0x2910054208004104L, 0x2100630a7020180L, 0x5822022042000000L, 0x2ca804a100200020L, 0x204042200000900L,
			0x2002121024000002L, 0x80404104202000e8L, 0x812a020205010840L, 0x8005181184080048L, 0x1001c20208010101L, 0x1001080204002100L, 0x1810080489021800L,
			0x62040420010a00L, 0x5028043004300020L, 0xc0080a4402605002L, 0x8a00a0104220200L, 0x940000410821212L, 0x1808024a280210L, 0x40c0422080a0598L,
			0x4228020082004050L, 0x200800400e00100L, 0x20b001230021040L, 0x90a0201900c00L, 0x4940120a0a0108L, 0x20208050a42180L, 0x1004804b280200L,
			0x2048020024040010L, 0x102c04004010200L, 0x20408204c002010L, 0x2411100020080c1L, 0x102a008084042100L, 0x941030000a09846L, 0x244100800400200L,
			0x4000901010080696L, 0x280404180020L, 0x800042008240100L, 0x220008400088020L, 0x4020182000904c9L, 0x23010400020600L, 0x41040020110302L,
			0x412101004020818L, 0x8022080a09404208L, 0x1401210240484800L, 0x22244208010080L, 0x1105040104000210L, 0x2040088800c40081L, 0x8184810252000400L,
			0x4004610041002200L, 0x40201a444400810L, 0x4611010802020008L, 0x80000b0401040402L, 0x20004821880a00L, 0x8200002022440100L, 0x9431801010068L,
			0x1040c20806108040L, 0x804901403022a40L, 0x2400202602104000L, 0x208520209440204L, 0x40c000022013020L, 0x2000104000420600L, 0x400000260142410L,
			0x800633408100500L, 0x2404080a1410L, 0x138200122002900L };

	private static final Magic[] rookMagics = new Magic[64];
	private static final Magic[] bishopMagics = new Magic[64];

	public static long getRookSet(final int sq, final long occ) {
		assert Square.validate(sq);
		final Magic magic = rookMagics[sq];
		return magic.magicMoves[(int) ((occ & magic.movementMask) * magic.magicNumber >>> magic.shift)];
	}

	public static long getBishopSet(final int sq, final long occ) {
		assert Square.validate(sq);
		final Magic magic = bishopMagics[sq];
		return magic.magicMoves[(int) ((occ & magic.movementMask) * magic.magicNumber >>> magic.shift)];
	}

	public static long getQueenSet(final int sq, final long occ) {
		assert Square.validate(sq);
		final Magic rookMagic = rookMagics[sq];
		final Magic bishopMagic = bishopMagics[sq];
		return rookMagic.magicMoves[(int) ((occ & rookMagic.movementMask) * rookMagic.magicNumber >>> rookMagic.shift)]
				| bishopMagic.magicMoves[(int) ((occ & bishopMagic.movementMask) * bishopMagic.magicNumber >>> bishopMagic.shift)];
	}

	public static long getRookSetEmptyBoard(final int sq) {
		assert Square.validate(sq);
		return rookMagics[sq].magicMoves[0];
	}

	public static long getBishopSetEmptyBoard(final int sq) {
		assert Square.validate(sq);
		return bishopMagics[sq].magicMoves[0];
	}

	public static long getQueenSetEmptyBoard(final int sq) {
		assert Square.validate(sq);
		return bishopMagics[sq].magicMoves[0] | rookMagics[sq].magicMoves[0];
	}

	static {
		for (int i = 0; i < 64; i++) {
			rookMagics[i] = new Magic(rookMagicNumbers[i]);
			bishopMagics[i] = new Magic(bishopMagicNumbers[i]);
		}
		calculateBishopMovementMasks();
		calculateRookMovementMasks();
		generateShiftArrys();
		long[][] bishopOccupancyVariations = calculateVariations(bishopMagics);
		long[][] rookOccupancyVariations = calculateVariations(rookMagics);
		generateBishopMoveDatabase(bishopOccupancyVariations);
		generateRookMoveDatabase(rookOccupancyVariations);
	}

	private static void generateShiftArrys() {
		for (int i = 0; i < 64; i++) {
			rookMagics[i].shift = 64 - Long.bitCount(rookMagics[i].movementMask);
			bishopMagics[i].shift = 64 - Long.bitCount(bishopMagics[i].movementMask);
		}
	}

	private static long[][] calculateVariations(Magic[] magics) {

		long[][] occupancyVariations = new long[64][];
		for (int index = 0; index < 64; index++) {
			int variationCount = (int) Util.POWER_LOOKUP[Long.bitCount(magics[index].movementMask)];
			occupancyVariations[index] = new long[variationCount];

			for (int variationIndex = 1; variationIndex < variationCount; variationIndex++) {
				long currentMask = magics[index].movementMask;

				for (int i = 0; i < 32 - Integer.numberOfLeadingZeros(variationIndex); i++) {
					if ((Util.POWER_LOOKUP[i] & variationIndex) != 0) {
						occupancyVariations[index][variationIndex] |= Long.lowestOneBit(currentMask);
					}
					currentMask &= currentMask - 1;
				}
			}
		}

		return occupancyVariations;
	}

	private static void calculateRookMovementMasks() {
		for (int index = 0; index < 64; index++) {

			// up
			for (int j = index + 8; j < 64 - 8; j += 8) {
				rookMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// down
			for (int j = index - 8; j >= 0 + 8; j -= 8) {
				rookMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// left
			for (int j = index + 1; j % 8 != 0 && j % 8 != 7; j++) {
				rookMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// right
			for (int j = index - 1; j % 8 != 7 && j % 8 != 0 && j > 0; j--) {
				rookMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
		}
	}

	private static void calculateBishopMovementMasks() {
		for (int index = 0; index < 64; index++) {

			// up-right
			for (int j = index + 7; j < 64 - 7 && j % 8 != 7 && j % 8 != 0; j += 7) {
				bishopMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// up-left
			for (int j = index + 9; j < 64 - 9 && j % 8 != 7 && j % 8 != 0; j += 9) {
				bishopMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// down-right
			for (int j = index - 9; j >= 0 + 9 && j % 8 != 7 && j % 8 != 0; j -= 9) {
				bishopMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
			// down-left
			for (int j = index - 7; j >= 0 + 7 && j % 8 != 7 && j % 8 != 0; j -= 7) {
				bishopMagics[index].movementMask |= Util.POWER_LOOKUP[j];
			}
		}
	}

	private static void generateRookMoveDatabase(long[][] rookOccupancyVariations) {
		for (int index = 0; index < 64; index++) {
			rookMagics[index].magicMoves = new long[rookOccupancyVariations[index].length];
			for (int variationIndex = 0; variationIndex < rookOccupancyVariations[index].length; variationIndex++) {
				long validMoves = 0;
				int magicIndex = (int) ((rookOccupancyVariations[index][variationIndex] * rookMagicNumbers[index]) >>> rookMagics[index].shift);

				for (int j = index + 8; j < 64; j += 8) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 8; j >= 0; j -= 8) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index + 1; j % 8 != 0; j++) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				for (int j = index - 1; j % 8 != 7 && j >= 0; j--) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((rookOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}

				rookMagics[index].magicMoves[magicIndex] = validMoves;
			}
		}
	}

	private static void generateBishopMoveDatabase(long[][] bishopOccupancyVariations) {
		for (int index = 0; index < 64; index++) {
			bishopMagics[index].magicMoves = new long[bishopOccupancyVariations[index].length];
			for (int variationIndex = 0; variationIndex < bishopOccupancyVariations[index].length; variationIndex++) {
				long validMoves = 0;
				int magicIndex = (int) ((bishopOccupancyVariations[index][variationIndex] * bishopMagicNumbers[index]) >>> bishopMagics[index].shift);

				// up-right
				for (int j = index + 7; j % 8 != 7 && j < 64; j += 7) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// up-left
				for (int j = index + 9; j % 8 != 0 && j < 64; j += 9) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-right
				for (int j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}
				// down-left
				for (int j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
					validMoves |= Util.POWER_LOOKUP[j];
					if ((bishopOccupancyVariations[index][variationIndex] & Util.POWER_LOOKUP[j]) != 0) {
						break;
					}
				}

				bishopMagics[index].magicMoves[magicIndex] = validMoves;
			}
		}
	}

	private static class Util {

		private static final byte[][] DISTANCE = new byte[64][64];
		static {
			for (int i = 0; i < 64; i++) {
				for (int j = 0; j < 64; j++) {
					DISTANCE[i][j] = (byte) Math.max(Math.abs((i >>> 3) - (j >>> 3)), Math.abs((i & 7) - (j & 7)));
				}
			}
		}

		public static final long[] POWER_LOOKUP = new long[64];
		static {
			for (int i = 0; i < 64; i++) {
				POWER_LOOKUP[i] = 1L << i;
			}
		}

	}

	private BitboardGen() {

	}

}
