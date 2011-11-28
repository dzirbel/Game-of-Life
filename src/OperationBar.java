import graphics.AcceleratedImage;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

public class OperationBar implements Runnable
{
	AcceleratedImage opBar;
	
	Information info;
	
	Rectangle bounds;
	Rectangle minimizeBounds;
	Rectangle closeBounds;
	
	public OperationBar(Information info)
	{
		this.info = info;
		opBar = new AcceleratedImage("images/operationBar.png", Transparency.TRANSLUCENT);
		bounds = new Rectangle(info.screen.width - opBar.getBufferedImage().getWidth(), 0, opBar.getBufferedImage().getWidth(), opBar.getBufferedImage().getHeight());
		minimizeBounds = new Rectangle(bounds.x + 5, bounds.y + 5, 40, 40);
		closeBounds = new Rectangle(bounds.x + 45, bounds.y + 5, 40, 40);
	}
	
	public void run()
	{
		
	}
	
	public boolean mousePressed(MouseEvent e)
	{
		if (bounds.contains(e.getX(), e.getY()))
		{
			if (minimizeBounds.contains(e.getX(), e.getY()))
			{
				info.window.frame.setState(Frame.ICONIFIED);
			}
			else if (closeBounds.contains(e.getX(), e.getY()))
			{
				System.exit(0);
			}
			return true;
		}
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		opBar.draw(bounds.x, bounds.y, g);
	}
}
