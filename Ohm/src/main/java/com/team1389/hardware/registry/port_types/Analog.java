package com.team1389.hardware.registry.port_types;

/**
 * represents an analog port on the roborio
 * 
 * @author amind
 *
 */
public class Analog extends PortInstance {
	/**
	 * 
	 * @param port the analog port number
	 */
	public Analog(int port) {
		super(port);
	}

	@Override
	public PortType getPortType() {
		return PortType.ANALOG;
	}
}
