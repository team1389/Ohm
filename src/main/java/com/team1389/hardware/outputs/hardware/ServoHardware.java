package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.AngleOut;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PWM;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.Servo;

/**
 * A Servo motor
 * 
 * @author Jacob Prinz
 */
public class ServoHardware extends Hardware<PWM> {
	/**
	 * @param requestedPort the port to attempt to initialize this hardware
	 * @param registry the registry associated with the robot
	 */
	public ServoHardware(PWM requestedPort, Registry registry) {
		super(requestedPort, registry);
	}

	Optional<Servo> wpiServo;

	/**
	 * 
	 * @return an output stream that controls the servo position
	 */
	public RangeOut<Position> getPositionOutput() {
		return new RangeOut<Position>(pos -> wpiServo.ifPresent(s -> s.set(pos)), 0, 1);
	}

	/**
	 * 
	 * @return an input stream of servo position
	 */
	public RangeIn<Position> getPositionInput() {
		return new RangeIn<Position>(Position.class, () -> wpiServo.map(servo -> servo.get()).orElse(0.0), 0, 1);
	}

	/**
	 * @return an input stream of servo angle (in degrees)
	 */
	public AngleIn<Position> getAngleInput() {
		return getPositionInput().getMappedToRange(0, 180).getWithSetRange(0, 360).getAsAngle(Position.class);
	}

	/**
	 * @return an output stream that controls the angle of the servo
	 */
	public AngleOut<Position> getAngleOutput() {
		return getPositionOutput().getMappedToRange(0, 180).getClamped().getWithSetRange(0, 360).getAsAngle();
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(getAngleInput().getWatchable("angle"));
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Servo";
	}

	@Override
	public void init(PWM port) {
		wpiServo = Optional.of(new Servo(port.index()));
	}

	@Override
	public void failInit() {
		wpiServo = Optional.empty();
	}
}
