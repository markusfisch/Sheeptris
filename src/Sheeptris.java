import java.applet.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A skinable multi-player Tetris game that runs as application
 * and applet
 */
public class Sheeptris extends Applet
{
	/**
	 * Command-line arguments
	 */
	private class Arguments
	{
		public String conf = null;
		public String players[] = null;
	}

	private Tetris tetris = null;

	/**
	 * Split a string into an array (for pre-1.3)
	 *
	 * @param string string to split
	 * @param seperator character
	 */
	public String[] split( String string, char seperator )
	{
		int items = 0;
		String content = "";

		string += seperator;

		for( int i = string.length(); i-- >= 0; )
		{
			if( i < 0 ||
				string.charAt( i ) == seperator )
			{
				if( content.length() > 0 ||
					items > 0 )
				{
					items++;
					content = "";
				}

				continue;
			}

			content += string.charAt( i );
		}

		if( items == 0 )
			return null;

		String array[] = new String[items];

		content = "";

		for( int i = string.length(); i-- >= 0; )
		{
			if( i < 0 ||
				string.charAt( i ) == seperator )
			{
				if( content.length() > 0 ||
					items != array.length )
				{
					array[--items] = content;
					content = "";
				}

				continue;
			}

			content = string.charAt( i )+content;
		}

		return array;
	}

	/**
	 * Load definitions from a player file
	 *
	 * @param file relative path and filename of player configuration file
	 * @param settings global settings
	 */
	public Player loadPlayer( String file, Settings settings )
	{
		// temporary class
		class ImageSet
		{
			public String name;
			public Image set[];

			public ImageSet( String name, Image set[] )
			{
				this.name = name;
				this.set = set;
			}
		}

		Player player = new Player();
		Vector keylist = new Vector();
		Vector facelist = new Vector();
		Vector figurelist = new Vector();

		try
		{
			MediaLoader ml = new MediaLoader( this );
			BufferedReader br = ml.getTextStream( file );

			{
				String name[] = split( file, '.' );

				if( name.length > 0 )
					player.name = name[0];
				else
					player.name = "Unknown";
			}

			for( String line; (line = br.readLine()) != null; )
			{
				// cut comments
				int p;
				if( (p = line.indexOf( '#' )) > -1 )
					line.substring( 0, p );

				// skip empty lines
				line = line.trim();
				if( line.length() == 0 )
					continue;

				String set[] = split( line, ':' );

				set[0] = set[0].toLowerCase().trim();

				if( set[0].startsWith( "key." ) )
				{
					String name[] = split( set[0], '.' );

					if( name.length == 2 )
						keylist.addElement( new KeyAction( name[1], set[1] ) );
				}
				else if( set[0].startsWith( "face." ) )
				{
					String name[] = split( set[0], '.' );

					if( name.length == 2 )
					{
						String imagefile[] = split( set[1], ',' );
						Image imageset[] = new Image[imagefile.length];

						for( int i = imagefile.length; i-- > 0; )
							imageset[i] = ml.getImage( imagefile[i].trim() );

						facelist.addElement( new ImageSet(
							name[1].trim().toLowerCase(), imageset ) );
					}
				}
				else if( set[0].startsWith( "figure." ) )
				{
					String param[] = split( set[1], ';' );

					if( param.length == 3 )
					{
						Figure figure = new Figure();

						param[0] = param[0].trim().toLowerCase();
						param[1] = param[1].trim().toLowerCase();

						for( Enumeration e = facelist.elements();
							e.hasMoreElements(); )
						{
							ImageSet is = (ImageSet) e.nextElement();

							if( is.name.equals( param[0] ) )
								figure.flying = is.set;
							else if( is.name.equals( param[1] ) )
								figure.fixed = is.set;
						}

						String waypoint[] = split( param[2], '|' );
						Point shape[] = new Point[waypoint.length];

						for( int i = 0; i < waypoint.length; i++ )
						{
							String point[] = split( waypoint[i], ',' );

							if( point.length != 2 )
								continue;

							shape[i] = new Point(
								Integer.parseInt( point[0].trim() ),
								Integer.parseInt( point[1].trim() ) );
						}

						figure.shape = shape;

						figurelist.addElement( figure );
					}
				}
			}

			br.close();
		}
		catch( Exception e )
		{
			System.err.println( e );
			return null;
		}

		try
		{
			MediaTracker mediaTracker = new MediaTracker( this );

			for( Enumeration e = facelist.elements();
				e.hasMoreElements(); )
			{
				ImageSet is = (ImageSet) e.nextElement();

				for( int i = is.set.length; i-- > 0; )
					mediaTracker.addImage( is.set[i], 0 );
			}

			mediaTracker.waitForID( 0 );
		}
		catch( InterruptedException e )
		{
			System.err.println( e );
			return null;
		}

		try
		{
			MediaTracker mediaTracker = new MediaTracker( this );

			// scale images if they doesn't match cell dimensions
			for( Enumeration e = facelist.elements();
				e.hasMoreElements(); )
			{
				ImageSet is = (ImageSet) e.nextElement();

				for( int i = is.set.length; i-- > 0; )
					if( is.set[i].getWidth( null ) !=
							settings.metricscell.width ||
						is.set[i].getHeight( null ) !=
							settings.metricscell.height )
					{
						is.set[i] = is.set[i].getScaledInstance(
							settings.metricscell.width,
							settings.metricscell.height,
							Image.SCALE_SMOOTH );

						mediaTracker.addImage( is.set[i], 0 );
					}
			}

			// create small tiles for preview
			for( Enumeration e = figurelist.elements();
				e.hasMoreElements(); )
			{
				Figure figure = (Figure) e.nextElement();

				if( figure.flying != null &&
					figure.flying.length > 0 )
				{
					Dimension metrics = figure.calculateDimensions();

					if( metrics.height > settings.previewheight )
						settings.previewheight = metrics.height;
					if( metrics.width > settings.previewheight )
						settings.previewheight = metrics.width;

					figure.preview = figure.flying[0].getScaledInstance(
						settings.metricscell.width>>1,
						settings.metricscell.height>>1,
						Image.SCALE_SMOOTH );

					mediaTracker.addImage( figure.preview, 0 );
				}
			}

			settings.previewheight *= settings.metricscell.height>>1;

			mediaTracker.waitForID( 0 );
		}
		catch( InterruptedException e )
		{
			System.err.println( e );
			return null;
		}

		if( !figurelist.isEmpty() )
		{
			Figure figures[] = new Figure[figurelist.size()];
			int i = 0;

			for( Enumeration e = figurelist.elements();
				e.hasMoreElements(); i++ )
				figures[i] = (Figure) e.nextElement();

			player.figures = figures;
		}

		if( !keylist.isEmpty() )
		{
			KeyAction keys[] = new KeyAction[keylist.size()];
			int i = 0;

			for( Enumeration e = keylist.elements();
				e.hasMoreElements(); i++ )
				keys[i] = (KeyAction) e.nextElement();

			player.keys = keys;
		}

		return player;
	}

	/**
	 * Load settings
	 *
	 * @param file relative path and filename of configuration file
	 */
	public Settings loadSettings( String file )
	{
		Settings settings = new Settings();

		try
		{
			MediaLoader ml = new MediaLoader( this );
			BufferedReader br = ml.getTextStream( file );

			for( String line; (line = br.readLine()) != null; )
			{
				// cut comments
				int p;
				if( (p = line.indexOf( '#' )) > -1 )
					line.substring( 0, p );

				// skip empty lines
				line = line.trim();
				if( line.length() == 0 )
					continue;

				String set[] = split( line, ':' );

				set[0] = set[0].toLowerCase().trim();

				if( set[0].equals( "metrics.board" ) )
				{
					String metrics[] = split( set[1], ',' );

					if( metrics.length == 2 )
					{
						settings.metricsboard.width =
							Integer.parseInt( metrics[0].trim() );
						settings.metricsboard.height =
							Integer.parseInt( metrics[1].trim() );
					}
				}
				else if( set[0].equals( "metrics.cell" ) )
				{
					String metrics[] = split( set[1], ',' );

					if( metrics.length == 2 )
					{
						settings.metricscell.width =
							Integer.parseInt( metrics[0].trim() );
						settings.metricscell.height =
							Integer.parseInt( metrics[1].trim() );
					}
				}
				else if( set[0].equals( "metrics.margin" ) )
				{
					String margin[] = split( set[1], ',' );

					if( margin.length == 2 )
					{
						settings.scoremargin.width =
							Integer.parseInt( margin[0].trim() );
						settings.scoremargin.height =
							Integer.parseInt( margin[1].trim() );
					}
				}
				else if( set[0].equals( "board.backdrop" ) )
					settings.boardbackdrop = ml.getImage( set[1].trim() );
				else if( set[0].equals( "sound.land" ) )
					settings.soundland = ml.getAudio( set[1].trim() );
				else if( set[0].equals( "sound.row" ) )
					settings.soundrow = ml.getAudio( set[1].trim() );
				else if( set[0].equals( "default" ) )
					settings.defaultplayer = set[1].trim();
				else if( set[0].equals( "string.gameover" ) )
					settings.stringgameover = set[1].trim();
				else if( set[0].equals( "string.typeyourname" ) )
					settings.stringtypeyourname = set[1].trim();
				else if( set[0].equals( "string.highscore" ) )
					settings.stringhighscore = set[1].trim();
				else if( set[0].startsWith( "score." ) )
				{
					String part[] = split( set[0], '.' );

					if( part.length == 2 )
					{
						int i = Integer.parseInt( part[1].trim() );

						if( i >= 0 && i <= 9 )
							settings.scorenumber[i] =
								ml.getImage( set[1].trim() );
					}
				}
				else if( set[0].startsWith( "glyph." ) )
				{
					String part[] = split( set[0], '.' );

					if( part.length == 2 )
					{
						part[1] = part[1].trim().toLowerCase();

						if( part[1].length() == 1 )
						{
							int i = part[1].charAt( 0 )-97;

							if( i >= 0 && i <= 25 )
								settings.glyph[i] =
									ml.getImage( set[1].trim() );
						}
					}
				}
			}

			br.close();
		}
		catch( Exception e )
		{
			System.err.println( e );
			return null;
		}

		try
		{
			MediaTracker mediaTracker = new MediaTracker( this );

			mediaTracker.addImage( settings.boardbackdrop, 0 );

			for( int i = settings.scorenumber.length; i-- > 0; )
				if( settings.scorenumber[i] != null )
					mediaTracker.addImage( settings.scorenumber[i], 0 );

			for( int i = settings.glyph.length; i-- > 0; )
				if( settings.glyph[i] != null )
					mediaTracker.addImage( settings.glyph[i], 0 );

			mediaTracker.waitForID( 0 );
		}
		catch( InterruptedException e )
		{
			System.err.println( e );
			return null;
		}

		return settings;
	}

	/**
	 * Create the game instance
	 *
	 * @param conf relative path and filename of global configuration file
	 * @param args relative path and filename of player configuration file(s)
	 */
	public Tetris create( String conf, String args[] )
	{
		Settings settings;

		if( conf == null )
			conf = "res/Default.conf";

		if( (settings = loadSettings( conf )) == null )
			return null;

		if( args.length == 0 )
			args = new String[]{ settings.defaultplayer };

		Player player[] = new Player[args.length];

		for( int i = args.length; i-- > 0; )
			if( (player[i] = loadPlayer( args[i], settings )) == null )
				return null;

		settings.player = player;

		if( !settings.isValid() )
			return null;

		return new Tetris( settings );
	}

	/**
	 * Initializes the game in applet mode
	 */
	public void init()
	{
		Vector player = new Vector();
		String name;

		for( int i = 0;
			(name = getParameter( "Player"+i )) != null;
			i++ )
			player.addElement( name );

		String args[] = new String[player.size()];
		int i = 0;

		for( Enumeration e = player.elements();
			e.hasMoreElements(); i++ )
			args[i] = (String) e.nextElement();

		if( (tetris = create( getParameter( "Configuration" ), args )) == null )
			return;

		// disable layout manager and set bounds manually
		setLayout( null );
		tetris.board.setBounds( 0, 0, getWidth(), getHeight() );

		add( tetris.board );
	}

	/**
	 * Starts the game in applet mode
	 */
	public void start()
	{
		if( tetris == null )
			return;

		tetris.start();
	}

	/**
	 * Stops the game in applet mode
	 */
	public void stop()
	{
		if( tetris == null )
			return;

		tetris.stop();
	}

	/**
	 * Stand-alone entry
	 *
	 * @param args command-line arguments
	 */
	public static void main( String args[] )
	{
		final Sheeptris sheeptris = new Sheeptris();
		final Arguments arguments = sheeptris.parseArguments( args );
		final Tetris tetris;

		if( (tetris = sheeptris.create(
			arguments.conf, arguments.players )) == null )
		{
			System.err.println( "Missing or invalid configuration file(s)!" );
			System.exit( -1 );
		}

		final Frame frame = new Frame( "Sheeptris" );

		frame.add( tetris.board );
		frame.pack();

		frame.setResizable( false );
		frame.addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					tetris.stop();
					System.exit( 0 );
				}
			} );

		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle bounds = frame.getBounds();

		frame.setLocation( (screen.width-bounds.width)>>1,
			(screen.height-bounds.height)>>1 );

		frame.show();
		tetris.start();
	}

	/**
	 * Parse arguments
	 *
	 * @param args argument list
	 */
	private Arguments parseArguments( String args[] )
	{
		Arguments arguments = new Arguments();
		Vector p = new Vector();

		for( int i = args.length; i-- > 0; )
			if( args[i].indexOf( ".conf" ) > -1 )
			{
				arguments.conf = args[i];
				args[i] = null;
			}
			else
				p.addElement( args[i] );

		arguments.players = new String[p.size()];

		int i = 0;

		for( Enumeration e = p.elements();
			e.hasMoreElements(); i++ )
			arguments.players[i] = (String) e.nextElement();

		return arguments;
	}
}
