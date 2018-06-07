package main;

import graphics.AcceleratedImage;
import image.ImageLoader;
import io.Listener;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Represents a highlight drawn behind an icon when the user hovers over it with the mouse.
 * A RollOver automatically fades in when the user moves the cursor over the icon and fades out
 *  when the user moves the cursor off of the icon.
 * Additionally, it can be "splashed" - faded in and out quickly, as when the icon is activated
 *  without the user of the mouse (for example, if a key is pressed that does the same action as
 *  clicking the icon).
 */
public class RollOver implements Runnable
{
    private AcceleratedImage selection;

    private float alpha;

    private int buffer;

    private static final long period = 25;
    /**
     * The time to completely fade in or out (i.e. from alpha of 0 to 1) in milliseconds.
     */
    private static final long fadeTime = 350;

    private Rectangle bounds;

    private SplashState splashing;

    /**
     * Creates a new RollOver with the given boundaries.
     *
     * @param bounds - the area of the screen which activates the RollOver when it is hovered over
     *  by the cursor
     */
    public RollOver(Rectangle bounds, int buffer)
    {
        this.buffer = buffer;
        selection = ImageLoader.load("rollover");
        alpha = 0;
        splashing = SplashState.NO_SPLASH;

        setBounds(bounds);

        new Thread(this).start();
    }

    /**
     * Gets the boundaries of this RollOver: the area of the screen which activates the RollOver
     *  when it is hovered over by the cursor.
     *
     * @return a cloned version of the boundaries of this RollOver
     */
    public Rectangle getBounds()
    {
        return (Rectangle) bounds.clone();
    }

    /**
     * Sets the boundaries of this RollOver: the area of the screen which activates the RollOver
     *  when it is hovered over by the cursor.
     *
     * @param bounds - the new boundaries for this RollOver
     */
    public void setBounds(Rectangle bounds)
    {
        this.bounds = new Rectangle(bounds.x - buffer, bounds.y - buffer,
                bounds.width + 2*buffer, bounds.height + 2*buffer);
        if (bounds != null)
        {
            selection.setScale((double)this.bounds.width/selection.getWidth(),
                    (double)this.bounds.height/selection.getHeight());
        }
    }

    /**
     * Runs this RollOver in its own Thread.
     * The RollOver continually updates its transparency based on the splashing state and mouse
     *  position.
     *
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime();
        float elapsed;
        while (true)
        {
            elapsed = (System.nanoTime() - lastUpdate) / 1000000;
            if (splashing == SplashState.NO_SPLASH)
            {
                if (bounds.contains(Listener.getMouse()))
                {
                    alpha = Math.min(alpha + elapsed / fadeTime, 1f);
                }
                else
                {
                    alpha = Math.max(alpha - elapsed / fadeTime, 0f);
                }
            }
            else if (splashing == SplashState.SPLASH_IN)
            {
                alpha += elapsed / fadeTime;
                if (alpha >= 1)
                {
                    alpha = 1;
                    splashing = SplashState.SPLASH_OUT;
                }
            }
            else if (splashing == SplashState.SPLASH_OUT)
            {
                alpha -= elapsed / fadeTime;
                if (alpha <= 0)
                {
                    alpha = 0;
                    splashing = SplashState.NO_SPLASH;
                }
            }

            lastUpdate = System.nanoTime();
            try
            {
                Thread.sleep(period);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    /**
     * Splashes this RollOver, causing it to fade entirely in and then entirely out, ignoring the
     *  cursor.
     */
    public void splash()
    {
        splashing = SplashState.SPLASH_IN;
    }

    /**
     * Draws this RollOver onto the given graphics context at the position of its boundaries.
     *
     * @param g - the graphics context
     */
    public void draw(Graphics2D g)
    {
        if (alpha > 0)
        {
            selection.setTransparency(alpha);
            selection.draw(bounds.x, bounds.y, g);
        }
    }

    /**
     * Defines the splashing state of a RollOver.
     */
    private enum SplashState
    {
        /**
         * This RollOver is not currently splashing: it should respond to the cursor's position.
         */
        NO_SPLASH,
        /**
         * This RollOver is currently splashing in: it should increase its transparency to 1 (fade
         *  in) without regarding the position of the cursor.
         */
        SPLASH_IN,
        /**
         * This RollOver is currently splashing out: it should decrease its transparency to 0 (fade
         *  out) without regarding the position of the cursor.
         */
        SPLASH_OUT;
    }
}
