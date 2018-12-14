package com.team1389.hardware.inputs.hardware;

import java.util.function.Supplier;

import com.team1389.hardware.inputs.interfaces.BinaryInput;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.DigitalIn.InputFilter;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.outputs.software.PercentOut;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;

/**
 * offers stream sources for all values available from the WPILib joystick
 * 
 * @author amind
 *
 */
public class JoystickHardware {
	protected final Joystick wpiJoystick;

	/**
	 * @param port the port of the joystick (can be set from the driver station program)
	 */
	public JoystickHardware(int port) {
		wpiJoystick = new Joystick(port);
	}

	/**
	 * @param button the button port to check
	 * @return a boolean stream that tracks the current state of the button
	 */
	public DigitalIn getButton(int button) {
		return new DigitalIn(getRawButton(button));
	}

	/**
	 * @param button the button port to check
	 * @param filter a boolean filter to apply to the button stream
	 * @return a boolean stream that tracks the current state of the button
	 */
	public DigitalIn getButton(int button, InputFilter filter) {
		return new DigitalIn(getRawButton(button), filter);
	}

	/**
	 * @param button the button port to check
	 * @return a boolean stream of the button data
	 */
	public BinaryInput getRawButton(int button) {
		return () -> {
			return wpiJoystick.getRawButton(button);
		};
	}

	/**
	 * 
	 * @param axis the axis to track
	 * @return a percent stream that tracks the value of the axis
	 */
	public PercentIn getAxis(int axis) {
		return new PercentIn(() -> {
			return wpiJoystick.getRawAxis(axis);
		});
	}

	/**
	 * @return an Integer supplier that tracks the value of the joystick POV hat switch
	 */
	public Supplier<Integer> getPov() {
		return () -> {
			return wpiJoystick.getPOV(0);
		};
	}

	public PercentOut getRumbler() {
		return new PercentOut(v -> setRumble(v));
	}

	public void setRumble(double val) {
		setRumble(val, val);
	}

	/**
	 * @param left intensity from 0 to 1
	 * @param right intensity from 0 to 1
	 */
	public void setRumble(double left, double right) {
		setLeftRumble(left);
		setRightRumble(right);
	}

	/**
	 * @param strength intensity of rumble from 0 to 1
	 */
	public void setLeftRumble(double strength) {
		wpiJoystick.setRumble(RumbleType.kLeftRumble, (float) strength);
	}

	/**
	 * @param strength intensity of rumble from 0 to 1
	 */
	public void setRightRumble(double strength) {
		wpiJoystick.setRumble(RumbleType.kRightRumble, (float) strength);
	}
}