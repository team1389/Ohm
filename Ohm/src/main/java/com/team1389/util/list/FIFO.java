package com.team1389.util.list;

import java.util.LinkedList;

/**
 * employs the <a href="https://en.wikipedia.org/wiki/FIFO_(computing_and_electronics)">FIFO</a> method to keep a fixed-size buffer of values
 * 
 * @author amind
 *
 * @param <T> the type of the list
 */
@SuppressWarnings("serial")
public class FIFO<T> extends LinkedList<T> {
	int bufferSize;

	/**
	 * @param maxSize the buffer size
	 */
	public FIFO(int maxSize) {
		this.bufferSize = maxSize;
	}

	/**
	 * adds an element to the FIFO que. If the size of the que is already larger than the buffer, values are removed until there is room for the new element
	 */
	@Override
	public void push(T val) {
		while (size() >= bufferSize) {
			remove();
		}
		add(val);
	}

	/**
	 * @param val the array to average
	 * @return the average of all values in the FIFO array
	 */
	public static double average(FIFO<Double> val) {
		return val.stream().mapToDouble(a -> a).average().getAsDouble();
	}

	/**
	 * 
	 * @param bufferSize the new buffer size
	 */
	public void setQueSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}
