package com.team1389.hardware.registry;

import java.util.List;
import java.util.Optional;

import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.registry.port_types.DIO;
import com.team1389.hardware.registry.port_types.PCM;
import com.team1389.hardware.registry.port_types.PWM;
import com.team1389.hardware.registry.port_types.PortInstance;
import com.team1389.hardware.registry.port_types.RelayPort;
import com.team1389.hardware.registry.port_types.SPIPort;
import com.team1389.hardware.registry.port_types.USB;
import com.team1389.watch.Watchable;
import com.team1389.watch.Watcher;

/**
 * This class tracks all hardware objects and ensures that no hardware attempts to install on a
 * taken port, to avoid crashing the robot code
 * 
 * @author amind
 *
 */
public class Registry {
	private Watcher watcher;
	private PortTracker<Analog> analogPorts;
	private PortTracker<PCM> pcmPorts;
	private PortTracker<PWM> pwmPorts;
	private PortTracker<CAN> canPorts;
	private PortTracker<DIO> dioPorts;
	private PortTracker<USB> usbPorts;
	private PortTracker<SPIPort> spiPorts;
	private PortTracker<RelayPort> relayPorts;

	boolean throwExceptions;

	/**
	 * initialize this registry instance
	 */
	public Registry() {
		watcher = new Watcher();
		pcmPorts = new PortTracker<>();
		analogPorts = new PortTracker<>();
		pwmPorts = new PortTracker<>();
		canPorts = new PortTracker<>();
		dioPorts = new PortTracker<>();
		usbPorts = new PortTracker<>();
		spiPorts = new PortTracker<>();
		relayPorts = new PortTracker<>();
		throwExceptions = false;
	}

	/**
	 * 
	 * @param portExceptions whether to throw exceptions when a used port is requested
	 */
	public Registry(boolean portExceptions) {
		this();
		this.throwExceptions = portExceptions;
	}

	/**
	 * add an object to the registry's watcher <br>
	 * this watcher is only used as a storage object for watchables, and is not displayed unless
	 * another watcher gets it from {@link #getHardwareInfo()}
	 * 
	 * @param watchable the object to watch
	 */
	public void registerWatchable(Watchable watchable) {
		watcher.watch(watchable);
	}

	/**
	 * @param r the port to check
	 * @return whether the given port is in use
	 */
	public <R extends PortInstance> boolean isUsed(R r) {
		return getRegister(r).isUsed(r);
	}

	/**
	 * @param r the port to claim
	 * @return whether the port was successfully claimed
	 * @throws PortTakenException if claiming failed and this registry throws port exceptions
	 */
	public <R extends PortInstance> boolean claim(R r) {
		boolean claim = getRegister(r).claim(r);
		if (throwExceptions && !claim) {
			throw new PortTakenException(r + " is taken!");
		}
		return claim;
	}

	/**
	 * @param r the port to get
	 * @return empty optional if the port is taken, otherwise an optional containing the port
	 * @throws PortTakenException if port was taken and this registry throws port exceptions
	 */
	public <R extends PortInstance> Optional<R> getPort(R r) {
		Optional<R> port;
		if (isUsed(r)) {
			if (throwExceptions) {
				throw new PortTakenException(r + " is taken!");
			}
			port = Optional.empty();
		} else {
			port = Optional.of(r);
			claim(r);
		}
		return port;
	}

	/**
	 * 
	 * @param r the port type
	 * @return the specific register for ports of type
	 */
	@SuppressWarnings("unchecked")
	public <T extends PortInstance> PortTracker<T> getRegister(T r) {
		switch (r.getPortType()) {
		case PWM:
			return (PortTracker<T>) pwmPorts;
		case ANALOG:
			return (PortTracker<T>) analogPorts;
		case CAN:
			return (PortTracker<T>) canPorts;
		case DIO:
			return (PortTracker<T>) dioPorts;
		case PCM:
			return (PortTracker<T>) pcmPorts;
		case USB:
			return (PortTracker<T>) usbPorts;
		case SPI:
			return (PortTracker<T>) spiPorts;
		case RELAY:
			return (PortTracker<T>) relayPorts;
		default:
			return null;
		}
	}

	/**
	 * @return a list of all hardware watchables associated with this registry
	 */
	public List<Watchable> getHardwareInfo() {
		return watcher.getWatchables();
	}

}
