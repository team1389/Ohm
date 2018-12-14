package com.team1389.command_framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.team1389.command_framework.command_base.Command;

/**
 * contains useful static operations for commands
 * 
 * @author amind
 *
 */
public class CommandUtil {
	/**
	 * combines the given array of commands into a single command that runs the
	 * array of commands simultaneously
	 * 
	 * @param commands
	 *            the array of commands to combine
	 * @return the simultaneous combined command
	 */
	public static Command combineSequential(Command... commands) {
		assert !Arrays.asList(commands).contains(null) : "cannot combine null command";
		return new Command() {
			int currentIndex = 0;

			@Override
			public boolean execute() {
				if (currentIndex >= commands.length) {
					return true;
				}
				boolean isFinished = commands[currentIndex].exec();
				if (isFinished) {
					System.out.println("next command");
					isFinished = false;
					currentIndex++;
				}
				return currentIndex >= commands.length;
			}

			@Override
			public String toString() {
				return "Seq:[" + Arrays.stream(commands)
						.map(command -> isCurrentCommand(command) ? command.getName().toUpperCase()
								: command.getName().toLowerCase())
						.collect(Collectors.joining("->")) + "]";
			}

			private boolean isCurrentCommand(Command command) {
				return isFinished() || command == commands[currentIndex];
			}

		};
	}

	/**
	 * combines the given array of commands into a single command that runs the
	 * array of commands simultaneously
	 * 
	 * @param commands
	 *            the array of commands to combine
	 * @return the combined command
	 */
	public static Command combineSimultaneous(Command... commands) {
		assert !Arrays.asList(commands).contains(null) : "cannot combine null command";
		return new Command() {
			List<Command> runningCommands;

			@Override
			public void initialize() {
				runningCommands = new ArrayList<>();
				runningCommands.addAll(Arrays.asList(commands));
				for (Command c : runningCommands) {
					c.init();
				}
			}

			@Override
			public boolean execute() {
				ListIterator<Command> iter = runningCommands.listIterator();
				while (iter.hasNext()) {
					if (iter.next().exec()) {
						iter.remove();
					}
				}
				return runningCommands.isEmpty();
			}

			@Override
			public String toString() {
				return "Simul:[" + Arrays.stream(commands).map(command -> command.getName().toLowerCase())
						.collect(Collectors.joining(",")) + "]";
			}

		};
	}

	/**
	 * creates a command out of the given supplier, which will be called as the
	 * command's execute method
	 * 
	 * @param execute
	 *            the supplier to execute each command tick
	 * @return a new command
	 */
	public static Command createCommand(Supplier<Boolean> execute) {
		return new Command() {
			@Override
			protected boolean execute() {
				return execute.get();
			}
		};
	}

	/**
	 * creates a command that runs the passed runnable one time, then exits
	 * 
	 * @param execute
	 *            the runnable to execute
	 */
	public static Command createCommand(Runnable execute) {
		return createCommand(execute, false);
	}
	
	/**
	 * creates a command that runs the passed runnable
	 * 
	 * @param execute
	 *            the runnable to execute
	 * @param isLoop whether to repeat the runnable after it completes; if true, the command will never complete unless canceled
	 */
	public static Command createCommand(Runnable execute, boolean isLoop) {
		return createCommand(() -> {
			execute.run();
			return !isLoop;
		});
	}

	/**
	 * runs a command and hangs the thread until the command finishes
	 * 
	 * @param command
	 *            the command to execute
	 */
	public static void executeSynchronousCommand(Command command, long millisBetween) {
		assert command != null : "cannot execute null command";
		boolean isFinished = false;
		while (!isFinished) {
			isFinished = command.exec();
			try {
				Thread.sleep(millisBetween);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}