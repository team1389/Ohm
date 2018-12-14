package com.team1389.util.unit_conversion.dimensions;

public enum TimeUnit implements BaseDimension {
	Milliseconds(1), Centiseconds(100), Seconds(1000), Minutes(60), Hours(3600);

	private final float baseConversionFactor;

	TimeUnit(float secondConversionFactor) {
		this.baseConversionFactor = secondConversionFactor;
	}

	public double getConversionFactor() {
		return baseConversionFactor;
	}

	@Override
	public BaseDimension getBaseUnit() {
		return TimeUnit.Milliseconds;
	}
}