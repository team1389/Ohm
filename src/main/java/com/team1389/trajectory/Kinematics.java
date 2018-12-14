package com.team1389.trajectory;

/**
 * Provides forward and inverse kinematics equations for the robot modeling the wheelbase as a
 * differential drive (with a corrective factor to account for the inherent skidding of the center 4
 * wheels quasi-kinematically).
 */

public class Kinematics {
	private static final double kEpsilon = 1E-9;
	private double kTrackLengthInches;
	private double kTrackWidthInches;
	private double kTrackEffectiveDiameter;
	private double kTrackScrubFactor;

	/**
	 * @param trackLengthInches the distance from the front axle to the rear axle
	 * @param trackWidthInches the width of the wheelbase
	 * @param trackScrubFactor constant to deal with discrepancy between theoretical effectivDiam
	 *            and actual effective diam, should be 1 for ideal scrub
	 */
	public Kinematics(double trackLengthInches, double trackWidthInches, double trackScrubFactor) {
		this.kTrackLengthInches = trackLengthInches;
		this.kTrackWidthInches = trackWidthInches;
		this.kTrackScrubFactor = trackScrubFactor;
		this.kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches + kTrackLengthInches * kTrackLengthInches)
				/ kTrackWidthInches;
	}

	/**
	 * Forward kinematics using only encoders, rotation is implicit (less accurate than below, but
	 * useful for predicting motion)
	 * 
	 * @param left_wheel_delta the distance travelled by the left encoders
	 * @param right_wheel_delta the distance travelled by the right encoders
	 * @return the calculated velocity
	 */
	public RigidTransform2d.Delta forwardKinematics(double left_wheel_delta, double right_wheel_delta) {
		double linear_velocity = (left_wheel_delta + right_wheel_delta) / 2;
		double delta_v = (right_wheel_delta - left_wheel_delta) / 2;
		double delta_rotation = delta_v * 2 * kTrackScrubFactor / kTrackEffectiveDiameter;
		return new RigidTransform2d.Delta(linear_velocity, 0, delta_rotation);
	}

	/**
	 * Forward kinematics using encoders and explicitly measured rotation (ex. from gyro)
	 * 
	 * @param left_wheel_delta the distance travelled by the left encoders
	 * @param right_wheel_delta the distance travelled by the right encoders
	 * @param delta_rotation_rads the angle rotated by the robot (in radians)
	 * @return the calculated velocity
	 */
	public RigidTransform2d.Delta forwardKinematics(double left_wheel_delta, double right_wheel_delta,
			double delta_rotation_rads) {
		return new RigidTransform2d.Delta((left_wheel_delta + right_wheel_delta) / 2, 0, delta_rotation_rads);
	}

	/**
	 * Append the result of forward kinematics to a previous pose.
	 * 
	 * @param current_pose the last observed robot pose
	 * @param left_wheel_delta the distance travelled by the left encoders
	 * @param right_wheel_delta the distance travelled by the right encoders
	 * @param current_heading the current rotation of the robot
	 * @return the updated position
	 */
	public RigidTransform2d integrateForwardKinematics(RigidTransform2d current_pose, double left_wheel_delta,
			double right_wheel_delta, Rotation2d current_heading) {
		RigidTransform2d.Delta with_gyro = forwardKinematics(left_wheel_delta, right_wheel_delta,
				current_pose.getRotation().inverse().rotateBy(current_heading).getRadians());
		return current_pose.transformBy(RigidTransform2d.fromVelocity(with_gyro));
	}

	/**
	 * this class stores a left and right wheel velocity
	 */
	public static class DriveVelocity {
		/**
		 * the left wheel velocity
		 */
		public final double left;
		/**
		 * the right wheel velocity
		 */
		public final double right;

		/**
		 * @param left the left wheel velocity
		 * @param right the right wheel velocity
		 */
		public DriveVelocity(double left, double right) {
			this.left = left;
			this.right = right;
		}
	}

	/**
	 * calculate the wheel velocities based on robot velocity
	 * 
	 * @param velocity the robot's velocity
	 * @return the left and right wheel velocities
	 */
	public DriveVelocity inverseKinematics(RigidTransform2d.Delta velocity) {
		if (Math.abs(velocity.dtheta) < kEpsilon) {
			return new DriveVelocity(velocity.dx, velocity.dx);
		}
		double delta_v = kTrackEffectiveDiameter * velocity.dtheta / (2 * kTrackScrubFactor);
		return new DriveVelocity(velocity.dx - delta_v, velocity.dx + delta_v);
	}

	public RigidTransform2d.Delta inverse(double fl, double fr, double bl, double br) {
		double FWD = (fl + bl + br + fr) / 4;

		double STR = (fl - bl + br - fr) / 4;

		double Wv = (fl + bl - br - fr) / (4*20);
		return new RigidTransform2d.Delta(FWD, STR, Wv);
	}
}