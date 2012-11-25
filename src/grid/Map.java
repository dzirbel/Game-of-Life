package grid;

import java.awt.Rectangle;
import java.util.ArrayList;

import utils.ListUtil;

/**
 * Holds the contents of a state of the simulation.
 * That is, a Map contains data regarding the state of a specific configuration, and is able to
 *  determine whether a specific cell is living or dead.
 * This Map implementation keeps an {@link ArrayList} of {@link Cell}s which are currently alive,
 *  which allows it to easily add Cells without expanding the size of the container, as with a
 *  two-dimensional array.
 * Due to the nature of the simulation, the Map is prone to sustained changes in the bounding box
 *  (gliders, etc.) and so such an ArrayList tends to be more efficient.
 * 
 * @author zirbinator
 */
public class Map
{
    private ArrayList<Cell> living;
    
    private int generation;
    
    /**
     * Creates a new, empty Map.
     */
    public Map()
    {
        living = new ArrayList<Cell>();
        generation = 0;
    }
    
    /**
     * Gets the current generation of this Map.
     * The generation is a counter which is incremented each time the Map is updated (a new
     *  generation is simulated) and reset to 0 whether the Map is cleared.
     * 
     * @return the generation of this Map
     */
    public int getGeneration()
    {
        return generation;
    }
    
    /**
     * Sets the cell at the given coordinates to the given state.
     * If the cell at the given location already has the given state, no action is taken.
     * 
     * @param x - the x-coordinate of the cell to alter
     * @param y - the y-coordinate of the cell to alter
     * @param alive - true if the cell should become alive, false otherwise
     */
    public synchronized void setAlive(int x, int y, boolean alive)
    {
        Cell cell = new Cell(x, y);
        if (alive)
        {
            if (!ListUtil.contains(cell, living))
            {
                ListUtil.add(cell, living);
            }
        }
        else
        {
            int index = ListUtil.get(cell, living);
            if (index != -1)
            {
                living.remove(index);
            }
        }
    }
    
    /**
     * Clears the given area of the Map.
     * That is, this method removes all the living cells whose coordinates are within the given
     *  area or on its border.
     * This method is typically faster than calling {@link #setAlive(int, int, boolean)} with false
     *  for each cell within the given area.
     * 
     * @param area - the area of the Map to clear
     */
    public synchronized void clear(Rectangle area)
    {
        for (int i = 0; i < living.size(); i++)
        {
            if (living.get(i).x >= area.x && living.get(i).x < area.x + area.width &&
                    living.get(i).y >= area.y && living.get(i).y < area.y + area.height)
            {
                living.remove(i--);
            }
        }
        ListUtil.sort(living);
    }
    
    /**
     * Creates a rectangular area from the border of the given area.
     * That is, this method sets all the cells with a coordinate equal to one of the sides of the
     *  given area to alive.
     * In particular, the cells within the rectangle are not changed.
     * This method is equivalent to traversing the sides of the area and calling 
     *  {@link #setAlive(int, int, boolean)} with true for each cell on the border.
     * 
     * @param area - the area of the map for which to make a rectangular border
     */
    public synchronized void square(Rectangle area)
    {
        for (int x = area.x; x < area.x + area.width; x++)
        {
            setAlive(x, area.y, true);
            setAlive(x, area.y + area.height - 1, true);
        }
        
        for (int y = area.y + 1; y < area.y + area.height - 1; y++)
        {
            setAlive(area.x, y, true);
            setAlive(area.x + area.width - 1, y, true);
        }
    }
    
    /**
     * Creates an ellipse within the given area.
     * That is, this method sets all cells on the border of the largest ellipse contained in the
     *  given area to true.
     * In particular, the cells within the ellipse and outside of it are not changed.
     * This method is equivalent to traversing the border of the ellipse within the area and
     *  calling {@link #setAlive(int, int, boolean)} with true for each cell on the border.
     * 
     * @param area - the area of the map in which to make an ellipse
     */
    public synchronized void oval(Rectangle area)
    {
        double delta = Math.max(1.0/(area.width*area.height), 0.0001);
        for (double theta = 0; theta < 2*Math.PI; theta += delta)
        {
            setAlive((int)Math.round(area.x + (area.width - 1)/2.0 + 
                            (area.width - 1)*Math.cos(theta)/2),
                    (int)Math.round(area.y + (area.height - 1)/2.0 + 
                            (area.height - 1)*Math.sin(theta)/2), true);
        }
    }
    
    /**
     * Rotates the given area of the map clockwise by 90 degrees.
     * Note that cells outside of the given area will be affected if it is not square.
     * 
     * @param area - the area of the Map to rotate clockwise
     * @return the given area transformed in a 90 degree rotation
     */
    public synchronized Rectangle rotateCW(Rectangle area)
    {
        boolean[][] map = new boolean[area.width][area.height];
        
        for (int x = 0; x < area.width; x++)
        {
            for (int y = 0; y < area.height; y++)
            {
                map[x][y] = isAlive(x + area.x, y + area.y);
                setAlive(x + area.x, y + area.y, false);
            }
        }
        
        int xShift = area.width < area.height && (area.width + area.height) % 2 != 0 ? 0 : -1;
        for (int x = 0; x < area.width; x++)
        {
            for (int y = 0; y < area.height; y++)
            {
                setAlive(area.x - y + (area.width + area.height)/2 + xShift,
                        area.y + x + (area.height - area.width)/2, map[x][y]);
            }
        }
        
        return new Rectangle(area.x - (area.height - area.width)/2,
                area.y - (area.width - area.height)/2,
                area.height, area.width);
    }
    
    /**
     * Rotates the given area of the map counterclockwise by 90 degrees.
     * Note that cells outside of the given area will be affected if it is not square.
     * 
     * @param area - the area of the Map to rotate counterclockwise
     * @return the given area transformed in a 90 degree rotation
     */
    public synchronized Rectangle rotateCCW(Rectangle area)
    {
        boolean[][] map = new boolean[area.width][area.height];
        
        for (int x = 0; x < area.width; x++)
        {
            for (int y = 0; y < area.height; y++)
            {
                map[x][y] = isAlive(x + area.x, y + area.y);
                setAlive(x + area.x, y + area.y, false);
            }
        }
        
        int yShift = area.width < area.height && (area.width + area.height) % 2 != 0 ? 0 : -1;
        for (int x = 0; x < area.width; x++)
        {
            for (int y = 0; y < area.height; y++)
            {
                setAlive(area.x + y + (area.width - area.height)/2,
                        area.y - x + (area.height + area.width)/2 + yShift, map[x][y]);
            }
        }
        
        return new Rectangle(area.x - (area.height - area.width)/2,
                area.y - (area.width - area.height)/2,
                area.height, area.width);
    }
    
    /**
     * Determines whether the cell at the given coordinates is alive.
     * 
     * @param x - the x-coordinate of the cell to check
     * @param y - the y-coordinate of the cell to check
     * @return true if the cell at (x,y) is alive, false otherwise
     */
    public synchronized boolean isAlive(int x, int y)
    {
        return ListUtil.contains(new Cell(x, y), living);
    }
    
    /**
     * Gets a copied list of the cells that are currently alive.
     * 
     * @return a list of cells that are currently alive
     */
    public synchronized ArrayList<Cell> getAlive()
    {
        ArrayList<Cell> alive = new ArrayList<Cell>();
        
        for (int i = 0; i < living.size(); i++)
        {
            alive.add(living.get(i).clone());
        }
        
        return alive;
    }
    
    /**
     * Updates the Map by simulating the next generation and setting the contents of the Map to the
     *  results of the simulation.
     * The generation counter is also incremented.
     */
    public synchronized void update()
    {
        living = Simulation.simulate(this);
        generation++;
    }
    
    /**
     * Clears the Map by removing all the living cells and resetting the generation counter to 0.
     */
    public synchronized void clear()
    {
        living.clear();
        generation = 0;
    }
}
