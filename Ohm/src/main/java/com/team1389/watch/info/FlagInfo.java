package com.team1389.watch.info;

import java.util.function.BooleanSupplier;

import edu.wpi.first.networktables.NetworkTable;

/**
 * a special boolean info that only displays if the boolean is true
 * 
 * @author amind
 *
 */
public class FlagInfo extends BooleanInfo {
	/**
	 * @param name the name of the info
	 * @param isFlag the boolean source to track
	 */
	public FlagInfo(String name, BooleanSupplier isFlag) {
		super(name, isFlag);
	}

	@Override
	public void publishUnderName(String name, NetworkTable table) {
		if (in.getAsBoolean()) {
			super.publishUnderName(name, table);
		} else {
			table.delete(name);
		}
	}

}
