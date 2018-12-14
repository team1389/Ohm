package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PWM;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.VictorSP;

/**
 * A victor SP motor controller
 * 
 * @author Jacob Prinz
 */
public class VictorHardware extends Hardware<PWM> {

	Optional<VictorSP> wpiVictor;
	boolean inverted;

	/**
	 * 
	 * @param inverted whether to invert the direction of the speed controller
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public VictorHardware(boolean inverted, PWM requestedPort, Registry registry) {
		this.inverted = inverted;
		attachHardware(requestedPort, registry);
	}

	/**
	 * @return a voltage output stream for this victor
	 */
	public PercentOut getVoltageController() {
		return new PercentOut(pos -> wpiVictor.ifPresent(s -> s.set(pos)));
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(getVoltageController().getWatchable("voltage"));
	}

	@Override
	public void init(PWM port) {
		VictorSP myVictor = new VictorSP(port.index());
		myVictor.setInverted(inverted);
		wpiVictor = Optional.of(myVictor);
	}

	@Override
	public void failInit() {
		wpiVictor = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Victor";
	}

}
