package com.team1389.hardware.outputs.hardware;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mindsensors.CANLight;
import com.team1389.hardware.Hardware;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.util.Color;

public class CANLightHardware extends Hardware<CAN> {
	Optional<CANLight> light;

	public CANLightHardware(CAN requestedPort, Registry registry) {
		super(requestedPort, registry);
		light.get();
	}

	public Consumer<Color> getColorOutput() {
		return this::show;
	}

	public BiConsumer<Color, Boolean> getBlinkableColorOutput() {
		return (color, val) -> {
			if (val)
				blink(color, .75);
			else
				show(color);
		};
	}

	private void show(Color color) {
		light.ifPresent(l -> l.showRGB(color.getRed(), color.getGreen(), color.getBlue()));
	}

	private void blink(Color color, double rate) {
		if (light.isPresent()) {
			CANLight light = this.light.get();
			light.writeRegister(0, rate, color.getRed(), color.getGreen(), color.getBlue());
			light.flash(0);
		}
	}

	@Override
	protected void init(CAN port) {
		light = Optional.of(new CANLight(port.index()));
	}

	@Override
	protected void failInit() {
		light = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "CANLight";
	}

}
