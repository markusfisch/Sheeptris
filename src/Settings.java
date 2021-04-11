import java.applet.*;
import java.awt.*;
import java.io.*;

/**
 * Settings for a tetris game
 */
class Settings
{
	/**
	 * Dimensions of the game-board in cells
	 */
	public Dimension metricsboard = new Dimension( 10, 20 );

	/**
	 * Dimensions of a cell in pixels
	 */
	public Dimension metricscell = new Dimension( 24, 24 );

	/**
	 * Backdrop image
	 */
	public Image boardbackdrop = null;

	/**
	 * Audioclip to play when figure has landed (optional)
	 */
	public AudioClip soundland = null;

	/**
	 * Audioclip to play when a row gets removed (optional)
	 */
	public AudioClip soundrow = null;

	/**
	 * Margin of the numbers to the right and upper corner
	 */
	public Dimension scoremargin = new Dimension( 4, 4 );

	/**
	 * List of images for the numbers to display scores
	 */
	public Image scorenumber[] = new Image[10];

	/**
	 * List of images for the glyphs
	 */
	public Image glyph[] = new Image[26];

	/**
	 * Configuration file of first player (mandatory !)
	 */
	public String defaultplayer;

	/**
	 * String to output when game is over
	 */
	public String stringgameover = "Game Over";

	/**
	 * String to ask the user for it's name
	 */
	public String stringtypeyourname = "Type your name";

	/**
	 * String to ask the user for it's name
	 */
	public String stringhighscore = "Highscore";

	/**
	 * Size (height) in pixels of preview area in display
	 * (automatically determined, no user setting !)
	 */
	public int previewheight = 0;

	/**
	 * List of players
	 */
	public Player player[];

	/**
	 * Returns if this settings set is valid
	 */
	public boolean isValid()
	{
		// check metrics and critical components
		if( player == null ||
			boardbackdrop == null ||
			metricsboard.width < 4 ||
			metricsboard.height < 8 ||
			metricscell.width < 4 ||
			metricscell.height < 4 ||
			metricsboard.width*metricscell.width > 1280 ||
			metricsboard.height*metricscell.height > 1024 ||
			player.length == 0 )
			return false;

		// check player data
		for( int p = player.length; p-- > 0; )
		{
			if( player[p] == null ||
				player[p].figures.length == 0 ||
				player[p].keys.length == 0 )
				return false;

			// check key bindings
			for( int k = player[p].keys.length; k-- > 0; )
				if( player[p].keys[k] == null ||
					player[p].keys[k].action == 0 ||
					player[p].keys[k].keycode == 0 )
					return false;

			// check shape definitions
			for( int s = player[p].figures.length; s-- > 0; )
			{
				if( player[p].figures[s] == null ||
					player[p].figures[s].fixed == null ||
					player[p].figures[s].fixed.length == 0 ||
					player[p].figures[s].flying == null ||
					player[p].figures[s].flying.length == 0 ||
					player[p].figures[s].shape == null ||
					player[p].figures[s].shape.length == 0 )
					return false;

				for( int i = player[p].figures[s].fixed.length; i-- > 0; )
					if( player[p].figures[s].fixed[i] == null )
						return false;
				for( int i = player[p].figures[s].flying.length; i-- > 0; )
					if( player[p].figures[s].flying[i] == null )
						return false;
			}
		}

		return true;
	}
}
