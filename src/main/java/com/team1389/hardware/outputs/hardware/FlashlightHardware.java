package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.DigitalOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PCM;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * a digital flashlight controller for a light attached to the PCM
 * 
 * @author amind
 *
 */
public class FlashlightHardware extends Hardware<PCM> {
	/**
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public FlashlightHardware(PCM requestedPort, Registry registry) {
		super(requestedPort, registry);
	}

	private Optional<Solenoid> wpiSolenoid;

	/**
	 * 
	 * @return a boolean output stream that controls the state of the light (on or off)
	 */
	public DigitalOut getDigitalOut() {
		return new DigitalOut(pos -> wpiSolenoid.ifPresent(s -> s.set(pos)));
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(getDigitalOut().getWatchable("light"));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Light";
	}

	@Override
	public void init(PCM port) {
		this.wpiSolenoid = Optional.of(new Solenoid(port.index()));
	}

	@Override
	public void failInit() {
		wpiSolenoid = Optional.empty();
	}
}
