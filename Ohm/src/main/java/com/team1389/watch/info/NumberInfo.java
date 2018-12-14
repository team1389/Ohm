package com.team1389.watch.info;

import com.team1389.hardware.inputs.interfaces.ScalarInput;

import edu.wpi.first.networktables.NetworkTable;

/**
 * an info type that tracks a double
 * 
 * @author amind
 *
 */
public class NumberInfo extends NamedSimpleWatchable {
	ScalarInput<?> source;

	/**
	 * @param name the name of the info
	 * @param source the double source to track
	 */
	public NumberInfo(String name, ScalarInput<?> source) {
		super(name);
		this.source = source;
	}

	@Override
	public void publishUnderName(String name, NetworkTable table) {
		table.getEntry(name).setDouble(source.get());
	}

	@Override
	public String getPrintString() {
		return name + ": " + source.get();
	}

	@Override
	public double getLoggable() {
		return source.get();
	}
}
