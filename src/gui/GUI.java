package gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import main.Information;

public class GUI
{
	ArrayList<View> views;
	
	Information info;
	
	public GUI(Information info)
	{
		this.info = info;
		views = new ArrayList<View>();
		
		//views.add(new PatternView(View.LOCATION_TAB_TOP, info));
	}
	
	public boolean consumed(MouseEvent e)
	{
		for (int i = 0; i < views.size(); i++)
		{
			if (views.get(i).consumed(e))
			{
				return true;
			}
		}
		return false;
	}
	
	public void draw(Graphics2D g)
	{
		for (int i = 0; i < views.size(); i++)
		{
			views.get(i).draw(g);
		}
	}
}
