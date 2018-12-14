package com.team1389.hardware.outputs.software;

import com.team1389.hardware.outputs.interfaces.ScalarOutput;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.Timer;
/**
 * a stream of doubles with a range with methods dealing with setpoint
 * @author Kenneth 
 *
 * @param <T> the type of value that the double represents
 */
public class ProfiledRangeOut<T extends Value> implements ScalarOutput<T> {
	double max, min, maxChange;
	Timer timer;
	double setpoint, goalPoint;
	ScalarOutput<T> controller;
	/**
	 * @param controller a stream of double values 
	 * @param min the min value
	 * @param max the max value
	 * @param maxChange the maximum change in setpoint 
	 * @param initialPos the current position 
	 */
	protected ProfiledRangeOut(ScalarOutput<T> controller, double min, double max, double maxChange,
			double initialPos) {
		this.maxChange = maxChange;
		this.controller = controller;
		this.min = min;
		this.max = max;
		this.setpoint = initialPos;
		timer = new Timer();
	}
	
	
	@Override
	public void set(double goalPoint) {
		setpoint = getNextSetpoint(goalPoint, timer.get());
		controller.set(setpoint);
		timer.zero();
	}
	/**
	 * 
	 * @param goalPoint the point that should be reached
	 * @param timeDiff the amount of time available to reach the goalpoint
	 * @return the closest point to the goalpoint that can be reached in the given time
	 */
	private double getNextSetpoint(double goalPoint, double timeDiff) {
		double maxChangeInSetpoint = maxChange * timeDiff;
		double newSetpoint;
		if (Math.abs(goalPoint - setpoint) < maxChangeInSetpoint) {
			newSetpoint = goalPoint;
		} else if (goalPoint > setpoint) {
			newSetpoint = setpoint + maxChangeInSetpoint;
		} else {
			newSetpoint = setpoint - maxChangeInSetpoint;
		}

		if (newSetpoint > max) {
			newSetpoint = max;
		} else if (newSetpoint < min) {
			newSetpoint = min;
		}

		return newSetpoint;
	}

}
