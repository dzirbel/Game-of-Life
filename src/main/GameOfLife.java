package main;

import io.Listener;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

/**
 * The main class that is run to run the Game of Life simulation.
 * 
 * @author Dominic
 */
public class GameOfLife
{
	public boolean leftHeld = false;
	public boolean rightHeld = false;
	public boolean upHeld = false;
	public boolean downHeld = false;
	public boolean minusHeld = false;
	public boolean plusHeld = false;
	private BufferStrategy strategy;
	
	public int moveSpeed = 5;
	private Information info;
	
	public Point mouse;
	
	public Thread mapThread;
	public Thread paneThread;
	
	/**
	 * Runs the simulation by creating a GameOfLife object and calling launch() and then run().
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		GameOfLife gameOfLife = new GameOfLife();
		gameOfLife.launch();
		gameOfLife.run();
	}
	
	/**
	 * Launches the GameOfLife by loading various components, particularly the Information which initializes most of the other classes.
	 */
	public void launch()
	{
		info = new Information();
		info.init(this);
		info.listener.requestNotification(this, "exit", Listener.TYPE_KEY_PRESSED, KeyEvent.VK_ESCAPE);
		info.listener.requestNotification(this, "keyPressed", Listener.TYPE_KEY_PRESSED, Listener.CODE_KEY_ALL);
		info.listener.requestNotification(this, "keyReleased", Listener.TYPE_KEY_RELEASED, Listener.CODE_KEY_ALL);
		mapThread = new Thread(info.map);
		mapThread.start();
		paneThread = new Thread(info.pane);
		paneThread.start();
	}
	
	/**
	 * Runs the simulation by drawing and updating at the maximum speed without sleeping.
	 */
	public void run()
	{
		while (true)
		{
			strategy = info.window.frame.getBufferStrategy();
			Graphics2D g = (Graphics2D)info.window.frame.getBufferStrategy().getDrawGraphics();
			draw(g);
			g.dispose();
			strategy.show();
			update();
		}
	}
	
	/**
	 * Called when the escape key is pressed or the "X" is clicked in the operation bar, immediately closes the program.
	 * 
	 * @param event - the Event that originated this call [not used]
	 */
	public void exit(KeyEvent event)
	{
		System.exit(0);
	}
	
	/**
	 * Called by the Listener when any key is pressed.
	 * Updates the flags determining which of several critical keys are held.
	 * 
	 * @param e - the KeyEvent that triggered the call
	 */
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			upHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			downHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			rightHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			leftHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_MINUS)
		{
			minusHeld = true;
		}
		else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
		{
			plusHeld = true;
		}
	}
	
	/**
	 * Called by the Listener when any key is released.
	 * Updates the flags determining which of several critical keys are held.
	 * 
	 * @param e - the KeyEvent that triggered the call
	 */
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			upHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			downHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			rightHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			leftHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_MINUS)
		{
			minusHeld = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS)
		{
			plusHeld = false;
		}
	}
	
	/**
	 * Updates the simulation by zooming or moving the window if necessary.
	 */
	public void update()
	{
		if (minusHeld && !plusHeld)
		{
			info.window.zoomOut();
		}
		if (plusHeld && !minusHeld)
		{
			info.window.zoomIn();
		}
		if (rightHeld && !leftHeld)
		{
			info.window.xMap += moveSpeed;
			if (info.window.xMap + info.screen.width > info.map.width*info.window.zoom)
			{
				info.window.xMap = info.map.width*(int)info.window.zoom - info.screen.width;
			}
		}
		if (leftHeld && !rightHeld)
		{
			info.window.xMap -= moveSpeed;
			if (info.window.xMap < 0)
			{
				info.window.xMap = 0;
			}
		}
		if (upHeld && !downHeld)
		{
			info.window.yMap -= moveSpeed;
			if (info.window.yMap < 0)
			{
				info.window.yMap = 0;
			}
		}
		if (downHeld && !upHeld)
		{
			info.window.yMap += moveSpeed;
			if (info.window.yMap + info.screen.height > info.map.height*info.window.zoom)
			{
				info.window.yMap = info.map.height*(int)info.window.zoom - info.screen.height;
			}
		}
	}
	
	/**
	 * Draws the simulation to the screen with the Graphics that should originate from the JFrame.
	 * First, the window is drawn, making up the background.
	 * Then, the utility pane is drawn on top of which is the pattern selector.
	 * Finally, the operation bar is drawn in the top right of the screen.
	 * 
	 * @param g - the current Graphics context of the JFrame
	 */
	public void draw(Graphics2D g)
	{
		info.window.draw(g);
		info.pane.draw(g);
		info.pane.selector.draw(g);
		info.opBar.draw(g);
	}
}
