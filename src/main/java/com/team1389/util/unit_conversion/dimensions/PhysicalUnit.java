package com.team1389.util.unit_conversion.dimensions;

import java.util.ArrayList;

import com.team1389.util.unit_conversion.Unit;
/**
 * the front facing class of the unit conversion package, 
 * @author ari
 *
 */
public interface PhysicalUnit extends Unit{

	public String name();

	public ArrayList<BaseDimension> getNumeratorUnits();

	public ArrayList<BaseDimension> getDenominatorUnits();

	public static ArrayList<BaseDimension> makeList(BaseDimension... dimensions){
		ArrayList<BaseDimension> list = new ArrayList<BaseDimension>();
		for(BaseDimension d : dimensions){
			list.add(d);
		}
		return list;
	}
	
	public static boolean checkSameDimension(PhysicalUnit one, PhysicalUnit two){
		ArrayList<BaseDimension> oneDem = one.getDenominatorUnits();
		ArrayList<BaseDimension> twoDem = two.getDenominatorUnits();

		for(int i = 0; i < oneDem.size(); i++){
			if(oneDem.get(i).getBaseUnit() != twoDem.get(i).getBaseUnit()){
				return false;
			}
		}

		for(int i = 0; i < twoDem.size(); i++){
			if(twoDem.get(i).getBaseUnit() != twoDem.get(i).getBaseUnit()){
				return false;
			}
		}

		return true;
	}

	public static PhysicalUnit makeCombinedUnit(String name, ArrayList<BaseDimension> numeratorUnits, ArrayList<BaseDimension> denominatorUnits){
		return new PhysicalUnit(){

			@Override
			public String name() {
				return name;
			}

			@Override
			public ArrayList<BaseDimension> getNumeratorUnits() {
				return numeratorUnits;
			}

			@Override
			public ArrayList<BaseDimension> getDenominatorUnits() {
				return denominatorUnits;
			}
		};
	}

	public static double convert(double val, PhysicalUnit from, PhysicalUnit to){
		if(checkSameDimension(from, to)){
			double conversionFactor = 1;
			for(BaseDimension unit : from.getNumeratorUnits()){
				conversionFactor /= unit.getConversionFactor();
			}
			for(BaseDimension unit : to.getNumeratorUnits()){
				conversionFactor *= unit.getConversionFactor();
			}
			for(BaseDimension unit : from.getDenominatorUnits()){
				conversionFactor *= unit.getConversionFactor();
			}
			for(BaseDimension unit : to.getDenominatorUnits()){
				conversionFactor *= unit.getConversionFactor();
			}
			return val * conversionFactor;

		}
		else{
			throw new IllegalArgumentException();
		}
	}

	public static double getConversionFactor(PhysicalUnit from, PhysicalUnit to) {
		return convert(1, from, to);
	}
	
	public static PhysicalUnit makeSimpleUnit(String name, BaseDimension numerator, BaseDimension denominator){
		return makeCombinedUnit(name, makeList(numerator), makeList(denominator));
	}

	
	public static void main(String[] args){
		System.out.println(convert(10,
				makeCombinedUnit("m/s", makeList(DistanceUnit.Meters), makeList(TimeUnit.Seconds)), 
				 makeCombinedUnit("mph", makeList(DistanceUnit.Miles), makeList(TimeUnit.Hours))));
	}
}
