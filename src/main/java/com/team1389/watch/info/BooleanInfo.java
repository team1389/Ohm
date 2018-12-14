package com.team1389.watch.info;

import java.util.function.BooleanSupplier;

import edu.wpi.first.networktables.NetworkTable;

/**
 * an info type that tracks the value of a boolean
 * @author amind
 */
public class BooleanInfo extends NamedSimpleWatchable {

	protected BooleanSupplier in;

	/**
	 * @param name the name of the info
	 * @param in the boolean source to track
	 */
	public BooleanInfo(String name, BooleanSupplier in) {
		super(name);
		this.in = in;
	}

	@Override
	public void publishUnderName(String name, NetworkTable subtable) {
		subtable.getEntry(name).setBoolean(in.getAsBoolean());
	}

	@Override
	public String getPrintString() {
		return getName() + ": " + in.getAsBoolean();
	}

	@Override
	public double getLoggable() {
		return in.getAsBoolean() ? 1 : 0;
	}

}
