package com.team1389.watch.input.stream;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.Value;

import edu.wpi.first.wpilibj.tables.ITable;
/**
 * tracks a double on the network table and makes its value available as a stream
 * 
 * @author amind
 *
 */
public class DashboardScalarInput extends DashboardInput<Double> implements ScalarInput<Value> {
	/**
	 * @param key the key to track
	 * @param table the table in which to find the key
	 * @param defaultVal the default val (to publish if the key does not exist yet)
	 */
	public DashboardScalarInput(String key, ITable table, Double defaultVal) {
		super(key, table, defaultVal);
		if (!table.containsKey(key)) {
			table.putNumber(key, defaultVal);
		} else {
			value = table.getNumber(key, defaultVal);
		}
	}

}
