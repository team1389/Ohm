package com.team1389.hardware.value_types;

import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * a value that can be tuned by PID (either rate or displacement)
 * 
 * @author amind
 *
 */
public abstract class PIDTunableValue extends Value {
	/**
	 * @return the function type (rate or displacement)
	 */
	public abstract PIDSourceType getValueType();
}
