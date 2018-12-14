package com.team1389.hardware.inputs.software;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.Speed;
import com.team1389.util.unit_conversion.dimensions.Distance;
import com.team1389.util.unit_conversion.dimensions.DistanceUnit;
import com.team1389.util.unit_conversion.dimensions.PhysicalUnit;
import com.team1389.util.unit_conversion.dimensions.TimeUnit;
import com.team1389.util.unit_conversion.dimensions.UsefulUnits;


/**
 * Initially, this class represents a stream of speeds. It is represented, initially, as encoder ticks per the passed
 * in time unit. The passed in time unit should be the default one for the encoder. You can then change the speed unit
 * to conventional dimensions, but only after you set the diameter. 
 * you set the diameter
 * @author Josh
 *
 */
public class SpeedEncoderIn extends EncoderIn<Speed> {
	
	private TimeUnit baseTimeUnit;
	private Distance baseDistanceUnit;
	
	private SpeedEncoderIn(ScalarInput<Speed> val, int encoderTicks) {
		super(Speed.class, val, 0, encoderTicks); //Speed is encoderTicks per time unit
	}

	/**
	 * 
	 * An example, a talon has 8192 encoder ticks per rotation and returns values in the unit centiseconds
	 * @param val the input supplier
	 * @param bottomUnit The time unit of the encoder 
	 * @param encoderTicks the number of ticks per rotation
	 */
	public SpeedEncoderIn(Supplier<Double> val, int encoderTicks, TimeUnit bottomUnit) {
		this(val::get, encoderTicks);
		this.baseTimeUnit = bottomUnit;
	}
	
	/**
	 * Empty constructor, encoder always returns 0s
	 * @param bottomUnit The time unit of the encoder 
	 * @param encoderTicks the number of ticks per rotation
	 */
	public SpeedEncoderIn(int encoderTicks, TimeUnit bottomUnit) {
		this(() -> 0.0, encoderTicks, bottomUnit);
	}

	/**
	 * Only call this method after a diameter has been set
	 * @param unit The speed unit to convert the speed too
	 * @return This stream, useful for chaining operations
	 */
	public SpeedEncoderIn changeStreamToThisSpeed(UsefulUnits.Speed unit){
		return convertValuesTo(unit);
	}
	

	/**
	 * Only call this method after a diameter has been set
	 * @param d The distance unit for the conversion
	 * @param t The time unit for the conversion
	 * @return This stream, useful for chaining operations
	 */
	public SpeedEncoderIn changeStreamToThisSpeed(Distance d, TimeUnit t){
		return convertValuesTo(PhysicalUnit.makeSimpleUnit("", d, t));
	}
	

	/**
	 * Maps the stream to revolutions per current time unit
	 * @return This object, useful for chaining operations
	 */
	public SpeedEncoderIn mapToRevolutionsPerTimeUnit() {
		mapToRange(-1, 1);
		return this;
	}

	private SpeedEncoderIn convertValuesTo(PhysicalUnit unit) {
		if(!PhysicalUnit.checkSameDimension(unit, UsefulUnits.Speed.METERS_PER_SECOND)){
			throw new IllegalArgumentException();
		}
		if (!diameterSet){
			try {
				setWheelDiameter(wheelDiameter.get());
			} catch (NoSuchElementException e) {
				throw new RuntimeException(
						"Cannot raw encoder ticks to " + unit.name().toLowerCase() + ". set wheel diameter first");
			}
		}

		TimeUnit timeUnit = (TimeUnit) unit.getDenominatorUnits().get(0);
		Distance distanceUnit = (Distance) unit.getNumeratorUnits().get(0);
		
		mapToRevolutionsPerTimeUnit();
		//values are now in rotations per current timeunit

		double unitNormalizedDistancePerRev = PhysicalUnit.getConversionFactor((PhysicalUnit) rotations, distanceUnit);
		scale(unitNormalizedDistancePerRev);
		//values are now in new distance units per current timeunit

		scale(1/PhysicalUnit.getConversionFactor((PhysicalUnit) baseTimeUnit, timeUnit));
		//values are now in new distance unit per new timeunit
		
		baseTimeUnit = timeUnit;
		//It is important to note that time is stored within this class, whereas distance units are stored in the max and mins of the range
		//IE if it is in rotations max and min are 1 and -1, if its in meters max and min will be something else, ect.
		
		baseDistanceUnit = distanceUnit;
		return this;
	}
	
	/**
	 * 
	 * @return The last set time unit (or the default input one)
	 */
	public TimeUnit getCurrentTimeUnit(){
		return baseTimeUnit;
	}
	
	/**
	 * May be null if no conversion has yet happened (or if diameter is not yet set)
	 * @return
	 */
	public Distance getCurrentDistanceUnit(){
		return baseDistanceUnit;
	}
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		SpeedEncoderIn test = new SpeedEncoderIn(() -> (double)((Math.random() - 0.5) * 8192 * 2) /*Max speed and min speed are 1 rotation per timeunit (second)*/, 8192, TimeUnit.Seconds);
		test.setWheelDiameter(PhysicalUnit.convert(1, DistanceUnit.Meters, DistanceUnit.Inches));
		test.changeStreamToThisSpeed(DistanceUnit.Meters, TimeUnit.Seconds);
		for(int i = 0; i < 100; i++){
			System.out.println(test.get());
		}
	}
}
