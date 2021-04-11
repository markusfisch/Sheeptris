import java.awt.*;

/**
 * The gaming piece
 */
class Piece
{
	public Figure figure;
	public Point pos;
	public int offset;
	public Dimension metrics;

	private Board board;
	private Figure figures[];
	private int next;
	private int flyingface;

	/**
	 * Construct piece
	 *
	 * @param offset horizontal offset position of piece
	 * @param board the board object to wich this piece is attached to
	 * @param figures the array of appearances this piece can take
	 */
	public Piece( int offset, Board board, Figure figures[] )
	{
		this.offset = offset;
		this.board = board;
		this.figures = figures;

		flyingface = 0;

		pos = new Point();

		selectFigure( diceFigure() );
		next = diceFigure();
	}

	/**
	 * Move figure left
	 */
	public void moveLeft()
	{
		erase();

		pos.x--;

		if( board.overlapping( this ) )
			pos.x++;

		draw();
	}

	/**
	 * Move figure right
	 */
	public void moveRight()
	{
		erase();

		pos.x++;

		if( board.overlapping( this ) )
			pos.x--;

		draw();
	}

	/**
	 * Move figure down
	 */
	public boolean fall()
	{
		erase();

		pos.y++;

		if( board.overlapping( this ) )
		{
			pos.y--;
			if( board.canmerge )
			{
				// save the next figure because in merge() the
				// preceeding figure after the next one is needed
				int now = next;

				next = diceFigure();
				board.merge( this );
				selectFigure( now );

				return true;
			}
			else
				draw();

			return true;
		}

		draw();

		return false;
	}

	/**
	 * Move figure to the bottom
	 */
	public void bottom()
	{
		while( !fall() );
	}

	/**
	 * Turn figure counter-clockwise
	 */
	public void turnLeft()
	{
		erase();

		turnCounterClockwise();
		if( board.overlapping( this ) )
			turnClockwise();

		draw();
	}

	/**
	 * Turn figure clockwise
	 */
	public void turnRight()
	{
		erase();

		turnClockwise();
		if( board.overlapping( this ) )
			turnCounterClockwise();

		draw();
	}

	/**
	 * Erase figure from view
	 */
	public void erase()
	{
		Point p = new Point( pos );

		for( int i = 0; i < figure.shape.length; i++ )
		{
			p.x += figure.shape[i].x;
			p.y += figure.shape[i].y;

			board.eraseCell( p.x, p.y );
		}
	}

	/**
	 * Draw figure
	 */
	public void draw()
	{
		Point p = new Point( pos );

		for( int i = 0; i < figure.shape.length; i++ )
		{
			p.x += figure.shape[i].x;
			p.y += figure.shape[i].y;

			board.drawCell( p.x, p.y, getFlyingFace() );
		}
	}

	/**
	 * Draw preview
	 *
	 * @param gc target Graphics object
	 * @param x horizontal position
	 * @param y vertical position
	 */
	public void drawPreview( Graphics gc, int x, int y )
	{
		Point shape[] = figures[next].shape;
		Point p = new Point( 0, 0 );
		Point mod = new Point( 0, 0 );
		int width = board.settings.metricscell.width>>1;
		int height = board.settings.metricscell.height>>1;

		for( int i = 0; i < shape.length; i++ )
		{
			p.x += shape[i].x;
			p.y += shape[i].y;

			if( p.x < 0 )
				mod.x = p.x;
			if( p.y < 0 )
				mod.y = p.y;
		}

		p.x = -mod.x;
		p.y = -mod.y;

		for( int i = 0; i < shape.length; i++ )
		{
			p.x += shape[i].x;
			p.y += shape[i].y;

			gc.drawImage( figure.preview,
				x+p.x*width,
				y+p.y*height,
				null );
		}
	}

	/**
	 * Turn figure counter-clockwise without any checks
	 */
	private void turnCounterClockwise()
	{
		for( int i = 0; i < figure.shape.length; i++ )
		{
			int tmp = figure.shape[i].y;

			figure.shape[i].y = figure.shape[i].x;
			figure.shape[i].x = -tmp;
		}
	}

	/**
	 * Turn figure clockwise without any checks
	 */
	public void turnClockwise()
	{
		for( int i = 0; i < figure.shape.length; i++ )
		{
			int tmp = figure.shape[i].y;

			figure.shape[i].y = - figure.shape[i].x;
			figure.shape[i].x = tmp;
		}
	}

	/**
	 * Return fixed face, this image set contains different apperances
	 * of a fixed face that doesn't depend on anything but random
	 */
	public Image getFixedFace()
	{
		return figure.fixed[(int)(Math.random()*figure.fixed.length)];
	}

	/**
	 * Return flying face, because this is image set is most likely
	 * some kind of animation the images are returned in the order
	 * of appearance
	 */
	public Image getFlyingFace()
	{
		if( ++flyingface >= figure.flying.length )
			flyingface = 0;

		return figure.flying[flyingface];
	}

	/**
	 * Dice next figure
	 */
	private int diceFigure()
	{
		return (int) (Math.random()*figures.length);
	}

	/**
	 * Initialize piece with a new figure
	 *
	 * @param index index of the figure to select
	 */
	private void selectFigure( int index )
	{
		figure = new Figure( figures[index] );

		for( int orient = (int) (Math.random()*3);
			orient-- > 0; turnClockwise() );

		metrics = figures[index].calculateDimensions();

		pos.x = offset;
		pos.y = -(metrics.height>>1);
	}
}
