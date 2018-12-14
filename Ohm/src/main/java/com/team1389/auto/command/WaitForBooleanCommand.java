package com.team1389.auto.command;

import com.team1389.command_framework.command_base.Command;
import com.team1389.hardware.inputs.interfaces.BinaryInput;

/**
 * This command waits until the given {@link BinaryInput} returns true
 * 
 * @author amind
 *
 */
public class WaitForBooleanCommand extends Command {
	BinaryInput untilTrue;

	/**
	 * 
	 * @param untilTrue the {@link BinaryInput} to wait for
	 */
	public WaitForBooleanCommand(BinaryInput untilTrue) {
		this.untilTrue = untilTrue;
	}

	@Override
	protected boolean execute() {
		return untilTrue.get();
	}

}
