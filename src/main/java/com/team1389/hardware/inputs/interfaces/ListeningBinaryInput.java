package com.team1389.hardware.inputs.interfaces;

import java.util.function.Consumer;

/**
 * a boolean stream that tracks changes and notifies a listener
 * 
 * @author amind
 *
 */
public class ListeningBinaryInput implements BinaryInput, Listener<Boolean> {
	private BinaryInput in;
	private Consumer<Boolean> onChange;
	private boolean newVal;
	private boolean oldVal;

	protected ListeningBinaryInput(BinaryInput in, Consumer<Boolean> onChange) {
		this.onChange = onChange;
		this.in = in;
	}

	public Boolean get() {
		update();
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
	public void onChange(Boolean newVal) {
		onChange.accept(newVal);
	}

	@Override
	public Boolean getLatestVal() {
		return newVal;
	}
}
