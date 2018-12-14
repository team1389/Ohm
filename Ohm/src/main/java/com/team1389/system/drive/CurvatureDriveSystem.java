package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

public class CurvatureDriveSystem extends DriveSystem
{
	protected DriveOut<Percent> drive;
	protected PercentIn throttle;
	protected PercentIn wheel;
	protected DigitalIn quickTurnButton;
	public CurvatureDriveAlgorithm calc;

	private DriveSignal mSignal = DriveSignal.NEUTRAL;

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
	public CurvatureDriveSystem(DriveOut<Percent> drive, PercentIn throttle, PercentIn wheel, DigitalIn quickTurnButton)
	{
		this(drive, throttle, wheel, quickTurnButton, 1.0, 1.0);

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
	 * @param turnSensitivity
	 *            severity of turn
	 */
	public CurvatureDriveSystem(DriveOut<Percent> drive, PercentIn throttle, PercentIn wheel, DigitalIn quickTurnButton,
			double turnSensitivity, double spinSensitivity)
	{
		this.drive = drive;
		this.throttle = throttle;
		this.wheel = wheel;
		this.quickTurnButton = quickTurnButton;
		calc = new CurvatureDriveAlgorithm(turnSensitivity, spinSensitivity);
	}

	/**
	 * if throttle or wheel is changed, all commands are cleared
	 */
	@Override
	public void init()
	{
	}

	/**
	 * expects to be called every tic, updates values of throttle, wheel,
	 * quickTurnButton, and brakemode
	 */
	@Override
	public void update()
	{

		mSignal = calc.calculate(throttle.get(), wheel.get(), quickTurnButton.get());
		drive.set(mSignal);

	}

	/**
	 * return key
	 */
	@Override
	public String getName()
	{
		return "Curvature Drive System";
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

}
