package clobber;
import game.*;

import java.awt.Point;
import java.util.*;

import clobber.ScoredClobberMove;

public class AlphaBetaPlayer extends GamePlayer {

	public static final double MAX_SCORE = Double.POSITIVE_INFINITY;
	public static final int ROWS = ClobberState.ROWS;
	public static final int COLS = ClobberState.COLS;
	
	public static final int MAX_DEPTH = 4;
	public int depthLimit;
	protected int eval_function;
	protected int P = 27;
	
	// Used to store the best move at any particular depth
	protected ScoredClobberMove[] mvStack;
	
	
	/**
	 * Constructs an AlphaBetaPlayer object with the specified name and depth.
	 * 
	 * @param n			: the name of the player
	 * @param depth		: the depth of the alpha beta search
	 * @param eval_function which evaluation function to use
	 */
	public AlphaBetaPlayer(String n, int depth, int eval_function) {
		super(n, new ClobberState(), false);
		this.depthLimit = depth;
		this.eval_function = eval_function;
	}
	
	/**
	 * Determines whether a board represents a completed game.  If the board is
	 * complete, then the evaluation value for the board is recorded based on
	 * which players is moving.
	 * 
	 * @param brd	: clobber board to be considered
	 * @param move	: the move associated with the board state
	 * @return 		: true if the board is terminal
	 */
	protected boolean terminalValue(GameState board, ScoredClobberMove move) {
		GameState.Status status = board.getStatus();
		boolean isTerminal = true;
		
		if (status == GameState.Status.HOME_WIN) {
			move.setScore(MAX_SCORE);
		}
		else if (status == GameState.Status.AWAY_WIN) {
			move.setScore(-MAX_SCORE);
		}
		else {
			isTerminal = false;
		}
		
		return isTerminal;
	}
	
	/**
	 * Used to initialize the alpha beta search.
	 * 		- Creates the default move stack of best moves
	 * 		- Other things?
	 */
	public void init() {
		mvStack = new ScoredClobberMove [MAX_DEPTH];
		
		for (int i=0; i < MAX_DEPTH; i++) {
			mvStack[i] = new ScoredClobberMove(0, 0, 0, 0, 0);
		}
	}
	
	/*
	public void reinit(GameState state) {
		ClobberMove firstmove = getMoves(state).get(0);
		for (int i=0; i < MAX_DEPTH; i++) {
			mvStack[i] = new ScoredClobberMove(firstmove, 0);
		}
	}
	*/
	
	/**
	 * Populates and return a list of possible clobber moves based on a specified
	 * game board and the current player's turn.
	 * 
	 * @param state		: the game board
	 * @return			: a list of clobber moves
	 */
	public ArrayList<ScoredClobberMove> getMoves(GameState state) {
		ClobberState board = (ClobberState) state;
		
		ArrayList<ScoredClobberMove> list = new ArrayList<ScoredClobberMove>();
		ScoredClobberMove move = new ScoredClobberMove();
		
		for (int r = 0; r < ClobberState.ROWS; r++) {
			for (int c = 0; c < ClobberState.COLS; c++) {
				move.row1 = r;
				move.col1 = c;
				move.row2 = r-1;
				move.col2 = c;
				if (board.moveOK(move)) list.add((ScoredClobberMove) move.clone());
				
				move.row2 = r+1;
				move.col2 = c;
				if (board.moveOK(move)) list.add((ScoredClobberMove) move.clone());
				
				move.row2 = r;
				move.col2 = c-1;
				if (board.moveOK(move)) list.add((ScoredClobberMove) move.clone());
				
				move.row2 = r;
				move.col2 = c+1;
				if (board.moveOK(move)) list.add((ScoredClobberMove) move.clone());
			}
		}
		
		return list;
	}
	
	/**
	 * Populates the list of best moves by performing a recursive alpha beta search.
	 * 
	 * @param board			: the board being considered
	 * @param currDepth		: the current depth of the search
	 * @param a				: alpha, the best maximum score
	 * @param b				: beta, the best minimum score
	 */
	private void alphaBeta(ClobberState board, int currDepth, double a, double b) {
		alphaBeta(board, currDepth, a, b, true);
	}
	private void alphaBeta(ClobberState board, int currDepth, double a, double b, boolean use_limit) {
		boolean toMaximize = (board.getWho() == GameState.Who.HOME);
		boolean isTerminal = terminalValue(board, mvStack[currDepth]);
		
		// If the move is terminal, allow score to propagate up
		if (isTerminal) {
			return;
		}
		
		// If the depth limit is reached, use the evaluation function
		else if (currDepth == depthLimit  && use_limit) {
			mvStack[currDepth].setScore(eval(board));
		}
		
		// Otherwise continue alpha beta recursion
		else {
			ScoredClobberMove tempMove = new ScoredClobberMove();
			
			double bestScore = (toMaximize ? 
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			
			ScoredClobberMove bestMove = mvStack[currDepth];
			ScoredClobberMove nextMove = mvStack[currDepth+1];
			
			bestMove.setScore(bestScore);
			GameState.Who currTurn = board.getWho();
			
			// Get possible moves and shuffle them
			ArrayList<ScoredClobberMove> moves = getMoves(board);
			Collections.shuffle(moves);
			
			for (int i = 0; i < moves.size(); i++) {
				// Create and make move
				tempMove = new ScoredClobberMove(moves.get(i));
				board.makeMove(tempMove);
				
				// Examine the move recursively
				alphaBeta(board, currDepth+1, a, b);
				
				// Undo the move
				board.board[tempMove.row1][tempMove.col1] = 
						(board.getWho() == GameState.Who.HOME ? 
								ClobberState.homeSym : ClobberState.awaySym);
				
				board.board[tempMove.row2][tempMove.col2] = 
						(board.getWho() == GameState.Who.HOME ? 
								ClobberState.awaySym : ClobberState.homeSym);
				
				// Ensure status and turn have not been modified
				board.status = GameState.Status.GAME_ON;
				board.who = currTurn;
				board.numMoves--;
				
				// Examine the results, relative to what we have seen
				if (toMaximize && nextMove.score >= bestMove.score) {
					bestMove.set(tempMove, nextMove.score);
				}
				else if (!toMaximize && nextMove.score <= bestMove.score) {
					bestMove.set(tempMove, nextMove.score);
				}
				
				// Update alpha and beta and perform pruning
				if (!toMaximize) {
					b = Math.min(bestMove.score, b);
					if (bestMove.score <= a || bestMove.score == -MAX_SCORE) {
						return;
					}
				}
				else {
					a = Math.max(bestMove.score, a);
					if (bestMove.score >= b || bestMove.score == MAX_SCORE) {
						return;
					}
				}
			}
		}
	}
	
	protected GameMove scorpu(GameState state)
	{
		ClobberState cs = (ClobberState)state;
		ArrayList<Point> possibleStones = new ArrayList<Point>();
		boolean home = (cs.getWho() == GameState.Who.HOME);
		int max_opposing = 0;
		for (int r = 0; r < ClobberState.ROWS; r++)
		{
			for (int c = 0; c < ClobberState.COLS; c++)
			{
				
				int opposingStones = 0;		
				if (home)
				{
					if (cs.board[r][c] == ClobberState.homeSym) //if this piece is owned by the player
					{					
						try { if (cs.board[r - 1][c + 0] == ClobberState.awaySym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 0][c - 1] == ClobberState.awaySym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 0][c + 1] == ClobberState.awaySym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 1][c + 0] == ClobberState.awaySym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
					}
					if (opposingStones > max_opposing)
					{
						possibleStones.clear(); //no stone has had this many adjacent opponents
						possibleStones.add(new Point(r, c));
						max_opposing = opposingStones;
					}
					if (opposingStones == max_opposing)
					{
						possibleStones.add(new Point(r, c));
					}
				}
				else
				{
					if (cs.board[r][c] == ClobberState.awaySym) //if this piece is owned by the player
					{					
						try { if (cs.board[r - 1][c + 0] == ClobberState.homeSym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 0][c - 1] == ClobberState.homeSym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 0][c + 1] == ClobberState.homeSym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
						try { if (cs.board[r + 1][c + 0] == ClobberState.homeSym) opposingStones++; } catch (IndexOutOfBoundsException e) {}
					}
					if (opposingStones > max_opposing)
					{
						possibleStones.clear(); //no stone has had this many adjacent opponents
						possibleStones.add(new Point(r, c));
						max_opposing = opposingStones;
					}
					if (opposingStones == max_opposing)
					{
						possibleStones.add(new Point(r, c));
					}
				}					
			}
		}
		Point stone_to_move = possibleStones.get((int)(Math.random() * (possibleStones.size() - 1))); //get a random stone from the array
		ArrayList<ClobberMove> validMoves = new ArrayList<ClobberMove>();
		int x = stone_to_move.x;
		int y = stone_to_move.y;
		if (home)
		{			
			try { if (cs.board[x - 1][y + 0] == ClobberState.awaySym) validMoves.add(new ClobberMove(x, y, x - 1, y + 0)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 0][y - 1] == ClobberState.awaySym) validMoves.add(new ClobberMove(x, y, x + 0, y - 1)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 0][y + 1] == ClobberState.awaySym) validMoves.add(new ClobberMove(x, y, x + 0, y + 1)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 1][y + 0] == ClobberState.awaySym) validMoves.add(new ClobberMove(x, y, x + 1, y + 0)); } catch (IndexOutOfBoundsException e) {}
			
		}
		else
		{
			try { if (cs.board[x - 1][y + 0] == ClobberState.homeSym) validMoves.add(new ClobberMove(x, y, x - 1, y + 0)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 0][y - 1] == ClobberState.homeSym) validMoves.add(new ClobberMove(x, y, x + 0, y - 1)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 0][y + 1] == ClobberState.homeSym) validMoves.add(new ClobberMove(x, y, x + 0, y + 1)); } catch (IndexOutOfBoundsException e) {}
			try { if (cs.board[x + 1][y + 0] == ClobberState.homeSym) validMoves.add(new ClobberMove(x, y, x + 1, y + 0)); } catch (IndexOutOfBoundsException e) {}
		}
		
		return validMoves.get((int)(Math.random() * (validMoves.size() - 1)));
	}
	
	protected GameMove headscratcher(GameState state)
	{
		
		return null;
	}
	
	/**
	 * Calculates the next move to be performed by the alpha beta player
	 * 
	 * @param state		: the current game board
	 * @param lastMove	: the last move to be performed
	 * @return			: the next move to be performed
	 */
	public GameMove getMove(GameState state, String lastMove) {
		// reinit(state);
		if (eval_function == 3)
		{
			return scorpu(state);
		}
		else if (eval_function == 4)
		{
			
			return getMoves(state).get(0); //temporary
		}
		else
		{
			if (state.numMoves <= 15)	
				alphaBeta((ClobberState)state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false); //don't use depth limit
			else
				alphaBeta((ClobberState)state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		
			return mvStack[0];
		}
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
		switch(eval_function)
		{
		case 1:
			return eval1(gs);
		case 2:
			return eval2(gs);
		default:
			return 0;
		}
	}
	private int eval1(ClobberState gs) {
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
		
		//System.out.println("Home: " + homePiecesMove + "\nAway: " + awayPiecesMove);
		
		// Return the difference between the home and away pieces
		return homePiecesMove - awayPiecesMove;
	}
	class Block
	{
		ArrayList<Point> points;
		int home_stones;
		int away_stones;
		Block()
		{
			points = new ArrayList<Point>();
			home_stones = 0;
			away_stones = 0;
		}
		Block(Point point, ClobberState state)
		{
			this();
			addPoint(point, state);
		}
		Block(Block other)
		{
			this();
			for(Point p : other.points)
			{
				points.add(p);
			}
			home_stones = other.home_stones;
			away_stones = other.away_stones;
		}
		void addPoint(Point p, ClobberState cs)
		{
			points.add(p);
			if (cs.board[p.x][p.y] == ClobberState.homeSym)
				home_stones++;
			else
				away_stones++;
		}
	}
	protected Block buildBlock(Block outer_block, Point current, ClobberState cs)
	{
		Block local_block = new Block(outer_block);
		
		return local_block;
	}
	protected ArrayList<Block> getBlocks(ClobberState cs)
	{
		
		for (int r = 0; r < ROWS; r++)
		{
			for (int c = 0; c < COLS; c++)
			{
				 
			}
		}
		return null;
	}
	private int eval2(ClobberState gs) {
		int home_score = 0;
		int away_score = 0;
		if (gs.numMoves < 15)
		{
			for (int row = 0; row < ClobberState.ROWS; row++) {
				for (int col = 0; col < ClobberState.COLS; col++) {
					// If the home piece can move, increment its movable pieces
					if (gs.board[row][col] == ClobberState.homeSym && canMove(row, col, gs.board)) {
						home_score++;
					}
					// If the away piece can move, increment its movable pieces
					else if (gs.board[row][col] == ClobberState.awaySym && canMove(row, col, gs.board)) {
						away_score++;
					}
				}
			}
		}
		return eval1(gs);
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
		int eval = 1;
		GamePlayer p = new AlphaBetaPlayer("AB_" + eval, AlphaBetaPlayer.MAX_DEPTH - 1, eval);
		p.compete(args, 1);
	}
}
