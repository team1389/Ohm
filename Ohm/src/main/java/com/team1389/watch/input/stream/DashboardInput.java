package com.team1389.watch.input.stream;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.tables.ITable;

/**
 * tracks a piece of data on the network table and makes its value available as a stream
 * 
 * @author amind
 * @param <T> the type of data
 */
public class DashboardInput<T> implements Supplier<T> {
	protected T value;

	/**
	 * @param key the key to track
	 * @param table the table in which to find the key
	 * @param defaultVal the default val (to publish if the key does not exist yet)
	 */
	@SuppressWarnings("unchecked")
	public DashboardInput(String key, ITable table, T defaultVal) {
		value = defaultVal;
		table.addTableListener((ITable changeTable, String changeKey, Object val, boolean changed) -> {
			if (changeKey.equals(key)) {
				this.value = (T) val;
			}
		});
	}

	@Override
	public T get() {
		return value;

	}

}
