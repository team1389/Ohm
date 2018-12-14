package com.team1389.trajectory;

import java.io.File;
import java.util.Arrays;

import com.team1389.command_framework.command_base.Command;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.system.drive.DriveOut;
import com.team1389.system.drive.DriveSignal;
import com.team1389.system.drive.DriveSystem;
import com.team1389.util.Timer;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.modifiers.TankModifier;

/**
 * drive system that follows paths autonomously using the Adaptive Pure Pursuit
 * Controller
 * 
 * @see AdaptivePurePursuitController
 * @author amind
 *
 */
public class PathFollowingSystem extends DriveSystem {
	/**
	 * the default time between updates
	 */
	public static final double UPDATE_DT = 1.0 / 20;
	private DistanceFollower leftDistance, rightDistance;
	private RangeIn<Position> leftPos, rightPos;
	private DriveOut<Percent> wheelVoltageOut;
	private Constants constants;
	private AngleIn<Position> heading;
	private boolean reversedPath;
	private double headingOffset;

	public PathFollowingSystem(DriveOut<Percent> voltage, RangeIn<Position> leftPos, RangeIn<Position> rightPos,
			AngleIn<Position> heading, Constants constants) {
		this.leftPos = leftPos;
		this.rightPos = rightPos;
		this.wheelVoltageOut = voltage;
		this.heading = heading.copy().setRange(-180, 180).getWrapped();
		this.constants = constants;
	}

	/**
	 * The robot follows a set path, which is defined by Waypoint objects.
	 * 
	 * @param path
	 *            the path to follow
	 * @param reversed
	 *            whether to follow the path in reverse
	 * @see Path
	 */
	public synchronized void followPath(Trajectory path, boolean reversed) {
		this.reversedPath = reversed;
		TankModifier modifier = new TankModifier(path).modify(constants.trackWidth);
		leftDistance = new DistanceFollower(modifier.getLeftTrajectory());
		rightDistance = new DistanceFollower(modifier.getRightTrajectory());
		constants.configureFollowers(leftDistance, rightDistance);
	}

	/**
	 * @return Returns if the robot mode is Path Following Control and the set path
	 *         is complete.
	 */
	public synchronized boolean isFinishedPath() {
		return (leftDistance == null || rightDistance == null)
				|| (leftDistance.isFinished() && rightDistance.isFinished());
	}

	/**
	 * updates the pathFollower, setting the speeds of the wheels based on the
	 * robot's current pose
	 */
	public void update() {
		if (!isFinishedPath()) {
			double reversed = reversedPath ? -1 : 1;
			double l = reversed * leftDistance.calculate(reversed * leftPos.get());
			double r = reversed * rightDistance.calculate(reversed * rightPos.get());
			double gyro_heading = heading.get() - headingOffset;
			double desired_heading = Pathfinder.r2d(leftDistance.getHeading());
			double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);

			double turn = constants.gyroP * angleDifference;
			wheelVoltageOut.set((l - turn), (r + turn));
		} else {
			wheelVoltageOut.set(DriveSignal.NEUTRAL);
		}
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(wheelVoltageOut, heading.getWatchable("angle"), leftPos.getWatchable("leftPos"),
				rightPos.getWatchable("rightPos"));
	}

	@Override
	public String getName() {
		return "Path follower";
	}

	@Override
	public void init() {

	}

	public static class Constants {
		public final double maxJerk, maxAccel, maxVel, pathP, pathD, pathA, gyroP, trackWidth;

		public Constants(double maxJerk, double maxAccel, double maxVel, double pathP, double pathD, double pathA,
				double gyroP, double trackWidth) {
			this.maxAccel = maxAccel;
			this.maxJerk = maxJerk;
			this.maxVel = maxVel;
			this.pathP = pathP;
			this.pathD = pathD;
			this.pathA = pathA;
			this.gyroP = gyroP;
			this.trackWidth = trackWidth;
		}

		private void configureFollower(DistanceFollower follower) {
			follower.configurePIDVA(pathP, 0.0, pathD, 1 / maxVel, pathA);
		}

		public void configureFollowers(DistanceFollower... followers) {
			Arrays.stream(followers).forEach(this::configureFollower);
		}
	}

	public static Trajectory loadFromFile(String name) {
		System.out.println("loading path from file...");
		Timer timer = new Timer();
		Trajectory path = Pathfinder.readFromCSV(new File("paths/path.csv"));
		System.out.println("path loaded in " + timer.get() + " secs");
		return path;
	}

	public Trajectory generateTrajectory(Waypoint[] points) {
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC,
				Trajectory.Config.SAMPLES_HIGH, UPDATE_DT, constants.maxVel, constants.maxAccel, constants.maxJerk);
		Trajectory generate = Pathfinder.generate(points, config);
		return generate;
	}

	public class PathFollowCommand extends Command {
		private Trajectory path;
		private boolean reversed;
		private double heading;

		public PathFollowCommand(Trajectory path, boolean reversed, double heading) {
			this.path = path;
			this.reversed = reversed;
			this.heading = heading;
		}

		public PathFollowCommand(Waypoint[] points, boolean reversed, double headingOffset) {
			this(generateTrajectory(points), reversed, headingOffset);
		}

		public PathFollowCommand(String filename, boolean reversed, double headingOffset) {
			this(loadFromFile(filename), reversed, headingOffset);
		}

		@Override
		protected void initialize() {
			System.out.println("starting path");
			headingOffset = heading;
			followPath(path, reversed);
		}

		@Override
		protected boolean execute() {
			update();
			return isFinishedPath();
		}

		@Override
		protected void done() {
			wheelVoltageOut.set(DriveSignal.NEUTRAL);
		}

	}
}
