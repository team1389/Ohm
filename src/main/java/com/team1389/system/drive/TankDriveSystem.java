package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;

/**
 * drive system that can turn in place
 * 
 * @author Kenneth
 *
 */
public class TankDriveSystem extends DriveSystem {
	private DriveOut<Percent> output;
	private PercentIn throttle;
	private PercentIn wheel;

	/**
	 * 
	 * @param output a percent controlled driveStream (can be speed or voltage)
	 * @param throttle percent of desired speed (forward/back)
	 * @param wheel percent of desired turning to (l/r)
	 */
	public TankDriveSystem(DriveOut<Percent> output, PercentIn throttle, PercentIn wheel) {
		this.output = output;
		this.throttle = throttle;
		this.wheel = wheel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {

	}

	/**
	 * update wheel and throttle values, then update output using those values
	 */
	@Override
	public void update() {
		double x = wheel.get();
		double y = throttle.get();
		output.set(new DriveSignal(-y + x, -y - x));
	}

	/**
	 * return key
	 */
	@Override
	public String getName() {
		return "Tank Drive";
	}

	/**
	 * add no watchables to stem
	 */
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(wheel.getWatchable("wheel"), throttle.getWatchable("throttle"))
				.put(output.getSubWatchables(CompositeWatchable.makeStem()));
	}

}
