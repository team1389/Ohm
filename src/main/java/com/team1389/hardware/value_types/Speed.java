package com.team1389.hardware.value_types;

import edu.wpi.first.wpilibj.PIDSourceType;
/**
 * represents the speed value type, considered a rate function
 * @author amind
 *
 */
public class Speed extends PIDTunableValue{

	@Override
	public PIDSourceType getValueType() {
		return PIDSourceType.kRate;
	}

}
