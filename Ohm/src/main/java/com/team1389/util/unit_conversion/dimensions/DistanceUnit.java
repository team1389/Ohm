package com.team1389.util.unit_conversion.dimensions;

public enum DistanceUnit implements Distance {
	Inches(39.37007874f), Feet(3.280839895f), Meters(1), Centimeters(100), Miles(0.0006213711922f), Kilometers(
			0.001f), Millimeters(1000);

	private final double metersConversionFactor;

	DistanceUnit(double metersConversionFactor) {
		this.metersConversionFactor = metersConversionFactor;
	}

	public double getConversionFactor() {
		return metersConversionFactor;
	}
}