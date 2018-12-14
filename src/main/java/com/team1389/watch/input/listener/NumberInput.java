package com.team1389.watch.input.listener;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;

/**
 * an input type that tracks the value of a boolean on a network table
 * 
 * @author amind
 *
 */
public class NumberInput extends InputWatchable<Double> {
	/**
	 * @param name the name of the input
	 * @param defaultVal the default value to apply to the network table
	 * @param onChange an action to perfom when the table value changes
	 */
	public NumberInput(String name, double defaultVal, Consumer<Double> onChange) {
		super(name, defaultVal, onChange);
	}

	@Override
	protected void put(NetworkTable table, String name, Double val) {
		table.getEntry(name).setDouble(val);
	}

	@Override
	protected Double get(NetworkTable table, String name, Double defaultVal) {
		return table.getEntry(name).getDouble(defaultVal);
	}

	@Override
	public double getLoggable() {
		return val;
	}

}
