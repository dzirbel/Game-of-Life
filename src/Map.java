public class Map implements Runnable
{
	boolean[][] map;
	
	long timeLastGeneration = 0;
	
	Information info;
	int width = 0;
	int height = 0;
	
	public Map(int width, int height, Information info)
	{
		this.width = width;
		this.height = height;
		this.info = info;
		map = new boolean[width][height];
	}
	
	public boolean isAlive(int x, int y)
	{
		try
		{
			return map[x][y];
		}
		catch (IndexOutOfBoundsException e)
		{
			return false;
		}
	}
	
	public void setAlive(int x, int y, boolean alive)
	{
		try
		{
			map[x][y] = alive;
		}
		catch (IndexOutOfBoundsException e){ }
	}
	
	public void run()
	{
		while (true)
		{
			System.out.println();
			if (!info.pane.paused)
			{
				if (System.nanoTime() - timeLastGeneration > info.pane.speedBar.getPeriod() * 1000000)
				{
					timeLastGeneration = System.nanoTime();
					update();
				}
			}
		}
	}
	
	public void clear()
	{
		map = new boolean[width][height];
		info.generation = 0;
	}
	
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
}
