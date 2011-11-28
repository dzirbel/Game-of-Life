import graphics.AcceleratedImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Pane implements Runnable
{
	AcceleratedImage pane;
	AcceleratedImage dots;
	AcceleratedImage next;
	AcceleratedImage broom;
	
	boolean paused = true;
	boolean beingDragged = false;
	BufferedImage play = null;
	BufferedImage pause = null;
	BufferedImage selection = null;
	BufferedImage img = null;
	
	Color lightBlue = new Color(0, 163, 231);
	
	float[] scales = {1f, 1f, 1f, 1f};
	float[] offsets = new float[4];
	float playAlpha = 0f;
	float pauseAlpha = 1f;
	float fadeSpeed = 0.033f;
	float paneAlpha = 1f;
	float paneFadeSpeed = 0.025f;
	float paneAlphaMin = 0.6f;
	Font fontBold = new Font(Font.SANS_SERIF, Font.BOLD, 18);
	Font fontPlain = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
	
	Information info;
	int x;
	int y;
	int width;
	int height;
	int xShift;
	int yShift;
	
	PatternSelection selector;
	
	Rectangle rect = new Rectangle();
	Rectangle playBounds = new Rectangle();
	Rectangle dotsBounds = new Rectangle();
	Rectangle nextBounds = new Rectangle();
	Rectangle broomBounds = new Rectangle();
	RescaleOp rescaler;
	RollOver playRO;
	RollOver nextRO;
	RollOver clearRO;
	RollOver dotsRO;
	
	SpeedBar speedBar;
	
	Thread playROThread;
	Thread nextROThread;
	Thread clearROThread;
	Thread dotsROThread;
	Thread selectorThread;
	
	public Pane(Information info)
	{
		this.info = info;
		try
		{
			play = ImageIO.read(new File("images/play.png"));
			pause = ImageIO.read(new File("images/pause.png"));
			selection = ImageIO.read(new File("images/selection.png"));
		}
		catch (IOException e)
		{
			System.out.println("A pane image could not be read.");
			System.out.println("The program will now exit.");
			System.exit(0);
		}
		pane = new AcceleratedImage("images/pane.png", AcceleratedImage.TRANSLUCENT);
		dots = new AcceleratedImage("images/dots.png", AcceleratedImage.TRANSLUCENT);
		next = new AcceleratedImage("images/next.png", AcceleratedImage.TRANSLUCENT);
		broom = new AcceleratedImage("images/broom.png", AcceleratedImage.TRANSLUCENT);
		width = pane.getBufferedImage().getWidth();
		height = pane.getBufferedImage().getHeight();
		x = info.screen.width - width - info.screen.width/20;
		y = info.screen.height - height - info.screen.height/30;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		info.pane = this;
		speedBar = new SpeedBar(info);
		selector = new PatternSelection(info);
		playRO = new RollOver(info, playBounds, true);
		nextRO = new RollOver(info, nextBounds, true);
		clearRO = new RollOver(info, broomBounds, true);
		dotsRO = new RollOver(info, dotsBounds, true);
		playROThread = new Thread(playRO);
		nextROThread = new Thread(nextRO);
		clearROThread = new Thread(clearRO);
		dotsROThread = new Thread(dotsRO);
		selectorThread = new Thread(selector);
		playROThread.start();
		nextROThread.start();
		clearROThread.start();
		dotsROThread.start();
		selectorThread.start();
		setRectangles();
	}
	
	public void setRectangles()
	{
		rect = new Rectangle(x, y, width, height);
		playBounds = new Rectangle(x + width/4 - play.getWidth()/2, y + height/2 - play.getHeight()/2, play.getWidth(), play.getHeight());
		dotsBounds = new Rectangle(x + 17*width/20 - dots.getBufferedImage().getWidth()/2, y + 7*height/24 - dots.getBufferedImage().getHeight()/2,
				dots.getBufferedImage().getWidth(), dots.getBufferedImage().getHeight());
		nextBounds = new Rectangle(playBounds.x + playBounds.width + 10, playBounds.y, next.getBufferedImage().getWidth(), next.getBufferedImage().getHeight());
		broomBounds = new Rectangle(nextBounds.x + nextBounds.width + 10, nextBounds.y, broom.getBufferedImage().getWidth(), broom.getBufferedImage().getHeight());
		speedBar.bounds = new Rectangle(x + 280, y + 45, 100, 40);
		speedBar.setSliderBounds(speedBar.bounds.x + speedBar.sliderX, true);
		selector.setBounds();
		playRO.bounds = playBounds;
		nextRO.bounds = nextBounds;
		clearRO.bounds = broomBounds;
		dotsRO.bounds = dotsBounds;
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
		if (paused)
		{
			pauseAlpha = Math.min(pauseAlpha + fadeSpeed, 1f);
			playAlpha = Math.max(playAlpha - fadeSpeed, 0f);
		}
		else
		{
			playAlpha = Math.min(playAlpha + fadeSpeed, 1f);
			pauseAlpha = Math.max(pauseAlpha - fadeSpeed, 0f);
		}
		if (beingDragged)
		{
			paneAlpha = Math.max(paneAlpha - paneFadeSpeed, paneAlphaMin);
		}
		else
		{
			paneAlpha = Math.min(paneAlpha + paneFadeSpeed, 1f);
		}
	}
	
	public boolean mousePressed(MouseEvent e)
	{
		if (rect.contains(e.getX(), e.getY()))
		{
			if (playBounds.contains(e.getX(), e.getY()))
			{
				paused = !paused;
			}
			else if (nextBounds.contains(e.getX(), e.getY()))
			{
				info.map.update();
			}
			else if (broomBounds.contains(e.getX(), e.getY()))
			{
				info.map.clear();
			}
			return true;
		}
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		drawToImage();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics paneG = bi.getGraphics();
		paneG.drawImage(img, 0, 0, width, height, null);
		scales[3] = paneAlpha;
		rescaler = new RescaleOp(scales, offsets, null);
		g.drawImage(bi, rescaler, x, y);
	}
	
	public void drawToImage()
	{
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)img.getGraphics();
		pane.draw(0, 0, g);
		selector.drawToImage(g);
		playRO.draw(g);
		nextRO.draw(g);
		clearRO.draw(g);
		dotsRO.draw(g);
		for (int i = 0; i < info.listener.rollOvers.size(); i++)
		{
			info.listener.rollOvers.get(i).draw(g);
		}
		dots.draw(dotsBounds.x - x, dotsBounds.y - y, g);
		next.draw(nextBounds.x - x, nextBounds.y - y, g);
		broom.draw(broomBounds.x - x, broomBounds.y - y, g);
		
		BufferedImage bi = new BufferedImage(pause.getWidth(), pause.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics pauseG = bi.getGraphics();
		pauseG.drawImage(pause, 0, 0, null);
		scales[3] = pauseAlpha;
		rescaler = new RescaleOp(scales, offsets, null);
		g.drawImage(bi, rescaler, playBounds.x - x, playBounds.y - y);
		
		bi = new BufferedImage(play.getWidth(), play.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics playG = bi.getGraphics();
		playG.drawImage(play, 0, 0, null);
		scales[3] = playAlpha;
		rescaler = new RescaleOp(scales, offsets, null);
		g.drawImage(bi, rescaler, playBounds.x - x,  playBounds.y - y);
		
		g.setColor(new Color(0, 0, 0));
		g.setFont(fontBold);
		g.drawString(new Integer(info.generation).toString(), 281, 36);
		g.setColor(lightBlue);
		g.drawString(new Integer(info.generation).toString(), 280, 35);
		
		speedBar.drawToImage(g);
	}
}
