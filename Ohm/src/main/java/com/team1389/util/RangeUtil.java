package com.team1389.util;

/**
 * this class offers a set of operations for a value in a range
 * 
 * @author amind
 *
 */
public class RangeUtil {
	/**
	 * Re-maps a number from one range to another. *
	 * <p>
	 * Does not constrain values to within the range, because out-of-range values are sometimes intended and useful. The constrain() function may be used either before or after this function, if limits to the ranges are desired.
	 * 
	 * <p>
	 * Note that the "lower bounds" of either range may be larger or smaller than the "upper bounds" so the map() function may be used to reverse a range of numbers, for example
	 * 
	 * <pre>
	 * {@code y = map(x, 1, 50, 50, 1);}
	 * </pre>
	 * <p>
	 * The function also handles negative numbers well, so that this example
	 * 
	 * <pre>
	 * {@code y = map(x, 1, 50, 50, -100);}
	 * </pre>
	 * 
	 * is also valid and works well.
	 * 
	 * @param x the value in the original range, Does not constrain values to within the range, because out-of-range values are sometimes intended and useful.
	 * @param in_min the lower limit of the original range
	 * @param in_max the upper limit of the original range
	 * @param out_min the lower limit of the mapped range
	 * @param out_max the upper limit of the mapped range
	 * @return the mapped version of {@code x}
	 */
	public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	/**
	 * Constrains a value to be between [{@code min} and {@code max}]
	 * 
	 * @param v the original value
	 * @param max the maximum value of {@code v };
	 * @param min the minimum value of {@code v };
	 * @return the constrained version of {@code v}
	 */
	public static double limit(double v, double min, double max) {
		return v > max ? max : v < min ? min : v;
	}

	/**
	 * Truncates any val close to zero, creating a "dead zone" around zero.
	 * 
	 * @param val the value to test; if it is in the dead zone, it will be set to zero.
	 * @param deadband the minimum distance from zero to avoid truncating
	 * @return the truncated version of {@code val}
	 */
	public static double applyDeadband(double val, double deadband) {
		return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
	}

	/**
	 * wraps a value to be within the given range (when a value leaves the range, it enters the range from the other side)
	 * 
	 * @param val the value to wrap
	 * @param min the minimum of the wrapping range
	 * @param max the maximum of the wrapping range
	 * @return the wrapped value
	 */
	public static double wrapValue(double val, double min, double max) {
		double divider = (max - min);
		return min + ((val - min) % (divider) + divider) % divider;

	}

}
