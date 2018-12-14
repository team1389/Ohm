package com.team1389.hardware;

import java.util.Optional;

import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PortInstance;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.FlagInfo;

/**
 * wraps any WPI hardware that uses two ports
 * 
 * @see Hardware
 * @author amind
 *
 * @param <T> the hardware port type
 */
public abstract class DoubleHardware<T extends PortInstance> implements CompositeWatchable {
	private Optional<T> port1, port2;
	private Optional<String> specificHardwareName;

	public DoubleHardware() {
		specificHardwareName = Optional.empty();
	}

	/**
	 * @param requestedPort1 the first port associated with this hardware
	 * @param requestedPort2 the second port associated with this hardware
	 * @param registry the registry associated with the robot
	 */
	public DoubleHardware(T requestedPort1, T requestedPort2, Registry registry) {
		this();
		attachHardware(requestedPort1, requestedPort2, registry);
	}

	protected void attachHardware(T requestedPort1, T requestedPort2, Registry registry) {
		this.port1 = registry.getPort(requestedPort1);
		this.port2 = registry.getPort(requestedPort2);
		if (port1.isPresent() && port2.isPresent()) {
			init(port1.get(), port2.get());
			registry.registerWatchable(this);
		} else {
			System.out.println("failed to initialize double hardware.");
			if (!port1.isPresent())
				System.out.println(requestedPort1 + " is taken");
			if (!port2.isPresent())
				System.out.println(requestedPort2 + " is taken");
			failInit();
		}
	}

	/**
	 * initializes the hardware object (subclasses will initialize wrapped WPILib objects here)
	 * 
	 * @param port1 the first port to initialize the hardware on
	 * @param port2 the second port to initialize the hardware on
	 */
	protected abstract void init(T port1, T port2);

	/**
	 * called in the place of Hardware#init when the requested port is taken
	 */
	protected abstract void failInit();

	/**
	 * @param specificHardwareName a specific string Identifier for this particular hardware
	 *            instance
	 */
	public void setName(String specificHardwareName) {
		this.specificHardwareName = Optional.of(specificHardwareName);
	}

	/**
	 * @return the first port associated with this hardware, or -1 if the hardware failed to
	 *         initialize
	 */
	public int getPortA() {
		return port1.map(PortInstance::index).orElse(-1);
	}

	/**
	 * @return the second port associated with this hardware, or -1 if the hardware failed to
	 *         initialize
	 */
	public int getPortB() {
		return port2.map(PortInstance::index).orElse(-1);
	}

	protected abstract String getHardwareIdentifier();

	@Override
	public String getName() {
		String defaultName = getHardwareIdentifier() + " " + getPortA() + " " + getPortB();
		return specificHardwareName.orElse(defaultName);
	}

	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(new FlagInfo("port fault", () -> {
			return !(port1.isPresent() && port2.isPresent());
		}));
	}
}
