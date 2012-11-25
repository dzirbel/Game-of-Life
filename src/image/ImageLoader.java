package image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import graphics.AcceleratedImage;

public class ImageLoader
{
    public static AcceleratedImage load(String name)
    {
        try
        {
            return new AcceleratedImage(ImageLoader.class.getResourceAsStream(name + ".png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static AcceleratedImage load(String name, int quality)
    {
        try
        {
            return new AcceleratedImage(ImageLoader.class.getResourceAsStream(name + ".png"),
                    quality);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static BufferedImage loadImage(String name)
    {
        try
        {
            return ImageIO.read(ImageLoader.class.getResourceAsStream(name + ".png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}
