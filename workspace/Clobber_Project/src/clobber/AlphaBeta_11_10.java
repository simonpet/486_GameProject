package clobber;
import game.*;
import game.GameState.Who;

import java.util.*;

import clobber.ScoredClobberMove;

public class AlphaBeta_11_10 extends GamePlayer {

	public static final double MAX_SCORE 	= Double.POSITIVE_INFINITY;
	public static final int ROWS 			= ClobberState.ROWS;
	public static final int COLS 			= ClobberState.COLS;
	public static final int MAX_DEPTH 		= 6;
	public static final int MAX_THREADS		= 3;
	private int moves_taken = 0;
	private int cutoff = 4;
	
	
	private ScoredClobberMove[] mvStack;
	private int depthLimit;
	
	
	/**
	 * Constructs an AlphaBetaPlayer object with the specified name and depth.
	 * 
	 * @param n			: the name of the player
	 * @param depth		: the depth of the alpha beta search
	 */
	public AlphaBeta_11_10(String n, int depth) {
		super(n, new ClobberState(), false);
		this.depthLimit = depth;
	}
	
	/**
	 * The main method of the Clobber Player.
	 * 
	 * @param args		: command line arguments
	 */
	public static void main(String [] args) {
		GamePlayer p = new AlphaBeta_11_10("AB_11_10", MAX_DEPTH - 1);
		p.compete(args, 1);
	}
	@Override
	public String messageForOpponent(String opponent)
	{
		return opponent + " is worst clobber player NA";
	}
	@Override
	public void startGame(String opponent)
	{
		moves_taken = 0;
	}
	
	/**
	 * Used to initialize data structures for the alpha beta search.
	 */
	public void init() {
		mvStack = null;
		if (moves_taken < cutoff)
			mvStack = new ScoredClobberMove [MAX_DEPTH];
		else
			mvStack = new ScoredClobberMove[31 - cutoff]; //fifteen max moves
		
		
		for (int i = 0; i < mvStack.length; i++) {
			mvStack[i] = new ScoredClobberMove(0, 0, 0, 0, 0);
		}
	}
	
	/**
	 * Populates and return a list of possible clobber moves based on a specified
	 * game board and the current player's turn.
	 * 
	 * @param state		: the game board
	 * @return			: a list of clobber moves
	 */
	public List<ScoredClobberMove> getPossibleMoves(GameState state) {
		ClobberState board = (ClobberState) state;
		
		List<ScoredClobberMove> list = new ArrayList<ScoredClobberMove>();
		ScoredClobberMove move = new ScoredClobberMove();
		
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				move.row1 = r;
				move.col1 = c;
				move.row2 = r-1;
				move.col2 = c;
				if (board.moveOK(move)) list.add((ScoredClobberMove)move.clone());
				
				move.row2 = r+1;
				move.col2 = c;
				if (board.moveOK(move)) list.add((ScoredClobberMove)move.clone());
				
				move.row2 = r;
				move.col2 = c-1;
				if (board.moveOK(move)) list.add((ScoredClobberMove)move.clone());
				
				move.row2 = r;
				move.col2 = c+1;
				if (board.moveOK(move)) list.add((ScoredClobberMove)move.clone());
			}
		}
		
		return list;
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
	 * Populates the list of best moves by performing a recursive alpha beta search.
	 * 
	 * @param state			: the board being considered
	 * @param mvStack		: stores the best move at any depth
	 * @param currDepth		: the current depth of the search
	 * @param a				: alpha, the best maximum score
	 * @param b				: beta, the best minimum score
	 */
	private void alphaBeta(ClobberState state, ScoredClobberMove[] mvStack, 
			int currDepth, double a, double b) {
		
		boolean toMaximize = (state.getWho() == GameState.Who.HOME);
		boolean isTerminal = terminalValue(state, mvStack[currDepth]);
		
		/** If the move is terminal, allow the score to propagate up **/
		if (isTerminal) {
			;
		}
		
		/** If the depth limit is reached, use the evaluation function **/
		else if (moves_taken < cutoff && currDepth == depthLimit) {
			mvStack[currDepth].setScore(evaluateState(state));
			//mvStack[currDepth].setScore(0);
		}
		
		/** Otherwise continue alpha beta recursion **/
		else {
			double bestScore = (toMaximize ? 
					Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			
			ScoredClobberMove bestMove = mvStack[currDepth];
			ScoredClobberMove nextMove = mvStack[currDepth + 1];
			
			bestMove.setScore(bestScore);
			
			// Get possible moves and shuffle them
			List<ScoredClobberMove> moves = getPossibleMoves(state);
			Collections.shuffle(moves);
			
			for (int i = 0; i < moves.size(); i++) {
				// Create and make move
				ScoredClobberMove tempMove = new ScoredClobberMove(moves.get(i));
				state.makeMove(tempMove);
				
				// Examine the move recursively
				alphaBeta(state, mvStack, currDepth + 1, a, b);
				
				// Undo the move
				undoMove(state, tempMove);
				
				// Examine the results relative to what we have seen
				if (toMaximize && nextMove.score >= bestMove.score) {
					bestMove.set(tempMove, nextMove.score);
				}
				else if (!toMaximize && nextMove.score <= bestMove.score) {
					bestMove.set(tempMove, nextMove.score);
				}
				
				// Update alpha and beta, perform pruning
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
	
	/**
	 * Undoes the specified move by retrieving the original game status and swapping
	 * pieces back to their original positions.
	 * 
	 * @param state		: the current state of the board
	 * @param move		: the move to undo
	 */
	private void undoMove(ClobberState state, ScoredClobberMove move) {
		// Ensure status and turn have not been modified
		state.status = GameState.Status.GAME_ON;
		state.who = (state.who == Who.HOME) ? Who.AWAY : Who.HOME;
		state.numMoves--;
		
		// Undo the move
		state.board[move.row1][move.col1] = 
				(state.getWho() == GameState.Who.HOME ? 
						ClobberState.homeSym : ClobberState.awaySym);
		
		state.board[move.row2][move.col2] = 
				(state.getWho() == GameState.Who.HOME ? 
						ClobberState.awaySym : ClobberState.homeSym);
	}
	
	private float evaluateState(ClobberState cs) {
		float homeScore = 0;
		float awayScore = 0;
		
		for (int row = 0; row < ClobberState.ROWS; row++) {
			for (int col = 0; col < ClobberState.COLS; col++) {
				
				// If the stone is a home symbol
				if (cs.board[row][col] == ClobberState.homeSym) {
					homeScore += evaluateStone(cs, row, col);
				}
				
				// If the stone is an away symbol
				else if (cs.board[row][col] == ClobberState.awaySym) {
					awayScore += evaluateStone(cs, row, col);
				}
			}
		}
		
		// Return the heuristic
		if (homeScore > awayScore) {
			return homeScore / awayScore;
		}
		else return -awayScore / homeScore;
	}
	
	private float evaluateStone(ClobberState cs, int row, int col) {
		float score = 1;
		char friend = cs.board[row][col];
		
		// Check if the symbol is empty
		if (friend == ClobberState.emptySym) {
			return score;
		}
		
		char opponent = (friend == ClobberState.homeSym) ? ClobberState.awaySym : ClobberState.homeSym;
		
		/** Check for opponent symbols that can take this stone **/
		if (ClobberMove.posOK(row + 1, col) && cs.board[row + 1][col] == opponent) score++;
		if (ClobberMove.posOK(row - 1, col) && cs.board[row - 1][col] == opponent) score++;
		if (ClobberMove.posOK(row, col + 1) && cs.board[row][col + 1] == opponent) score++;
		if (ClobberMove.posOK(row, col - 1) && cs.board[row][col - 1] == opponent) score++;
		
		/** Check for friend symbols that can take opponent stones **/
		if (ClobberMove.posOK(row + 1, col + 1) && cs.board[row + 1][col + 1] == friend)
			if (cs.board[row + 1][col] == opponent || cs.board[row][col + 1] == opponent) score++;
		
		if (ClobberMove.posOK(row + 1, col - 1) && cs.board[row + 1][col - 1] == friend)
			if (cs.board[row + 1][col] == opponent || cs.board[row][col - 1] == opponent) score++;
		
		if (ClobberMove.posOK(row - 1, col + 1) && cs.board[row - 1][col + 1] == friend)
			if (cs.board[row - 1][col] == opponent || cs.board[row][col + 1] == opponent) score++;
		
		if (ClobberMove.posOK(row - 1, col - 1) && cs.board[row - 1][col - 1] == friend)
			if (cs.board[row - 1][col] == opponent || cs.board[row][col - 1] == opponent) score++;
		
		// Return the total score
		return score;
	}
	
	/**
	 * Calculates the next move to be performed by the alpha beta player
	 * 
	 * @param state		: the current game board
	 * @param lastMove	: the last move to be performed
	 * @return			: the next move to be performed
	 */
	public GameMove getMove(GameState state, String lastMove) {
		moves_taken++;
		init();
		
		alphaBeta((ClobberState)state, mvStack, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		
		return mvStack[0];
		
		/*
		List<ScoredClobberMove> allMoves = getPossibleMoves((ClobberState)state);
		
		int movesPerThread = Math.max(1, allMoves.size() / MAX_THREADS);
		AlphaBetaThread[] threads = new AlphaBetaThread[(int)(allMoves.size() / movesPerThread)];
		
		// Run each of the threads over the possible moves
		for (int i = 0; i < threads.length; i++) {
			if (i == threads.length - 1) {
				threads[i] = new AlphaBetaThread((ClobberState)state,
						allMoves.subList(i * movesPerThread, allMoves.size()));
			}
			else {
				threads[i] = new AlphaBetaThread((ClobberState)state,
						allMoves.subList(i * movesPerThread, (i + 1) * movesPerThread));
			}
			
			threads[i].start();
		}
		
		// Wait for the threads to complete
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ScoredClobberMove best = threads[0].bestMove;
		
		// Determine which move is best
		for (int i = 1; i < threads.length; i++) {
			ScoredClobberMove temp = threads[i].bestMove;
			
			if (state.who == GameState.Who.HOME && temp.score > best.score) best = temp;
			else if (temp.score < best.score) best = temp;
		}
		
		// Return the best move
		return best;
		*/
	}
	
	private class AlphaBetaThread extends Thread {
		
		private ClobberState state;					// The state of the board
		private ScoredClobberMove[] mvStack;		// Stores the best move at any depth
		private List<ScoredClobberMove> moves;		// The moves to evaluate
		public ScoredClobberMove bestMove;			// The best move
		
		/**
		 * Constructor for the AlphaBetaThread.
		 * 
		 * @param state		: the current state of the board
		 * @param moves		: a list of possible moves
		 */
		public AlphaBetaThread(ClobberState state, List<ScoredClobberMove> moves) {
			this.moves = moves;
			this.bestMove = moves.get(0);
			
			this.mvStack = new ScoredClobberMove[AlphaBeta_11_10.MAX_DEPTH];
			this.state = new ClobberState();
			
			// Initialize the move stack
			for (int i=0; i < AlphaBeta_11_10.MAX_DEPTH; i++) {
				this.mvStack[i] = new ScoredClobberMove(0, 0, 0, 0, 0);
			}
			
			// Copy the board state
			for (int row = 0; row < AlphaBeta_11_10.ROWS; row++) {
				for (int col = 0; col < AlphaBeta_11_10.COLS; col++) {
					this.state.board[row][col] = state.board[row][col];
				}
			}
			
			this.state.who = state.getWho();
			this.state.status = state.getStatus();
			this.state.numMoves = state.getNumMoves();
		}
		
		public void run() {
			for (int i = 0; i < moves.size(); i++) {
				// Make the move
				state.makeMove(moves.get(i));
				
				// Perform the recursive alpha beta search
				AlphaBeta_11_10.this.alphaBeta(state, mvStack, 0,
						Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
				
				// Undo the move
				AlphaBeta_11_10.this.undoMove(state, moves.get(i));
				
				// Update the score of the original move
				moves.get(i).score = mvStack[0].score;
				
				// Check if the move is better than the best move
				if (i == 0) bestMove = moves.get(0);
				else if (state.who == GameState.Who.HOME && 
						moves.get(i).score > bestMove.score) bestMove = moves.get(i);
				else if (state.who == GameState.Who.AWAY && 
						moves.get(i).score < bestMove.score) bestMove = moves.get(i);
			}
		}
	}
}