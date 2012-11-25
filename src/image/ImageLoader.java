package image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import graphics.AcceleratedImage;

/**
 * Provides utility functions for loading images.
 * Images are loaded as resources of this class so that they can be easily packaged in a JAR
 *  without the need of external folders or resources.
 * 
 * @author zirbinator
 */
public class ImageLoader
{
    /**
     * Loads the image with the given name.
     * The name may end with an extension (typically .png or .jpg); if it does not, .png will be
     *  appended.
     * 
     * @param name - the name of the image to load
     * @return the loaded image, or null if there was an error loading the image
     * @see AcceleratedImage#AcceleratedImage(java.io.InputStream)
     */
    public static AcceleratedImage load(String name)
    {
        try
        {
            return new AcceleratedImage(ImageLoader.class.getResourceAsStream(getImageName(name)));
        }
        catch (IOException ex)
        {
            System.out.println("I/O Error loading the image with name " + name + ": ");
            ex.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            System.out.println("Could not locate the image resource with name " + name + ": ");
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Loads the image with the given name and quality.
     * The name may end with an extension (typically .png or .jpg); if it does not, .png will be
     *  appended.
     * 
     * @param name - the name of the image to load
     * @param quality - the quality of the loaded image
     * @return the loaded image, or null if there was an error loading the image
     * @see AcceleratedImage#AcceleratedImage(java.io.InputStream, int)
     */
    public static AcceleratedImage load(String name, int quality)
    {
        try
        {
            return new AcceleratedImage(ImageLoader.class.getResourceAsStream(getImageName(name)),
                    quality);
        }
        catch (IOException ex)
        {
            System.out.println("I/O Error loading the image with name " + name + ": ");
            ex.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            System.out.println("Could not locate the image resource with name " + name + ": ");
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Loads the image with the given name.
     * The name may end with an extension (typically .png or .jpg); if it does not, .png will be
     *  appended.
     * 
     * @param name - the name of the image to load
     * @return the loaded image, or null if there was an error loading the image
     * @see ImageIO#read(java.io.InputStream)
     */
    public static BufferedImage loadImage(String name)
    {
        try
        {
            return ImageIO.read(ImageLoader.class.getResourceAsStream(getImageName(name)));
        }
        catch (IOException ex)
        {
            System.out.println("I/O Error loading the image with name " + name + ": ");
            ex.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            System.out.println("Could not locate the image resource with name " + name + ": ");
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Converts the given name to an appropriate image name.
     * That is, a standard image file extension is appended to the given name if it has no such
     *  extension.
     * 
     * @param name - the name for which to find an appropriate image name
     * @return the potential name for the image to be loaded as a resource of this class
     */
    private static String getImageName(String name)
    {
        if (name.endsWith(".png") || name.endsWith(".jpg"))
        {
            return name;
        }
        return name + ".png";
    }
}
