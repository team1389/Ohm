package com.team1389.hardware.inputs.software;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.unit_conversion.dimensions.Distance;
import com.team1389.util.unit_conversion.dimensions.DistanceUnit;
import com.team1389.util.unit_conversion.dimensions.PhysicalUnit;

public class PositionEncoderIn extends EncoderIn<Position> {

	private Distance baseDistanceUnit;

	public PositionEncoderIn(ScalarInput<Position> val, int encoderTicks) {
		super(Position.class, val, encoderTicks);
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass it the same class
	 * as the Range type, i.e. for a RangeIn{@literal<Position>}, pass it <em>Position.class</em>
	 * 
	 * @param val the input supplier
	 * @param min the minimum value of the range
	 * @param encoderTicks the maximum value of the range
	 */
	public PositionEncoderIn(Supplier<Double> val, int encoderTicks) {
		this(val::get, encoderTicks);
	}

	/**
	 * the parameter {@code type} here is to work around java's type erasure. pass it the same class
	 * as the Range type, i.e. for a RangeIn{@literal<Position>}, pass it <em>Position.class</em>
	 * 
	 * @param type the value type of the stream
	 * @param min the minimum value of the range
	 * @param max the maximum value of the range
	 */
	public PositionEncoderIn(int encoderTicks) {
		this(() -> 0.0, encoderTicks);
	}

	/**
	 * 
	 * @param unit The unit to change the stream to
	 * @return This stream, for chaining operations
	 */
	public PositionEncoderIn changeStreamTo(Distance unit) {
		return convertValuesTo(unit);
	}

	private PositionEncoderIn mapToRevolutions() {
		mapToRange(0, 1);
		return this;
	}

	public PositionEncoderIn convertValuesTo(Distance unit) {
		if (!diameterSet) {
			try {
				setWheelDiameter(wheelDiameter.get());
			} catch (NoSuchElementException e) {
				throw new RuntimeException(
						"Cannot raw encoder ticks to " + unit.name().toLowerCase() + ". set wheel diameter first");
			}
		}
		baseDistanceUnit = unit;

		// We know its a physical unit because setting the diameter makes it one
		double unitNormalizedDistancePerRev = PhysicalUnit.getConversionFactor((PhysicalUnit) rotations, unit);
		mapToRevolutions();
		scale(unitNormalizedDistancePerRev);
		return this;
	}

	/**
	 * May be null if no conversion has yet happened (or if diameter is not yet set)
	 * @return
	 */
	public Distance getCurrentDistanceUnit() {
		return baseDistanceUnit;
	}
	public PositionEncoderIn copy(){
		return new PositionEncoderIn(this.input,(int) this.max);
	}
	public PositionEncoderIn getInches() {
		return changeStreamTo(DistanceUnit.Inches);
	}
}
