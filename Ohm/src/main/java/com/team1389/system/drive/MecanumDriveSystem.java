package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * Very simple mecanum drive class
 * 
 * @author Raffi
 *
 */

public class MecanumDriveSystem extends DriveSystem {
	private FourWheelSignal wheelValues;
	private PercentIn xThrottle;
	private PercentIn yThrottle;
	private PercentIn twist;
	private FourDriveOut<Percent> wheels;
	private DigitalIn fieldOriented;
	private RangeIn<Position> angle;

	public MecanumDriveSystem(PercentIn xThrottle, PercentIn yThrottle, PercentIn twist, FourDriveOut<Percent> wheels,
			AngleIn<Position> angle, DigitalIn toggleFieldOriented) {
		this.xThrottle = xThrottle;
		this.yThrottle = yThrottle;
		this.twist = twist;
		this.wheels = wheels;
		this.angle = angle.copy().mapToRange(0, 2 * Math.PI);
		this.fieldOriented = toggleFieldOriented;
	}

	public void update() {
		wheelValues = MecanumAlgorithm.mecanumCalc(xThrottle.get(), yThrottle.get(), twist.get(), fieldOriented.get(),
				angle.get());
		wheels.set(wheelValues);
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> arg0) {
		return arg0.put(xThrottle.getWatchable("x"), yThrottle.getWatchable("y"), twist.getWatchable("theta"), wheels,
				fieldOriented.getWatchable("trigger"), angle.mapToAngle(Position.class).getWatchable("angle"),
				fieldOriented.getWatchable("current yaw"));
	}

	@Override
	public String getName() {
		return "Mecanum";
	}

	@Override
	public void init() {

	}

}