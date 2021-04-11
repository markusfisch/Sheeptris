import java.awt.*;

/**
 * Display provides a basic framework for showing information and
 * needs to subclassed in order to perform some useful operation
 */
class Display extends Canvas
{
	protected Dimension metrics;
	protected Graphics gc = null;
	protected Image buffer = null;
	protected FontMetrics fm;

	private Image backdrop;
	private String fontname;
	private int fontsize;
	private Color fontcolor;

	/**
	 * Initialize display by providing the displays properties
	 *
	 * @param metrics the dimensions of the component (fixed size)
	 * @param backdrop background image
	 * @param fontname name of the font to use when writing text
	 * @param fontsize size of the font
	 * @param fontcolor color of the font
	 */
	public Display( Dimension metrics, Image backdrop,
		String fontname, int fontsize, Color fontcolor )
	{
		this.metrics = metrics;
		this.backdrop = backdrop;
		this.fontname = fontname;
		this.fontsize = fontsize;
		this.fontcolor = fontcolor;
	}

	/**
	 * Returns dimensions of this component
	 */
	public Dimension getPreferredSize()
	{
		return metrics;
	}

	/**
	 * Returns the minimum size
	 */
	public Dimension getMinimumSize()
	{
		return getPreferredSize();
	}

	/**
	 * Returns the maximum size
	 */
	public Dimension getMaximumSize()
	{
		return getPreferredSize();
	}

	/**
	 * Draw surface
	 *
	 * @param g the graphics context to use
	 */
	public void update( Graphics g )
	{
		if( buffer == null )
		{
			if( (buffer = createImage(
				metrics.width<<1, metrics.height )) == null )
				return;

			gc = buffer.getGraphics();
			if( fontname != null )
			{
				gc.setFont( new Font( fontname, Font.PLAIN, fontsize ) );
				gc.setColor( fontcolor );
				fm = gc.getFontMetrics();
			}

			int height = backdrop.getHeight( this );
			int width = backdrop.getWidth( this );

			if( height <= 0 && width <= 0 )
				return;

			for( int y = 0; y < metrics.height; y += height )
				for( int x = 0; x < metrics.width; x += height )
					gc.drawImage( backdrop, x, y, null );

			gc.copyArea( 0, 0, metrics.width, metrics.height,
				metrics.width, 0 );
			gc.copyArea( 0, 0, metrics.width, metrics.height,
				metrics.width<<1, 0 );
		}

		g.drawImage( buffer, 0, 0, null );
	}

	/**
	 * Repaint surface by updating
	 *
	 * @param g the graphics context to use
	 */
	public void paint( Graphics g )
	{
		update( g );
	}
}
