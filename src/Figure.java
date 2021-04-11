import java.awt.*;

/**
 * Figure defines the appearance of a gaming piece
 */
class Figure
{
	public Point shape[];
	public Image fixed[];
	public Image flying[];
	public Image preview;

	/**
	 * Standart constructor
	 */
	public Figure()
	{
	}

	/**
	 * Copy constructor
	 */
	public Figure( Figure template )
	{
		if( template.shape.length > 0 )
		{
			// copy shape because it will be altered when it gets
			// rotated
			Point shape[] = new Point[template.shape.length];

			for( int i = template.shape.length; --i >= 0; )
				shape[i] = new Point( template.shape[i] );

			this.shape = shape;
		}
		else
		{
			shape = null;
		}

		fixed = template.fixed;
		flying = template.flying;
		preview = template.preview;
	}

	/**
	 * Calculate dimensions of figure
	 */
	public Dimension calculateDimensions()
	{
		int xmin = 0, xmax = 0;
		int ymin = 0, ymax = 0;

		for( int x = 0, y = 0, i = 0; i < shape.length; i++ )
		{
			y += shape[i].y;
			if( y < ymin )
				ymin = y;
			if( y > ymax )
				ymax = y;

			x += shape[i].x;
			if( x < xmin )
				xmin = x;
			if( x > xmax )
				xmax = x;
		}

		return new Dimension( xmax-xmin, ymax-ymin );
	}
}
