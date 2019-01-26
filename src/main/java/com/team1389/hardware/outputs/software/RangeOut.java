package com.team1389.hardware.outputs.software;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.interfaces.ListeningOutput;
import com.team1389.hardware.outputs.interfaces.ScalarOutput;
import com.team1389.hardware.value_types.PIDTunableValue;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.DisplayDouble;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.NumberInfo;
import com.team1389.watch.input.listener.NumberInput;

/**
 * a stream of doubles with a range, and methods to deal with conversion and range of the stream
 * 
 * @author Kenneth
 *
 * @param <T> The type that the doubles in the stream represent
 */
public class RangeOut<T extends Value> implements ScalarInput<T> {
	private double lastVal;
	protected ScalarOutput<T> output;
	protected double min, max;

	/**
	 * 
	 * @param out the stream that is operated upon
	 * @param min the min value
	 * @param max the max value
	 */
	public RangeOut(ScalarOutput<T> out, double min, double max) {
		this.output = out;
		this.min = min;
		this.max = max;
	}

	/**
	 * 
	 * @param out consumer to pass stream values down to
	 * @param min the min value
	 * @param max the max value
	 */
	public RangeOut(Consumer<Double> out, double min, double max) {
		this.min = min;
		this.max = max;
		output = out::accept;
	}

	/**
	 * @return value the last set value of the output stream
	 */
	@Override
	public Double get() {
		return lastVal;
	}

	/**
	 * 
	 * @param val the value to pass down the stream
	 */
	public void set(double val) {
		lastVal = val;
		output.set(val);
	}

	/**
	 * 
	 * @return min the min value
	 */
	public double min() {
		return min;
	}

	/**
	 * 
	 * @return max the max value
	 */
	public double max() {
		return max;
	}

	/**
	 * 
	 * @return range the magnitude of the range
	 */
	public double range() {
		return min() - max();
	}

	/**
	 * preserve type of stream
	 * 
	 * @return this stream with the type it was called from
	 */
	@SuppressWarnings("unchecked")
	private <R extends RangeOut<T>> R cast(RangeOut<T> streamToCast) {
		return (R) streamToCast;
	}

	public RangeOut<T> copy() {
		return new RangeOut<>(output, min, max);
	}

	/**
	 * maps this stream to a percent stream (you pass it percent values and it converts them to the
	 * old range before passing them down)
	 * 
	 * @return the new stream that is this stream mapped to the aforementioned range
	 */
	public PercentOut mapToPercentOut() {
		return new PercentOut(this.copy());
	}

	/**
	 * maps this stream to an angle stream (you pass it angle values and it converts them to the old
	 * range before passing them down)
	 * 
	 * @return the new stream that is this stream mapped to the aforementioned range
	 */
	public <V extends PIDTunableValue> AngleOut<V> mapToAngle() {
		return new AngleOut<V>(getConvertedCopy());
	}

	/**
	 * maps the stream to a given range, setting a new min and max, and adjusting the stream values
	 * to compensate
	 * 
	 * @param min of stream being operated on
	 * @param max of stream being operated on
	 * @return new stream mapped to given range
	 */
	public <R extends RangeOut<T>> R mapToRange(double min, double max) {
		ScalarOutput<T> newOutput = ScalarOutput.mapToRange(output, min, max, this.min, this.max);
		RangeOut<T> newStream = new RangeOut<T>(newOutput, min, max);
		return cast(newStream);
	}

	/**
	 * invert the values in the stream
	 * 
	 * @return new inverted stream
	 */
	public <R extends RangeOut<T>> R invert() {

		ScalarOutput<T> newOutput = ScalarOutput.invert(output);
		RangeOut<T> newStream = new RangeOut<T>(newOutput, min, max);
		return cast(newStream);
	}

	/**
	 * Maps the stream from the given range to another range, setting a new min and max, and
	 * adjusting the stream values to compensate. <br>
	 * This is the equivalent of calling {@link RangeIn#setRange(oldMin, oldMax)}, then
	 * {@link RangeIn#mapToRange(min, max)} Can be used to reverse the stream values as explained in
	 * {@link com.team1389.util.RangeUtil#map(double, double, double, double, double) RangeUtil.map}
	 * 
	 * @see RangeIn#mapToRange(double, double) mapToRange
	 * @param oldMin the claimed min of the original stream TODO make this clearer
	 * @param oldMax the claimed max of the original stream
	 * @param min of stream being operated on
	 * @param max of stream being operated on
	 * @return new stream mapped to given range
	 */
	public RangeOut<T> adjustRange(double oldMin, double oldMax, double min, double max) {
		ScalarOutput<T> newOutput = ScalarOutput.mapToRange(output, oldMin, oldMax, min, max);
		return new RangeOut<T>(newOutput, min, max);
	}

	/**
	 * apply a {@link ProfiledRangeOut} to the output stream
	 * 
	 * @param maxChange the max change in position
	 * @param initialPos the current position
	 * @return new stream of type {@link ProfiledRangeOut}
	 */
	public <R extends RangeOut<T>> R getProfiledOut(double maxChange, double initialPos) {
		ScalarOutput<T> newOutput = new ProfiledRangeOut<T>(output, min, max, maxChange, initialPos);
		return cast();
	}

	/**
	 * add a change listener to the stream
	 * 
	 * @param onChange what to run if values change
	 * @return new stream with new change listener TODO fix this to use consumer
	 */
	public <R extends RangeOut<T>> R addChangeListener(Consumer<Double> onChange) {
		output = ScalarOutput.getListeningOutput(output, onChange);
		return cast();
	}

	/**
	 * @param name the string identifier of this stream
	 * @return a watchable object that tracks the value of this stream
	 */
	public Watchable getWatchable(String name) {
		return new NumberInfo(name, this);
	}

	public Watchable getSettingWatchable(String name, double defaultVal) {
		return new NumberInput(name, defaultVal, this::set);
	}

	/**
	 * adds a stream as a follower; The stream will now be passed any value this stream recieves.
	 * <p>
	 * If the follower stream has a different range than the leader stream, the input will be mapped
	 * to the follower's range before being passed
	 * 
	 * @param outFollow stream to add as a follower
	 * @return this stream but with an additional follower
	 */
	public <R extends RangeOut<T>> R addFollowers(RangeOut<T> outFollow) {
		ScalarOutput<T> out = this.output;
		output = val -> {
			out.set(val);
			outFollow.set(val);
		};

		return cast();
	}

	/**
	 * causes the stream to replace the value of anything in the deadzone with 0.0
	 * 
	 * @param deadband the max distance from 0 (deadzone)
	 * @return stream with Deadband of {@code deadband}
	 */
	public <R extends RangeOut<T>> R applyDeadband(double deadband) {
		addOperation(d -> "-> deadband[" + deadband + "] = " + d);
		output = ScalarOutput.applyDeadband(output, deadband);
		return cast();
	}

	/**
	 * constrains values in the stream to be between min and max of this stream
	 * 
	 * @return new stream that is limited to a range
	 */
	public <R extends RangeOut<T>> R clamp() {
		return limit(min, max);
	}

	/**
	 * clamps the stream within the range of [-abs , abs]
	 * 
	 * @param abs the absolute value of min/max of range
	 * @return clamped stream
	 */
	public <R extends RangeOut<T>> R limit(double abs) {
		return limit(-abs, abs);
	}

	/**
	 * constrains values in the stream to be between min and max arguments
	 * 
	 * @param min the min value of desired capped range
	 * @param max the max value of desired capped range
	 * @return new stream that is limited to a range
	 */
	public <R extends RangeOut<T>> R limit(double min, double max) {
		addOperation(d -> "-> limit[" + min + "," + max + "] = " + d);
		output = ScalarOutput.limit(output, min, max);
		return cast();
	}

	/**
	 * 
	 * @param offsetAmt the stream that holds the values which this stream is offset by
	 * @return this stream but with all the values offset by the value in the input stream
	 */
	public <R extends RangeOut<T>> R offset(ScalarInput<?> offsetAmt) {
		output = ScalarOutput.offset(output, offsetAmt);
		return cast();
	}

	/**
	 * 
	 * @param amt the value that is added to every value in this stream
	 * @return this stream but with {@code amt} added to every value
	 */
	public <R extends RangeOut<T>> R offset(double amt) {
		return offset(() -> {
			return amt;
		});
	}

	/**
	 * 
	 * @param factor value to scale by
	 * @return this stream but with min, max, and val multiplied by {@code factor}
	 */
	public RangeOut<T> scale(double factor) {
		output = ScalarOutput.scale(output, factor);
		max *= factor;
		min *= factor;
		return this;
	}

	/**
	 * converts the stream to the desired value type
	 * 
	 * @return a new stream with the same values but the new type
	 */
	protected <N extends Value> RangeOut<N> getConvertedCopy() {
		return new RangeOut<N>(output, min, max);
	}

	/**
	 * sets the min and max values of the stream <br>
	 * <em>NOTE</em>: Unlike {@link RangeOut#mapToRange(double, double) mapToRange}, this operation
	 * does not affect the values in the stream, only the range.
	 * 
	 * @param min value to set as min
	 * @param max value to set as max
	 * @return this stream but with a new min and max
	 */
	public RangeOut<T> setRange(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	}

	/**
	 * applies the given operation to the values coming through this stream
	 * 
	 * @param operation the unary operation (takes a double value returns a mapped value)
	 * @return the mapped stream
	 */
	public RangeOut<T> map(UnaryOperator<Double> operation) {
		output = ScalarOutput.map(output, operation);
		return this;
	}

	public String toString() {
		return lastVal + operations.get();
	}

}
