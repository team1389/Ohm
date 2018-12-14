package com.team1389.hardware.registry.port_types;

import edu.wpi.first.wpilibj.I2C;

public class I2CPort extends PortInstance {

	I2C.Port port;

	public I2CPort(I2C.Port port) {
		super(port.value);
		this.port = port;
	}

	@Override
	public PortType getPortType() {
		return PortType.I2C;
	}

	public I2C.Port getPort() {
		return port;
	}

}
