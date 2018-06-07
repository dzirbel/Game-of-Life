package grid;

import java.awt.Point;

/**
 * Represents a single, living cell in the Game of Life simulation.
 * This class is simply an implementation of the {@link java.awt.Point} superclass that allows for
 *  a natural ordering so that sorted lists can be maintained.
 */
public class Cell extends Point implements Comparable<Cell>
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new, empty Cell at (0, 0).
     *
     * @see Point#Point()
     */
    public Cell()
    {
        super();
    }

    /**
     * Creates a new Cell at the given coordinates.
     *
     * @param x - the x-coordinate of the Cell
     * @param y - the y-coordinate of the Cell
     * @see Point#Point(int, int)
     */
    public Cell(int x, int y)
    {
        super(x, y);
    }

    /**
     * Creates a new Cell at the same location as the given Cell.
     *
     * @param c - the cell to be copied into this cell
     * @see Point#Point(Point)
     */
    public Cell(Cell c)
    {
        super(c);
    }

    /**
     * Compares this Cell to the given Cell.
     * Cells are ordered based primarily on the x-coordinates and secondarily on y-coordinates.
     * That is, if the x-coordinates are different, a Cell is considered to be "greater than"
     *  another Cell if its x-coordinate is larger than the other Cell's x-coordinate.
     * Similarly, a Cell is considered to be "less than" another Cell if it's x-coordinate is
     *  smaller than the other Cell's x-coordinate.
     * If the x-coordinates are the same, the Cells are compared based on y-coordinates in a
     *  similar manner: if the y-coordinate of one Cell is larger than that of another, it is
     *  larger than the other Cell and if its y-coordinate is smaller, it is smaller.
     * Two cells are equivalent if and only if both the x- and y-coordinates are equal.
     *
     * @param o - the Cell to which to compare
     * @return a comparison, {@code -1} if o is less than this cell,
     *  {@code 1} if o is greater than this cell,
     *  or {@code 0} if o equals this cell
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Cell o)
    {
        if (x < o.x)
        {
            return -1;
        }
        else if (x > o.x)
        {
            return 1;
        }
        else
        {
            if (y < o.y)
            {
                return -1;
            }
            else if (y > o.y)
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }

    /**
     * Determines whether this Cell equals the given object.
     * The two are equal if and only if the given Object is a Cell and its x- and y-coordinates are
     *  both equal to this Cell's x- and y-coordinates.
     *
     * @param o - the object to which to compare
     * @return true if the given object is equal to this Cell, false otherwise
     * @see Point#equals(Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof Cell)
        {
            return ((Cell) o).x == x && ((Cell) o).y == y;
        }
        return false;
    }

    /**
     * Returns a clone of this Cell with the same coordinates.
     * This is equivalent to using the {@link #Cell(Cell)} constructor with this as the parameter.
     *
     * @return a cloned version of this Cell
     * @see java.awt.geom.Point2D#clone()
     * @see Cell#Cell(Cell)
     */
    public Cell clone()
    {
        return new Cell(this);
    }

    /**
     * Returns a String representation of this Cell with the format:
     * <pre>
     * x,y
     * </pre>
     *
     * @return a user-friendly String representation of this Cell
     * @see Point#toString()
     */
    public String toString()
    {
        return x + "," + y;
    }
}
