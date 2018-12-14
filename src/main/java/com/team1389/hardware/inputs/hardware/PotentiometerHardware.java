package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * represents an analog potentiometer
 * 
 * @author amind
 *
 */
public class PotentiometerHardware extends Hardware<Analog> {
	private Optional<AnalogPotentiometer> wpiPot;
	private double range, offset;

	/**
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public PotentiometerHardware(double range, double offset, Analog requestedPort, Registry registry) {
		this.range = range;
		this.offset = offset;
		attachHardware(requestedPort, registry);
	}

	public PotentiometerHardware(Analog requestedPort, Registry registry) {
		this(1, 0, requestedPort, registry);
	}

	/**
	 * 
	 * @return an input stream of potentiometer values on the range [0,1]
	 */
	public RangeIn<Position> getAnalogInput() {
		return new RangeIn<>(Position.class, () -> wpiPot.map(pot -> (range * pot.get() / .35) - offset).orElse(0.0), 0,
				1);
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(getAnalogInput().getWatchable("val"));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Potentiometer";
	}

	@Override
	public void init(Analog port) {
		wpiPot = Optional.of(new AnalogPotentiometer(port.index()));
	}

	@Override
	public void failInit() {
		wpiPot = Optional.empty();
	}
}
