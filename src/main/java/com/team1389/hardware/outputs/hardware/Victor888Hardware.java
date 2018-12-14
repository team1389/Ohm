package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PWM;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.Victor;
/**
 * A victor 888 motor controller
 * 
 * @author Ari Mindell
 */
public class Victor888Hardware extends Hardware<PWM> {
	/**
	 * 
	 * @param inverted whether to invert the direction of the speed controller
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public Victor888Hardware(boolean inverted, PWM requestedPort, Registry registry) {
		this.inverted = inverted;
		attachHardware(requestedPort, registry);
	}

	boolean inverted;
	Optional<Victor> wpiVictor;

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(getVoltageOutput().getWatchable("voltage"));
	}
	/**
	 * @return a voltage output stream for this victor
	 */
	public PercentOut getVoltageOutput() {
		return new PercentOut(pos -> wpiVictor.ifPresent(s -> s.set(pos)));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Victor888";
	}

	@Override
	public void init(PWM port) {
		Victor victor = new Victor(port.index());
		victor.setInverted(inverted);
		wpiVictor = Optional.of(victor);
	}

	@Override
	public void failInit() {
		wpiVictor = Optional.empty();
	}

}
