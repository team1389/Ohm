package com.team1389.watch.input.stream;

import com.team1389.hardware.inputs.interfaces.BinaryInput;

import edu.wpi.first.wpilibj.tables.ITable;

/**
 * tracks a boolean on the network table and makes its value available as a stream
 * 
 * @author amind
 *
 */
public class DashboardBinaryInput extends DashboardInput<Boolean> implements BinaryInput {
	/**
	 * @param key the key to track
	 * @param table the table in which to find the key
	 * @param defaultVal the default val (to publish if the key does not exist yet)
	 */
	public DashboardBinaryInput(String key, ITable table, Boolean defaultVal) {
		super(key, table, defaultVal);
		if (!table.containsKey(key)) {
			table.putBoolean(key, defaultVal);
		}
	}

}