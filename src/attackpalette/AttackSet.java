package attackpalette;

public class AttackSet {
	static final int cost_pawn = 1;
	static final int cost_knight = 3;
	static final int cost_bishop = 3;
	static final int cost_rook = 5;
	static final int cost_queen = 9;
	static final int cost_king = 100;

	private long attacks;
	private int type;// PieceType;
	private int origin;// how to treat pawns???
	private int commitment;// cost of attacking piece
	private int prevCommitment;// precondition for this attackset being utilized

	long getAttacks() {
		return attacks;
	}

	void setAttacks(long attacks) {
		this.attacks = attacks;
	}

	int getType() {
		return type;
	}

	void setType(int type) {
		this.type = type;
	}

	int getOrigin() {
		return origin;
	}

	void setOrigin(int origin) {
		this.origin = origin;
	}

	int getCommitment() {
		return commitment;
	}

	void setCommitment(int commitment) {
		this.commitment = commitment;
	}

	int getPrevCommitment() {
		return prevCommitment;
	}

	void setPrevCommitment(int prevCommitment) {
		this.prevCommitment = prevCommitment;
	}

}
