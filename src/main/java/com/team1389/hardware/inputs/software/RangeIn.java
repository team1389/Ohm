package com.team1389.hardware.inputs.software;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.team1389.hardware.inputs.interfaces.BinaryInput;
import com.team1389.hardware.inputs.interfaces.ListeningScalarInput;
import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.PIDTunableValue;
import com.team1389.hardware.value_types.Value;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.NumberInfo;

/**
 * an input stream of doubles with a range and stream operations to do math on
 * the stream as values flow through
 * 
 * @author amind
 *
 * @param <T>
 *            The type that the doubles in the stream represent
 */
public class RangeIn<T extends Value> {
	protected Class<T> type;
	protected ScalarInput<T> input;// interface that represents a single method
									// that returns a double
	protected double max, min;
	Supplier<String> operations;

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass
	 * it the same class as the Range type, i.e. for a RangeIn{@literal<Position>},
	 * pass it <em>Position.class</em>
	 * 
	 * @param type
	 *            the value type of the stream
	 * @param val
	 *            the input supplier
	 * @param min
	 *            the minimum value of the range
	 * @param max
	 *            the maximum value of the range
	 */
	public RangeIn(Class<T> type, ScalarInput<T> val, double min, double max) {
		this.input = val;
		this.min = min;
		this.max = max;
		this.type = type;
		ScalarInput<T> in = input;
		this.operations = () -> "[read values] = " + in.get();
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass
	 * it the same class as the Range type, i.e. for a RangeIn{@literal<Position>},
	 * pass it <em>Position.class</em>
	 * 
	 * @param type
	 *            the value type of the stream
	 * @param val
	 *            the input supplier
	 * @param min
	 *            the minimum value of the range
	 * @param max
	 *            the maximum value of the range
	 */
	public RangeIn(Class<T> type, Supplier<Double> val, double min, double max) {
		this(type, val::get, min, max);
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass
	 * it the same class as the Range type, i.e. for a RangeIn{@literal<Position>},
	 * pass it <em>Position.class</em>
	 * 
	 * @param type
	 *            the value type of the stream
	 * @param min
	 *            the minimum value of the range
	 * @param max
	 *            the maximum value of the range
	 */
	public RangeIn(Class<T> type, double min, double max) {
		this(type, () -> 0.0, min, max);
	}

	/**
	 * @return the current value of the stream
	 */
	public double get() {
		return input.get();
	}

	/**
	 * @return the stream type
	 */
	public Class<T> getType() {
		return type;
	}

	/**
	 * @return the input stream (no range attached)
	 */
	public ScalarInput<T> getStream() {
		return input;
	}

	/**
	 * @return the minimum value of the range
	 */
	public double min() {
		return min;
	}

	/**
	 * 
	 * @return the maximum value of the range
	 */
	public double max() {
		return max;
	}

	/**
	 * 
	 * @return the magnitude of the range
	 */
	public double range() {
		return max - min;
	}

	/**
	 * 
	 * @param name
	 *            the string identifier of this stream
	 * @return a watchable that tracks the value of the stream
	 */
	public Watchable getWatchable(String name) {
		return new NumberInfo(name, this::get);
	}

	public RangeIn<T> copy() {
		return new RangeIn<T>(type, input, min, max);
	}

	/**
	 * maps this range to an angle stream (it converts any value it recieves to an
	 * angle before passing it upstream<br>
	 * the parameter {@code type} here is to work around java's type erasure. pass
	 * it the same class as the Range type, i.e. for an AngleIn{@literal<Position>},
	 * pass it <em>Position.class</em>
	 * 
	 * @param type
	 *            the angle value type
	 * @return the mapped range
	 * 
	 */
	public <V extends PIDTunableValue> AngleIn<V> mapToAngle(Class<V> type) {
		return new AngleIn<V>(convertRange(type));
	}

	/**
	 * 
	 */
	public <R extends RangeIn<T>> R reverse() {
		mapToRange(max, min);
		setRange(max, min);
		return cast();
	}

	/**
	 * maps this stream to a percent stream (it converts any value it recieves to a
	 * percentage before passing it upstream)
	 * 
	 * @return the new stream that is this stream mapped to the aforementioned range
	 */

	public PercentIn mapToPercentIn() {
		return new PercentIn(this);
	}

	@SuppressWarnings("unchecked")
	private <R extends RangeIn<T>> R cast() {
		return (R) this;
	}

	/**
	 * sets the range of this stream <br>
	 * <em>NOTE</em>: Unlike {@link RangeIn#mapToRange(double, double) mapToRange},
	 * this operation does not affect the values in the stream, only the range.
	 * 
	 * @param min
	 *            value to set as min
	 * @param max
	 *            value to set as max
	 * @return this stream but with a new min and max * @param min
	 */
	public RangeIn<T> setRange(double min, double max) {
		addOperation(d -> " -> setRange[" + min + "," + max + "] = " + d);
		this.min = min;
		this.max = max;
		return this;
	}

	/**
	 * Maps the stream to a given range, setting a new min and max, and adjusting
	 * the stream values to compensate. <br>
	 * Can be used to reverse the stream values as explained in
	 * {@link com.team1389.util.RangeUtil#map(double, double, double, double, double)
	 * RangeUtil.map}
	 * 
	 * @param min
	 *            of stream being operated on
	 * @param max
	 *            of stream being operated on
	 * @return new stream mapped to given range
	 */
	public RangeIn<T> mapToRange(double min, double max) {
		return adjustRange(this.min, this.max, min, max);
	}

	/**
	 * Maps the stream from the given range to another range, setting a new min and
	 * max, and adjusting the stream values to compensate. <br>
	 * This is the equivalent of calling {@link RangeIn#setRange(oldMin, oldMax)},
	 * then {@link RangeIn#mapToRange(min, max)} Can be used to reverse the stream
	 * values as explained in
	 * {@link com.team1389.util.RangeUtil#map(double, double, double, double, double)
	 * RangeUtil.map}
	 * 
	 * @see RangeIn#mapToRange(double, double) mapToRange
	 * @param oldMin
	 *            the claimed min of the original stream TODO make this clearer
	 * @param oldMax
	 *            the claimed max of the original stream
	 * @param min
	 *            of stream being operated on
	 * @param max
	 *            of stream being operated on
	 * @return new stream mapped to given range
	 */
	public RangeIn<T> adjustRange(double oldMin, double oldMax, double min, double max) {
		input = ScalarInput.mapToRange(input, oldMin, oldMax, min, max);
		addOperation(d -> "-> map from [" + oldMin + "," + oldMax + "] to [" + min + "," + max + "] = " + d);
		this.min = min;
		this.max = max;
		return this;
	}

	/**
	 * adds a listener to the stream which will perform the given action when the
	 * stream's value changes <br>
	 * <em>NOTE</em>: The stream only registers changes when its
	 * {@link RangeIn#get() get()} method is called periodically.
	 * 
	 * @param onChange
	 *            the action to perform when the stream value changes
	 * @return the stream with listener attached
	 */
	public <R extends RangeIn<T>> R addChangeListener(Consumer<Double> onChange) {
		ListeningScalarInput<T> listeningInput = ScalarInput.getListeningInput(input, onChange);
		input = listeningInput;
		return cast();
	}

	/**
	 * causes the stream to replace the value of anything in the deadzone with 0.0
	 * 
	 * @param deadband
	 *            the max distance from 0 (deadzone)
	 * @return stream with deadband of {@code deadband}
	 */
	public <R extends RangeIn<T>> R applyDeadband(double deadband) {
		input = ScalarInput.applyDeadband(input, deadband);
		addOperation(d -> " -> deadband[" + deadband + "] = " + d);
		return cast();
	}

	/**
	 * Inverts the stream values: {@code val=-val}.<br>
	 * To <b>reverse</b> the stream, use the
	 * {@link RangeIn#mapToRange(double, double) map} function
	 * 
	 * @return the inverted stream
	 */
	public <R extends RangeIn<T>> R invert() {
		input = ScalarInput.invert(input);
		addOperation(d -> " -> invert = " + d);
		return cast();
	}

	/**
	 * scales the stream values by the given factor
	 * 
	 * @param factor
	 *            the factor to scale by
	 * @return the scaled stream
	 */
	public <R extends RangeIn<T>> R scale(double factor) {
		ScalarInput<T> in = input;
		input = ScalarInput.scale(input, factor);
		addOperation(d -> " -> scale[" + in.get() + "*" + factor + "] = " + d);
		max *= factor;
		min *= factor;
		return cast();
	}

	/**
	 * wraps values outside the stream range in through the other side.
	 * 
	 * @return the wrapped stream
	 */
	public <R extends RangeIn<T>> R getWrapped() {
		input = ScalarInput.getWrapped(input, min(), max());
		addOperation(d -> " -> wrap[" + min + "," + max + "] = " + d);
		return cast();
	}

	/**
	 * sums the values of the this stream and the given stream, producing a single
	 * combined stream
	 * 
	 * @param rngIn
	 *            the stream to add to this one
	 * @return the combined stream
	 */
	public <R extends RangeIn<T>> R sumInputs(RangeIn<T> rngIn) {
		ScalarInput<T> in = input;
		input = ScalarInput.sum(input, rngIn.input);
		addOperation(d -> " -> sum[" + in.get() + "+" + rngIn.get() + "] = " + d);
		return cast();
	}

	public <R extends RangeIn<T>> R offset(double val) {
		ScalarInput<T> in = input;
		input = () -> in.get() + val;
		addOperation(d -> " -> sum[" + in.get() + "+" + val + "] = " + d);
		return cast();
	}

	public <R extends RangeIn<T>> R adjustOffsetToMatch(double makeCurrentVal) {
		return offset(makeCurrentVal - get());
	}

	/**
	 * constrains values in the stream to be between min and max of this stream
	 * 
	 * @return new stream that is limited to a range
	 */
	public <R extends RangeIn<T>> R clamp() {
		return limit(min, max);
	}

	/**
	 * clamps the stream within the range of [-abs , abs]
	 * 
	 * @param abs
	 *            the absolute value of min/max of range
	 * @return clamped stream
	 */
	public <R extends RangeIn<T>> R limit(double abs) {
		return limit(-abs, abs);
	}

	/**
	 * constrains values in the stream to be between min and max arguments
	 * 
	 * @param min
	 *            the min value of desired capped range
	 * @param max
	 *            the max value of desired capped range
	 * @return new stream that is limited to a range
	 */
	public <R extends RangeIn<T>> R limit(double min, double max) {
		input = ScalarInput.limitRange(input, min, max);
		addOperation(d -> " -> limit[" + min + "," + max + "] = " + d);
		return cast();
	}

	/**
	 * converts the stream to the desired value type
	 * 
	 * @param type
	 *            the stream type;
	 * 
	 * @return a new stream with the same values but the new type
	 */
	private <N extends Value> RangeIn<N> convertRange(Class<N> type) {
		return new RangeIn<N>(type, input, min, max);
	}

	/**
	 * creates a boolean source that returns true when the value of the RangeIn is
	 * within the given range
	 * 
	 * @param rangeMin_inclusive
	 *            the lower limit of the range to compare values to
	 * @param rangeMax_exclusive
	 *            the upper limit of the range to compare values to
	 * @return a boolean source that represents whether the current value of the
	 *         RangeIn is within the range
	 */
	public BinaryInput getWithinRange(double rangeMin_inclusive, double rangeMax_exclusive) {
		return () -> {
			double get = get();
			return get < rangeMax_exclusive && get >= rangeMin_inclusive;
		};
	}

	public void clone(RangeIn<T> toClone) {
		this.input = toClone.input;
		this.min = toClone.min;
		this.max = toClone.max;
		this.operations = toClone.operations;
	}

	/**
	 * applies the given operation to the values coming through this stream
	 * 
	 * @param operation
	 *            the unary operation (takes a double value returns a mapped value)
	 * @return the mapped stream
	 */
	public <R extends RangeIn<T>> R map(UnaryOperator<Double> operation) {
		input = ScalarInput.map(input, operation);
		return cast();
	}

	@Override
	public String toString() {
		return operations.get() + " -> [result]";
	}

	protected void addOperation(Function<Double, String> operation) {
		ScalarInput<T> in = input;
		Supplier<String> oldOperations = operations;
		operations = () -> oldOperations.get().concat(operation.apply(in.get()));
	}

}
