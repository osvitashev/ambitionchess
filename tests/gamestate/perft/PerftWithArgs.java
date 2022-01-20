package gamestate.perft;

public class PerftWithArgs {

	public static void main(String[] args) {
		String fen = args[0];
		int depth = Integer.valueOf(args[1]);
		
		PerftTest.testPerft(fen, depth);

	}

}
