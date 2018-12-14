package com.team1389.hardware.inputs.software;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.team1389.hardware.inputs.interfaces.BinaryInput;
import com.team1389.hardware.inputs.interfaces.ListeningBinaryInput;
import com.team1389.watch.info.BooleanInfo;

/**
 * a boolean input stream. The purpose of this class wrapped around {@link BinaryInput} is to make
 * the stream operations chainable (you can call them on a stream instance and they return the same
 * stream instance)
 * 
 * @author amind
 *
 */
public class DigitalIn {
	private BinaryInput input;

	/**
	 * @param input the supplier of boolean values
	 */
	public DigitalIn(Supplier<Boolean> input) {
		this.input = input::get;
	}

	/**
	 * 
	 * @param in the supplier of boolean values
	 * @param type the desired input filter
	 */
	public DigitalIn(BinaryInput in, InputFilter type) {
		this(in);
		getSpecial(type);
	}

	/**
	 * @param name the String identifier of the watchable
	 * @return a watchable that tracks the value of this stream
	 */
	public BooleanInfo getWatchable(String name) {
		return new BooleanInfo(name, input::get);
	}

	/**
	 * @param filterType the desired input filter
	 * @return the filtered stream
	 */
	public DigitalIn getSpecial(InputFilter filterType) {
		switch (filterType) {
		case LATCHED:
			return latched();
		case TOGGLED:
			return toggled();
		default:
			return this;
		}
	}

	/**
	 * @author amind represents a filter to apply to a boolean stream
	 * @see com.team1389.util.boolean_util.LatchedBoolean LatchedBoolean
	 * @see com.team1389.util.boolean_util.ToggleBoolean ToggleBoolean
	 */
	public enum InputFilter {
		/**
		 * 
		 */
		LATCHED,
		/**
		 * 
		 */
		TOGGLED;
	}

	/**
	 * @return the latched version of this boolean stream
	 * @see com.team1389.util.boolean_util.LatchedBoolean LatchedBoolean
	 */
	public DigitalIn latched() {
		this.input = BinaryInput.getLatched(input);
		return this;
	}

	/**
	 * @return the toggled version of this boolean stream
	 * @see com.team1389.util.boolean_util.ToggleBoolean ToggleBoolean
	 */
	public DigitalIn toggled() {
		this.input = BinaryInput.getToggled(input);
		return this;
	}

	/**
	 * combines a list of boolean streams into a single stream that returns true if and only if all
	 * of the original streams are true
	 * 
	 * @param toCombine the list of streams to combine
	 * @return the combined stream
	 */
	public DigitalIn combineAND(DigitalIn... toCombine) {
		Stream<BinaryInput> inps = Arrays.stream(toCombine).map(it -> it.input);
		this.input = BinaryInput.combineAND(Stream.concat(inps, Stream.of(input)).toArray(BinaryInput[]::new));
		return this;
	}

	/**
	 * combines a list of boolean streams into a single stream that returns true if any of the
	 * original streams are true
	 * 
	 * @param toCombine the list of streams to combine
	 * @return the combined stream
	 */
	public DigitalIn combineOR(DigitalIn... toCombine) {
		Stream<BinaryInput> inps = Arrays.stream(toCombine).map(it -> it.input);
		this.input = BinaryInput.combineOR(Stream.concat(inps, Stream.of(input)).toArray(BinaryInput[]::new));
		return this;
	}

	/**
	 * @return the inverted boolean stream
	 */
	public DigitalIn invert() {
		this.input = BinaryInput.invert(input);
		return this;
	}

	/**
	 * generates a new stream that passes the output values through the given unary operation to the
	 * original stream
	 * 
	 * @param operation the unary operation (takes a boolean value returns a mapped value)
	 * @return the mapped stream
	 */
	public DigitalIn map(UnaryOperator<Boolean> operation) {
		this.input = BinaryInput.map(input, operation);
		return this;
	}

	public DigitalIn addChangeListener(Consumer<Boolean> onChange, boolean asyncPoll) {
		ListeningBinaryInput listeningSource = BinaryInput.getListeningSource(input, onChange);
		input = listeningSource;
		if (asyncPoll) {
			ListenerLooper.addListener(listeningSource);
		}

		return this;
	}

	public DigitalIn copy() {
		return new DigitalIn(input);
	}

	/**
	 * @return the value of the boolean stream
	 */
	public boolean get() {
		return input.get();
	}
}
