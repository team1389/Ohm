package com.team1389.watch.input.listener;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;



/**
 * an input type that tracks the value of a boolean on a network table
 * 
 * @author amind
 *
 */
public class BooleanInput extends InputWatchable<Boolean> {
	/**
	 * @param name the name of the input
	 * @param defaultVal the default value to apply to the network table
	 * @param onChange an action to perfom when the table value changes
	 */
	public BooleanInput(String name, boolean defaultVal, Consumer<Boolean> onChange) {
		super(name, defaultVal, onChange);
	}

	@Override
	protected void put(NetworkTable table, String name, Boolean val) {
		table.getEntry(name).setBoolean(val);
	}

	@Override
	protected Boolean get(NetworkTable table, String name, Boolean defaultVal) {
		return table.getEntry(name).getBoolean(defaultVal);
	}

	@Override
	public double getLoggable() {
		return val ? 1 : 0;
	}

}
