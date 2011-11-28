import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class RollOver implements Runnable
{
	boolean usingMouse = true;
	boolean goingUp = true;
	boolean done = false;
	BufferedImage img = null;
	
	Information info;
	
	float[] scales = {1f, 1f, 1f, 1f};
	float[] offsets = new float[4];
	float alpha = 0f;
	float alphaSpeed = 0.05f;
	
	Rectangle bounds;
	RescaleOp rescaler;
	
	public RollOver(Information info, Rectangle bounds, boolean usingMouse)
	{
		this.info = info;
		this.bounds = bounds;
		this.usingMouse = usingMouse;
		try
		{
			img = ImageIO.read(new File("images/selection.png"));
		}
		catch (IOException e) { System.out.println("rollover error"); }
		if (!usingMouse)
		{
			alphaSpeed = 0.1f;
		}
	}

	public void run() 
	{
		while (true)
		{
			if (usingMouse)
			{
				updateMouse();
			}
			else
			{
				updateNoMouse();
				if (done)
				{
					break;
				}
			}
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e) { }
		}
	}
	
	public void updateMouse()
	{
		if (bounds.contains(info.listener.mouseLocation))
		{
			alpha = Math.min(alpha + alphaSpeed, 1f);
		}
		else
		{
			alpha = Math.max(alpha - alphaSpeed, 0f);
		}
	}
	
	public void updateNoMouse()
	{
		if (goingUp)
		{
			alpha += alphaSpeed;
			if (alpha >= 1)
			{
				alpha = 1;
				goingUp = false;
			}
		}
		else
		{
			alpha -= alphaSpeed;
			if (alpha <= 0)
			{
				done = true;
			}
		}
	}
	
	public void draw(Graphics2D g)
	{
		if (alpha > 0.001f)
		{
			BufferedImage bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics biG = bi.getGraphics();
			biG.drawImage(img, 0, 0, bounds.width, bounds.height, null);
			scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, bounds.x - info.pane.x, bounds.y - info.pane.y);
		}
	}
	
	public void draw(int shiftX, int shiftY, Graphics2D g)
	{
		if (alpha > 0.001f)
		{
			BufferedImage bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics biG = bi.getGraphics();
			biG.drawImage(img, 0, 0, bounds.width, bounds.height, null);
			scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, bounds.x + shiftX, bounds.y + shiftY);
		}
	}
}
