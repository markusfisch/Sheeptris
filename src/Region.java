import java.awt.*;

/**
 * Region represents a screen region that is defined by the upper left corner
 * and the lower right, this kind of definition is better suited for many
 * updates than a Rectangle object
 */
class Region
{
	public int x1;
	public int y1;
	public int x2;
	public int y2;

	private Dimension metricscell;

	/**
	 * Initialize region with metricscell for transformation
	 *
	 * @param metricscell dimensions of a cell
	 */
	public Region( Dimension metricscell )
	{
		this.metricscell = metricscell;
		clear();
	}

	/**
	 * Clear region
	 */
	public void clear()
	{
		x1 = y1 = 0xffffff;
		x2 = y2 = 0;
	}

	/**
	 * Expand region to include point
	 *
	 * @param x horizontal position
	 * @param y vertical position
	 */
	public void include( int x, int y )
	{
		if( x < x1 )
			x1 = x;
		if( x > x2 )
			x2 = x;
		if( y < y1 )
			y1 = y;
		if( y > y2 )
			y2 = y;
	}

	/**
	 * Expand region to include rectangle
	 *
	 * @param x horizontal position of left corner
	 * @param y vertical position of top corner
	 * @param width width of rectangle
	 * @param height height of rectangle
	 */
	public void include( int x, int y, int width, int height )
	{
		if( x < x1 )
			x1 = x;
		if( (x += width) > x2 )
			x2 = x;
		if( y < y1 )
			y1 = y;
		if( (y += height) > y2 )
			y2 = y;
	}

	/**
	 * Return horizontal starting position
	 */
	public int getX()
	{
		return x1*metricscell.width;
	}

	/**
	 * Return vertical starting position
	 */
	public int getY()
	{
		return y1*metricscell.height;
	}

	/**
	 * Return width
	 */
	public int getWidth()
	{
		return ((x2-x1)+1)*metricscell.width;
	}

	/**
	 * Return height
	 */
	public int getHeight()
	{
		return ((y2-y1)+1)*metricscell.height;
	}

	/**
	 * Returns if rectangle is empty or not
	 */
	public boolean isEmpty()
	{
		if( x1 < 0xffffff )
			return false;

		return true;
	}
}
