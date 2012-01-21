package grid;

import java.util.ArrayList;

/**
 * Contains information regarding different options for rules regarding the creation and destruction of cells upon a simulation update.
 * 
 * TODO: load various rule sets from file
 * 
 * @author Dominic
 */
public class Rules
{
	ArrayList<Integer> alive;
	ArrayList<Integer> dead;
	
	/**
	 * The default rules for the simulation originally used by mathematician John Conway.
	 */
	public static final int DEFAULT = 1;
	
	/**
	 * Creates a new Rules object with the given rule set.
	 * 
	 * @param rules - the rule set used to determine the cell count
	 */
	public Rules(int rules)
	{
		alive = alive(rules);
		dead = dead(rules);
	}
	
	/**
	 * Returns a Rule object based on the given rules.
	 * 
	 * @param rules - the rule set used to determine the cell count
	 */
	public static Rules getRules(int rules)
	{
		return new Rules(rules);
	}
	
	/**
	 * Returns a list of the possible numbers of neighboring cells (including diagonal) required for an alive cell to remain alive under the given rule set.
	 * 
	 * @param rules - the rule set used to determine the cell count
	 * @return alive - possible neighbor amounts such that a living cell remains alive, sorted from least to greatest
	 */
	public static ArrayList<Integer> alive(int rules)
	{
		ArrayList<Integer> alive = new ArrayList<Integer>();
		if (rules == DEFAULT)
		{
			alive.add(2);
			alive.add(3);
			return alive;
		}
		return null;
	}
	
	/**
	 * Returns a list of the possible numbers of neighboring cells (including diagonal) required for a dead cell to become alive under the given rule set.
	 * 
	 * @param rules - the rule set used to determine the cell count
	 * @return dead - possible neighbor amounts such that a living cell remains alive, sorted from least to greatest
	 */
	public static ArrayList<Integer> dead(int rules)
	{
		ArrayList<Integer> dead = new ArrayList<Integer>();
		if (rules == DEFAULT)
		{
			dead.add(3);
			return dead;
		}
		return null;
	}
}
