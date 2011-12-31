package main;

import io.Listener;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Contains the Window that the Game of Life runs within, along with information regarding the zoom, viewing pane, and so on.
 * 
 * @author Dominic
 */
public class Grid implements Runnable
{
	private boolean madeAlive = true;
	public boolean leftHeld = false;
	public boolean rightHeld = false;
	public boolean upHeld = false;
	public boolean downHeld = false;
	public boolean minusHeld = false;
	public boolean plusHeld = false;
	
	public double zoom = 20;
	public static double maxZoom = 75;
	public static double minZoom = 5;
	public static double zoomSpeedKey = 0.5;
	public static double zoomSpeedScroll = 2;
	
	private Information info;
	private int aliveIndex;
	public static int moveSpeed = 5;
	public double xMap = 0;
	public double yMap = 0;
	
	private static long period = 10;
	
	/**
	 * Creates a new Window with the given Information.
	 * Initializes and loads the JFrame with the customized icon, sets the Listener to listen to the JFrame, and sets it to be in full-screen exclusive mode.
	 * 
	 * @param info - the current Information
	 */
	public Grid(Information info)
	{
		this.info = info;
		aliveIndex = info.imageLoader.getIndex("alive");
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseDragged", Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "keyPressed", Listener.TYPE_KEY_PRESSED, Listener.CODE_KEY_ALL);
		info.listener.requestNotification(this, "keyReleased", Listener.TYPE_KEY_RELEASED, Listener.CODE_KEY_ALL);
	}
	
	/**
	 * Runs the Grid in its own Thread by continually updating and sleeping.
	 */
	public void run()
	{
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
	 * Updates the simulation by zooming or moving the grid if necessary.
	 */
	public void update()
	{
		if (minusHeld && !plusHeld)
		{
			info.grid.zoom(-zoomSpeedKey);
		}
		if (plusHeld && !minusHeld)
		{
			info.grid.zoom(zoomSpeedKey);
		}
		if (rightHeld && !leftHeld)
		{
			info.grid.xMap += moveSpeed;
			if (xMap + info.screen.width > info.map.width*zoom)
			{
				xMap = info.map.width*zoom - info.screen.width;
			}
		}
		if (leftHeld && !rightHeld)
		{
			xMap -= moveSpeed;
			if (xMap < 0)
			{
				xMap = 0;
			}
		}
		if (upHeld && !downHeld)
		{
			yMap -= moveSpeed;
			if (yMap < 0)
			{
				yMap = 0;
			}
		}
		if (downHeld && !upHeld)
		{
			yMap += moveSpeed;
			if (yMap + info.screen.height > info.map.height*zoom)
			{
				yMap = info.map.height*zoom - info.screen.height;
			}
		}
	}
	
	/**
	 * Zooms by the given amount.
	 * A positive zoom amount represents zooming in, giving larger tile sizes.
	 * A negative zoom amount represents zooming out, giving smaller tile sizes.
	 * 
	 * @param amount - the amount to zoom
	 */
	public void zoom(double amount)
	{
		zoom = Math.max(minZoom, Math.min(maxZoom, zoom + amount));
		if (xMap < 0)
		{
			xMap = 0;
		}
		if (yMap < 0)
		{
			yMap = 0;
		}
		if (xMap + info.screen.width > info.map.width*zoom)
		{
			xMap = info.map.width*zoom - info.screen.width;
		}
		if (yMap + info.screen.height > info.map.height*zoom)
		{
			yMap = info.map.height*zoom - info.screen.height;
		}
		if (info.toolbar.selector.selected != null)
		{
			info.toolbar.selector.selected.generateFullSizeImage();
		}
	}
	
	/**
	 * Returns the cell that the mouse pointer is currently in.
	 * 
	 * @return Point - the cell in which the mouse pointer is currently, in cells
	 */
	public Point mouseCell()
	{
		Point mouseLocation = (Point)(info.mouse).clone();
		mouseLocation.x += xMap;
		mouseLocation.y += yMap;
		mouseLocation.x = (int)(mouseLocation.x/zoom);
		mouseLocation.y = (int)(mouseLocation.y/zoom);
		return mouseLocation;
	}
	
	public double toCell(double pixel)
	{
		return pixel/zoom;
	}
	
	public double toPixel(double cell)
	{
		return cell*zoom;
	}
	
	/**
	 * Called by the Listener when any key is pressed.
	 * Updates the flags determining which of several critical keys are held.
	 * 
	 * @param e - the KeyEvent that triggered the call
	 */
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			upHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			downHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			rightHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			leftHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_MINUS)
		{
			minusHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
		{
			plusHeld = true;
		}
	}
	
	/**
	 * Called by the Listener when any key is released.
	 * Updates the flags determining which of several critical keys are held.
	 * 
	 * @param e - the KeyEvent that triggered the call
	 */
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			upHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			downHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			rightHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			leftHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_MINUS)
		{
			minusHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
		{
			plusHeld = false;
		}
	}
	
	/**
	 * Called by the Listener when the left mouse button (BUTTON1) is pressed.
	 * If no overlay (the pane, the operation bar, and the pattern selector) consumes the Event, 
	 *  the Window processes the input, either setting the cell clicked on to alive or dead.
	 * Whether the cell was made alive or erased is saved so that if the mouse is dragged that option is continued.
	 * 
	 * @param e - the MouseEvent triggering the call
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!info.controlBar.consumed(e) && !info.toolbar.consumed(e) && !info.toolbar.selector.consumed(e))
		{
			Point mouseCell = mouseCell();
			int x = mouseCell.x;
			int y = mouseCell.y;
			if (info.map.isAlive(mouseCell))
			{
				info.map.setAlive(x, y, false);
				madeAlive = false;
			}
			else
			{
				info.map.setAlive(x, y, true);
				madeAlive = true;
			}
		}
	}
	
	/**
	 * Called by the Listener when the mouse is dragged (held and moved).
	 * The cell over which the mouse has been dragged is set to the made alive boolean, set when the mouse was first pressed.
	 * This has the effect of making each drag either create or delete living cells rather than switch alive/dead.
	 * 
	 * @param e - the MouseEvent triggering the call
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!info.controlBar.consumed(e) && !info.toolbar.consumed(e) && !info.toolbar.selector.consumed(e))
		{
			Point mouseCell = mouseCell();
			int x = mouseCell.x;
			int y = mouseCell.y;
			info.map.setAlive(x, y, madeAlive);
		}
	}
	
	/**
	 * Draws the grid and the map.
	 * If the zoom is high enough (zoomed in enough), the full alive image is drawn; otherwise, a green rectangle is used to reduce draw time.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		if (zoom > 15)
		{
			for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
			{
				for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
				{
					if (info.map.isAlive((x + (int)xMap)/(int)zoom, (y + (int)yMap)/(int)zoom))
					{
						info.imageLoader.get(aliveIndex).draw(x, y, (int)zoom, (int)zoom, g);
					}
					else
					{
						g.setColor(new Color(0, 0, 0));
						g.fillRect(x, y, (int)zoom, (int)zoom);
					}
				}
			}
		}
		else
		{
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, info.screen.width, info.screen.height);
			for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
			{
				for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
				{
					if (info.map.isAlive((x + (int)xMap)/(int)zoom, (y + (int)yMap)/(int)zoom))
					{
						g.setColor(new Color(0, 215, 10));
						g.fillRect(x, y, (int)zoom, (int)zoom);
					}
				}
			}
		}
		g.setColor(new Color(50, 50, 50));
		for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
		{
			g.drawLine(x, 0, x, info.screen.height);
		}
		for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
		{
			g.drawLine(0, y, info.screen.width, y);
		}
	}
}
