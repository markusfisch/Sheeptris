import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.*;
import java.awt.*;

/**
 * This class provides a configurable multi-player tetris game
 */
class Tetris
{
	/**
	 * Update ticker
	 */
	private class Ticker extends Thread
	{
		/**
		 * Setup and start thread on creation
		 */
		public Ticker()
		{
			setPriority( Thread.MIN_PRIORITY+1 );
			start();
		}

		/**
		 * Run the ticker
		 */
		public void run()
		{
			int level = 10;
			int delay = 0;

			for( ;; )
			{
				try
				{
					sleep( 50 );
				}
				catch( InterruptedException e )
				{
					return;
				}

				if( ++delay == level )
				{
					if( level > 1 &&
						(level = 10-(board.lines/(12+level))) < 1 )
						level = 1;

					delay = 0;
					handleTickerEvent( true );
				}
				else
					handleTickerEvent( false );
			}
		}
	}

	public Board board = null;

	private Ticker ticker = null;
	private Player player[];
	private boolean paused = false;

	/**
	 * Initialize game environment
	 *
	 * @param settings the game settings
	 */
	public Tetris( Settings settings )
	{
		settings.metricsboard.width =
			settings.metricsboard.width*settings.player.length;

		board = new Board( settings );
		player = settings.player;

		board.addKeyListener(
			new KeyAdapter()
			{
				public void keyPressed( KeyEvent e )
				{
					handleKeyEvent( e );
				}
			} );
	}

	/**
	 * Start game
	 */
	public synchronized void start()
	{
		board.requestFocus();

		if( player.length == 0 )
			return;

		if( ticker == null ||
			!ticker.isAlive() )
		{
			int gap = board.settings.metricsboard.width/player.length;

			for( int i = player.length, offset = gap>>1;
				i-- > 0; offset += gap )
				player[i].reset( offset, board );

			// force board to have a gc draw() can use
			board.paint( board.getGraphics() );

			board.clear();
			board.draw();

			ticker = new Ticker();
		}

		board.repaint();
	}

	/**
	 * Stop game
	 */
	public synchronized void stop()
	{
		if( ticker == null )
			return;

		ticker.interrupt();
		ticker = null;
	}

	/**
	 * Continue game
	 */
	public synchronized void resume()
	{
		if( ticker != null )
			return;

		ticker = new Ticker();
	}

	/**
	 * Handle ticker event
	 */
	private synchronized void handleTickerEvent( boolean fall )
	{
		int active = 0;

		if( !board.ur.isEmpty() )
		{
			board.repaint( board.ur.getX(), board.ur.getY(),
				board.ur.getWidth(), board.ur.getHeight() );

			board.ur.clear();
		}

		for( int i = 0; i < player.length; i++ )
			if( player[i].alive )
			{
				if( fall &&
					player[i].piece.fall() &&
					board.overlapping( player[i].piece ) &&
					board.canmerge )
				{
					player[i].alive = false;
					continue;
				}

				active++;
			}

		if( !board.ur.isEmpty() )
		{
			board.repaint( 0, board.ur.getX(), board.ur.getY(),
				board.ur.getWidth(), board.ur.getHeight() );

			board.ur.clear();
		}

		if( active == 0 )
		{
			stop();

			Dimension d = board.getStringMetrics(
				board.settings.stringgameover );
			board.write(
				(board.metrics.width-d.width)>>1,
				(board.metrics.height/2),
				board.settings.stringgameover );

			board.repaint();
		}
	}

	/**
	 * Handle keyboard events
	 *
	 * @param e event
	 */
	private synchronized void handleKeyEvent( KeyEvent e )
	{
		int kc = e.getKeyCode();

		if( kc == KeyEvent.VK_PAUSE ||
			kc == KeyEvent.VK_ESCAPE )
		{
			if( ticker != null )
			{
				paused = true;
				stop();
			}
			else if( paused )
			{
				paused = false;
				resume();
			}

			return;
		}
		else if( kc == KeyEvent.VK_END )
		{
			stop();
			System.exit( 0 );

			return;
		}
		else if( ticker == null )
		{
			if( kc == KeyEvent.VK_SPACE ||
				kc == KeyEvent.VK_ENTER )
				start();

			// if game is inactive function returns here
			// in any case
			return;
		}

		int action = 0;
		int p;

		for( p = player.length; action == 0 && p-- > 0; )
			if( player[p].alive )
				for( int k = player[p].keys.length; k-- > 0; )
					if( player[p].keys[k].keycode == kc )
					{
						action = player[p].keys[k].action;
						break;
					}

		switch( action )
		{
			case KeyAction.ROTATE:
				if (e.isShiftDown())
					player[p].piece.turnLeft();
				else
					player[p].piece.turnRight();
				break;
			case KeyAction.LEFT:
				player[p].piece.moveLeft();
				break;
			case KeyAction.RIGHT:
				player[p].piece.moveRight();
				break;
			case KeyAction.FALL:
				player[p].piece.fall();
				break;
			case KeyAction.BOTTOM:
				player[p].piece.bottom();
				break;
		}
	}
}
