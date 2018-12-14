package com.team1389.util.unit_conversion.dimensions;

import java.util.ArrayList;

public class UsefulUnits {

	
	public enum Speed implements PhysicalUnit{
		MILES_PER_HOUR(DistanceUnit.Miles, TimeUnit.Hours), METERS_PER_SECOND(DistanceUnit.Meters, TimeUnit.Seconds), KILOMETERS_PER_HOUR(DistanceUnit.Kilometers, TimeUnit.Hours), FEET_PER_SECOND(DistanceUnit.Feet, TimeUnit.Seconds);
		
		public final BaseDimension top;
		public final BaseDimension bottom;
		Speed(BaseDimension top, BaseDimension bottom){
			this.top = top;
			this.bottom = bottom;
		}
		@Override
		public ArrayList<BaseDimension> getNumeratorUnits() {
			return PhysicalUnit.makeList(top);
		}
		@Override
		public ArrayList<BaseDimension> getDenominatorUnits() {
			return PhysicalUnit.makeList(bottom);
		}
	}
}
