import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * KeyAction binds some action to a player
 */
class KeyAction
{
	public static final int ROTATE = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int FALL = 4;
	public static final int BOTTOM = 5;

	public int action;
	public int keycode;

	/**
	 * Initialize key
	 *
	 * @param name of the action that this key should do
	 * @param key some key
	 */
	public KeyAction( String name, String key )
	{
		name = name.trim().toLowerCase();
		key = key.trim().toLowerCase();

		if( name.equals( "rotate" ) )
			action = ROTATE;
		else if( name.equals( "left" ) )
			action = LEFT;
		else if( name.equals( "right" ) )
			action = RIGHT;
		else if( name.equals( "fall" ) )
			action = FALL;
		else if( name.equals( "bottom" ) )
			action = BOTTOM;
		else
			action = 0;

		if( key.equals( "left" ) )
			keycode = KeyEvent.VK_LEFT;
		else if( key.equals( "right" ) )
			keycode = KeyEvent.VK_RIGHT;
		else if( key.equals( "up" ) )
			keycode = KeyEvent.VK_UP;
		else if( key.equals( "down" ) )
			keycode = KeyEvent.VK_DOWN;
		else if( key.equals( "space" ) )
			keycode = KeyEvent.VK_SPACE;
		else if( key.equals( "return" ) ||
			key.equals( "enter" ) )
			keycode = KeyEvent.VK_ENTER;
		else if( key.equals( "control" ) ||
			key.equals( "ctrl" ) )
			keycode = KeyEvent.VK_CONTROL;
		else if( key.equals( "alt" ) )
			keycode = KeyEvent.VK_ALT;
		else if( key.equals( "shift" ) )
			keycode = KeyEvent.VK_SHIFT;
		else if( key.equals( "0" ) )
			keycode = KeyEvent.VK_NUMPAD0;
		else if( key.equals( "1" ) )
			keycode = KeyEvent.VK_NUMPAD1;
		else if( key.equals( "2" ) )
			keycode = KeyEvent.VK_NUMPAD2;
		else if( key.equals( "3" ) )
			keycode = KeyEvent.VK_NUMPAD3;
		else if( key.equals( "4" ) )
			keycode = KeyEvent.VK_NUMPAD4;
		else if( key.equals( "5" ) )
			keycode = KeyEvent.VK_NUMPAD5;
		else if( key.equals( "6" ) )
			keycode = KeyEvent.VK_NUMPAD6;
		else if( key.equals( "7" ) )
			keycode = KeyEvent.VK_NUMPAD7;
		else if( key.equals( "8" ) )
			keycode = KeyEvent.VK_NUMPAD8;
		else if( key.equals( "9" ) )
			keycode = KeyEvent.VK_NUMPAD9;
		else
		{
			keycode = key.toUpperCase().charAt( 0 );

			if( keycode < 0x41 || keycode > 0x5A )
				keycode = 0;
		}
	}
}
