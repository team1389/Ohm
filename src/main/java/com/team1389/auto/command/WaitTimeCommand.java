package com.team1389.auto.command;

import com.team1389.command_framework.command_base.Command;
import com.team1389.util.boolean_util.TimedBoolean;

public class WaitTimeCommand extends Command {
    TimedBoolean timer;

    public WaitTimeCommand(double time) {
        timer = new TimedBoolean(time);
    }

    public boolean execute() {
        return timer.get();
    }

}