package grid;

import java.awt.Point;
import java.util.ArrayList;

public class Simulation
{
	/**
	 * Simulates and returns the next generation of the given simulation based on the given rule set.
	 * Each living coordinate and coordinates surrounding that coordinate are added to a list of cells possibly alive in the next generation.
	 * Meanwhile, a list of the number of times each cell is added is kept so that, once all the possible cells have been added,
	 *  this list can be checked to see if an appropriate number of neighboring cells are alive, seeing as each living neighbor will
	 *  trigger one addition to the possible coordinates.
	 * The possible coordinates are then checked to see if the number of neighboring cells allows that cell to be alive in the next generation.
	 * 
	 * @param coords - a sorted list of the cells currently alive
	 * @param rules - the rules by which to simulate the next generation
	 * @return newCoords - the next generation of the given simulation
	 */
	public static ArrayList<Point> simulate(ArrayList<Point> coords, Rules rules)
	{
		ArrayList<Point> neighbors = new ArrayList<Point>();			// list of all cells next to currently alive cells
		ArrayList<Integer> neighborsAdded = new ArrayList<Integer>();	// number of times neighboring cells have been added - matched with neighbors
		ArrayList<Integer> coordsAdded = new ArrayList<Integer>();		// number of times currently alive cells have been added - matched with coords
		ArrayList<Point> newCoords = new ArrayList<Point>();
		
		for (int i = 0; i < coords.size(); i++)
		{
			coordsAdded.add(0);
		}
		
		for (int i = 0; i < coords.size(); i++)
		{
			for (int x = -1; x < 2; x++)
			{
				for (int y = -1; y < 2; y++)
				{
					if (!(x == 0 && y == 0))
					{
						Point point = new Point(coords.get(i).x + x, coords.get(i).y + y);
						if (ListUtils.binary(coords, point) != -1)
						{
							// this point is currently alive
							coordsAdded.set(i, coordsAdded.get(i) + 1);		// add 1 to the count of living neighbors of the currently alive cell at i
						}
						else
						{
							// this point is not currently alive
							int neighborsIndex = ListUtils.binary(neighbors, point);
							if (neighborsIndex == -1)
							{
								// neighbor not yet in the list
								int addIndex = ListUtils.getAddIndex(neighbors, point);
								neighbors.add(addIndex, point);				// add the neighbor to the list
								neighborsAdded.add(addIndex, 1);			// this neighbor has been added once
							}
							else
							{
								// neighbor is currently in the list
								neighborsAdded.set(neighborsIndex, neighborsAdded.get(neighborsIndex) + 1);
										// add one to the number of times this neighbor has been added
							}
						}
					}
				}
			}
		}
		
		for (int i = 0; i < coords.size(); i++)
		{
			if (rules.alive.contains(coordsAdded.get(i)))
			{
				// should be alive in new coordinates
				Point p = coords.get(i);
				int index = ListUtils.getAddIndex(newCoords, p);
				if (index != -1)
				{
					newCoords.add(index, p);
				}
			}
		}
		
		for (int i = 0; i < neighbors.size(); i++)
		{
			if (neighborsAdded.get(i) == 3)
			{
				// should be alive in new coordinates
				Point p = neighbors.get(i);
				int index = ListUtils.getAddIndex(newCoords, p);
				if (index != -1)
				{
					newCoords.add(index, p);
				}
			}
		}
		
		return newCoords;
	}
	
	/**
	 * Simulates and returns the next generation of the given simulation based on the default rule set.
	 * Each living coordinate and coordinates surrounding that coordinate are added to a list of cells possibly alive in the next generation.
	 * Meanwhile, a list of the number of times each cell is added is kept so that, once all the possible cells have been added,
	 *  this list can be checked to see if an appropriate number of neighboring cells are alive, seeing as each living neighbor will
	 *  trigger one addition to the possible coordinates.
	 * The possible coordinates are then checked to see if the number of neighboring cells allows that cell to be alive in the next generation.
	 * 
	 * @param coords - a sorted list of the cells currently alive
	 * @return newCoords - the next generation of the given simulation
	 */
	public static ArrayList<Point> simulate(ArrayList<Point> coords)
	{
		return simulate(coords, Rules.getRules(Rules.DEFAULT));
	}
}