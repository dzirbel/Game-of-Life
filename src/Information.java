import java.awt.Rectangle;

public class Information 
{
	GameOfLife main;
	Map map;
	Window window;
	Pane pane;
	Rectangle screen;
	Listener listener;
	int generation;
	OperationBar opBar;
	
	public Information(GameOfLife main)
	{
		this.main = main;
		map = main.map;
		window = main.window;
		pane = main.pane;
		screen = main.screen;
		listener = main.listener;
		generation = 0;
	}
}
