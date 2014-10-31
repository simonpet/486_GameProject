
package clobber;
import java.util.StringTokenizer;

public class ScoredClobberMove extends ClobberMove
{
	public double score;
	public ScoredClobberMove()
	{
		super();
	}
	public ScoredClobberMove(ScoredClobberMove m)
	{
		super(m);
		this.score = m.score;
	}
	public ScoredClobberMove(ClobberMove m)
	{
		super(m);
	}
	public ScoredClobberMove(ClobberMove m, double score)
	{
		super(m);
		this.score = score;
	}
	public ScoredClobberMove(int r1, int c1, int r2, int c2, double score)
	{
		super(r1, c1, r2, c2);
		this.score = score;
	}
	public ClobberMove reverse()
	{
		return new ClobberMove(row2, col2, row1, col1);
	}
	public Object clone()
	{ return new ScoredClobberMove(row1, col1, row2, col2, score); }
	public String toString()
	{
		return row1 + " " + col1 + " " + row2 + " " + col2 + " " + score;
	}
	public void parseMove(String s)
	{
		StringTokenizer toks = new StringTokenizer(s);
		row1 = Integer.parseInt(toks.nextToken());
		col1 = Integer.parseInt(toks.nextToken());
		row2 = Integer.parseInt(toks.nextToken());
		col2 = Integer.parseInt(toks.nextToken());
		score = Double.parseDouble(toks.nextToken());
	}
	public void set(int row1, int col1, int row2, int col2, double score)
	{
		this.row1 = row1;
		this.col1 = col1;
		this.row2 = row2;
		this.col2 = col2;
		this.score = score;
	}
	public void set(ClobberMove move, double score)
	{
		this.row1 = move.row1;
		this.col1 = move.col1;
		this.row2 = move.row2;
		this.col2 = move.col2;
		this.score = score;
	}
	
}