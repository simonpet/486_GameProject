package clobber;
import game.*;

import java.util.*;


public class AlphaBetaPlayer extends GamePlayer {
	int turns = 0;
	public AlphaBetaPlayer(String n) 
	{
		super(n, new ClobberState(), false);
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		ClobberState board = (ClobberState)state;
		ArrayList<ClobberMove> list = new ArrayList<ClobberMove>();  
		ClobberMove mv = new ClobberMove();
		if (turns < 60)
		{
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
		}
		else
		{
			//TODO alphabeta
			throw new IllegalArgumentException();
		}
		turns++;
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
	public static void main(String [] args)
	{
		GamePlayer p = new RandomClobberPlayer("AI");
		p.compete(args, 1);
	}
}
