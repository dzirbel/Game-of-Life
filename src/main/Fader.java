package main;

/**
 * A convenience class that can be used to fade an object from opaque to invisible.
 * A float alpha is adjusted between 0 and 1 that represents the alpha value (transparency) of the object.
 * The speed at which the alpha value changes is set in the constructor as alpha units (between 0 and 1) per 10 milliseconds.
 * Thus, a speed of 0.01 is equivalent to the fader changing 0.01 every ten milliseconds, so that it goes from 0 to 1 in one second.
 * A negative speed makes the alpha value go down, making the fader more transparent.
 * 
 * @author Dominic
 */
public class Fader implements Runnable
{
	public boolean running;
	
	public float alpha;
	public float speed;
	
	private long period = 10;
	
	/**
	 * Creates a new Fader with the given alpha and speed values.
	 * The Fader will not begin to adjust the alpha value until start() is called.
	 * 
	 * @param alpha - the initial alpha value, between 0 and 1
	 * @param speed - the amount that the alpha value changes every 10 milliseconds
	 */
	public Fader(float alpha, float speed)
	{
		this.alpha = Math.max(0, Math.min(1, alpha));
		this.speed = speed;
		running = false;
	}
	
	/**
	 * Creates a new Fader with the given speed value.
	 * The alpha value is set to 1 (completely opaque).
	 * The Fader will not being to adjust the alpha value until start() is called.
	 * 
	 * @param speed - the amount that the alpha value changes every 10 milliseconds.
	 */
	public Fader(float speed)
	{
		this.speed = speed;
		alpha = 1;
		running = false;
	}
	
	/**
	 * Runs the Fader, continuously looping adjusting the alpha value and sleeping.
	 * The alpha value is adjusted by the speed float every 10 milliseconds, but is kept between 0 and 1.
	 */
	public void run()
	{
		while (true)
		{
			if (running)
			{
				alpha += speed;
				alpha = Math.max(alpha, 0);
				alpha = Math.min(alpha, 1);
			}
			try
			{
				Thread.sleep(period);
			}
			catch (InterruptedException ex) { }
		}
	}
	
	/**
	 * Sets the running flag to true so that the alpha value will automatically change every 10 milliseconds until stop() is called.
	 */
	public void start()
	{
		running = true;
	}
	
	/**
	 * Sets the running flag to false, stopping the alpha value from changing until start() is called.
	 */
	public void stop()
	{
		running = false;
	}
}
