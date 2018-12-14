package com.team1389.util.boolean_util;

import java.util.function.Supplier;

/**
 * An iterative boolean latch. Returns true once if and only if the value of the tracked raw boolean changes from false to true.
 */
public class LatchedBoolean {
	private boolean mLast = false;
	protected Supplier<Boolean> raw;
	boolean val;

	/**
	 * @param raw the raw boolean supplier
	 */
	protected LatchedBoolean(Supplier<Boolean> raw) {
		this.raw = raw;
		this.val = raw.get();
	}

	protected boolean update(boolean newValue) {
		boolean ret = false;
		if (newValue && !mLast) {
			ret = true;
		}
		mLast = newValue;
		val = ret;
		return ret;
	}

	protected boolean get() {
		return update(raw.get());
	}

	/**
	 * @param raw a supplier of raw boolean values
	 * @return the latched version of the input supplier
	 */
	public static Supplier<Boolean> latch(Supplier<Boolean> raw) {
		LatchedBoolean latched = new LatchedBoolean(raw);
		return latched::get;
	}

}