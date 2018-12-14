package com.team1389.system.drive;

import com.team1389.system.drive.DriveSignal;

public class SixWheelSignal
{
	private final double topLeft;
	private final double topRight;
	private final double middleLeft;
	private final double middleRight;
	private final double bottomLeft;
	private final double bottomRight;
	public static SixWheelSignal NEUTRAL = new SixWheelSignal(0, 0, 0, 0, 0, 0);

	public SixWheelSignal(double topLeft, double topRight, double middleLeft, double middleRight, double bottomLeft,
			double bottomRight)
	{
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		this.middleLeft = middleLeft;
		this.middleRight = middleRight;
		this.topLeft = topLeft;
		this.topRight = topRight;
	}

	public DriveSignal getBottomWheels()
	{
		return new DriveSignal(bottomLeft, bottomRight);
	}

	public DriveSignal getMiddleWheels()
	{
		return new DriveSignal(middleLeft, middleRight);
	}

	public DriveSignal getTopWheels()
	{
		return new DriveSignal(topLeft, topRight);
	}

	public String toString()
	{
		return "LV: " + topLeft + "RV: " + topRight + "MLV: " + middleLeft + "MRV: " + middleRight + "BLV: "
				+ bottomLeft + "BRV: " + bottomRight;
	}

}