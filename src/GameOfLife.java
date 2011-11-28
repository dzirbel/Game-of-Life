import graphics.DisplayMonitor;

import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;

/**
 * The main class that is run to run the Game of Life simulation.
 * 
 * @author Dominic
 */
public class GameOfLife
{
	BufferStrategy strategy;
	
	DisplayMode displayMode;
	
	GraphicsDevice device;
	GraphicsEnvironment environment;
	
	int moveSpeed = 5;
	
	Map map;
	
	Listener listener;
	
	Information info;
	
	OperationBar opBar;
	
	Pane pane;
	
	Rectangle screen;
	
	Thread mapThread;
	Thread paneThread;
	Thread opBarThread;
	
	Window window;
	
	public static void main(String[] args)
	{
		GameOfLife m = new GameOfLife();
		m.launch();
		m.run();
	}
	
	public void launch()
	{
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		displayMode = DisplayMonitor.getBestDisplayMode(device);
		screen = new Rectangle(0, 0, displayMode.getWidth(), displayMode.getHeight());
		if (device.isDisplayChangeSupported())
		{
			try
			{
				device.setDisplayMode(displayMode);
			}
			catch(IllegalArgumentException ex) { }
		}
		info = new Information(this);
		listener = new Listener(info);
		info.listener = listener;
		pane = new Pane(info);
		info.pane = pane;
		map = new Map(400, 400, info);
		info.map = map;
		window = new Window(info);
		info.window = window;
		opBar = new OperationBar(info);
		info.opBar = opBar;
		mapThread = new Thread(map);
		mapThread.start();
		paneThread = new Thread(pane);
		paneThread.start();
		opBarThread = new Thread(opBar);
		opBarThread.start();
	}
	
	public void run()
	{
		while (true)
		{
			strategy = window.frame.getBufferStrategy();
			Graphics2D g = (Graphics2D)window.frame.getBufferStrategy().getDrawGraphics();
			draw(g);
			g.dispose();
			strategy.show();
			update();
		}
	}
	
	public void update()
	{
		if (listener.minusHeld && !listener.plusHeld)
		{
			window.zoomOut();
		}
		if (listener.plusHeld && !listener.minusHeld)
		{
			window.zoomIn();
		}
		if (listener.rightHeld && !listener.leftHeld)
		{
			window.xMap += moveSpeed;
			if (window.xMap + screen.width > map.width*window.zoom)
			{
				window.xMap = map.width*(int)window.zoom - screen.width;
			}
		}
		if (listener.leftHeld && !listener.rightHeld)
		{
			window.xMap -= moveSpeed;
			if (window.xMap < 0)
			{
				window.xMap = 0;
			}
		}
		if (listener.upHeld && !listener.downHeld)
		{
			window.yMap -= moveSpeed;
			if (window.yMap < 0)
			{
				window.yMap = 0;
			}
		}
		if (listener.downHeld && !listener.upHeld)
		{
			window.yMap += moveSpeed;
			if (window.yMap + screen.height > map.height*window.zoom)
			{
				window.yMap = map.height*(int)window.zoom - screen.height;
			}
		}
	}
	
	public void draw(Graphics2D g)
	{
		window.draw(g);
		pane.draw(g);
		pane.selector.draw(g);
		opBar.draw(g);
	}
}
