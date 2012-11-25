package main;

import graphics.DisplayMonitor;
import grid.Grid;

import image.ImageLoader;

import java.awt.Composite;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Handles launching the Game of Life simulation, running it in a rendering loop, and exiting.
 * This class also contains the main components of the game, such as the {@link Grid} that holds
 *  the state of the simulation and {@link Toolbar} that contains most of the interface.
 * 
 * @author zirbinator
 */
public class GameOfLife
{
    private ControlBar controlBar;
    
    private Diagnostics diagnostics;
    
    private static GameOfLife GoL;
    private Grid grid;
    
    private JFrame frame;
    
    /**
     * The amount of time taken each render loop in milliseconds.
     * That is, after the 
     */
    public static final long period = 20;
    
    private Toolbar toolbar;
    
    /**
     * The main method of the program.
     * A new GameOfLife object is instantiated and launched.
     * 
     * @param args - command-line arguments, ignored
     * @see #run()
     */
    public static void main(String[] args)
    {
        GoL = new GameOfLife();
        GoL.run();
    }
    
    /**
     * Runs the Game of Life.
     * First a full-screen exclusive window is created with the {@link DisplayMonitor}.
     * Next, the components of the simulation, such as the {@link Grid} are initialized.
     * Finally, this thread is looped infinitely in a draw/render/sleep loop.
     */
    public void run()
    {
        frame = DisplayMonitor.createFrame("Game of Life", new Panel());
        frame.setIconImage(ImageLoader.loadImage("icon"));
        
        diagnostics = new Diagnostics();
        grid = new Grid();
        toolbar = new Toolbar();
        controlBar = new ControlBar();
        
        long drawStart;
        long drawEnd;
        long renderEnd;
        long sleepTime;
        BufferStrategy strategy;
        
        while (true)
        {
            drawStart = System.nanoTime();
            // being "draw"
            strategy = frame.getBufferStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            draw(g);
            g.dispose();
            // end "draw"
            drawEnd = System.nanoTime();
            
            // begin "render"
            strategy.show();
            Toolkit.getDefaultToolkit().sync();
            // end "render"
            renderEnd = System.nanoTime();
            
            sleepTime = period - (System.nanoTime() - drawStart)/1000000;
            if (sleepTime <= 0)
            {
                System.out.println("[WARNING] Rendering took over the allotted period by " +
                        -sleepTime + " ms.");
            }
            else
            {
                try
                {
                    Thread.sleep(sleepTime);
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
            
            diagnostics.record(drawEnd - drawStart, renderEnd - drawEnd,
                    System.nanoTime() - renderEnd, System.nanoTime() - drawStart, sleepTime <= 0);
        }
    }
    
    /**
     * Gets the {@link Grid} object holding the state of the simulation.
     * 
     * @return the {@link Grid} used by this Game of Life
     */
    public static Grid getGrid()
    {
        return GoL.grid;
    }
    
    /**
     * Gets the {@link Toolbar} object responsible for the majority of the interface.
     * 
     * @return the {@link Toolbar} used by this Game of Life
     */
    public static Toolbar getToolbar()
    {
        return GoL.toolbar;
    }
    
    /**
     * Gets the {@link ControlBar} object responsible for closing and minimizing the window.
     * 
     * @return the {@link ControlBar} used by this Game of Life
     */
    public static ControlBar getControlBar()
    {
        return GoL.controlBar;
    }
    
    /**
     * Minimizes the window containing this Game of Life.
     */
    public static void minimize()
    {
        GoL.frame.setState(Frame.ICONIFIED);
    }
    
    /**
     * Determines whether the given event should be consumed for the given object.
     * That is, this method checks whether objects "higher" in the notification chain are using
     *  this event, in which case the given object may not.
     * For example, if the {@link Toolbar} were to check whether it can process (handle) a certain
     *  event, it must call this method with the event and <code>this</code>.
     * The method will check whether the {@link ControlBar} would handle this event, seeing as it
     *  has precedence over the {@link Toolbar}.
     * If the {@link ControlBar} has consumed the event, true would be returned, and the
     *  {@link Toolbar} should not handle it, otherwise false would be returned, and the
     *  {@link Toolbar} may handle it.
     * 
     * @param e - the event that may or may not be handled
     * @param o - the object that may or may not handle the event (should be <code>this</code> in
     *  most cases)
     * @return true if the event has been consumed and should not be handled by the object, false
     *  if the event has not been consumed and may be handled by the object
     */
    public static boolean consumed(MouseEvent e, Object o)
    {
        if (o == GoL.controlBar)
        {
            return false;
        }
        else if (o == GoL.toolbar)
        {
            return GoL.controlBar.consumed(e);
        }
        else if (o == GoL.grid)
        {
            return GoL.controlBar.consumed(e) || GoL.toolbar.consumed(e);
        }
        return true;
    }
    
    /**
     * Exits the Game of Life program.
     * Exiting diagnostic information is printed and then a call to <code>System.exit(0)</code> is
     *  made.
     * 
     * @see Diagnostics#printExitInfo()
     * @see System#exit(int);
     */
    public static void exit()
    {
        GoL.diagnostics.printExitInfo();
        System.exit(0);
    }
    
    /**
     * Draws the Game of Life on the given graphics context.
     * 
     * @param g - the graphics context of the screen or any buffers
     */
    private void draw(Graphics2D g)
    {
        Composite c = g.getComposite();
        grid.draw(g);
        g.setComposite(c);
        toolbar.draw(g);
        g.setComposite(c);
        controlBar.draw(g);
        g.setComposite(c);
        diagnostics.draw(g);
    }
    
    /**
     * A lightweight panel that allows for more operating-system compatibility, such as
     *  screenshots when it is applied to the full-screen window.
     * 
     * @author zirbinator
     */
    private class Panel extends JPanel
    {
        private static final long serialVersionUID = 1L;
        
        public void paintComponent(Graphics g)
        {
            try
            {
                draw((Graphics2D) g);
            }
            catch (NullPointerException ex) { }
        }
    }
}
