package com.team1389.trajectory;

import java.text.DecimalFormat;

import com.team1389.util.interpolable.Interpolable;

/**
 * A translation in a 2d coordinate frame. Translations are simply shifts in an (x, y) plane.
 */
public class Translation2d implements Interpolable<Translation2d> {
	protected double x_;
	protected double y_;
	
	/**
	 * default constructor creates the translation (0,0)
	 */
	public Translation2d() {
		x_ = 0;
		y_ = 0;
	}
	/**
	 * @param x the x offset
	 * @param y the y offset
	 */
	public Translation2d(double x, double y) {
		x_ = x;
		y_ = y;
	}
	/**
	 * makes a copy of the given translation
	 * @param other the translation to copy
	 */
	public Translation2d(Translation2d other) {
		x_ = other.x_;
		y_ = other.y_;
	}

	/**
	 * The "norm" of a transform is the Euclidean distance in x and y.
	 * 
	 * @return Sqrt(x^2 + y^2)
	 */
	public double norm() {
		return Math.hypot(x_, y_);
	}

	/**
	 * 
	 * @return the x offset of the transform
	 */
	public double getX() {
		return x_;
	}

	/**
	 * 
	 * @return the y offset of the transform
	 */
	public double getY() {
		return y_;
	}

	/**
	 * @param x the new x offset of the transform
	 */
	public void setX(double x) {
		x_ = x;
	}

	/**
	 * @param y the new y offset of the transform
	 */
	public void setY(double y) {
		y_ = y;
	}

	/**
	 * We can compose Translation2d's by adding together the x and y shifts.
	 * 
	 * @param other The other translation to add.
	 * @return The combined effect of translating by this object and the other.
	 */
	public Translation2d translateBy(Translation2d other) {
		return new Translation2d(x_ + other.x_, y_ + other.y_);
	}

	/**
	 * We can also rotate Translation2d's. See: https://en.wikipedia.org/wiki/Rotation_matrix
	 * 
	 * @param rotation The rotation to apply.
	 * @return This translation rotated by rotation.
	 */
	public Translation2d rotateBy(Rotation2d rotation) {
		return new Translation2d(x_ * rotation.cos() - y_ * rotation.sin(), x_ * rotation.sin() + y_ * rotation.cos());
	}

	/**
	 * The inverse simply means a Translation2d that "undoes" this object.
	 * 
	 * @return Translation by -x and -y.
	 */
	public Translation2d inverse() {
		return new Translation2d(-x_, -y_);
	}

	@Override
	public Translation2d interpolate(Translation2d other, double x) {
		if (x <= 0) {
			return new Translation2d(this);
		} else if (x >= 1) {
			return new Translation2d(other);
		}
		return extrapolate(other, x);
	}

	/**
	 * uses linear prediction to predict a point in the future based on two given points
	 * 
	 * @param other the second point
	 * @param x the distance into the future to extrapolate
	 * @return the extrapolated translation
	 * @see <a href="https://en.wikipedia.org/wiki/Linear_prediction">linear prediction</a>
	 */
	public Translation2d extrapolate(Translation2d other, double x) {
		return new Translation2d(x * (other.x_ - x_) + x_, x * (other.y_ - y_) + y_);
	}

	@Override
	public String toString() {
		final DecimalFormat fmt = new DecimalFormat("#0.000");
		return "(" + fmt.format(x_) + "," + fmt.format(y_) + ")";
	}
}
