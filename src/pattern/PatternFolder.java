package pattern;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RescaleOp;
import java.io.File;
import java.util.ArrayList;

import main.Information;
import main.Tooltip;

/**
 * Represents a single folder in which pattern data files are stored.
 * Each folder is responsible for loading and storing data for all the patterns within the location given upon initialization.
 * Additionally, folders are used when selecting patterns from the PatternSelector.
 * The folder's have an x and y coordinate system relative only to the boundaries of the PatternSelector, 
 *  so that each folder has no knowledge of its position on the screen.
 * Thus, when drawn, each folder draws directly onto an image kept by the PatternSelector which is then rendered onto the screen.
 * Additionally, the coordinates of the PatternFolders are equal to the distance from the bottom right corner of the PatternSelector,
 *  so that moving up and left is equal to positive change in the x and y coordinates.
 * This is so that, when expanding, the end coordinates of the expansion are easier to determine and the other folders can be moved more simply.
 * Each folder creates an icon composed of a front and back folder image and images representing the patterns within the folder.
 * These folders are displayed horizontally from the pattern selector menu, showing only the folder image and the pattern icons.
 * When clicked on, a folder expands both vertically and horizontally, showing the names of the patterns that it contains.
 * The PatternNameBox used is given coordinates relative to the location of the folder rather than the PatternSelector.
 * 
 * @author Dominic
 */
public class PatternFolder
{
	public ArrayList<PatternIcon> patterns;
	
	private boolean expanded;
	private boolean changing;
	
	public double width;
	public double height;
	private double folderX = 0;						// the folder image's distance from x,y when expanded - will increase if expanding, decrease if retracting
	private double folderY = 0;						// the folder image's distance from x,y when expanded - will increase if expanding, decrease if retracting
	private double folderXExpanded;					// the folder image's distance from x,y when fully expanded
	private double folderYExpanded;					// the folder image's distance from x,y when fully expanded
	private double folderXSpeed;					// the speed that the folder image's x-coordinate moves in px/ms
	private double folderYSpeed;					// the speed that the folder image's y-coordinate moves in px/ms
	private double namesHeightSpeed;				// the speed that the PatternNameBox's height and y-coordinate moves in px/ms
	private double widthSpeed;						// the speed that the width of the folder's bounding box moves in px/ms
	private double heightSpeed;						// the speed that the height of the folder's bounding box moves in px/ms
	private static double THETA = Math.PI/2;		// the total sweep of the pattern icons in radians
	private static double ROTATION = Math.PI/3;		// the total change in the rotation of the icons in radians
	
	private float[] scales = {0.9f, 0.9f, 0.9f, 0.9f};
	private float[] offsets = new float[4];
	
	private Information info;
	private int frontIndex;
	private int backIndex;
	public int x;									// distance from the right side of the PatternSelector
	public int y;									// distance from the bottom of the PatternSelector
	public int expandedWidth = 250;					// the width of the bounding box when fully expanded, in px
	public int expandedHeight;						// the height of the bounding box when fully expanded, in px
	public static int FOLDER_WIDTH = 125;			// the width of the folder image in px
	public static int FOLDER_HEIGHT = 125;			// the height of the folder image in px
	public static int ICON_WIDTH = 40;				// the width of a pattern icon in px
	public static int ICON_HEIGHT = 40;				// the height of a pattern icon in px
	private static int TOP_BUFFER = 20;				// the distance from the top of the bounding box to the top of the folder
	private static int MIDDLE_BUFFER = 30;			// the distance from the bottom of the folder to the top of the name box
	private static int BOTTOM_BUFFER = 10;			// the distance from the bottom of the bounding box to bottom of the name box
	private static int SIDE_BUFFER = 15;			// the distance from the sides of the bounding box to the name box
	
	private static long expansionTime = 200;		// the amount of time to fully expand in ms
	private long lastUpdate = 0;
	
	public PatternNameBox names;
	private static Point center = new Point(0, FOLDER_HEIGHT/3);
	
	private RescaleOp rescaler;
	
	public String location;
	public String name;
	
	public Thread tooltipThread;
	public Tooltip tooltip;
	
	/**
	 * Creates a new PatternFolder with the given folder location, x and y coordinates, and Information.
	 * 
	 * @param location - the filename in which all the patterns are stored
	 * @param x - the x-coordinate of this folder, relative to the bottom-right corner of the pattern selector box
	 * @param y - the y-coordinate of this folder, relative to the bottom-right corner of the pattern selector box
	 * @param info - the current Information
	 */
	public PatternFolder(String location, int x, int y, Information info)
	{
		this.location = location;
		this.x = x;
		this.y = y;
		this.info = info;
		width = FOLDER_WIDTH;
		height = FOLDER_HEIGHT;
		patterns = new ArrayList<PatternIcon>();
		frontIndex = info.imageLoader.getIndex("folderFront");
		backIndex = info.imageLoader.getIndex("folderBack");
		expanded = false;
		changing = false;
		rescaler = new RescaleOp(scales, offsets, null);		
		
		File folder = new File(location);
		name = folder.getName();
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			patterns.add(new PatternIcon(0, 0, Pattern.load(files[i].getPath(), ICON_WIDTH, ICON_HEIGHT, info), info));
		}
		
		setIcons();
		
		for (int i = 0; i < patterns.size(); i++)
		{
			patterns.get(i).setRotation(-(ROTATION/2) + (ROTATION/patterns.size())*i);
		}
		
		names = new PatternNameBox(this, new Rectangle(SIDE_BUFFER, FOLDER_HEIGHT - BOTTOM_BUFFER, expandedWidth - SIDE_BUFFER*2, 0), info);
		
		expandedHeight = TOP_BUFFER + FOLDER_HEIGHT + MIDDLE_BUFFER + names.expandedHeight + BOTTOM_BUFFER;
		folderXExpanded = (expandedWidth - FOLDER_WIDTH)/2;
		folderYExpanded = TOP_BUFFER;
		
		names.setLocation(SIDE_BUFFER, expandedHeight - BOTTOM_BUFFER - names.expandedHeight);
		
		tooltip = new Tooltip(new Rectangle(0, 0, 0, 0), new String(name + " Patterns"), info);
		tooltipThread = new Thread(tooltip);
		tooltipThread.start();
	}
	
	/**
	 * Moves the components of the folder if the changing flag is true, meanging that the components should still be in motion.
	 * The time since the last update is found and used to calculate the change of each component given the speeds calculated when
	 *  an expansion or retraction is begun.
	 * The bounds on the PatternSelector are adjusted in each update.
	 * As soon as any of the components, and theoretically all of them,
	 *  reach its destination location, all the components are set to their destinations and cease movement.
	 */
	public void update()
	{
		tooltip.bounds.x = info.toolbar.selector.bounds.x + info.toolbar.selector.bounds.width - x;
		tooltip.bounds.y = info.toolbar.selector.bounds.y + info.toolbar.selector.bounds.height - y;
		if (changing)
		{
			long timeElapsed = (System.nanoTime() - lastUpdate)/1000000;	// ms
			folderX += folderXSpeed*timeElapsed;
			folderY += folderYSpeed*timeElapsed;
			width += widthSpeed*timeElapsed;
			height += heightSpeed*timeElapsed;
			names.bounds.height += namesHeightSpeed*timeElapsed;
			names.setLocation(names.bounds.x, names.bounds.y - (int)(namesHeightSpeed*timeElapsed));
			if (expanded)
			{
				// moving from 0,0 to folderX,folderY; WIDTH to expandedWidth; HEIGHT to expandedHeight: expanding
				tooltip.bounds = new Rectangle();
				if (folderX > folderXExpanded || folderY > folderYExpanded || width > expandedWidth || height > expandedHeight)
				{
					folderX = folderXExpanded;
					folderY = folderYExpanded;
					width = expandedWidth;
					height = expandedHeight;
					names.bounds.height = names.expandedHeight;
					names.bounds.y = (int)folderY + FOLDER_HEIGHT + MIDDLE_BUFFER;
					changing = false;
				}
			}
			else
			{
				// moving from folderX,folderY to 0,0; expandedWidth to WIDTH; expandedHeight to HEIGHT: retracting
				tooltip.bounds = new Rectangle(info.toolbar.selector.bounds.x + info.toolbar.selector.bounds.width - x,
						info.toolbar.selector.bounds.y + info.toolbar.selector.bounds.height - y, FOLDER_WIDTH, FOLDER_HEIGHT);
				if (folderX < 0 || folderY < 0 || width < FOLDER_WIDTH || height < FOLDER_HEIGHT)
				{
					folderX = 0;
					folderY = 0;
					width = FOLDER_WIDTH;
					height = FOLDER_HEIGHT;
					changing = false;
				}
			}
			info.toolbar.selector.setBounds(PatternSelector.FOLDER_EXPAND);
			lastUpdate = System.nanoTime();
		}
	}
	
	/**
	 * Sets the location of the folder to the given coordinates and adjusts the icons.
	 * 
	 * @param x - the x-coordinate, the distance from the right side of the PatternSelector box, in pixels
	 * @param y - the y-coordinate, the distance from the bottom of the PatternSelector box, in pixels
	 */
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
		setIcons();
	}
	
	/**
	 * Sets the locations of the PatternIcons relative to the coordinates of this PatternFolder.
	 * Each icon is set to a location on a circle extending from the left side of the folder icon toward the right side.
	 * This circle has a center at 0,FOLDER_HEIGHT/3 and radius of half the height.
	 * The portion of the circle where icons are located runs from -THETA/2 to THETA/2, where THETA is PI/2 (90 degrees).
	 * Each pattern icon is set shifted by the center location, where the center of the circle lies, relative to the coordinates of this folder.
	 * Then the icon is shifted by the radius of the circle times the cosine (for the x) or sine (for the y).
	 */
	public void setIcons()
	{
		double radius = FOLDER_HEIGHT/2;
		for (int i = 0; i < patterns.size(); i++)
		{
			double thetaI = -THETA/2 + i*(THETA/patterns.size());
			int xLoc = (int)Math.round(Math.cos(thetaI)*radius + center.x);
			int yLoc = (int)Math.round(Math.sin(thetaI)*radius + center.y);
			patterns.get(i).setLocation(xLoc, yLoc);
		}
	}
	
	/**
	 * Expands or retracts this folder according to the given boolean.
	 * Depending on the given boolean, speeds of various moving components are calculated 
	 *  in pixels per millisecond according to the total time of an expansion or retraction.
	 * 
	 * @param expand - true if the folder should expand, false if it should retract
	 */
	public void expand(boolean expand)
	{
		if (expand)
		{
			folderXSpeed = folderXExpanded/expansionTime;							// px/ms
			folderYSpeed = folderYExpanded/expansionTime;							// px/ms
			widthSpeed = (expandedWidth - (double)FOLDER_WIDTH)/expansionTime;		// px/ms
			heightSpeed = (expandedHeight - (double)FOLDER_HEIGHT)/expansionTime;	// px/ms
			namesHeightSpeed = names.expandedHeight/expansionTime;					// px/ms
			changing = true;
			lastUpdate = System.nanoTime();
		}
		else
		{
			folderXSpeed = -folderXExpanded/expansionTime;							// px/ms
			folderYSpeed = -folderYExpanded/expansionTime;							// px/ms
			widthSpeed = (FOLDER_WIDTH - (double)expandedWidth)/expansionTime;		// px/ms
			heightSpeed = (FOLDER_HEIGHT - (double)expandedHeight)/expansionTime;	// px/ms
			namesHeightSpeed = -names.expandedHeight/expansionTime;					// px/ms
			changing = true;
			lastUpdate = System.nanoTime();
		}
		expanded = expand;
	}
	
	/**
	 * Returns true if this folder is expanded and false if it is not.
	 * 
	 * @return expanded - true if the folder is expanded, false otherwise
	 */
	public boolean isExpanded()
	{
		return expanded;
	}
	
	/**
	 * Returns the Pattern whose name was clicked on at the given point if the folder is fully expanded.
	 * Null is returned if the pattern is not expanded, the name box was not clicked on, or the name box returns a faulty name.
	 * 
	 * @param bounds - the boundary box of the PatternSelector, used to convert from the on-screen coordinates to the native coordinates used by this folder
	 * @param point - the location of the mouse click on the screen
	 * @return pattern - the pattern that was selected, null if none was clicked on
	 */
	public Pattern mousePressed(Rectangle bounds, Point point)
	{
		if (expanded && !changing)
		{
			String name = names.mousePressed(new Point(point.x - bounds.x - (bounds.width - x), point.y - bounds.y - (bounds.height - y)));
			if (name == null)
			{
				return null;
			}
			for (int i = 0; i < patterns.size(); i++)
			{
				if (patterns.get(i).getName().equals(name))
				{
					return patterns.get(i).getPattern();
				}
			}
		}
		return null;
	}
	
	/**
	 * Draws this folder with the given Graphics2D and the PatternSelector's boundary box.
	 * First, the native x and y coordinates are converted to the coordinate system used to draw relative to the top-left corner of the PatternSelector.
	 * Then, the back of the folder image is drawn followed by the pattern icons.
	 * If the folder is expanded, the PatternNameBox is drawn.
	 * Last the front of the folder image is drawn and then the name of the folder.
	 * 
	 * @param bounds - the bounding box of the PatternSelector
	 * @param g - the Graphics context used by the PatternSelector to render the selection box to an image
	 */
	public void draw(Rectangle bounds, Graphics2D g)
	{
		int x = bounds.width - this.x;
		int y = bounds.height - this.y;
		
		info.imageLoader.get(backIndex).draw((int)folderX + x, (int)folderY + y, g);
		AffineTransform transform = new AffineTransform();
		for (int i = 0; i < patterns.size(); i++)
		{
			transform.setToTranslation(folderX + x, folderY + y);
			g.setTransform(transform);
			patterns.get(i).draw(rescaler, g);
		}
		g.setTransform(new AffineTransform());
		
		if (expanded)
		{
			g.setClip(new Rectangle(0, y, info.screen.width, (int)height));
			transform = new AffineTransform();
			double expandedX = x - (expandedWidth - width)/2;
			transform.translate(expandedX, y);
			g.setTransform(transform);
			names.draw(g);
			g.setClip(null);
		}
		info.imageLoader.get(frontIndex).draw((int)folderX + x, (int)folderY + y, g);
		
		g.setColor(Color.black);
		g.setFont(Information.fontBold);
		g.drawString(name, (int)folderX + x + 15, y + FOLDER_HEIGHT);
		g.setColor(Information.lightBlue);
		g.drawString(name, (int)folderX + x + 15 - 1, y + FOLDER_HEIGHT - 1);
	}
}
