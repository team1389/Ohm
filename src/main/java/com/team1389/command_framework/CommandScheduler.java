package com.team1389.command_framework;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import com.team1389.command_framework.command_base.Command;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.StringInfo;

/**
 * creates queue of commands to execute, and executes them in the order they were added
 * 
 * @author Kenneth
 *
 */
public class CommandScheduler implements CompositeWatchable {
	List<Command> executing;
	String name;

	/**
	 * initializes the scheduler
	 */
	public CommandScheduler() {
		this("Scheduler");
		executing = new ArrayList<Command>();
	}

	/**
	 * 
	 * @param name the string identifier of this scheduler
	 */
	public CommandScheduler(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param command added to List
	 */
	public void schedule(Command command) {
		if (command == null) {
			System.out.println("cannot schedule null command");
			return;
		}
		executing.add(command);
	}

	/**
	 * executes commands in List until none remain
	 */
	public void update() {
		ListIterator<Command> iter = executing.listIterator();
		while (iter.hasNext()) {
			if (iter.next().exec()) {
				iter.remove();
			}
		}

	}

	/**
	 * 
	 * @return if no more commands in list
	 */
	public boolean isFinished() {
		return executing.isEmpty();
	}

	/**
	 * reset list of commands
	 */
	public void cancelAll() {
		for (Command c : executing) {
			c.cancel();
		}
		executing = new ArrayList<Command>();
	}

	/**
	 * create {@link Watchable} with values of list under key
	 */
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(new StringInfo("Executing Commands", () -> getCurrentlyExecutingCommands()));
	}

	private String getCurrentlyExecutingCommands() {
		return executing.stream().map(command -> command.getName()).collect(Collectors.joining(","));
	}

	@Override
	public String getName() {
		return name;
	}
}
