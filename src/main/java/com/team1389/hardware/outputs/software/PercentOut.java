package com.team1389.hardware.outputs.software;

import java.util.function.Consumer;

import com.team1389.hardware.outputs.interfaces.ScalarOutput;
import com.team1389.hardware.value_types.Percent;

/**
 * An input that gives a value from -1 to 1
 * 
 * @author Ari Mindell
 */
public class PercentOut extends RangeOut<Percent> {
	/**
	 * @param out the consumer to pass values to
	 */
	public PercentOut(ScalarOutput<Percent> out) {
		super(out, -1, 1);
	}

	/**
	 * @param out the consumer to pass values to
	 */
	public PercentOut(Consumer<Double> out) {
		this(ScalarOutput.convert(out));
	}

	/**
	 * 
	 * @param out a stream to convert to a percent stream
	 */
	public PercentOut(RangeOut<?> out) {
		this(ScalarOutput.mapToPercent(out.output, out.min, out.max));
	}

	public PercentOut copy() {
		return new PercentOut(output);
	}
}
