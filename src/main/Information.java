package main;

import graphics.DisplayMonitor;
import graphics.ImageLoader;
import grid.Grid;
import gui.GUI;

import io.Listener;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;

import pattern.PatternFolder;

/**
 * Creates a global information network through a set of global variables.
 * 
 * @author Dominic
 */
public class Information
{
	private AppletOfLife appletOfLife;
	
	/**
	 * The standard light blue color to be used for text and other features.
	 */
	public static Color lightBlue = new Color(0, 163, 231);
	/**
	 * The ControlBar that controls minimization and closing via the mouse.
	 */
	public ControlBar controlBar;
	
	/**
	 * The Diagnostics object used to record and display all information in the program.
	 */
	public Diagnostics diagnostics;
	/**
	 * The optimal DisplayMode found using the DisplayMonitor class.
	 */
	public DisplayMode displayMode;
	
	/**
	 * The standard bold font.
	 */
	public static Font fontBold = new Font(Font.SANS_SERIF, Font.BOLD, 18);
	/**
	 * The standard plain font.
	 */
	public static Font fontPlain = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
	/**
	 * The font used for displaying technical information.
	 */
	public static Font fontTech = new Font(Font.MONOSPACED, Font.PLAIN, 15);
	
	/**
	 * The main GameOfLife object that created this Information.
	 */
	private GameOfLife gameOfLife;
	/**
	 * The GraphicsDevice used to select the best DisplayMode and enter full-screen exclusive mode.
	 */
	public GraphicsDevice device;
	/**
	 * The local monitor configuration's GraphicsEnvironment.
	 */
	public GraphicsEnvironment environment;
	/**
	 * The Grid object.
	 */
	public Grid grid;
	/**
	 * The GUI container that holds all the Views.
	 */
	public GUI gui;
	
	/**
	 * The ImageLoader that handles all image loading and referencing.
	 */
	public ImageLoader imageLoader;
	/**
	 * The current generation of the simulation.
	 */
	public int generation;
	/**
	 * Represents milli-units.
	 */
	public static int MILLI = 1;
	/**
	 * Represents micro-units.
	 */
	public static int MICRO = 2;
	/**
	 * Represents nano-units.
	 */
	public static int NANO = 3;
	
	/**
	 * The Listener that receives all key and mouse input events.
	 */
	public Listener listener;
	
	/**
	 * The current location of the pointer on the screen.
	 */
	public Point mouse;
	
	/**
	 * A Rectangle that represents the screen with x- and y-coordinates of 0 and the width and height of the best DisplayMode.
	 */
	public Rectangle screen;
	
	/**
	 * The Toolbar that constitutes the majority of the GUI.
	 */
	public Toolbar toolbar;
	
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
		appletOfLife = null;
		mouse = new Point(0, 0);
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		displayMode = DisplayMonitor.getBestDisplayMode(device);
		if (device.isDisplayChangeSupported())
		{
			device.setDisplayMode(displayMode);
		}
		screen = new Rectangle(0, 0, device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
		imageLoader = new ImageLoader(false);
		imageLoader.add("images/selection.png", "selection", Transparency.TRANSLUCENT);
		imageLoader.add("images/alive.png", "alive", Transparency.OPAQUE);
		imageLoader.add("images/dead.png", "dead", Transparency.OPAQUE);
		imageLoader.add("images/folderFront.png", "folderFront", Transparency.TRANSLUCENT);
		imageLoader.add("images/folderBack.png", "folderBack", Transparency.TRANSLUCENT);
		imageLoader.add("images/tab.png", "tab", Transparency.TRANSLUCENT);
		imageLoader.get("folderFront").setScale(((double)PatternFolder.FOLDER_WIDTH)/((double)imageLoader.get("folderFront").getWidth()),
				((double)PatternFolder.FOLDER_HEIGHT)/((double)imageLoader.get("folderFront").getHeight()));
		imageLoader.get("folderBack").setScale(((double)PatternFolder.FOLDER_WIDTH)/((double)imageLoader.get("folderBack").getWidth()),
				((double)PatternFolder.FOLDER_HEIGHT)/((double)imageLoader.get("folderBack").getHeight()));
		listener = new Listener();
		diagnostics = new Diagnostics(this);
		toolbar = new Toolbar(this);
		grid = new Grid(this);
		controlBar = new ControlBar(this);
		generation = 0;
		gui = new GUI(this);
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_MOVED, 0);
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_DRAGGED, 0);
	}
	
	public void init(AppletOfLife appletOfLife)
	{
		this.appletOfLife = appletOfLife;
		gameOfLife = null;
		mouse = new Point(0, 0);
		imageLoader = new ImageLoader(false);
		imageLoader.add("selection.png", "selection", Transparency.TRANSLUCENT);
		imageLoader.add("alive.png", "alive", Transparency.OPAQUE);
		imageLoader.add("dead.png", "dead", Transparency.OPAQUE);
		imageLoader.add("folderFront.png", "folderFront", Transparency.TRANSLUCENT);
		imageLoader.add("folderBack.png", "folderBack", Transparency.TRANSLUCENT);
		imageLoader.add("tab.png", "tab", Transparency.TRANSLUCENT);
		imageLoader.get("folderFront").setScale(((double)PatternFolder.FOLDER_WIDTH)/((double)imageLoader.get("folderFront").getWidth()),
				((double)PatternFolder.FOLDER_HEIGHT)/((double)imageLoader.get("folderFront").getHeight()));
		imageLoader.get("folderBack").setScale(((double)PatternFolder.FOLDER_WIDTH)/((double)imageLoader.get("folderBack").getWidth()),
				((double)PatternFolder.FOLDER_HEIGHT)/((double)imageLoader.get("folderBack").getHeight()));
		screen = new Rectangle(0, 0, appletOfLife.getWidth(), appletOfLife.getHeight());
		listener = new Listener();
		diagnostics = new Diagnostics(this);
		toolbar = new Toolbar(this);
		grid = new Grid(this);
		controlBar = new ControlBar(this);
		generation = 0;
		gui = new GUI(this);
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_MOVED, 0);
		listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_DRAGGED, 0);
	}
	
	public int load(String imageName, String referenceName, int transparency)
	{
		if (gameOfLife != null)
		{
			return imageLoader.add("images/" + imageName, referenceName, transparency);
		}
		else if (appletOfLife != null)
		{
			return imageLoader.add(imageName, referenceName, transparency);
		}
		return -1;
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
	
	/**
	 * Converts the given value with units firstUnit to units secondUnit.
	 * This method only supports conversions between milli-, micro-, and nano-seconds.
	 * 
	 * @param firstUnit - the unit of the given value
	 * @param secondUnit - the unit which the returned value has
	 * @param value - the value to be converted
	 * @return conversion - the given value in the given destination units
	 */
	public static long time(int firstUnit, int secondUnit, long value)
	{
		if (firstUnit == MILLI)
		{
			if (firstUnit == MILLI)
			{
				return value;
			}
			else if (firstUnit == MICRO)
			{
				return value*1000;
			}
			else if (firstUnit == NANO)
			{
				return value*1000000;
			}
		}
		else if (firstUnit == MICRO)
		{
			if (firstUnit == MILLI)
			{
				return value/1000;
			}
			else if (firstUnit == MICRO)
			{
				return value;
			}
			else if (firstUnit == NANO)
			{
				return value*1000;
			}
		}
		else if (firstUnit == NANO)
		{
			if (firstUnit == MILLI)
			{
				return value/1000000;
			}
			else if (firstUnit == MICRO)
			{
				return value/1000;
			}
			else if (firstUnit == NANO)
			{
				return value;
			}
		}
		return -1;
	}
	
	public Graphics getGraphics()
	{
		if (gameOfLife != null)
		{
			return gameOfLife.frame.getBufferStrategy().getDrawGraphics();
		}
		else if (appletOfLife != null)
		{
			
		}
		return null;
	}
	
	public void minimize()
	{
		if (gameOfLife != null)
		{
			gameOfLife.frame.setState(Frame.ICONIFIED);
		}
	}
	
	public boolean isApplet()
	{
		if (appletOfLife != null)
		{
			return true;
		}
		return false;
	}
}
