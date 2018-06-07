package pattern;

import graphics.AcceleratedImage;
import graphics.ButtonListener;

import image.ImageLoader;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import main.Toolbar;

/**
 * Represents an "extension" to an enclosing {@link Toolbar} that displays patterns and allows the
 *  user to pick them and place them on the grid.
 * A PatternSelector accomplishes this by holding and managing a list of {@link PatternSelector}s.
 */
public class PatternSelector implements Runnable
{
    private AcceleratedImage cap;
    private ArrayList<PatternFolder> folders;

    /**
     * The button to press to make the PatternSelector slide when it is at the "in" state.
     */
    private ButtonListener inSlideButton;
    /**
     * The button to press to make the PatternSelector slide when it is at the "out" state.
     */
    private ButtonListener outSlideButton;

    private static final Color fadeColor = new Color(137, 137, 137);

    private double slidePos;
    private double fadePos;
    private double width;
    private double minSlidePos;

    private int maxWidth;
    /**
     * The distance from the sides of the pattern area to the first and last pattern folders.
     */
    private static final int sideBuffer = 5;
    /**
     * The distance between pattern folders.
     */
    private static final int buffer = 10;
    private static final int maxFadeHeight = 75;
    /**
     * The distance from the bottom of the toolbar to the bottom of each PatternFolder, in pixels.
     */
    private static final int folderBottomBuffer = 40;

    private static final long slideTime = 125;
    private static final long fadeTime = 75;

    private SelectorState state;
    private static final String patternsFile = "patterns.txt";

    private Toolbar toolbar;

    /**
     * Creates a new PatternSelector enclosed by the given Toolbar.
     *
     * @param toolbar - the Toolbar on which to attach this PatternSelector
     */
    public PatternSelector(Toolbar toolbar)
    {
        this.toolbar = toolbar;

        try
        {
            loadPatterns();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        slidePos = minSlidePos;
        fadePos = 0;
        width = minSlidePos*maxWidth;
        state = SelectorState.IN;

        cap = ImageLoader.load("selector_cap");

        try
        {
            inSlideButton = new ButtonListener(null, "slideOut", this);
            outSlideButton = new ButtonListener(null, "slideIn", this);
        }
        catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }

        new Thread(this).start();
    }

    /**
     * Runs this PatternSelector in its own Thread.
     *
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime(), elapsed;
        while (true)
        {
            elapsed = (System.nanoTime() - lastUpdate)/1000000;
            onResize();
            if (state == SelectorState.MOVING_OUT)
            {
                slidePos += (double)elapsed/slideTime;

                if (slidePos >= 1)
                {
                    slidePos = 1;
                    state = SelectorState.FADING_IN;

                    for (int i = 0; i < folders.size(); i++)
                    {
                        folders.get(i).setOn(true);
                    }
                }

                width = slidePos*maxWidth;
            }
            else if (state == SelectorState.MOVING_IN)
            {
                slidePos -= (double)elapsed/slideTime;

                if (slidePos <= minSlidePos)
                {
                    slidePos = minSlidePos;
                    state = SelectorState.IN;
                }

                width = slidePos*maxWidth;
            }
            else if (state == SelectorState.FADING_IN)
            {
                fadePos += (double)elapsed/fadeTime;

                if (fadePos >= 1)
                {
                    fadePos = 1;
                    state = SelectorState.OUT;
                }
            }
            else if (state == SelectorState.FADING_OUT)
            {
                fadePos -= (double)elapsed/fadeTime;

                if (fadePos <= 0)
                {
                    fadePos = 0;
                    state = SelectorState.MOVING_IN;

                    for (int i = 0; i < folders.size(); i++)
                    {
                        folders.get(i).setOn(false);
                    }
                }
            }

            Rectangle toolbarBounds = toolbar.getBounds();
            if (state == SelectorState.IN)
            {
                inSlideButton.setOn(true);
                inSlideButton.setButton(new Rectangle(
                        toolbarBounds.x - (int)width - cap.getWidth(), toolbarBounds.y,
                        cap.getWidth(), cap.getHeight()));
            }
            else
            {
                inSlideButton.setOn(false);
            }

            if (state == SelectorState.OUT)
            {
                outSlideButton.setOn(true);
                outSlideButton.setButton(new Rectangle(
                        toolbarBounds.x - (int)width - cap.getWidth(), toolbarBounds.y,
                        cap.getWidth(), cap.getHeight()));
            }
            else
            {
                outSlideButton.setOn(false);
            }

            lastUpdate = System.nanoTime();
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Loads the patterns from the patterns file.
     *
     * @throws IOException
     */
    private void loadPatterns() throws IOException
    {
        folders = new ArrayList<PatternFolder>();

        BufferedReader in = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(patternsFile)));

        String line;
        while (true)
        {
            String name = in.readLine();

            if (name == null)
            {
                break;
            }

            ArrayList<Pattern> patterns = new ArrayList<Pattern>();

            while (true)
            {
                String fullName = in.readLine();

                if (fullName.startsWith("====="))
                {
                    break;
                }
                String shortName = in.readLine();

                ArrayList<String> lines = new ArrayList<String>();
                int maxLength = 0;
                while (true)
                {
                    line = in.readLine();
                    if (line.startsWith("###"))
                    {
                        break;
                    }

                    maxLength = Math.max(maxLength, line.length());
                    lines.add(line);
                }

                boolean[][] pattern = new boolean[maxLength][lines.size()];

                for (int i = 0; i < lines.size(); i++)
                {
                    for (int j = 0; j < lines.get(i).length(); j++)
                    {
                        pattern[j][i] = lines.get(i).charAt(j) == '1' ||
                                lines.get(i).charAt(j) == 't';
                    }
                }

                patterns.add(new Pattern(pattern, fullName, shortName));
            }

            folders.add(new PatternFolder(this, name, patterns));
        }

        in.close();

        maxWidth = 2*sideBuffer;
        for (int i = 0; i < folders.size(); i++)
        {
            maxWidth += folders.get(i).getSize().width;
            if (i != folders.size() - 1)
            {
                maxWidth += buffer;
            }
        }
        minSlidePos = -25.0/maxWidth;
        onResize();
    }

    /**
     * This method should be invoked whenever a change is made forcing PatternSelector to resize or
     *  move its components.
     * For example, this method should be called whenever the enclosing Toolbar moves and whenever
     *  the internal PatternFolders change their size.
     */
    public void onResize()
    {
        Rectangle toolbarBounds = toolbar.getBounds();
        int x = (int) (toolbarBounds.x - width + sideBuffer);
        for (int i = 0; i < folders.size(); i++)
        {
            folders.get(i).setLocation(new Point(x,
                    toolbarBounds.y + toolbarBounds.height - folderBottomBuffer -
                    folders.get(i).getSize().height));
            x += folders.get(i).getSize().width + buffer;
        }
    }

    /**
     * Determines whether the given event should be consumed by the PatternSelector - and thus by
     *  the enclosing Toolbar.
     *
     * @param e - the triggering event
     * @return true if the PatternSelector should consume the event and it should not be used by
     *  any other interface components, false otherwise
     */
    public boolean consumed(MouseEvent e)
    {
        if (outSlideButton.isOn() && outSlideButton.getButton().contains(e.getPoint()))
        {
            return true;
        }
        if (inSlideButton.isOn() && inSlideButton.getButton().contains(e.getPoint()))
        {
            return true;
        }

        Rectangle toolbarBounds = toolbar.getBounds();
        int height = getHeight();
        return new Rectangle((int) (toolbarBounds.x - width - cap.getWidth()),
                toolbarBounds.y + toolbarBounds.height - height,
                (int) (cap.getWidth() + width), height).contains(e.getLocationOnScreen());
    }

    /**
     * Slides the PatternSelector out.
     */
    public void slideOut()
    {
        state = SelectorState.MOVING_OUT;
    }

    /**
     * Slides the PatternSelector in.
     */
    public void slideIn()
    {
        state = SelectorState.FADING_OUT;
    }

    /**
     * Gets the height of this PatternSelector, which is determined primarily by the maximum height
     *  of each PatternFolder.
     *
     * @return the height of this PatternSelector
     */
    private int getHeight()
    {
        int height = toolbar.getBounds().height;
        for (int i = 0; i < folders.size(); i++)
        {
            height = Math.max(height, 40 + folders.get(i).getSize().height);
        }
        return height;
    }

    /**
     * Draws the PatternSelector with the given alpha and graphics context.
     * The drawing is done relative to the top-left corner of the screen.
     *
     * @param alpha - the transparency with which the PatternSelector should be drawn
     * @param g - the graphics context
     */
    public void draw(float alpha, Graphics2D g)
    {
        Rectangle toolbarBounds = toolbar.getBounds();

        AcceleratedImage img = new AcceleratedImage(
                (int)(width + cap.getWidth() + toolbar.getArc()/2), getHeight());
        Graphics2D gImg = (Graphics2D) img.getContents().getGraphics();

        if (slidePos > 0)
        {
            gImg.setColor(Toolbar.backgroundColor);
            gImg.fillRect(cap.getWidth()/2, img.getHeight() - 25, img.getWidth(), 10);

            gImg.setColor(Toolbar.borderColor);
            gImg.setStroke(new BasicStroke(3));
            gImg.drawRect(cap.getWidth()/2, img.getHeight() - 25, img.getWidth(), 10);

            if (fadePos > 0)
            {
                float[] fractions = { 0, 1 };
                Color[] colors = { fadeColor, new Color(0, 0, 0, 0) };
                gImg.setPaint(new LinearGradientPaint(
                        new Point2D.Double(0, img.getHeight() - 25),
                        new Point2D.Double(0, img.getHeight() - 25 - fadePos*maxFadeHeight),
                        fractions, colors));
                gImg.fillRect(cap.getWidth()/2, img.getHeight() - 25 - maxFadeHeight,
                        img.getWidth(), maxFadeHeight);
            }
        }

        cap.draw(0, img.getHeight() - cap.getHeight(), gImg);

        img.setTransparency(alpha);
        img.draw(toolbarBounds.x - img.getWidth() + toolbar.getArc()/2,
                toolbarBounds.y - img.getHeight() + toolbarBounds.height, g);

        if (fadePos > 0)
        {
            for (int i = 0; i < folders.size(); i++)
            {
                folders.get(i).draw((float)(fadePos*alpha), g);
            }
        }
    }

    /**
     * Defines the possible states of the PatternSelector.
     */
    private enum SelectorState
    {
        IN,
        MOVING_OUT,
        FADING_IN,
        OUT,
        FADING_OUT,
        MOVING_IN;
    }
}
