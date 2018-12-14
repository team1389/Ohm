package com.team1389.hardware.inputs.hardware;

import java.util.Optional;
import java.util.function.Function;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.registry.port_types.PortInstance;
import com.team1389.hardware.registry.port_types.SPIPort;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * wraps a WPILib {@link edu.wpi.first.wpilibj.AnalogGyro AnalogGyro}, providing stream sources for
 * the gyro position and rate
 * 
 * @author amind
 *
 */
public class GyroHardware<T extends PortInstance> extends Hardware<T> {
	public static final Function<Analog, Gyro> SIMPLE_ANALOG = p -> new AnalogGyro(p.index());
	public static final Function<SPIPort, Gyro> ADXRS_450 = spi -> new ADXRS450_Gyro(spi.getPort());
	public static final Function<SPIPort, Gyro> ADXRS_453 = ADXRS453_Gyro::new;
	public static final Function<SPIPort, Gyro> NAVX = NavX::new;

	Function<T, Gyro> gyroConstructor;

	public GyroHardware(Function<T, Gyro> gyroConstructor, T requestedPort, Registry registry) {
		this.gyroConstructor = gyroConstructor;
		attachHardware(requestedPort, registry);
	}

	private Optional<Gyro> wpiGyro;

	/**
	 * @return a stream of the gyro position (in degrees)
	 */
	public AngleIn<Position> getAngleInput() {
		return new AngleIn<Position>(Position.class, () -> wpiGyro.map(gyr -> gyr.getAngle()).orElse(0.0));
	}

	/**
	 * @return a stream of the gyro rate (in degrees)
	 */
	public AngleIn<Speed> getRateInput() {
		return new AngleIn<Speed>(Speed.class, () -> wpiGyro.map(gyr -> gyr.getRate()).orElse(0.0));
	}

	public void reset() {
		wpiGyro.ifPresent(gyro -> gyro.reset());
	}

	public void calibrate() {
		wpiGyro.ifPresent(gyro -> gyro.calibrate());
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(getAngleInput().getWatchable("angle"));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Gyro";
	}

	@Override
	public void failInit() {
		wpiGyro = Optional.empty();
	}

	@Override
	protected void init(T port) {
		wpiGyro = Optional.ofNullable(gyroConstructor.apply(port));
	}

	// example
	@SuppressWarnings("unused")
	private void example() {
		new GyroHardware<>(ADXRS_453, new SPIPort(SPI.Port.kMXP), new Registry());
	}
}
