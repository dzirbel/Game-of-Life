package pattern;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import image.ImageLoader;
import graphics.AcceleratedImage;
import grid.Grid;

/**
 * Defines a single pattern as a rectangular area of cells.
 * Patterns have a full name, which is typically displayed in a tooltip, and a short name, which
 *  is shown on the screen, where space may be limited.
 * The pattern acts a non-writeable holder for the full name, short name, and pattern array.
 */
public class Pattern implements Comparable<Pattern>
{
    private static AcceleratedImage alive;

    /**
     * Holds the pattern's data.
     * Each cell is a single element of the array; true is customarily used for living cells and
     *  false for dead cells.
     * The pattern should be traversed as follows:
     * <pre>
     * for (int x = 0; x < pattern.length; x++)
     * {
     *     for (int y = 0; y < pattern[x].length; y++)
     *     {
     *         boolean cell = pattern[x][y];
     *     }
     * }
     * </pre>
     * That is, the first array indices are typically the "x" coordinates of the pattern and the
     *  second indices are the "y" coordinates.
     * {@link #getWidth()} and {@link #getHeight()} can also be used to find the width and height
     *  of the pattern, as opposed to {@code pattern.length} and {@code pattern[0].length}.
     */
    public final boolean[][] pattern;

    private static final Color thumbBackground = Color.black;

    /**
     * The full name of this Pattern.
     * This name is typically displayed as a tooltip.
     */
    public final String fullName;
    /**
     * An abbreviated name of this Pattern.
     * This name is typically displayed on the screen, since space may be limited.
     */
    public final String shortName;

    static
    {
        alive = ImageLoader.load("alive");
    }

    /**
     * Creates a new Pattern with the given map and names.
     *
     * @param pattern - the pattern held by this Pattern
     * @param fullName - the full name of the Pattern
     * @param shortName - and abbreviated name for this Pattern
     */
    public Pattern(boolean[][] pattern, String fullName, String shortName)
    {
        this.pattern = pattern;
        this.fullName = fullName;
        this.shortName = shortName;
    }

    /**
     * Creates a new Pattern with the given map and name.
     * The {@link #shortName} and {@link #fullName} are both set to the given name.
     *
     * @param pattern - the pattern held by this Pattern
     * @param name - the name of this Pattern
     */
    public Pattern(boolean[][] pattern, String name)
    {
        this(pattern, name, name);
    }

    /**
     * Gets the width of this Pattern, equal to {@code pattern.length}.
     *
     * @return the width of this Pattern, in cells
     */
    public int getWidth()
    {
        return pattern.length;
    }

    /**
     * Gets the height of this Pattern, equal to {@code pattern[0].length} (or 0 if
     *  {@code pattern.length} is 0).
     *
     * @return the height of this Pattern, in cells.
     */
    public int getHeight()
    {
        if (pattern.length == 0)
        {
            return 0;
        }

        return pattern[0].length;
    }

    /**
     * Generates a thumbnail image of this Pattern with the given size.
     *
     * @param width - the width of the desired image, in pixels
     * @param height - the height of the desired image, in pixels
     * @return an AcceleratedImage of the requested size which visually depicts this Pattern
     */
    public AcceleratedImage generateThumb(int width, int height)
    {
        AcceleratedImage thumb = new AcceleratedImage(width, height);
        Graphics2D g = (Graphics2D) thumb.getContents().getGraphics();

        g.setColor(thumbBackground);
        g.fillRect(0, 0, width, height);

        // the size of a single cell in the thumb
        double cellSize = Math.min((double)width/getWidth(), (double)height/getHeight());
        // scale the alive image
        alive.setScale(cellSize/alive.getWidth(), cellSize/alive.getHeight());
        // compensate for the fact that the given size may not have proportions equal to the
        //  proportions of this pattern
        double xShift = (width - cellSize*getWidth())/2;
        double yShift = (height - cellSize*getHeight())/2;
        g.setColor(Grid.aliveColor);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                if (pattern[x][y])
                {
                    g.fill(new Rectangle2D.Double(x*cellSize + xShift, y*cellSize + yShift,
                            cellSize, cellSize));
                }
            }
        }

        return thumb;
    }

    /**
     * Determines whether this Pattern is equal to the given Object.
     * They are equal if any only if the given Object is a pattern with identical full names, short
     *  names, and patterns.
     *
     * @param o - the object to which to compare this Pattern
     * @return true if this Pattern equals the given Object, false otherwise
     * @see Object#equals(Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof Pattern)
        {
            Pattern p = (Pattern)o;
            return p.fullName.equals(fullName) && p.shortName.equals(shortName) &&
                    p.pattern.equals(pattern);
        }
        return false;
    }

    /**
     * Returns a String representation of this Pattern, with the format:
     * <pre>
     * fullName [shortName]:
     * 10101010101
     * 01010101010
     * 10101010101
     * 01010101010
     * </pre>
     *
     * @return a user-friendly String representation of this Pattern
     * @see Object#toString()
     */
    public String toString()
    {
        String str = fullName + " [" + shortName + "]:\n";

        for (int y = 0; y < getHeight(); y++)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                str += pattern[x][y] ? "1" : "0";
            }
            str += "\n";
        }

        return str;
    }

    /**
     * Compares this Pattern to the given one, based on the full names of each Pattern.
     *
     * @param p - the Pattern to which to compare
     * @return a comparison
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Pattern p)
    {
        return -fullName.compareTo(p.fullName);
    }
}
