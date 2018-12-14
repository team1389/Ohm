package com.team1389.system.drive;

import com.team1389.control.SynchronousPID;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.trajectory.Rotation2d;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * a driveSystem that directs the robot towards a {@link VelocityHeadingSetpoint}
 * 
 * @author amind
 */
public class VelocityHeadingSystem extends DriveSystem {
	// TODO figure out how to use this drive algorithm
	protected DriveOut<Speed> drive;
	private AngleIn<Position> gyro;

	private double mLastHeadingErrorDegrees;
	private VelocityHeadingSetpoint velocityHeadingSetpoint_;
	private SynchronousPID velocityHeadingPid_;

	/**
	 * @param drive a speed controlled drive stream
	 * @param gyro a stream of robot heading
	 */
	public VelocityHeadingSystem(DriveOut<Speed> drive, AngleIn<Position> gyro) {
		this.drive = drive;
		this.gyro = gyro;
	}

	/**
	 * 
	 */
	public void update() {
		Rotation2d actualGyroAngle = Rotation2d.fromDegrees(gyro.get());

		mLastHeadingErrorDegrees = velocityHeadingSetpoint_.getHeading().rotateBy(actualGyroAngle.inverse())
				.getDegrees();

		double deltaSpeed = velocityHeadingPid_.calculate(mLastHeadingErrorDegrees);
		drive.set(velocityHeadingSetpoint_.getLeftSpeed() + deltaSpeed / 2,
				velocityHeadingSetpoint_.getRightSpeed() - deltaSpeed / 2);
	}

	/**
	 * sets the {@link VelocityHeadingSetpoint} of this driveSystem. assumes that the setpoint
	 * desires straight line motion
	 * 
	 * @param forward_inches_per_sec the desired wheel speed (applied to both wheels)
	 * @param headingSetpoint the desired heading
	 */
	public void setVelocityHeadingSetpoint(double forward_inches_per_sec, Rotation2d headingSetpoint) {
		velocityHeadingPid_.reset();
		velocityHeadingSetpoint_ = new VelocityHeadingSetpoint(forward_inches_per_sec, forward_inches_per_sec,
				headingSetpoint);
	}

	/**
	 * represents a desired set of wheel speeds and heading of the robot. Especially useful if the
	 * robot is negotiating a turn and to forecast the robot's location.
	 */
	public class VelocityHeadingSetpoint {
		private final double leftSpeed_;
		private final double rightSpeed_;
		private final Rotation2d headingSetpoint_;

		/**
		 * @param leftSpeed the desired speed of the left wheels
		 * @param rightSpeed the desired speed of the right wheels
		 * @param headingSetpoint the desired heading of the robot
		 */
		public VelocityHeadingSetpoint(double leftSpeed, double rightSpeed, Rotation2d headingSetpoint) {
			leftSpeed_ = leftSpeed;
			rightSpeed_ = rightSpeed;
			headingSetpoint_ = headingSetpoint;
		}

		/**
		 * @return the desired speed of the left wheels
		 */
		public double getLeftSpeed() {
			return leftSpeed_;
		}

		/**
		 * @return the desired speed of the right wheels
		 */
		public double getRightSpeed() {
			return rightSpeed_;
		}

		/**
		 * @return the desired heading of the robot
		 */
		public Rotation2d getHeading() {
			return headingSetpoint_;
		}
	}

	// TODO
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem;
	}

	@Override
	public String getName() {
		return "VelocityHeading";
	}

	@Override
	public void init() {

	}
}
