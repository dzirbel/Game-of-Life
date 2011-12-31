package pattern;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import main.Information;

/**
 * Represents an icon containing the data of a Pattern.
 * 
 * @author Dominic
 */
public class PatternIcon
{
	private AffineTransform transform;
	
	public double rotation;
	
	public int x;
	public int y;
	
	private Pattern pattern;
	
	/**
	 * Creates a new PatternIcon at the given coordinates with the given Pattern and Information.
	 * The Pattern held by this icon is initialized with the clone() method, then it is set to fill black and an image is generated.
	 * Note that the size of the icon is equal to the size of the given Pattern's image.
	 * 
	 * @param x - the x-coordinate for the PatternIcon, the top-left corner ignoring rotation
	 * @param y - the y-coordinate for the PatternIcon, the top-left corner ignoring rotation
	 * @param pattern - the Pattern contained by this icon
	 * @param info - the current Information
	 */
	public PatternIcon(int x, int y, Pattern pattern, Information info)
	{
		this.x = x;
		this.y = y;
		this.pattern = pattern.clone();
		this.pattern.setFillBlack(true);
		this.pattern.generateImage();
		transform = new AffineTransform();
		rotation = 0;
	}
	
	/**
	 * Returns the name of the Pattern, equivalent to calling getPattern().name.
	 * 
	 * @return name - the standard name of the Pattern
	 */
	public String getName()
	{
		return pattern.name;
	}
	
	/**
	 * The Pattern contained by this icon.
	 * 
	 * @return pattern - the Pattern held by this icon
	 */
	public Pattern getPattern()
	{
		return pattern;
	}
	
	/**
	 * Sets the rotation to the given one and rotates the AffineTransform around the coordinates.
	 * 
	 * @param rotation - the rotation of the icon, in radians
	 */
	public void setRotation(double rotation)
	{
		this.rotation = rotation;
		transform.setToRotation(rotation, x, y);
	}
	
	/**
	 * Sets the coordinates to the given ones and then re-rotates around the coordinates.
	 * 
	 * @param x - the x-coordinate for the PatternIcon, the top-left corner ignoring rotation
	 * @param y - the y-coordinate for the PatternIcon, the top-left corner ignoring rotation
	 */
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
		transform.setToRotation(rotation, x, y);
	}
	
	/**
	 * Sets the coordinates to the given ones and then re-rotates around the coordinates.
	 * 
	 * @param p - the coordinates for the PatternIcon, the top-left corner ignoring rotation
	 */
	public void setLocation(Point p)
	{
		setLocation(p.x, p.y);
	}
	
	/**
	 * Draws this icon at the coordinates with the rotation.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		BufferedImage img = pattern.img.getBufferedImage();
		g.transform(transform);
		g.drawImage(img, x, y, null);
		g.setTransform(new AffineTransform());
	}
	
	/**
	 * Draws this icon at the coordinates with the rotation and the given RescaleOp.
	 * 
	 * @param rescaler - a RescaleOp used to rescale the image pixel-by-pixel
	 * @param g - the current Graphics context
	 */
	public void draw(RescaleOp rescaler, Graphics2D g)
	{
		BufferedImage img = pattern.img.getBufferedImage();
		g.transform(transform);
		g.drawImage(img, rescaler, x, y);
		g.setTransform(new AffineTransform());
	}
	
	/**
	 * Draws this icon at the coordinates with the rotation and given RescaleOp and the given shifts.
	 * 
	 * @param rescaler - a RescaleOp used to rescale the image pixel-by-pixel
	 * @param shiftX - the x-shift
	 * @param shiftY - the y-shift
	 * @param g - the current Graphics context
	 */
	public void draw(RescaleOp rescaler, int shiftX, int shiftY, Graphics2D g)
	{
		BufferedImage img = pattern.img.getBufferedImage();
		g.transform(transform);
		g.drawImage(img, rescaler, x + shiftX, y + shiftY);
		g.setTransform(new AffineTransform());
	}
}
