package clobber;
import game.*;

import java.util.*;


public class AlphaBetaPlayer extends GamePlayer {
	
	public AlphaBetaPlayer(String n) {
		super(n, new ClobberState(), false);
	}
	
	public GameMove getMove(GameState state, String lastMove) {
		ClobberState board = (ClobberState)state;
		ArrayList<ClobberMove> list = new ArrayList<ClobberMove>();  
		ClobberMove mv = new ClobberMove();
		
		for (int r=0; r<ClobberState.ROWS; r++) {
			for (int c=0; c<ClobberState.COLS; c++) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r-1; mv.col2 = c;
				if (board.moveOK(mv)) {
					list.add((ClobberMove)mv.clone());
				}
				mv.row2 = r+1; mv.col2 = c;
				if (board.moveOK(mv)) {
					list.add((ClobberMove)mv.clone());
				}
				mv.row2 = r; mv.col2 = c-1;
				if (board.moveOK(mv)) {
					list.add((ClobberMove)mv.clone());
				}
				mv.row2 = r; mv.col2 = c+1;
				if (board.moveOK(mv)) {
					list.add((ClobberMove)mv.clone());
				}
			}
		}
		
		int value = eval(board);
		
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	
	/**
	 * An evaluation function for the alpha-beta player.  This method calculates the difference
	 * between the number of pieces home and away can move.  Home should prefer states where the
	 * function is maximized, whereas away should prefer states with minimized values.
	 * 
	 * @param gs	: the current game state
	 * @return		: an integer indicating which side is winning
	 */
	private int eval(ClobberState gs) {
		int homePiecesMove = 0;
		int awayPiecesMove = 0;
		
		for (int row = 0; row < ClobberState.ROWS; row++) {
			for (int col = 0; col < ClobberState.COLS; col++) {
				// If the home piece can move, increment its movable pieces
				if (gs.board[row][col] == ClobberState.homeSym && canMove(row, col, gs.board)) {
					homePiecesMove++;
				}
				// If the away piece can move, increment its movable pieces
				else if (gs.board[row][col] == ClobberState.awaySym && canMove(row, col, gs.board)) {
					awayPiecesMove++;
				}
			}
		}
		
		System.out.println("Home: " + homePiecesMove + "\nAway: " + awayPiecesMove);
		
		// Return the difference between the home and away pieces
		return homePiecesMove - awayPiecesMove;
	}
	
	/**
	 * Checks whether the player piece at the specified location can move to at least one adjacent
	 * location.  Pieces can move to in-bound and opponent-occupied adjacent locations.
	 * 
	 * @param row		: the row of the player piece
	 * @param col		: the column of the player piece
	 * @param board		: the game board
	 * @return			: true if the player piece can move
	 */
	private boolean canMove(int row, int col, char[][] board) {
		char playerSym = board[row][col];
		ClobberMove cm = new ClobberMove();
		
		// Check (row + 1, column)
		cm.row2 = row + 1;
		cm.col2 = col;
		if (moveOK(cm, board, playerSym)) return true;
		
		// Check (row - 1, column)
		cm.row2 = row - 1;
		cm.col2 = col;
		if (moveOK(cm, board, playerSym)) return true;
		
		// Check (row, column + 1)
		cm.row2 = row;
		cm.col2 = col + 1;
		if (moveOK(cm, board, playerSym)) return true;
		
		// Check (row, column - 1)
		cm.row2 = row;
		cm.col2 = col - 1;
		if (moveOK(cm, board, playerSym)) return true;
		
		// Return false if no moves possible
		return false;
	}
	
	/**
	 * Checks to see whether the specified move is valid.  This method assumes that the player
	 * is adjacent to the move locations (i.e. that the moves are available) and only returns true
	 * if the location is in bounds and occupied by the opponent.
	 * 
	 * @param cm				: the move object
	 * @param board				: the Clobber board
	 * @param playerSym			: the color of the player
	 * @return					: true if OK
	 */
	private boolean moveOK(ClobberMove cm, char[][] board, char playerSym) {
		// Set the opponent symbol
		char opponent = (playerSym == ClobberState.homeSym) ? 
				ClobberState.awaySym : ClobberState.homeSym;
		
		// Check whether the move is valid
		if (cm != null && Util.inrange(cm.row2, 0, ClobberState.ROWS - 1) &&
			Util.inrange(cm.col2, 0, ClobberState.COLS - 1) && board[cm.row2][cm.col2] == opponent) {
				return true;
			}
		
		return false;
	}
	
	public static void main(String [] args) {
		GamePlayer p = new AlphaBetaPlayer("Alpha Beta Player");
		p.compete(args, 1);
	}
}
