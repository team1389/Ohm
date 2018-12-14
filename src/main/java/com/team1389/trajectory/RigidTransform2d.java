package com.team1389.trajectory;

import com.team1389.util.interpolable.Interpolable;

/**
 * Represents a 2d pose (rigid transform) containing translational and rotational elements.
 * 
 * Inspired by Sophus
 * 
 * @see <a href="https://github.com/strasdat/Sophus/tree/master/sophus">Sophus github</a>
 */

public class RigidTransform2d implements Interpolable<RigidTransform2d> {
	private final static double kEps = 1E-9;

	/**
	 * Movement along an arc at constant curvature and velocity. We can use ideas from "differential
	 * calculus" to create new RigidTransform2d's from a Delta.
	 **/
	public static class Delta {
		/**
		 * change in x
		 */
		public final double dx;
		/**
		 * change in y
		 */
		public final double dy;
		/**
		 * change in theta
		 */
		public final double dtheta;

		/**
		 * @param dx change in x
		 * @param dy change in y
		 * @param dtheta change in theta
		 */
		public Delta(double dx, double dy, double dtheta) {
			this.dx = dx;
			this.dy = dy;
			this.dtheta = dtheta;
		}

		public Delta(RigidTransform2d pose2, RigidTransform2d pose1) {
			RigidTransform2d poseChange = pose2.transformBy(pose1.inverse());
			this.dx = poseChange.translation_.x_;
			this.dy = poseChange.translation_.y_;
			this.dtheta = poseChange.rotation_.getRadians();
		}
	}

	protected Translation2d translation_;
	protected Rotation2d rotation_;

	/**
	 * default rigidTransform has 0 rotation and translation
	 */
	public RigidTransform2d() {
		translation_ = new Translation2d();
		rotation_ = new Rotation2d();
	}

	/**
	 * @param translation the translation component of the transform
	 * @param rotation the rotation component of the transform
	 */
	public RigidTransform2d(Translation2d translation, Rotation2d rotation) {
		translation_ = translation;
		rotation_ = rotation;
	}

	/**
	 * makes a copy of the given rigidTransform
	 * 
	 * @param other the rigidTransform to copy
	 */
	public RigidTransform2d(RigidTransform2d other) {
		translation_ = new Translation2d(other.translation_);
		rotation_ = new Rotation2d(other.rotation_);
	}

	/**
	 * generates a rigidTransform with the given translation component and 0 rotation
	 * 
	 * @param translation the translation component
	 * @return the rigidTransform object
	 */
	public static RigidTransform2d fromTranslation(Translation2d translation) {
		return new RigidTransform2d(translation, new Rotation2d());
	}

	/**
	 * generates a rigidTransform with the given rotation component and 0 translation
	 * 
	 * @param rotation the rotation component
	 * @return the rigidTransform object
	 */
	public static RigidTransform2d fromRotation(Rotation2d rotation) {
		return new RigidTransform2d(new Translation2d(), rotation);
	}

	/**
	 * Obtain a new RigidTransform2d from a (constant curvature) velocity.
	 * 
	 * @param delta the velocity to watch
	 * @return the resultant rigidTransform
	 * @see <a href= "https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp">Sophus se2</a>
	 */
	public static RigidTransform2d fromVelocity(Delta delta) {
		double sin_theta = Math.sin(delta.dtheta);
		double cos_theta = Math.cos(delta.dtheta);
		double s, c;
		if (Math.abs(delta.dtheta) < kEps) {
			s = 1.0 - 1.0 / 6.0 * delta.dtheta * delta.dtheta;
			c = .5 * delta.dtheta;
		} else {
			s = sin_theta / delta.dtheta;
			c = (1.0 - cos_theta) / delta.dtheta;
		}
		return new RigidTransform2d(new Translation2d(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
				new Rotation2d(cos_theta, sin_theta, false));
	}

	/**
	 * @return the translation component
	 */
	public Translation2d getTranslation() {
		return translation_;
	}

	/**
	 * @param translation the new translation component
	 */
	public void setTranslation(Translation2d translation) {
		translation_ = translation;
	}

	/**
	 * @return the rotation component
	 */
	public Rotation2d getRotation() {
		return rotation_;
	}

	/**
	 * @param rotation the new rotation component
	 */
	public void setRotation(Rotation2d rotation) {
		rotation_ = rotation;
	}

	/**
	 * Transforming this RigidTransform2d means first translating by other.translation and then
	 * rotating by other.rotation
	 * 
	 * @param other The other transform.
	 * @return This transform * other
	 */
	public RigidTransform2d transformBy(RigidTransform2d other) {
		return new RigidTransform2d(translation_.translateBy(other.translation_.rotateBy(rotation_)),
				rotation_.rotateBy(other.rotation_));
	}

	/**
	 * Transforming this RigidTransform2d means first translating by other.translation and then
	 * rotating by other.rotation
	 * 
	 * @param other The other transform.
	 * @return This transform * other
	 */
	public RigidTransform2d transformByRotate(RigidTransform2d other) {
		return new RigidTransform2d(translation_.translateBy(other.translation_.rotateBy(rotation_)),
				rotation_);
	}

	/**
	 * Transforming this RigidTransform2d means first translating by other.translation and then
	 * rotating by other.rotation
	 * 
	 * @param other The other transform.
	 * @return This transform * other
	 */
	public RigidTransform2d transformBySimple(RigidTransform2d other) {
		return new RigidTransform2d(translation_.translateBy(other.translation_), rotation_.rotateBy(other.rotation_));
	}

	/**
	 * The inverse of this transform "undoes" the effect of translating by this transform.
	 * 
	 * @return The opposite of this transform.
	 */
	public RigidTransform2d inverse() {
		Rotation2d rotation_inverted = rotation_.inverse();
		return new RigidTransform2d(translation_.inverse().rotateBy(rotation_inverted), rotation_inverted);
	}

	/**
	 * Do linear interpolation of this transform (there are more accurate ways using constant
	 * curvature, but this is good enough).
	 */
	@Override
	public RigidTransform2d interpolate(RigidTransform2d other, double x) {
		if (x <= 0) {
			return new RigidTransform2d(this);
		} else if (x >= 1) {
			return new RigidTransform2d(other);
		}
		return new RigidTransform2d(translation_.interpolate(other.translation_, x),
				rotation_.interpolate(other.rotation_, x));
	}

	@Override
	public String toString() {
		return "T:" + translation_.toString() + ", R:" + rotation_.toString();
	}
}
