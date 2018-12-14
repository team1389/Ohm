package com.team1389.system.drive;

public class MecanumAlgorithm {
	public static FourWheelSignal mecanumCalc(double xThrottle, double yThrottle, double twist, boolean fieldOriented,
			double theta) {
		double forward = -yThrottle; // push joystick forward to go forward
		double right = xThrottle; // push joystick to the right to strafe right
		double clockwise = twist;
		if (fieldOriented) {
			System.out.println("Mecanum Drive System: field oriented");
			double temp = forward * Math.cos(theta) + right * Math.sin(theta);
			right = -forward * Math.sin(theta) + right * Math.cos(theta);
			forward = temp;
		}
		double front_left = forward + clockwise + right;
		double front_right = forward - clockwise - right;
		double rear_left = forward + clockwise - right;
		double rear_right = forward - clockwise + right;
		// Finally, normalize the wheel speed commands
		// so that no wheel speed command exceeds magnitude of 1:
		double max = Math.abs(front_left);
		if (Math.abs(front_right) > max)
			max = Math.abs(front_right);
		if (Math.abs(rear_left) > max)
			max = Math.abs(rear_left);
		if (Math.abs(rear_right) > max)
			max = Math.abs(rear_right);
		if (max > 1) {
			front_left /= max;
			front_right /= max;
			rear_left /= max;
			rear_right /= max;
		}

		return new FourWheelSignal(front_left, front_right, rear_left, rear_right);
	}
}
