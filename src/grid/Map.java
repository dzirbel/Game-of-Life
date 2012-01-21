package grid;

import java.awt.Point;
import java.util.ArrayList;

import main.Information;

/**
 * Represents the two-dimensional array of cells, each of which is either alive or dead, able to be expanded infinitely.
 * The map of cells is stored in an ArrayList of Points, representing the coordinates of the cells that are alive.
 * Getting and setting the state of cells is simple through the use of the get() and set() methods.
 * The list of living cells is kept sorted primarily by the x-coordinates of the cells and,
 *  in the case of equal x-coordinates, by the y-coordinates, as given by ListUtils.compare().
 * The map uses the state of the toolbar to play/pause automatically and get the time between generations.
 * 
 * @author Dominic
 */
public class Map implements Runnable
{
	private ArrayList<Point> living;		// list of all coordinates of living cells, sorted primarily by x-coordinates and secondarily by y-coordinates
	
	private Information info;
	
	private long timeLastGeneration;
	private static long period = 10;
	
	/**
	 * Creates a new Map with the given Information.
	 * 
	 * @param info - the current Information
	 */
	public Map(Information info)
	{
		this.info = info;
		living = new ArrayList<Point>();
	}
	
	/**
	 * Runs the Map in its own Thread by sleeping until enough time has elapsed,
	 *  according to the toolbar's speed bar, to update the simulation with the update() method.
	 */
	public void run()
	{
		timeLastGeneration = System.nanoTime();
		while (true)
		{
			if (!info.toolbar.paused)
			{
				if (System.nanoTime() - timeLastGeneration > info.toolbar.speedBar.getPeriod() * 1000000)
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
	 * Updates the simulation using Simulation.simulate() and incrementing the generation.
	 */
	public void update()
	{
		living = Simulation.simulate(getLiving());
		info.generation++;
	}
	
	/**
	 * Clears the simulation by erasing all living cells and reseting the generation to 0.
	 */
	public void clear()
	{
		living = new ArrayList<Point>();
		info.generation = 0;
		info.toolbar.paused = true;
	}
	
	/**
	 * Sets the cell at the given coordinates to the given state.
	 * 
	 * @param alive - true if the cell should be made alive, false if it should be made dead
	 * @param point - the coordinates of the cell
	 */
	public void set(boolean alive, Point point)
	{
		int addIndex = ListUtils.getAddIndex(living, point);
		if (addIndex == -1)
		{
			// currently in list -> alive
			if (!alive)
			{
				living.remove(ListUtils.binary(living, point));
			}
		}
		else
		{
			// currently not in list -> dead
			if (alive)
			{
				if (addIndex != -1)
				{
					living.add(addIndex, point);
				}
			}
		}
	}
	
	/**
	 * Sets the cell at the given coordinates to the given state.
	 * 
	 * @param alive - true if the cell should be made alive, false if it should be made dead
	 * @param x - the x-coordinate of the cell
	 * @param y - the y-coordinate of the cell
	 */
	public void set(boolean alive, int x, int y)
	{
		set(alive, new Point(x, y));
	}
	
	/**
	 * Finds whether the cell at the given coordinates is alive.
	 * 
	 * @param x - the x-coordinate of the cell, in cells
	 * @param y - the y-coordinate of the cell, in cells
	 * @return alive - true if the cell at the given coordinates is alive, false otherwise
	 */
	public boolean get(int x, int y)
	{
		if (ListUtils.binary(living, new Point(x, y)) == -1)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a list of all the living cells.
	 * 
	 * @return living - a sorted list of the living cells
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Point> getLiving()
	{
		return (ArrayList<Point>)living.clone();
	}
}