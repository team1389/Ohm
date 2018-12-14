package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.DIO;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * represents any digital input (hall effect, infared proximity, limit switches, etc)
 * 
 * @author amind
 *
 */
public class SwitchHardware extends Hardware<DIO> {
	boolean inverted;

	/**
	 * 
	 * @param inverted whether to invert the sensor value stream
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public SwitchHardware(boolean inverted, DIO requestedPort, Registry registry) {
		this.inverted=inverted;
		attachHardware(requestedPort, registry);
	}

	/**
	 * assumes switch is not inverted
	 * 
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public SwitchHardware(DIO requestedPort, Registry registry) {
		this(false, requestedPort, registry);
	}

	Optional<DigitalInput> wpiSwitch;

	/**
	 * @return a boolean stream that tracks the value of the sensor
	 */
	public DigitalIn getSwitchInput() {
		return new DigitalIn(() -> wpiSwitch.map(this::getRawSwitch).orElse(false));
	}

	private boolean getRawSwitch(DigitalInput switchVal) {
		return inverted ^ switchVal.get();
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(getSwitchInput().getWatchable("val"));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Switch";
	}

	@Override
	public void init(DIO port) {
		DigitalInput input = new DigitalInput(port.index());
		wpiSwitch = Optional.of(input);
	}

	@Override
	public void failInit() {
		wpiSwitch = Optional.empty();
	}

}
