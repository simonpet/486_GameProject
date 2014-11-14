package clobber;

import java.util.Comparator;

public class ScoredClobberMove extends ClobberMove {
	
	public double score;
	public int distanceScore;
	
	
	public static class SortMoveAsc implements Comparator<ScoredClobberMove>{
		public int compare(ScoredClobberMove scm1, ScoredClobberMove scm2){
			if(scm1.distanceScore > scm2.distanceScore) return 1;
			else if (scm1.distanceScore == scm2.distanceScore) return 0;
			else return -1;
		}
	}
	
	public static class SortMoveDes implements Comparator<ScoredClobberMove>{
		public int compare(ScoredClobberMove scm1, ScoredClobberMove scm2){
			if(scm1.distanceScore > scm2.distanceScore) return -1;
			else if (scm1.distanceScore == scm2.distanceScore) return 0;
			else return 1;
		}
	}
	
	/**
	 * Constructs a default instance of ScoredClobberPlayer.  This constructor uses the super-
	 * constructor to create a standard ClobberMove, then sets default score value.
	 */
	public ScoredClobberMove() {
		super();
		this.score = 0;
	}
	
	/**
	 * Constructs an instance ScoredClobberMove based on a specified source move.  This
	 * constructor uses the super-constructor to copy the original move, then sets the score
	 * using the values from the original.
	 * 
	 * @param move		: the source ScoredClobberMove
	 */
	public ScoredClobberMove(ScoredClobberMove move) {
		super(move);
		this.score = move.score;
	}
	
	/**
	 * Constructs an instance of ScoredClobberMove using the specified parameters.
	 * 
	 * @param r1		: the row of the attacking stone
	 * @param c1		: the column of the attacking stone
	 * @param r2		: the row of the target stone
	 * @param c2		: the column of the attacking stone
	 * @param score		: the state score associated with the move
	 */
	public ScoredClobberMove(int r1, int c1, int r2, int c2, double score) {
		super(r1, c1, r2, c2);
		this.score = score;
	}
	
	/**
	 * Calculates the distance score associated with the Clobber move.  If the stone is close
	 * to the center of the board and moving toward the closer edge, then the distance score
	 * associated with the move is greater.
	 */
	public void calculateDistanceScore(){
		distanceScore = 0;
		
		// If the move is up
		if (row1 >= (ClobberState.ROWS / 2) && row2 > row1)
			distanceScore = ClobberState.ROWS - row1;
		
		// If the move is down
		else if (row1 < (ClobberState.ROWS / 2) && row2 < row1)
			distanceScore = row1;
		
		// If the move is right
		else if (col1 >= (ClobberState.COLS / 2) && col2 > col1)
			distanceScore = ClobberState.COLS - col1;
		
		// If the move is left
		else if (col1 <= (ClobberState.COLS / 2) && col2 < col1)
			distanceScore = col1;
	}
	
	/**
	 * Sets the instance variables of the ScoredClobberMove based on the specified input.
	 * This method allows developers to update all the instance variables.
	 * 
	 * @param row1		: the row of the attacking stone
	 * @param col1		: the column of the attacking stone
	 * @param row2		: the row of the target stone
	 * @param col2		: the column of the target stone
	 * @param score		: the state score associated with the move
	 */
	public void set(int row1, int col1, int row2, int col2, double score) {
		this.row1 = row1;
		this.col1 = col1;
		this.row2 = row2;
		this.col2 = col2;
		this.score = score;
	}
	
	/**
	 * Sets the instance variables of the ScoredClobberMove based on a reference move and a
	 * specified score value.  This method creates a deeply copies the reference values.
	 * 
	 * @param move		: the source ClobberMove
	 * @param score		: the score associated with the move
	 */
	public void set(ClobberMove move, double score) {
		this.row1 = move.row1;
		this.col1 = move.col1;
		this.row2 = move.row2;
		this.col2 = move.col2;
		this.score = score;
	}
	
	public void setScore(double newScore) {
		this.score = newScore;
	}
	
	public Object clone() {
		return new ScoredClobberMove(row1, col1, row2, col2, score);
	}
	
	public String toString() {
		return row1 + " " + col1 + " " + row2 + " " + col2 + " " + score;
	}
}