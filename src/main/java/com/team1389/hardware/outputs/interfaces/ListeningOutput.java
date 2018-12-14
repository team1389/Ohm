package com.team1389.hardware.outputs.interfaces;

import java.util.function.Consumer;

/**
 * tracks the values passed to a consumer and notifies a listener if a change to the value occurs
 * 
 * @author Kenneth
 *
 * @param <T> type of value that the double is representing
 */
public class ListeningOutput<T> implements Consumer<T> {
	private Consumer<T> out;
	private Consumer<T> onChange;
	T oldVal;

	public ListeningOutput(Consumer<T> out, Consumer<T> onChange, T defaultVal) {
		this.onChange = onChange;
		this.out = out;
		oldVal=defaultVal;
	}

	@Override
	public void accept(T val) {
		out.accept(val);
		if (!val.equals(oldVal)) {
			onChange.accept(val);
		}
		oldVal = val;
	}

	public T getLatestVal() {
		return oldVal;
	}
}
