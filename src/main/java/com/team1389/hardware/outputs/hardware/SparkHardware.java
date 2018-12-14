package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PWM;

import edu.wpi.first.wpilibj.Spark;

public class SparkHardware extends Hardware<PWM> {
	Optional<Spark> wpiSpark;
	boolean inverted;

	public SparkHardware(boolean inverted, PWM port, Registry registry) {
		this.inverted = inverted;
		attachHardware(port, registry);
	}

	public SparkHardware(PWM port, Registry registry) {
		this(false, port, registry);
	}

	public PercentOut getVoltageOutput() {
		return new PercentOut(d -> wpiSpark.ifPresent(s -> s.set(d)));
	}

	@Override
	protected void init(PWM port) {
		Spark value = new Spark(port.index());
		value.setInverted(inverted);
		this.wpiSpark = Optional.of(value);
	}

	@Override
	protected void failInit() {
		this.wpiSpark = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "spark";
	}
}
