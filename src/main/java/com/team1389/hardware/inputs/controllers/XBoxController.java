package com.team1389.hardware.inputs.controllers;

import com.team1389.hardware.inputs.hardware.JoystickHardware;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;

public class XBoxController extends JoystickHardware {

	public XBoxController(int port) {
		super(port);
	}

	public final XBoxStick leftStick = new XBoxStick(0, 1, 9);
	public final XBoxStick rightStick = new XBoxStick(4, 5, 10);

	public class XBoxStick {
		private int xAxis, yAxis, stickButton;

		protected XBoxStick(int xAxis, int yAxis, int stickButton) {
			this.xAxis = xAxis;
			this.yAxis = yAxis;
			this.stickButton = stickButton;
		}

		public DigitalIn button() {
			return getButton(stickButton);
		}

		public PercentIn xAxis() {
			return getAxis(xAxis);
		}

		public PercentIn yAxis() {
			return getAxis(yAxis);
		}
	}

	public PercentIn leftTrigger() {
		return getAxis(2);
	}

	public PercentIn rightTrigger() {
		return getAxis(3);
	}

	public DigitalIn aButton() {
		return getButton(1);
	}

	public DigitalIn bButton() {
		return getButton(2);
	}

	public DigitalIn xButton() {
		return getButton(3);
	}

	public DigitalIn yButton() {
		return getButton(4);
	}

	public DigitalIn leftBumper() {
		return getButton(5);
	}

	public DigitalIn rightBumper() {
		return getButton(6);
	}

	public DigitalIn upArrow() {
		return new DigitalIn(() -> wpiJoystick.getPOV(0) == 0);
	}

	public DigitalIn leftArrow() {
		return new DigitalIn(() -> wpiJoystick.getPOV(0) == 270);
	}

	public DigitalIn rightArrow() {
		return new DigitalIn(() -> wpiJoystick.getPOV(0) == 90);
	}

	public DigitalIn downArrow() {
		return new DigitalIn(() -> wpiJoystick.getPOV(0) == 180);
	}

	public DigitalIn startButton() {
		return getButton(8);
	}

	public DigitalIn backButton() {
		return getButton(7);
	}
}
