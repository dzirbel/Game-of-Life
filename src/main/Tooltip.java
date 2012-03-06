package main;

import io.Listener;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Represents a Tooltip, a "hover box" that gives some information via text when a certain object is hovered over by the mouse.
 * Tooltips are drawn on-screen when a certain amount of time has elapsed with the mouse hovering over the bounding box given to the Tooltip.
 * At the time when it becomes visible, it sets its location to the current location of the mouse, adjusted if that location would put the Tooltip off the screen.
 * Each Tooltip is drawn as a translucent black rounded rectangle with a solid black outline surrounding blue text.
 * The height and length of the text on-screen is found using the FontMetrics class.
 * 
 * @author Dominic
 */
public class Tooltip implements Runnable
{
	private boolean visible;
	private boolean hovering;						// true if the mouse is currently hovering, false otherwise
	private boolean initialized = false;			// true if the image has been made (requires GameOfLife full initialization)
	private BufferedImage img = null;
	
	private float[] scales = {1f, 1f, 1f, 1f};
	private float[] offsets = new float[4];
	private float alpha = 0;
	private float alphaSpeed = 0;
	
	private Information info;
	private static int buffer = 5;					// distance from the text to the edge of the tooltip in px
	
	private long hoverTime = 0;						// time before tooltip becomes visible in ms
	private static long defaultHoverTime = 1500;	// default time before tooltip becomes visible in ms
	private long hoverStart = 0;					// time that the mouse began hovering in ns
	private static long period = 25;				// time slept between updates in ms
	private static long alphaTime = 350;			// time to become fully fade in ms
	private long lastUpdate;
	
	private Metrics metrics;
	
	public Point location = new Point();
	private static Point shift = new Point(2, 12);	// distance from the mouse that the tooltip appears
	
	public Rectangle bounds;						// bounds that the tooltip becomes visible over
	private Rectangle2D size;						// boundary box for the tooltip
	private RescaleOp rescaler;
	
	public String message;
	
	/**
	 * Creates a new Tooltip with the given bounding box, message, and Information.
	 * The time to become visible is set to the default.
	 * 
	 * @param bounds - the boundary of the object that this Tooltip is denoting on the screen in px
	 * @param message - the text that this Tooltip displays when visible
	 * @param info - the current Information
	 */
	public Tooltip(Rectangle bounds, String message, Information info)
	{
		this(bounds, message, defaultHoverTime, info);
	}
	
	/**
	 * Creates a new Tooltip with the given bounding box, message, hover time, and Information.
	 * The time to become visible is set to the default.
	 * 
	 * @param bounds - the boundary of the object that this Tooltip is denoting on the screen in px
	 * @param message - the text that this Tooltip displays when visible
	 * @param hoverTime - the time that the mouse must be hovering above the bounds before the Tooltip becomes visible in ms
	 * @param info - the current Information
	 */
	public Tooltip(Rectangle bounds, String message, long hoverTime, Information info)
	{
		this.bounds = bounds;
		this.message = message;
		this.hoverTime = hoverTime;
		this.info = info;
		info.listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_MOVED, 0);
		
		init();
	}
	
	/**
	 * Initializes the Tooltip by finding the size that the text would be on the screen and creating an image containing the graphics of the Tooltip, among other things.
	 * Because finding the size of the text requires the draw graphics from the GameOfLife's JFrame, this method may not fully initialize immediately
	 *  and cannot be counted on to work the first time.
	 */
	private void init()
	{
		try
		{
			Graphics g = info.getGraphics();
			metrics = new Metrics(Information.fontPlain);
			size = metrics.getStringBounds(message, g);
			
			drawToImage();
			
			alphaSpeed = 1/(float)alphaTime;					// 1/ms
			
			initialized = true;
		}
		catch (Exception ex) { }
	}
	
	/**
	 * Runs the Tooltip in its own thread by continually updating and sleeping.
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
	 * Updates the Tooltipby setting alpha values, measuring the time hovered, and so on.
	 * Because full initialization of the Tooltip requires that the GameOfLife class fully initializes
	 *  to the point that the Graphics used to draw can be gotten, initialization must be constantly checked.
	 */
	public void update()
	{
		if (!initialized)
		{
			init();
		}
		else
		{
			if (hovering == true)
			{
				if (!visible && System.nanoTime() - hoverStart >= hoverTime*1000000)
				{
					// invisible but enough time has elapsed to be visible
					visible = true;
					location = (Point)info.mouse.clone();
					location.x += shift.x;
					location.y += shift.y;
					if (!bounds.contains(location))
					{
						location = new Point(bounds.x + bounds.width/2, bounds.y + bounds.height/2);
					}
				}
				if (visible && alpha < 1)
				{
					// fading in
					alpha += alphaSpeed*(System.nanoTime() - lastUpdate)/1000000;
					if (alpha > 1)
					{
						// done fading
						alpha = 1;
					}
				}
			}
			else
			{
				if (alpha > 0)
				{
					// fading out
					alpha -= alphaSpeed*(System.nanoTime() - lastUpdate)/1000000;
					if (alpha < 0)
					{
						// done fading
						alpha = 0;
						visible = false;
					}
				}
			}
		}
		lastUpdate = System.nanoTime();
	}
	
	/**
	 * Called when the mouse is moved, this method updates the status of hovering.
	 * 
	 * @param e - the MouseEvent causing this method call
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (bounds.contains(e.getLocationOnScreen()))
		{
			if (hovering == false)
			{
				hoverStart = System.nanoTime();
			}
			hovering = true;
		}
		else
		{
			hovering = false;
		}
	}
	
	/**
	 * Draws the Tooltip to the screen.
	 * 
	 * @param g - the Graphics context that is given directly by the window, it cannot be translated or found by an image
	 */
	public void draw(Graphics2D g)
	{
		if (visible)
		{
			scales[0] = scales[1] = scales[2] = scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			int drawX = location.x;
			int drawY = location.y;
			
			if (location.x + size.getWidth() > info.screen.width)
			{
				// the tooltip would have been off the screen to the right
				drawX -= (int)size.getWidth();
			}
			if (location.y + size.getHeight() > info.screen.height)
			{
				// the tooltip would have been off the screen at the bottom
				drawY -= (int)size.getHeight();
			}
			
			g.drawImage(img, rescaler, drawX, drawY);
		}
	}
	
	/**
	 * Draws the contents of the Tooltip to an image so that drawing to the screen is faster and easier.
	 */
	private void drawToImage()
	{
		img = new BufferedImage((int)size.getWidth() + buffer*2, (int)size.getHeight() + buffer*2, BufferedImage.TYPE_INT_ARGB);
		Graphics imgG = img.getGraphics();
		imgG.setColor(Color.black);
		imgG.drawRoundRect(0, 0, img.getWidth() - 1, img.getHeight() - 1, buffer, buffer);
		imgG.drawRoundRect(1, 1, img.getWidth() - 3, img.getHeight() - 3, buffer, buffer);
		imgG.setColor(new Color(0, 0, 0, 150));
		imgG.fillRoundRect(0, 0, img.getWidth(), img.getHeight(), buffer, buffer);
		
		imgG.setColor(Color.black);
		imgG.setFont(Information.fontPlain);
		imgG.drawString(message, buffer + 1, buffer + metrics.getHeight() + 1);
		imgG.setColor(Information.lightBlue);
		imgG.drawString(message, buffer, buffer + metrics.getHeight());
	}
}

/**
 * Simply extends FontMetrics so that the constructor can be accessed.
 * 
 * @author Dominic
 */
class Metrics extends FontMetrics
{
	private static final long serialVersionUID = 1L;

	/**
	 * Calls the super constructor.
	 */
	public Metrics(Font font)
	{
		super(font);
	}
}
