package grid;

import java.awt.Point;
import java.util.ArrayList;

public class Simulation
{
//	public static int simulateWins = 0;
//	public static int simulate2Wins = 0;
//	
//	/**
//	 * Simulates and returns the next generation of the given simulation based on the given rule set.
//	 * Each living coordinate and coordinates surrounding that coordinate are added to a list of cells possibly alive in the next generation.
//	 * Meanwhile, a list of the number of times each cell is added is kept so that, once all the possible cells have been added,
//	 *  this list can be checked to see if an appropriate number of neighboring cells are alive, seeing as each living neighbor will
//	 *  trigger one addition to the possible coordinates.
//	 * The possible coordinates are then checked to see if the number of neighboring cells allows that cell to be alive in the next generation.
//	 * 
//	 * @param coords - a sorted list of the cells currently alive
//	 * @param rules - the rules by which to simulate the next generation
//	 * @return newCoords - the next generation of the given simulation
//	 */
//	public static ArrayList<Point> simulate(ArrayList<Point> coords, Rules rules)
//	{
//		long simulateStart = System.nanoTime();
//		
//		ArrayList<Point> neighbors = new ArrayList<Point>();			// list of all cells next to currently alive cells
//		ArrayList<Integer> neighborsAdded = new ArrayList<Integer>();	// number of times neighboring cells have been added - matched with neighbors
//		ArrayList<Integer> coordsAdded = new ArrayList<Integer>();		// number of times currently alive cells have been added - matched with coords
//		ArrayList<Point> newCoords = new ArrayList<Point>();
//		
//		for (int i = 0; i < coords.size(); i++)
//		{
//			coordsAdded.add(0);
//		}
//		
//		for (int i = 0; i < coords.size(); i++)
//		{
//			for (int x = -1; x < 2; x++)
//			{
//				for (int y = -1; y < 2; y++)
//				{
//					if (!(x == 0 && y == 0))
//					{
//						Point point = new Point(coords.get(i).x + x, coords.get(i).y + y);
//						if (ListUtils.binary(coords, point) != -1)
//						{
//							// this point is currently alive
//							coordsAdded.set(i, coordsAdded.get(i) + 1);		// add 1 to the count of living neighbors of the currently alive cell at i
//						}
//						else
//						{
//							// this point is not currently alive
//							int neighborsIndex = ListUtils.binary(neighbors, point);
//							if (neighborsIndex == -1)
//							{
//								// neighbor not yet in the list
//								int addIndex = ListUtils.getAddIndex(neighbors, point);
//								neighbors.add(addIndex, point);				// add the neighbor to the list
//								neighborsAdded.add(addIndex, 1);			// this neighbor has been added once
//							}
//							else
//							{
//								// neighbor is currently in the list
//								neighborsAdded.set(neighborsIndex, neighborsAdded.get(neighborsIndex) + 1);
//										// add one to the number of times this neighbor has been added
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		for (int i = 0; i < coords.size(); i++)
//		{
//			if (rules.alive.contains(coordsAdded.get(i)))
//			{
//				// should be alive in new coordinates
//				Point p = coords.get(i);
//				int index = ListUtils.getAddIndex(newCoords, p);
//				if (index != -1)
//				{
//					newCoords.add(index, p);
//				}
//			}
//		}
//		
//		for (int i = 0; i < neighbors.size(); i++)
//		{
//			if (neighborsAdded.get(i) == 3)
//			{
//				// should be alive in new coordinates
//				Point p = neighbors.get(i);
//				int index = ListUtils.getAddIndex(newCoords, p);
//				if (index != -1)
//				{
//					newCoords.add(index, p);
//				}
//			}
//		}
//		
//		long simulateEnd = System.nanoTime();
//		
//		ArrayList<Point> simulate2Coords = simulate2(coords, rules);
//		
//		long end = System.nanoTime();
//		
//		if (!newCoords.equals(simulate2Coords))
//		{
//			System.out.println("[ERROR] The simulate methods returned different results!");
//			System.out.println("[ERROR]    Simulate:\n" + newCoords);
//			System.out.println("[ERROR]    Simulate 2:\n" + simulate2Coords);
//			System.exit(0);
//		}
//		
//		long simulateTime = simulateEnd - simulateStart;
//		long simulate2Time = end - simulateEnd;
//		
//		System.out.println("   Simulate2: " + simulate2Time + " ns; Simulate: " + simulateTime + " ns; Diff:" + (simulate2Time - simulateTime));
//		
//		if (simulateTime > simulate2Time)
//		{
//			simulate2Wins++;
//		}
//		else if (simulateTime < simulate2Time)
//		{
//			simulateWins++;
//		}
//		
//		return newCoords;
//	}
	
	/**
	 * Simulates and returns the next generation of the given simulation based on the given rule set.
	 * A two dimensional array is made from the given coordinates so that determining whether a cell at a given location is alive is faster.
	 * Each living coordinate and coordinates surrounding that coordinate are added to a list of cells possibly alive in the next generation,
	 *  which also keeps track of the number of times each potential cell has been added.
	 * The possible coordinates are then checked to see if the number of neighboring cells allows that cell to be alive in the next generation.
	 * 
	 * @param coords - a sorted list of the cells currently alive
	 * @param rules - the rules by which to simulate the next generation
	 * @return newCoords - the next generation of the given simulation
	 */
	public static ArrayList<Point> simulate(ArrayList<Point> coords, Rules rules)
	{
		if (coords.size() == 0)
		{
			return coords;
		}
		
		int xMin = coords.get(0).x;
		int yMin = coords.get(0).y;
		int yMax = coords.get(0).y;
		for (int i = 0; i < coords.size(); i++)
		{
			if (coords.get(i).y > yMax)
			{
				yMax = coords.get(i).y;
			}
			else if (coords.get(i).y < yMin)
			{
				yMin = coords.get(i).y;
			}
		}
		
		boolean[][] map = new boolean[coords.get(coords.size() - 1).x - xMin + 1][yMax - yMin + 1];
		
		for (int i = 0; i < coords.size(); i++)
		{
			map[coords.get(i).x - xMin][coords.get(i).y - yMin] = true;
		}
		
		ArrayList<Cell> alive = new ArrayList<Cell>();
		ArrayList<Cell> dead = new ArrayList<Cell>();
		
		for (int i = 0; i < coords.size(); i++)
		{
			alive.add(new Cell(coords.get(i), 0));
			for (int x = -1; x < 2; x++)
			{
				for (int y = -1; y < 2; y++)
				{
					if (!(x == 0 && y == 0))
					{
						Point point = new Point(coords.get(i).x + x, coords.get(i).y + y);
						
						boolean cellAlive;
						try
						{
							cellAlive = map[point.x - xMin][point.y - yMin];
						}
						catch (IndexOutOfBoundsException ex)
						{
							cellAlive = false;
						}
						
						if (cellAlive)
						{
							// this point is currently alive
							alive.get(i).add();								// add 1 to the count of living neighbors of the currently alive cell at i
						}
						else
						{
							// this point is not currently alive
							int index = ListUtils.binaryCell(dead, point);
							if (index == -1)
							{
								// neighbor not yet in the list
								int addIndex = ListUtils.getAddIndexCell(dead, point);
								dead.add(addIndex, new Cell(point, 1));		// add the dead cell to the list, with one time added
							}
							else
							{
								// neighbor is currently in the list
								dead.get(index).add();						// add one to the number of times this dead cell has been added
							}
						}
					}
				}
			}
		}
		
		ArrayList<Point> newCoords = new ArrayList<Point>();
		
		for (int i = 0; i < alive.size(); i++)
		{
			if (rules.alive.contains(alive.get(i).timesAdded))
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
		
		for (int i = 0; i < dead.size(); i++)
		{
			if (rules.dead.contains(dead.get(i).timesAdded))
			{
				// should be alive in new coordinates
				Point p = dead.get(i);
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

@SuppressWarnings("serial")
class Cell extends Point
{
	int timesAdded;
	
	public Cell(Point location, int timesAdded)
	{
		super(location);
		this.timesAdded = timesAdded;
	}
	
	public void add()
	{
		timesAdded++;
	}
}