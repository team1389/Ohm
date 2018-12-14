package com.team1389.hardware.outputs.interfaces;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * a boolean output stream
 * 
 * @author Jacob Prinz
 */
public interface BinaryOutput extends Consumer<Boolean> {
	/**
	 * @param val the value to pass down the stream
	 */
	public void set(Boolean val);

	@Override
	public default void accept(Boolean val) {
		set(val);
	}

	/**
	 * inverts the output of the stream
	 * @param out the stream to invert
	 * @return the inverted stream
	 */
	public static BinaryOutput invert(BinaryOutput out) {
		return (Boolean val) -> {
			out.set(!val);
		};
	}
	/**
	 * applies the given operation to the values coming through this stream
	 * @param output the old stream to receive mapped values
	 * @param operation the unary operation (takes a double value returns a mapped value)
	 * @return the mapped stream
	 */
	public static BinaryOutput map(BinaryOutput output, UnaryOperator<Boolean> operation){
		return b->output.set(operation.apply(b));
	}
	/**
	 * @param consumer the consumer to convert
	 * @return an output stream that passes its stream down to the given consumer
	 */
	public static BinaryOutput convert(Consumer<Boolean> consumer) {
		return consumer::accept;
	}

}
