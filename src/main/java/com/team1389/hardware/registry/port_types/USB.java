package com.team1389.hardware.registry.port_types;

/**
 * represents a USB port on the roborio
 * 
 * @author amind
 *
 */
public class USB extends PortInstance {

	/**
	 * @param port the port number
	 */
	public USB(int port) {
		super(port);
	}

	@Override
	public PortType getPortType() {
		return PortType.USB;
	}

}
