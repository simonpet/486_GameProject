
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
	public ScoredClobberMove(int r1, int c1, int r2, int c2, double score)
	{
		super(r1, c1, r2, c2);
		this.score = score;
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
	
}