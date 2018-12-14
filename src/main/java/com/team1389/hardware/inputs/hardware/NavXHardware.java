package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.kauailabs.navx.frc.AHRS;
import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.SPIPort;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * wraps a Kauailabs {@link com.kauailabs.navx.frc.AHRS NavX}, providing stream sources for the yaw,
 * rate, etc.
 * 
 * @author amind
 *
 */
public class NavXHardware extends Hardware<SPIPort> {
	Optional<AHRS> navX;

	/**
	 * @param port the port of the navX
	 * @param registry the registry associated with the robot
	 */
	public NavXHardware(SPIPort requestedPort, Registry registry) {
		super(requestedPort, registry);
	}

	/**
	 * @return a stream of yaw data from the NavX (in degrees)
	 */
	public AngleIn<Position> getYawInput() {
		return new AngleIn<Position>(Position.class, () -> {
			return (double) navX.map(n -> n.getAngle()).orElse(0.0);
		});
	}

	public void reset() {
		navX.ifPresent(n -> n.reset());
	}

	// TODO add NavX capabilities
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(getYawInput().getWatchable("yaw"));
	}

	@Override
	protected void init(SPIPort port) {
		navX = Optional.of(new AHRS(port.getPort()));
	}

	@Override
	protected void failInit() {
		navX = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "NavX";
	}
}
