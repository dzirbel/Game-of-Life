package main;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JApplet;
import javax.swing.JPanel;

public class AppletOfLife extends JApplet implements Runnable
{
	private Information info;
	
	public static long period = 20;
	private static final long serialVersionUID = 1L;
	
	public Panel panel;
	
	public Thread toolbarThread;
	public Thread gridThread;

	public void init()
	{
		info = new Information();
		info.init(this);
		
		toolbarThread = new Thread(info.toolbar);
		toolbarThread.start();
		gridThread = new Thread(info.grid);
		gridThread.start();
		
		panel = new Panel();
		
		setContentPane(panel);
	}
	
	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException ex) { }
		}
	}
	
	private class Panel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D)g;
			info.grid.draw(g2);
			info.toolbar.draw(g2);
			info.toolbar.selector.draw(g2);
			info.controlBar.draw(g2);
			info.gui.draw(g2);
			info.diagnostics.draw(g2);
		}
	}
}
