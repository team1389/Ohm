package com.team1389.hardware;

import java.util.Optional;

import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PortInstance;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.FlagInfo;

/**
 * wraps a WPI lib hardware object, adding watchable support and port tracking via
 * {@link com.team1389.hardware.registry.Registry Registry}
 * 
 * <p>
 * Subclasses are intended to handle the case where the hardware fails to claim a port on
 * construction
 * @author amind
 *
 * @param <T> the hardware port type
 */
public abstract class Hardware<T extends PortInstance> implements CompositeWatchable {
	Optional<String> specificHardwareName;
	protected Optional<T> port;

	public Hardware() {
		specificHardwareName = Optional.empty();
	}

	/**
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public Hardware(T requestedPort, Registry registry) {
		this();
		attachHardware(requestedPort, registry);
	}

	public void attachHardware(T requestedPort, Registry registry) {
		this.port = registry.getPort(requestedPort);
		if (!port.isPresent()) {
			failInit();
			System.out.println("hardware failed to initialize on " + requestedPort);
		}else{
			port.ifPresent(this::init);
			registry.registerWatchable(this);
		}

	}

	/**
	 * @param specificHardwareName a specific string Identifier for this particular hardware
	 *            instance
	 */
	public void setName(String specificHardwareName) {
		this.specificHardwareName = Optional.of(specificHardwareName);
	}

	/**
	 * initializes the hardware object (subclasses will initialize wrapped WPILib objects here)
	 * 
	 * @param port the port to initialize the hardware on
	 */
	protected abstract void init(T port);

	/**
	 * called in the place of Hardware#init when the requested port is taken
	 */
	protected abstract void failInit();

	/**
	 * @return the port associated with this hardware, or negative one if the hardware failed to
	 *         initialize
	 */
	public int getPort() {
		return port.map(PortInstance::index).orElse(-1);
	}

	protected abstract String getHardwareIdentifier();

	@Override
	public String getName() {
		String defaultName = getHardwareIdentifier() + " " + getPort();
		return specificHardwareName.orElse(defaultName);
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(new FlagInfo("port fault", () -> !port.isPresent()));
	}
}
