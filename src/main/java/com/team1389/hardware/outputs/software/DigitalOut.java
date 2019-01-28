package com.team1389.hardware.outputs.software;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.team1389.hardware.outputs.interfaces.BinaryOutput;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.BooleanInfo;

/**
 * a stream of booleans with methods to manipulate it
 * @author Kenneth
 *
 */
public class DigitalOut implements BooleanSupplier {
	BinaryOutput out;
	private boolean last;

	/**
	 * 
	 * @param out stream of booleans
	 */
	public DigitalOut(BinaryOutput out) {
		this.out = out;
	}

	/**
	 * @param out the boolean consumer
	 */
	public DigitalOut(Consumer<Boolean> out) {
		this(BinaryOutput.convert(out));
	}

	/**
	 * 
	 * @param onOrOff value to be passed down the stream
	 */
	public void set(boolean onOrOff) {
		this.last = onOrOff;
		out.set(onOrOff);
	}

	/**
	 * 
	 * @return this stream but inverted
	 */
	public DigitalOut getInverted() {
		BinaryOutput newOutput = BinaryOutput.invert(out);
		return new DigitalOut(newOutput);
	}

	/**
	 * adds the given digital out streams as followers of this stream
	 * @return this stream with a relay to the given followers
	 */
	public DigitalOut getWithAddedFollowers(DigitalOut... follows) {
		BinaryOutput newOutput  = val -> {
			out.set(val);
			Arrays.stream(follows).forEach(d -> d.set(val));
		};
		return new DigitalOut(newOutput);

	}

	/**
	 * 
	 * @param name the String identifier of the watchable
	 * @return a watchable that tracks the boolean stream
	 */
	public Watchable getWatchable(String name) {
		return new BooleanInfo(name, this);
	}

	/**
	 * generates a new stream that passes the output values through the given unary operation to the
	 * original stream
	 * @param operation the unary operation (takes a boolean value returns a mapped value)
	 * @return the mapped stream
	 */
	public DigitalOut getMapped(UnaryOperator<Boolean> operation) {
		BinaryOutput newOut = BinaryOutput.map(out, operation);
		return new DigitalOut(newOut);
	}

	/**
	 * @return value of the stream
	 */
	@Override
	public boolean getAsBoolean() {
		return last;
	}
}
