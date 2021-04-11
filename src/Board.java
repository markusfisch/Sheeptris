import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * Game board
 */
class Board extends Display
{
	/**
	 * Representation of a cell
	 */
	private class Cell
	{
		public Image face = null;
		public boolean fixed = false;
	}

	public Settings settings;
	public Region ur;
	public Cell board[][];
	public int lines;
	public boolean canmerge = false;

	private Dimension metricsnumber = null;
	private Dimension metricsspace = null;
	private Image backup = null;

	/**
	 * Construct a board with a number of columns and
	 * rows
	 *
	 * @param settings board settings
	 */
	public Board( Settings settings )
	{
		super( new Dimension(
			settings.metricsboard.width*settings.metricscell.width,
			settings.metricsboard.height*settings.metricscell.height ),
			settings.boardbackdrop,
			null,
			0,
			null );

		enableEvents( KeyEvent.KEY_EVENT_MASK );

		this.settings = settings;

		ur = new Region( settings.metricscell );
		board = new Cell[settings.metricsboard.height]
			[settings.metricsboard.width];

		for( int r = settings.metricsboard.height; r-- > 0; )
			for( int c = settings.metricsboard.width; c-- > 0; )
				board[r][c] = new Cell();

		for( int i = settings.glyph.length; i-- > 0; )
			if( settings.glyph[i] != null )
			{
				metricsspace = new Dimension(
					settings.glyph[i].getWidth( null ),
					settings.glyph[i].getHeight( null ) );

				break;
			}

		clear();
	}

	/**
	 * Determine if piece overlaps
	 *
	 * @param piece gaming piece to test
	 */
	public boolean overlapping( Piece piece )
	{
		Point p = new Point( piece.pos );

		canmerge = false;

		for( int i = 0; i < piece.figure.shape.length; i++ )
		{
			p.x += piece.figure.shape[i].x;
			p.y += piece.figure.shape[i].y;

			if( p.x < 0 || p.x >= settings.metricsboard.width )
				return true;

			if( p.y >= settings.metricsboard.height )
				return (canmerge = true);

			if( p.y < 0 )
				continue;

			if( board[p.y][p.x].face != null )
			{
				canmerge = board[p.y][p.x].fixed;

				return true;
			}
		}

		return false;
	}

	/**
	 * Copy piece into the board
	 *
	 * @param piece gaming piece to copy
	 */
	public void merge( Piece piece )
	{
		Point p = new Point( piece.pos );

		for( int i = 0; i < piece.figure.shape.length; i++ )
		{
			p.x += piece.figure.shape[i].x;
			p.y += piece.figure.shape[i].y;

			if( p.y < 0 )
				continue;

			board[p.y][p.x].face = piece.getFixedFace();
			board[p.y][p.x].fixed = true;
		}

		if( settings.soundland != null )
			settings.soundland.play();

		findFullRows();
		ur.include( 0, 0,
			settings.metricsboard.width,
			settings.metricsboard.height );

		draw();
	}

	/**
	 * Clear board
	 */
	public void clear()
	{
		lines = 0;

		for( int r = settings.metricsboard.height; r-- > 0; )
			for( int c = settings.metricsboard.width; c-- > 0; )
			{
				board[r][c].face = null;
				board[r][c].fixed = false;
			}
	}

	/**
	 * Erase a cell
	 *
	 * @param column horizontal position in cells
	 * @param row vertical position in cells
	 */
	public void eraseCell( int column, int row )
	{
		if( gc == null || row < 0 ||
			column < 0 || column >= settings.metricsboard.width )
			return;

		ur.include( column, row );
		board[row][column].face = null;

		gc.copyArea( column*settings.metricscell.width+metrics.width,
			row*settings.metricscell.height,
			settings.metricscell.width,
			settings.metricscell.height,
			-metrics.width,
			0 );
	}

	/**
	 * Draw one single cell
	 *
	 * @param column horizontal position in cells
	 * @param row vertical position in cells
	 * @param img Image object to draw
	 */
	public void drawCell( int column, int row, Image img )
	{
		if( gc == null || row < 0 ||
			column < 0 || column >= settings.metricsboard.width )
			return;

		ur.include( column, row );
		board[row][column].face = img;

		gc.drawImage( img,
			column*settings.metricscell.width,
			row*settings.metricscell.height,
			null );
	}

	/**
	 * Draw board
	 */
	public void draw()
	{
		if( gc == null )
			return;

		updateScores();

		gc.copyArea( metrics.width, 0, metrics.width, metrics.height,
			-metrics.width, 0 );

		int x = 0;
		int y = 0;

		for( int r = 0; r < settings.metricsboard.height;
			r++, x = 0, y+=settings.metricscell.height )
			for( int c = 0; c < settings.metricsboard.width;
				c++, x+=settings.metricscell.width )
				if( board[r][c].face != null )
					gc.drawImage( board[r][c].face, x, y, null );
	}

	/**
	 * Return dimensions in pixels of string
	 *
	 * @param str string
	 */
	public Dimension getStringMetrics( String str )
	{
		Dimension metrics = new Dimension( 0, metricsspace.height );

		str = str.toLowerCase();

		for( int i = 0; i < str.length(); i++ )
		{
			int n = str.charAt( i )-97;

			if( n >= 0 && n < settings.glyph.length &&
				settings.glyph[n] != null )
			{
				metrics.width += settings.glyph[n].getWidth( null );

				if( settings.glyph[n].getHeight( null ) > metrics.height )
					metrics.height = settings.glyph[n].getHeight( null );
			}
			else if( metricsspace != null )
				metrics.width += metricsspace.width;
		}

		return metrics;
	}

	/**
	 * Write some text
	 *
	 * @param x left begin coordinate
	 * @param y upper begin coordinate
	 * @param str string to write
	 */
	public void write( int x, int y, String str )
	{
		str = str.toLowerCase();

		for( int i = 0; i < str.length(); i++ )
		{
			int n = str.charAt( i )-97;

			if( n >= 0 && n < settings.glyph.length &&
				settings.glyph[n] != null )
			{
				gc.drawImage( settings.glyph[n],
					x, y, null );

				x += settings.glyph[n].getWidth( null );
			}
			else if( metricsspace != null )
				x += metricsspace.width;
		}
	}

	/**
	 * Remove a row
	 *
	 * @param r row to remove
	 */
	private void removeRow( int r )
	{
		for( ; r > 0; r-- )
			for( int c = settings.metricsboard.width; c-- > 0; )
				if( board[r-1][c].fixed ||
					(board[r-1][c].face == null &&
					board[r][c].fixed) )
				{
					board[r][c].face = board[r-1][c].face;
					board[r][c].fixed = board[r-1][c].fixed;
				}

		// always clear first line
		for( int c = settings.metricsboard.width; c-- > 0; )
			if( board[0][c].fixed )
			{
				board[0][c].face = null;
				board[0][c].fixed = false;
			}

		lines++;

		if( settings.soundrow != null )
			settings.soundrow.play();
	}

	/**
	 * Check board for full rows
	 */
	private boolean findFullRows()
	{
		boolean removed = false;

		for( int r = settings.metricsboard.height; r-- > 0; )
		{
			int c = settings.metricsboard.width;

			for( ; c-- > 0; )
				if( !board[r][c].fixed )
					break;

			if( c == -1 )
			{
				removeRow( r++ );
				removed = true;
			}
		}

		return removed;
	}

	/**
	 * Update scores & preview
	 */
	private void updateScores()
	{
		if( metricsnumber == null )
		{
			if( (metricsnumber = new Dimension( 0, 0 )) == null )
				return;

			for( int i = settings.scorenumber.length; i-- > 0; )
			{
				int w = settings.scorenumber[i].getWidth( null );
				int h = settings.scorenumber[i].getHeight( null );

				if( w > metricsnumber.width )
					metricsnumber.width = w;
				if( h > metricsnumber.height )
					metricsnumber.height = h;
			}

			if( metricsnumber.height > settings.previewheight )
				settings.previewheight = metricsnumber.height;
		}

		if( backup == null )
		{
			if( (backup = createImage(
				metrics.width,
				settings.previewheight )) == null )
				return;

			Graphics bgc = backup.getGraphics();

			bgc.drawImage( buffer, -metrics.width, 0, null );
		}

		int border = metrics.width<<1;
		int x = border-settings.scoremargin.width;
		int y = settings.scoremargin.height;

		gc.drawImage( backup, metrics.width, 0, null );

		String str = String.valueOf( lines );

		for( int i = str.length(); i-- > 0; )
		{
			int n = str.charAt( i )-48;

			if( n >= 0 && n < settings.scorenumber.length &&
				settings.scorenumber[n] != null )
			{
				x -= settings.scorenumber[n].getWidth( null );

				gc.drawImage( settings.scorenumber[n],
					x, y, null );
			}
		}

		x = metrics.width+settings.scoremargin.width;
		int width = metrics.width/settings.player.length;

		for( int player = settings.player.length;
			--player >= 0;
			x += width )
			settings.player[player].piece.drawPreview(
				gc, x, settings.scoremargin.height );
	}
}
