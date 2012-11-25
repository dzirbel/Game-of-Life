package grid;

import graphics.AcceleratedImage;
import graphics.DisplayMonitor;
import image.ImageLoader;
import io.Listener;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import pattern.Pattern;

import main.Diagnostics;
import main.GameOfLife;

/**
 * Handles the interface between the user and a Map object.
 * The Grid is drawn as the background of the full-screen window and it handles all access to the
 *  Map, including:
 * <ul>
 * <li>Creation and destruction of cells with the mouse</li>
 * <li>Zooming and movement of the Grid</li>
 * <li>Rendering the Grid at the current position and zoom</li>
 * </ul>
 * 
 * @author zirbinator
 */
public class Grid implements Runnable
{
    private AcceleratedImage aliveImage;
    private ArrayList<Long> simulationTimes;
    
    /**
     * Whether the user is currently dragging to either create or destroy cells.
     */
    private boolean dragging;
    /**
     * Whether the user is currently creating cells with the mouse drag.
     * Note: this can be true even after the user has released the mouse.
     */
    private boolean creating;
    private boolean upHeld;
    private boolean downHeld;
    private boolean rightHeld;
    private boolean leftHeld;
    private boolean plusHeld;
    private boolean minusHeld;
    private boolean[][] clipboard;
    
    private static final Color backgroundColor = Color.black;
    /**
     * The color used for living cells when zoomed out, typically green.
     */
    public static final Color aliveColor = new Color(0, 215, 10);
    /**
     * The color of the dividers between the cells, typically gray.
     */
    private static final Color dividerColor = new Color(50, 50, 50);
    private static final Color simulationTimesColor = Color.red;
    
    /**
     * The far left coordinate of the screen's view of the grid, in cell coordinates.
     */
    protected double x;
    /**
     * The top coordinate of the screen's view of the grid, in cell coordinates.
     */
    protected double y;
    /**
     * The current zoom: the size (width and height) of a single cell in pixels.
     */
    double zoom;
    /**
     * The speed of movement from the arrow keys in pixels/millisecond: the amount moved (in
     *  pixels) in 1 millisecond is {@code MOVE_SPEED/zoom}.
     */
    private static final double MOVE_SPEED = 0.6;
    /**
     * The maximum ("closest") zoom: the maximum size (width and height) of a single cell in
     *  pixels.
     */
    private static final double MAX_ZOOM = 70;
    /**
     * The minimum ("farthest") zoom: the minimum size (width and height) of a single cell in
     *  pixels.
     */
    private static final double MIN_ZOOM = 1;
    /**
     * The minimum zoom (width and height of cells in pixels) for which grid lines appear at all.
     */
    private static final double GRID_ZOOM = 5;
    /**
     * The largest zoom (width and height of cells in pixels) for which the grid lines begin to
     *  fade.
     */
    private static final double FADE_START = 25;
    /**
     * The speed of zooming from the plus/minus keys in pixels/millisecond: the change (in the zoom
     *  in pixels) in 1 millisecond is {@code ZOOM_SPEED_KEY*zoom} while plus/minus are held.
     */
    private static final double ZOOM_SPEED_KEY = 0.003;
    /**
     * The speed of zooming from the mouse wheel: the change (in the zoom in pixels) from a single
     *  mouse wheel "tick" is {@code ZOOM_SPEED_MOUSE*zoom}.
     */
    private static final double ZOOM_SPEED_MOUSE = 0.1;
    
    private static final long period = 10;
    /**
     * The maximum simulation time labeled in the diagnostic view.
     * Simulation times above this are shown outside (above) the diagnostic view.
     */
    private static final long maxSimulationTime = 50;
    
    private Map map;
    
    private Pattern selectedPattern;
    private Point lastDrag;
    
    private Selection selection;
    
    /**
     * Creates a new, empty Grid.
     * The Grid's position is initialize to a standard zoom and the top-left of the screen to be
     *  the origin.
     * The Grid's Map is also created, and empty.
     */
    public Grid()
    {
        x = 0;
        y = 0;
        zoom = 20;
        upHeld = false;
        downHeld = false;
        rightHeld = false;
        leftHeld = false;
        
        dragging = false;
        creating = false;
        lastDrag = new Point();
        simulationTimes = new ArrayList<Long>();
        selection = new Selection(this);
        selectedPattern = null;
        
        map = new Map();
        clipboard = null;
        
        aliveImage = ImageLoader.load("alive", AcceleratedImage.OPAQUE);
        
        Listener.requestNotification(this, "keyPressed", Listener.TYPE_KEY_PRESSED);
        Listener.requestNotification(this, "keyReleased", Listener.TYPE_KEY_RELEASED);
        Listener.requestNotification(this, "mousePressed", Listener.TYPE_MOUSE_PRESSED);
        Listener.requestNotification(this, "mouseReleased",
                Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseDragged",
                Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseWheel",
                Listener.TYPE_MOUSE_WHEEL, Listener.CODE_SCROLL_BOTH);
        
        new Thread(this).start();
    }
    
    /**
     * Runs the Grid's position and zoom changes in a separate Thread.
     * That is, the position and zoom of the Grid are constantly updated based on the states of
     *  relevant keys such as the arrow and plus/minus keys.
     * 
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime();
        while (true)
        {
            long elapsed = (System.nanoTime() - lastUpdate)/1000000;
            if (upHeld && !downHeld)
            {
                y -= toCell(MOVE_SPEED*elapsed);
            }
            else if (downHeld && !upHeld)
            {
                y += toCell(MOVE_SPEED*elapsed);
            }
            if (rightHeld && !leftHeld)
            {
                x += toCell(MOVE_SPEED*elapsed);
            }
            else if (leftHeld && !rightHeld)
            {
                x -= toCell(MOVE_SPEED*elapsed);
            }
            if (plusHeld && !minusHeld)
            {
                zoom(ZOOM_SPEED_KEY*elapsed*zoom);
            }
            else if (minusHeld && !plusHeld)
            {
                zoom(-ZOOM_SPEED_KEY*elapsed*zoom);
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
     * Gets the current generation of the Grid's Map.
     * The generation is a counter which is incremented every time the Grid is updated and reset
     *  (to 0) whenever the Grid is cleared.
     * 
     * @return the current generation of the Map
     */
    public int getGeneration()
    {
        return map.getGeneration();
    }
    
    /**
     * Sets the pattern currently "held" by the user.
     * This pattern is shown on the screen as if it is dragged by the mouse, and is placed onto the
     *  grid when the user clicks the mouse (or removed when the user right-clicks).
     * 
     * @param pattern - the currently selected pattern, or null for no pattern
     */
    public void setSelectedPattern(Pattern pattern)
    {
        selectedPattern = pattern;
    }
    
    /**
     * Updates the Grid's Map, which simulates the next generation and replaces the Map's contents
     *  with the next generation.
     * 
     * @see Map#update()
     */
    public void update()
    {
        long before = System.nanoTime();
        map.update();
        simulationTimes.add((System.nanoTime() - before)/1000000);
    }
    
    /**
     * Clears the Grid's Map, which removes all living cells from the Map and resets the generation
     *  counter.
     * 
     * @see Map#clear()
     */
    public void clear()
    {
        map.clear();
        simulationTimes.clear();
    }
    
    /**
     * Clears the given area.
     * 
     * @param area - the area of the grid to clear, in cell coordinates
     * @see Map#clear(Rectangle)
     */
    public void clear(Rectangle area)
    {
        map.clear(area);
    }
    
    /**
     * Copies the given area onto the Grid's clipboard.
     * 
     * @param area - the area of the grid to copy, in cell coordinates
     */
    public void copy(Rectangle area)
    {
        clipboard = new boolean[area.width][area.height];
        for (int x = 0; x < area.width; x++)
        {
            for (int y = 0; y < area.height; y++)
            {
                clipboard[x][y] = map.isAlive(area.x + x, area.y + y);
            }
        }
    }
    
    /**
     * Creates a rectangular border around the interior of the given area.
     * 
     * @param area - the area of the grid to "square", in cell coordinates
     * @see Map#square(Rectangle)
     */
    public void square(Rectangle area)
    {
        map.square(area);
    }
    
    /**
     * Creates an ellipse contained in the given area.
     * 
     * @param area - the area of the grid in which to create an oval, in cell coordinates
     * @see Map#oval(Rectangle)
     */
    public void oval(Rectangle area)
    {
        map.oval(area);
    }
    
    /**
     * Rotates the given area of the Grid clockwise 90 degrees.
     * 
     * @param area - the area of the grid to rotate, in cell coordinates
     * @return the transformed area
     * @see Map#rotateCW(Rectangle)
     */
    public Rectangle rotateCW(Rectangle area)
    {
        return map.rotateCW(area);
    }
    
    /**
     * Rotates the given area of the Grid counterclockwise 90 degrees.
     * 
     * @param area - the area of the grid to rotate, in cell coordinates
     * @return the transformed area
     * @see Map#rotateCCW(Rectangle)
     */
    public Rectangle rotateCCW(Rectangle area)
    {
        return map.rotateCCW(area);
    }
    
    /**
     * Pastes the current contents of the clipboard at the current location of the mouse.
     */
    private void pasteClipboard()
    {
        if (clipboard != null)
        {
            Cell mouse = getMouseCell();
            for (int x = 0; x < clipboard.length; x++)
            {
                for (int y = 0; y < clipboard[x].length; y++)
                {
                    map.setAlive(mouse.x + x, mouse.y + y, clipboard[x][y]);
                }
            }
        }
    }
    
    /**
     * Invoked when a key is pressed.
     * The states of movement and zooming keys are updated.
     * 
     * @param e - the triggering event
     */
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_RIGHT:
            rightHeld = true;
            break;
        case KeyEvent.VK_LEFT:
            leftHeld = true;
            break;
        case KeyEvent.VK_UP:
            upHeld = true;
            break;
        case KeyEvent.VK_DOWN:
            downHeld = true;
            break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_EQUALS:
            plusHeld = true;
            break;
        case KeyEvent.VK_MINUS:
            minusHeld = true;
            break;
        }
    }
    
    /**
     * Invoked when a key is released.
     * The states of movement and zooming keys are updated and "V" with the control key held pastes
     *  the current clipboard.
     * 
     * @param e - the triggering event
     */
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
        case KeyEvent.VK_RIGHT:
            rightHeld = false;
            break;
        case KeyEvent.VK_LEFT:
            leftHeld = false;
            break;
        case KeyEvent.VK_UP:
            upHeld = false;
            break;
        case KeyEvent.VK_DOWN:
            downHeld = false;
            break;
        case KeyEvent.VK_PLUS:
        case KeyEvent.VK_EQUALS:
            plusHeld = false;
            break;
        case KeyEvent.VK_MINUS:
            minusHeld = false;
            break;
        case KeyEvent.VK_V:
            if (Listener.controlHeld())
            {
                pasteClipboard();
            }
            break;
        }
    }
    
    /**
     * Invoked when the mouse is pressed.
     * 
     * @param e - the triggering event
     */
    public void mousePressed(MouseEvent e)
    {
        if (!GameOfLife.consumed(e, this))
        {
            if (e.getButton() == MouseEvent.BUTTON1)
            {
                if (!selection.mousePressed(e))
                {
                    Cell mouseCell = getMouseCell();
                    if (selectedPattern == null)
                    {
                        dragging = true;
                        
                        if (map.isAlive(mouseCell.x, mouseCell.y))
                        {
                            map.setAlive(mouseCell.x, mouseCell.y, false);
                            creating = false;
                        }
                        else
                        {
                            map.setAlive(mouseCell.x, mouseCell.y, true);
                            creating = true;
                        }
                    }
                    else
                    {
                        for (int x = 0; x < selectedPattern.getWidth(); x++)
                        {
                            for (int y = 0; y < selectedPattern.getHeight(); y++)
                            {
                                map.setAlive(mouseCell.x + x, mouseCell.y + y,
                                        selectedPattern.pattern[x][y]);
                            }
                        }
                    }
                }
            }
            else if (e.getButton() == MouseEvent.BUTTON3)
            {
                selectedPattern = null;
            }
        }
        
        lastDrag = e.getLocationOnScreen();
    }
    
    /**
     * Invoked when the mouse is released.
     * 
     * @param e - the triggering event
     */
    public void mouseReleased(MouseEvent e)
    {
        selection.mouseReleased(e);
        dragging = false;
    }
    
    /**
     * Invoked when the mouse is dragged.
     * 
     * @param e - the triggering event
     */
    public void mouseDragged(MouseEvent e)
    {
        if (!GameOfLife.consumed(e, this))
        {
            if (dragging)
            {
                Cell mouseCell = getMouseCell();
                map.setAlive(mouseCell.x, mouseCell.y, creating);
            }
            else
            {
                selection.mouseDragged(e);
            }
        }
        
        lastDrag = e.getLocationOnScreen();
    }
    
    /**
     * Invoked when the mouse wheel is scrolled.
     * 
     * @param e - the triggering event
     */
    public void mouseWheel(MouseWheelEvent e)
    {
        zoom(-ZOOM_SPEED_MOUSE * zoom * e.getWheelRotation());
    }
    
    /**
     * Zooms by the given amount.
     * The zoom is adjusted by the given amount and kept between {@link #MIN_ZOOM} and
     *  {@link #MAX_ZOOM}.
     * The x- and y-coordinates of the Grid are also adjusted so that the cursor is kept over the
     *  same cell before and after the zoom change.
     * 
     * @param amount - the amount to change the zoom, in pixels
     */
    protected void zoom(double amount)
    {
        double prevZoom = zoom;
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom + amount));
        
        x -= (DisplayMonitor.screen.width - zoom/prevZoom*DisplayMonitor.screen.width)/zoom
                *Listener.getMouse().x/DisplayMonitor.screen.width;
        y -= (DisplayMonitor.screen.height - zoom/prevZoom*DisplayMonitor.screen.height)/zoom
                *Listener.getMouse().y/DisplayMonitor.screen.height;
    }
    
    /**
     * Converts the given pixel value to a cell location.
     * That is, the value scaled by the current zoom is returned, so that this method is equivalent
     *  to the statement {@code pixel/zoom}.
     * Examples:
     * <ul>
     * <li>{@code toCell(0) = 0}</li>
     * <li>{@code toCell(screen_width)} is the number of cell shown on the screen</li>
     * <li>{@code toCell(mouse_x)} is the x-coordinate of the cell that the mouse is currently
     *  hovering over, shifted by -{@link #x}.</li>
     * </ul>
     * 
     * @param pixel - the pixel value to be converted to the cell scale
     * @return the cell equivalent of the given pixel value
     * @see #toPixel(double)
     */
    protected double toCell(double pixel)
    {
        return pixel / zoom;
    }
    
    /**
     * Converts the given cell value to a pixel location.
     * That is, the value is scaled by the current zoom is returned, so this method is equivalent
     *  to the statement {@code cell*zoom}.
     * Examples:
     * <ul>
     * <li>{@code toPixel(0) = 0}</li>
     * <li>{@code toPixel(1) = zoom}</li>
     * </ul>
     * 
     * @param cell - the cell value to be converted to the pixel scale
     * @return the pixel equivalent of the given cell value
     * @see #toCell(double)
     */
    protected double toPixel(double cell)
    {
        return cell * zoom;
    }
    
    /**
     * Gets the Cell over which the mouse is currently hovering.
     * 
     * @return the Cell whose coordinates are the coordinates of the cell currently below the mouse
     */
    private Cell getMouseCell()
    {
        return new Cell(roundToward0(x + toCell(Listener.getMouse().x)),
                roundToward0(y + toCell(Listener.getMouse().y)));
    }
    
    /**
     * Gets the integer farthest from 0 that is closer to 0 (or the same distance)than the given
     *  double.
     * That is, if the given value is less than 0, it is truncated and decremented, and if it is
     *  greater than or equal to 0 it is simply truncated.
     * 
     * @param a - the value to which to round towards 0
     * @return the given value, rounded (truncated) "towards" 0
     */
    private static int roundToward0(double a)
    {
        if (a < 0)
        {
            return (int) a - 1;
        }
        return (int) a;
    }
    
    /**
     * Draws the Grid on the screen.
     * Note that any drawing operations before this will be erased because the entire screen is
     *  filled with the background color of the Grid.
     * 
     * @param g - the graphics context
     */
    public void draw(Graphics2D g)
    {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, DisplayMonitor.screen.width, DisplayMonitor.screen.height);
        
        ArrayList<Cell> alive = map.getAlive();
        
        if (zoom <= FADE_START)
        {
            g.setColor(aliveColor);
            
            for (int i = 0; i < alive.size(); i++)
            {
                Cell c = alive.get(i);
                if (c.x >= x - 1 && c.y >= y - 1 && c.x < x + toCell(DisplayMonitor.screen.width)
                        && c.y < y + toCell(DisplayMonitor.screen.height))
                {
                    g.fillRect((int) toPixel(c.x - x) + (c.x > x ? 1 : 0),
                            (int) toPixel(c.y - y) + (c.y > y ? 1 : 0),
                            (int) zoom, (int) zoom);
                }
            }
            
            if (selectedPattern != null)
            {
                Cell mouse = getMouseCell();
                for (int i = 0; i < selectedPattern.getWidth(); i++)
                {
                    for (int j = 0; j < selectedPattern.getHeight(); j++)
                    {
                        if (selectedPattern.pattern[i][j])
                        {
                            Cell c = new Cell(mouse.x + i, mouse.y + j);
                            if (c.x >= x - 1 && c.y >= y - 1 && c.x < x + toCell(DisplayMonitor.screen.width)
                                    && c.y < y + toCell(DisplayMonitor.screen.height))
                            {
                                g.fillRect((int) toPixel(c.x - x) + (c.x > x ? 1 : 0),
                                        (int) toPixel(c.y - y) + (c.y > y ? 1 : 0),
                                        (int) zoom, (int) zoom);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            aliveImage.setScale(zoom / aliveImage.getWidth(), zoom / aliveImage.getHeight());
            
            for (int i = 0; i < alive.size(); i++)
            {
                Cell c = alive.get(i);
                if (c.x >= x - 1 && c.y >= y - 1 && c.x < x + toCell(DisplayMonitor.screen.width)
                        && c.y < y + toCell(DisplayMonitor.screen.height))
                {
                    g.setClip((int) (toPixel(c.x - x) + (c.x > x ? 1 : 0)),
                            (int) (toPixel(c.y - y) + (c.y > y ? 1 : 0)), (int) zoom, (int) zoom);
                    
                    aliveImage.draw((int) (toPixel(c.x - x) + (c.x > x ? 1 : 0)),
                            (int) (toPixel(c.y - y) + (c.y > y ? 1 : 0)), g);
                }
            }
            
            if (selectedPattern != null)
            {
                Cell mouse = getMouseCell();
                for (int i = 0; i < selectedPattern.getWidth(); i++)
                {
                    for (int j = 0; j < selectedPattern.getHeight(); j++)
                    {
                        if (selectedPattern.pattern[i][j])
                        {
                            Cell c = new Cell(mouse.x + i, mouse.y + j);
                            if (c.x >= x - 1 && c.y >= y - 1 && c.x < x + toCell(DisplayMonitor.screen.width)
                                    && c.y < y + toCell(DisplayMonitor.screen.height))
                            {
                                g.setClip((int) (toPixel(c.x - x) + (c.x > x ? 1 : 0)),
                                        (int) (toPixel(c.y - y) + (c.y > y ? 1 : 0)), (int) zoom, (int) zoom);
                                
                                aliveImage.draw((int) (toPixel(c.x - x) + (c.x > x ? 1 : 0)),
                                        (int) (toPixel(c.y - y) + (c.y > y ? 1 : 0)), g);
                            }
                        }
                    }
                }
            }
            
            g.setClip(null);
        }
        
        Composite c = g.getComposite();
        if (zoom >= GRID_ZOOM)
        {
            g.setColor(dividerColor);
            
            if (zoom <= FADE_START)
            {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,
                        (float) (zoom/FADE_START)));
            }
            
            for (double x0 = Math.floor(x) - x; x0 < DisplayMonitor.screen.width / zoom + 1; x0++)
            {
                g.drawLine((int) toPixel(x0), 0, (int) toPixel(x0), DisplayMonitor.screen.height);
            }
            for (double y0 = Math.floor(y) - y; y0 < DisplayMonitor.screen.height / zoom + 1; y0++)
            {
                g.drawLine(0, (int) toPixel(y0), DisplayMonitor.screen.width, (int) toPixel(y0));
            }
        }
        g.setComposite(c);
        
        selection.draw(g);
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
    public synchronized void drawDiagnostics(Graphics2D g, Rectangle area)
    {
        g.setColor(Diagnostics.border);
        g.drawRect(area.x, area.y, area.width, area.height);
        g.drawString("Grid Information", area.x, area.y - 2);
        
        g.drawString("x: " + Diagnostics.df.format(x) + " [tile] " + 
                Diagnostics.df.format(toPixel(x)) + " [px]",
                area.x + 5, area.y + 20);
        g.drawString("y: " + Diagnostics.df.format(y) + " [tile] " + 
                Diagnostics.df.format(toPixel(y)) + " [px]",
                area.x + 5, area.y + 40);
        
        g.drawString("Zoom: " + Diagnostics.df.format(zoom), area.x + 5, area.y + 60);
        g.drawString("Dragging: " + dragging, area.x + 5, area.y + 80);
        g.drawString("Creating: " + creating, area.x + 5, area.y + 100);
        
        g.drawString("Last Drag:", area.x + 5, area.y + 120);
        g.drawString(Diagnostics.df.format(lastDrag.x) + " [px] " + 
        Diagnostics.df.format(toCell(lastDrag.x)) + " [tile]",
                area.x + 20, area.y + 140);
        g.drawString(Diagnostics.df.format(lastDrag.y) + " [px] " + 
                Diagnostics.df.format(toCell(lastDrag.y)) + " [tile]",
                area.x + 20, area.y + 160);
        
        for (int i = 0; i <= 10; i++)
        {
            g.drawLine(area.x - 4, area.y + area.height - i*area.height/10,
                    area.x, area.y + area.height - i*area.height/10);
            g.drawString(String.valueOf(maxSimulationTime * i/10),
                    area.x - 30, area.y + area.height - i*area.height/10 + 5);
        }
        
        while (simulationTimes.size() > area.width - 1)
        {
            simulationTimes.remove(0);
        }
        
        g.setColor(simulationTimesColor);
        for (int i = 0; i < simulationTimes.size(); i++)
        {
            g.drawRect(area.x + simulationTimes.size() - i - 1,
                    (int) (area.y + area.height - 
                            area.height*simulationTimes.get(i)/maxSimulationTime - 2),
                    1, 1);
        }
    }
}
