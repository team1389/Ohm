package com.team1389.controllers;

import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Value;

/**
 * an adapter from am Ohm output stream to a WPILib PIDOutput
 * 
 * @author amind
 *
 * @param <T> the value type of the output stream
 * @see PIDRangeIn
 * @see PIDController
 */
public class PIDRangeOut<T extends Value> implements edu.wpi.first.wpilibj.PIDOutput {

	RangeOut<T> outputRange;

	/**
	 * @param voltageOutput the output stream
	 */
	public PIDRangeOut(RangeOut<T> voltageOutput) {
		this.outputRange = voltageOutput;
	}

	@Override
	public void pidWrite(double output) {
		outputRange.set(output);
	}
	
	protected static <T extends Value> PIDRangeOut<T> get(RangeOut<T> out) {
		return new PIDRangeOut<>(out);
	}

}
