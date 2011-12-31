package pattern;

import io.Listener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.util.ArrayList;

import main.Information;
import main.RollOver;
import main.Tooltip;

/**
 * Represents the menu allowing the user to select a pattern found within the pattern data files.
 * The patterns are organized into folders, represented by PatternFolders, which categorize them into types.
 * The menu loads the patterns into folders and then draws the folders to the screen.
 * 
 * @author Dominic
 */
public class PatternSelector implements Runnable
{
	public ArrayList<PatternFolder> folders;
	
	public boolean visible = false;
	
	private float[] scales = {1f, 1f, 1f, 1f};
	private float[] offsets = new float[4];
	private float alpha = 0f;
	private static float alphaSpeed = 0.05f;
	
	private Information info;
	private static int buffer = 25;					// distance from the edges of the bounding box to the folders
	private int arrowIndex;
	public static final int TOOLBAR_MOVE = 1;
	public static final int FOLDER_EXPAND = 2;
	public static final int INIT = 3;
	
	private long period = 10;						// time between updates in ms

	public Pattern selected;
	
	public Rectangle bounds;
	private RescaleOp rescaler;
	public RollOver arrowRO;
	
	private String patternFolder = "patterns/";		// location of patterns
	
	public Thread arrowROThread;
	public Thread arrowTooltipThread;
	public Tooltip arrowTooltip;
	
	/**
	 * Creates a new PatternSelector with the given Information.
	 * Various fields are initialized, requests are made to the listener, and patterns are loaded from file.
	 * 
	 * @param info - the current Information
	 */
	public PatternSelector(Information info)
	{
		this.info = info;
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON_ALL);
		arrowIndex = info.imageLoader.add("images/arrow.png", "arrow", Transparency.TRANSLUCENT);
		folders = new ArrayList<PatternFolder>();
		selected = null;
		bounds = new Rectangle();
		
		File folder = new File(patternFolder);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			PatternFolder patternFolder = new PatternFolder(files[i].getPath(), 0, 0, info);
			folders.add(patternFolder);
		}
		
		arrowRO = new RollOver(new Rectangle(), info);
		arrowROThread = new Thread(arrowRO);
		arrowROThread.start();
		
		arrowTooltip = new Tooltip(new Rectangle(), "Open the Pattern Selector", info);
		arrowTooltipThread = new Thread(arrowTooltip);
		arrowTooltipThread.start();
		
		setBounds(INIT);
		
		for (int i = 0; i < folders.size(); i++)
		{
			folders.get(i).names.setLocation(folders.get(i).names.bounds.x, folders.get(i).names.bounds.y);		// fully initialize the tooltips
		}
	}
	
	/**
	 * Sets the bounding box, the location of the arrow in the toolbar, and the locations of the folders.
	 * Different actions will be taken depending on the reason for the boundary adjustment so that the necessary components are moved but no more.
	 * 
	 * @param reason - the reason for this boundary adjustment
	 */
	public void setBounds(int reason)
	{
		if (reason == TOOLBAR_MOVE)
		{
			arrowRO.bounds = new Rectangle(info.toolbar.x + 25, info.toolbar.y + info.toolbar.bounds.height/2 - info.imageLoader.get(arrowIndex).getHeight()/2,
					info.imageLoader.get(arrowIndex).getWidth(), info.imageLoader.get(arrowIndex).getHeight());
			arrowTooltip.bounds = arrowRO.bounds;
			
			if (arrowRO.bounds.x >= info.screen.getWidth()/2 && arrowRO.bounds.y >= info.screen.getHeight()/2)
			{
				// bottom-right corner of the screen
				bounds.x = info.toolbar.x - bounds.width;
				bounds.y = info.toolbar.y + info.toolbar.height - bounds.height;
			}
			else if (arrowRO.bounds.x < info.screen.getWidth()/2 && arrowRO.bounds.y >= info.screen.getHeight()/2)
			{
				// bottom-left corner of the screen
				bounds.x = info.toolbar.x;
				bounds.y = info.toolbar.y - bounds.height;
			}
			else if (arrowRO.bounds.x >= info.screen.getWidth()/2 && arrowRO.bounds.y < info.screen.getHeight()/2)
			{
				// top-right corner of the screen
				bounds.x = info.toolbar.x - bounds.width;
				bounds.y = info.toolbar.y;
			}
			else if (arrowRO.bounds.x < info.screen.getWidth()/2 && arrowRO.bounds.y < info.screen.getHeight()/2)
			{
				// top-left corner of the screen
				bounds.x = info.toolbar.x;
				bounds.y = info.toolbar.y + info.toolbar.height;
			}
			for (int i = 0; i < folders.size(); i++)
			{
				folders.get(i).names.setLocation(folders.get(i).names.bounds.x, folders.get(i).names.bounds.y);		// fully initialize the tooltips
			}
		}
		else if (reason == FOLDER_EXPAND || reason == INIT)
		{
			int xLoc = 0;
			for (int i = folders.size() - 1; i >= 0; i--)
			{
				xLoc += buffer + folders.get(i).width;
				folders.get(i).setLocation(xLoc, buffer + (int)folders.get(i).height);
			}
			
			int height = PatternFolder.FOLDER_HEIGHT;
			for (int i = 0; i < folders.size(); i++)
			{
				height = (int)Math.max(height, folders.get(i).height);
			}
			height += 2*buffer;
			bounds.height = height;
			
			int width = (folders.size() + 1)*buffer;
			for (int i = 0; i < folders.size(); i++)
			{
				width += folders.get(i).width;
			}
			bounds.width = width;
			
			if (arrowRO.bounds.x >= info.screen.getWidth()/2 && arrowRO.bounds.y >= info.screen.getHeight()/2)
			{
				// bottom-right corner of the screen
				bounds.x = info.toolbar.x - bounds.width;
				bounds.y = info.toolbar.y + info.toolbar.height - bounds.height;
			}
			else if (arrowRO.bounds.x < info.screen.getWidth()/2 && arrowRO.bounds.y >= info.screen.getHeight()/2)
			{
				// bottom-left corner of the screen
				bounds.x = info.toolbar.x;
				bounds.y = info.toolbar.y - bounds.height;
			}
			else if (arrowRO.bounds.x >= info.screen.getWidth()/2 && arrowRO.bounds.y < info.screen.getHeight()/2)
			{
				// top-right corner of the screen
				bounds.x = info.toolbar.x - bounds.width;
				bounds.y = info.toolbar.y;
			}
			else if (arrowRO.bounds.x < info.screen.getWidth()/2 && arrowRO.bounds.y < info.screen.getHeight()/2)
			{
				// top-left corner of the screen
				bounds.x = info.toolbar.x;
				bounds.y = info.toolbar.y + info.toolbar.height;
			}
			for (int i = 0; i < folders.size(); i++)
			{
				folders.get(i).names.setLocation(folders.get(i).names.bounds.x, folders.get(i).names.bounds.y);		// fully initialize the tooltips
			}
		}
	}
	
	/**
	 * Runs the PatternSelection within its own Thread by continually updating and sleeping.
	 */
	public void run()
	{
		while (true)
		{
			update();
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException e) { }
		}
	}
	
	/**
	 * Updates the PatternSelection by adjusting the alpha value based on whether or not the PatternSelection is currently visible.
	 * Also updates the folders held by the selector.
	 */
	public void update()
	{
		if (visible)
		{
			alpha = Math.min(alpha + alphaSpeed, 1f);
		}
		else
		{
			alpha = Math.max(alpha - alphaSpeed, 0f);
		}
		for (int i = 0; i < folders.size(); i++)
		{
			folders.get(i).update();
		}
	}
	
	/**
	 * Called when the mouse is pressed, this method takes an appropriate action if required.
	 * First, if either the toolbar or the control bar consume the event, the selected pattern is set to null
	 *  and the arrow is checked and, if it was clicked, the selector is switched between visibility.
	 * Otherwise, if the event did not originate from the left mouse button (BUTTON1),
	 *  it is checked to see whether it came from the right mouse button (BUTTON3) and, if so, the selected pattern is set to null.
	 * Otherwise, if the bounds do not contain the event or the selector is invisible, the selected pattern is placed into the grid.
	 * Otherwise, the folders are checked and expanded properly, and if a pattern was selected it is set cloned to the selected pattern. 
	 * 
	 * @param e - the event causing this method invocation.
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!info.toolbar.consumed(e) && !info.controlBar.consumed(e))
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				if (bounds.contains(e.getPoint()) && visible)
				{
					// within the bounds, visible, left mouse button = folders
					boolean inFolder = false;
					for (int i = 0; i < folders.size(); i++)
					{
						if (folders.get(i).isExpanded())
						{
							Pattern pattern = folders.get(i).mousePressed(bounds, e.getPoint());
							if (pattern != null)
							{
								inFolder = true;
								selected = pattern.clone();
								selected.generateFullSizeImage();
							}
						}
						if ((new Rectangle(bounds.x + bounds.width - folders.get(i).x,
								bounds.y + bounds.height - folders.get(i).y,
								(int)folders.get(i).width,
								(int)folders.get(i).height).contains(e.getPoint())))
						{
							for (int j = 0; j < folders.size(); j++)
							{
								folders.get(j).expand(false);
							}
							folders.get(i).expand(true);
							inFolder = true;
						}
					}
					if (!inFolder)
					{
						for (int i = 0; i < folders.size(); i++)
						{
							folders.get(i).expand(false);
						}
					}
				}
				else if (selected != null)
				{
					// not consumed by toolbar or control bar, not in bounds (or invisible), left mouse button = place selection
					Point mouseCell = info.grid.mouseCell();
					for (int x = 0; x < selected.width; x++)
					{
						for (int y = 0; y < selected.height; y++)
						{
							try
							{
								info.map.map[x + mouseCell.x][y + mouseCell.y] = selected.pattern[y][x];
							}
							catch (IndexOutOfBoundsException ex) { System.out.println("!"); }
						}
					}
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON3)
			{
				// not consumed by toolbar or control bar, right mouse button = remove selection
				selected = null;
			}
		}
		else
		{
			// consumed by toolbar or control bar = remove selection, check the arrow
			selected = null;
			if (arrowRO.bounds.contains(e.getLocationOnScreen()))
			{
				visible = !visible;
				for (int i = 0; i < folders.size(); i++)
				{
					folders.get(i).expand(false);
				}
			}
		}
	}
	
	/**
	 * Determines whether the given MouseEvent should be consumed by the PatternSelection or whether it can continue for further processing.
	 * 
	 * @param e - the MouseEvent that has been generated
	 * @return consumed - true if the MouseEvent occurred within the selection area while the PatternSelection was visible
	 *  or if a pattern is selected and neither the Pane now the OperationBar consume the Event,
	 *  signaling that the user is attempting to place the selected pattern; false otherwise
	 */
	public boolean consumed(MouseEvent e)
	{
		if (bounds.contains(e.getPoint()) && visible)
		{
			return true;
		}
		if (selected != null && !info.toolbar.consumed(e) && !info.controlBar.consumed(e))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Draws the PatternSelection to the given Graphics context.
	 * If the alpha value for the PatternSelection is above 0, 
	 *  the selection area is drawn to an image which is in turn drawn to the screen at the proper location with the current alpha value.
	 * Regardless of the alpha value, if the selected pattern is not null, it is drawn at the current mouse location.
	 * Also draws all Tooltips contained within the selector using the standard Graphics.
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		if (alpha > 0.0001f)
		{
			Rectangle bounds = (Rectangle)this.bounds.clone();
			BufferedImage bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics selectorG = bi.getGraphics();
			selectorG.drawImage(drawSelectionAreaToImage(bounds), 0, 0, bounds.width, bounds.height, null);
			scales[0] = scales[1] = scales[2] = scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, bounds.x, bounds.y);
		}
		if (selected != null)
		{
			Point mouseCell = info.grid.mouseCell();
			selected.img.draw((int)info.grid.zoom*mouseCell.x - (int)info.grid.xMap,
					(int)info.grid.zoom*mouseCell.y - (int)info.grid.yMap, g);
		}
		
		arrowTooltip.draw(g);
		for (int i = 0; i < folders.size(); i++)
		{
			folders.get(i).tooltip.draw(g);
			for (int j = 0; j < folders.get(i).names.tooltips.size(); j++)
			{
				folders.get(i).names.tooltips.get(j).draw(g);
			}
		}
	}
	
	/**
	 * Draws the selection area which displays the folders and patterns to an image.
	 * A translucent gray background is drawn, surrounded by a black border.
	 * All the folders are drawn and the resultant image is returned.
	 */
	private BufferedImage drawSelectionAreaToImage(Rectangle bounds)
	{
		BufferedImage img = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		g.setColor(new Color(80, 80, 80, 100));
		g.fillRect(0, 0, bounds.width, bounds.height);
		g.setColor(Color.black);
		g.drawRect(0, 0, bounds.width, bounds.height);
		g.drawRect(1, 1, bounds.width - 2, bounds.height - 2);
		g.drawRect(2, 2, bounds.width - 4, bounds.height - 4);
		
		for (int i = 0; i < folders.size(); i++)
		{
			folders.get(i).draw(bounds, g);
		}
		return img;
	}
	
	/**
	 * Draws the arrow appearing on the pane along with the arrow's RollOver.
	 * The arrow is drawn relative to the screen rather than the Pane's top-left corner.
	 * 
	 * @param g - the provided Graphics context
	 */
	public void drawToImage(Graphics2D g)
	{
		arrowRO.draw(-info.toolbar.x, -info.toolbar.y, g);
		info.imageLoader.get(arrowIndex).draw(arrowRO.bounds.x - info.toolbar.x, arrowRO.bounds.y - info.toolbar.y, g);
	}
}
