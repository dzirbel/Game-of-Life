package main;

import graphics.AcceleratedImage;
import graphics.ButtonListener;
import graphics.DisplayMonitor;
import graphics.Tooltip;
import graphics.Tooltip.TooltipTheme;
import image.ImageLoader;
import io.Listener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import pattern.PatternSelector;

/**
 * Represents the on-screen toolbar that allows the user to play/pause the simulation, go to the
 *  next generation, clear the simulation, etc.
 * 
 * @author zirbinator
 */
public class Toolbar implements Runnable
{
    private AcceleratedImage play;
    private AcceleratedImage pause;
    private AcceleratedImage next;
    private AcceleratedImage clear;
    private AcceleratedImage move;
    private AcceleratedImage img;
    private AffineTransform transform;
    
    private boolean dragging;
    private boolean paused;
    private BufferedImage bi;
    private ButtonListener playButton;
    private ButtonListener nextButton;
    private ButtonListener clearButton;
    
    /**
     * The color of the generation text.
     */
    private static final Color generationColor = new Color(0, 163, 231);
    private static final Color generationShadowColor = new Color(30, 30, 30);
    /**
     * The color of the background of the Toolbar.
     */
    public static final Color backgroundColor = new Color(50, 50, 50);
    private static final Color darkBackgroundColor = new Color(40, 40, 40);
    /**
     * The color of the border of the Toolbar.
     */
    public static final Color borderColor = new Color(15, 15, 15);
    
    /**
     * The minimum speed for updates of the simulation while playing in updates/s.
     */
    private static final double minUpdateSpeed = 0.65;
    /**
     * The maximum speed for updates of the simulation while playing in updates/s.
     */
    private static final double maxUpdateSpeed = 750;
    
    private float playAlpha  = 1;
    private float pauseAlpha = 0;
    private float paneAlpha  = 1;
    /**
     * The minimum transparency of the toolbar.
     */
    private static final float paneAlphaMin = 0.5f;
    private float[] scales = {1f, 1f, 1f, 1f};
    private float[] offsets = new float[4];
    private static final float borderSize = 3.5f;
    private static final Font generationFont = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    
    private static final int width = 400;
    private static final int height = 100;
    
    private static final long period = 25;
    /**
     * The time to fade the play/pause button entirely from play to pause or vice versa in
     *  milliseconds.
     */
    private static final long playFadeTime = 500;
    /**
     * The time to fade the toolbar from opaque to {@link #paneAlphaMin} or vice versa in
     *  milliseconds.
     */
    private static final long paneFadeTime = 650;
    
    private PatternSelector patterns;
    private Point dragOrigin;
    private static final Point playPos =  new Point(40,  20);
    private static final Point nextPos =  new Point(110, 20);
    private static final Point clearPos = new Point(180, 20);
    private static final Point movePos =  new Point(340, 15);
    private static final Point genPos =   new Point(260, 35);
    private static final Point speedPos = new Point(255, 50);
    
    private Rectangle bounds;
    private Rectangle dotsBounds;
    private RescaleOp rescaler;
    private RollOver playRO;
    private RollOver nextRO;
    private RollOver clearRO;
    private RollOver moveRO;
    
    private SliderBar speedSlider;
    
    private Tooltip playTooltip;
    private Tooltip nextTooltip;
    private Tooltip clearTooltip;
    private Tooltip moveTooltip;
    private TooltipTheme tooltipTheme;
    
    /**
     * Creates a new Toolbar at the default location and settings.
     */
    public Toolbar()
    {
        play = ImageLoader.load("play");
        pause = ImageLoader.load("pause");
        next = ImageLoader.load("next");
        clear = ImageLoader.load("stop");
        move = ImageLoader.load("move");
        
        Listener.requestNotification(this, "mousePressed",
                Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseReleased",
                Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseDragged",
                Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "keyReleased",
                Listener.TYPE_KEY_RELEASED, Listener.CODE_KEY_ALL);
        
        bounds = new Rectangle(DisplayMonitor.screen.width - width - DisplayMonitor.screen.width/20,
                DisplayMonitor.screen.height - height - DisplayMonitor.screen.height/30,
                width, height);
        
        dragging = false;
        paused = true;
        
        try
        {
            playButton = new ButtonListener(null, "pause", this);
            nextButton = new ButtonListener(null, "next", this);
            clearButton = new ButtonListener(null, "clear", this);
        }
        catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }
        
        speedSlider = new SliderBar(null);
        tooltipTheme = new TooltipTheme();
        playTooltip = new Tooltip("Play/Pause [P]", playButton.getButton(), tooltipTheme);
        nextTooltip = new Tooltip("Next Generation [N]", nextButton.getButton(), tooltipTheme);
        clearTooltip = new Tooltip("Clear the Grid [S]", clearButton.getButton(), tooltipTheme);
        tooltipTheme.delay = 1750;
        moveTooltip = new Tooltip("Drag to Move the Toolbar", dotsBounds, tooltipTheme);
        speedSlider.setTooltip(new Tooltip("Adjust the speed of the simulation",
                null, tooltipTheme));
        
        playRO = new RollOver(new Rectangle(), 3);
        nextRO = new RollOver(new Rectangle(), 3);
        clearRO = new RollOver(new Rectangle(), 3);
        moveRO = new RollOver(new Rectangle(), 7);
        
        setBounds();
        
        patterns = new PatternSelector(this);
        
        new Thread(this).start();
        new Thread(new GridUpdater()).start();
    }
    
    /**
     * Gets the boundaries of this Toolbar on the screen.
     * 
     * @return the boundaries of this Toolbar on the screen, in pixels
     */
    public Rectangle getBounds()
    {
        return (Rectangle) bounds.clone();
    }
    
    /**
     * Gets the size of the arc used to draw the background rounded rectangle.
     * 
     * @return the diameter of the rounded corners of this Toolbar, in pixels
     */
    public int getArc()
    {
        return 3*bounds.height/4;
    }
    
    /**
     * Resets the sub-boundaries of the Toolbar based on the current location of the Toolbar.
     * That is, areas such as the location of the play button (and its roll-over and tooltip) as
     *  updated based on the current bounds.
     */
    private void setBounds()
    {
        playButton.setButton(new Rectangle(bounds.x + playPos.x, bounds.y + playPos.y,
                play.getWidth(), play.getHeight()));
        nextButton.setButton(new Rectangle(bounds.x + nextPos.x, bounds.y + nextPos.y,
                next.getWidth(), next.getHeight()));
        clearButton.setButton(new Rectangle(bounds.x + clearPos.x, bounds.y + clearPos.y,
                clear.getWidth(), clear.getHeight()));
        dotsBounds = new Rectangle(bounds.x + movePos.x, bounds.y + movePos.y,
                move.getWidth(), move.getHeight());
        
        playTooltip.setHoverArea(playButton.getButton());
        nextTooltip.setHoverArea(nextButton.getButton());
        clearTooltip.setHoverArea(clearButton.getButton());
        moveTooltip.setHoverArea(dotsBounds);
        playRO.setBounds(playButton.getButton());
        nextRO.setBounds(nextButton.getButton());
        clearRO.setBounds(clearButton.getButton());
        moveRO.setBounds(dotsBounds);
        
        speedSlider.setLocation(new Point(bounds.x + speedPos.x, bounds.y + speedPos.y));
    }
    
    /**
     * Runs the Toolbar; simply updates the alpha faders.
     * 
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime();
        while (true)
        {
            long elapsed = (System.nanoTime() - lastUpdate)/1000000;
            
            if (paused)
            {
                pauseAlpha = Math.max(pauseAlpha - (float) elapsed/playFadeTime, 0f);
                playAlpha = Math.min(playAlpha + (float) elapsed/playFadeTime, 1f);
            }
            else
            {
                playAlpha = Math.max(playAlpha - (float) elapsed/playFadeTime, 0f);
                pauseAlpha = Math.min(pauseAlpha + (float) elapsed/playFadeTime, 1f);
            }
            if (dragging)
            {
                paneAlpha = Math.max(paneAlpha - (float) elapsed/paneFadeTime, paneAlphaMin);
            }
            else
            {
                paneAlpha = Math.min(paneAlpha + (float) elapsed/paneFadeTime, 1f);
            }
            
            lastUpdate = System.nanoTime();
            try
            {
                Thread.sleep(period);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Changes the pause state of this Toolbar.
     */
    public void pause()
    {
        paused = !paused;
    }
    
    /**
     * Moves to the next generation.
     * Equivalent to the statement
     * <pre>
     * GameOfLife.getGrid().update();
     * </pre>
     */
    public void next()
    {
        GameOfLife.getGrid().update();
    }
    
    /**
     * Clears the grid and automatically pauses the simulation.
     */
    public void clear()
    {
        GameOfLife.getGrid().clear();
        paused = true;
    }
    
    /**
     * Invoked when the left mouse button is pressed.
     * 
     * @param e - the triggering event
     */
    public void mousePressed(MouseEvent e)
    {
        if (!GameOfLife.consumed(e, this))
        {
            if (dotsBounds.contains(e.getX(), e.getY()))
            {
                dragging = true;
                dragOrigin = new Point(e.getX() - bounds.x, e.getY() - bounds.y);
                moveTooltip.setVisible(true);
            }
        }
    }
    
    /**
     * Invoked when the left mouse button is released.
     * 
     * @param e - the triggering event
     */
    public void mouseReleased(MouseEvent e)
    {
        dragging = false;
        moveTooltip.setVisible(false);
    }
    
    /**
     * Invoked when the left mouse button is dragged (pressed and moved).
     * 
     * @param e - the triggering event
     */
    public void mouseDragged(MouseEvent e)
    {
        if (dragging)
        {
            int x = bounds.x;
            int y = bounds.y;
            int prevX = bounds.x;
            int prevY = bounds.y;
            
            x = e.getX() - dragOrigin.x;
            y = e.getY() - dragOrigin.y;
            
            if (x + width > DisplayMonitor.screen.width - GameOfLife.getControlBar().getBounds().width && 
                    y < GameOfLife.getControlBar().getBounds().height)
            {
                if (prevX + width > DisplayMonitor.screen.width - GameOfLife.getControlBar().getBounds().width)
                {
                    y = GameOfLife.getControlBar().getBounds().height;
                }
                if (prevY < GameOfLife.getControlBar().getBounds().height)
                {
                    x = DisplayMonitor.screen.width - GameOfLife.getControlBar().getBounds().width - width;
                }
            }
            
            bounds.x = Math.max(0, Math.min(DisplayMonitor.screen.width - width, x));
            bounds.y = Math.max(0, Math.min(DisplayMonitor.screen.height - height, y));
            
            setBounds();
            patterns.onToolbarMove();
        }
    }
    
    /**
     * Invoked when a key is released.
     * 
     * @param e - the triggering event
     */
    public void keyReleased(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_P && !Listener.controlHeld())
        {
            pause();
            playRO.splash();
        }
        else if (e.getKeyCode() == KeyEvent.VK_N && !Listener.controlHeld())
        {
            next();
            nextRO.splash();
        }
        else if (e.getKeyCode() == KeyEvent.VK_S && !Listener.controlHeld())
        {
            clear();
            clearRO.splash();
        }
        else if (e.getKeyCode() == KeyEvent.VK_COMMA && !Listener.controlHeld())
        {
            speedSlider.adjustPosition(-0.1);
        }
        else if (e.getKeyCode() == KeyEvent.VK_PERIOD && !Listener.controlHeld())
        {
            speedSlider.adjustPosition(0.1);
        }
    }
    
    /**
     * Determines whether the Toolbar should consume the given event.
     * If it is consumed, component of the interface with lower priority than the Toolbar should
     *  not handle it.
     * 
     * @param e - the event to be consumed
     * @return true if the Toolbar should consume this event, false otherwise
     */
    public boolean consumed(MouseEvent e)
    {
        if (bounds.contains(e.getPoint()) || dragging || patterns.consumed(e))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Draws the Toolbar on the given graphics context.
     * 
     * @param g - the graphics context
     */
    public void draw(Graphics2D g)
    {
        Area a = new Area(new Rectangle(0, 0, DisplayMonitor.screen.width, DisplayMonitor.screen.height));
        a.subtract(new Area(new RoundRectangle2D.Double(bounds.x + (int) (borderSize/2), bounds.y + (int) (borderSize/2),
                (int) (bounds.width - borderSize), (int) (bounds.height - borderSize),
               getArc(), getArc())));
        g.setClip(a);
        
        patterns.draw(paneAlpha, g);
        
        g.setClip(null);
        
        img = new AcceleratedImage(bounds.width, bounds.height);
        Graphics2D gImg = (Graphics2D) img.getContents().getGraphics();
        
        gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float[] fractions = { 0, 1 };
        Color[] colors = { backgroundColor, darkBackgroundColor };
        gImg.setPaint(new LinearGradientPaint(
                new Point2D.Double(0, 0),
                new Point2D.Double(0, img.getHeight()),
                fractions, colors));
        gImg.fill(new RoundRectangle2D.Double(borderSize/2, borderSize/2,
                bounds.width - borderSize, bounds.height - borderSize, getArc(), getArc()));
        
        gImg.setStroke(new BasicStroke(borderSize));
        gImg.setColor(borderColor);
        gImg.draw(new RoundRectangle2D.Double(borderSize/2, borderSize/2,
                bounds.width - borderSize, bounds.height - borderSize, getArc(), getArc()));
        
        transform = new AffineTransform();
        transform.setToTranslation(-bounds.x, -bounds.y);
        gImg.setTransform(transform);
        playRO.draw(gImg);
        nextRO.draw(gImg);
        clearRO.draw(gImg);
        moveRO.draw(gImg);
        move.draw(dotsBounds.x, dotsBounds.y, gImg);
        next.draw(nextButton.getButton().x, nextButton.getButton().y, gImg);
        clear.draw(clearButton.getButton().x, clearButton.getButton().y, gImg);
        speedSlider.draw(gImg);
        
        if (pauseAlpha > 0)
        {
            bi = new BufferedImage(pause.getWidth(), pause.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            pause.draw(0, 0, (Graphics2D) bi.getGraphics());
            scales[3] = pauseAlpha;
            rescaler = new RescaleOp(scales, offsets, null);
            gImg.drawImage(bi, rescaler, playButton.getButton().x, playButton.getButton().y);
        }
        
        if (playAlpha > 0)
        {
            bi = new BufferedImage(play.getWidth(), play.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            play.draw(0, 0, (Graphics2D) bi.getGraphics());
            scales[3] = playAlpha;
            rescaler = new RescaleOp(scales, offsets, null);
            gImg.drawImage(bi, rescaler, playButton.getButton().x, playButton.getButton().y);
        }
        gImg.setTransform(new AffineTransform());
        
        gImg.setColor(generationShadowColor);
        gImg.setFont(generationFont);
        gImg.drawString(new Integer(GameOfLife.getGrid().getGeneration()).toString(),
                genPos.x + 1, genPos.y + 1);
        gImg.setColor(generationColor);
        gImg.drawString(new Integer(GameOfLife.getGrid().getGeneration()).toString(),
                genPos.x, genPos.y);
        
        gImg.dispose();
        
        img.setTransparency(paneAlpha);
        img.validate(g);
        img.draw(bounds.x, bounds.y, g);
        
        speedSlider.drawTooltip(g);
        playTooltip.draw(g);
        nextTooltip.draw(g);
        clearTooltip.draw(g);
        moveTooltip.draw(g);
    }
    
    /**
     * Draws diagnostics information in the given rectangular area.
     * Note that the information is not guaranteed (or clipped) to the given area, in fact, some
     *  text is guaranteed to be drawn to the left and above the area.
     * The text inside the area is also not shortened or cropped if the area is small.
     * The font currently used by the graphics context is used to draw the diagnostic text.
     * 
     * @param g - the graphics context
     * @param area - the area in which diagnostic information should be drawn
     */
    public void drawDiagnostics(Graphics2D g, Rectangle area)
    {
        g.setColor(Diagnostics.border);
        g.drawRect(area.x, area.y, area.width, area.height);
        g.drawString("Toolbar Information", area.x, area.y - 2);
        
        g.drawString("x: " + Diagnostics.df.format(bounds.x) + " [px]", area.x + 5, area.y + 20);
        g.drawString("y: " + Diagnostics.df.format(bounds.y) + " [px]", area.x + 5, area.y + 40);
        
        g.drawString("Playing:  " + !paused, area.x + 5, area.y + 60);
        g.drawString("Dragging: " + dragging, area.x + 5, area.y + 80);
        
        g.drawString("Speed:  " + Diagnostics.df.format(getSpeed()) + " [updates/s]",
                area.x + 5, area.y + 100);
        g.drawString("Period: " + getPeriod() + " [ms]", area.x + 5, area.y + 120);
    }
    
    /**
     * Gets the current update speed based on the speed slider's position.
     * The update speed is in the interval
     *  <code>[{@link #minUpdateSpeed}, {@link #maxUpdateSpeed}]</code>; that is, this is a mapping
     *  (a bijection) from <code>[-1, 1]</code> (the range for the slider position) and the
     *  minimum and maximum update speeds.
     * The mapping is exponentially increasing based on the formula:
     * <pre>
     * minUpdateSpeed + (maxUpdateSpeed - minUpdateSpeed + 1)^((sliderPos - (-1))/(1 - (-1))) - 1
     * </pre>
     * 
     * @return the speed at which the simulation should be updated (if playing) in updates/s
     * @see #getPeriod()
     */
    private double getSpeed()
    {
        return minUpdateSpeed + Math.pow(maxUpdateSpeed - minUpdateSpeed + 1,
                (speedSlider.getPosition() + 1)/2) - 1;
    }
    
    /**
     * Gets the current period for the simulation updates based on the current speed.
     * This is simply the inverse of the current speed, converted to milliseconds:
     * <pre>
     * (1/getSpeed())*1000
     * </pre>
     * 
     * @return the time between updates for the simulation updater in milliseconds
     * @see #getSpeed()
     */
    private long getPeriod()
    {
        return (long) (1000.0/getSpeed());
    }
    
    /**
     * Updates the {@link Grid} continuously while the simulation is playing.
     * A GridUpdater should be run in a separated Thread and will automatically update the Grid.
     * 
     * @author zirbinator
     */
    private class GridUpdater implements Runnable
    {
        public void run()
        {
            long lastUpdate = System.nanoTime();
            while (true)
            {
                if (!paused && System.nanoTime() - lastUpdate > getPeriod()*1000000)
                {
                    lastUpdate = System.nanoTime();
                    GameOfLife.getGrid().update();
                }
                
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
