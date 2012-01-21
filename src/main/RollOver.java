package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * Represents the highlight that can be placed behind buttons and other objects on the screen.
 * A RollOver fades in as the mouse is moved over the object and then fades out after the mouse leaves.
 * A RollOver also has the ability to "splash" - fade fully in and then fully out once.
 * 
 * @author Dominic
 */
public class RollOver implements Runnable
{
	private boolean splashing = false;
	private boolean splashingUp = false;
	
	private float[] scales = {1f, 1f, 1f, 1f};
	private float[] offsets = new float[4];
	private float alpha = 0f;
	public static float alphaSpeed = 0.05f;
	
	private Information info;
	private int selectionIndex;
	
	private static long period = 20;
	
	public Rectangle bounds;
	private RescaleOp rescaler;
	
	/**
	 * Creates a new RollOver with the given bounds and Information.
	 * 
	 * @param bounds - the bounding rectangle for this RollOver
	 * @param info - the current Information
	 */
	public RollOver(Rectangle bounds, Information info)
	{
		this.info = info;
		this.bounds = bounds;
		selectionIndex = info.imageLoader.getIndex("selection");
	}
	
	/**
	 * Runs the RollOver by continually updating and sleeping.
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
			catch (InterruptedException e) { }
		}
	}
	
	/**
	 * Updates the RollOver by checking if the mouse is currently within the boundary of the RollOver and increasing the alpha value if so.
	 * If the mouse is not, the alpha value is decreased.
	 * In either case, the alpha value is kept between 0 and 1.
	 */
	public void update()
	{
		if (splashing)
		{
			if (splashingUp)
			{
				alpha += alphaSpeed;
				if (alpha > 1)
				{
					alpha = 1;
					splashingUp = false;
				}
			}
			else
			{
				alpha -= alphaSpeed;
				if (alpha < 0)
				{
					alpha = 0;
					splashing = false;
				}
			}
		}
		else
		{
			if (bounds.contains(info.mouse))
			{
				alpha = Math.min(alpha + alphaSpeed, 1f);
			}
			else
			{
				alpha = Math.max(alpha - alphaSpeed, 0f);
			}
		}
	}
	
	public void splash()
	{
		splashing = true;
		splashingUp = true;
	}
	
	/**
	 * Draws the RollOver with the current alpha value at the location specified by the bounding box.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		draw(0, 0, g);
	}
	
	/**
	 * Draws the RollOver with the current alpha value at the location specified by the bounding box, shifted by the given amounts.
	 * 
	 * @param shiftX - the amount to shift the x-coordinate of the RollOver, in pixels
	 * @param shiftY - the amount to shift the y-coordinate of the RollOver, in pixels
	 * @param g - the current Graphics context
	 */
	public void draw(int shiftX, int shiftY, Graphics2D g)
	{
		if (alpha > 0.001f)
		{
			BufferedImage bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics biG = bi.getGraphics();
			info.imageLoader.get(selectionIndex).setScale((double)((double)bounds.width)/((double)info.imageLoader.get(selectionIndex).getWidth()),
					(double)((double)bounds.height)/((double)info.imageLoader.get(selectionIndex).getHeight()));
			info.imageLoader.get(selectionIndex).draw(0, 0, (Graphics2D)biG);
			info.imageLoader.get(selectionIndex).setTransform(new AffineTransform());
			
			scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, bounds.x + shiftX, bounds.y + shiftY);
		}
	}
}
