package com.team1389.util.unit_conversion.dimensions;

public interface Distance extends BaseDimension {
	public double getConversionFactor();

	public static Distance makeDistanceUnit(String name, double conversionFactor) {
		return new Distance() {
			@Override
			public String name() {
				return name;
			}

			@Override
			public double getConversionFactor() {
				return conversionFactor;
			}
		};
	}

	@Override
	public default BaseDimension getBaseUnit() {
		return DistanceUnit.Meters;
	}

}
