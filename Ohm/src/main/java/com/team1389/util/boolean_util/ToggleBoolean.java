package com.team1389.util.boolean_util;

import java.util.function.Supplier;

/**
 * tracks a raw boolean value and generates a new boolean value that toggles every time the original switches from false to true
 * 
 * @author Kenneth
 *
 */
public class ToggleBoolean extends LatchedBoolean {
	boolean toggle;

	private ToggleBoolean(Supplier<Boolean> raw) {
		super(raw);
	}

	@Override
	protected boolean update(boolean newVal) {
		if (super.update(newVal)) {
			toggle = !toggle;
		}
		return toggle;
	}

	/**
	 * @param raw a supplier of raw boolean values
	 * @return the toggled version of the input supplier
	 */
	public static Supplier<Boolean> toggle(Supplier<Boolean> raw) {
		ToggleBoolean toggled = new ToggleBoolean(raw);
		return toggled::get;
	}

}
