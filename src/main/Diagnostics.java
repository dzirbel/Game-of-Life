package main;

import io.Listener;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class represents a collection of methods useful for giving diagnostic information.
 * The diagnostic view can be toggled with the F3 key.
 * Along the bottom of the screen is a graph of the times for various aspects of the rendering cycle over time in ms.
 * At the top-left of the screen, a small box of general information shows the current location of the mouse on the screen.
 * Below it, a box gives information about the current state of the Grid and acts as a graph of the time that simulating single generations take.
 * Finally, on the left of the screen is a display of the Java Heap usage.
 * 
 * @author Dominic
 */
public class Diagnostics
{
	private ArrayList<Long> drawTimes;
	private ArrayList<Long> renderTimes;
	private ArrayList<Long> sleepTimes;
	private ArrayList<Long> simulationTimes;
	
	private boolean visible;
	
	private static Color red = new Color(215, 0, 0);
	private static Color yellow = new Color(237, 201, 0);
	private static Color green = new Color(11, 159, 0);
	private static Color blue = new Color(0, 64, 156);
	private static Color purple = new Color(108, 0, 156);
	private static Color gray = new Color(130, 130, 130);
	
	private DecimalFormat df;
	
	private Information info;
	private static int graphYShift = 50;
	private static int graphXShift = 75;
	private static int graphHeight = 400;
	private static int graphVerticalDivision = 50;
	private static int graphDivisionSize = 7;
	private static int legendIconSize = 20;
	private static int legendTextSize = 100;
	private static int legendBuffer = 5;
	private static int gridInfoWidth = 450;
	private static int gridInfoHeight = 200;
	private static int generalInfoWidth = 250;
	private static int generalInfoHeight = 70;
	private static int heapWidth = 300;
	private static int heapHeight = 30;
	
	/**
	 * Creates a new Diagnostics object with the given Information.
	 * 
	 * @param info - the global Information
	 */
	public Diagnostics(Information info)
	{
		this.info = info;
		visible = false;
		drawTimes = new ArrayList<Long>();
		renderTimes = new ArrayList<Long>();
		sleepTimes = new ArrayList<Long>();
		simulationTimes = new ArrayList<Long>();
		df = new DecimalFormat("0.00");
		
		info.listener.requestNotification(this, "keyPressed", Listener.TYPE_KEY_PRESSED, Listener.CODE_KEY_ALL);
	}
	
	/**
	 * Invoked when a key is pressed.
	 * Changes the visibility of the diagnostic pane if the F3 key was pressed.
	 * 
	 * @param e - the KeyEvent that caused this method call
	 */
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_F3)
		{
			visible = !visible;
		}
	}
	
	/**
	 * Records the given information for one rendering loop.
	 * 
	 * @param draw - the amount of time to draw a single frame in ns
	 * @param render - the amount of time to render a single frame onto the screen in ns
	 * @param sleep - the amount of the time theoretically slept in ns
	 */
	public void recordRenderLoop(long draw, long render, long sleep)
	{
		drawTimes.add(draw);
		renderTimes.add(render);
		sleepTimes.add(sleep);
	}
	
	/**
	 * Records the given information for one simulation.
	 * 
	 * @param time - the amount of time to simulate a single generation in ns
	 */
	public void recordSimulation(long time)
	{
		simulationTimes.add(time);
	}
	
	/**
	 * Gets the value in the given list at the given distance from the end of the list.
	 * If this index is not in the array, -1 is returned
	 * 
	 * @param list - the list to reference
	 * @param shift - the distance from the end of the array
	 * @return value - the value in the given list at list.size() - shift - 1, -1 if this is out of bounds
	 */
	private static long getFromTop(ArrayList<Long> list, int shift)
	{
		if (list.size() < shift + 1)
		{
			return -1;
		}
		
		return list.get(list.size() - shift - 1);
	}
	
	/**
	 * Converts the given nanosecond measurement to a pixel value for display.
	 * 
	 * @param nanosecond - the recorded time in nanoseconds
	 * @return pixel - the distance above the horizontal axis at which the given value should be shown
	 */
	private static int toPixel(long nanosecond)
	{
		return (int)(nanosecond/100000);
	}
	
	/**
	 * Converts the given pixel value to a nanosecond measurement.
	 * 
	 * @param pixel - the distance above the horizontal axis at which the given value is shown
	 * @return nanosecond - the corresponding time in nanoseconds
	 */
	private static long toNanosecond(int pixel)
	{
		return pixel*100000;
	}
	
	/**
	 * Converts the given pixel value to a millisecond measurement.
	 * 
	 * @param pixel - the distance above the horizontal axis at which the given value is shown
	 * @return millisecond - the corresponding time in milliseconds
	 */
	private static long toMillisecond(int pixel)
	{
		return toNanosecond(pixel)/1000000;
	}
	
	/**
	 * Displays diagnostic information on the screen.
	 * A graph is shown along the bottom of the screen displaying the times various aspects of the rendering loop take in a single frame, in ms.
	 * Above it, a box of information regarding the Grid is shown.
	 * This box also functions as a graph axis for the simulation times, which will be displayed as red points.
	 * Above the Grid Information is a general information box containing the location of the cursor.
	 * Finally, a display of the heap storage is shown on the right of the screen.
	 * 
	 * @param g - the current Graphics object
	 */
	public void draw(Graphics2D g)
	{
		if (visible)
		{
			g.setFont(Information.fontTech);
			drawRenderingGraph(g);
			drawGridInfo(g, graphXShift, info.screen.height - 700);
			drawGeneralInfo(g, graphXShift, 50);
			drawMemory(g, info.screen.width - heapWidth - 75, 100);
		}
	}
	
	/**
	 * Draws a graph of the most recent recorded values of the rendering loop.
	 * The vertical scale is in milliseconds, and applies to all points drawn on the graph.
	 * The red points represent the time taken to draw the contents of the screen to an image.
	 * The yellow points represent the time taken to render this image to the screen.
	 * The green points represent the amount of "left over" time that is used to sleep.
	 * Sum points of all the above points are found and shown in purple.
	 * A horizontal line of the period of the rendering loop, the theoretical time for one rendering cycle, is given in blue.
	 * 
	 * @param g - the current Graphics context
	 */
	private void drawRenderingGraph(Graphics2D g)
	{
		if (drawTimes.size() > 0)
		{
			g.setColor(purple);
			for (int x = 0; x < Math.min(drawTimes.size(), info.screen.width - graphXShift); x++)
			{
				g.fillRect(x + graphXShift, info.screen.height - graphYShift - 
						(toPixel(getFromTop(drawTimes, x)) + toPixel(getFromTop(renderTimes, x)) + toPixel(getFromTop(sleepTimes, x))), 2, 2);
			}
			
			g.setColor(red);
			for (int x = 0; x < Math.min(drawTimes.size(), info.screen.width - graphXShift); x++)
			{
				g.fillRect(x + graphXShift, info.screen.height - graphYShift - toPixel(getFromTop(drawTimes, x)), 2, 2);
			}
			
			g.setColor(yellow);
			for (int x = 0; x < Math.min(drawTimes.size(), info.screen.width - graphXShift); x++)
			{
				g.fillRect(x + graphXShift, info.screen.height - graphYShift - toPixel(getFromTop(renderTimes, x)), 2, 2);
			}
			
			g.setColor(green);
			for (int x = 0; x < Math.min(drawTimes.size(), info.screen.width - graphXShift); x++)
			{
				g.fillRect(x + graphXShift, info.screen.height - graphYShift - toPixel(getFromTop(sleepTimes, x)), 2, 2);
			}
			
			g.setColor(blue);
			g.drawLine(graphXShift, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000)),
					info.screen.width, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000)));
			g.drawLine(graphXShift, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000) + 1),
					info.screen.width, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000)) + 1);
			g.drawLine(graphXShift, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000) - 1),
					info.screen.width, (int)(info.screen.height - graphYShift - toPixel(GameOfLife.period*1000000)) - 1);
			
			g.setColor(gray);
			g.drawLine(graphXShift, info.screen.height - graphYShift - graphHeight, graphXShift, info.screen.height - graphYShift);
			g.drawLine(graphXShift - 1, info.screen.height - graphYShift - graphHeight, graphXShift - 1, info.screen.height - graphYShift);
			
			g.drawLine(graphXShift, info.screen.height - graphYShift + 1, info.screen.width, info.screen.height - graphYShift + 1);
			g.drawLine(graphXShift, info.screen.height - graphYShift + 2, info.screen.width, info.screen.height - graphYShift + 2);
			
			for (int y = 0; y <= graphHeight; y += graphVerticalDivision)
			{
				g.drawLine(graphXShift - graphDivisionSize, info.screen.height - graphYShift - y, graphXShift, info.screen.height - graphYShift - y);
				g.drawString("" + toMillisecond(y), 25, info.screen.height - graphYShift - y + 5);
			}
			
			drawRenderingLegend(g, graphXShift, info.screen.height - graphYShift + 15);
		}
	}
	
	/**
	 * Draws a legend for the rendering graph at the given location.
	 * The blue line is the period.
	 * The red points are draw times.
	 * The yellow points are render times.
	 * The green points are sleep times.
	 * The purple points are sums of drawing, rendering, and sleeping.
	 * 
	 * @param g - the current Graphics context
	 * @param x - the top-left x-coordinate of the legend
	 * @param y - the top-left y-coordinate of the legend
	 */
	private void drawRenderingLegend(Graphics2D g, int x, int y)
	{
		int progress = x;
		
		g.setColor(blue);
		g.drawLine(progress, y + legendIconSize, progress + legendIconSize, y);
		g.drawLine(progress, y + legendIconSize + 1, progress + legendIconSize + 1, y);
		progress += legendIconSize + legendBuffer;
		
		g.setColor(gray);
		g.drawString("Period", progress, y + legendIconSize);
		progress += legendTextSize;
		
		g.setColor(red);
		g.fillRect(progress, y, legendIconSize, legendIconSize);
		progress += legendIconSize + legendBuffer;
		
		g.setColor(gray);
		g.drawString("Draw", progress, y + legendIconSize);
		progress += legendTextSize;
		
		g.setColor(yellow);
		g.fillRect(progress, y, legendIconSize, legendIconSize);
		progress += legendIconSize + legendBuffer;
		
		g.setColor(gray);
		g.drawString("Render", progress, y + legendIconSize);
		progress += legendTextSize;
		
		g.setColor(green);
		g.fillRect(progress, y, legendIconSize, legendIconSize);
		progress += legendIconSize + legendBuffer;
		
		g.setColor(gray);
		g.drawString("Sleep", progress, y + legendIconSize);
		progress += legendTextSize;
		
		g.setColor(purple);
		g.fillRect(progress, y, legendIconSize, legendIconSize);
		progress += legendIconSize + legendBuffer;
		
		g.setColor(gray);
		g.drawString("Sum", progress, y + legendIconSize);
		progress += legendTextSize;
	}
	
	/**
	 * Draws a box of information for the Grid.
	 * Included fields are the location, zoom, dragging (true/false), creating (true/false), and last drag point.
	 * A graph of simulation times is also drawn on top of the box.
	 * 
	 * @param g - the current Graphics context
	 * @param x - the top-left x-coordinate of the box
	 * @param y - the top-left y-coordinate of the box
	 */
	private void drawGridInfo(Graphics2D g, int x, int y)
	{
		g.setColor(gray);
		g.drawRect(x, y, gridInfoWidth, gridInfoHeight);
		g.drawString("Grid Information", x, y - 2);
		g.drawString("xLoc: " + df.format(info.grid.xLoc) + " [tile] " + df.format(info.grid.toPixel(info.grid.xLoc)) + " [px]", x + 5, y + 20);
		g.drawString("yLoc: " + df.format(info.grid.yLoc) + " [tile] " + df.format(info.grid.toPixel(info.grid.yLoc)) + " [px]", x + 5, y + 40);
		g.drawString("Zoom: " + df.format(info.grid.zoom), x + 5, y + 60);
		g.drawString("Dragging: " + info.grid.isDragging(), x + 5, y + 80);
		g.drawString("Creating: " + info.grid.isCreating(), x + 5, y + 100);
		g.drawString("Last Drag:", x + 5, y + 120);
		g.drawString(info.grid.getLastDrag().x + " [px] " + df.format(info.grid.toTile(info.grid.getLastDrag().x)) + " [tile]", x + 20, y + 140);
		g.drawString(info.grid.getLastDrag().y + " [px] " + df.format(info.grid.toTile(info.grid.getLastDrag().y)) + " [tile]", x + 20, y + 160);
		
		drawGridGraph(g, x, y);
	}
	
	/**
	 * Draws a graph of the durations of simulating generations.
	 * These times are shown as red points and are presumed to be drawn on the Grid Information box.
	 * 
	 * @param g - the current Graphics context
	 * @param x - the top-left x-coordinate of the box
	 * @param y - the top-left y-coordinate of the box
	 */
	private void drawGridGraph(Graphics2D g, int x, int y)
	{
		g.setColor(red);
		for (int i = 0; i < Math.min(simulationTimes.size(), gridInfoWidth); i++)
		{
			g.fillRect(i + x, y + gridInfoHeight - toPixel(getFromTop(simulationTimes, i)) - 5, 2, 2);
		}
		
		g.setColor(gray);
		for (int i = 0; i <= gridInfoHeight; i += graphVerticalDivision)
		{
			g.drawLine(x - graphDivisionSize, y + gridInfoHeight - i, x, y + gridInfoHeight - i);
			g.drawString("" + toMillisecond(i), 25, y + gridInfoHeight - i + 5);
		}
	}
	
	/**
	 * Draws a box of general information, containing the current location of the mouse pointer on the screen.
	 * 
	 * @param g - the current Graphics context
	 * @param x - the top-left x-coordinate of the box
	 * @param y - the top-left y-coordinate of the box
	 */
	private void drawGeneralInfo(Graphics2D g, int x, int y)
	{
		g.setColor(gray);
		g.drawRect(x, y, generalInfoWidth, generalInfoHeight);
		g.drawString("General Information", x, y - 2);
		g.drawString("Mouse: ", x + 5, y + 20);
		g.drawString(info.mouse.x + " [px] " + df.format(info.grid.getMouseTile().x) + " [tile]", x + 20, y + 40);
		g.drawString(info.mouse.y + " [px] " + df.format(info.grid.getMouseTile().y) + " [tile]", x + 20, y + 60);
	}
	
	/**
	 * Draws a display of the state of Java heap usage.
	 * 
	 * @param g - the current Graphics context
	 * @param x - the top-left x-coordinate of the box
	 * @param y - the top-left y-coordinate of the box
	 */
	private void drawMemory(Graphics2D g, int x, int y)
	{
		long heapSize = Runtime.getRuntime().totalMemory();
		long maxHeapSize = Runtime.getRuntime().maxMemory();
		long heapUsed = heapSize - Runtime.getRuntime().freeMemory();
		
		g.setColor(green);
		g.fillRect(x, y, heapWidth, heapHeight);
		
		g.setColor(yellow);
		g.fillRect(x, y, (int)(((double)heapSize/maxHeapSize)*heapWidth), heapHeight);
		
		g.setColor(red);
		g.fillRect(x, y, (int)(((double)heapUsed/maxHeapSize)*heapWidth), heapHeight);
		
		g.setColor(gray);
		g.drawRect(x, y, heapWidth, heapHeight);
		g.drawString("Heap Usage", x, y - 4f);
		
		g.setColor(green);
		g.fillRect(x, y + heapHeight + 5, 20, 20);
		g.setColor(gray);
		g.drawString("Max. Heap: " + maxHeapSize + " [B] " + maxHeapSize/1048576 + " [MB]", x + 25, y + heapHeight + 20);
		
		g.setColor(yellow);
		g.fillRect(x, y + heapHeight + 35, 20, 20);
		g.setColor(gray);
		g.drawString("Cur. Heap: " + heapSize + " [B] " + heapSize/1048576 + " [MB]", x + 25, y + heapHeight + 50);
		
		g.setColor(red);
		g.fillRect(x, y + heapHeight + 65, 20, 20);
		g.setColor(gray);
		g.drawString("Mem. Used: " + heapUsed + " [B] " + heapUsed/1048576 + " [MB]", x + 25, y + heapHeight + 80);
	}
}
