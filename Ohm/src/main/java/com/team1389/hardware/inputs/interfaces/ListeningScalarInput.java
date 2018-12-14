package com.team1389.hardware.inputs.interfaces;

import java.util.function.Consumer;

import com.team1389.hardware.value_types.Value;

/**
 * A double stream that listens for a change in the value and executes a runnable on change. Note
 * that this class only checks for a change when the get method is called.
 * 
 * @author Josh
 *
 * @param <T> The stream's type
 */
public class ListeningScalarInput<T extends Value> implements ScalarInput<T>, Listener<Double> {
	private ScalarInput<T> in;
	private Consumer<Double> onChange;
	private double newVal;
	private double oldVal;

	/**
	 * @param in The stream to listen for changes in
	 * @param onChange The runnable to execute when a change is detected.
	 */
	protected ListeningScalarInput(ScalarInput<T> in, Consumer<Double> onChange) {
		this.onChange = onChange;
		this.in = in;
	}

	/**
	 * Note again that this is the method that checks for a change, between the current value and
	 * the value from the last time this method was called. If there is a change it calls the
	 * runnable passed in through the constructor.
	 * 
	 */
	public Double get() {
		check();
		return newVal;
	}

	private void update() {
		oldVal = newVal;
		newVal = in.get();
	}

	public boolean hasChanged() {
		update();
		return oldVal != newVal;

	}

	@Override
	public void onChange(Double newVal) {
		onChange.accept(newVal);
	}

	@Override
	public Double getLatestVal() {
		return newVal;
	}
}
