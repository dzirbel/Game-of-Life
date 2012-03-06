package gui;

import graphics.AcceleratedImage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.regex.Pattern;

import main.Information;

import pattern.PatternFolder;

public class PatternView extends View
{
	@SuppressWarnings("unused")
	private ArrayList<PatternFolder> folders;
	
	@SuppressWarnings("unused")
	private static int TOP_BUFFER = 30;					// distance from the top of the bounding box to the top of the folders
	@SuppressWarnings("unused")
	private static int BOTTOM_BUFFER = 20;				// distance from the bottom of the bounding box to the bottom of the folders
	@SuppressWarnings("unused")
	private static int SIDE_BUFFER = 25;				// distance between folders and other folders as well as the sides of the bounding box
	
	public Pattern selected;
	
	@SuppressWarnings("unused")
	private static String patternFolder = "patterns/";	// location of patterns
	
	public PatternView(int location, Information info)
	{
		super(location, 250, 100, info);
		icon = new AcceleratedImage("images/patternIcon.png");
	}
	
	public void update()
	{
		super.update();
	}
	
	public void drawContents(Graphics2D g)
	{
		g.setColor(Color.green);
		g.fillRect(0, 0, bounds.width, bounds.height);
	}
}
