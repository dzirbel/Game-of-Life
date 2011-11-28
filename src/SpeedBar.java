import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpeedBar
{
	BufferedImage slider = null;
	BufferedImage sliderBar = null;
	
	Information info;
	int minSliderX;
	int maxSliderX;
	int sliderX;
	int speed;
	int minSpeed = 0;
	int maxSpeed = 100;
	
	long minPeriod = 500;
	long maxPeriod = 0;
	
	Rectangle bounds;
	Rectangle sliderBounds;
	
	public SpeedBar(Information info)
	{
		try
		{
			slider = ImageIO.read(new File("images/slider.png"));
			sliderBar = ImageIO.read(new File("images/sliderBar.png"));
		}
		catch(IOException e) { System.out.println("speedbar error"); }
		this.info = info;
		bounds = new Rectangle(info.pane.x + 280, info.pane.y + 45, 100, 40);
		minSliderX = 10;
		maxSliderX = bounds.width - 10 - slider.getWidth();
		sliderX = (minSliderX + maxSliderX)/2;
		setSliderBounds(bounds.x + sliderX, false);
	}
	
	public void setSliderBounds(int x, boolean movingPane)
	{
		sliderBounds = new Rectangle(x, bounds.y + bounds.height/2 - slider.getHeight()/2, slider.getWidth(), slider.getHeight());
		if (!movingPane)
		{
			sliderX = sliderBounds.x - bounds.x;
		}
	}
	
	public int setSpeed()
	{
		speed = sliderX - minSliderX;
		speed *= maxSpeed - minSpeed;
		speed /= maxSliderX - minSliderX;
		return speed;
	}
	
	public long getPeriod()
	{
		//System.out.println("sliderX: " + sliderX);
		setSpeed();
		//System.out.println("speed: " + speed);
		long period = minPeriod + speed*((maxPeriod - minPeriod)/(maxSpeed - minSpeed));
		//System.out.println("period: " + period);
		return period;
	}
	
	public void drawToImage(Graphics2D g)
	{
		g.drawImage(sliderBar, bounds.x - info.pane.x, bounds.y - info.pane.y, bounds.width, bounds.height, null);
		g.drawImage(slider, sliderBounds.x - info.pane.x, sliderBounds.y - info.pane.y, sliderBounds.width, sliderBounds.height, null);
	}
}
