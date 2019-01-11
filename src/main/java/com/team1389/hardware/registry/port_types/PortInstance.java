package com.team1389.hardware.registry.port_types;

import edu.wpi.first.wpilibj.SendableBase;

/**
 * represents a generic port
 * 
 * @author amind
 *
 */
public abstract class PortInstance {
	private int index;

	/**
	 * @return the port number
	 */
	public int index() {
		return index;
	}

	/**
	 * @param port the port number
	 */
	public PortInstance(int port) {
		this.index = port;
	}

	/**
	 * possible types of port
	 * 
	 * @author amind
	 *
	 */
	public enum PortType {
		/**
		 * PWM port on the roborio
		 */
		PWM(10),
		/**
		 * PCM port
		 */
		PCM(8),
		/**
		 * port on the CAN bus
		 */
		CAN(Integer.MAX_VALUE),
		/**
		 * Analog port
		 */
		ANALOG(4),
		/**
		 * Digital Output port on the roborio
		 */
		DIO(10),
		/**
		 * USB port on the roborio
		 */
		SPI(5), I2C(Integer.MAX_VALUE), USB(2), RELAY(4);
		protected final int maxPorts;

		private PortType(int maxPorts) {
			this.maxPorts = maxPorts;
		}
	}

	/**
	 * @return the port type
	 */
	public abstract PortType getPortType();

	@Override
	public String toString() {
		return getPortType().name() + " port " + index;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PortInstance) {
			return ((PortInstance) o).index == this.index;
		}
		return false;
	}
}
