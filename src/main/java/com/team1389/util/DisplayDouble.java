package com.team1389.util;

import java.text.DecimalFormat;

public class DisplayDouble {

	public static void main(String[] args) {
		System.out.println(get(-123.45636, 5));
		System.out.println(get(5674.1236, 7));
		System.out.println(get(-11456764.213466, 7));
		System.out.println(get(1243564365474357346243637352364373462364752343534646319.9757, 5));
		System.out.println(get(-.000000235235236625, 7));
		System.out.println(get(-0.000000000000000000000000000000000023525235,5));
		System.out.println(get(10000000, 7));
	}

	final String interp;

	public DisplayDouble(int width, double val) {
		this.interp = get(val, width);
	}

	/**
	 * 
	 * @param d The double to turn into a string of length
	 * @param length Greater than or equal to 7 if you want all cases represented. Can be less, but
	 *            some cases might go over.<br>
	 *            A negative really small or really large number is 7 long.<br>
	 *            A positive really small or really large number and a negative large number is 6
	 *            long.<br>
	 *            Does not count the decimal point.
	 * @return The string
	 */
	public static String get(double d, int length) {
		if (d == 0) {
			return String.format(("%." + (length - 1) + "f"), 0.0);
		}

		int numDecimalPlaces = length - (int) (Math.log10(Math.abs(d)) + 1);
		if (d < 0)
			numDecimalPlaces--;
		if (numDecimalPlaces > 0) {
			String s = String.format(("%." + numDecimalPlaces + "f"), d);
			if (s.length() <= length + 1) {
				return s;
			}
		}

		String s = "";
		StringBuilder hashtags = new StringBuilder("#");
		int lastLength = 0;
		while (s.length() < length + 1) {
			DecimalFormat formatter = new DecimalFormat("0." + hashtags + "E0");
			s = formatter.format(d);
			hashtags.append("#");
			if (s.length() == lastLength) {
				break;
			}
			lastLength = s.length();
		}
		if (s.length() < length + 1) {
			s = addZeros(s, length);
		}
		return s;
	}

	private static String addZeros(String s, int goalLength) {
		if (!s.contains(".")) {
			s = s.substring(0, s.indexOf('E')) + ".0" + s.substring(s.indexOf('E'));
		}
		StringBuilder result = new StringBuilder(s);
		while (result.length() < goalLength + 1) {
			int i = result.lastIndexOf("E");
			result = result.insert(i, "0");
		}
		return result.toString();
	}

	public String toString() {
		return interp;
	}
}
