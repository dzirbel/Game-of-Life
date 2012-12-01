package main;

import graphics.AcceleratedImage;
import graphics.ButtonListener;
import graphics.DisplayMonitor;
import graphics.Tooltip;
import graphics.Tooltip.TooltipTheme;
import image.ImageLoader;
import io.Listener;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Represents a small area of the screen in the top left hand corner that allows the user to
 *  minimize or close the window with the mouse.
 * 
 * @author zirbinator
 */
public class ControlBar
{
    private AcceleratedImage background;
    private AcceleratedImage minimize;
    private AcceleratedImage close;
    
    private static Rectangle minimizeArea = new Rectangle(10, 5, 40, 40);
    private static Rectangle closeArea    = new Rectangle(50, 5, 40, 40);
    
    private Rectangle bounds;
    private RollOver minimizeRO;
    private RollOver closeRO;
    
    private Tooltip minimizeTooltip;
    private Tooltip closeTooltip;
    
    /**
     * Creates a new ControlBar by loading the images, initializing fields, and requesting user
     *  input notifications.
     */
    public ControlBar()
    {
        background = ImageLoader.load("control_bar");
        minimize = ImageLoader.load("minimize");
        close = ImageLoader.load("close");
        
        bounds = new Rectangle(DisplayMonitor.screen.width - background.getWidth(), 0,
                background.getWidth(), background.getHeight());
        minimizeTooltip = new Tooltip("Minimize",
                new Rectangle(bounds.x + minimizeArea.x, bounds.y + minimizeArea.y,
                        minimizeArea.width, minimizeArea.height), new TooltipTheme());
        closeTooltip = new Tooltip("Exit [esc]",
                new Rectangle(bounds.x + closeArea.x, bounds.y + closeArea.y,
                        minimizeArea.width, minimizeArea.height), new TooltipTheme());
        minimizeRO = new RollOver(minimizeTooltip.getHoverArea(), 3);
        closeRO = new RollOver(closeTooltip.getHoverArea(), 3);
        
        try
        {
            new ButtonListener(minimizeTooltip.getHoverArea(), "minimize", this);
            new ButtonListener(closeTooltip.getHoverArea(), "exit", this);
        }
        catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }
        
        Listener.requestNotification(this, "exit", Listener.TYPE_KEY_PRESSED,
                KeyEvent.VK_ESCAPE);
    }
    
    /**
     * Gets a cloned version of the boundaries of this ControlBar on the screen.
     * The ControlBar is guaranteed to draw within these boundaries and only handle mouse events
     *  originating in them.
     * 
     * @return the boundaries of this ControlBar in pixels
     */
    public Rectangle getBounds()
    {
        return (Rectangle) bounds.clone();
    }
    
    /**
     * Invoked when the user presses the "_" button.
     * Simply minimizes the Game of Life.
     * 
     * @see GameOfLife#minimize()
     */
    public void minimize()
    {
        GameOfLife.minimize();
    }
    
    /**
     * Invoked when the user releases the escape key or the "X" button.
     * Simply exits the Game of Life.
     * 
     * @see GameOfLife#exit()
     */
    public void exit()
    {
        GameOfLife.exit();
    }
    
    /**
     * Determines whether the given {@link MouseEvent} should be consumed by the ControlBar.
     * If so, no other components of the interface with lower input priority should react to this
     *  event.
     * 
     * @param e - an event from the user
     * @return true if the ControlBar has consumed this event and it should not be accessed by
     *  other components, false otherwise
     */
    public boolean consumed(MouseEvent e)
    {
        return bounds.contains(e.getLocationOnScreen());
    }
    
    /**
     * Draws the ControlBar on the screen.
     * 
     * @param g - the graphics context
     */
    public void draw(Graphics2D g)
    {
        background.draw(bounds.x, bounds.y, g);
        minimizeRO.draw(g);
        closeRO.draw(g);
        minimize.draw(bounds.x + minimizeArea.x, bounds.y + minimizeArea.y, g);
        close.draw(bounds.x + closeArea.x, bounds.y + closeArea.y, g);
        minimizeTooltip.draw(g);
        closeTooltip.draw(g);
    }
}
