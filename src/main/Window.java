package main;

import io.Listener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Contains the Window that the Game of Life runs within, along with information regarding the zoom, viewing pane, and so on.
 * 
 * @author Dominic
 */
public class Window 
{
	private boolean madeAlive = false;
	
	public double zoom = 20;
	public double maxZoom = 75;
	public double minZoom = 5;
	public double zoomSpeed = 1;
	
	private Image icon = new ImageIcon("images/icon.png").getImage();
	private int aliveIndex;
	
	public JFrame frame;
	
	private Information info;
	public int xMap = 0;
	public int yMap = 0;
	
	/**
	 * Creates a new Window with the given Information.
	 * Initializes and loads the JFrame with the customized icon, sets the Listener to listen to the JFrame, and sets it to be in full-screen exclusive mode.
	 * 
	 * @param info - the current Information
	 */
	public Window(Information info)
	{
		this.info = info;
		frame = new JFrame("Game of Life");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(icon);
		frame.setContentPane(new Panel(info));
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.addKeyListener(info.listener);
		frame.addMouseListener(info.listener);
		frame.addMouseMotionListener(info.listener);
		frame.addMouseWheelListener(info.listener);
		info.device.setFullScreenWindow(frame);
		frame.createBufferStrategy(2);
		aliveIndex = info.imageLoader.getIndex("alive");
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
		info.listener.requestNotification(this, "mouseDragged", Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
	}
	
	/**
	 * Returns the Graphics context generated by the JFrame.
	 * 
	 * @return graphics - the JFrame's Graphics
	 */
	public Graphics2D getGraphics()
	{
		return (Graphics2D)frame.getGraphics();
	}
	
	/**
	 * Zooms in by zoomSpeed.
	 */
	public void zoomIn()
	{
		System.out.println("!");
		zoom = Math.min(zoom + zoomSpeed, maxZoom);
	}
	
	/**
	 * Zooms out by zoomSpeed.
	 */
	public void zoomOut()
	{
		System.out.println("~");
		zoom = Math.max(zoom - zoomSpeed, minZoom);
	}
	
	/**
	 * Returns the cell that the mouse pointer is currently in.
	 * 
	 * @return Point - the cell that the mouse pointer is currently in, in cells
	 */
	public Point mouseCell()
	{
		Point mouseLocation = (Point)(info.mouse).clone();
		mouseLocation.x += xMap;
		mouseLocation.y += yMap;
		mouseLocation.x = (int)(mouseLocation.x/zoom);
		mouseLocation.y = (int)(mouseLocation.y/zoom);
		return mouseLocation;
	}
	
	/**
	 * Called by the Listener when the left mouse button (BUTTON1) is pressed.
	 * If no overlay (the pane, the operation bar, and the pattern selector) consumes the Event, 
	 *  the Window processes the input, either setting the cell clicked on to alive or dead.
	 * Whether the cell was made alive or erased is saved so that if the mouse is dragged that option is continued.
	 * 
	 * @param e - the MouseEvent triggering the call
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!info.opBar.consumed(e) && !info.pane.consumed(e) && !info.pane.selector.consumed(e))
		{
			Point mouseCell = mouseCell();
			int x = mouseCell.x;
			int y = mouseCell.y;
			if (info.map.isAlive(mouseCell))
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
	
	/**
	 * Called by the Listener when the mouse is dragged (held and moved).
	 * The cell over which the mouse has been dragged is set to the made alive boolean, set when the mouse was first pressed.
	 * This has the effect of making each drag either create or delete living cells rather than switch alive/dead.
	 * 
	 * @param e - the MouseEvent triggering the call
	 */
	public void mouseDragged(MouseEvent e)
	{
		if (!info.opBar.consumed(e) && !info.pane.consumed(e) && !info.pane.selector.consumed(e))
		{
			Point mouseCell = mouseCell();
			int x = mouseCell.x;
			int y = mouseCell.y;
			info.map.setAlive(x, y, madeAlive);
		}
	}
	
	/**
	 * Draws the grid and the map.
	 * If the zoom is high enough (zoomed in enough), the full alive image is drawn; otherwise, a green rectangle is used to reduce draw time.
	 * 
	 * @param g - the current Graphics context
	 */
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
						info.imageLoader.get(aliveIndex).draw(x, y, (int)zoom, (int)zoom, g);
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

/**
 * Allows for screenshots and screen recording to capture the screen by providing a more tpyical context in which to draw.
 * 
 * @author Dominic
 */
class Panel extends JPanel
{
	Information info;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new Panel with the given Information.
	 * 
	 * @param info - the current Information
	 */
	public Panel(Information info)
	{
		this.info = info;
	}
	
	/**
	 * Called during a typical drawing method, simply calls the Game of Life's draw method.
	 * This allows for screenshots to be made with a conventional method.
	 */
	public void paintComponent(Graphics g)
	{
		info.gameOfLife.draw((Graphics2D)g);
	}
}
