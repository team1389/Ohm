package com.team1389.hardware.registry.port_types;

import edu.wpi.first.wpilibj.SPI;

/**
 * kMXP = 4 kOnboardCS0 = 0 kOnboardCS1 = 1 kOnboardCS2 = 2 kOnboardCS3 = 3
 * @author amind TODO add comments
 */
public class SPIPort extends PortInstance {
	SPI.Port port;

	public SPIPort(SPI.Port port) {
		super(port.value);
		this.port = port;
	}

	@Override
	public PortType getPortType() {
		return PortType.SPI;
	}

	public SPI.Port getPort() {
		return port;
	}

}
