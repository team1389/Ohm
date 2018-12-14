package com.team1389.hardware.registry.port_types;

public class RelayPort extends PortInstance{

	public RelayPort(int port) {
		super(port);
	}

	@Override
	public PortType getPortType() {
		return PortType.RELAY;
	}

}
