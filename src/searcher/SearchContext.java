package searcher;

import gamestate.Gamestate;
import gamestate.MoveGen;
import gamestate.MovePool;

/**
 * Is intended to provide communication between different components of the engine.
 * The intention is that a context will map to a thread once we reach multithreading.
 * 
 * @author sveta
 *
 */
public class SearchContext {
	public Evaluator getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}
	public MoveGen getMoveGenerator() {
		return move_generator;
	}
	public MovePool getMovepool() {
		return movepool;
	}
	public Gamestate getBrd() {
		return brd;
	}
	private MoveGen move_generator = new MoveGen();
	private MovePool movepool = new MovePool();
	private Gamestate brd = new Gamestate();
	private Evaluator evaluator;
}
