package main;

import io.Listener;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 * Represents a small portion of the screen in the upper-right hand corner that enables the user to minimize or close the window with the mouse.
 * 
 * @author Dominic
 */
public class ControlBar
{	
	private Information info;
	private int controlBarIndex;
	
	private static Point minimizeShift = new Point(5, 5);
	private static Point closeShift = new Point(45, 5);
	
	public Rectangle bounds;
	private Rectangle minimizeBounds;
	private Rectangle closeBounds;
	
	private static String controlBarFile = "images/controlBar.png";
	
	/**
	 * Creates a new OperationBar with the given Information.
	 * The operation bar image is added to the ImageLoader used by the Information and then bounding Rectangles are initialized.
	 * Finally, a request is made to the Information's Listener to receive mouse press inputs.
	 * 
	 * @param info - the current Information
	 */
	public ControlBar(Information info)
	{
		this.info = info;
		controlBarIndex = info.imageLoader.add(controlBarFile, "controlBar", Transparency.TRANSLUCENT);
		bounds = new Rectangle(info.screen.width - info.imageLoader.get(controlBarIndex).getWidth(), 0,
				info.imageLoader.get(controlBarIndex).getWidth(), info.imageLoader.get(controlBarIndex).getHeight());
		minimizeBounds = new Rectangle(bounds.x + minimizeShift.x, bounds.y + minimizeShift.y, 40, 40);
		closeBounds = new Rectangle(bounds.x + closeShift.x, bounds.y + closeShift.y, 40, 40);
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
	}
	
	/**
	 * Called by the Listener when the left mouse button (BUTTON1) is pressed.
	 * The input is checked to see if it is within the bounds of either the minimize or close button, and appropriate actions are taken if so.
	 * 
	 * @param e - the MouseEvent that triggered this call
	 */
	public void mousePressed(MouseEvent e)
	{
		if (minimizeBounds.contains(e.getX(), e.getY()))
		{
			info.gameOfLife.frame.setState(Frame.ICONIFIED);
		}
		else if (closeBounds.contains(e.getX(), e.getY()))
		{
			System.exit(0);
		}
	}
	
	/**
	 * Determines whether the given MouseEvent should be consumed by the OperationBar or whether it can continue for further processing.
	 * 
	 * @param e - the MouseEvent that has been generated
	 * @return consumed - true if the MouseEvent occurred within the boundaries of the OperationBar, false otherwise
	 */
	public boolean consumed(MouseEvent e)
	{
		if (bounds.contains(e.getPoint()))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Draws the operation bar's image with the Information's ImageLoader.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		info.imageLoader.get(controlBarIndex).draw(bounds.x, bounds.y, g);
	}
}
