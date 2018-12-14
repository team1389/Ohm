
package com.team1389.auto.command;

import com.team1389.command_framework.command_base.Command;
/**
 * This command does nothing (what did you expect?)
 * @author amind
 *
 */
public class DoNothingCommand extends Command {

	@Override
	public boolean execute() {
		return true;
	}

}