package com.team1389.util.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * an ArrayList with new methods for adding to the list, used for easier method chaining
 * 
 * @author amind
 *
 * @param <T> the list type
 */
public class AddList<T> extends ArrayList<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * adds the array of objects to the list
	 * @param ts the array to add
	 * @return this list
	 */
	@SuppressWarnings("unchecked")
	public AddList<T> put(T... ts) {
		addAll(Arrays.asList(ts));
		return this;
	}
	/**
	 * adds an object to the list
	 * @param t the object to add
	 * @return this list
	 */
	public AddList<T> put(T t) {
		add(t);
		return this;
	}
	/**
	 * adds the list of objects to this list
	 * @param ts the array to add
	 * @return this list
	 */
	public AddList<T> put(List<T> ts) {
		addAll(ts);
		return this;
	}

}
