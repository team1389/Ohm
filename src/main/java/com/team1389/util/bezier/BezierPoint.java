package com.team1389.util.bezier;

import com.team1389.util.Pair;

public class BezierPoint extends Pair<Double, Double> {

	public BezierPoint(Double one, Double two) {
		super(one, two);
	}
	
	public Double getX(){
		return getFirst();
	}
	
	public Double getY(){
		return getSecond();
	}
	
}
