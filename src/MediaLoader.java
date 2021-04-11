import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;

/**
 * This class provides functions to load images, sounds or text from
 * a JAR, over http or the local file-system
 */
class MediaLoader
{
	Applet applet;

	/**
	 * Create a new media loader
	 *
	 * @param applet Applet object
	 */
	public MediaLoader( Applet applet )
	{
		if( applet == null )
			throw new NullPointerException();

		this.applet = applet;
	}

	/**
	 * Load image
	 *
	 * @param file relative path and filename of image file
	 */
	public Image getImage( String file )
	{
		return Toolkit.getDefaultToolkit().createImage(
			applet.getClass().getResource( file ) );
	}

	/**
	 * Load audio clip
	 *
	 * @param file relative path and filename of audio file
	 */
	public AudioClip getAudio( String file )
	{
		return Applet.newAudioClip(
			applet.getClass().getResource( file ) );
	}

	/**
	 * Load a text file
	 *
	 * @param file relative path and filename of audio file
	 */
	public BufferedReader getTextStream( String file )
	{
		try
		{
			InputStream is =
				applet.getClass().getResourceAsStream( file );

			BufferedReader br = new BufferedReader(
				new InputStreamReader( is ) );

			return br;
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
