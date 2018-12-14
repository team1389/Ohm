package com.team1389.auto.command;

import com.team1389.util.boolean_util.TimedBoolean;

/**
 * This command waits a given time
 * @author Kenneth
 *
 */
public class WaitTimeCommand extends WaitForBooleanCommand {
	TimedBoolean timer;

	/**
	 * @param time amount of time to wait (seconds)
	 */
	public WaitTimeCommand(double time) {
		super(null);
		timer = new TimedBoolean(time);
		untilTrue = timer;
	}

	/**
	 * initializes WaitForBooleanCommand
	 */
	@Override
	public void initialize() {
		super.initialize();
	}

}
