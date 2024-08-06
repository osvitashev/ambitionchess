package gamestate;
/**
 * 
 * The class is based on the fact that during the game tree exploration moves are allocated and deallocated as a stack.
 *
 */
public class MovePool {
	private int[] movepool = new int[1000];
	private int movepool_size=0;
	
	public void clear() {
		movepool_size=0;
	}
	
	public void resize(int newSize) {
		assert newSize>=0;
		movepool_size=newSize;
	}
	
	public void add(int move) {
		movepool[movepool_size++]=move;
	}
	
	public int size() {
		return movepool_size;
	}
	
	public int get(int index) {
		return movepool[index];
	}
	
	

}
