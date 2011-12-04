package main;

import java.awt.Point;

/**
 * Represents the grid of cells as a 2D boolean array.
 * Currently, the map is of a static size and all work must be done within that box.
 * 
 * @author Dominic
 */
public class Map implements Runnable
{
	public boolean[][] map;
	
	private long timeLastGeneration = 0;
	private long period = 3;
	
	private Information info;
	public int width = 0;
	public int height = 0;
	
	/**
	 * Creates a new Map object with the given dimensions and Information.
	 * 
	 * @param width - the width of the Map, in cells
	 * @param height - the height of the Map, in cells
	 * @param info - the current Information
	 */
	public Map(int width, int height, Information info)
	{
		this.width = width;
		this.height = height;
		this.info = info;
		map = new boolean[width][height];
	}
	
	/**
	 * Runs the Game of Life simulation, if the current game state is not paused.
	 * Between each update, a preset time is spent sleeping, creating a potential for delay between pausing
	 *  and a limit on the update speed, but allows the computer to spend time with other operations.
	 * If the game is not paused, the speed slider position is used to determine whether the time elapsed since the last update is enough that the map should be updated again.
	 * If so, update is called, otherwise the map is not updated and a certain is spent sleeping.
	 */
	public void run()
	{
		while (true)
		{
			if (!info.pane.paused)
			{
				if (System.nanoTime() - timeLastGeneration > info.pane.speedBar.getPeriod() * 1000000)
				{
					timeLastGeneration = System.nanoTime();
					update();
				}
			}
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException ex) { }
		}
	}	
	
	/**
	 * Updates the Game of Life simulation.
	 * While this method is not exceedingly efficient, it does update properly and within a reasonable time.
	 * First, an entirely new array of booleans is created.
	 * Then, the entire array is run through and each cell is either set to alive or dead based on the following rules:<br>
	 * If the cell was alive it remains alive if it had two or three neighbors that were alive, otherwise it dies.<br>
	 * If the cell was dead it becomes alive if exactly three neighbors were alive, otherwise it remains dead.<br>
	 * At the end of the method,
	 *  the map is set to the new map so that the changes take place as if they were (nearly) instantaneous and the generation is incremented.
	 */
	public void update()
	{
		boolean newMap[][] = new boolean[width][height];
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				boolean alive = isAlive(x, y);
				int numNeighborsAlive = 0;
				if (isAlive(x-1, y-1)) { numNeighborsAlive++; }
				if (isAlive(x-1, y))   { numNeighborsAlive++; }
				if (isAlive(x-1, y+1)) { numNeighborsAlive++; }
				if (isAlive(x,   y-1)) { numNeighborsAlive++; }
				if (isAlive(x,   y+1)) { numNeighborsAlive++; }
				if (isAlive(x+1, y-1)) { numNeighborsAlive++; }
				if (isAlive(x+1, y))   { numNeighborsAlive++; }
				if (isAlive(x+1, y+1)) { numNeighborsAlive++; }
				if (alive)
				{
					if (numNeighborsAlive < 2 || numNeighborsAlive > 3)
					{
						newMap[x][y] = false;
					}
					else
					{
						newMap[x][y] = true;
					}
				}
				else
				{
					if (numNeighborsAlive == 3)
					{
						newMap[x][y] = true;
					}
				}
			}
		}
		map = newMap;
		info.generation++;
	}
	
	/**
	 * Determines if the cell at the given coordinates is alive.
	 * 
	 * @param x - the x-coordinate of the cell, in cells
	 * @param y - the y-coordinate of the cell, in cells
	 * @return alive - true if the cell is alive, false otherwise or if the cell is out of the bounds of the array
	 */
	public boolean isAlive(int x, int y)
	{
		try
		{
			return map[x][y];
		}
		catch (IndexOutOfBoundsException ex)
		{
			//ExceptionHandler.receive(ex, "Attempted to check if the cell at " + x + ", " + y + " is alive. False was returned.");
			return false;
		}
	}
	
	/**
	 * Determines if the cell at the given point is alive.
	 * 
	 * @param p - the coordinates of the cell, in cells
	 * @return alive - true if the cell is alive, false otherwise or if the cell is out of the bounds of the array
	 */
	public boolean isAlive(Point p)
	{
		return isAlive(p.x, p.y);
	}
	
	/**
	 * Sets the cell at the given coordinates to the given life value.
	 * 
	 * @param x - the x-coordinate of the cell, in cells
	 * @param y - the y-coordinate of the cell, in cells
	 * @param alive - true if the cell should be set to alive, false if the cell should be set to dead
	 */
	public void setAlive(int x, int y, boolean alive)
	{
		try
		{
			map[x][y] = alive;
		}
		catch (IndexOutOfBoundsException ex)
		{
			//ExceptionHandler.receive(ex, "Attempted to set the cell at " + x + ", " + y + " to (true=on, false=off) " + alive + ".");
		}
	}
	
	/**
	 * Clears the map by reinitializing the 2D boolean array, setting all the cells to dead, and setting the current generation to 0.
	 */
	public void clear()
	{
		map = new boolean[width][height];
		info.generation = 0;
	}
}
