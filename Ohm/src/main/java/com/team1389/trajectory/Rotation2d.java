package com.team1389.trajectory;

import java.text.DecimalFormat;

import com.team1389.util.interpolable.Interpolable;

/**
 * A rotation in a 2d coordinate frame represented a point on the unit circle (cosine and sine).
 * 
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 */
public class Rotation2d implements Interpolable<Rotation2d> {
	protected static final double kEpsilon = 1E-9;

	protected double cos_angle_;
	protected double sin_angle_;

	/**
	 * 
	 */
	public Rotation2d() {
		this(1, 0, false);
	}

	/**
	 * creates a rotation object using the given x and y as coordinates on the unit circle
	 * 
	 * @param x the x coordinate on the unit circle
	 * @param y the y coordinate on the unit circle
	 * @param normalize whether to use normalizing to reset rounding errors
	 */
	public Rotation2d(double x, double y, boolean normalize) {
		cos_angle_ = x;
		sin_angle_ = y;
		if (normalize) {
			normalize();
		}
	}

	/**
	 * makes a copy of the given rotation
	 * 
	 * @param other the rotation to copy
	 */
	public Rotation2d(Rotation2d other) {
		cos_angle_ = other.cos_angle_;
		sin_angle_ = other.sin_angle_;
	}

	/**
	 * generates a rotation object from a given angle
	 * 
	 * @param angle_radians the angle in radians
	 * @return the rotation2d
	 */
	public static Rotation2d fromRadians(double angle_radians) {
		return new Rotation2d(Math.cos(angle_radians), Math.sin(angle_radians), false);
	}

	/**
	 * generates a rotation object from a given angle
	 * 
	 * @param angle_degrees the angle in degrees
	 * @return the rotation2d
	 */
	public static Rotation2d fromDegrees(double angle_degrees) {
		return fromRadians(Math.toRadians(angle_degrees));
	}

	/**
	 * From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object we might accumulate rounding errors. Normalizing forces us to re-scale the sin and cos to reset rounding errors.
	 */
	public void normalize() {
		double magnitude = Math.hypot(cos_angle_, sin_angle_);
		if (magnitude > kEpsilon) {
			sin_angle_ /= magnitude;
			cos_angle_ /= magnitude;
		} else {
			sin_angle_ = 0;
			cos_angle_ = 1;
		}
	}

	/**
	 * @return the cos of the rotation angle
	 */
	public double cos() {
		return cos_angle_;
	}

	/**
	 * @return the sin of the rotation angle
	 */
	public double sin() {
		return sin_angle_;
	}

	/**
	 * @return the tangent of the rotation angle
	 * 
	 */
	public double tan() {
		if (cos_angle_ < kEpsilon) {
			if (sin_angle_ >= 0.0) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		return sin_angle_ / cos_angle_;
	}
	
	/**
	 * @return the rotation angle in radians
	 */
	public double getRadians() {
		return Math.atan2(sin_angle_, cos_angle_);
	}
	
	/**
	 * @return the rotation angle in degrees
	 */
	public double getDegrees() {
		return Math.toDegrees(getRadians());
	}

	/**
	 * We can rotate this Rotation2d by adding together the effects of it and another rotation.
	 * 
	 * @param other The other rotation. See: https://en.wikipedia.org/wiki/Rotation_matrix
	 * @return This rotation rotated by other.
	 */
	public Rotation2d rotateBy(Rotation2d other) {
		return new Rotation2d(cos_angle_ * other.cos_angle_ - sin_angle_ * other.sin_angle_,
				cos_angle_ * other.sin_angle_ + sin_angle_ * other.cos_angle_, true);
	}

	/**
	 * The inverse of a Rotation2d "undoes" the effect of this rotation.
	 * 
	 * @return The opposite of this rotation.
	 */
	public Rotation2d inverse() {
		return new Rotation2d(cos_angle_, -sin_angle_, false);
	}

	@Override
	public Rotation2d interpolate(Rotation2d other, double x) {
		if (x <= 0) {
			return new Rotation2d(this);
		} else if (x >= 1) {
			return new Rotation2d(other);
		}
		double angle_diff = inverse().rotateBy(other).getRadians();
		return this.rotateBy(Rotation2d.fromRadians(angle_diff * x));
	}

	@Override
	public String toString() {
		final DecimalFormat fmt = new DecimalFormat("#0.000");
		return "(" + fmt.format(getDegrees()) + " deg)";
	}
}
