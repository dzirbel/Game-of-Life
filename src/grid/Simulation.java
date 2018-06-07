package grid;

import java.util.ArrayList;

import utils.ListUtil;

/**
 * Handles the simulation of {@link Map}s.
 */
public class Simulation
{
    /**
     * Simulates the next generation for the given Map.
     *
     * @param map - the Map for which to simulate the next generation
     * @return a (sorted) list of the cells in the next generation of the given Map
     */
    public static ArrayList<Cell> simulate(Map map)
    {
        // cells that were alive in the last generation
        ArrayList<Cell> alive = map.getAlive();
        // a list of all the cells that are being considered for life in the next generation,
        // parallel with "neighbors"
        ArrayList<Cell> cells = new ArrayList<Cell>();
        // how many neighbors each cell being considered for life in the next generation has,
        // parallel with "cells"
        ArrayList<Integer> neighbors = new ArrayList<Integer>();

        // Pass I:
        //  Go through all living cells and take note of the neighbors of each cell.
        //  Keep a list of all the cells that are neighbors of living cells (cells ArrayList) and
        //   the number of times that this cell has been "noticed" as a neighbor
        //   (neighbors ArrayList).
        // At the end of Pass I, the "cells" and "neighbors" lists will contain all the cells that
        //  should be considered for life and how many neighbors each has.
        for (int i = 0; i < alive.size(); i++)
        {
            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++)
                {
                    if (!(x == 0 && y == 0))
                    {
                        Cell curr = new Cell(alive.get(i).x + x, alive.get(i).y + y);
                        int index = ListUtil.get(curr, cells);
                        if (index == -1)        // curr has not been added to cells
                        {
                            // add curr to cells and give it 1 neighbor
                            int ai = ListUtil.getAddIndex(curr, cells);
                            cells.add(ai, curr);
                            neighbors.add(ai, 1);
                        }
                        else
                            // curr has been added to cells
                        {
                            // increment the number of neighbors that curr has
                            neighbors.set(index, neighbors.get(index) + 1);
                        }
                    }
                }
            }
        }

        // Pass 2:
        // Go through all the cells that have been counted as neighbors for and check if they
        //  be alive in the next generation (which depends on whether they were alive in the last
        //  generation).
        // If they will be alive, keep them in the "cells" list, otherwise remove them.
        // At the end of Pass 2, "cells" will contain all the cells that should be alive in the
        //  next generation.
        for (int i = 0; i < cells.size(); i++)
        {
            if (ListUtil.contains(cells.get(i), alive))
            {
                if (neighbors.get(i) != 2 && neighbors.get(i) != 3)
                {
                    cells.remove(i);
                    neighbors.remove(i);
                    i--;
                }
            }
            else
            {
                if (neighbors.get(i) != 3)
                {
                    cells.remove(i);
                    neighbors.remove(i);
                    i--;
                }
            }
        }

        return cells;
    }
}
