package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.value_types.Position;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogUltrasonicHardware extends Hardware<Analog> {
	public static final double ANALOG_INPUT_VOLTAGE = 5;// volts
	public static final double DEFAULT_VOLTS_PER_CM = 4.9E-3;// volts
	private double voltsPerCm;
	private Optional<AnalogInput> wpiInput;

	public AnalogUltrasonicHardware(double voltsPerCm, Analog requestedPort, Registry registry) {
		this.setVoltsPerCm(voltsPerCm);
		attachHardware(requestedPort, registry);
	}

	public AnalogUltrasonicHardware(Analog requestedPort, Registry registry) {
		this(DEFAULT_VOLTS_PER_CM, requestedPort, registry);
	}

	public RangeIn<Position> getDistanceIn() {
		return new RangeIn<>(Position.class, this::getDistanceCm, 0, 765);
	}

	private double getDistanceCm() {
		return wpiInput.map(i -> i.getAverageVoltage() / voltsPerCm).orElse(0.0);
	}

	@Override
	protected void init(Analog port) {
		AnalogInput value = new AnalogInput(port.index());
		value.setAverageBits(4);
		wpiInput = Optional.of(value);
	}

	@Override
	protected void failInit() {
		wpiInput = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Analog US sensor";
	}

	public void setVoltsPerCm(double voltsPerCm) {
		this.voltsPerCm = voltsPerCm;
	}

}
