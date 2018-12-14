package com.team1389.hardware.registry.port_types;

/**
 * represents a PWM port on the roborio
 * 
 * @author amind
 *
 */
public class PWM extends PortInstance {
	/**
	 * 
	 * @param port the port number
	 */
	public PWM(int port) {
		super(port);
	}

	@Override
	public PortType getPortType() {
		return PortType.PWM;
	}

}
