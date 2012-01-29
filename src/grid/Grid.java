package grid;

import io.Listener;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Information;

/**
 * Represents the visible grid on the screen.
 * This class is responsible for the location and zoom of the viewing area on the screen.
 * Also contained within the class is the primary Map object which holds information regarding the living cells in the grid.
 * The grid handles mouse presses and drags for the Map, using them as triggers to create or destroy cells in the map.
 * 
 * @author Dominic
 */
public class Grid implements Runnable
{
	private BufferedImage aliveImage;
	
	private boolean dragging = false;						// true if and only if the left mouse button is pressed when the mouse is being dragged
	private boolean creating = true;						// true if tiles are being made alive when the mouse is dragged, false otherwise
	private boolean up = false;								// true if the up key is currently held
	private boolean down = false;							// true if the down key is currently held
	private boolean right = false;							// true if the right key is currently held
	private boolean left = false;							// true if the left key is currently held
	private boolean shift = false;							// true if the shift key is currently held
	private boolean plus = false;							// true if the plus key is currently held
	private boolean minus = false;							// true if the minus key is currently held
	
	private static Color green = new Color(0, 215, 10);		// the green color used for alive tiles when zoomed out enough
	private static Color gray = new Color(50, 50, 50);		// the gray color used for the divider bars between tiles
	
	public double xLoc;										// the viewing distance from the origin of 0,0 in tiles
	public double yLoc;										// the viewing distance from the origin of 0,0 in tiles
	public static final double MOVE_SPEED = 0.6;			// the speed at which movement occurs in px/ms [movement in 1 ms = MOVE_SPEED/zoom]
	public double zoom;										// the size of a single tile on the screen
	public static final double MAX_ZOOM = 75;				// maximum zoom - the largest allowable tile size
	public static final double MIN_ZOOM = 1;				// minimum zoom - the smallest allowable tile size
	public static final double GRID_ZOOM = 5;				// minimum zoom for the grid lines to appear at all on the screen
	public static final double FADE_START = 25;				// the largest zoom for which grid fading begins.
	public static final double ZOOM_SPEED_KEY = 0.003;		// the speed of zooming per millisecond for which an appropriate key is held in px/ms 
															// [zoom in 1 ms = ZOOM_SPEED_KEY*zoom]
	public static final double ZOOM_SPEED_MOUSE = 0.1;		// the speed of zooming per mouse wheel scroll, adjusted by zoom
	
	private Information info;
	
	public static long period = 10;
	private long lastUpdate = 0;
	
	public Map map;
	
	private Point lastDrag = new Point();
	
	private Robot bot;
	
	public Thread mapThread;
	
	/**
	 * Creates a new Grid object with the given Information.
	 * Member fields such as the Map and locations are initialized and
	 *  requests are made to the listener for method calls upon certain events.
	 * 
	 * @param info - the current Information
	 */
	public Grid(Information info)
	{
		this.info = info;
		map = new Map(info);
		aliveImage = info.imageLoader.get("alive").getBufferedImage();
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseReleased", Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseDragged", Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "keyPressed", Listener.TYPE_KEY_PRESSED, Listener.CODE_KEY_ALL);
		info.listener.requestNotification(this, "keyReleased", Listener.TYPE_KEY_RELEASED, Listener.CODE_KEY_ALL);
		info.listener.requestNotification(this, "mouseWheel", Listener.TYPE_MOUSE_WHEEL, Listener.CODE_SCROLL_BOTH);
		zoom = 20;
		xLoc = 0;
		yLoc = 0;
		
		try
		{
			bot = new Robot();
		}
		catch (AWTException ex)
		{
			ex.printStackTrace();
			bot = null;
		}
		
		mapThread = new Thread(map);
		mapThread.start();
	}
	
	/**
	 * Runs the Grid in its own Thread.
	 * The update method is continuously called, sleeping by the preset period in between.
	 */
	public void run()
	{
		lastUpdate = System.nanoTime();
		while (true)
		{
			update();
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException ex) { }
		}
	}
	
	/**
	 * Updates the Grid by adjusting the location and zoom based on the state of keys.
	 * The arrow keys are used to move the viewing area on the screen and the minus and plus (or equals) keys are used to zoom.
	 * Alternatively, if the shift key is held, the up and down arrows are used to zoom.
	 * Because the location of the viewing area is stored in cells rather than pixels,
	 *  the change in location equals the time elapsed since the last update times the movement speed,
	 *  converted to a tile value from a pixel value.
	 * Similarly, the zooming is shifted by zoom so that zooming does not appear to 'slow down' as the user zooms in.
	 * This effect would occur because, while the zoom amount is changing by the same amount,
	 *  the cells appear to be getting larger at a smaller rate when they are already large.
	 * Thus, the zoom amount equals the amount to zoom per millisecond times the time since the last update, then multiplied by the zoom value.
	 * This will effectively make zooming look smoother because zooming speeds up as zoom gets larger.
	 */
	public void update()
	{
		long time = (System.nanoTime() - lastUpdate)/1000000;			// time since the last update in milliseconds
		if (shift)
		{
			if (up && !down)
			{
				// zoom in = tiles become bigger -> zoom becomes larger
				zoom(ZOOM_SPEED_KEY*time*zoom);
			}
			else if (down && !up)
			{
				// zoom out = tiles become smaller -> zoom becomes smaller
				zoom(-ZOOM_SPEED_KEY*time*zoom);
			}
		}
		else
		{
			if (up && !down)
			{
				yLoc -= toTile(MOVE_SPEED*time);
			}
			else if (down && !up)
			{
				yLoc += toTile(MOVE_SPEED*time);
			}
			if (right && !left)
			{
				xLoc += toTile(MOVE_SPEED*time);
			}
			else if (left && !right)
			{
				xLoc -= toTile(MOVE_SPEED*time);
			}
			if (plus && !minus)
			{
				// zoom in = tiles become bigger -> zoom becomes larger
				zoom(ZOOM_SPEED_KEY*time*zoom);
			}
			else if (minus && !plus)
			{
				// zoom out = tiles become smaller -> zoom becomes smaller
				zoom(-ZOOM_SPEED_KEY*time*zoom);
			}
		}
		lastUpdate = System.nanoTime();
	}
	
	/**
	 * Zooms by the given amount.
	 * The zoom value, equal to the size of the cells on the screen,
	 *  is adjusted by the given amount, which means that zooming will appear slower when zoomed in.
	 * The zoom is kept between the minimum and maximum zoom amounts.
	 * Finally, the x- and y-locations are adjusted so that the grid zooms in around the cursor location.
	 * That is, if the cursor is over a certain tile before zooming, its location over that tile will be maintained.
	 * 
	 * @param amount - the amount by which to adjust the zoom value
	 */
	public synchronized void zoom(double amount)
	{
		double prevZoom = zoom;
		zoom += amount;
		
		if (zoom > MAX_ZOOM)
		{
			zoom = MAX_ZOOM;
		}
		else if (zoom < MIN_ZOOM)
		{
			zoom = MIN_ZOOM;
		}
		
		xLoc -= ((info.screen.width - (zoom/prevZoom)*info.screen.width)/zoom) * info.mouse.x/info.screen.width;
		yLoc -= ((info.screen.height - (zoom/prevZoom)*info.screen.height)/zoom) * info.mouse.y/info.screen.height;
	}
	
	/**
	 * Called when any mouse button is pressed.
	 * First, the event is checked to make sure that it originated from a left mouse click. 
	 * First, the event is checked to see if any overlying layers would process it (i.e. the control bar, toolbar, and selector).
	 * If shift is not being held, the cell clicked on is switched (alive if dead, dead if alive).
	 * 
	 * @param e - the MouseEvent triggering this call
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!info.controlBar.consumed(e) && !info.toolbar.consumed(e) && !info.toolbar.selector.consumed(e))
		{
			if (!shift)
			{
				Point mouseTile = getMouseTile();
				if (map.get(mouseTile.x, mouseTile.y))
				{
					// tile is alive
					map.set(false, mouseTile.x, mouseTile.y);
					creating = false;
				}
				else
				{
					// tile is dead
					map.set(true, mouseTile.x, mouseTile.y);
					creating = true;
				}
			}
			dragging = true;
		}
		
		lastDrag = e.getLocationOnScreen();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		dragging = false;
	}
	
	/**
	 * Called when the mouse is held and dragged.
	 * The event is checked to see if it is not consumed by another object
	 *  and the left mouse button is being dragged (confirmed with the dragging flag set only if the left mouse button is currently held).
	 * If this is the case and shift is not being held, cells that have been dragged over by the mouse are created or destroyed.
	 * If shift is held, the mouse is used to drag the viewing area around and the x- and y-locations are adjusted accordingly.
	 * 
	 * @param e - the MouseEvent that triggered this method call
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!info.controlBar.consumed(e) && !info.toolbar.consumed(e) && !info.toolbar.selector.consumed(e))
		{
			if (dragging)
			{
				if (!shift)
				{
					Point mouseTile = getMouseTile();
					map.set(creating, mouseTile.x, mouseTile.y);
				}
				else
				{
					xLoc += toTile(lastDrag.x - e.getLocationOnScreen().x);
					yLoc += toTile(lastDrag.y - e.getLocationOnScreen().y);
				}
			}
		}
		lastDrag = e.getLocationOnScreen();
	}
	
	/**
	 * Called when a key is pressed.
	 * If the control key is not held, the appropriate key flag is set to true.
	 * If control is held, the Robot is used to move the mouse and create a cell in the direction given by the key event.
	 * This feature gives the user the ability to easily draw straight lines and count distances.
	 * 
	 * @param e - the KeyEvent that triggered this method call
	 */
	public void keyPressed(KeyEvent e)
	{
		if (!e.isControlDown())
		{
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				up = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				down = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				right = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			{
				left = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			{
				shift = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
			{
				plus = true;
			}
			else if (e.getKeyCode() == KeyEvent.VK_MINUS)
			{
				minus = true;
			}
		}
		else
		{
			if (bot != null)
			{
				if (e.getKeyCode() == KeyEvent.VK_UP)
				{
					bot.mouseMove(info.mouse.x, (int)(info.mouse.y - zoom));
					Point mouseTile = getMouseTile();
					mouseTile.y--;
					map.set(true, mouseTile.x, mouseTile.y);
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					bot.mouseMove(info.mouse.x, (int)(info.mouse.y + zoom));
					Point mouseTile = getMouseTile();
					mouseTile.y++;
					map.set(true, mouseTile.x, mouseTile.y);
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					bot.mouseMove((int)(info.mouse.x + zoom), info.mouse.y);
					Point mouseTile = getMouseTile();
					mouseTile.x++;
					map.set(true, mouseTile.x, mouseTile.y);
				}
				else if (e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					bot.mouseMove((int)(info.mouse.x - zoom), info.mouse.y);
					Point mouseTile = getMouseTile();
					mouseTile.x--;
					map.set(true, mouseTile.x, mouseTile.y);
				}
			}
		}
	}
	
	/**
	 * Called when a key is released.
	 * This method releases the appropriate trigger, provided that the control key is not held down.
	 * 
	 * @param e - the KeyEvent that triggered this method call
	 */
	public void keyReleased(KeyEvent e)
	{
		if (!e.isControlDown())
		{
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				up = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				down = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			{
				right = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			{
				left = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			{
				shift = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
			{
				plus = false;
			}
			else if (e.getKeyCode() == KeyEvent.VK_MINUS)
			{
				minus = false;
			}
		}
	}
	
	/**
	 * Called when the mouse wheel is rotated.
	 * This method uses the direction and amount of the the given event to determine the amount to zoom.
	 * Additionally, the zoom is used to scale the zoom amount so that zooming appears to be even throughout its range.
	 * 
	 * @param e - the MouseWheelEvent that triggered this method call
	 */
	public void mouseWheel(MouseWheelEvent e)
	{
		zoom(-ZOOM_SPEED_MOUSE*zoom*e.getWheelRotation());
	}
	
	/**
	 * Returns true if and only if the left mouse button is currently held over the grid.
	 * 
	 * @return dragging - true if the mouse is being dragged, false otherwise
	 */
	public boolean isDragging()
	{
		return dragging;
	}
	
	/**
	 * Returns true if and only if the mouse is currently "creating" living cells.
	 * 
	 * @return creating - true if the mouse is currently used to create cells, false otherwise
	 */
	public boolean isCreating()
	{
		return creating;
	}
	
	/**
	 * Returns the Point at which the left mouse button was last clicked or dragged, in pixels.
	 * 
	 * @return lastDrag - the on-screen coordinates of the mouse's last press or drag
	 */
	public Point getLastDrag()
	{
		return lastDrag;
	}
	
	/**
	 * Converts the given value in the pixel coordinate system to the correct coordinate in the corresponding tile system.
	 * This method simply divides the given pixel value by the zoom to find the tile value.
	 * 
	 * @param pixel - the distance measured in pixels
	 * @return tile - the distance measured in tiles
	 */
	public double toTile(double pixel)
	{
		return pixel/zoom;
	}
	
	/**
	 * Converts the given value in the pixel coordinate system to the correct coordinate in the corresponding pixel system.
	 * This method simply multiplies the given tile value by the zoom to find the correct pixel value.
	 * 
	 * @param tile - the distance measured in tiles
	 * @return pixel - the distance measured in pixels
	 */
	public double toPixel(double tile)
	{
		return tile*zoom;
	}
	
	/**
	 * Gets the tile that the mouse is currently hovering over.
	 * 
	 * @return mouseTile - the tile coordinates of the mouse
	 */
	public Point getMouseTile()
	{
		return new Point(roundToward0(info.mouse.x/zoom + xLoc), roundToward0(info.mouse.y/zoom + yLoc));
	}
	
	/**
	 * Rounds the given double toward 0.
	 * If the double is greater than 0, it is casted to an integer and returned.
	 * If the double is less than 0, it is casted to an integer, decremented, and returned.
	 * Otherwise, 0 is returned.
	 * 
	 * @param a - the double to be rounded toward 0
	 * @return rounded - a rounded integer value whose absolute value is the greatest integer less than or equal to the absolute value of a
	 */
	private static int roundToward0(double a)
	{
		if (a > 0)
		{
			return (int)a;
		}
		else if (a < 0)
		{
			return (int)a - 1;
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * Returns the "decimal" part of the given double. That is, the value returned is in the interval [0,1] such that (int)d + getDecimal(d) = d.
	 * 
	 * @param d - the double of which the decimal is to be returned
	 * @return decimal - the decimal portion of the given value
	 */
	private static double getDecimal(double d)
	{
		return d - Math.floor(d);
	}
	
	public synchronized void draw(Graphics2D g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, info.screen.width, info.screen.height);
		ArrayList<Point> alive = map.getLiving();
		if (zoom <= 15)
		{
			g.setColor(green);
			for (int i = 0; i < alive.size(); i++)
			{
				if (alive.get(i).x >= xLoc - 1 && alive.get(i).y >= yLoc - 1
						&& alive.get(i).x < xLoc + toTile(info.screen.width) && alive.get(i).y < yLoc + toTile(info.screen.height))
				{
					boolean xShift = alive.get(i).x - xLoc > 0;
					boolean yShift = alive.get(i).y - xLoc > 0;
					g.fillRect((int)toPixel(alive.get(i).x - xLoc) + (xShift ? 1 : 0),
							(int)toPixel(alive.get(i).y - yLoc) + (yShift ? 1 : 0), (int)zoom, (int)zoom);
				}
			}
		}
		else
		{
			for (int i = 0; i < alive.size(); i++)
			{
				if (alive.get(i).x >= xLoc - 1 && alive.get(i).y >= yLoc - 1
						&& alive.get(i).x < xLoc + toTile(info.screen.width) && alive.get(i).y < yLoc + toTile(info.screen.height))
				{
					boolean xShift = alive.get(i).x - xLoc > 0;
					boolean yShift = alive.get(i).y - xLoc > 0;
					g.drawImage(aliveImage, (int)toPixel(alive.get(i).x - xLoc) + (xShift ? 1 : 0),
							(int)toPixel(alive.get(i).y - yLoc) + (yShift ? 1 : 0), (int)zoom, (int)zoom, null);
				}
			}
		}
		if (zoom > GRID_ZOOM)
		{
			if (zoom < FADE_START)
			{
				g.setColor(new Color(gray.getRed(), gray.getGreen(), gray.getBlue(), (int)(255*(zoom/FADE_START))));
			}
			else
			{
				g.setColor(gray);
			}
			for (double x = -getDecimal(xLoc); x < info.screen.width/zoom + 1; x++)
			{
				g.drawLine((int)toPixel(x), 0, (int)toPixel(x), info.screen.height);
			}
			for (double y = -getDecimal(yLoc); y < info.screen.height/zoom + 1; y++)
			{
				g.drawLine(0, (int)toPixel(y), info.screen.width, (int)toPixel(y));
			}
		}
	}
}
