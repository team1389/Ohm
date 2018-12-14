package com.team1389.system.drive;

public class CurvatureDriveAlgorithm
{
	private double mQuickStopAccumulator;
	private double kTurnSensitivity;

	private double kSpinSensitivity;

	public CurvatureDriveAlgorithm(double turnSensitivity, double spinSensitivity)
	{
		this.kTurnSensitivity = turnSensitivity;
		this.kSpinSensitivity = spinSensitivity;
	}

	public void setCurveSensitivity(double kTurnSensitivity)
	{
		this.kTurnSensitivity = kTurnSensitivity;
	}

	public void setSpinSensitivity(double kSpinSensitivity)
	{
		this.kSpinSensitivity = kSpinSensitivity;
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
	public DriveSignal calculate(double throttle, double wheel, boolean isQuickTurn)
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
			angularPower = wheel * kSpinSensitivity;
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
		return new DriveSignal(leftPwm, rightPwm);
	}

}
