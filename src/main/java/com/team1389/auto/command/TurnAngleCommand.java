package com.team1389.auto.command;

import com.team1389.command_framework.command_base.Command;
import com.team1389.configuration.PIDConstants;
import com.team1389.controllers.SynchronousPIDController;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Value;
import com.team1389.system.drive.DriveOut;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * this commmand turns an actuator a given angle
 * 
 * @author amind
 *
 * @param <O> the ouput type
 */
public class TurnAngleCommand<O extends Value> extends Command {
	SynchronousPIDController<O, Position> pid;
	AngleIn<Position> angleIn;
	double angle;
	double tolerance;
	boolean isAbsolute;

	/**
	 * 
	 * @param angle the angle to turn
	 * @param isAbsoluteAngle whether the given angle is an absolute angle, or relative to the
	 *            acuator's starting angle
	 * @param tolerance the tolerance around the target angle in degrees
	 * @param angleVal an angle input that represents the actuator's angle
	 * @param output an output that control's the actuator's movement
	 * @param turnPID the pid constants for the angle turning controller
	 */
	public TurnAngleCommand(double angle, boolean isAbsoluteAngle, double tolerance, RangeIn<Position> angleVal,
			RangeOut<O> output, PIDConstants turnPID) {
		pid = new SynchronousPIDController<O, Position>(turnPID, angleVal, output);
		pid.setInputRange(-Double.MAX_VALUE, Double.MAX_VALUE);
		this.angle = angle;
		this.tolerance = tolerance;
		this.isAbsolute = isAbsoluteAngle;
	}

	/**
	 * assumes angle is relative to actuator's starting angle
	 * 
	 * @param angle the angle to turn
	 * @param tolerance the tolerance around the target angle in degrees
	 * @param angleVal an angle input that represents the actuator's angle
	 * @param output an output that control's the actuator's movement
	 * @param turnPID the pid constants for the angle turning controller
	 */
	public TurnAngleCommand(double angle, double tolerance, AngleIn<Position> angleVal, RangeOut<O> output,
			PIDConstants turnPID) {
		this(angle, false, tolerance, angleVal, output, turnPID);
		this.angleIn = angleVal;

	}

	@Override
	public void initialize() {
		if (!isAbsolute) {
			angle += pid.getSource().get();
		}
		pid.setSetpoint(angle);
		pid.setErrQue(25);
	}

	@Override
	public boolean execute() {
		pid.update();
		SmartDashboard.putNumber("val", pid.getSource().get());
		SmartDashboard.putNumber("error", pid.getError());
		return pid.onTargetStable(tolerance);
	}
	
	@Override
	public void done(){
		pid.getOutput().set(0);
	}
	public static <T extends Value> RangeOut<T> createTurnController(DriveOut<T> asTank) {
		return asTank.left().copy().getWithAddedFollowers(asTank.right().copy().getInverted());
	}

}
