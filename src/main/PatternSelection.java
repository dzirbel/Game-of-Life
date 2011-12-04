package main;

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

/**
 * Represents the selector that is used to pick one of the preset patterns.
 * 
 * @author Dominic
 */
public class PatternSelection implements Runnable
{
	private boolean f = false;
	private boolean t = true;
	private boolean[][] blockPattern = {		{t, t},
												{t, t}};
	
	private boolean[][] beehivePattern = {		{f, t, t, f}, 
												{t, f, f, t}, 
												{f, t, t, f}};
	
	private boolean[][] loafPattern = {			{f, t, t, f},
												{t, f, f, t},
												{f, t, f, t},
												{f, f, t, f}};
	
	private boolean[][] boatPattern = {			{t, t, f},
												{t, f, t},
												{f, t, f}};
	
	private boolean[][] blinkerPattern = {		{t, t, t}};
	
	private boolean[][] toadPattern = {			{f, t, t, t},
												{t, t, t, f}};
	
	private boolean[][] beaconPattern = {		{t, t, f, f},
												{t, f, f, f},
												{f, f, f, t},
												{f, f, t, t}};
	
	private boolean[][] pulsarPattern = {		{f, t, f},
												{t, t, t},
												{t, f, t},
												{t, t, t},
												{f, t, f}};
	
	private boolean[][] gliderPattern = {		{f, f, t},
												{t, f, t},
												{f, t, t}};
	
	private boolean[][] lightWeightPattern = {	{t, f, f, t, f},
												{f, f, f, f, t},
												{t, f, f, f, t},
												{f, t, t, t, t}};
	
	private boolean[][] mediumWeightPattern = {	{f, f, t, f, f, f},
												{t, f, f, f, t, f},
												{f, f, f, f, f, t},
												{t, f, f, f, f, t},
												{f, t, t, t, t, t}};
	
	private boolean[][] heavyWeightPattern = {	{f, f, t, t, f, f, f},
												{t, f, f, f, f, t, f},
												{f, f, f, f, f, f, t},
												{t, f, f, f, f, f, t},
												{f, t, t, t, t, t, t}};
	
	private boolean[][] queenBeePattern = {		{t, t, f, f},
												{f, f, t, f},
												{f, f, f, t},
												{f, f, f, t},
												{f, f, f, t},
												{f, f, f, t},
												{f, f, t, f},
												{t, t, f, f}};
	
	private boolean[][] gosperGunPattern = {	{f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f},
												{f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f},
												{f, f, f, f, f, f, f, f, f, f, f, f, t, t, f, f, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, t, t},
												{f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, t, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, t, t},
												{t, t, f, f, f, f, f, f, f, f, t, f, f, f, f, f, t, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f},
												{t, t, f, f, f, f, f, f, f, f, t, f, f, f, t, f, t, t, f, f, f, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f},
												{f, f, f, f, f, f, f, f, f, f, t, f, f, f, f, f, t, f, f, f, f, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f},
												{f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f},
												{f, f, f, f, f, f, f, f, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f}};
	
	private boolean[][] rpentominoPattern = {	{f, t, t},
												{t, t, f},
												{f, t, f}};
	
	private boolean[][] dieHardPattern = {		{f, f, f, f, f, f, t, f},
												{t, t, f, f, f, f, f, f},
												{f, t, f, f, f, t, t, t}};
	
	private boolean[][] acornPattern = {		{f, t, f, f, f, f, f},
												{f, f, f, t, f, f, f},
												{t, t, f, f, t, t, t}};
	public boolean visible = false;
	BufferedImage img;
	
	private float[] scales = {1f, 1f, 1f, 1f};
	private float[] offsets = new float[4];
	private float alpha = 0f;
	private float alphaSpeed = 0.05f;
	
	private Information info;
	public static int numPatterns = 17;
	public static  int numStable = 4;
	public static  int numOscilators = 4;
	public static  int numSpaceships = 5;
	public static  int numGliderGuns = 1;
	public static  int numExploders = 3;
	public static  int numRows = 5;
	public static  int numColumns = 5;
	private int width = 600;
	private int height = 600;
	private int x;
	private int y;
	private int arrowIndex;
	
	private long period = 10;
	
	public Pattern block;
	public Pattern beehive;
	public Pattern loaf;
	public Pattern boat;
	public Pattern blinker;
	public Pattern toad;
	public Pattern beacon;
	public Pattern pulsar;
	public Pattern glider;
	public Pattern lightWeight;
	public Pattern mediumWeight;
	public Pattern heavyWeight;
	public Pattern queenBee;
	public Pattern gosperGun;
	public Pattern rpentomino;
	public Pattern dieHard;
	public Pattern acorn;
	public Pattern selected;
	
	private Rectangle arrowBounds;
	private Rectangle selectionArea;
	private RescaleOp rescaler;
	public RollOver arrowRO;
	public RollOver blockRO;
	public RollOver beehiveRO;
	public RollOver loafRO;
	public RollOver boatRO;
	public RollOver blinkerRO;
	public RollOver toadRO;
	public RollOver beaconRO;
	public RollOver pulsarRO;
	public RollOver gliderRO;
	public RollOver lightWeightRO;
	public RollOver mediumWeightRO;
	public RollOver heavyWeightRO;
	public RollOver queenBeeRO;
	public RollOver gosperGunRO;
	public RollOver rpentominoRO;
	public RollOver dieHardRO;
	public RollOver acornRO;
	
	public Thread arrowROThread;
	public Thread blockROThread;
	public Thread beehiveROThread;
	public Thread loafROThread;
	public Thread boatROThread;
	public Thread blinkerROThread;
	public Thread toadROThread;
	public Thread beaconROThread;
	public Thread pulsarROThread;
	public Thread gliderROThread;
	public Thread lightWeightROThread;
	public Thread mediumWeightROThread;
	public Thread heavyWeightROThread;
	public Thread queenbeeROThread;
	public Thread gosperGunROThread;
	public Thread rpentominoROThread;
	public Thread dieHardROThread;
	public Thread acornROThread;
	
	/**
	 * Creates a new PatternSelection from the given Information.
	 * 
	 * @param info
	 */
	public PatternSelection(Information info)
	{
		this.info = info;
		info.listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
		arrowIndex = info.imageLoader.add("images/arrow.png", "arrow", Transparency.TRANSLUCENT);
		
		arrowRO = new RollOver(new Rectangle(), info);
		blockRO = new RollOver(new Rectangle(), info);
		beehiveRO = new RollOver(new Rectangle(), info);
		loafRO = new RollOver(new Rectangle(), info);
		boatRO = new RollOver(new Rectangle(), info);
		blinkerRO = new RollOver(new Rectangle(), info);
		toadRO = new RollOver(new Rectangle(), info);
		beaconRO = new RollOver(new Rectangle(), info);
		pulsarRO = new RollOver(new Rectangle(), info);
		gliderRO = new RollOver(new Rectangle(), info);
		lightWeightRO = new RollOver(new Rectangle(), info);
		mediumWeightRO = new RollOver(new Rectangle(), info);
		heavyWeightRO = new RollOver(new Rectangle(), info);
		queenBeeRO = new RollOver(new Rectangle(), info);
		gosperGunRO = new RollOver(new Rectangle(), info);
		rpentominoRO = new RollOver(new Rectangle(), info);
		dieHardRO = new RollOver(new Rectangle(), info);
		acornRO = new RollOver(new Rectangle(), info);
		
		setBounds();
		
		arrowROThread = new Thread(arrowRO);
		blockROThread = new Thread(blockRO);
		beehiveROThread = new Thread(beehiveRO);
		loafROThread = new Thread(loafRO);
		boatROThread = new Thread(boatRO);
		blinkerROThread = new Thread(blinkerRO);
		toadROThread = new Thread(toadRO);
		beaconROThread = new Thread(beaconRO);
		pulsarROThread = new Thread(pulsarRO);
		gliderROThread = new Thread(gliderRO);
		lightWeightROThread = new Thread(lightWeightRO);
		mediumWeightROThread = new Thread(mediumWeightRO);
		heavyWeightROThread = new Thread(heavyWeightRO);
		queenbeeROThread = new Thread(queenBeeRO);
		gosperGunROThread = new Thread(gosperGunRO);
		rpentominoROThread = new Thread(rpentominoRO);
		dieHardROThread = new Thread(dieHardRO);
		acornROThread = new Thread(acornRO);
		
		block = new Pattern(blockPattern, Pattern.STABLE, "Block", info);
		beehive = new Pattern(beehivePattern, Pattern.STABLE, "Beehive", info);
		loaf = new Pattern(loafPattern, Pattern.STABLE, "Loaf", info);
		boat = new Pattern(boatPattern, Pattern.STABLE, "Boat", info);
		blinker = new Pattern(blinkerPattern, Pattern.OSCILATING, "Blinker", info);
		toad = new Pattern(toadPattern, Pattern.OSCILATING, "Toad", info);
		beacon = new Pattern(beaconPattern, Pattern.OSCILATING, "Beacon", info);
		pulsar = new Pattern(pulsarPattern, Pattern.OSCILATING, "Pulsar", info);
		glider = new Pattern(gliderPattern, Pattern.SPACESHIP, "Glider", info);
		lightWeight = new Pattern(lightWeightPattern, Pattern.SPACESHIP, "LWSS", info);
		mediumWeight = new Pattern(mediumWeightPattern, Pattern.SPACESHIP, "MWSS", info);
		heavyWeight = new Pattern(heavyWeightPattern, Pattern.SPACESHIP, "HWSS", info);
		queenBee = new Pattern(queenBeePattern, Pattern.SPACESHIP, "Queen Bee", info);
		gosperGun = new Pattern(gosperGunPattern, Pattern.GLIDERGUN, "Gosper Gun", info);
		rpentomino = new Pattern(rpentominoPattern, Pattern.EXPLODER, "R-pentomino", info);
		dieHard = new Pattern(dieHardPattern, Pattern.EXPLODER, "Die Hard", info);
		acorn = new Pattern(acornPattern, Pattern.EXPLODER, "Acorn", info);
		selected = null;
		
		arrowROThread.start();
		blockROThread.start();
		beehiveROThread.start();
		loafROThread.start();
		boatROThread.start();
		blinkerROThread.start();
		toadROThread.start();
		beaconROThread.start();
		pulsarROThread.start();
		gliderROThread.start();
		lightWeightROThread.start();
		mediumWeightROThread.start();
		heavyWeightROThread.start();
		queenbeeROThread.start();
		gosperGunROThread.start();
		rpentominoROThread.start();
		dieHardROThread.start();
		acornROThread.start();
		
		block.generateImage();
		beehive.generateImage();
		loaf.generateImage();
		boat.generateImage();
		blinker.generateImage();
		toad.generateImage();
		beacon.generateImage();
		pulsar.generateImage();
		glider.generateImage();
		lightWeight.generateImage();
		mediumWeight.generateImage();
		heavyWeight.generateImage();
		queenBee.generateImage();
		gosperGun.generateImage();
		rpentomino.generateImage();
		dieHard.generateImage();
		acorn.generateImage();
		
		img = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
	}
	
	/**
	 * Sets the current bounding boxes based on the location of the Pane.
	 * The PatternSelection can take one of several locations relative to the Pane based on whether it can fit within the space provided in some directions of the Pane.
	 */
	public void setBounds()
	{
		arrowBounds = new Rectangle(info.pane.x + 25, info.pane.y + info.pane.bounds.height/2 - info.imageLoader.get(arrowIndex).getHeight()/2,
				info.imageLoader.get(arrowIndex).getWidth(), info.imageLoader.get(arrowIndex).getHeight());
		arrowRO.bounds = arrowBounds;
		selectionArea = new Rectangle(x, y, width, height);
		if (arrowBounds.x >= info.screen.getWidth()/2 && arrowBounds.y >= info.screen.getHeight()/2)
		{
			x = info.pane.x - width;
			y = info.pane.y + info.pane.height - height;
		}
		if (arrowBounds.x < info.screen.getWidth()/2 && arrowBounds.y >= info.screen.getHeight()/2)
		{
			x = info.pane.x;
			y = info.pane.y - height;
		}
		if (arrowBounds.x >= info.screen.getWidth()/2 && arrowBounds.y < info.screen.getHeight()/2)
		{
			x = info.pane.x - width;
			y = info.pane.y;
		}
		if (arrowBounds.x < info.screen.getWidth()/2 && arrowBounds.y < info.screen.getHeight()/2)
		{
			x = info.pane.x;
			y = info.pane.y + info.pane.height;
		}
		blockRO.bounds = new Rectangle(				x, 			y,       	120, 120);
		beehiveRO.bounds = new Rectangle(			x, 			y + 120, 	120, 120);
		loafRO.bounds = new Rectangle(					x, 			y + 240, 	120, 120);
		boatRO.bounds = new Rectangle(					x, 			y + 360, 	120, 120);
		blinkerRO.bounds = new Rectangle(			x + 120,	y, 			120, 120);
		toadRO.bounds = new Rectangle(					x + 120,	y + 120, 	120, 120);
		beaconRO.bounds = new Rectangle(				x + 120,	y + 240, 	120, 120);
		pulsarRO.bounds = new Rectangle(				x + 120,	y + 360, 	120, 120);
		gliderRO.bounds = new Rectangle(				x + 240,	y, 			120, 120);
		lightWeightRO.bounds = new Rectangle(	x + 240,	y + 120, 	120, 120);
		mediumWeightRO.bounds = new Rectangle(	x + 240,	y + 240, 	120, 120);
		heavyWeightRO.bounds = new Rectangle(	x + 240,	y + 360, 	120, 120);
		queenBeeRO.bounds = new Rectangle(			x + 240,	y + 480, 	120, 120);
		gosperGunRO.bounds = new Rectangle(		x + 360,	y,			120, 120);
		rpentominoRO.bounds = new Rectangle(		x + 480,	y, 			120, 120);
		dieHardRO.bounds = new Rectangle(			x + 480,	y + 120, 	120, 120);
		acornRO.bounds = new Rectangle(				x + 480,	y + 240, 	120, 120);
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
	}
	
	/**
	 * Called by the Listener when the left mouse button (BUTTON1) has been pressed.
	 * If the click originated within the selection area and the PatternSelection is visible, 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (!info.pane.consumed(e) && !info.opBar.consumed(e))
		{
			if (selectionArea.contains(e.getPoint()) && visible)
			{
				if (blockRO.bounds.contains(e.getPoint()))
				{
					selected = block.clone();
				}
				else if (beehiveRO.bounds.contains(e.getPoint()))
				{
					selected = beehive.clone();
				}
				else if (loafRO.bounds.contains(e.getPoint()))
				{
					selected = loaf.clone();
				}
				else if (boatRO.bounds.contains(e.getPoint()))
				{
					selected = boat.clone();
				}
				else if (blinkerRO.bounds.contains(e.getPoint()))
				{
					selected = blinker.clone();
				}
				else if (toadRO.bounds.contains(e.getPoint()))
				{
					selected = toad.clone();
				}
				else if (beaconRO.bounds.contains(e.getPoint()))
				{
					selected = beacon.clone();
				}
				else if (pulsarRO.bounds.contains(e.getPoint()))
				{
					selected = pulsar.clone();
				}
				else if (gliderRO.bounds.contains(e.getPoint()))
				{
					selected = glider.clone();
				}
				else if (lightWeightRO.bounds.contains(e.getPoint()))
				{
					selected = lightWeight.clone();
				}
				else if (mediumWeightRO.bounds.contains(e.getPoint()))
				{
					selected = mediumWeight.clone();
				}
				else if (heavyWeightRO.bounds.contains(e.getPoint()))
				{
					selected = heavyWeight.clone();
				}
				else if (queenBeeRO.bounds.contains(e.getPoint()))
				{
					selected = queenBee.clone();
				}
				else if (gosperGunRO.bounds.contains(e.getPoint()))
				{
					selected = gosperGun.clone();
				}
				else if (rpentominoRO.bounds.contains(e.getPoint()))
				{
					selected = rpentomino.clone();
				}
				else if (dieHardRO.bounds.contains(e.getPoint()))
				{
					selected = dieHard.clone();
				}
				else if (acornRO.bounds.contains(e.getPoint()))
				{
					selected = acorn.clone();
				}
				else
				{
					selected = null;
				}
				if (selected != null)
				{
					selected.setSize((int)(selected.width*info.window.zoom), (int)(selected.height*info.window.zoom));
					selected.generateFullSizeImage();
				}
			}
			else if (selected != null)
			{
				Point mouseCell = info.window.mouseCell();
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
		else
		{
			selected = null;
			if (arrowBounds.contains(e.getLocationOnScreen()))
			{
				visible = !visible;
			}
		}
	}
	
	/**
	 * Determines whether the given MouseEvent should be consumed by the PatternSelection or whether it can continue for further processing.
	 * 
	 * @param e - the MouseEvent that has been generated
	 * @return consumed - true if the MouseEvent occurred within the selection area while the PatternSelection was visible
	 *  or if a pattern is selected and neither the Pane now the OperationBar consume the Event,
	 *   signaling that the user is attempting to place the selected pattern; false otherwise
	 */
	public boolean consumed(MouseEvent e)
	{
		if (selectionArea.contains(e.getPoint()) && visible)
		{
			return true;
		}
		if (selected != null && !info.pane.consumed(e) && !info.opBar.consumed(e))
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
	 * 
	 * @param g - the current Graphics context
	 */
	public void draw(Graphics2D g)
	{
		if (alpha > 0.0001f)
		{
			drawSelectionAreaToImage();
			BufferedImage bi = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
			Graphics paneG = bi.getGraphics();
			paneG.drawImage(img, 0, 0, 600, 600, null);
			scales[0] = scales[1] = scales[2] = scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, x, y);
		}
		if (selected != null)
		{
			Point mouseCell = info.window.mouseCell();
			selected.img.draw((int)info.window.zoom*mouseCell.x - info.window.xMap,
					(int)info.window.zoom*mouseCell.y - info.window.yMap, g);
		}
	}
	
	/**
	 * Draws the selection area to the BufferedImage img.
	 * First, the background rectangle is drawn with a transparent gray, surrounded by a black border.
	 * Next, the backgrounds of the various patterns are bordered with solid black and filled with a transparent white.
	 * The name captions are then drawn below the pattern images.
	 * The RollOvers are drawn and then the pictures of the patterns themselves are drawn.
	 */
	private void drawSelectionAreaToImage()
	{
		img = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		g.setColor(new Color(80, 80, 80, 100));
		g.fillRect(0, 0, 600, 600);
		g.setColor(Color.black);
		g.drawRect(0, 0, 600, 600);
		g.drawRect(1, 1, 598, 598);
		g.drawRect(2, 2, 596, 596);
		
		g.setColor(Color.black);
		g.drawRect(blockRO.bounds.x - x, blockRO.bounds.y - y, blockRO.bounds.width, blockRO.bounds.height);
		g.drawRect(beehiveRO.bounds.x - x, beehiveRO.bounds.y - y, beehiveRO.bounds.width, beehiveRO.bounds.height);
		g.drawRect(loafRO.bounds.x - x, loafRO.bounds.y - y, loafRO.bounds.width, loafRO.bounds.height);
		g.drawRect(boatRO.bounds.x - x, boatRO.bounds.y - y, boatRO.bounds.width, boatRO.bounds.height);
		g.drawRect(blinkerRO.bounds.x - x, blinkerRO.bounds.y - y, blinkerRO.bounds.width, blinkerRO.bounds.height);
		g.drawRect(toadRO.bounds.x - x, toadRO.bounds.y - y, toadRO.bounds.width, toadRO.bounds.height);
		g.drawRect(beaconRO.bounds.x - x, beaconRO.bounds.y - y, beaconRO.bounds.width, beaconRO.bounds.height);
		g.drawRect(pulsarRO.bounds.x - x, pulsarRO.bounds.y - y, pulsarRO.bounds.width, pulsarRO.bounds.height);
		g.drawRect(gliderRO.bounds.x - x, gliderRO.bounds.y - y, gliderRO.bounds.width, gliderRO.bounds.height);
		g.drawRect(lightWeightRO.bounds.x - x, lightWeightRO.bounds.y - y, lightWeightRO.bounds.width, lightWeightRO.bounds.height);
		g.drawRect(mediumWeightRO.bounds.x - x, mediumWeightRO.bounds.y - y, mediumWeightRO.bounds.width, mediumWeightRO.bounds.height);
		g.drawRect(heavyWeightRO.bounds.x - x, heavyWeightRO.bounds.y - y, heavyWeightRO.bounds.width, heavyWeightRO.bounds.height);
		g.drawRect(queenBeeRO.bounds.x - x, queenBeeRO.bounds.y - y, queenBeeRO.bounds.width, queenBeeRO.bounds.height);
		g.drawRect(gosperGunRO.bounds.x - x, gosperGunRO.bounds.y - y, gosperGunRO.bounds.width, gosperGunRO.bounds.height);
		g.drawRect(rpentominoRO.bounds.x - x, rpentominoRO.bounds.y - y, rpentominoRO.bounds.width, rpentominoRO.bounds.height);
		g.drawRect(dieHardRO.bounds.x - x, dieHardRO.bounds.y - y, dieHardRO.bounds.width, dieHardRO.bounds.height);
		g.drawRect(acornRO.bounds.x - x, acornRO.bounds.y - y, acornRO.bounds.width, acornRO.bounds.height);
		
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(blockRO.bounds.x - x, blockRO.bounds.y - y, blockRO.bounds.width, blockRO.bounds.height);
		g.fillRect(beehiveRO.bounds.x - x, beehiveRO.bounds.y - y, beehiveRO.bounds.width, beehiveRO.bounds.height);
		g.fillRect(loafRO.bounds.x - x, loafRO.bounds.y - y, loafRO.bounds.width, loafRO.bounds.height);
		g.fillRect(boatRO.bounds.x - x, boatRO.bounds.y - y, boatRO.bounds.width, boatRO.bounds.height);
		g.fillRect(blinkerRO.bounds.x - x, blinkerRO.bounds.y - y, blinkerRO.bounds.width, blinkerRO.bounds.height);
		g.fillRect(toadRO.bounds.x - x, toadRO.bounds.y - y, toadRO.bounds.width, toadRO.bounds.height);
		g.fillRect(beaconRO.bounds.x - x, beaconRO.bounds.y - y, beaconRO.bounds.width, beaconRO.bounds.height);
		g.fillRect(pulsarRO.bounds.x - x, pulsarRO.bounds.y - y, pulsarRO.bounds.width, pulsarRO.bounds.height);
		g.fillRect(gliderRO.bounds.x - x, gliderRO.bounds.y - y, gliderRO.bounds.width, gliderRO.bounds.height);
		g.fillRect(lightWeightRO.bounds.x - x, lightWeightRO.bounds.y - y, lightWeightRO.bounds.width, lightWeightRO.bounds.height);
		g.fillRect(mediumWeightRO.bounds.x - x, mediumWeightRO.bounds.y - y, mediumWeightRO.bounds.width, mediumWeightRO.bounds.height);
		g.fillRect(heavyWeightRO.bounds.x - x, heavyWeightRO.bounds.y - y, heavyWeightRO.bounds.width, heavyWeightRO.bounds.height);
		g.fillRect(queenBeeRO.bounds.x - x, queenBeeRO.bounds.y - y, queenBeeRO.bounds.width, queenBeeRO.bounds.height);
		g.fillRect(gosperGunRO.bounds.x - x, gosperGunRO.bounds.y - y, gosperGunRO.bounds.width, gosperGunRO.bounds.height);
		g.fillRect(rpentominoRO.bounds.x - x, rpentominoRO.bounds.y - y, rpentominoRO.bounds.width, rpentominoRO.bounds.height);
		g.fillRect(dieHardRO.bounds.x - x, dieHardRO.bounds.y - y, dieHardRO.bounds.width, dieHardRO.bounds.height);
		g.fillRect(acornRO.bounds.x - x, acornRO.bounds.y - y, acornRO.bounds.width, acornRO.bounds.height);
		
		g.setFont(info.pane.fontPlain);
		g.drawString(block.name, blockRO.bounds.x - x + 2, blockRO.bounds.y - y - 4 + 120);
		g.drawString(beehive.name, beehiveRO.bounds.x - x + 2, beehiveRO.bounds.y - y - 4 + 120);
		g.drawString(loaf.name, loafRO.bounds.x - x + 2, loafRO.bounds.y - y - 4 + 120);
		g.drawString(boat.name, boatRO.bounds.x - x + 2, boatRO.bounds.y - y - 4 + 120);
		g.drawString(blinker.name, blinkerRO.bounds.x - x + 2, blinkerRO.bounds.y - y - 4 + 120);
		g.drawString(toad.name, toadRO.bounds.x - x + 2, toadRO.bounds.y - y - 4 + 120);
		g.drawString(beacon.name, beaconRO.bounds.x - x + 2, beaconRO.bounds.y - y - 4 + 120);
		g.drawString(pulsar.name, pulsarRO.bounds.x - x + 2, pulsarRO.bounds.y - y - 4 + 120);
		g.drawString(glider.name, gliderRO.bounds.x - x + 2, gliderRO.bounds.y - y - 4 + 120);
		g.drawString(lightWeight.name, lightWeightRO.bounds.x - x + 2, lightWeightRO.bounds.y - y - 4 + 120);
		g.drawString(mediumWeight.name, mediumWeightRO.bounds.x - x + 2, mediumWeightRO.bounds.y - y - 4 + 120);
		g.drawString(heavyWeight.name, heavyWeightRO.bounds.x - x + 2, heavyWeightRO.bounds.y - y - 4 + 120);
		g.drawString(queenBee.name, queenBeeRO.bounds.x - x + 2, queenBeeRO.bounds.y - y - 4 + 120);
		g.drawString(gosperGun.name, gosperGunRO.bounds.x - x + 2, gosperGunRO.bounds.y - y - 4 + 120);
		g.drawString(rpentomino.name, rpentominoRO.bounds.x - x + 2, rpentominoRO.bounds.y - y - 4 + 120);
		g.drawString(dieHard.name, dieHardRO.bounds.x - x + 2, dieHardRO.bounds.y - y - 4 + 120);
		g.drawString(acorn.name, acornRO.bounds.x - x + 2, acornRO.bounds.y - y - 4 + 120);
		g.setColor(Pane.lightBlue);
		g.drawString(block.name, blockRO.bounds.x - x + 3, blockRO.bounds.y - y - 3 + 120);
		g.drawString(beehive.name, beehiveRO.bounds.x - x + 3, beehiveRO.bounds.y - y - 3 + 120);
		g.drawString(loaf.name, loafRO.bounds.x - x + 3, loafRO.bounds.y - y - 3 + 120);
		g.drawString(boat.name, boatRO.bounds.x - x + 3, boatRO.bounds.y - y - 3 + 120);
		g.drawString(blinker.name, blinkerRO.bounds.x - x + 3, blinkerRO.bounds.y - y - 3 + 120);
		g.drawString(toad.name, toadRO.bounds.x - x + 3, toadRO.bounds.y - y - 3 + 120);
		g.drawString(beacon.name, beaconRO.bounds.x - x + 3, beaconRO.bounds.y - y - 3 + 120);
		g.drawString(pulsar.name, pulsarRO.bounds.x - x + 3, pulsarRO.bounds.y - y - 3 + 120);
		g.drawString(glider.name, gliderRO.bounds.x - x + 3, gliderRO.bounds.y - y - 3 + 120);
		g.drawString(lightWeight.name, lightWeightRO.bounds.x - x + 3, lightWeightRO.bounds.y - y - 3 + 120);
		g.drawString(mediumWeight.name, mediumWeightRO.bounds.x - x + 3, mediumWeightRO.bounds.y - y - 3 + 120);
		g.drawString(heavyWeight.name, heavyWeightRO.bounds.x - x + 3, heavyWeightRO.bounds.y - y - 3 + 120);
		g.drawString(queenBee.name, queenBeeRO.bounds.x - x + 3, queenBeeRO.bounds.y - y - 3 + 120);
		g.drawString(gosperGun.name, gosperGunRO.bounds.x - x + 3, gosperGunRO.bounds.y - y - 3 + 120);
		g.drawString(rpentomino.name, rpentominoRO.bounds.x - x + 3, rpentominoRO.bounds.y - y - 3 + 120);
		g.drawString(dieHard.name, dieHardRO.bounds.x - x + 3, dieHardRO.bounds.y - y - 3 + 120);
		g.drawString(acorn.name, acornRO.bounds.x - x + 3, acornRO.bounds.y - y - 3 + 120);
		
		blockRO.draw(-x, -y, g);
		beehiveRO.draw(-x, -y, g);
		loafRO.draw(-x, -y, g);
		boatRO.draw(-x, -y, g);
		blinkerRO.draw(-x, -y, g);
		toadRO.draw(-x, -y, g);
		beaconRO.draw(-x, -y, g);
		pulsarRO.draw(-x, -y, g);
		gliderRO.draw(-x, -y, g);
		lightWeightRO.draw(-x, -y, g);
		mediumWeightRO.draw(-x, -y, g);
		heavyWeightRO.draw(-x, -y, g);
		queenBeeRO.draw(-x, -y, g);
		gosperGunRO.draw(-x, -y, g);
		rpentominoRO.draw(-x, -y, g);
		dieHardRO.draw(-x, -y, g);
		acornRO.draw(-x, -y, g);
		
		block.img.draw(blockRO.bounds.x - x + 20, blockRO.bounds.y - y + 20, g);
		beehive.img.draw(beehiveRO.bounds.x - x + 20, beehiveRO.bounds.y - y + 20, g);
		loaf.img.draw(loafRO.bounds.x - x + 20, loafRO.bounds.y - y + 20, g);
		boat.img.draw(boatRO.bounds.x - x + 20, boatRO.bounds.y - y + 20, g);
		blinker.img.draw(blinkerRO.bounds.x - x + 20, blinkerRO.bounds.y - y + 20, g);
		toad.img.draw(toadRO.bounds.x - x + 20, toadRO.bounds.y - y + 20, g);
		beacon.img.draw(beaconRO.bounds.x - x + 20, beaconRO.bounds.y - y + 20, g);
		pulsar.img.draw(pulsarRO.bounds.x - x + 20, pulsarRO.bounds.y - y + 20, g);
		glider.img.draw(gliderRO.bounds.x - x + 20, gliderRO.bounds.y - y + 20, g);
		lightWeight.img.draw(lightWeightRO.bounds.x - x + 20, lightWeightRO.bounds.y - y + 20, g);
		mediumWeight.img.draw(mediumWeightRO.bounds.x - x + 20, mediumWeightRO.bounds.y - y + 20, g);
		heavyWeight.img.draw(heavyWeightRO.bounds.x - x + 20, heavyWeightRO.bounds.y - y + 20, g);
		queenBee.img.draw(queenBeeRO.bounds.x - x + 20, queenBeeRO.bounds.y - y + 20, g);
		gosperGun.img.draw(gosperGunRO.bounds.x - x + 20, gosperGunRO.bounds.y - y + 20, g);
		rpentomino.img.draw(rpentominoRO.bounds.x - x + 20, rpentominoRO.bounds.y - y + 20, g);
		dieHard.img.draw(dieHardRO.bounds.x - x + 20, dieHardRO.bounds.y - y + 20, g);
		acorn.img.draw(acornRO.bounds.x - x + 20, acornRO.bounds.y - y + 20, g);
	}
	
	/**
	 * Draws the arrow appearing on the pane along with the arrow's RollOver.
	 * The arrow is drawn relative to the screen rather than the Pane's top-left corner.
	 * 
	 * @param g - the provided Graphics context
	 */
	public void drawToImage(Graphics2D g)
	{
		arrowRO.draw(-info.pane.x, -info.pane.y, g);
		info.imageLoader.get(arrowIndex).draw(arrowBounds.x - info.pane.x, arrowBounds.y - info.pane.y, g);
	}
}
