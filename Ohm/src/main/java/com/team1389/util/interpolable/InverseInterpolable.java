package com.team1389.util.interpolable;

/**
 * InverseInterpolable is an interface used by an Interpolating Tree as the Key type. Given two endpoint keys and a third query key, an InverseInterpolable object can calculate the interpolation parameter of the query key on the interval [0, 1].
 * 
 * @param <T> The Type of InverseInterpolable
 * @see InterpolatingTreeMap
 */
public interface InverseInterpolable<T> {
	/**
	 * estimate how far (on [0, 1]) between this point and {@code upper} the {@code query} point lies.
	 * 
	 * @param upper the upper point
	 * @param query the query point
	 * @return The interpolation parameter on [0, 1] representing how far between this point and the upper point the query point lies.
	 */
	public double inverseInterpolate(T upper, T query);
}
