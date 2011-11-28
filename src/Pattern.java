import graphics.AcceleratedImage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class Pattern 
{
	AcceleratedImage alive;
	
	boolean[][] pattern;
	Image img;
	
	Information info;
	public final static int STABLE = 1;
	public final static int OSCILATING = 2;
	public final static int SPACESHIP = 3;
	public final static int GLIDERGUN = 4;
	public final static int EXPLODER = 5;
	int category;
	
	String name;
	
	public Pattern(boolean[][] pattern, int category, String name, Information info)
	{
		this.pattern = pattern;
		this.category = category;
		this.name = name;
		this.info = info;
		alive = new AcceleratedImage("images/alive.png", Transparency.OPAQUE);
		img = new BufferedImage(120, 120, BufferedImage.TRANSLUCENT);
	}
	
	public void generateImage()
	{
		int width = pattern[0].length;
		int height = pattern.length;
		int size = Math.max(width, height);
		double cellSize = 80/size;
		int xShift = (80 - (int)(width*cellSize))/2;
		int yShift = (80 - (int)(height*cellSize))/2;
		for (int x = 0; x < pattern[0].length; x++)
		{
			for (int y = 0; y < pattern.length; y++)
			{
				if (pattern[y][x])
				{
					alive.draw((int)(x*cellSize + 20) + xShift, (int)(y*cellSize + 20) + yShift,
							(int)cellSize, (int)cellSize, (Graphics2D)img.getGraphics());
				}
			}
		}
	}
	
	public Pattern clone()
	{
		return new Pattern(pattern, category, name, info);
	}
	
	public void generateFullSizeImage()
	{
		for (int x = 0; x < pattern[0].length; x++)
		{
			for (int y = 0; y < pattern.length; y++)
			{
				if (pattern[y][x])
				{
					alive.draw((int)(x*info.window.zoom), (int)(y*info.window.zoom),
							(int)info.window.zoom, (int)info.window.zoom, (Graphics2D)img.getGraphics());
				}
			}
		}
	}
}
