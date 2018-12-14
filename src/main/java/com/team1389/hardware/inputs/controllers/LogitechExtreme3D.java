package com.team1389.hardware.inputs.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.team1389.hardware.inputs.hardware.JoystickHardware;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;

public class LogitechExtreme3D extends JoystickHardware {
	public LogitechExtreme3D(int port) {
		super(port);
	}

	public PercentIn yaw() {
		return getAxis(2);
	}

	public PercentIn yAxis() {
		return getAxis(1);
	}

	public PercentIn xAxis() {
		return getAxis(0);
	}

	public PercentIn throttle() {
		return getAxis(3);
	}

	Supplier<Integer> hatSwitch() {
		return getPov();
	}

	public DigitalIn thumbButton() {
		return getButton(2);
	}

	public DigitalIn trigger() {
		return getButton(1);
	}

	private static final List<Integer> panelButtonPorts = Arrays.asList(new Integer[] { 5, 6, 3, 4 });

	/**
	 * 1-4 top left: 1 top right: 2 bot left: 3 bot right: 4
	 * @return
	 */
	public DigitalIn panelButton(int port) {
		if (!panelButtonPorts.contains(port))
			throw new IllegalArgumentException("chosen port is not a panel button");
		return getButton(port);
	}

	private static final List<Integer> baseButtonPorts = Arrays.asList(new Integer[] { 7, 8, 9, 10, 11, 12 });

	/**
	 * 7-12
	 * @return
	 */
	public DigitalIn baseButton(int port) {
		if (!baseButtonPorts.contains(port))
			throw new IllegalArgumentException("chosen port is not a base button");
		return getButton(port);
	}
}
