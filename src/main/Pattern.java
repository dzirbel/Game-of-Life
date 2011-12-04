package main;

import graphics.AcceleratedImage;

import java.awt.image.BufferedImage;

/**
 * Represents a single Pattern made up of alive and dead cells arranged in a two dimensional array of booleans.
 * Patterns are organized into one of five categories based on their function within the simulation:<br>
 * STABLE: these patterns hold their shape unless acted upon by an outside cell<br>
 * OSCILATING: these patterns switch between a finite number of different patterns before repeating<br>
 * SPACEHIP: these patterns move in a certain direction, though they may or may not be able to retain their shape<br>
 * GLIDERGUN: these patterns create a stream of spaceships<br>
 * EXPLODER: these patterns, seemingly small, create a huge "explosion" that expands but eventually stops
 * 
 * @author Dominic
 */
public class Pattern 
{
	AcceleratedImage img;
	
	public boolean[][] pattern;
	
	private Information info;
	public final static int SIZE = 80;
	public final static int STABLE = 1;
	public final static int OSCILATING = 2;
	public final static int SPACESHIP = 3;
	public final static int GLIDERGUN = 4;
	public final static int EXPLODER = 5;
	public int category;
	public int width;
	public int height;
	private int aliveIndex;
	
	public String name;
	
	/**
	 * Creates a new Pattern with the given variables and Information.
	 * The width and height of the image representing this pattern are set to the default, 
	 *  meaning that if the image exceeds these sizes, the entire image will not be shown.
	 * 
	 * @param pattern - the array of booleans that make up the pattern
	 * @param category - the type of pattern that this pattern is
	 * @param name - the name of this pattern
	 * @param info - the current Information
	 */
	public Pattern(boolean[][] pattern, int category, String name, Information info)
	{
		this.pattern = pattern;
		this.category = category;
		this.name = name;
		this.info = info;
		width = pattern[0].length;
		height = pattern.length;
		aliveIndex = info.imageLoader.getIndex("alive");
		img = new AcceleratedImage(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB), BufferedImage.TRANSLUCENT);
	}
	
	/**
	 * Creates a new Pattern with the given variables and Information.
	 * The width and height of the image representing this pattern are set to the given dimensions.
	 * 
	 * @param pattern - the array of booleans that make up the pattern
	 * @param category - the type of pattern that this pattern is
	 * @param name - the name of this pattern
	 * @param width - the width of the image made for this pattern, in pixels
	 * @param height - the height of the image made for this pattern, in pixels
	 * @param info - the current Information
	 */
	public Pattern(boolean[][] pattern, int category, String name, int width, int height, Information info)
	{
		this.pattern = pattern;
		this.category = category;
		this.name = name;
		this.info = info;
		width = pattern[0].length;
		height = pattern.length;
		aliveIndex = info.imageLoader.getIndex("alive");
		img = new AcceleratedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), BufferedImage.TRANSLUCENT);
	}
	
	/**
	 * Sets the width and height of the image representing this pattern to the given dimensions.
	 * This erases the any previous drawing to the image, so calls to generateImage() and generateFullSizeImage() must be recalled.
	 * 
	 * @param width - the width of the image made for this pattern, in pixels
	 * @param height - the height of the image made for this pattern, in pixels
	 */
	public void setSize(int width, int height)
	{
		img = new AcceleratedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), BufferedImage.TRANSLUCENT);
	}
	
	/**
	 * Generates the image used to display the pattern within the selector.
	 * A size for each cell is calculated based on the maximum of the width and height and this is used to scale the alive image.
	 * x- and y-shifts are calculated in case the pattern is not square and does not completely fill the space in one direction.
	 * The image is left blank for dead cells and appropriately scaled and positioned "alive" images are used for alive cells.
	 */
	public void generateImage()
	{
		double cellSize = SIZE/Math.max(width, height);
		int xShift = (int)(SIZE - width*cellSize)/2;
		int yShift = (int)(SIZE - height*cellSize)/2;
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (pattern[y][x])
				{
					img.getGraphics().drawImage(info.imageLoader.get(aliveIndex).getBufferedImage(), (int)(x*cellSize + xShift), (int)(y*cellSize + yShift),
							(int)cellSize, (int)cellSize, null);
				}
			}
		}
	}
	
	/**
	 * Returns a duplicate of the current pattern with the same array, category, name, and Information.
	 */
	public Pattern clone()
	{
		return new Pattern(pattern, category, name, info);
	}
	
	/**
	 * Generates an image that is scaled only based on the current zoom value that is used after the pattern has been selected and is now being dragged around the window.
	 */
	public void generateFullSizeImage()
	{
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (pattern[y][x])
				{
					img.getGraphics().drawImage(info.imageLoader.get(aliveIndex).getBufferedImage(), (int)(x*info.window.zoom), (int)(y*info.window.zoom),
							(int)info.window.zoom, (int)info.window.zoom, null);
//					info.imageLoader.get(aliveIndex).draw((int)(x*info.window.zoom), (int)(y*info.window.zoom),
//							(int)info.window.zoom, (int)info.window.zoom, (Graphics2D)img.getGraphics());
				}
			}
		}
	}
}
