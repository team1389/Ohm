package com.team1389.hardware.inputs.interfaces;

public interface Listener<T> {
	public boolean hasChanged();

	public void onChange(T newVal);

	public T getLatestVal();

	public default void check() {
		if (hasChanged()) {
			onChange(getLatestVal());
		}
	}

}
