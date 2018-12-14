package com.team1389.auto.command;

import com.team1389.command_framework.command_base.Command;
import com.team1389.configuration.PIDConstants;
import com.team1389.control.SynchronousPID;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.motion_profile.MotionProfile;
import com.team1389.motion_profile.ProfileUtil;
import com.team1389.system.drive.DriveOut;
import com.team1389.system.drive.DriveSignal;
import com.team1389.util.Timer;

import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * command to move to a setpoint for tank
 * 
 * @author Kenneth
 *
 */
public class DriveStraightCommand extends Command {
	SynchronousPID left, right, gyro;
	DriveOut<Percent> drive;
	RangeIn<Position> leftPos, rightPos;
	AngleIn<Position> heading;
	MotionProfile profile;
	Timer timer;
	double leftStart, rightStart, dist, tolerance;

	public DriveStraightCommand(PIDConstants constants, PIDConstants gyroConstants, DriveOut<Percent> drive,
			RangeIn<Position> left, RangeIn<Position> right, AngleIn<Position> gyro, double dx, double maxAccel,
			double maxDecel, double maxSpeed, double tolerance) {
		this.left = new SynchronousPID(constants.p, constants.i, constants.d, constants.f, PIDSourceType.kDisplacement);
		this.right = new SynchronousPID(constants.p, constants.i, constants.d, constants.f,
				PIDSourceType.kDisplacement);
		this.gyro = new SynchronousPID(gyroConstants.p, gyroConstants.i, gyroConstants.d, gyroConstants.f,
				PIDSourceType.kDisplacement);
		this.gyro.setInputRange(-180, 180);
		this.gyro.setOutputRange(-1, 1);
		this.dist = dx;
		this.leftPos = left;
		this.rightPos = right;
		this.drive = drive;
		this.heading = gyro;
		profile = ProfileUtil.trapezoidal(dx, 0, maxAccel, maxDecel, maxSpeed);
		timer = new Timer();
		this.tolerance = tolerance;
	}

	@Override
	protected void initialize() {
		System.out.println("initializing command");
		timer.zero();
		leftStart = leftPos.get();
		rightStart = rightPos.get();
		gyro.setSetpoint(heading.get());
	}

	@Override
	protected boolean execute() {
		System.out.println("going");
		double setpoint = profile.getPosition(timer.get());
		left.setSetpoint(setpoint + leftStart);
		right.setSetpoint(setpoint + rightStart);
		double leftVal = left.calculate(leftPos.get());
		double rightVal = right.calculate(rightPos.get());
		double headingSet = gyro.calculate(heading.get());
		drive.set(leftVal - headingSet, rightVal + headingSet);
		return profile.isFinished(timer.get()) && left.onTarget(tolerance) && right.onTarget(tolerance);
	}

	@Override
	protected void done() {
		drive.set(DriveSignal.NEUTRAL);
	}

}
