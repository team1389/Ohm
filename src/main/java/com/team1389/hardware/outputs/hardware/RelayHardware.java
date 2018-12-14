package com.team1389.hardware.outputs.hardware;

import java.util.Optional;
import java.util.function.Consumer;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.DigitalOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.RelayPort;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.StringInfo;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

public class RelayHardware extends Hardware<RelayPort> {
	private Optional<Relay> wpiRelay;

	public RelayHardware(RelayPort requestedPort, Registry registry) {
		super(requestedPort, registry);
	}

	public DigitalOut getOnOffStream() {
		return new DigitalOut(val -> wpiRelay.ifPresent(rel -> rel.set(val ? Value.kForward : Value.kOff)));
	}

	public Consumer<Relay.Value> getRelayStream() {
		return val -> wpiRelay.ifPresent(rel -> rel.set(val));
	}

	private Relay.Value getCurrent() {
		return wpiRelay.map(rel -> rel.get()).orElse(Value.kOff);
	}

	@Override
	protected void failInit() {
		wpiRelay = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Relay";
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(new StringInfo("value", () -> getCurrent().name()));
	}

	@Override
	protected void init(RelayPort port) {
		wpiRelay = Optional.of(new Relay(port.index()));
	}

}
