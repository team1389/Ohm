package com.team1389.hardware.inputs.software;

import java.util.function.Supplier;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.PIDTunableValue;

/**
 * represents an angle stream in degrees
 * 
 * @author amind
 *
 * @param <T> the value type of the angle stream (angular velocity or angle)
 */
public class AngleIn<T extends PIDTunableValue> extends RangeIn<T> {
	/**
	 * @param type the stream value type
	 * @param supplier a double supplier to generate the stream
	 */
	public AngleIn(Class<T> type, Supplier<Double> supplier) {
		super(type, supplier, 0, 360);
	}

	/**
	 * @param in the stream to convert to an angle stream
	 */
	public AngleIn(RangeIn<T> in) {
		this(in.type, ScalarInput.mapToAngle(in.input, in.min, in.max));
	}

	public AngleIn<T> copy() {
		return new AngleIn<T>(type, input);
	}
}
