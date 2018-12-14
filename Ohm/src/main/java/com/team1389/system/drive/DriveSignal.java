package com.team1389.system.drive;

/**
 * A drivetrain command consisting of the left, right motor settings and whether the brake mode is enabled.
 */
public class DriveSignal {
	protected double leftMotor;
	protected double rightMotor;

	/**
	 * @param left the left motor value
	 * @param right the right motor value
	 */
	public DriveSignal(double left, double right) {
		this.leftMotor = left;
		this.rightMotor = right;
	}

	/**
	 * a neutral DriveSignal
	 */
	public static DriveSignal NEUTRAL = new DriveSignal(0, 0);

	@Override
	public String toString() {
		return "L: " + leftMotor + ", R: " + rightMotor;
	}
}