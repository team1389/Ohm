package com.team1389.hardware.value_types;

import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * represents the position type, considered a displacement function
 * @author amind
 *
 */
public class Position extends PIDTunableValue {

	@Override
	public PIDSourceType getValueType() {
		return PIDSourceType.kDisplacement;
	}

}
