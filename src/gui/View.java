package gui;

import graphics.AcceleratedImage;

import io.Listener;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import main.Information;

public abstract class View implements Runnable
{
	/**
	 * The icon used to identify a View.
	 */
	protected AcceleratedImage icon;
	private AffineTransform tabTransform;
	
	/**
	 * Whether this View is currently being dragged.
	 */
	protected boolean dragging = false;
	private boolean held = false;
	private boolean draggedIntoMiddle = false;
	
	/**
	 * The background Color.
	 */
	public static final Color BACKGROUND = new Color(128, 128, 128, 102);
	/**
	 * The Color used to draw the borders of the view.
	 */
	public static final Color BORDER = new Color(28, 28, 28);
	
	/**
	 * The amount that this View is open, in px.
	 */
	protected double amountOpenX = 0;
	protected double amountOpenY = 0;
	
	/**
	 * The distance from the bounds to the edge of the drawn bounds.
	 */
	private static final int BUFFER = 7;
	private static final int DRAG_BUFFER = 100;
	private static final int DRAG_DISTANCE = 20;
	private static final int TAB_BUFFER = 10;
	public static final int SIDE_DISTANCE = 20;
	private int tabIndex;
	private int tabWidth;
	private int tabHeight;
	public static final int LOCATION_TAB_TOP = 1;
	public static final int LOCATION_TAB_RIGHT = 2;
	public static final int LOCATION_TAB_LEFT = 3;
	public static final int LOCATION_TAB_BOTTOM = 4;
	public static final int LOCATION_FLOATING = 5;
	protected int location;
	public static final int OPEN = 1;
	public static final int CLOSED = 2;
	public static final int OPENING = 3;
	public static final int CLOSING = 4;
	protected int open;
	private static final int DIRECTION_UP = 1;
	private static final int DIRECTION_RIGHT = 2;
	private static final int DIRECTION_LEFT = 3;
	private static final int DIRECTION_DOWN = 4;
	private static final int CORNER_TOP_LEFT = 1;
	private static final int CORNER_TOP_RIGHT = 2;
	private static final int CORNER_BOTTOM_LEFT = 3;
	private static final int CORNER_BOTTOM_RIGHT = 4;
	/**
	 * The time between updates, in ms.
	 */
	protected static int period = 20;
	protected Information info;
	
	/**
	 * The time of the last update, in ns.
	 */
	private long lastUpdate;
	/**
	 * The amount of time that it should take for a View to open/close, in ms.
	 */
	protected long timeToOpen = 500;
	
	private static final Point iconShift = new Point(6, 4);
	private static final Point iconSize = new Point(24, 24);
	private Point lastDrag = new Point();
	
	/**
	 * The area in which View-specific content is drawn.
	 */
	protected Rectangle bounds;
	
	/**
	 * Initializes a View object by initializing fields.
	 * 
	 * @param info - the current Information
	 */
	public View(int defaultLocation, int width, int height, Information info)
	{
		this.info = info;
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON_ALL);
		info.listener.requestNotification(this, "mouseReleased", Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON_ALL);
		info.listener.requestNotification(this, "mouseDragged", Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON_ALL);
		tabIndex = info.imageLoader.getIndex("tab");
		tabWidth = info.imageLoader.get(tabIndex).getWidth();
		tabHeight = info.imageLoader.get(tabIndex).getHeight();
		tabTransform = new AffineTransform();
		open = CLOSED;
		location = LOCATION_TAB_RIGHT;
		bounds = new Rectangle();
		bounds.width = width;
		bounds.height = height;
		setLocation(defaultLocation);
		if (defaultLocation == LOCATION_FLOATING)
		{
			bounds.x = 500;
			bounds.y = 500;
		}
	}
	
	/**
	 * Runs a view by looping 
	 */
	public void run()
	{
		while (true)
		{
			lastUpdate = System.nanoTime();
			update();
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException ex) { }
		}
	}
	
	protected long getLastUpdate()
	{
		return lastUpdate;
	}
	
	protected long timeSinceLastUpdate()
	{
		return (System.nanoTime() - lastUpdate)/1000000;
	}
	
	public void open()
	{
		if (open == CLOSED)
		{
			open = OPENING;
		}
	}
	
	public void close()
	{
		if (open == OPEN)
		{
			open = CLOSING;
		}
	}
	
	protected final void setLocation(int location)
	{
		this.location = location;
		if (location == LOCATION_TAB_TOP)
		{
			bounds.x = SIDE_DISTANCE + BUFFER;
			bounds.y = -bounds.height;
			open = CLOSED;
		}
		else if (location == LOCATION_TAB_BOTTOM)
		{
			bounds.x = SIDE_DISTANCE + BUFFER;
			bounds.y = info.screen.height;
			open = CLOSED;
		}
		else if (location == LOCATION_TAB_RIGHT)
		{
			bounds.x = -bounds.width;
			bounds.y = SIDE_DISTANCE + BUFFER;
			open = CLOSED;
		}
		else if (location == LOCATION_TAB_LEFT)
		{
			bounds.x = info.screen.width;
			bounds.y = SIDE_DISTANCE + BUFFER;
			open = CLOSED;
		}
	}
	
	public final int getMaxOpenX()
	{
		if (location == LOCATION_TAB_TOP || location == LOCATION_TAB_BOTTOM)
		{
			return 0;
		}
		if (location == LOCATION_TAB_RIGHT || location == LOCATION_TAB_LEFT || location == LOCATION_FLOATING)
		{
			return bounds.width;
		}
		return -1;
	}
	
	public final int getMaxOpenY()
	{
		if (location == LOCATION_TAB_TOP || location == LOCATION_TAB_BOTTOM || location == LOCATION_FLOATING)
		{
			return bounds.height;
		}
		if (location == LOCATION_TAB_RIGHT || location == LOCATION_TAB_LEFT)
		{
			return 0;
		}
		return -1;
	}
	
	public void update()
	{
		if (open == OPENING)
		{
			amountOpenX += getOpenSpeedX()*timeSinceLastUpdate()/1000000;
			amountOpenY += getOpenSpeedY()*timeSinceLastUpdate()/1000000;
			if (amountOpenX >= getMaxOpenX() || amountOpenY >= getMaxOpenY())
			{
				amountOpenX = getMaxOpenX();
				amountOpenY = getMaxOpenY();
				open = OPEN;
			}
		}
		else if (open == CLOSING)
		{
			amountOpenX -= getOpenSpeedX()*timeSinceLastUpdate()/1000000;
			amountOpenY -= getOpenSpeedY()*timeSinceLastUpdate()/1000000;
			if (amountOpenX <= 0 || amountOpenY <= 0)
			{
				amountOpenX = 0;
				amountOpenY = 0;
				open = CLOSED;
			}
		}
	}
	
	protected final double getOpenSpeedX()
	{
		return getMaxOpenX()/timeToOpen;		// in px/ms
	}
	
	protected final double getOpenSpeedY()
	{
		return getMaxOpenY()/timeToOpen; 		// in px/ms
	}
	
	private final Point getTabLocation()
	{
		Point tabLocation = new Point();
		if (location == LOCATION_FLOATING || location == LOCATION_TAB_BOTTOM)
		{
			tabLocation.x = bounds.x - BUFFER + TAB_BUFFER;
			tabLocation.y = bounds.y - BUFFER - tabHeight;
		}
		else if (location == LOCATION_TAB_TOP)
		{
			tabLocation.x = bounds.x - BUFFER + TAB_BUFFER;
			tabLocation.y = bounds.y + bounds.height + BUFFER;
		}
		else if (location == LOCATION_TAB_LEFT)
		{
			tabLocation.x = bounds.x + bounds.width + BUFFER + tabHeight;
			tabLocation.y = bounds.y - BUFFER + TAB_BUFFER;
		}
		else if (location == LOCATION_TAB_RIGHT)
		{
			tabLocation.x = bounds.x - BUFFER - tabHeight;
			tabLocation.y = bounds.y - BUFFER + TAB_BUFFER;
		}
		
		return tabLocation;
	}
	
	private final Rectangle getIconBounds()
	{
		Point tabLocation = getTabLocation();
		return new Rectangle(tabLocation.x + iconShift.x, tabLocation.y + iconShift.y, iconSize.x, iconSize.y);
	}
	
	protected final void drawBounds(Graphics2D g)
	{
		g.setClip(info.screen);
		if (open == OPEN)
		{
			if (location == LOCATION_FLOATING)
			{
				drawBoundsRect(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_UP, g);
			}
			else if (location == LOCATION_TAB_TOP)
			{
				drawBoundsRect(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_DOWN, g);
			}
			else if (location == LOCATION_TAB_BOTTOM)
			{
				drawBoundsRect(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_UP, g);
			}
			else if (location == LOCATION_TAB_RIGHT)
			{
				drawBoundsRect(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_RIGHT, g);
			}
			else if (location == LOCATION_TAB_LEFT)
			{
				drawBoundsRect(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_LEFT, g);
			}
		}
		else if (open == OPENING || open == CLOSING)
		{
			if (location == LOCATION_FLOATING)
			{
				drawBoundsRect(bounds.x + bounds.width - (int)amountOpenX - BUFFER, bounds.y + bounds.height - (int)amountOpenY - BUFFER,
						(int)amountOpenX + BUFFER*2, (int)amountOpenY + BUFFER*2, DIRECTION_UP, g);
			}
			else if (location == LOCATION_TAB_TOP)
			{
				drawBoundsRect(bounds.x - BUFFER, -BUFFER, bounds.width + BUFFER*2, (int)amountOpenY + BUFFER*2, DIRECTION_DOWN, g);
			}
			else if (location == LOCATION_TAB_BOTTOM)
			{
				drawBoundsRect(bounds.x - BUFFER, info.screen.height - (int)amountOpenY - BUFFER,
						bounds.width + BUFFER*2, (int)amountOpenY + BUFFER*2, DIRECTION_UP, g);
			}
			else if (location == LOCATION_TAB_RIGHT)
			{
				drawBoundsRect(info.screen.width - (int)amountOpenX - BUFFER, bounds.y - BUFFER,
						(int)amountOpenX + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_RIGHT, g);
			}
			else if (location == LOCATION_TAB_LEFT)
			{
				drawBoundsRect(-BUFFER, bounds.y - BUFFER,
						(int)amountOpenX + BUFFER*2, bounds.height + BUFFER*2, DIRECTION_LEFT, g);
			}
		}
		else if (open == CLOSED)
		{
			if (location == LOCATION_FLOATING)
			{
				g.setColor(Color.black);
				g.drawLine(bounds.x + bounds.width - tabWidth, bounds.y, bounds.x + bounds.width, bounds.y);
			}
			else if (location == LOCATION_TAB_TOP)
			{
				drawBoundsRect(bounds.x - BUFFER, -BUFFER - TAB_BUFFER*2, bounds.width + BUFFER*2, BUFFER*2 + TAB_BUFFER*2, DIRECTION_DOWN, g);
			}
			else if (location == LOCATION_TAB_BOTTOM)
			{
				drawBoundsRect(bounds.x - BUFFER, info.screen.height - BUFFER - TAB_BUFFER*2, bounds.width + BUFFER*2, BUFFER*2 + TAB_BUFFER*2, DIRECTION_UP, g);
			}
			else if (location == LOCATION_TAB_RIGHT)
			{
				drawBoundsRect(info.screen.width - BUFFER - TAB_BUFFER*2, bounds.y - BUFFER, BUFFER*2 + TAB_BUFFER*2, bounds.height + BUFFER*2, DIRECTION_RIGHT, g);
			}
			else if (location == LOCATION_TAB_LEFT)
			{
				drawBoundsRect(-BUFFER - TAB_BUFFER*2, bounds.y - BUFFER, BUFFER*2 + TAB_BUFFER*2, bounds.height + BUFFER*2, DIRECTION_LEFT, g);
			}
		}
	}
	
	private final void drawBoundsRect(int x, int y, int width, int height, int tabDirection, Graphics2D g)
	{
		g.translate(x, y);
		g.setColor(BACKGROUND);
		g.fillRoundRect(0, 0, width, height, TAB_BUFFER*2, TAB_BUFFER*2);
		g.setColor(BORDER);
		if (location == LOCATION_FLOATING)
		{
			drawCorner(0, 0, CORNER_TOP_LEFT, g);
			drawCorner(width, 0, CORNER_TOP_RIGHT, g);
			drawCorner(0, height, CORNER_BOTTOM_LEFT, g);
			drawCorner(width, height, CORNER_BOTTOM_RIGHT, g);
		}
		else if (location == LOCATION_TAB_TOP)
		{
			drawCorner(0, height, CORNER_BOTTOM_LEFT, g);
			drawCorner(width, height, CORNER_BOTTOM_RIGHT, g);
		}
		else if (location == LOCATION_TAB_BOTTOM)
		{
			drawCorner(0, 0, CORNER_TOP_LEFT, g);
			drawCorner(width, 0, CORNER_TOP_RIGHT, g);
		}
		else if (location == LOCATION_TAB_RIGHT)
		{
			drawCorner(0, 0, CORNER_TOP_LEFT, g);
			drawCorner(0, height, CORNER_BOTTOM_LEFT, g);
		}
		else if (location == LOCATION_TAB_LEFT)
		{
			drawCorner(width, 0, CORNER_TOP_RIGHT, g);
			drawCorner(width, height, CORNER_BOTTOM_RIGHT, g);
		}
		
		if (tabDirection == DIRECTION_UP)
		{
			// tab is on top - subtract from top line
			g.drawLine(TAB_BUFFER + tabWidth, 0, width - TAB_BUFFER, 0);				// top
			g.drawLine(width, TAB_BUFFER, width, height - TAB_BUFFER);					// right
			g.drawLine(0, TAB_BUFFER, 0, height - TAB_BUFFER);							// left
			g.drawLine(TAB_BUFFER, height, width - TAB_BUFFER, height);					// bottom
		}
		else if (tabDirection == DIRECTION_DOWN)
		{
			// tab is on bottom - subtract from bottom line
			g.drawLine(TAB_BUFFER, 0, width - TAB_BUFFER, 0);							// top
			g.drawLine(width, TAB_BUFFER, width, height - TAB_BUFFER);					// right
			g.drawLine(0, TAB_BUFFER, 0, height - TAB_BUFFER);							// left
			g.drawLine(TAB_BUFFER + tabWidth, height, width - TAB_BUFFER, height);		// bottom
		}
		else if (tabDirection == DIRECTION_RIGHT)
		{
			// tab is on right - subtract from right line
			g.drawLine(TAB_BUFFER, 0, width - TAB_BUFFER, 0);							// top
			g.drawLine(width, TAB_BUFFER + tabWidth, width, height - TAB_BUFFER);		// right
			g.drawLine(0, TAB_BUFFER, 0, height - TAB_BUFFER);							// left
			g.drawLine(TAB_BUFFER, height, width - TAB_BUFFER, height);					// bottom
		}
		else if (tabDirection == DIRECTION_LEFT)
		{
			// tab is on left - subtract from left line
			g.drawLine(TAB_BUFFER, 0, width - TAB_BUFFER, 0);							// top
			g.drawLine(width, TAB_BUFFER, width, height - TAB_BUFFER);					// right
			g.drawLine(0, TAB_BUFFER + tabWidth, 0, height - TAB_BUFFER);				// left
			g.drawLine(TAB_BUFFER, height, width - TAB_BUFFER, height);					// bottom
		}
		g.setTransform(new AffineTransform());
		drawTab(tabDirection, g);
	}
	
	private final void drawCorner(int x, int y, int corner, Graphics2D g)
	{
		if (corner == CORNER_TOP_LEFT)
		{
			g.drawArc(x, y, TAB_BUFFER*2, TAB_BUFFER*2, 90, 90);
		}
		else if (corner == CORNER_TOP_RIGHT)
		{
			g.drawArc(x - TAB_BUFFER*2, y, TAB_BUFFER*2, TAB_BUFFER*2, 0, 90);
		}
		else if (corner == CORNER_BOTTOM_LEFT)
		{
			g.drawArc(x, y - TAB_BUFFER*2, TAB_BUFFER*2, TAB_BUFFER*2, 180, 90);
		}
		else if (corner == CORNER_BOTTOM_RIGHT)
		{
			g.drawArc(x - TAB_BUFFER*2, y - TAB_BUFFER*2, TAB_BUFFER*2, TAB_BUFFER*2, 270, 90);
		}
	}
	
	private final void drawTab(int direction, Graphics2D g)
	{
		tabTransform = new AffineTransform();
		Point tabLocation = getTabLocation();
		if (direction == DIRECTION_UP)
		{
			g.drawImage(info.imageLoader.get(tabIndex).getBufferedImage(), tabLocation.x, tabLocation.y, null);
			icon.draw(tabLocation.x + iconShift.x, tabLocation.y + iconShift.y, g);
		}
		else if (direction == DIRECTION_DOWN)
		{
			tabTransform.setToScale(1.0, -1.0);
			g.setTransform(tabTransform);
			g.drawImage(info.imageLoader.get(tabIndex).getBufferedImage(), tabLocation.x, -tabLocation.y - tabHeight, null);
			g.setTransform(new AffineTransform());
			icon.draw(tabLocation.x + iconShift.x, tabLocation.y + tabHeight - iconShift.y - iconSize.y, g);
		}
		else if (direction == DIRECTION_RIGHT)
		{
			tabTransform.setToRotation(-Math.PI/2, tabLocation.x, tabLocation.y);
			g.setTransform(tabTransform);
			g.drawImage(info.imageLoader.get(tabIndex).getBufferedImage(), -500, 500, null);
			g.setTransform(new AffineTransform());
			icon.draw(tabLocation.x + iconShift.x, tabLocation.y + tabHeight - iconShift.y - iconSize.y, g);
		}
		else if (direction == DIRECTION_LEFT)
		{
			
		}
		
		//tabTransform.setToRotation(rotation, tabLocation.x, tabLocation.y);
//		g.setTransform(tabTransform);
//		g.drawImage(info.imageLoader.get(tabIndex).getBufferedImage(), tabLocation.x, tabLocation.y, null);
//		g.setTransform(new AffineTransform());
//		icon.draw(tabLocation.x + iconShift.x, tabLocation.y + iconShift.y, g);
	}
	
	public final void draw(Graphics2D g)
	{
		drawBounds(g);
		
		g.translate(bounds.x, bounds.y);
		g.setClip(new Rectangle(0, 0, bounds.width, bounds.height));
		drawContents(g);
		g.setTransform(new AffineTransform());
		g.setClip(info.screen);
	}
	
	public boolean consumed(MouseEvent e)
	{
		Rectangle extendedBounds = new Rectangle(bounds.x - BUFFER, bounds.y - BUFFER, bounds.width + 2*BUFFER, bounds.height + 2*BUFFER);
		Rectangle tabBounds = new Rectangle(getTabLocation().x, getTabLocation().y, tabWidth, tabHeight);
		if (extendedBounds.contains(e.getLocationOnScreen()) || tabBounds.contains(e.getLocationOnScreen()))
		{
			return true;
		}
		return false;
	}
	
	public final void mousePressed(MouseEvent e)
	{
		if (getIconBounds().contains(e.getLocationOnScreen()))
		{
			held = true;
			lastDrag = e.getLocationOnScreen();
		}
	}
	
	public final void mouseReleased(MouseEvent e)
	{
		if (dragging && draggedIntoMiddle)
		{
			if (bounds.x < DRAG_BUFFER)
			{
				// left
				setLocation(LOCATION_TAB_LEFT);
			}
			else if (bounds.x > info.screen.width - DRAG_BUFFER)
			{
				// right
				setLocation(LOCATION_TAB_RIGHT);
			}
			else if (bounds.y < DRAG_BUFFER)
			{
				// top
				setLocation(LOCATION_TAB_TOP);
			}
			else if (bounds.y > info.screen.height - DRAG_BUFFER)
			{
				// bottom
				setLocation(LOCATION_TAB_BOTTOM);
			}
		}
		
		held = false;
		dragging = false;
		draggedIntoMiddle = false;
	}
	
	public final void mouseDragged(MouseEvent e)
	{
		if (held)
		{
			if (dragging)
			{
				bounds.x += e.getLocationOnScreen().x - lastDrag.x;
				bounds.y += e.getLocationOnScreen().y - lastDrag.y;
				lastDrag = e.getLocationOnScreen();
				if (!draggedIntoMiddle)
				{
					if (bounds.x > DRAG_BUFFER && bounds.x < info.screen.width - DRAG_BUFFER &&
							bounds.y > DRAG_BUFFER && bounds.y < info.screen.height - DRAG_BUFFER)
					{
						draggedIntoMiddle = true;
					}
				}
			}
			else
			{
				if (Math.abs(e.getLocationOnScreen().x - lastDrag.x) >= DRAG_DISTANCE ||
						Math.abs(e.getLocationOnScreen().y - lastDrag.y) >= DRAG_DISTANCE)
				{
					location = LOCATION_FLOATING;
					bounds.x = lastDrag.x - TAB_BUFFER + BUFFER - iconShift.x - iconSize.x/2;
					bounds.y = lastDrag.y + BUFFER + tabHeight - iconShift.y - iconSize.y/2;
					open = OPEN;
					dragging = true;
					draggedIntoMiddle = false;
				}
			}
		}
	}
	
	public abstract void drawContents(Graphics2D g);
}
