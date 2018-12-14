package com.team1389.hardware.outputs.interfaces;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.PIDTunableValue;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.RangeUtil;

/**
 * This interface represents a single double output stream, and stores static operations that can be
 * applied to it
 * 
 * @author amind
 * @param <T> the value type that the double represents
 */
public interface ScalarOutput<T extends Value> extends Consumer<Double> {
	@Override
	public default void accept(Double val) {
		this.set(val);
	}

	/**
	 * @param val the value to pass down the stream
	 */
	public void set(double val);

	/**
	 * @param consumer the consumer to convert
	 * @return a scalar output that passes its stream down to the consumer
	 */
	public static <T extends Value> ScalarOutput<T> convert(Consumer<Double> consumer) {
		return consumer::accept;
	}

	/**
	 * offsets the stream values by the current value of the given input stream
	 * 
	 * @param out the stream to operate on
	 * @param in the stream to get offset values from
	 * @return the offset stream
	 */
	public static <T extends Value> ScalarOutput<T> offset(ScalarOutput<T> out, ScalarInput<?> in) {
		return (double val) -> out.set(val + in.get());
	}

	/**
	 * maps the given stream from one range to another <br>
	 * note that streams are not confined to the range
	 * 
	 * @param out the stream to operate on
	 * @param inMin the original min value
	 * @param inMax the original max value
	 * @param outMin the min value of the output stream
	 * @param outMax the max value of the output stream
	 * @return the mapped output (does not change the value type)
	 */
	public static <T extends Value> ScalarOutput<T> mapToRange(ScalarOutput<T> out, double inMin, double inMax,
			double outMin, double outMax) {
		return val -> out.set(RangeUtil.map(val, inMin, inMax, outMin, outMax));

	}

	/**
	 * maps the given stream from any range to degrees (0 to 360)
	 * 
	 * @param out the stream to operate on
	 * @param outMin the original min value
	 * @param outMax the original max value
	 * @return the mapped output, now an angle stream
	 */
	public static <T extends PIDTunableValue> ScalarOutput<T> mapToAngle(ScalarOutput<? extends T> out, double outMin,
			double outMax) {
		return val -> out.set(RangeUtil.map(val, 0, 360, outMin, outMax));
	}

	/**
	 * maps the given stream from any range to percent (-1 to 1)
	 * 
	 * @param out the stream to operate on
	 * @param outMin the original min value
	 * @param outMax the original max value
	 * @return the mapped output, now a percent stream
	 */
	public static ScalarOutput<Percent> mapToPercent(ScalarOutput<?> out, double outMin, double outMax) {
		return val -> out.set(RangeUtil.map(val, -1, 1, outMin, outMax));

	}

	/**
	 * scales the given stream by a constant factor
	 * 
	 * @param out the stream to operate on
	 * @param scale the factor to scale
	 * @return the scaled stream (does not change the value type)
	 */
	public static <T extends Value> ScalarOutput<T> scale(ScalarOutput<T> out, double scale) {
		return val -> out.set(val * scale);

	}

	/**
	 * inverts the given stream
	 * 
	 * @param out the stream to invert
	 * @return the inverted stream (does not change the value type)
	 */
	public static <T extends Value> ScalarOutput<T> invert(ScalarOutput<T> out) {
		return val -> out.set(-val);

	}

	/**
	 * modifies the given stream to truncate values to zero if they are almost zero
	 * 
	 * @param output the stream to operate on
	 * @param deadband how close the values need to be to zero to get truncated
	 * @return the stream with a deadband (does not change the value type)
	 */
	public static <T extends Value> ScalarOutput<T> applyDeadband(ScalarOutput<T> output, double deadband) {
		return val -> output.set(RangeUtil.applyDeadband(val, deadband));
	}

	/**
	 * confines the given stream within the given range if the stream's value is outside the range,
	 * it will be replaced with the nearest edge of the range
	 * 
	 * @param out the stream to operate on
	 * @param max the max value of the limit range
	 * @param min the min value of the limit range
	 * @return the limited stream (does not change the value type)
	 */
	public static <T extends Value> ScalarOutput<T> limit(ScalarOutput<T> out, double min, double max) {
		return val -> out.set(RangeUtil.limit(val, min, max));
	}

	/**
	 * converts the stream to a {@link ListeningOutput} which runs the given runnable when the
	 * stream's value changes
	 * 
	 * @param output the stream to listen to
	 * @param onChange the runnable to call on value change
	 * @return the stream with listener attached
	 */
	public static <T extends Value> ScalarOutput<T> getListeningOutput(ScalarOutput<T> output,
			Consumer<Double> onChange) {
		return new ListeningOutput<Double>(output, onChange, 0.0)::accept;
	}

	/**
	 * generates a new stream that passes the output values through the given double unary operation
	 * to the original stream
	 * 
	 * @param output the old stream to receive mapped values
	 * @param operation the unary operation (takes a double value returns a mapped value)
	 * @return the mapped stream
	 */
	public static <T extends Value> ScalarOutput<T> map(ScalarOutput<T> output, UnaryOperator<Double> operation) {
		return d -> output.set(operation.apply(d));
	}

}
