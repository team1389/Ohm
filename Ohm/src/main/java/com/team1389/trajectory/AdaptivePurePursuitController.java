package com.team1389.trajectory;

import java.util.Optional;
import java.util.Set;

/**
 * Implements an adaptive pure pursuit controller. See:
 * 
 * 
 * Basically, we find a spot on the path we'd like to follow and calculate the
 * wheel speeds necessary to make us land on that spot. The target spot is a
 * specified distance ahead of us, and we look further ahead the greater our
 * tracking error.
 * 
 * @see <a href=
 *      "https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4.pdf">white_paper</a>
 */
public class AdaptivePurePursuitController {
	private static final double kEpsilon = 1E-9;

	double mFixedLookahead;
	Path mPath;
	RigidTransform2d.Delta mLastCommand;
	double mLastTime;
	double mMaxAccel;
	double mDt;
	boolean mReversed;
	double mPathCompletionTolerance;

	/**
	 * @param fixed_lookahead
	 *            the fixed distance to look down the path TODO is this used all
	 *            the time?
	 * @param max_accel
	 *            the max acceleration of the robot
	 * @param nominal_dt
	 *            update rate when code is running at nominal efficiency TODO
	 *            what is this used for
	 * @param path
	 *            the path to follow
	 * @param reversed
	 *            whether to follow the path backwards
	 * @param path_completion_tolerance
	 *            the acceptable remaining length for us to decide we're
	 *            finished following the path
	 */
	public AdaptivePurePursuitController(double fixed_lookahead, double max_accel, double nominal_dt, Path path,
			boolean reversed, double path_completion_tolerance) {
		mFixedLookahead = fixed_lookahead;
		mMaxAccel = max_accel;
		mPath = path;
		mDt = nominal_dt;
		mLastCommand = null;
		mReversed = reversed;
		mPathCompletionTolerance = path_completion_tolerance;
	}

	/**
	 * @return whether we're finished following the path
	 */
	public boolean isDone() {
		double remainingLength = mPath.getRemainingLength();
		return remainingLength <= mPathCompletionTolerance;
	}

	/**
	 * updates the controller, returning a desired robot velocity based on the
	 * current robot pose
	 * 
	 * @param robot_pose
	 *            the current robot pose
	 * @param now
	 *            the current time in millis
	 * @return the desired robot velocity
	 */
	public RigidTransform2d.Delta calculate(RigidTransform2d robot_pose, double now) {
		RigidTransform2d pose = robot_pose;
		if (mReversed) {
			pose = new RigidTransform2d(robot_pose.getTranslation(),
					robot_pose.getRotation().rotateBy(Rotation2d.fromRadians(Math.PI)));
		}

		double distance_from_path = mPath.update(robot_pose.getTranslation());
		if (this.isDone()) {
			return new RigidTransform2d.Delta(0, 0, 0);
		}

		PathSegment.Sample lookahead_point = mPath.getLookaheadPoint(robot_pose.getTranslation(),
				distance_from_path + mFixedLookahead);
		Optional<Circle> circle = joinPath(pose, lookahead_point.translation);

		double speed = lookahead_point.speed;
		if (mReversed) {
			speed *= -1;
		}
		// Ensure we don't accelerate too fast from the previous command
		double dt = now - mLastTime;
		if (mLastCommand == null) {
			mLastCommand = new RigidTransform2d.Delta(0, 0, 0);
			dt = mDt;
		}
		double accel = (speed - mLastCommand.dx) / dt;
		if (accel < -mMaxAccel) {
			speed = mLastCommand.dx - mMaxAccel * dt;
		} else if (accel > mMaxAccel) {
			speed = mLastCommand.dx + mMaxAccel * dt;
		}

		// Ensure we slow down in time to stop
		// vf^2 = v^2 + 2*a*d
		// 0 = v^2 + 2*a*d
		double remaining_distance = mPath.getRemainingLength();
		double max_allowed_speed = Math.sqrt(2 * mMaxAccel * remaining_distance);
		System.out.println(max_allowed_speed);
		if (Math.abs(speed) > max_allowed_speed) {
			speed = max_allowed_speed * Math.signum(speed);
		}
		final double kMinSpeed = 4.0;
		if (Math.abs(speed) < kMinSpeed) {
			// Hack for dealing with problems tracking very low speeds with
			// Talons
			speed = kMinSpeed * Math.signum(speed);
		}

		RigidTransform2d.Delta rv;
		if (circle.isPresent()) {
			rv = new RigidTransform2d.Delta(speed, 0,
					(circle.get().turn_right ? -1 : 1) * Math.abs(speed) / circle.get().radius);
		} else {
			rv = new RigidTransform2d.Delta(speed, 0, 0);
		}
		mLastTime = now;
		mLastCommand = rv;
		return rv;
	}

	/**
	 * @return a list of the path markers that have already been traversed
	 */
	public Set<String> getMarkersCrossed() {
		return mPath.getMarkersCrossed();
	}

	/**
	 * represents a circle used for turning calculations TODO figure out how
	 * this is used
	 */
	public static class Circle {
		/**
		 * the center of the circle
		 */
		public final Translation2d center;
		/**
		 * the radius of the circle
		 */
		public final double radius;
		/**
		 * whether to follow the circle right or left
		 */
		public final boolean turn_right;

		/**
		 * @param center
		 *            the center of the circle
		 * @param radius
		 *            the radius of the circle
		 * @param turn_right
		 *            whether to follow the circle right or left
		 */
		public Circle(Translation2d center, double radius, boolean turn_right) {
			this.center = center;
			this.radius = radius;
			this.turn_right = turn_right;
		}
	}

	/**
	 * TODO what does this do
	 * 
	 * @param robot_pose
	 *            current robot position
	 * @param lookahead_point
	 *            the point to tune based on //TODO 254?
	 * @return a circle...? //TODO 254?
	 */
	public static Optional<Circle> joinPath(RigidTransform2d robot_pose, Translation2d lookahead_point) {
		double x1 = robot_pose.getTranslation().getX();
		double y1 = robot_pose.getTranslation().getY();
		double x2 = lookahead_point.getX();
		double y2 = lookahead_point.getY();

		Translation2d pose_to_lookahead = robot_pose.getTranslation().inverse().translateBy(lookahead_point);
		double cross_product = pose_to_lookahead.getX() * robot_pose.getRotation().sin()
				- pose_to_lookahead.getY() * robot_pose.getRotation().cos();
		if (Math.abs(cross_product) < kEpsilon) {
			return Optional.empty();
		}

		double dx = x1 - x2;
		double dy = y1 - y2;
		double my = (cross_product > 0 ? -1 : 1) * robot_pose.getRotation().cos();
		double mx = (cross_product > 0 ? 1 : -1) * robot_pose.getRotation().sin();

		double cross_term = mx * dx + my * dy;

		if (Math.abs(cross_term) < kEpsilon) {
			// Points are colinear
			return Optional.empty();
		}

		return Optional.of(new Circle(
				new Translation2d((mx * (x1 * x1 - x2 * x2 - dy * dy) + 2 * my * x1 * dy) / (2 * cross_term),
						(-my * (-y1 * y1 + y2 * y2 + dx * dx) + 2 * mx * y1 * dx) / (2 * cross_term)),
				.5 * Math.abs((dx * dx + dy * dy) / cross_term), cross_product > 0));
	}

}