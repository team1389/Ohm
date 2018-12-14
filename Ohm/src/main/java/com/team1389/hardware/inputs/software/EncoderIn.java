package com.team1389.hardware.inputs.software;

import java.util.Optional;
import java.util.function.Supplier;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.PIDTunableValue;
import com.team1389.util.unit_conversion.dimensions.Distance;
import com.team1389.util.unit_conversion.dimensions.DistanceUnit;
import com.team1389.util.unit_conversion.dimensions.PhysicalUnit;

public class EncoderIn<T extends PIDTunableValue> extends RangeIn<T> {
	static Optional<Double> wheelDiameter = Optional.empty();
	protected Distance rotations;
	boolean diameterSet = false;
	

	
	public EncoderIn(Class<T> type, ScalarInput<T> val, int encoderTicks) {
		this(type, val, 0, encoderTicks);
	}
	
	public EncoderIn(Class<T> type, ScalarInput<T> val, double min, int encoderTicks){
		super(type, val, min, encoderTicks);
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass it the same class
	 * as the Range type, i.e. for a RangeIn{@literal<Position>}, pass it <em>Position.class</em>
	 * 
	 * @param type the value type of the stream
	 * @param val the input supplier
	 * @param min the minimum value of the range
	 * @param encoderTicks the maximum value of the range
	 */
	public EncoderIn(Class<T> type, Supplier<Double> val, int encoderTicks) {
		this(type, val::get, encoderTicks);
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass it the same class
	 * as the Range type, i.e. for a RangeIn{@literal<Position>}, pass it <em>Position.class</em>
	 * 
	 * @param type the value type of the stream
	 * @param min the minimum value of the range
	 * @param max the maximum value of the range
	 */
	public EncoderIn(Class<T> type, int encoderTicks) {
		this(type, () -> 0.0, encoderTicks);
	}

	/**
	 * 
	 * @param ticks
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <R extends EncoderIn<T>>  R setTicksPerRotation(int ticks) {
		this.setRange(0, ticks);
		return (R) this;
	}

	/**
	 * @param diameter the wheel diameter in inches
	 * @return the same stream
	 */
	@SuppressWarnings("unchecked")
	public <R extends EncoderIn<T>> R setWheelDiameter(double diameter) {
		double conversionFactor = 1
				/ PhysicalUnit.convert(diameter * Math.PI, DistanceUnit.Inches, DistanceUnit.Meters);
		rotations = Distance.makeDistanceUnit("rotations", conversionFactor); //Rotations per meter
		diameterSet = true;
		return (R) this;
	}

	

	/**
	 * sets the default wheel diameter for all position streams. if
	 * {@link EncoderIn#setWheelDiameter(double) setWheelDiameter} is not called, this diameter will
	 * be used instead.
	 * 
	 * @param diameter the default diameter in inches
	 */
	public static void setGlobalWheelDiameter(double diameter) {
		wheelDiameter = Optional.of(diameter);
	}

	public static double getGlobalWheelDiameter() {
		return wheelDiameter.orElse(Double.NaN);
	}
	

	/**
	 * Might be null
	 * @return The distance unit representing one rotation
	 */
	public Distance getRotationsUnit(){
		return rotations;
	}
	
}
