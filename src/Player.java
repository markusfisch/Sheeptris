/**
 * Player instance
 */
class Player
{
	public String name;
	public KeyAction keys[];
	public Figure figures[];
	public Piece piece;
	public int score;
	public boolean alive;

	/**
	 * Reset player
	 */
	public void reset( int offset, Board board )
	{
		piece = new Piece( offset, board, figures );
		score = 0;
		alive = true;
	}
}
