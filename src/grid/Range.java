package grid;

/**
 * Represents an interval, typically of an array.
 * 
 * @author Dominic
 */
public class Range
{
	int start;
	int end;
	
	/**
	 * Cretes a new Range object with the given start and end.
	 * 
	 * @param start - the start of the Range
	 * @param end - the end of the Range
	 */
	public Range(int start, int end)
	{
		this.start = start;
		this.end = end;
	}
}
