package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.value_types.Value;

public class PressureSensorHardware extends Hardware<Analog> {
	Optional<RevRoboticsAirPressureSensor> pressureSensor;

	public PressureSensorHardware(Analog requestedPort, Registry registry) {
		super(requestedPort, registry);
	}

	public RangeIn<Value> getPressureInput() {
		return new RangeIn<>(Value.class, () -> pressureSensor.map(p -> p.getAirPressurePsi()).orElse(0.0), 0, 120);
	}

	@Override
	protected void init(Analog port) {
		pressureSensor = Optional.of(new RevRoboticsAirPressureSensor(port.index()));
	}

	@Override
	protected void failInit() {
		pressureSensor = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Pressure Sensor";
	}

}
