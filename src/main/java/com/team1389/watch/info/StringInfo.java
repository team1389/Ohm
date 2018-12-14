package com.team1389.watch.info;

import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;

/**
 * an info type that tracks the value of a string
 * 
 * @author amind
 *
 */
public class StringInfo extends NamedSimpleWatchable {

	Supplier<String> source;

	/**
	 * @param name the name of this info
	 * @param source the string source to track
	 */
	public StringInfo(String name, Supplier<String> source) {
		super(name);
		this.source = source;
	}

	@Override
	public void publishUnderName(String name, NetworkTable table) {
		table.getEntry(name).setString(source.get());
	}

	@Override
	public String getPrintString() {
		return name + ": " + source.get();
	}

	@Override
	public double getLoggable() {
		return -1;
	}
}
