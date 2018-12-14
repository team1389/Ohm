package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.DoubleHardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.NumberInfo;

import edu.wpi.first.wpilibj.Ultrasonic;

@Deprecated
public class UltrasonicHardware extends DoubleHardware<Analog> {
	Optional<Ultrasonic> wpiUltraSonic;

	public UltrasonicHardware(Analog requestedPort1, Analog requestedPort2, Registry registry) {
		super(requestedPort1, requestedPort2, registry);

	}

	public RangeIn<Value> getRangeIn() {

		return new RangeIn<Value>(Value.class, () -> wpiUltraSonic.get().getRangeInches(), 0, 100);

	}

	@Override
	protected void init(Analog port1, Analog port2) {
		Ultrasonic ultraS = new Ultrasonic(port1.index(), port2.index());
		wpiUltraSonic = Optional.of(ultraS);
	}

	@Override
	protected void failInit() {
		wpiUltraSonic = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "UltraSonicSensor";
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(new NumberInfo("Sensor", this.getRangeIn().getStream()));
	}

}
