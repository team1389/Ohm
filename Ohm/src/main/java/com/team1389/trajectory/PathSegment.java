package com.team1389.trajectory;

/**
 * A PathSegment consists of two Translation2d objects (the start and end
 * points) as well as the speed of the robot.
 *
 */
public class PathSegment {
	protected static final double kEpsilon = 1E-9;

	/**
	 * TODO where is this used?
	 */
	public static class Sample {
		/**
		 * the position of the sample
		 */
		public final Translation2d translation;
		/**
		 * the speed at the given position
		 */
		public final double speed;

		/**
		 * @param translation
		 *            the position of the sample
		 * @param speed
		 *            the speed at the given position
		 */
		public Sample(Translation2d translation, double speed) {
			this.translation = translation;
			this.speed = speed;
		}
	}

	// TODO what is this speed
	protected double mSpeed;
	protected Translation2d mStart;
	protected Translation2d mEnd;
	protected Translation2d mStartToEnd; // pre-computed for efficiency
	protected double mLength; // pre-computed for efficiency

	/**
	 * a collection of information on the closest point on the path segment
	 */
	public static class ClosestPointReport {
		/**
		 * index of the point on the path segment (unclamped)
		 */
		public double index;

		/**
		 * index of the point (clamped to [0, 1])
		 */
		public double clamped_index;

		/**
		 * the result of interpolate(clamped_index)
		 */
		public Translation2d closest_point;

		/**
		 * the distance from the closest point to the query point
		 */
		public double distance;
	}

	/**
	 * @param start
	 *            the initial position of the robot
	 * @param end
	 *            the position of the robot after travelling this segment
	 * @param speed
	 *            the speed of the robot
	 */
	public PathSegment(Translation2d start, Translation2d end, double speed) {
		mEnd = end;
		mSpeed = speed;
		updateStart(start);
	}

	/**
	 * @param new_start
	 *            the new initial position of the robot for this segment
	 */
	public void updateStart(Translation2d new_start) {
		mStart = new_start;
		mStartToEnd = mStart.inverse().translateBy(mEnd);
		mLength = mStartToEnd.norm();
	}

	/**
	 * @return the speed during this segment
	 */
	public double getSpeed() {
		return mSpeed;
	}

	/**
	 * @return the position of the robot before traversing the segment
	 */
	public Translation2d getStart() {
		return mStart;
	}

	/**
	 * @return the position of the robot at the end of the segment
	 */
	public Translation2d getEnd() {
		return mEnd;
	}

	/**
	 * @return the arclength of the segment
	 */
	public double getLength() {
		return mLength;
	}

	/**
	 * estimates the position of the robot at a given distance along the path
	 * 
	 * @param index
	 *            the distance along the segment as a percent (from 0 to 1)
	 * @return the position of the robot at the given distance
	 */
	public Translation2d interpolate(double index) {
		return mStart.interpolate(mEnd, index);
	}

	/**
	 * takes the dot of the vector from start to other and the vector from start
	 * to end
	 * 
	 * @param other
	 *            a point between start and end on the path
	 * @return the dot of the two vectors
	 */
	public double dotProduct(Translation2d other) {
		Translation2d start_to_other = mStart.inverse().translateBy(other);
		return mStartToEnd.getX() * start_to_other.getX() + mStartToEnd.getY() * start_to_other.getY();
	}

	/**
	 * @param query_point
	 *            the point to examine
	 * @return information about the closest real point on the path TODO what
	 *         does this do?
	 */
	public ClosestPointReport getClosestPoint(Translation2d query_point) {
		ClosestPointReport rv = new ClosestPointReport();
		if (mLength > kEpsilon) {
			double dot_product = dotProduct(query_point);
			rv.index = dot_product / (mLength * mLength);
			rv.clamped_index = Math.min(1.0, Math.max(0.0, rv.index));
			rv.closest_point = interpolate(rv.index);
		} else {
			rv.index = rv.clamped_index = 0.0;
			rv.closest_point = new Translation2d(mStart);
		}
		rv.distance = rv.closest_point.inverse().translateBy(query_point).norm();
		return rv;
	}
}