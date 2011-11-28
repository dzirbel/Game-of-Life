import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class PatternSelection implements Runnable
{
	boolean f = false;
	boolean t = true;
	boolean[][] blockPattern = {		{t, t},
										{t, t}};
	
	boolean[][] beehivePattern = {		{f, t, t, f}, 
										{t, f, f, t}, 
										{f, t, t, f}};
	
	boolean[][] loafPattern = {			{f, t, t, f},
										{t, f, f, t},
										{f, t, f, t},
										{f, f, t, f}};
	
	boolean[][] boatPattern = {			{t, t, f},
										{t, f, t},
										{f, t, f}};
	
	boolean[][] blinkerPattern = {		{t, t, t}};
	
	boolean[][] toadPattern = {			{f, t, t, t},
										{t, t, t, f}};
	
	boolean[][] beaconPattern = {		{t, t, f, f},
										{t, f, f, f},
										{f, f, f, t},
										{f, f, t, t}};
	
	boolean[][] pulsarPattern = {		{f, t, f},
										{t, t, t},
										{t, f, t},
										{t, t, t},
										{f, t, f}};
	
	boolean[][] gliderPattern = {		{f, f, t},
										{t, f, t},
										{f, t, t}};
	
	boolean[][] lightWeightPattern = {	{t, f, f, t, f},
										{f, f, f, f, t},
										{t, f, f, f, t},
										{f, t, t, t, t}};
	
	boolean[][] mediumWeightPattern = {	{f, f, t, f, f, f},
										{t, f, f, f, t, f},
										{f, f, f, f, f, t},
										{t, f, f, f, f, t},
										{f, t, t, t, t, t}};
	
	boolean[][] heavyWeightPattern = {	{f, f, t, t, f, f, f},
										{t, f, f, f, f, t, f},
										{f, f, f, f, f, f, t},
										{t, f, f, f, f, f, t},
										{f, t, t, t, t, t, t}};
	
	boolean[][] queenBeePattern = {		{t, t, f, f},
										{f, f, t, f},
										{f, f, f, t},
										{f, f, f, t},
										{f, f, f, t},
										{f, f, f, t},
										{f, f, t, f},
										{t, t, f, f}};
	
	boolean[][] gosperGunPattern = {	{f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f},
										{f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f},
										{f, f, f, f, f, f, f, f, f, f, f, f, t, t, f, f, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, t, t},
										{f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, t, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, t, t},
										{t, t, f, f, f, f, f, f, f, f, t, f, f, f, f, f, t, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f},
										{t, t, f, f, f, f, f, f, f, f, t, f, f, f, t, f, t, t, f, f, f, f, t, f, t, f, f, f, f, f, f, f, f, f, f, f},
										{f, f, f, f, f, f, f, f, f, f, t, f, f, f, f, f, t, f, f, f, f, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f},
										{f, f, f, f, f, f, f, f, f, f, f, t, f, f, f, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f},
										{f, f, f, f, f, f, f, f, f, f, f, f, t, t, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f, f}};
	
	boolean[][] rpentominoPattern = {	{f, t, t},
										{t, t, f},
										{f, t, f}};
	
	boolean[][] dieHardPattern = {		{f, f, f, f, f, f, t, f},
										{t, t, f, f, f, f, f, f},
										{f, t, f, f, f, t, t, t}};
	
	boolean[][] acornPattern = {		{f, t, f, f, f, f, f},
										{f, f, f, t, f, f, f},
										{t, t, f, f, t, t, t}};
	boolean visible = false;
	BufferedImage arrow = null;
	BufferedImage patternBackground = null;
	BufferedImage patternSelection = null;
	BufferedImage img;
	
	float[] scales = {1f, 1f, 1f, 1f};
	float[] offsets = new float[4];
	float alpha = 0f;
	float alphaSpeed = 0.05f;
	
	Information info;
	int numPatterns = 17;
	int numStable = 4;
	int numOscilators = 4;
	int numSpaceships = 5;
	int numGliderGuns = 1;
	int numExploders = 3;
	int numRows = 5;
	int numColumns = 5;
	int width = 600;
	int height = 600;
	int x;
	int y;
	
	Pattern block;
	Pattern beehive;
	Pattern loaf;
	Pattern boat;
	Pattern blinker;
	Pattern toad;
	Pattern beacon;
	Pattern pulsar;
	Pattern glider;
	Pattern lightWeight;
	Pattern mediumWeight;
	Pattern heavyWeight;
	Pattern queenBee;
	Pattern gosperGun;
	Pattern rpentomino;
	Pattern dieHard;
	Pattern acorn;
	Pattern selected;
	
	Rectangle arrowBounds;
	Rectangle selectionArea;
	Rectangle blockBounds;
	Rectangle beehiveBounds;
	Rectangle loafBounds;
	Rectangle boatBounds;
	Rectangle blinkerBounds;
	Rectangle toadBounds;
	Rectangle beaconBounds;
	Rectangle pulsarBounds;
	Rectangle gliderBounds;
	Rectangle lightWeightBounds;
	Rectangle mediumWeightBounds;
	Rectangle heavyWeightBounds;
	Rectangle queenBeeBounds;
	Rectangle gosperGunBounds;
	Rectangle rpentominoBounds;
	Rectangle dieHardBounds;
	Rectangle acornBounds;
	RescaleOp rescaler;
	RollOver arrowRO;
	RollOver blockRO;
	RollOver beehiveRO;
	RollOver loafRO;
	RollOver boatRO;
	RollOver blinkerRO;
	RollOver toadRO;
	RollOver beaconRO;
	RollOver pulsarRO;
	RollOver gliderRO;
	RollOver lightWeightRO;
	RollOver mediumWeightRO;
	RollOver heavyWeightRO;
	RollOver queenBeeRO;
	RollOver gosperGunRO;
	RollOver rpentominoRO;
	RollOver dieHardRO;
	RollOver acornRO;
	
	Thread arrowROThread;
	Thread blockROThread;
	Thread beehiveROThread;
	Thread loafROThread;
	Thread boatROThread;
	Thread blinkerROThread;
	Thread toadROThread;
	Thread beaconROThread;
	Thread pulsarROThread;
	Thread gliderROThread;
	Thread lightWeightROThread;
	Thread mediumWeightROThread;
	Thread heavyWeightROThread;
	Thread queenbeeROThread;
	Thread gosperGunROThread;
	Thread rpentominoROThread;
	Thread dieHardROThread;
	Thread acornROThread;
	
	public PatternSelection(Information info)
	{
		this.info = info;
		try
		{
			arrow = ImageIO.read(new File("images/arrow.png"));
			patternSelection = ImageIO.read(new File("images/patternSelection.png"));
			patternBackground = ImageIO.read(new File("images/patternBackground.png"));
		}
		catch (IOException e) { System.out.println("patternselection error"); }
		arrowRO = new RollOver(info, arrowBounds, true);
		blockRO = new RollOver(info, blockBounds, true);
		beehiveRO = new RollOver(info, beehiveBounds, true);
		loafRO = new RollOver(info, loafBounds, true);
		boatRO = new RollOver(info, boatBounds, true);
		blinkerRO = new RollOver(info, blinkerBounds, true);
		toadRO = new RollOver(info, toadBounds, true);
		beaconRO = new RollOver(info, beaconBounds, true);
		pulsarRO = new RollOver(info, pulsarBounds, true);
		gliderRO = new RollOver(info, gliderBounds, true);
		lightWeightRO = new RollOver(info, lightWeightBounds, true);
		mediumWeightRO = new RollOver(info, mediumWeightBounds, true);
		heavyWeightRO = new RollOver(info, heavyWeightBounds, true);
		queenBeeRO = new RollOver(info, queenBeeBounds, true);
		gosperGunRO = new RollOver(info, gosperGunBounds, true);
		rpentominoRO = new RollOver(info, rpentominoBounds, true);
		dieHardRO = new RollOver(info, dieHardBounds, true);
		acornRO = new RollOver(info, acornBounds, true);
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
		
		setBounds();
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
	
	public void setBounds()
	{
		arrowBounds = new Rectangle(info.pane.x + 25, info.pane.y + info.pane.rect.height/2 - arrow.getHeight()/2,
				arrow.getWidth(), arrow.getHeight());
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
		blockBounds = new Rectangle(		x, 			y,       	120, 120);
		beehiveBounds = new Rectangle(		x, 			y + 120, 	120, 120);
		loafBounds = new Rectangle(			x, 			y + 240, 	120, 120);
		boatBounds = new Rectangle(			x, 			y + 360, 	120, 120);
		blinkerBounds = new Rectangle(		x + 120,	y, 			120, 120);
		toadBounds = new Rectangle(			x + 120,	y + 120, 	120, 120);
		beaconBounds = new Rectangle(		x + 120,	y + 240, 	120, 120);
		pulsarBounds = new Rectangle(		x + 120,	y + 360, 	120, 120);
		gliderBounds = new Rectangle(		x + 240,	y, 			120, 120);
		lightWeightBounds = new Rectangle(	x + 240,	y + 120, 	120, 120);
		mediumWeightBounds = new Rectangle(	x + 240,	y + 240, 	120, 120);
		heavyWeightBounds = new Rectangle(	x + 240,	y + 360, 	120, 120);
		queenBeeBounds = new Rectangle(		x + 240,	y + 480, 	120, 120);
		gosperGunBounds = new Rectangle(	x + 360,	y,			120, 120);
		rpentominoBounds = new Rectangle(	x + 480,	y, 			120, 120);
		dieHardBounds = new Rectangle(		x + 480,	y + 120, 	120, 120);
		acornBounds = new Rectangle(		x + 480,	y + 240, 	120, 120);
		blockRO.bounds = blockBounds;
		beehiveRO.bounds = beehiveBounds;
		loafRO.bounds = loafBounds;
		boatRO.bounds = boatBounds;
		blinkerRO.bounds = blinkerBounds;
		toadRO.bounds = toadBounds;
		beaconRO.bounds = beaconBounds;
		pulsarRO.bounds = pulsarBounds;
		gliderRO.bounds = gliderBounds;
		lightWeightRO.bounds = lightWeightBounds;
		mediumWeightRO.bounds = mediumWeightBounds;
		heavyWeightRO.bounds = heavyWeightBounds;
		queenBeeRO.bounds = queenBeeBounds;
		gosperGunRO.bounds = gosperGunBounds;
		rpentominoRO.bounds = rpentominoBounds;
		dieHardRO.bounds = dieHardBounds;
		acornRO.bounds = acornBounds;
		arrowRO.bounds = arrowBounds;
	}
	
	public void run()
	{
		while (true)
		{
			update();
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e) { }
		}
	}
	
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
	
	public boolean mousePressed(MouseEvent e)
	{
		if (e.getButton() != MouseEvent.BUTTON1)
		{
			selected = null;
			return false;
		}
		if (selectionArea.contains(info.listener.mouseLocation) && visible)
		{
			if (blockBounds.contains(info.listener.mouseLocation))
			{
				selected = block.clone();
			}
			else if (beehiveBounds.contains(info.listener.mouseLocation))
			{
				selected = beehive.clone();
			}
			else if (loafBounds.contains(info.listener.mouseLocation))
			{
				selected = loaf.clone();
			}
			else if (boatBounds.contains(info.listener.mouseLocation))
			{
				selected = boat.clone();
			}
			else if (blinkerBounds.contains(info.listener.mouseLocation))
			{
				selected = blinker.clone();
			}
			else if (toadBounds.contains(info.listener.mouseLocation))
			{
				selected = toad.clone();
			}
			else if (beaconBounds.contains(info.listener.mouseLocation))
			{
				selected = beacon.clone();
			}
			else if (pulsarBounds.contains(info.listener.mouseLocation))
			{
				selected = pulsar.clone();
			}
			else if (gliderBounds.contains(info.listener.mouseLocation))
			{
				selected = glider.clone();
			}
			else if (lightWeightBounds.contains(info.listener.mouseLocation))
			{
				selected = lightWeight.clone();
			}
			else if (mediumWeightBounds.contains(info.listener.mouseLocation))
			{
				selected = mediumWeight.clone();
			}
			else if (heavyWeightBounds.contains(info.listener.mouseLocation))
			{
				selected = heavyWeight.clone();
			}
			else if (queenBeeBounds.contains(info.listener.mouseLocation))
			{
				selected = queenBee.clone();
			}
			else if (gosperGunBounds.contains(info.listener.mouseLocation))
			{
				selected = gosperGun.clone();
			}
			else if (rpentominoBounds.contains(info.listener.mouseLocation))
			{
				selected = rpentomino.clone();
			}
			else if (dieHardBounds.contains(info.listener.mouseLocation))
			{
				selected = dieHard.clone();
			}
			else if (acornBounds.contains(info.listener.mouseLocation))
			{
				selected = acorn.clone();
			}
			selected.generateFullSizeImage();
			return true;
		}
		else if (!info.listener.mousePressedInOpBar && !info.listener.mousePressedInPane)
		{
			try
			{
				Point mouseCell = info.window.mouseCell();
				for (int i = 0; i < selected.pattern.length; i++)
				{
					for (int j = 0; j < selected.pattern[0].length; j++)
					{
						info.map.map[j + mouseCell.x][i + mouseCell.y] = selected.pattern[i][j];
					}
				}
				return true;
			}
			catch (NullPointerException ex) { }
		}
		selected = null;
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		if (alpha > 0.0001f)
		{
			drawSelectionAreaToImage();
			BufferedImage bi = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
			Graphics paneG = bi.getGraphics();
			paneG.drawImage(img, 0, 0, 600, 600, null);
			scales[3] = alpha;
			rescaler = new RescaleOp(scales, offsets, null);
			g.drawImage(bi, rescaler, x, y);
		}
		try
		{
			Point mouseCell = info.window.mouseCell();
			g.drawImage(selected.img, (int)info.window.zoom*mouseCell.x - info.window.xMap,
					(int)info.window.zoom*mouseCell.y - info.window.yMap, null);
		}
		catch (NullPointerException e) { }
	}
	
	public void drawSelectionAreaToImage()
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
		g.drawRect(blockBounds.x - x, blockBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(beehiveBounds.x - x, beehiveBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(loafBounds.x - x, loafBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(boatBounds.x - x, boatBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(blinkerBounds.x - x, blinkerBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(toadBounds.x - x, toadBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(beaconBounds.x - x, beaconBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(pulsarBounds.x - x, pulsarBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(gliderBounds.x - x, gliderBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(lightWeightBounds.x - x, lightWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(mediumWeightBounds.x - x, mediumWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(heavyWeightBounds.x - x, heavyWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(queenBeeBounds.x - x, queenBeeBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(gosperGunBounds.x - x, gosperGunBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(rpentominoBounds.x - x, rpentominoBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(dieHardBounds.x - x, dieHardBounds.y - y, blockBounds.width, blockBounds.height);
		g.drawRect(acornBounds.x - x, acornBounds.y - y, blockBounds.width, blockBounds.height);
		g.setColor(new Color(0, 0, 0, 100));
		g.fillRect(blockBounds.x - x, blockBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(beehiveBounds.x - x, beehiveBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(loafBounds.x - x, loafBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(boatBounds.x - x, boatBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(blinkerBounds.x - x, blinkerBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(toadBounds.x - x, toadBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(beaconBounds.x - x, beaconBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(pulsarBounds.x - x, pulsarBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(gliderBounds.x - x, gliderBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(lightWeightBounds.x - x, lightWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(mediumWeightBounds.x - x, mediumWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(heavyWeightBounds.x - x, heavyWeightBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(queenBeeBounds.x - x, queenBeeBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(gosperGunBounds.x - x, gosperGunBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(rpentominoBounds.x - x, rpentominoBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(dieHardBounds.x - x, dieHardBounds.y - y, blockBounds.width, blockBounds.height);
		g.fillRect(acornBounds.x - x, acornBounds.y - y, blockBounds.width, blockBounds.height);
		g.setFont(info.pane.fontPlain);
		g.drawString(block.name, blockBounds.x - x + 2, blockBounds.y - y - 4 + 120);
		g.drawString(beehive.name, beehiveBounds.x - x + 2, beehiveBounds.y - y - 4 + 120);
		g.drawString(loaf.name, loafBounds.x - x + 2, loafBounds.y - y - 4 + 120);
		g.drawString(boat.name, boatBounds.x - x + 2, boatBounds.y - y - 4 + 120);
		g.drawString(blinker.name, blinkerBounds.x - x + 2, blinkerBounds.y - y - 4 + 120);
		g.drawString(toad.name, toadBounds.x - x + 2, toadBounds.y - y - 4 + 120);
		g.drawString(beacon.name, beaconBounds.x - x + 2, beaconBounds.y - y - 4 + 120);
		g.drawString(pulsar.name, pulsarBounds.x - x + 2, pulsarBounds.y - y - 4 + 120);
		g.drawString(glider.name, gliderBounds.x - x + 2, gliderBounds.y - y - 4 + 120);
		g.drawString(lightWeight.name, lightWeightBounds.x - x + 2, lightWeightBounds.y - y - 4 + 120);
		g.drawString(mediumWeight.name, mediumWeightBounds.x - x + 2, mediumWeightBounds.y - y - 4 + 120);
		g.drawString(heavyWeight.name, heavyWeightBounds.x - x + 2, heavyWeightBounds.y - y - 4 + 120);
		g.drawString(queenBee.name, queenBeeBounds.x - x + 2, queenBeeBounds.y - y - 4 + 120);
		g.drawString(gosperGun.name, gosperGunBounds.x - x + 2, gosperGunBounds.y - y - 4 + 120);
		g.drawString(rpentomino.name, rpentominoBounds.x - x + 2, rpentominoBounds.y - y - 4 + 120);
		g.drawString(dieHard.name, dieHardBounds.x - x + 2, dieHardBounds.y - y - 4 + 120);
		g.drawString(acorn.name, acornBounds.x - x + 2, acornBounds.y - y - 4 + 120);
		g.setColor(info.pane.lightBlue);
		g.drawString(block.name, blockBounds.x - x + 3, blockBounds.y - y - 3 + 120);
		g.drawString(beehive.name, beehiveBounds.x - x + 3, beehiveBounds.y - y - 3 + 120);
		g.drawString(loaf.name, loafBounds.x - x + 3, loafBounds.y - y - 3 + 120);
		g.drawString(boat.name, boatBounds.x - x + 3, boatBounds.y - y - 3 + 120);
		g.drawString(blinker.name, blinkerBounds.x - x + 3, blinkerBounds.y - y - 3 + 120);
		g.drawString(toad.name, toadBounds.x - x + 3, toadBounds.y - y - 3 + 120);
		g.drawString(beacon.name, beaconBounds.x - x + 3, beaconBounds.y - y - 3 + 120);
		g.drawString(pulsar.name, pulsarBounds.x - x + 3, pulsarBounds.y - y - 3 + 120);
		g.drawString(glider.name, gliderBounds.x - x + 3, gliderBounds.y - y - 3 + 120);
		g.drawString(lightWeight.name, lightWeightBounds.x - x + 3, lightWeightBounds.y - y - 3 + 120);
		g.drawString(mediumWeight.name, mediumWeightBounds.x - x + 3, mediumWeightBounds.y - y - 3 + 120);
		g.drawString(heavyWeight.name, heavyWeightBounds.x - x + 3, heavyWeightBounds.y - y - 3 + 120);
		g.drawString(queenBee.name, queenBeeBounds.x - x + 3, queenBeeBounds.y - y - 3 + 120);
		g.drawString(gosperGun.name, gosperGunBounds.x - x + 3, gosperGunBounds.y - y - 3 + 120);
		g.drawString(rpentomino.name, rpentominoBounds.x - x + 3, rpentominoBounds.y - y - 3 + 120);
		g.drawString(dieHard.name, dieHardBounds.x - x + 3, dieHardBounds.y - y - 3 + 120);
		g.drawString(acorn.name, acornBounds.x - x + 3, acornBounds.y - y - 3 + 120);
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
		g.drawImage(block.img, blockBounds.x - x, blockBounds.y - y, null);
		g.drawImage(beehive.img, beehiveBounds.x - x, beehiveBounds.y - y, null);
		g.drawImage(loaf.img, loafBounds.x - x, loafBounds.y - y, null);
		g.drawImage(boat.img, boatBounds.x - x, boatBounds.y - y, null);
		g.drawImage(blinker.img, blinkerBounds.x - x, blinkerBounds.y - y, null);
		g.drawImage(toad.img, toadBounds.x - x, toadBounds.y - y, null);
		g.drawImage(beacon.img, beaconBounds.x - x, beaconBounds.y - y, null);
		g.drawImage(pulsar.img, pulsarBounds.x - x, pulsarBounds.y - y, null);
		g.drawImage(glider.img, gliderBounds.x - x, gliderBounds.y - y, null);
		g.drawImage(lightWeight.img, lightWeightBounds.x - x, lightWeightBounds.y - y, null);
		g.drawImage(mediumWeight.img, mediumWeightBounds.x - x, mediumWeightBounds.y - y, null);
		g.drawImage(heavyWeight.img, heavyWeightBounds.x - x, heavyWeightBounds.y - y, null);
		g.drawImage(queenBee.img, queenBeeBounds.x - x, queenBeeBounds.y - y, null);
		g.drawImage(gosperGun.img, gosperGunBounds.x - x, gosperGunBounds.y - y, null);
		g.drawImage(rpentomino.img, rpentominoBounds.x - x, rpentominoBounds.y - y, null);
		g.drawImage(dieHard.img, dieHardBounds.x - x, dieHardBounds.y - y, null);
		g.drawImage(acorn.img, acornBounds.x - x, acornBounds.y - y, null);
	}
	
	public void drawToImage(Graphics2D g)
	{
		arrowRO.draw(g);
		g.drawImage(arrow, arrowBounds.x - info.pane.x, arrowBounds.y - info.pane.y, arrowBounds.width, arrowBounds.height, null);
	}
}
