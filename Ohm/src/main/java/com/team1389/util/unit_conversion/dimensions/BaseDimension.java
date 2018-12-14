package com.team1389.util.unit_conversion.dimensions;

import java.util.ArrayList;

interface BaseDimension extends PhysicalUnit {
	
	/**
	 * @return how many units/base unit
	 */
	double getConversionFactor();

	public BaseDimension getBaseUnit();

	public default ArrayList<BaseDimension> getDenominatorUnits() {
		return new ArrayList<BaseDimension>();
	}

	public default ArrayList<BaseDimension> getNumeratorUnits() {
		ArrayList<BaseDimension> singleUnit = new ArrayList<>();
		singleUnit.add(this);
		return singleUnit;
	}
}
