package com.team1389.system.drive;

import com.team1389.system.drive.DriveSignal;

public class FourWheelSignal {
	private final double topLeft;
	private final double topRight;
	private final double bottomLeft;
	private final double bottomRight;
	public static FourWheelSignal NEUTRAL = new FourWheelSignal(0, 0, 0, 0);

	public FourWheelSignal(double topLeft, double topRight, double bottomLeft, double bottomRight) {
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
		this.topLeft = topLeft;
		this.topRight = topRight;
	}
	

	public DriveSignal getBottomWheels() {
		return new DriveSignal(bottomLeft, bottomRight);
	}

	public DriveSignal getTopWheels() {
		return new DriveSignal(topLeft, topRight);
	}

	public String toString() {
		return "LV: " + topLeft + "RV: " + topRight + "BLV: " + bottomLeft + "BRV: "
				+ bottomRight;
	}

}