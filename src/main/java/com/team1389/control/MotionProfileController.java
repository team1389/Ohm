package com.team1389.control;

import com.team1389.command_framework.command_base.Command;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.motion_profile.MotionProfile;
import com.team1389.util.Timer;

/**
 * a controller that uses PID to follow motion profiles
 * 
 * @author amind
 *
 */
public class MotionProfileController extends SynchronousPIDController<Percent, Position> {
	MotionProfile following;
	double startPos;
	RangeIn<Speed> vel;
	Timer timer;

	/**
	 * @param kP the proportional gain for the PID controller
	 * @param kI the integral gain for the PID controller
	 * @param kD the derivative gain for the PID controller
	 * @param source a position input stream
	 * @param vel a speed input stream
	 * @param output a percent output
	 */
	public MotionProfileController(double kP, double kI, double kD, double kF, RangeIn<Position> source,
			RangeIn<Speed> vel, RangeOut<Percent> output) {
		super(kP, kI, kD, kF, source, output);
		this.vel = vel;
		startPos = 0;
		timer = new Timer();
	}

	/**
	 * stores the given profile to pursue <br>
	 * <em>NOTE</em>: the controller must be updated periodically to follow the profile as expected
	 * 
	 * @param prof the profile to follow
	 */
	public void followProfile(MotionProfile prof) {
		this.following = prof;
		startPos = source.get();
		timer.zero();
	}

	@Override
	public void update() {
		double time = timer.get();
		if (!isFinished()) {
			super.setSetpoint(following.getPosition(time) + startPos);
		}
		super.update();
	}

	public boolean isFinished() {
		double time = timer.get();
		return following == null || following.isFinished(time);
	}

	/**
	 * 
	 * @param tolerance
	 * @return Whether the controller is within tolerance with the last point of the motion profile
	 */
	@Override
	public boolean onTarget(double tolerance) {
		return m_last_input != Double.NaN
				&& Math.abs((following.getEndPosition() + startPos) - m_last_input) < tolerance;
	}

	/**
	 * 
	 * @param tolerance
	 * @return Whether the controller has been within tolerance of the endpoint for the last five ticks
	 */
	@Override
	public boolean onTargetStable(double tolerance) {
		return onTarget(tolerance) && super.onTargetStable(tolerance);
	}
	
	/**
	 * 
	 * @param tolerance
	 * @return Whether the controller is within tolerance with the current setpoint
	 */
	public boolean isTracking(double tolerance){
		return super.onTarget(tolerance);
	}

	public RangeIn<Speed> getSpeedIn() {
		return vel;
	}

	public Command followProfileCommand(MotionProfile prof) {
		return new Command() {
			@Override
			protected void initialize() {
				followProfile(prof);
			}

			@Override
			protected boolean execute() {
				update();
				return isFinished();
			}

		};
	}
}
