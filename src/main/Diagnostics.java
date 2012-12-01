package main;

import graphics.DisplayMonitor;
import io.Listener;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Keeps track of diagnostic information and displays diagnostics on the screen.
 * The diagnostics are automatically toggled with the "F3" key.
 * 
 * @author zirbinator
 */
public class Diagnostics
{
    private final ArrayList<Long> drawTimes;
    private final ArrayList<Long> renderTimes;
    private final ArrayList<Long> sleepTimes;
    private final ArrayList<Long> totalTimes;
    
    private boolean visible;
    
    private static final Color red = new Color(215, 0, 0);
    private static final Color yellow = new Color(237, 201, 0);
    private static final Color green = new Color(11, 159, 0);
    private static final Color blue = new Color(0, 64, 156);
    private static final Color purple = new Color(108, 0, 156);
    /**
     * The standard border color for diagnostic information.
     * This color should be used to draw all boxes and most text.
     */
    public static final Color border = new Color(130, 130, 130);
    
    /**
     * The standard diagnostic number formatter.
     * This should be used to format most numbers displayed in the diagnostics.
     */
    public static final DecimalFormat df = new DecimalFormat("#0.000");
    private double frameRate;
    
    /**
     * The size (horizontal and vertical dimensions in pixels) of the icons in the legend of the
     *  drawing time graph.
     */
    private static final int legendIconSize = 20;
    /**
     * The height of the heap graph in pixels.
     */
    private static final int heapHeight = 30;
    /**
     * The largest render time shown on the rending graph in milliseconds (times beyond this are
     *  shown above the boundaries of the graph).
     */
    private static final int maxRenderTime = 50;
    private int drawLoops;
    private int longDrawLoops;
    
    private long lastUpdatedFrameRate;
    private static long heapSize = Runtime.getRuntime().totalMemory();
    private static long maxHeapSize = Runtime.getRuntime().maxMemory();
    private static long heapUsed = heapSize - Runtime.getRuntime().freeMemory();
    
    private static final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
    
    private static final Rectangle graphArea = new Rectangle(50, 450, 1200, 400);
    private static final Rectangle gridArea = new Rectangle(50, 185, 450, 200);
    private static final Rectangle toolbarArea = new Rectangle(550, 185, 450, 200);
    private static final Rectangle generalArea = new Rectangle(50, 50, 250, 110);
    private static final Rectangle memoryArea = new Rectangle(1150, 100, 350, 120);
    
    /**
     * Creates a new, empty Diagnostics.
     */
    public Diagnostics()
    {
        visible = false;
        drawTimes = new ArrayList<Long>();
        renderTimes = new ArrayList<Long>();
        sleepTimes = new ArrayList<Long>();
        totalTimes = new ArrayList<Long>();
        drawLoops = 0;
        longDrawLoops = 0;
        frameRate = -1;
        lastUpdatedFrameRate = System.nanoTime();
        
        Listener.requestNotification(this, "switchVisibility",
                Listener.TYPE_KEY_PRESSED, KeyEvent.VK_F3);
    }
    
    /**
     * Toggles the visibility of the diagnostic information.
     * This method is invoked automatically when the F3 key is released.
     */
    public synchronized void switchVisibility()
    {
        visible = !visible;
    }
    
    /**
     * Records the given draw/render/sleep data from a single drawing loop.
     * 
     * @param draw - the time it took for the drawing phase, in nanoseconds (the time it took to
     *  draw the Game of Life onto a buffer)
     * @param render - the time it took for the rendering phase, in nanoseconds (the time it took
     *  to apply the buffer onto the screen)
     * @param sleep - the time alloted for sleeping (may not be the actual time slept)
     */
    public synchronized void record(long draw, long render, long sleep, long total,
            boolean wasLong)
    {
        drawTimes.add(draw);
        renderTimes.add(render);
        sleepTimes.add(sleep);
        totalTimes.add(total);
        
        drawLoops++;
        if (wasLong)
        {
            longDrawLoops++;
        }
    }
    
    /**
     * Gets the current frame rate for the Game of Life in frames per second.
     * The frame rate is updated once every second.
     * 
     * @return the frame rate
     */
    public synchronized double getFrameRate()
    {
        if (System.nanoTime() - lastUpdatedFrameRate > 1000000000 || frameRate == -1)
        {
            frameRate = 1000000000.0/totalTimes.get(totalTimes.size() - 1);
            lastUpdatedFrameRate = System.nanoTime();
        }
        return frameRate;
    }
    
    /**
     * Prints summary information regarding the execution of the program to the standard output.
     * This should be called only when the program exits.
     */
    public synchronized void printExitInfo()
    {
        System.out.println(longDrawLoops + " [" + 100.0*longDrawLoops/drawLoops + "%] " +
        		"of the total number of drawing loops, " + drawLoops + ", took over the period.");
    }
    
    /**
     * Draws diagnostic information with the given graphics context if the diagnostics are
     *  currently toggled to visible.
     * If the diagnostics are invisible, nothing will be drawn.
     * 
     * @param g - the graphics context
     */
    public synchronized void draw(Graphics2D g)
    {
        if (visible)
        {
            g.setFont(font);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            drawGeneralInfo(g, generalArea);
            drawMemory(g, memoryArea);
            GameOfLife.getGrid().drawDiagnostics(g, gridArea);
            GameOfLife.getToolbar().drawDiagnostics(g, toolbarArea);
            drawRenderingGraph(g, graphArea);
        }
    }
    
    /**
     * Draws the "General Information" section in the given area.
     * Note that if the area is too small for the information it will be clipped to the area (with
     *  the exception of the title which is always drawn above the area).
     * 
     * @param g - the graphics context
     * @param area - the area in which to draw the general information box
     */
    private void drawGeneralInfo(Graphics2D g, Rectangle area)
    {
        g.setColor(border);
        g.drawRect(area.x, area.y, area.width, area.height);
        g.drawString("General Information", area.x, area.y - 2);
        
        g.setClip(area);
        g.drawString("Screen: " + DisplayMonitor.screen.width + "x" + DisplayMonitor.screen.height,
                area.x + 5, area.y + 20);
        g.drawString("Mouse: ", area.x + 5, area.y + 40);
        g.drawString(Listener.getMouse().x + " [px]", area.x + 20, area.y + 60);
        g.drawString(Listener.getMouse().y + " [px]", area.x + 20, area.y + 80);
        g.drawString("Frame Rate: " + df.format(getFrameRate()) + " [fps]",
                area.x + 5, area.y + 100);
        g.setClip(null);
    }
    
    /**
     * Draws information pertaining to the current state of the Java Heap in the given area.
     * Note that if the area is too small for the display it will be clipped to the area.
     * 
     * @param g - the graphics context
     * @param area - the area in which to draw memory information
     */
    private static void drawMemory(Graphics2D g, Rectangle area)
    {
        g.setClip(area);
        heapSize = Runtime.getRuntime().totalMemory();
        maxHeapSize = Runtime.getRuntime().maxMemory();
        heapUsed = heapSize - Runtime.getRuntime().freeMemory();
        
        g.setColor(green);
        g.fillRect(area.x, area.y, area.width, heapHeight);
        g.fillRect(area.x + 2, area.y + 2 + heapHeight + 5, 16, 16);
        
        g.setColor(yellow);
        g.fillRect(area.x, area.y, (int) ((double) heapSize/maxHeapSize*area.width), heapHeight);
        g.fillRect(area.x + 2, area.y + 2 + heapHeight + 35, 16, 16);
        
        g.setColor(red);
        g.fillRect(area.x, area.y, (int) ((double) heapUsed/maxHeapSize*area.width), heapHeight);
        g.fillRect(area.x + 2, area.y + 2 + heapHeight + 65, 16, 16);
        
        g.setColor(border);
        g.drawRect(area.x, area.y, area.width, heapHeight);
        g.drawString("JVM RAM Usage", area.x, area.y - 4);
        
        g.drawString("Max. Heap: " + maxHeapSize/1048576 + " [MB] " + maxHeapSize + " [B]",
                area.x + 25, area.y
                + heapHeight + 20);
        g.drawString("Cur. Heap: " + heapSize/1048576 + " [MB] " + heapSize + " [B]",
                area.x + 25, area.y
                + heapHeight + 50);
        g.drawString("Mem. Used: " + heapUsed/1048576 + " [MB] " + heapUsed + " [B]",
                area.x + 25, area.y
                + heapHeight + 80);
        g.setClip(null);
    }
    
    /**
     * Draws a graph of the recorded rendering times in the given area.
     * Note that the graph will exceed the area to the left and below, but never to the right.
     * In extraordinary circumstances (when the drawing/rendering times are above the period
     *  allotted for them) some parts of the graph will be plotted above the area and not clipped.
     * 
     * @param g - the graphics context
     * @param area - the area in which to draw the graph
     */
    private void drawRenderingGraph(Graphics2D g, Rectangle area)
    {
        while (drawTimes.size() > area.width - 1)
        {
            drawTimes.remove(0);
            renderTimes.remove(0);
            sleepTimes.remove(0);
            totalTimes.remove(0);
        }
        
        g.setColor(purple);
        for (int i = 0; i < totalTimes.size(); i++)
        {
            g.drawRect(area.x + totalTimes.size() - i, (int) (area.y + area.height - 
                            area.height*totalTimes.get(i)/(1000000*maxRenderTime)),
                    1, 1);
        }
        
        g.setColor(yellow);
        for (int i = 0; i < renderTimes.size(); i++)
        {
            g.drawRect(area.x + renderTimes.size() - i, (int) (area.y + area.height - 
                            area.height*renderTimes.get(i)/(1000000*maxRenderTime)),
                    1, 1);
        }
        
        g.setColor(green);
        for (int i = 0; i < sleepTimes.size(); i++)
        {
            g.drawRect(area.x + sleepTimes.size() - i, (int) (area.y + area.height - 
                            area.height*sleepTimes.get(i)/(1000000*maxRenderTime)),
                    1, 1);
        }
        
        g.setColor(red);
        for (int i = 0; i < drawTimes.size(); i++)
        {
            g.drawRect(area.x + drawTimes.size() - i, (int) (area.y + area.height - 
                    area.height*drawTimes.get(i)/(1000000*maxRenderTime)),
                    1, 1);
        }
        
        g.setColor(blue);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(area.x,
                (int) (area.y + area.height - area.height*GameOfLife.period/maxRenderTime),
                area.x + area.width,
                (int) (area.y + area.height - area.height*GameOfLife.period/maxRenderTime));
        
        g.setColor(border);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(area.x, area.y, area.x, area.y + area.height);
        g.drawLine(area.x, area.y + area.height, area.x + area.width, area.y + area.height);
        
        for (int y = 0; y <= maxRenderTime; y += 5)
        {
            g.drawLine(area.x - 7, area.y + area.height - area.height*y/maxRenderTime,
                    area.x, area.y + area.height - area.height*y/maxRenderTime);
            g.drawString(String.valueOf(y),
                    area.x - 35, area.y + area.height - area.height*y/maxRenderTime + 5);
        }
        
        int x = area.x;
        int y = area.y + area.height + 10;
        
        g.setColor(blue);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(x, y + legendIconSize, x + legendIconSize, y);
        x += legendIconSize + 10;
        
        g.setColor(border);
        g.drawString("Period", x, y + legendIconSize);
        x += 105;
        
        g.setColor(red);
        g.fillRect(x, y, legendIconSize, legendIconSize);
        x += legendIconSize + 10;
        
        g.setColor(border);
        g.drawString("Draw", x, y + legendIconSize);
        x += 105;
        
        g.setColor(yellow);
        g.fillRect(x, y, legendIconSize, legendIconSize);
        x += legendIconSize + 10;
        
        g.setColor(border);
        g.drawString("Render", x, y + legendIconSize);
        x += 105;
        
        g.setColor(green);
        g.fillRect(x, y, legendIconSize, legendIconSize);
        x += legendIconSize + 10;
        
        g.setColor(border);
        g.drawString("Sleep", x, y + legendIconSize);
        x += 105;
        
        g.setColor(purple);
        g.fillRect(x, y, legendIconSize, legendIconSize);
        x += legendIconSize + 10;
        
        g.setColor(border);
        g.drawString("Sum", x, y + legendIconSize);
    }
}
