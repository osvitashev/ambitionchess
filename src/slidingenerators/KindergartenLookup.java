package slidingenerators;

/**
 * 
 * Ported from https://github.com/Gigantua/Chess_Movegen
 *
 */
public class KindergartenLookup {
	private final static long ranks_64[] = new long[64];
	static {
		long ranks[] = new long[8];
		for (int i = 0; i < 8; i++) {
			ranks[i] = (long) (0b11111111) << (i * 8);
		}
		for (int i = 0; i < 64; i++) {
			ranks_64[i] = ranks[i / 8];
		}
	}
	private final static long files_64[] = new long[64];
	static {
		long files[] = new long[8];
		for (int i = 0; i < 8; i++) {
			files[i] = (long) (0b0000000100000001000000010000000100000001000000010000000100000001L) << i;
		}
		for (int i = 0; i < 64; i++) {
			files_64[i] = files[i % 8];
		}
	}
	private final static long upper_left_side_zero = 0b1000000011000000111000001111000011111000111111001111111011111111L;
	private final static long lower_right_side_zero = 0b1111111101111111001111110001111100001111000001110000001100000001L;
	private final static long lower_left_side_zero = 0b1111111011111100111110001111000011100000110000001000000000000000L;
	private final static long upper_right_side_zero = 0b0000000000000001000000110000011100001111000111110011111101111111L;
	private final static long main_diagonal = 0b1000000001000000001000000001000000001000000001000000001000000001L; // A1 to H8
	private final static long anti_diagonal = 0b0000000100000010000001000000100000010000001000000100000010000000L; // H1 to A8
	private final static long diagonals_64[] = new long[64];
	static {
		for (int i = 0; i < 8; i++) {
			long current_diagonal = (main_diagonal << i) & upper_left_side_zero;
			long tmp = current_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				diagonals_64[index] = current_diagonal;
				tmp = tmp & (~(1L << index));
			}
		}
		for (int i = 1; i < 8; i++) {
			long current_diagonal = (main_diagonal >>> i) & lower_right_side_zero;
			long tmp = current_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				diagonals_64[index] = current_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
	}
	private final static long anti_diagonals_64[] = new long[64];
	static {
		for (int i = 1; i < 8; i++) {
			long current_anti_diagonal = (anti_diagonal << i) & lower_left_side_zero;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		{
			long current_anti_diagonal = anti_diagonal;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
		for (int i = 1; i < 8; i++) {
			long current_anti_diagonal = (anti_diagonal >>> i) & upper_right_side_zero;
			long tmp = current_anti_diagonal;
			while (tmp != 0) {
				int index = Long.numberOfTrailingZeros(tmp);// std::countr_zero(tmp);
				anti_diagonals_64[index] = current_anti_diagonal;
				tmp = tmp & ~(1L << index);
			}
		}
	}

	private static byte hashkeyRank(int square, long occupancy) {
		return (byte) (((occupancy >>> ((square / 8) * 8)) >>> 1) & 0b111111);
	}

	private static byte hashkeyFile(int square, long occupancy) {
		return (byte) ((((((occupancy >>> (square % 8)) & files_64[0]) * main_diagonal) >>> 56) >>> 1) & 0b111111);
	}

	private static byte hashkeyDiagonal(int square, long occupancy) {
		return (byte) (((((occupancy & diagonals_64[square]) * files_64[0]) >>> 56) >>> 1) & 0b111111);
	}

	private static byte hashkeyAntiDiagonal(int square, long occupancy) {
		return (byte) (((((occupancy & anti_diagonals_64[square]) * files_64[0]) >>> 56) >>> 1) & 0b111111);
	}

	private final static long north = 8;
	private final static long south = -8;
	private final static long east = 1;
	private final static long west = -1;
	private final static long north_east = 9;
	private final static long north_west = 7;
	private final static long south_west = -9;
	private final static long south_east = -7;

	private final static long possible_ranks[] = new long[64];
	static {
		for (long i = 0; i < 64; i++) {
			long tmp = (long) (0b10000001) | (((long) (i)) << 1);
			for (int j = 0; j < 8; j++) {
				possible_ranks[(int) i] |= tmp << (j * 8);
			}
		}
	}
	private final static long possible_files[] = new long[64];

	private static long rankToFile(long rank) {
		return (((rank & 0b11111111) * main_diagonal) & files_64[7]) >>> 7;
	}

	static {
		for (long i = 0; i < 64; i++) {
			long tmp = rankToFile((long) (0b10000001) | (((long) (i)) << 1));
			for (int j = 0; j < 8; j++) {
				possible_files[(int) i] |= tmp << j;
			}
		}
	}
	private final static long rank_attack_table[][] = new long[64][64];
	static {
		for (int index = 0; index < 64; index++) {
			for (int possible_ranks_index = 0; possible_ranks_index < 64; possible_ranks_index++) {
				long tmp_attack_mask = 0;
				long occupancy = possible_ranks[possible_ranks_index];
				long i = index;
				while (true) {
					i += east;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if (index % 8 == 7) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				i = index;
				while (true) {
					i += west;
					if (i >= 64 || i < 0/* equals i < 0 */)
					{
						break;
					}
					if (index % 8 == 0) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				rank_attack_table[index][hashkeyRank(index, occupancy)] = tmp_attack_mask;
			}
		}
	}
	private final static long file_attack_table[][] = new long[64][64];
	static {
		for (int index = 0; index < 64; index++) {
			for (int possible_files_index = 0; possible_files_index < 64; possible_files_index++) {
				long tmp_attack_mask = 0;
				long occupancy = possible_files[possible_files_index];
				long i = index;
				while (true) {
					i += north;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				i = index;
				while (true) {
					i += south;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				file_attack_table[index][hashkeyFile(index, occupancy)] = tmp_attack_mask;
			}
		}
	}
	private final static long diagonal_attack_table[][] = new long[64][64];
	static {
		for (int index = 0; index < 64; index++) {
			for (int possible_ranks_index = 0; possible_ranks_index < 64; possible_ranks_index++) {
				long tmp_attack_mask = 0;
				long occupancy = possible_ranks[possible_ranks_index];
				long i = index;
				while (true) {
					i += north_east;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if (index % 8 == 7) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				i = index;
				while (true) {
					i += south_west;

					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if (index % 8 == 0) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				diagonal_attack_table[index][hashkeyDiagonal(index, occupancy)] = tmp_attack_mask;
			}
		}
	}
	private final static long anti_diagonal_attack_table[][] = new long[64][64];
	static {
		for (int index = 0; index < 64; index++) {
			for (int possible_ranks_index = 0; possible_ranks_index < 64; possible_ranks_index++) {
				long tmp_attack_mask = 0;
				long occupancy = possible_ranks[possible_ranks_index];
				int i = index;
				while (true) {
					i += north_west;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if (index % 8 == 0) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				i = index;
				while (true) {
					i += south_east;
					if (i >= 64 || i < 0/* equals i < 0 */) {
						break;
					}
					if (index % 8 == 7) {
						break;
					}
					if ((occupancy & (1L << i)) != 0) {
						tmp_attack_mask |= (1L << i);
						break;
					} else {
						tmp_attack_mask |= (1L << i);
					}
				}
				anti_diagonal_attack_table[index][hashkeyAntiDiagonal(index, occupancy)] = tmp_attack_mask;
			}
		}
	}

	public static long Rook(int square, long occupation) {
		return rank_attack_table[square][hashkeyRank(square, occupation)] | file_attack_table[square][hashkeyFile(square, occupation)];
	}

	public static long Bishop(int square, long occupation) {
		return anti_diagonal_attack_table[square][hashkeyAntiDiagonal(square, occupation)] | diagonal_attack_table[square][hashkeyDiagonal(square, occupation)];
	}

	public static long Queen(int square, long occupation) {
		return anti_diagonal_attack_table[square][hashkeyAntiDiagonal(square, occupation)] | diagonal_attack_table[square][hashkeyDiagonal(square, occupation)]
				| rank_attack_table[square][hashkeyRank(square, occupation)] | file_attack_table[square][hashkeyFile(square, occupation)];
	}

}
