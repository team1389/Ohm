	package com.team1389.motion_profile;

/**
 * This class contains methods for creating and combining (and maybe sometime in the future manipulating) {@link MotionProfile}s
 * @author Josh
 *
 */
public class ProfileUtil {

	/**
	 * Creates a {@link MotionProfile} that is the fastest way to travel a certain distance. It is "trapezoidal" in nature
	 * because the speed graph, in the simplest case, looks like a trapezoid: a positive slope (acceleration), 
	 * a horizontal line at top (max speed), and a negative slope (deceleration). <br>
	 * Some important edge cases: <br>
	 * If the max speed cannot be reached the speed graph will be a triangle, not a trapezoid. <br>
	 * If the robot cannot stop in time it will decelerate as fast as possible, accelerate backwards, and decelerate again. <br>
	 * If the initial velocity is in the opposite direction of the distance then the robot will decelerate as fast as possible and
	 * then accelerate and decelerate towards the direction of the distance.
	 * @param dx The distance to be traveled (area under the trapezoid), may be positive or negative
	 * @param Vo The initial velocity, may be positive or negative
	 * @param maxAccel The maximum acceleration of the robot, by convention positive
	 * @param maxDecel The maximum deceleration of the robot, by convention positive
	 * @param maxSpeed The maximum speed of the robot, by convention positive
	 * @return The {@link MotionProfile} representing the path of the robot
	 */
	public static MotionProfile trapezoidal(double dx, double Vo, double maxAccel, double maxDecel, double maxSpeed) {
		// Make sure these values are positive
		maxDecel = Math.abs(maxDecel);
		maxAccel = Math.abs(maxAccel);
		maxSpeed = Math.abs(maxSpeed);

		boolean inverted = false;
		if (dx < 0) {
			Vo *= -1;
			dx *= -1;
			inverted = true;
		}
		// From here on out is is assumed dx is positive

		// If Vo is less than 0 (i.e. we are going in the opposite direction of our dx) immediately slow to 0 and then calculated
		// what to do next (recursivly)
		if (Vo < 0) {
			SimpleKinematics slowDown = new SimpleKinematics(Vo, 0, Double.NaN, maxDecel, Double.NaN);
			return combine(new ConstantAccelProfile(slowDown, inverted),
					trapezoidal((inverted ? -1 : 1) * (dx - slowDown.x), 0, maxAccel, maxDecel, maxSpeed));
		}

		// From here on out, it is assumed vo >= 0
		SimpleKinematics accelSegment = new SimpleKinematics(Vo, maxSpeed, Double.NaN, maxAccel, Double.NaN);
		SimpleKinematics decelSegment = new SimpleKinematics(maxSpeed, 0, Double.NaN, -maxDecel, Double.NaN);

		double diff = dx - (accelSegment.x + decelSegment.x);
		if (diff < 0) {
			SimpleKinematics testDecel = new SimpleKinematics(Vo, 0, Double.NaN, -maxAccel, Double.NaN);
			if (testDecel.x > dx /* i.e. we cant stop in time and have to backtrack */) {
				double vMax = -calculateTopSpeed(testDecel.x - dx, 0, maxAccel, maxDecel);
				accelSegment = new SimpleKinematics(0, vMax, Double.NaN, -maxAccel, Double.NaN);
				decelSegment = new SimpleKinematics(vMax, 0, Double.NaN, maxDecel, Double.NaN);
				return combine(new ConstantAccelProfile(testDecel, inverted),
						new ConstantAccelProfile(accelSegment, inverted),
						new ConstantAccelProfile(decelSegment, inverted));
			}

			double vMax = calculateTopSpeed(dx, Vo, maxAccel, maxDecel);
			accelSegment = new SimpleKinematics(Vo, vMax, Double.NaN, maxAccel, Double.NaN);
			decelSegment = new SimpleKinematics(vMax, 0, Double.NaN, -maxDecel, Double.NaN);
			return combine(new ConstantAccelProfile(accelSegment, inverted),
					new ConstantAccelProfile(decelSegment, inverted));
		} else {
			return combine(new ConstantAccelProfile(accelSegment, inverted),
					new ConstantAccelProfile(0, diff, maxSpeed, inverted),
					new ConstantAccelProfile(decelSegment, inverted));
		}
	}

	/**
	 * Returns the height of the triangular speed profile. In other words, how fast you can possibly go with constant acceleration and constant deceleration in the given x. It is interpreted as a speed, not a velocity, so it could be negative. However, just assume everything is positive and convert later.
	 * 
	 * @param dx The magnitude of the distance to travel
	 * @param Vo The magnitude of the initial velocity
	 * @param a The acceleration, the right side of the triangle
	 * @param d The deceleration, the left side of the triangle
	 * @return The magnitude of the max velocity of the triangle
	 */
	private static double calculateTopSpeed(double dx, double Vo, double a, double d) {
		a = Math.abs(a);
		d = Math.abs(d);
		dx = Math.abs(dx);
		// For the derivation of this equation, draw out a triangle and think it out.
		// For a hint: use vf ^ 2 = vo^2 + 2ax twice.
		return Math.sqrt((dx + Vo * Vo / 2.0 / a) / (1.0 / 2.0 / a + 1.0 / 2.0 / d));
	}

	/**
	 * Makes use of the {@link CombinedProfile} class
	 * @param motionProfiles The {@link MotionProfile}s to combine
	 * @return The single, combined {@link MotionProfile}
	 */
	public static MotionProfile combine(MotionProfile... motionProfiles) {
		MotionProfile combined = null;
		for (MotionProfile motionProfile : motionProfiles) {
			if (combined == null) {
				combined = motionProfile;
			} else {
				combined = new CombinedProfile(combined, motionProfile);
			}
		}
		return combined;
	}

}
