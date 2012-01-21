package main;

import io.Listener;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 * Represents the slider that determines the speed at which successive generations are calculated.
 * 
 * @author Dominic
 */
public class SpeedBar
{
	private boolean dragging = false;
	
	private Information info;
	public int minSliderX;
	public int maxSliderX;
	public int sliderX;
	public int speed;
	public static int minSpeed = 0;
	public static int maxSpeed = 100;
	private int sliderIndex;
	private int sliderBarIndex;
	
	public static long minPeriod = 500;
	public static long maxPeriod = 0;
	
	public Rectangle bounds;
	public Rectangle sliderBounds;
	
	/**
	 * Creates a new SpeedBar with the given Information.
	 * Images, bounding boxes, and other fields are initialized.
	 * 
	 * @param info - the current Information
	 */
	public SpeedBar(Information info)
	{
		this.info = info;
		sliderIndex = info.imageLoader.add("images/slider.png", "slider", Transparency.TRANSLUCENT);
		sliderBarIndex = info.imageLoader.add("images/sliderBar.png", "sliderBar", Transparency.TRANSLUCENT);
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseReleased", Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseDragged", Listener.TYPE_MOUSE_DRAGGED, 0);
		bounds = new Rectangle(info.toolbar.x + 280, info.toolbar.y + 45, 100, 40);
		minSliderX = 10;
		maxSliderX = bounds.width - 10 - info.imageLoader.get(sliderIndex).getWidth();
		sliderX = (minSliderX + maxSliderX)/2;
		setSliderBounds(bounds.x + sliderX, false);
	}
	
	/**
	 * Sets the boundaries for the slider.
	 * If the Pane is dragged and thus the SpeedBar's bounds must be updated, true should be given.
	 * Otherwise, it is assumed that the slider itself is being dragged and the location of the slider along the bar will be altered.
	 * 
	 * @param x - the x-coordinate of the slider on the screen
	 * @param movingPane - true if the pane's movement caused this call, false otherwise
	 */
	public void setSliderBounds(int x, boolean movingPane)
	{
		sliderBounds = new Rectangle(x, bounds.y + bounds.height/2 - info.imageLoader.get(sliderIndex).getHeight()/2,
				info.imageLoader.get(sliderIndex).getWidth(), info.imageLoader.get(sliderIndex).getHeight());
		if (!movingPane)
		{
			sliderX = sliderBounds.x - bounds.x;
		}
	}
	
	/**
	 * Called by the Listener whenever the left mouse button (BUTTON1) is pressed.
	 * Checks to see if the input comes from within the boundaries of the slider; if so, the slider enters drag mode.
	 * 
	 * @param e - the MouseEvent that triggered the call
	 */
	public void mousePressed(MouseEvent e)
	{
		if (sliderBounds.contains(e.getLocationOnScreen()))
		{
			dragging = true;
		}
	}
	
	/**
	 * Called by the Listener whenever the left mouse button (BUTTON1) is released.
	 * Simply sets the dragging flag to false, signaling that the slider is no longer being dragged.
	 * 
	 * @param e - the MouseEvent that triggered the call
	 */
	public void mouseReleased(MouseEvent e)
	{
		dragging = false;
	}
	
	/**
	 * Called by the Listener whenever the mouse is dragged (held and moved).
	 * If the slider is being dragged (it was initially clicked on and has not been released) the slider x-value is adjusted.
	 * 
	 * @param e - the MouseEvent that triggered the call
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (dragging)
		{
			setSliderBounds(Math.min(maxSliderX + bounds.x, Math.max(minSliderX + bounds.x, e.getX())), false);
		}
	}
	
	public void adjustSpeed(int amount)
	{
		setSliderBounds(Math.min(maxSliderX + bounds.x, Math.max(minSliderX + bounds.x, bounds.x + sliderX + amount)), false);
	}
	
	/**
	 * Returns the current speed given by the slider.
	 * This is an arbitrary value between minSpeed and maxSpeed.
	 * 
	 * @return speed - the current speed
	 */
	public int getSpeed()
	{
		speed = sliderX - minSliderX;
		speed *= maxSpeed - minSpeed;
		speed /= maxSliderX - minSliderX;
		return speed;
	}
	
	/**
	 * Returns the period duration of the generation according to the slider.
	 * 
	 * @return period - the length of the theoretical period, in ms
	 */
	public long getPeriod()
	{
		getSpeed();
		long period = minPeriod + speed*((maxPeriod - minPeriod)/(maxSpeed - minSpeed));
		return period;
	}
	
	/**
	 * Draws the slider bar and slider to the given Graphics.
	 * The slider's coordinates are adjusted so that they are drawn relative to the Pane rather than the screen.
	 * 
	 * @param g - the current Graphics context
	 */
	public void drawToImage(Graphics2D g)
	{
		info.imageLoader.get(sliderBarIndex).draw(bounds.x - info.toolbar.x, bounds.y - info.toolbar.y, bounds.width, bounds.height, g);
		info.imageLoader.get(sliderIndex).draw(sliderBounds.x - info.toolbar.x, sliderBounds.y - info.toolbar.y, sliderBounds.width, sliderBounds.height, g);
	}
}
