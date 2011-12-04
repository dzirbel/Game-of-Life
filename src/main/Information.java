package main;

import graphics.DisplayMonitor;
import graphics.ImageLoader;

import io.Listener;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

/**
 * Creates a global information network through a set of global variables.
 * 
 * @author Dominic
 */
public class Information
{
	/**
	 * The optimal DisplayMode found using the DisplayMonitor class.
	 */
	public DisplayMode displayMode;
	
	/**
	 * The main GameOfLife object that created this Information.
	 */
	public GameOfLife gameOfLife;
	/**
	 * The GraphicsDevice used to select the best DisplayMode and enter full-screen exclusive mode.
	 */
	public GraphicsDevice device;
	/**
	 * The local monitor configuration's GraphicsEnvironment.
	 */
	public GraphicsEnvironment environment;
	
	/**
	 * The ImageLoader that handles all image loading and referencing.
	 */
	public ImageLoader imageLoader;
	/**
	 * The current generation of the simulation.
	 */
	public int generation;
	
	/**
	 * The Listener that receives all key and mouse input events.
	 */
	public Listener listener;
	
	/**
	 * The Map object that holds all the information regarding the states of the cells.
	 */
	public Map map;
	
	/**
	 * The OperationBar that controls minimization and closing via the mouse.
	 */
	public OperationBar opBar;
	
	/**
	 * The Pane that constitutes the majority of the GUI.
	 */
	public Pane pane;
	/**
	 * The current location of the pointer on the screen.
	 */
	public Point mouse;
	
	/**
	 * A Rectangle that represents the screen with x- and y-coordinates of 0 and the width and height of the best DisplayMode.
	 */
	public Rectangle screen;
	
	/**
	 * The Window object.
	 */
	public Window window;
	
	/**
	 * Initializes this Information with the given GameOfLife.
	 * The Information's GameOfLife is set to the given one.
	 * The GraphicsEnvironment is created.
	 * The GraphicsDevice is initialized.
	 * The optimal DisplayMode is initialized using the DisplayMonitor class.
	 * The screen Rectangle is created with the width and height of the DisplayMode.
	 * The ImageLoader is initialized.
	 * The Listener is initialized.
	 * The Pane is initialized.
	 * The Map is initialized with a width and height of 400.
	 * The Window is initialized.
	 * The OperationBar is initialized.
	 * The current generation is set to 0.
	 * 
	 * @param gameOfLife - the main GameOfLife object
	 */
	public void init(GameOfLife gameOfLife)
	{
		this.gameOfLife = gameOfLife;
		mouse = new Point(0, 0);
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		displayMode = DisplayMonitor.getBestDisplayMode(device);
		screen = new Rectangle(0, 0, displayMode.getWidth(), displayMode.getHeight());
		imageLoader = new ImageLoader(false);
		imageLoader.add("images/selection.png", "selection", Transparency.TRANSLUCENT);
		imageLoader.add("images/alive.png", "alive", Transparency.OPAQUE);
		imageLoader.add("images/dead.png", "dead", Transparency.OPAQUE);
		listener = new Listener();
		pane = new Pane(this);
		map = new Map(400, 400, this);
		window = new Window(this);
		opBar = new OperationBar(this);
		generation = 0;
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_MOVED, 0);
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_DRAGGED, 0);
	}
	
	/**
	 * Called by the Listener when the mouse is moved.
	 * Sets the current mouse location to the source of the given MouseEvent, keeping it in sync with the pointer.
	 * 
	 * @param event - the trigger of the call
	 */
	public void mouseMoved(MouseEvent event)
	{
		mouse = event.getLocationOnScreen();
	}
}
