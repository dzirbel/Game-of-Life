package grid;

import java.awt.Point;
import java.util.ArrayList;

/**
 * A collection of useful static methods for lists, specifically sorted ArrayLists.
 * 
 * @author Dominic
 */
public class ListUtils
{	
	/**
	 * Signifies that the first value given is greater than the second.
	 */
	public static final int GREATER = 1;
	/**
	 * Signifies that the fist value given is less than the second.
	 */
	public static final int EQUAL = 0;
	/**
	 * Signifies that the first value given is equal to the second.
	 */
	public static final int LESS = -1;
	
	/**
	 * Gets the index of the given value within the given sorted list.
	 * If the value occurs multiple times within the list, any of the possible indexes may be returned.
	 * To find the index of the given value, a binary search is used.
	 * A binary search uses high and low boundaries to narrow down the space in which a value can be located within a sorted list.
	 * At each iteration, the midpoint between the boundaries is found and compared with the value to be found.
	 * Depending on this comparison, the boundaries are adjusted and the search is repeated.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @return index - the index of the value in the list, -1 if the value does not occur in the list
	 */
	public static int binary(ArrayList<Integer> list, int value)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		while (true)
		{
			if (high < low)
			{
				return -1;				// the value is not within the list, return -1
			}
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			if (list.get(mid) < value)
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
			}
			else if (list.get(mid) > value)
			{
				high = mid - 1;			// the higher bound is too high, adjust to below the point checked
			}
			else
			{
				return mid;				// the correct value is found
			}
		}
	}
	
	/**
	 * Gets the index of the given point within the given sorted list.
	 * If the point occurs multiple times within the list, any of the possible indexes may be returned.
	 * To find the index of the given point, a binary search is used.
	 * A binary search uses high and low boundaries to narrow down the space in which a value can be located within a sorted list.
	 * At each iteration, the midpoint between the boundaries is found and compared with the point to be found.
	 * Depending on this comparison, the boundaries are adjusted and the search is repeated.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param point - the point for which to search in the list
	 * @return index - the index of the value in the list, -1 if the value does not occur in the list
	 */
	public static int binary(ArrayList<Point> list, Point point)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		while (true)
		{
			if (high < low)
			{
				return -1;				// the value is not within the list, return -1
			}
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			if (isLess(list.get(mid), point))
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
			}
			else if (isGreater(list.get(mid), point))
			{
				high = mid - 1;			// the higher bound is too high, adjust to below the point checked
			}
			else
			{
				return mid;				// the correct value is found
			}
		}
	}
	
	/**
	 * Gets the index of the given point within the given sorted list.
	 * If the point occurs multiple times within the list, any of the possible indexes may be returned.
	 * To find the index of the given point, a binary search is used.
	 * A binary search uses high and low boundaries to narrow down the space in which a value can be located within a sorted list.
	 * At each iteration, the midpoint between the boundaries is found and compared with the point to be found.
	 * Depending on this comparison, the boundaries are adjusted and the search is repeated.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param point - the point for which to search in the list
	 * @return index - the index of the value in the list, -1 if the value does not occur in the list
	 */
	public static int binaryCell(ArrayList<Cell> list, Point point)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		while (true)
		{
			if (high < low)
			{
				return -1;				// the value is not within the list, return -1
			}
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			if (isLess(list.get(mid), point))
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
			}
			else if (isGreater(list.get(mid), point))
			{
				high = mid - 1;			// the higher bound is too high, adjust to below the point checked
			}
			else
			{
				return mid;				// the correct value is found
			}
		}
	}
	
	/**
	 * Finds first (lowest) index of the given value within the given sorted list.
	 * The index representing the value one past the next lower number is returned,
	 *  that is, the smallest index is returned among those which represent the given value.
	 * A binary search is used to find the first occurrence of the value between the given ranges.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @param start - the lower bound (inclusive) of the sublist to be searched
	 * @param end - the higher bound (inclusive) of the sublist to be searched
	 * @return index - the lowest index within the range of the given value, -1 if the value does not occur in the list
	 */
	public static int binaryMin(ArrayList<Integer> list, int value, int start, int end)
	{
		int low = start;				// the lower bound of the remaining possible space in which the value could lie
		int high = end;					// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		while (true)
		{
			if (high < low)
			{
				return -1;				// the value is not within the list, return -1
			}
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			if (mid > 0)
			{
				if (list.get(mid) == value && list.get(mid - 1) < value)
				{
					return mid;			// the mid value is the lowest value (the next lowest is less than value)
				}
			}
			else
			{
				if (list.get(mid) == value)
				{
					return mid;			// the mid value is the lowest value (the lowest in the list, in fact)
				}
			}
			if (list.get(mid) < value)
			{
				low = mid + 1;			// mid is too low, adjust the low bound
			}
			else
			{
				high = mid - 1;			// mid is too high, adjust the high bound (includes if list.get(mid) == value but it is not the lowest)
			}
		}
	}
	
	/**
	 * Finds first (lowest) index of the given value within the given sorted list.
	 * The index representing the value one past the next lower number is returned,
	 *  that is, the smallest index is returned among those which represent the given value.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @return index - the lowest index within the range of the given value, -1 if the value does not occur in the list
	 */
	public static int binaryMin(ArrayList<Integer> list, int value)
	{
		return binaryMin(list, value, 0, list.size() - 1);
	}
	
	/**
	 * Finds last (highest) index of the given value within the given sorted list.
	 * The index representing the value one before the next higher number is returned,
	 *  that is, the highest index is returned among those which represent the given value.
	 * A binary search is used to find the last occurrence of the value between the given ranges.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @param start - the lower bound (inclusive) of the sublist to be searched
	 * @param end - the higher bound (inclusive) of the sublist to be searched
	 * @return index - the highest index within the range of the given value, -1 if the value does not occur in the list
	 */
	public static int binaryMax(ArrayList<Integer> list, int value, int start, int end)
	{
		int low = start;				// the lower bound of the remaining possible space in which the value could lie
		int high = end;					// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		while (true)
		{
			if (high < low)
			{
				return -1;				// the value is not within the list, return -1
			}
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			if (mid + 1 < list.size())
			{
				if (list.get(mid) == value && list.get(mid + 1) > value)
				{
					return mid;			// the mid value is the highest value (the next higher is greater than value)
				}
			}
			else
			{
				if (list.get(mid) == value)
				{
					return mid;			// the mid value is the highest value (the highest in the list, in fact)
				}
			}
			if (list.get(mid) <= value)
			{
				low = mid + 1;			// mid is too low, adjust the low bound (includes if list.get(mid) == value but it is not the highest)
			}
			else
			{
				high = mid - 1;			// mid is too high, adjust the high bound
			}
		}
	}
	
	/**
	 * Finds last (highest) index of the given value within the given sorted list.
	 * The index representing the value one before the next higher number is returned,
	 *  that is, the highest index is returned among those which represent the given value.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @return index - the highest index within the range of the given value, -1 if the value does not occur in the list
	 */
	public static int binaryMax(ArrayList<Integer> list, int value)
	{
		return binaryMax(list, value, 0, list.size() - 1);
	}
	
	/**
	 * Returns the Range representing the interval of the given value within the given sorted list.
	 * The range runs from the first index of the given value to the last index, both inclusive.
	 * The list will only be searched between the given starting and ending values.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @param start - the lower bound (inclusive) of the sublist to be searched
	 * @param end - the higher bound (inclusive) of the sublist to be searched
	 * @return range - the range of the value within the list, null if the value is not in the list
	 */
	public static Range range(ArrayList<Integer> list, int value, int start, int end)
	{
		int min = binaryMin(list, value, start, end);
		int max = binaryMax(list, value, start, end);
		if (min == -1 || max == -1)
		{
			return null;
		}
		return new Range(min, max);
	}
	
	/**
	 * Returns the Range representing the interval of the given value within the given sorted list.
	 * The range runs from the first index of the given value to the last index, both inclusive.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @return range - the range of the value within the list, null if the value is not in the list
	 */
	public static Range range(ArrayList<Integer> list, int value)
	{
		return range(list, value, 0, list.size() - 1);
	}
	
	/**
	 * Gets the index of the given value within the given sorted list.
	 * If the value occurs multiple times within the list, any of the possible indexes may be returned.
	 * To find the index of the given value, an interpolation search is used.
	 * This search is similar to the binary search, in that it portions off a section of the given list,
	 *  which must be sorted in ascending order, and refines the search in between those bounds.
	 * The search assumes that the list follows a generally linear pattern in order to optimize the results;
	 *  other trends (for example, exponentially increasing values) will return the correct index
	 *   but the time spent searching will increase.
	 * In general, if the data is approximately linear, the search time approaches O(log log N))
	 *  as the list becomes more linear.
	 * 
	 * @param list - the list to be searched, sorted in increasing order
	 * @param value - the value for which to search in the list
	 * @return index - the index of value within the list, -1 if the value does not occur in the list
	 */
	public static int interpolate(ArrayList<Integer> list, int value)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, between low and high (within the bounds)
		
		while (list.get(low) <= value && list.get(high) >= value)
		{
			// find the point to be checked
			// this point is scaled so as to be in a near-optimal position given a linear distribution of the values within the list
			// the point is located between low and high so as to be the best possible guess of the location of the value
			// a graphical explanation for the method used to find the point is as follows:
			//   the list is assumed to have a linear or near-linear distribution between high and low
			//   note that if it does not, the algorithm will nevertheless work, but will be less than optimal
			//   first, the slope of the list between low and high is approximated with the following formula:
			//      slope = rise/run = (list.get(high) - list.get(low))/(high - low)
			//   then, to find the estimated location of the value along the line with this slope and the y-intercept of list.get(low),
			//   the following formula is used:
			//      y = mx + b -> x = (y - b)/m = (value_to_be_found - list.get(low))*(1/slope)
			//   this gives the formula used to find the midpoint, because the index of an entry is analogous to the x-axis, once it is shifted by the low point
			mid = low + (value - list.get(low)) * (high - low)/(list.get(high) - list.get(low));
			if (list.get(mid) < value)
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
			}
			else if (list.get(mid) > value)
			{
				high = mid - 1;			// the higher bound is too high, adjust to below the point checked
			}
			else
			{
				return mid;				// the correct value is found
			}
		}
		
		if (list.get(low) == value)
		{
			return low;					// the correct value is found
		}
		return -1;						// the value is not in the given list, return -1
	}
	
	/**
	 * Returns the index at which the given Point should be added to the given sorted list of Points.
	 * A binary search is used to find the index at which the given Point fits into the sorted list.
	 * 
	 * @param list - the list to which the Point is to be added, sorted
	 * @param point - the Point to add to the given list
	 * @return index - the index at which the given Point should be added
	 */
	public static int getAddIndex(ArrayList<Point> list, Point point)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		if (list.size() == 0)
		{
			return 0;
		}
		
		while (true)
		{
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			
			if (isLess(list.get(mid), point))
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
				if (mid < list.size() - 1)
				{
					if (isGreater(list.get(mid + 1), point))
					{
						return mid + 1;	// mid < point, mid + 1 > point -> mid + 1 is the index at which to add
					}
				}
				else
				{
					return mid + 1;		// mid is the last index, everything else is less -> add at last index + 1
				}
			}
			else if (isGreater(list.get(mid), point))
			{
				high = mid;			// the higher bound is too high, adjust to below the point checked
				if (mid > 0)
				{
					if (isLess(list.get(mid - 1), point))
					{
						return mid;		// mid > point, mid - 1 < point -> mid is the index at which to add
					}
				}
				else
				{
					return mid;			// mid is the first index, everything else is greater -> add at first index
				}
			}
			else
			{
				return -1;				// point is already in the list
			}
		}
	}
	
	/**
	 * Returns the index at which the given Point should be added to the given sorted list of Points.
	 * A binary search is used to find the index at which the given Point fits into the sorted list.
	 * 
	 * @param list - the list to which the Point is to be added, sorted
	 * @param point - the Point to add to the given list
	 * @return index - the index at which the given Point should be added
	 */
	public static int getAddIndexCell(ArrayList<Cell> list, Point point)
	{
		int low = 0;					// the lower bound of the remaining possible space in which the value could lie
		int high = list.size() - 1;		// the higher bound of the remaining possible space in which the value could lie
		int mid;						// the point to be checked, the midpoint between low and high (within the bounds)
		
		if (list.size() == 0)
		{
			return 0;
		}
		
		while (true)
		{
			// find the point to be checked, the midpoint between the low and high boundaries
			mid = (low + high)/2;
			
			if (isLess(list.get(mid), point))
			{
				low = mid + 1;			// the lower bound is too low, adjust to above the point checked
				if (mid < list.size() - 1)
				{
					if (isGreater(list.get(mid + 1), point))
					{
						return mid + 1;	// mid < point, mid + 1 > point -> mid + 1 is the index at which to add
					}
				}
				else
				{
					return mid + 1;		// mid is the last index, everything else is less -> add at last index + 1
				}
			}
			else if (isGreater(list.get(mid), point))
			{
				high = mid;			// the higher bound is too high, adjust to below the point checked
				if (mid > 0)
				{
					if (isLess(list.get(mid - 1), point))
					{
						return mid;		// mid > point, mid - 1 < point -> mid is the index at which to add
					}
				}
				else
				{
					return mid;			// mid is the first index, everything else is greater -> add at first index
				}
			}
			else
			{
				return -1;				// point is already in the list
			}
		}
	}
	
	/**
	 * Compares the two given points and returns a comparison.
	 * The x-coordinate is the primary comparison and the y-coordinate is secondary if the x-coordinates are equal.
	 * That is,<br>
	 * if p1.x > p2.x: GREATER<br>
	 * if p1.x < p2.x: LESS<br>
	 * if p1.x == p2.x:<br>
	 * &nbsp &nbsp &nbsp if p1.y > p2.y: GREATER<br>
	 * &nbsp &nbsp &nbsp if p1.y < p2.y: LESS<br>
	 * &nbsp &nbsp &nbsp if p1.y == p2.y: EQUAL<br>
	 * 
	 * @param p1 - the first point to be compared
	 * @param p2 - the second point to be compared
	 * @return comparison - GREATER if p1 > p2, LESS if p1 < p2, EQUAL otherwise
	 */
	public static int compare(Point p1, Point p2)
	{
		if (p1.x > p2.x)
		{
			return GREATER;
		}
		else if (p1.x < p2.x)
		{
			return LESS;
		}
		else
		{
			if (p1.y > p2.y)
			{
				return GREATER;
			}
			else if (p1.y < p2.y)
			{
				return LESS;
			}
			else
			{
				return EQUAL;
			}
		}
	}
	
	/**
	 * Determines whether the first point is greater than the second.
	 * The point is greater if either the x-coordinate is larger or,
	 *  in the case that the x-coordinates are equal, the y-coordinate is larger.
	 * 
	 * @param p1 - the first point to be compared
	 * @param p2 - the second point to be compared
	 * @return greater - true if the first point is greater than the second, false otherwise
	 */
	public static boolean isGreater(Point p1, Point p2)
	{
		if (p1.x > p2.x || (p1.x == p2.x && p1.y > p2.y))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the first point is less than the second.
	 * The point is less if either the x-coordinate is less or,
	 *  in the case that the x-coordinates are equal, the y-coordinate is larger.
	 * 
	 * @param p1 - the first point to be compared
	 * @param p2 - the second point to be compared
	 * @return less - true if the first point is less than the second, false otherwise
	 */
	public static boolean isLess(Point p1, Point p2)
	{
		if (p1.x < p2.x || (p1.x == p2.x && p1.y < p2.y))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the two points are equal.
	 * This method is equivalent to calling p1.equals(p2),
	 *  and returns true only if both coordinates are equal.
	 * 
	 * @param p1 - the first point to be compared
	 * @param p2 - the second point to be compared
	 * @return equal - true if the two points are equal, false otherswise
	 */
	public static boolean isEqual(Point p1, Point p2)
	{
		if (p1.x == p2.x && p1.y == p2.y)
		{
			return true;
		}
		return false;
	}
}
