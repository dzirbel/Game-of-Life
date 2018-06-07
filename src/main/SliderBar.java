package main;

import graphics.AcceleratedImage;
import graphics.Tooltip;
import image.ImageLoader;
import io.Listener;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Represents a slider which can be moved from left to right.
 * The slider has two components: a bar (the background horizontal line or area) and a slider (the
 *  vertical holder of the position of the SliderBar).
 * The slider holds a position value in the interval [-1, 1] which can be adjusted by the user or
 *  the client class.
 */
public class SliderBar
{
    private AcceleratedImage slider;
    private AcceleratedImage bar;

    private boolean dragging;

    private double position;

    private static final int sideBuffer = 39;

    private Point loc;

    private Rectangle sliderBounds;

    private Tooltip tooltip;

    /**
     * Creates a new SliderBar at the given location.
     *
     * @param loc - the top-left coordinate of this SliderBar
     */
    public SliderBar(Point loc)
    {
        setLocation(loc);

        slider = ImageLoader.load("slider");
        bar = ImageLoader.load("slider_bar");

        position = 0;
        tooltip = null;

        Listener.requestNotification(this, "mousePressed",
                Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseReleased",
                Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseDragged",
                Listener.TYPE_MOUSE_DRAGGED, Listener.CODE_BUTTON1);
    }

    /**
     * Sets the tooltip to display when the user hovers over the slider.
     *
     * @param tooltip - the tooltip for this SliderBar, or null for no tooltip
     */
    public void setTooltip(Tooltip tooltip)
    {
        this.tooltip = tooltip;
        if (tooltip != null)
        {
            tooltip.setHoverArea(sliderBounds);
        }
    }

    /**
     * Gets the current position of this SliderBar, a double in the interval [-1, 1] indicating the
     *  position of the slider, where -1 means the slider is all the way to the left, 1 means the
     *  slider is all the way to the right, and 0 means the slider is centered.
     *
     * @return the current position of the SliderBar
     * @see #setPosition(double)
     */
    public double getPosition()
    {
        return position;
    }

    /**
     * Adjusts the position of this SliderBar by the given amount.
     * This is equivalent to the statement
     * <pre>
     * setPosition(getPosition() + change)
     * </pre>
     *
     * @param change - the amount by which to change the position of the SliderBar
     * @see #setPosition(double)
     * @see #getPosition()
     */
    public void adjustPosition(double change)
    {
        setPosition(getPosition() + change);
    }

    /**
     * Sets the current position of this SliderBar to the given position.
     * This will move the slider's location on the screen to the given position.
     *
     * @param position - the new position for this SliderBar, trimmed to the interval [-1, 1]
     * @see #getPosition()
     */
    public void setPosition(double position)
    {
        this.position = Math.max(-1, Math.min(1, position));
        updateSliderBounds();
    }

    /**
     * Moves the entire SliderBar to the given location.
     * The position of the SliderBar (the amount that the slider is moved, see
     *  {@link #getPosition()}) will not be changed.
     *
     * @param loc - the new top-left coordinate for this SliderBar on the screen
     */
    public void setLocation(Point loc)
    {
        this.loc = loc;
        updateSliderBounds();
        dragging = false;
    }

    /**
     * Invoked when the left mouse button is pressed.
     *
     * @param e - the triggering event
     */
    public void mousePressed(MouseEvent e)
    {
        if (sliderBounds.contains(e.getLocationOnScreen()))
        {
            dragging = true;
            if (tooltip != null)
            {
                tooltip.setVisible(true);
            }
        }
    }

    /**
     * Invoked when the left mouse button has been released.
     */
    public void mouseReleased()
    {
        dragging = false;
        if (tooltip != null)
        {
            tooltip.setVisible(false);
        }
    }

    /**
     * Invoked when the left mouse button is being dragged (pressed and moved).
     *
     * @param e - the triggering event
     */
    public void mouseDragged(MouseEvent e)
    {
        if (dragging)
        {
            setPosition((e.getLocationOnScreen().x - loc.x - bar.getWidth()/2.0)/
                    (bar.getWidth() - 2*sideBuffer));
            updateSliderBounds();
        }
    }

    /**
     * Updates the slider bounds (the boundaries of the slider icon) based on the current location
     *  and position.
     * That is, the slider bounds are entirely refreshed.
     */
    private void updateSliderBounds()
    {
        if (loc != null)
        {
            sliderBounds = new Rectangle(
                    (int) (loc.x + bar.getWidth()/2 +
                            position*(bar.getWidth() - 2*sideBuffer) - slider.getWidth()/2),
                    (int) (loc.y + bar.getHeight()/2 - slider.getHeight()/2),
                    slider.getWidth(), slider.getHeight());
            if (tooltip != null)
            {
                tooltip.setHoverArea(sliderBounds);
            }
        }
    }

    /**
     * Draws this SliderBar with the given graphics context.
     *
     * @param g - the graphics context
     */
    public void draw(Graphics2D g)
    {
        bar.draw(loc.x, loc.y, g);
        slider.draw(sliderBounds.x, sliderBounds.y, g);
    }

    public void drawTooltip(Graphics2D g)
    {
        if (tooltip != null)
        {
            tooltip.draw(g);
        }
    }
}
