package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * drive system that can switch between curvature and tank drive
 * 
 */
public class CheesyDriveSystem extends DriveSystem
{
	private DriveOut<Percent> drive;
	private PercentIn throttle;
	private PercentIn wheel;
	private DigitalIn quickTurnButton;

	private DriveSignal mSignal = DriveSignal.NEUTRAL;

	private double mQuickStopAccumulator;
	private double kTurnSensitivity;

	/**
	 * 
	 * @param drive
	 *            percent of voltage going to left/right motors
	 * @param throttle
	 *            percent of desired speed(forward/back)
	 * @param wheel
	 *            percent used for turning(l/r)
	 * @param quickTurnButton
	 *            switching from curvature drive, to tank drive
	 * @param turnSensitivity
	 *            severity of turn
	 */
	public CheesyDriveSystem(DriveOut<Percent> drive, PercentIn throttle, PercentIn wheel, DigitalIn quickTurnButton,
			double turnSensitivity)
	{
		this.drive = drive;
		this.throttle = throttle;
		this.wheel = wheel;
		this.quickTurnButton = quickTurnButton;
		this.kTurnSensitivity = turnSensitivity;
	}

	/**
	 * 
	 * @param drive
	 *            percent of voltage going to left/right motors
	 * @param throttle
	 *            percent of desired speed(forward/back)
	 * @param wheel
	 *            percent used for turning(l/r)
	 * @param quickTurnButton
	 *            switching from curvature drive, to tank drive
	 */
	public CheesyDriveSystem(DriveOut<Percent> drive, PercentIn throttle, PercentIn wheel, DigitalIn quickTurnButton)
	{
		this(drive, throttle, wheel, quickTurnButton, 1.0);
	}

	/**
	 * if throttle or wheel is changed, all commands are cleared
	 */
	@Override
	public void init()
	{
		// throttle.addChangeListener(COMMAND_CANCEL);
		// wheel.addChangeListener(COMMAND_CANCEL);
	}

	/**
	 * expects to be called every tic, updates values of throttle, wheel,
	 * quickTurnButton, and brakemode
	 */
	@Override
	public void update()
	{
		mSignal = cheesyDrive(throttle.get(), wheel.get(), quickTurnButton.get());
		drive.set(mSignal);
	}

	/**
	 * return key
	 */
	@Override
	public String getName()
	{
		return "Drive System";
	}

	/**
	 * add watchables for voltage being applied to the motors, and the quickturn
	 * button
	 */
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return stem.put(drive, quickTurnButton.getWatchable("quickTurnButton"));
	}

	/**
	 * 
	 * @param val
	 *            that kTurnSensitivity is being passed to
	 */
	public void setTurnSensitivity(double val)
	{
		this.kTurnSensitivity = val;
	}

	/**
	 * 
	 * @param throttle
	 *            percent of desired speed(forward/back)
	 * @param wheel
	 *            percent used for turning(l/r)
	 * @param isQuickTurn
	 *            whether the drivemode is tank drive
	 * @return amount of voltage going to l/r motors, and the break mode
	 */
	public DriveSignal cheesyDrive(double throttle, double wheel, boolean isQuickTurn)
	{

		double overPower;

		double angularPower;

		if (isQuickTurn)
		{
			if (Math.abs(throttle) < 0.2)
			{
				double alpha = 0.1;
				mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + alpha * wheel * 2;
			}
			overPower = 1.0;
			angularPower = wheel;
		} else
		{
			overPower = 0.0;
			angularPower = Math.abs(throttle) * wheel * kTurnSensitivity - mQuickStopAccumulator;
			if (mQuickStopAccumulator > 1)
			{
				mQuickStopAccumulator -= 1;
			} else if (mQuickStopAccumulator < -1)
			{
				mQuickStopAccumulator += 1;
			} else
			{
				mQuickStopAccumulator = 0.0;
			}
		}

		double rightPwm = throttle - angularPower;
		double leftPwm = throttle + angularPower;
		if (leftPwm > 1.0)
		{
			rightPwm -= overPower * (leftPwm - 1.0);
			leftPwm = 1.0;
		} else if (rightPwm > 1.0)
		{
			leftPwm -= overPower * (rightPwm - 1.0);
			rightPwm = 1.0;
		} else if (leftPwm < -1.0)
		{
			rightPwm += overPower * (-1.0 - leftPwm);
			leftPwm = -1.0;
		} else if (rightPwm < -1.0)
		{
			leftPwm += overPower * (-1.0 - rightPwm);
			rightPwm = -1.0;
		}
		mSignal.rightMotor = rightPwm;
		mSignal.leftMotor = leftPwm;
		return mSignal;
	}

}