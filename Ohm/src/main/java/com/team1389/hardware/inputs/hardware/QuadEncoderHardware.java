package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.hardware.DoubleHardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.DIO;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;

import edu.wpi.first.wpilibj.Encoder;

public class QuadEncoderHardware extends DoubleHardware<DIO> {
	private boolean reversed;
	private Optional<Encoder> wpiEncoder;

	public QuadEncoderHardware(boolean reversed, DIO requestedPort1, DIO requestedPort2, Registry registry) {
		this.reversed = reversed;
		attachHardware(requestedPort1, requestedPort2, registry);
	}

	public QuadEncoderHardware(DIO requestedPort1, DIO requestedPort2, Registry registry) {
		this(false, requestedPort1, requestedPort2, registry);
	}

	/**
	 * you must set the distance per pulse for this method to return a predictable result!
	 * 
	 * @see QuadEncoderHardware#setRevsPerTick(double) set distance per pulse
	 * @return a stream that tracks the distance traveled by the encoder in rotations
	 */
	public RangeIn<Position> getDistanceInput() {
		return new RangeIn<Position>(Position.class, () -> wpiEncoder.map(e -> e.getDistance()).orElse(0.0), 0, 1);
	}

	/**
	 * you must set the distance per pulse for this method to return a predictable result!
	 * 
	 * @see QuadEncoderHardware#setRevsPerTick(double) set distance per pulse
	 * @return a stream that tracks the rate of the encoder in rotations per second
	 */
	public RangeIn<Speed> getRateInput() {
		return new RangeIn<Speed>(Speed.class, () -> wpiEncoder.map(e -> e.getRate()).orElse(0.0), 0, 1);
	}

	/**
	 * @param revs the number of revolutions per encoder tick
	 */
	public void setRevsPerTick(double revs) {
		wpiEncoder.ifPresent(e -> e.setDistancePerPulse(revs));
	}

	@Override
	protected void init(DIO port1, DIO port2) {
		Encoder encoder = new Encoder(port1.index(), port2.index());
		encoder.setReverseDirection(reversed);
		wpiEncoder = Optional.of(encoder);
	}

	@Override
	protected void failInit() {
		wpiEncoder = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Encoder";
	}

}
