package com.team1389.util;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.value_types.Value;

/**
 * This timer class is intended to provide a simple way to measure time
 * intervals between events in the code. All values are in seconds
 * 
 * @author amind
 *
 */
public class Timer implements ScalarInput<Value> {
	edu.wpi.first.wpilibj.Timer timer;
	private double lastMark = 0;
	private double last;

	/**
	 * initializes the WPILib timer
	 */
	public Timer() {
		timer = new edu.wpi.first.wpilibj.Timer();
		timer.start();
	}

	/**
	 * sets the time elapsed to 0 (resets the timer)
	 * <p>
	 * zeroing the timer will remove a mark if there is one
	 */
	public void zero() {
		timer.reset();
		lastMark = 0;
		last = timer.get();
	}

	/**
	 * marks the current time for later reference
	 * <p>
	 * if a mark was previously stored, it will be replaced
	 * 
	 */
	public void mark() {
		lastMark = get();
	}

	/**
	 * @return time since the mark was set
	 */
	public double getSinceMark() {
		return get() - lastMark;
	}

	/**
	 * @return the current value of the timer
	 */
	@Override
	public Double get() {
		return timer.get() - last;
	}

	/**
	 * 
	 * @return an input stream that tracks the time as stored by this timer (units
	 *         seconds)
	 */
	public RangeIn<Value> getTimeInput() {
		return new RangeIn<Value>(Value.class, this::get, 0, 60);
	}

}
