package com.team1389.hardware.outputs.software;

import com.team1389.hardware.outputs.interfaces.ScalarOutput;
import com.team1389.hardware.value_types.PIDTunableValue;

/**
 * a double stream that maps from an angle to a given range
 * 
 * @author Kenneth
 * @param <T> the value type of the stream (position or speed)
 *
 */
public class AngleOut<T extends PIDTunableValue> extends RangeOut<T> {
	/**
	 * allows this stream to map angle values from angle range to a different range
	 * 
	 * @param out stream to be operated on
	 */
	public AngleOut(ScalarOutput<T> out) {
		super(out, 0, 360);
	}

	/**
	 * maps from an angle stream to the min and max of {@code out}
	 * 
	 * @param out stream being operated on
	 */
	public AngleOut(RangeOut<? extends T> out) {
		this(ScalarOutput.mapToAngle(out.output, out.min, out.max));
	}

}
