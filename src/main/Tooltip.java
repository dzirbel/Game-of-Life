package main;

import java.awt.Graphics;
import java.awt.Rectangle;

public class Tooltip implements Runnable
{
	boolean hovering;
	
	Information info;
	
	long hoveringTime;
	long hoveringStart;
	
	Rectangle bounds;
	
	public Tooltip(Rectangle bounds, Information info)
	{
		this.bounds = bounds;
		this.info = info;
		hovering = false;
		hoveringTime = 0;
		hoveringStart = 0;
	}

	public void run() 
	{
		while (true)
		{
			update();
		}
	}
	
	public void update()
	{
		if (hovering)
		{
			if (!bounds.contains(info.mouse))
			{
				hovering = false;
			}
		}
		else
		{
			
		}
	}
	
	public void draw(Graphics g)
	{
		
	}
	
	public void drawToImage()
	{
		
	}
}
