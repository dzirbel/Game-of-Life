package pattern;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.Information;
import main.Tooltip;

/**
 * Represents a rectangular box containing the names of the patterns contained within one PatternFolder when that folder is expanded.
 * 
 * @author Dominic
 */
public class PatternNameBox
{
	public ArrayList<String> names;
	public ArrayList<Tooltip> tooltips;
	public ArrayList<Thread> threads;
	
	private static Color darkGray = new Color(0, 0, 0, 150);
	private static Color lightGray = new Color(50, 50, 50, 200);
	
	private Information info;
	public int expandedHeight;
	public static int NAME_HEIGHT = 35;
	
	private PatternFolder folder;
	
	public Rectangle bounds;
	
	/**
	 * Creates a new PatternNameBox with the given patterns and bounding rectangle.
	 * Also initializes the Tooltips, but uses 0, 0 as the x- and y-coordinates until setLocation() is called.
	 * 
	 * @param folder - the PatternFolder containing the pattern information for this name box
	 * @param bounds - the bounding box for this box
	 * @param info - the current Information
	 */
	public PatternNameBox(PatternFolder folder, Rectangle bounds, Information info)
	{
		this.folder = folder;
		this.bounds = bounds;
		this.info = info;
		names = new ArrayList<String>();
		for (int i = 0; i < folder.patterns.size(); i++)
		{
			names.add(folder.patterns.get(i).getName());
		}
		expandedHeight = NAME_HEIGHT*names.size();
		tooltips = new ArrayList<Tooltip>();
		threads = new ArrayList<Thread>();
		for (int i = 0; i < names.size(); i++)
		{
			Tooltip tooltip = new Tooltip(new Rectangle(0, 0, bounds.width, NAME_HEIGHT), folder.patterns.get(i).getPattern().expandedName, 750, info);
			tooltips.add(tooltip);
			threads.add(new Thread(tooltip));
			threads.get(threads.size() - 1).start();			
		}
		setLocation(bounds.x, bounds.y);
	}
	
	/**
	 * Sets the bounding rectangle to the given x and y coordinates.
	 * Adjusts the locations of the Tooltips and makes sure that they are inactive (with a height of zero) if the folder is not expanded.
	 * 
	 * @param x - the new x-coordinate
	 * @param y - the new y-coordinate
	 */
	public void setLocation(int x, int y)
	{
		bounds.x = x;
		bounds.y = y;
		try
		{
			for (int i = 0; i < tooltips.size(); i++)
			{
				tooltips.get(i).bounds.x = info.toolbar.selector.bounds.x + info.toolbar.selector.bounds.width - folder.x + bounds.x;
				tooltips.get(i).bounds.y = i*NAME_HEIGHT + info.toolbar.selector.bounds.y + info.toolbar.selector.bounds.height - folder.y + bounds.y;
				if (!folder.isExpanded())
				{
					tooltips.get(i).bounds.height = 0;
				}
				else
				{
					tooltips.get(i).bounds.height = NAME_HEIGHT;
				}
			}
		}
		catch (NullPointerException ex) { }
	}
	
	/**
	 * Returns the name selected by the mouse click at the given location.
	 * 
	 * @param point - the location of the mouse press
	 * @return name - the name of the clicked on pattern, null if no name was selected
	 */
	public String mousePressed(Point point)
	{
		if (bounds.contains(point))
		{
			int index = (point.y - bounds.y)/NAME_HEIGHT;
			try
			{
				return names.get(index);
			}
			catch (IndexOutOfBoundsException ex)
			{
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Draws the name box.
	 * The box is comprised of rectangles alternating between blue and gray containing the names of the patterns.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		boolean dark = true;
		for (int i = 0; i < names.size(); i++)
		{
			if (dark)
			{
				g.setColor(darkGray);
			}
			else
			{
				g.setColor(lightGray);
			}
			g.fillRect(bounds.x, bounds.y + i*NAME_HEIGHT, bounds.width, NAME_HEIGHT);
			g.setColor(Color.black);
			g.setFont(Information.fontBold);
			g.drawString(names.get(i), bounds.x + 5, bounds.y + NAME_HEIGHT - 5 + i*NAME_HEIGHT);
			g.setColor(Information.lightBlue);
			g.drawString(names.get(i), bounds.x + 5 - 1, bounds.y + NAME_HEIGHT - 5 - 1 + i*NAME_HEIGHT);
			dark = !dark;
		}
	}
}
