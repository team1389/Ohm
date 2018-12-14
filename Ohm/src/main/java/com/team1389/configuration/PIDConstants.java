package com.team1389.configuration;

/**
 * PID control configuration constants
 * 
 * @author Jacob Prinz
 */
public final class PIDConstants {
	/**
	 * the default PID gains
	 */
	public static final PIDConstants DEFAULT = new PIDConstants(0, 0, 0);
	/**
	 * the proportional gain
	 */
	public final double p;
	/**
	 * the integral gain
	 */
	public final double i;
	/**
	 * the derivative gain
	 */
	public final double d;
	/**
	 * the feed forward gain
	 */
	public final double f;

	/**
	 * @param p the proportional gain
	 * @param i the integral gain
	 * @param d the derivative gain
	 * @param f the feed forward gain
	 */
	public PIDConstants(double p, double i, double d, double f) {
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
	}

	/**
	 * @param p the proportional gain
	 * @param i the integral gain
	 * @param d the derivative gain
	 */
	public PIDConstants(double p, double i, double d) {
		this(p, i, d, 0);
	}
}
