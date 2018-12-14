package com.team1389.motion_profile;

/**
 * This class represents a solved kinematics system with constant acceleration. It is final, in that once it is constructed
 * each of the system's parameters will remain constant. To get any of the parameters one can access any of the final class
 * variables.
 *
 */
public class SimpleKinematics {
		
	/**
	 * The initial velocity of the system
	 */
	public final double vo;
	
	/**
	 * The final velocity of the system
	 */
	public final double vf;
	
	/**
	 * The time the motion takes
	 */
	public final double t;
	
	/**
	 * The constant acceleration of the system
	 */
	public final double a;
	
	/**
	 * The distance traveled in the system
	 */
	public final double x;

	private double initialV, finalV, time, accel, dx;


	/**
	 * Creates and solves the kinematics system. At least three of the variable must be real, but the rest may be Double.NaN if they are unknown.
	 * No check is made to see if the motion is possible as described, and so invalid input may result in negative time or other
	 * unexpected results.
	 * @param v0 The initial velocity of the system
	 * @param vf The final velocity of the system
	 * @param t The total time the motion takes
	 * @param a The constant acceleration of the system
	 * @param S The distance traveled in the system
	 */
	public SimpleKinematics(double v0, double vf, double t, double a, double S) {
		this.accel = a;
		this.initialV = v0;
		this.time = t;
		this.finalV = vf;
		this.dx = S;
		solveSystem();
		
		this.vo = initialV;
		this.vf = finalV;
		this.t = time;
		this.a = accel;
		this.x = dx;
	}

	@Override
	public String toString() {
		return "Vo: " + initialV + " Vf: " + finalV + " a: " + accel + " t: " + time + " S: " + dx;
	}

	private void solveSystem() {
		int code = 0;
		code = Double.isNaN(dx) ? code : code | 1;
		code = code << 1;
		code = Double.isNaN(time) ? code : code | 1;
		code = code << 1;
		code = Double.isNaN(accel) ? code : code | 1;
		code = code << 1;
		code = Double.isNaN(finalV) ? code : code | 1;
		code = code << 1;
		code = Double.isNaN(initialV) ? code : code | 1;
		solveSystem(code);
	}

	private void solveSystem(int code) {
		double tS = dx;
		double tVo = initialV;
		double tV = finalV;
		double ta = accel;
		double tt = time;
		if ((code & 19) == 19) {
			if (tt == 0) {
				time = 0;
				accel = 0;
			} else {
				tt = 2 * tS / (tVo + tV);
				time = tt;
				accel = (tV - tVo) / tt;
			}
		}
		if ((code & 7) == 7) {
			if (ta == 0) {
				time = 0;
				dx = 0;
			} else {
				tt = (tV - tVo) / ta;
				time = tt;
				dx = tVo * tt + ta * tt * tt / 2;
			}
		}
		if ((code & 11) == 11) {
			if (tt == 0) {
				accel = 0;
				dx = 0;
			} else {
				ta = (tV - tVo) / tt;
				accel = ta;
				dx = tVo * tt + ta * tt * tt / 2;
			}
		}
		if ((code & 14) == 14) {
			tVo = tV - ta * tt;
			initialV = tVo;
			dx = tVo * tt + ta * tt * tt / 2;
		}
		if ((code & 26) == 26) {
			if (tt == 0) {
				initialV = tV;
				accel = 0;
			} else {
				tVo = 2 * tS / tt - tV;
				initialV = tVo;
				accel = (tV - tVo) / tt;
			}
		}
		if ((code & 22) == 22) {
			if (ta == 0) {
				initialV = tV;
				time = dx / initialV;
			} else {
				tt = -tV + Math.signum(tS)*Math.sqrt(tV * tV - 2 * ta * tS) / -accel;
				initialV = tV - ta * tt;
				time = tt;
			}
		}
		if ((code & 28) == 28) {
			if (tt == 0) {
				initialV = 0;
				finalV = 0;
			} else {
				tVo = (tS - ta * tt * tt / 2) / tt;
				initialV = tVo;
				finalV = tVo + ta * tt;
			}
		}
		if ((code & 13) == 13) {
			tV = tVo + ta * tt;
			finalV = tV;
			dx = tVo * tt + ta * tt * tt / 2;
		}
		if ((code & 25) == 25) {
			if (tt == 0) {
				accel = 0;
				finalV = tVo;
			} else {
				ta = 2 * (tS - tVo * tt) / (tt * tt);
				accel = ta;
				finalV = tVo + ta * tt;
			}
		}
		if ((code & 21) == 21) {
			if (ta == 0) {
				finalV = tVo;
				time = dx / finalV;
			} else {
				tV = Math.signum(accel) * Math.sqrt(tVo * tVo + 2 * ta * tS);
				finalV = tV;
				time = (tV - tVo) / ta;
			}
		}
	}
	

}
