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
 * a stream of doubles with a range, and methods to deal with conversion and
 * range of the stream. Note that all operations do not affect original stream,
 * and instead return a new stream with the operation applied
 * 
 * @author Kenneth
 *
 * @param <T>
 *            The type that the doubles in the stream represent
 */
public class RangeOut<T extends Value> implements ScalarInput<T>
{
	private double lastVal;
	protected ScalarOutput<T> output;
	private Supplier<String> operations;
	protected double min, max;

	/**
	 * 
	 * @param out
	 *                the stream that is operated upon
	 * @param min
	 *                the min value
	 * @param max
	 *                the max value
	 */
	public RangeOut(ScalarOutput<T> out, double min, double max)
	{
		this.output = out;
		this.min = min;
		this.max = max;
	}

	/**
	 * 
	 * @param out
	 *                consumer to pass stream values down to
	 * @param min
	 *                the min value
	 * @param max
	 *                the max value
	 */
	public RangeOut(Consumer<Double> out, double min, double max)
	{
		this.min = min;
		this.max = max;
		output = out::accept;
	}

	/**
	 * @return value the last set value of the output stream
	 */
	@Override
	public Double get()
	{
		return lastVal;
	}

	/**
	 * 
	 * @param val
	 *                the value to pass down the stream
	 */
	public void set(double val)
	{
		lastVal = val;
		output.set(val);
	}

	/**
	 * 
	 * @return min the min value
	 */
	public double min()
	{
		return min;
	}

	/**
	 * 
	 * @return max the max value
	 */
	public double max()
	{
		return max;
	}

	/**
	 * 
	 * @return range the magnitude of the range
	 */
	public double range()
	{
		return min() - max();
	}

	/**
	 * preserve type of stream
	 * 
	 * @param streamToCast
	 *                         Stream that will be altered to a stream of type
	 *                         <R>
	 * @return Stream of type <R>
	 */
	@SuppressWarnings("unchecked")
	private <R extends RangeOut<T>> R cast(RangeOut<T> streamToCast)
	{
		return (R) streamToCast;
	}

	public RangeOut<T> copy()
	{
		return new RangeOut<>(output, min, max);
	}

	/**
	 * maps a copy of this stream to a percent stream (you pass it percent
	 * values and it converts them to the old range before passing them down)
	 * 
	 * @return the new stream that is a copy of this stream mapped to the
	 *         aforementioned range
	 */
	public PercentOut getAsPercentOut()
	{
		RangeOut<T> newStream = this.copy();
		double oldMin = min;
		double oldMax = max;
		newStream.addOperation(d -> "-> map[percent]:[" + oldMin + "," + oldMax + "]" + " = " + d);
		return new PercentOut(newStream);
	}

	/**
	 * maps a copy of this stream to an angle stream (you pass it angle values
	 * and it converts them to the old range before passing them down)
	 * 
	 * @return the new stream that is a copy of this stream mapped to the
	 *         aforementioned range
	 */
	public <V extends PIDTunableValue> AngleOut<V> getAsAngle()
	{
		RangeOut<T> newStream = getConvertedCopy();
		double oldMin = min;
		double oldMax = max;
		newStream.addOperation(d -> "-> map[degrees]:[" + oldMin + "," + oldMax + "]" + " = " + d);
		return new AngleOut<V>(getConvertedCopy());
	}

	/**
	 * maps a copy of this stream to a given range, setting a new min and max,
	 * and adjusting the stream values to compensate
	 * 
	 * @param min
	 *                Minimum of range for new stream
	 * @param max
	 *                Maximum of range for the new stream
	 * @return copy of this stream mapped to given range
	 */
	public <R extends RangeOut<T>> R getMappedToRange(double min, double max)
	{
		double oldMin = this.min;
		double oldMax = this.max;
		ScalarOutput<T> newOutput = ScalarOutput.mapToRange(output, min, max, oldMin, oldMax);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.min = min;
		newStream.max = max;
		newStream.addOperation(d -> "-> map[" + min + "," + max + "]:[" + oldMin + "," + oldMax + "]" + " = " + d);
		return cast(newStream);
	}

	/**
	 * copies this stream and inverts the values in it
	 * 
	 * @return a copy of this stream, but inverted
	 */
	public <R extends RangeOut<T>> R getInverted()
	{

		ScalarOutput<T> newOutput = ScalarOutput.invert(output);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.addOperation(d -> "-> invert = " + d);
		return cast(newStream);
	}

	/**
	 * Maps a copy of this stream from the given range to another range, setting
	 * a new min and max, and adjusting the stream values to compensate. <br>
	 * This is the equivalent of calling
	 * {@link RangeIn#getWithSetRange(oldMin, oldMax)}, then
	 * {@link RangeIn#getMappedToRange(min, max)} Can be used to reverse the
	 * stream values as explained in
	 * {@link com.team1389.util.RangeUtil#map(double, double, double, double, double)
	 * RangeUtil.map}
	 * 
	 * @see RangeIn#mapToRange(double, double) mapToRange
	 * @param oldMin
	 *                   the claimed min of the original stream TODO make this
	 *                   clearer
	 * @param oldMax
	 *                   the claimed max of the original stream
	 * @param min
	 *                   of stream being operated on
	 * @param max
	 *                   of stream being operated on
	 * @return copy of this stream mapped to given range, as if its min and max
	 *         are the first two arguments
	 */
	public RangeOut<T> getAdjustedToRange(double oldMin, double oldMax, double min, double max)
	{
		ScalarOutput<T> newOutput = ScalarOutput.mapToRange(output, oldMin, oldMax, min, max);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.min = min;
		newStream.max = max;
		newStream.addOperation(d -> "-> map from [" + oldMin + "," + oldMax + "] to [" + min + "," + max + "] = " + d);
		return newStream;
	}

	/**
	 * create a copy of this rangeout that is a {@link ProfiledRangeOut}
	 * 
	 * @param maxChange
	 *                       the max change in position
	 * @param initialPos
	 *                       the current position
	 * @return copy of this stream of type {@link ProfiledRangeOut} with added
	 *         functionality of {@link ProfiledRangeOut}
	 */
	public <R extends RangeOut<T>> R getProfiledOut(double maxChange, double initialPos)
	{
		ScalarOutput<T> newOutput = new ProfiledRangeOut<T>(output, min, max, maxChange, initialPos);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.max = max;
		newStream.addOperation(d -> "-> profile[" + maxChange + "] = " + d);
		return cast(newStream);
	}

	/**
	 * Makes a copy of this stream with a change listener
	 * 
	 * @param onChange
	 *                     function to run if values change
	 * @return copy of this stream with added change listener
	 */
	public <R extends RangeOut<T>> R getWithChangeListener(Consumer<Double> onChange)
	{
		ScalarOutput<T> newOutput = ScalarOutput.getListeningOutput(output, onChange);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		return cast(newStream);
	}

	/**
	 * @param name
	 *                 the string identifier of this stream
	 * @return a watchable object that tracks the value of this stream
	 */
	public Watchable getWatchable(String name)
	{
		return new NumberInfo(name, this);
	}

	public Watchable getSettingWatchable(String name, double defaultVal)
	{
		return new NumberInput(name, defaultVal, this::set);
	}

	/**
	 * adds a stream as a follower to a copy of this stream; The follower stream
	 * will now be passed any value this stream recieves. Output is not mapped
	 * if follower range is different from master stream's range
	 * <p>
	 * 
	 * @param outFollow
	 *                      stream to add as a follower
	 * @return a copy of this stream but with an additional follower
	 */
	public <R extends RangeOut<T>> R getWithAddedFollowers(RangeOut<T> outFollow)
	{
		ScalarOutput<T> newOutput = val ->
		{
			output.set(val);
			outFollow.set(val);
		};

		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		return cast(newStream);
	}

	/**
	 * creates a copy of this stream that replaces the value of anything in the
	 * deadzone with 0.0
	 * 
	 * @param deadband
	 *                     the max distance from 0 (deadzone)
	 * @return copy of this stream with Deadband of {@code deadband}
	 */
	public <R extends RangeOut<T>> R getWithDeadband(double deadband)
	{
		ScalarOutput<T> newOutput = ScalarOutput.applyDeadband(output, deadband);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.addOperation(d -> "-> deadband[" + deadband + "] = " + d);
		return cast(newStream);
	}

	/**
	 * constrains values in the stream to be between min and max of this stream
	 * 
	 * @return new stream that is limited to a range
	 */
	public <R extends RangeOut<T>> R getClamped()
	{
		return getLimited(min, max);
	}

	/**
	 * clamps the stream within the range of [-abs , abs]
	 * 
	 * @param abs
	 *                the absolute value of min/max of range
	 * @return clamped stream
	 */
	public <R extends RangeOut<T>> R getLimited(double abs)
	{
		return getLimited(-abs, abs);
	}

	/**
	 * creates a copy of this stream which constrains values in the stream to be
	 * between min and max arguments
	 * 
	 * @param min
	 *                the min value of desired capped range
	 * @param max
	 *                the max value of desired capped range
	 * @return copy of this stream that is limited to the desired range
	 */
	public <R extends RangeOut<T>> R getLimited(double min, double max)
	{
		ScalarOutput<T> newOutput = ScalarOutput.limit(output, min, max);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.min = min;
		newStream.max = max;
		newStream.addOperation(d -> "-> limit[" + min + "," + max + "] = " + d);
		return cast(newStream);
	}

	/**
	 * 
	 * @param offsetAmt
	 *                      the stream that holds the values which this stream
	 *                      is offset by
	 * @return a copy of this stream with all the values offset by the value in
	 *         the input stream
	 */
	public <R extends RangeOut<T>> R getOffset(ScalarInput<?> offsetAmt)
	{
		ScalarOutput<T> newOutput = ScalarOutput.offset(output, offsetAmt);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		return cast(newStream);
	}

	/**
	 * 
	 * @param amt
	 *                the value that is added to every value in the copy of this
	 *                stream
	 * @return a copy of this stream but with {@code amt} added to every value
	 */
	public <R extends RangeOut<T>> R getOffset(double amt)
	{
		return getOffset(() ->
		{
			return amt;
		});
	}

	/**
	 * 
	 * @param factor
	 *                   value to scale output by
	 * @return a copy of this stream but with min, max, and val multiplied by
	 *         {@code factor}
	 */
	public RangeOut<T> getScaled(double factor)
	{
		ScalarOutput<T> newOutput = ScalarOutput.scale(output, factor);
		double newMax = factor * max;
		double newMin = factor * min;
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		newStream.max = newMax;
		newStream.min = newMin;
		return newStream;
	}

	/**
	 * makes a copy of this stream that is the desired value type
	 * 
	 * @return a copy of this stream with the same values but the new type
	 */
	protected <N extends Value> RangeOut<N> getConvertedCopy()
	{
		return new RangeOut<N>(output, min, max);
	}

	/**
	 * creates a copy of this stream with a new min and max value <br>
	 * <em>NOTE</em>: Unlike {@link RangeOut#getMappedToRange(double, double)
	 * mapToRange}, this operation does not affect the values in the stream,
	 * only the range.
	 * 
	 * @param min
	 *                value to set as min
	 * @param max
	 *                value to set as max
	 * @return copy of this stream but with a new min and max
	 */
	public RangeOut<T> getWithSetRange(int min, int max)
	{
		double newMin = min;
		double newMax = max;
		RangeOut<T> newStream = this.copy();
		newStream.min = newMin;
		newStream.max = newMax;
		return newStream;
	}

	/**
	 * applies the given operation to the values coming through a copy of this
	 * stream
	 * 
	 * @param operation
	 *                      the unary operation (takes a double value returns a
	 *                      mapped value)
	 * @return a copy of this stream, but mapped
	 */
	public RangeOut<T> getMapped(UnaryOperator<Double> operation)
	{
		ScalarOutput<T> newOutput = ScalarOutput.map(output, operation);
		RangeOut<T> newStream = this.copy();
		newStream.output = newOutput;
		return newStream;
	}

	private void addOperation(Function<String, String> operation)
	{
		ListeningOutput<Double> lastVal = new ListeningOutput<Double>(output, k ->
		{
		}, 0.0);
		this.output = lastVal::accept;
		Supplier<String> oldOperations = operations;
		operations = () -> operation.apply(DisplayDouble.get(lastVal.getLatestVal(), 7)) + oldOperations.get();
	}

	public String toString()
	{
		return "Last applied value: " + lastVal;
	}

}
