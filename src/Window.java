import graphics.AcceleratedImage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Window 
{
	AcceleratedImage dead;
	AcceleratedImage alive;
	
	boolean madeAlive = false;
	
	double zoom = 20;
	double maxZoom = 75;
	double minZoom = 5;
	double zoomSpeed = 1;
	
	Image icon = new ImageIcon("images/icon.png").getImage();
	
	JFrame frame;
	
	Information info;
	int xMap = 0;
	int yMap = 0;
	
	public Window(Information info)
	{
		this.info = info;
		dead = new AcceleratedImage("images/dead.png", Transparency.OPAQUE);
		alive = new AcceleratedImage("images/alive.png", Transparency.OPAQUE);
		frame = new JFrame("Game of Life");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.addKeyListener(info.listener);
		frame.addMouseListener(info.main.listener);
		frame.addMouseMotionListener(info.main.listener);
		frame.addMouseWheelListener(info.main.listener);
		info.main.device.setFullScreenWindow(frame);
		frame.createBufferStrategy(2);
	}
	
	public Graphics2D getGraphics()
	{
		return (Graphics2D)frame.getGraphics();
	}
	
	public void zoomIn()
	{
		zoom = Math.min(zoom + zoomSpeed, maxZoom);
	}
	
	public void zoomOut()
	{
		zoom = Math.max(zoom - zoomSpeed, minZoom);
	}
	
	public Point mouseCell()
	{
		Point mouseLocation = (Point)(info.listener.mouseLocation).clone();
		mouseLocation.x += xMap;
		mouseLocation.y += yMap;
		mouseLocation.x = (int)(mouseLocation.x/zoom);
		mouseLocation.y = (int)(mouseLocation.y/zoom);
		return mouseLocation;
	}
	
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			Point mouseCell = mouseCell();
			int x = mouseCell.x;
			int y = mouseCell.y;
			if (info.map.isAlive(x, y))
			{
				info.map.setAlive(x, y, false);
				madeAlive = false;
			}
			else
			{
				info.map.setAlive(x, y, true);
				madeAlive = true;
			}
		}
	}
	
	public void mouseDragged()
	{
		Point mouseCell = mouseCell();
		int x = mouseCell.x;
		int y = mouseCell.y;
		if (madeAlive)
		{
			info.map.setAlive(x, y, true);
		}
		else
		{
			info.map.setAlive(x, y, false);
		}
	}
	
	public void draw(Graphics2D g)
	{
		if (zoom > 15)
		{
			for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
			{
				for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
				{
					if (info.map.isAlive((x + (int)xMap)/(int)zoom, (y + (int)yMap)/(int)zoom))
					{
						alive.draw(x, y, (int)zoom, (int)zoom, g);
					}
					else
					{
						g.setColor(new Color(0, 0, 0));
						g.fillRect(x, y, (int)zoom, (int)zoom);
					}
				}
			}
		}
		else
		{
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, info.screen.width, info.screen.height);
			for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
			{
				for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
				{
					if (info.map.isAlive((x + (int)xMap)/(int)zoom, (y + (int)yMap)/(int)zoom))
					{
						g.setColor(new Color(0, 215, 10));
						g.fillRect(x, y, (int)zoom, (int)zoom);
					}
				}
			}
		}
		g.setColor(new Color(50, 50, 50));
		for (int x = -(int)(xMap%zoom); x < info.screen.width; x += zoom)
		{
			g.drawLine(x, 0, x, info.screen.height);
		}
		for (int y = -(int)(yMap%zoom); y < info.screen.height; y += zoom)
		{
			g.drawLine(0, y, info.screen.width, y);
		}
	}
}
