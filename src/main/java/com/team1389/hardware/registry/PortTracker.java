package com.team1389.hardware.registry;

import java.util.ArrayList;
import java.util.List;

import com.team1389.hardware.registry.port_types.PortInstance;

/**
 * 
 * @author amind
 *
 * @param <T> the port type to track
 */
public class PortTracker<T extends PortInstance> {

	private List<T> usedPorts;

	/**
	 * initializes the port tracker
	 */
	public PortTracker() {
		usedPorts = new ArrayList<>();
	}

	/**
	 * @param port the port to check
	 * @return true if the port is in use
	 */
	public boolean isUsed(T port) {
		return usedPorts.contains(port);
	}
	
	/**
	 * @param port the port to claim
	 * @return true if the port was successfully changed
	 */
	public boolean claim(T port) {
		return usedPorts.add(port);
	}

	@Override
	public String toString() {
		return usedPorts.toString();
	}

}
