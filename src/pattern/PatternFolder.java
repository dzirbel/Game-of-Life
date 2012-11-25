package pattern;

import graphics.AcceleratedImage;
import image.ImageLoader;
import io.Listener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import utils.ListUtil;

import main.GameOfLife;

/**
 * Represents a folder of {@link Pattern}s.
 * Each PatternFolder holds a list of {@link Pattern}s and a name associated with the folder,
 *  and is primarily responsible for drawing a visual pattern and allowing the user to select
 *  patterns via mouse input, in conjunction with a {@link PatternSelector}.
 * 
 * @author zirbinator
 */
public class PatternFolder implements Runnable
{
    private AcceleratedImage folderBack;
    private AcceleratedImage folderFront;
    private ArrayList<Pattern> patterns;
    private ArrayList<AcceleratedImage> thumbs;
    
    private boolean on;
    /**
     * Whether the user is currently hovering the mouse over the folder image.
     */
    private boolean hoveringFolder;
    
    private static final Color nameColor = new Color(0, 163, 231);
    private static final Color nameShadowColor = Color.black;
    private static final Color patternNameColor = new Color(10, 10, 10);
    private static final Color patternBoxBorder = new Color(12, 12, 12);
    private static final Color patternBoxBackground = new Color(50, 50, 50);
    
    private Dimension size;
    /**
     * The amount that the PatternFolder is open, in the range [0, 1], where 0 is entirely closed
     *  and 1 is entirely open.
     */
    private double openAmount;
    /**
     * The amount that the folder image is shown as "open", in the range [0, maxFolderOpenAmount],
     *  where 0 is shown as completely closed and maxFolderOpenAmount is shown as open as possible.
     */
    private double folderOpenAmount;
    private static final double maxFolderOpenAmount = 0.2;
    
    private FolderState state;
    private static final Font nameFont = new Font(Font.SANS_SERIF, Font.ITALIC, 14);
    private static final Font patternNameFont = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private static final Metrics nameMetrics = new Metrics(nameFont);
    
    /**
     * The width of the entire PatternFolder depiction, in pixels.
     */
    private static final int width = 175;
    /**
     * The distance between the bottom of the PatternFolder depiction and folder image, in pixels.
     */
    private static final int bottomBuffer = 20;
    /**
     * The distance between the top of the PatternFolder and the folder image, in pixels.
     * This is used as a cautionary butter in case the pattern thumbs go above the folder.
     */
    private static final int topBuffer = 25;
    /**
     * The size of the folder image on the screen, in pixels.
     */
    private static final int folderSize = 75;
    /**
     * The size of the PatternFolder depiction when it is closed, in pixels.
     */
    private static final int closedHeight = bottomBuffer + folderSize + topBuffer;
    /**
     * The size of the PatternFolder depiction when it is open, in pixels.
     * This height is also used when it is closing or opening.
     * The open height is necessarily calculated when the patterns are set, as it is based off the
     *  number of patterns held by the PatternFolder.
     */
    private int openHeight;
    private int selected;
    /**
     * The height of a single pattern in the PatternFolder depiction when it is open.
     */
    private static final int patternHeight = 22;
    
    private static final long period = 15;
    /**
     * The time it takes to go from completely closed to completely open, in milliseconds
     */
    private static final long openTime = 100;
    
    private PatternSelector selector;
    /**
     * The location of the PatternFolder depiction on the screen, in pixels.
     * This should be controlled by the enclosing PatternSelector, along with the on/off state.
     */
    private Point location;
    /**
     * The location of the name in the PatternFolder depiction, in pixels, relative to
     *  {@link #location}.
     */
    private Point nameLocation;
    /**
     * The location of the folder in the PatternFolder depiction, in pixels.
     * The x-coordinate is the distance from the left side of the depiction, and the y-coordinate
     *  is the positive distance from the bottom of the depiction.
     */
    private Point folderLocation;
    /**
     * The requested folder location when the PatternFolder is closed.
     * The x-coordinate is the distance from the left side of the depiction, and the y-coordinate
     *  is the positive distance from the bottom of the depiction.
     */
    private static final Point closedFolderLocation = new Point((width - folderSize)/2,
            bottomBuffer + folderSize);
    /**
     * The requested folder location when the PatternFolder is open.
     * The x-coordinate is the distance from the left side of the depiction, and the y-coordinate
     *  is the positive distance from the bottom of the depiction.
     */
    private static final Point openFolderLocation = new Point(0, closedFolderLocation.y);
    
    /**
     * The name of this PatternFolder.
     */
    private String name;
    
    /**
     * Creates a new PatternFolder with the given name and patterns.
     * The folder is closed by default.
     * 
     * @param name - the name of this PatternFolder
     * @param patterns - the Patterns held by this PatternFolder
     */
    public PatternFolder(PatternSelector selector, String name, ArrayList<Pattern> patterns)
    {
        this.selector = selector;
        setName(name);
        setPatterns(patterns);
        
        on = false;
        hoveringFolder = false;
        state = FolderState.CLOSED;
        openAmount = 0;
        folderOpenAmount = 0;
        selected = -1;
        folderLocation = new Point(closedFolderLocation);
        size = new Dimension(width, closedHeight);
        
        folderBack = ImageLoader.load("folder_back");
        folderFront = ImageLoader.load("folder_front");
        folderBack.resize(folderSize, folderSize);
        folderFront.resize(folderSize, folderSize);
        
        Listener.requestNotification(this, "mouseMoved", Listener.TYPE_MOUSE_MOVED);
        Listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED);
        
        new Thread(this).start();
    }
    
    /**
     * Runs this PatternFolder in a separate thread.
     * 
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime(), elapsed;
        while (true)
        {
            elapsed = (System.nanoTime() - lastUpdate)/1000000;
            
            if (state == FolderState.OPENING)
            {
                openAmount += (double)elapsed/openTime;
                
                if (openAmount >= 1)
                {
                    openAmount = 1;
                    state = FolderState.OPEN;
                    findSize();
                }
            }
            else if (state == FolderState.CLOSING)
            {
                openAmount -= (double)elapsed/openTime;
                
                if (openAmount <= 0)
                {
                    openAmount = 0;
                    state = FolderState.CLOSED;
                    findSize();
                }
            }
            folderLocation.x = (int) (openAmount*openFolderLocation.x + (1 - openAmount)*closedFolderLocation.x);
            
            if (hoveringFolder || state == FolderState.OPEN || state == FolderState.OPENING)
            {
                folderOpenAmount = maxFolderOpenAmount;
            }
            else
            {
                folderOpenAmount = 0;
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
     * Gets the name of this PatternFolder, typically used as a generic categorization of its
     *  patterns and displayed along with the folder.
     * 
     * @return the name of this PatternFolder
     * @see #setName(String)
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the name of this PatternFolder, typically used as a generic categorization of its
     *  patterns and displayed along with the folder.
     * 
     * @param name - the new name of this PatternFolder
     * @see #getName()
     */
    public void setName(String name)
    {
        this.name = name;
        nameLocation = null;
    }
    
    /**
     * Sets the patterns held by this PatternFolder.
     * Note that this should be done sparingly, as it is necessary to sort them and generate
     *  thumbnail images for each pattern, which can be time-consuming.
     * 
     * @param patterns - the patterns to be held by this PatternFolder
     */
    public void setPatterns(ArrayList<Pattern> patterns)
    {
        this.patterns = patterns;
        ListUtil.sort(this.patterns);
        thumbs = new ArrayList<AcceleratedImage>();
        for (int i = 0; i < this.patterns.size(); i++)
        {
            thumbs.add(this.patterns.get(i).generateThumb(patternHeight - 2, patternHeight - 2));
        }
        openHeight = Math.max(closedHeight,
                bottomBuffer + this.patterns.size()*patternHeight + topBuffer);
    }
    
    /**
     * Determines whether this PatternFolder is currently in the "on" state, signifying that it
     *  will respond to input.
     * 
     * @return true if this PatternFolder is reacting to user input, false otherwise
     * @see #setOn(boolean)
     */
    public boolean isOn()
    {
        return on;
    }
    
    /**
     * Sets the "on" state of this PatternFolder, used to signify whether it should respond to user
     *  input.
     * 
     * @param on - true if this PatternFolder is currently visible on the screen and should be
     *  reacting to user input, false otherwise
     * @see #isOn()
     */
    public void setOn(boolean on)
    {
        this.on = on;
    }
    
    /**
     * Gets the current size of this PatternFolder.
     * 
     * @return the size of the PatternFolder, in pixels
     */
    public Dimension getSize()
    {
        return (Dimension) size.clone();
    }
    
    /**
     * Gets the location of this PatternFolder on the screen.
     * 
     * @return the current location of this PatternFolder on the screen, in pixels
     * @see #setLocation(Point)
     */
    public Point getLocation()
    {
        return new Point(location);
    }
    
    /**
     * Sets the location of this PatternFolder on the screen.
     * 
     * @param location - the location of this PatternFolder on the screen, in pixels
     * @see #getLocation()
     */
    public void setLocation(Point location)
    {
        this.location = new Point(location);
    }
    
    /**
     * Gets the area in which the folder should be shown on the screen.
     * 
     * @return the area of the screen which should contain the folder image
     */
    private Rectangle getFolderArea()
    {
        return new Rectangle(location.x + folderLocation.x,
                location.y + size.height - folderLocation.y,
                folderSize, folderSize);
    }
    
    /**
     * Gets the box in which the pattern at the given index should be displayed.
     * This box is relative to location rather than the screen in general; thus, for example, if
     *  checking whether the mouse is hovering over a certain box, the x- and y-coordinate of the
     *  box must be incremented by location.
     * 
     * @param patternIndex - the index of the pattern for which to find the box
     * @return the box in which the pattern should be shown
     */
    private Rectangle getPatternBox(int patternIndex)
    {
        return new Rectangle(openFolderLocation.x + folderSize,
                size.height - bottomBuffer - (patternIndex + 1)*patternHeight,
                size.width - (openFolderLocation.x + folderSize), patternHeight);
    }
    
    /**
     * Finds the current size of this PatternFolder.
     * The size is only affected by the state, and so this should be called whenever the state is
     *  changed.
     */
    private void findSize()
    {
        if (state == FolderState.CLOSED)
        {
            size = new Dimension(width, closedHeight);
        }
        else
        {
            size = new Dimension(width, openHeight);
        }
        
        selector.onResize();
        
        synchronized (nameLocation)
        {
            nameLocation = null;
        }
    }
    
    /**
     * Invoked when the mouse is moved.
     * 
     * @param e - the triggering event
     */
    public synchronized void mouseMoved(MouseEvent e)
    {
        hoveringFolder = on && getFolderArea().contains(e.getLocationOnScreen());
        
        selected = -1;
        if (state == FolderState.OPEN)
        {
            for (int i = 0; i < patterns.size(); i++)
            {
                Rectangle box = getPatternBox(i);
                box.x += location.x;
                box.y += location.y;
                if (box.contains(e.getLocationOnScreen()))
                {
                    selected = i;
                }
            }
        }
    }
    
    /**
     * Invoked when the mouse is pressed.
     * 
     * @param e - the triggering event
     */
    public synchronized void mousePressed(MouseEvent e)
    {
        if (on && getFolderArea().contains(e.getLocationOnScreen()))
        {
            if (state == FolderState.CLOSED)
            {
                state = FolderState.OPENING;
                findSize();
            }
            else if (state == FolderState.OPEN)
            {
                state = FolderState.CLOSING;
                findSize();
            }
        }
        
        if (on && selected != -1)
        {
            GameOfLife.getGrid().setSelectedPattern(patterns.get(selected));
        }
    }
    
    /**
     * Draws this PatternFolder.
     * The PatternFolder is drawn relative to the screen.
     * 
     * @param alpha - the transparency with which the PatternFolder should be drawn
     * @param g - the graphics context
     */
    public synchronized void draw(float alpha, Graphics2D g)
    {
        AcceleratedImage img = new AcceleratedImage(size.width, size.height);
        Graphics2D gImg = (Graphics2D) img.getContents().getGraphics();
        
        if (nameLocation == null)
        {
            nameLocation = new Point((int) (size.width - nameMetrics.getStringBounds(name, gImg).getWidth() - 15),
                    size.height - (bottomBuffer - nameFont.getSize())/2);
        }
        synchronized (nameLocation)
        {
            gImg.setFont(nameFont);
            gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gImg.setColor(nameShadowColor);
            gImg.drawString(name, nameLocation.x + 1, nameLocation.y + 1);
            gImg.setColor(nameColor);
            gImg.drawString(name, nameLocation.x, nameLocation.y);
            gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        folderBack.draw(folderLocation.x, size.height - folderLocation.y, gImg);
        folderFront.setScale(1 - folderOpenAmount, 1);
        folderFront.draw(folderLocation.x, size.height - folderLocation.y, gImg);
        
        if (state != FolderState.CLOSED)
        {
            AcceleratedImage patternsImg = new AcceleratedImage(
                    size.width - (openFolderLocation.x + folderSize), patterns.size()*patternHeight);
            Graphics2D gPatterns = (Graphics2D) patternsImg.getContents().getGraphics();
            AffineTransform t = new AffineTransform();
            t.setToTranslation(-openFolderLocation.x - folderSize, -size.height + bottomBuffer + patterns.size()*patternHeight);
            gPatterns.setTransform(t);
            
            for (int i = 0; i < patterns.size(); i++)
            {
                drawPatternBox(i, gPatterns);
            }
            
            patternsImg.setTransparency((float) openAmount);
            patternsImg.draw(openFolderLocation.x + folderSize,
                    size.height - bottomBuffer - (patterns.size())*patternHeight, gImg);
        }
        
        img.setTransparency(alpha);
        img.draw(location.x, location.y, g);
    }
    
    /**
     * Draws the box giving information regarding the pattern at the given index.
     * The drawing is done relative to location, as given by {@link #getPatternBox(int)}.
     * 
     * @param patternIndex - the index of the pattern to depict
     * @param g - the graphics context
     * @see #getPatternBox(int)
     */
    private void drawPatternBox(int patternIndex, Graphics2D g)
    {
        Rectangle box = getPatternBox(patternIndex);
        g.setColor(patternBoxBorder);
        g.fillRect(box.x, box.y, box.width, box.height);
        g.setColor(patternBoxBackground);
        g.fillRect(box.x + 1, box.y + 1, box.width - 2, box.height - 2);
        
        thumbs.get(patternIndex).draw(box.x + 1, box.y + 1, g);
        
        if (selected == patternIndex)
        {
            g.setColor(new Color(0, 163, 231));
            g.drawRect(box.x + box.height, box.y + 2, box.width - box.height - 3, box.height - 5);
        }
        
        g.setColor(patternNameColor);
        g.setFont(patternNameFont);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(patterns.get(patternIndex).shortName, box.x + box.height + 3, box.y + box.height - 5);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    
    /**
     * A trivial implementation of {@link FontMetrics} to provide its capabilities.
     * 
     * @author zirbinator
     */
    private static class Metrics extends FontMetrics
    {
        private static final long serialVersionUID = 1L;

        protected Metrics(Font f)
        {
            super(f);
        }
    }
    
    /**
     * Represents the state of a PatternFolder.
     * 
     * @author zirbinator
     */
    private static enum FolderState
    {
        CLOSED,
        OPENING,
        OPEN,
        CLOSING;
    }
}
