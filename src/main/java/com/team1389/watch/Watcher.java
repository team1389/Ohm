package com.team1389.watch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.team1389.util.Loopable;
import com.team1389.watch.info.SimpleWatchable;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * stores a set of watchables, can display them in a variety of different ways
 * including on the SmartDashboard, written to a log, and printed to the
 * console.
 * 
 * @author Kenneth
 *
 */
public class Watcher {

	/**
	 * The default NetworkTable to publish to (The SmartDashboard table)
	 */
	public static NetworkTable DASHBOARD = NetworkTableInstance.getDefault().getTable("SmartDashboard");
	protected List<Watchable> watchables;
	protected Map<String, SimpleWatchable> flatWatchables;
	private Optional<LogFile> log;

	/**
	 * initializes the watcher object
	 */
	public Watcher() {
		flatWatchables = new HashMap<>();
		watchables = new ArrayList<>();
	}

	public Watcher(Watchable... watchables) {
		this();
		watch(watchables);
	}

	/**
	 * adds the given watchables to the list of things we are watching
	 * 
	 * @param watchables
	 *            added to list
	 * @return list of watchables
	 */
	public Watcher watch(Watchable... watchables) {
		return this.watch(Arrays.asList(watchables));
	}

	/**
	 * adds the given list watchables to the list of things we are watching
	 * 
	 * @param watchables
	 *            list of watchables to add
	 * @return original list
	 */
	public Watcher watch(List<Watchable> watchables) {
		watchables.forEach(w -> this.flatWatchables.putAll(w.getFlat(Optional.empty())));
		this.watchables.addAll(watchables);
		return this;
	}

	/**
	 * sets the log file to use
	 * 
	 * @param logLocation
	 *            the log file to log to
	 * @see Watcher#DEFAULT_LOG
	 */
	public void setLogLocation(LogFile logLocation) {
		log = Optional.of(logLocation);
	}

	/**
	 * @return a list of tracked watchables
	 */
	public List<Watchable> getWatchables() {
		return watchables;
	}

	public void outputToLog() {
		outputTo(w -> w.log());
	}

	public void outputToDashboard() {
		outputTo(w -> w.publish(Watcher.DASHBOARD));
	}

	public void outputToConsole() {
		outputTo(System.out::println);
	}

	public synchronized void outputTo(Consumer<Watcher> outputMethod) {
		updateList.put(this, outputMethod);
	}

	/**
	 * logs our list of watchable information to the set log location <br>
	 * <em>NOTE:</em> if no log location is set, this will do nothing. <br>
	 * use {@link Watcher#setLogLocation(LogFile)} to set the log location
	 */
	private void log() {
		if (!log.isPresent()) {
			System.out.println("Warning: no log location set, log commands are ignored");
		}
		log.ifPresent(this::logTo);
	}

	private void logTo(LogFile file) {
		try {
			if (!file.isInited()) {
				file.init();
				file.writeHeadings(new ArrayList<String>(flatWatchables.keySet()));
			}
			file.writeRow(flatWatchables.values().stream().mapToDouble(w -> w.getLoggable()).boxed()
					.collect(Collectors.toList()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * publishes all tracked watchable data to the given NetworkTable
	 * 
	 * @param table
	 *            to publish to
	 */
	protected void publish(NetworkTable table) {
		for (Entry<String, SimpleWatchable> info : flatWatchables.entrySet()) {
			info.getValue().publishUnderName(info.getKey(), table);
		}
	}

	/**
	 * @return a single string representing all tracked watchable data
	 */
	public String toString() {
		String s = "";
		for (Entry<String, SimpleWatchable> info : flatWatchables.entrySet()) {
			s = String.join(s, info.getKey() + ":" + info.getValue().getPrintString() + "\n");
		}
		return s;
	}

	/**
	 * reset list
	 */
	public void clearWatchers() {
		flatWatchables = new HashMap<>();
		watchables = new ArrayList<>();
	}

	static HashMap<Watcher, Consumer<Watcher>> updateList = new HashMap<>();

	static Loopable getWatcherUpdater() {
		return Watcher::update;
	}

	public static void resetWatchers() {
		updateList = new HashMap<>();
		System.out.println("watchers reset");
	}

	public static synchronized void update() {
		updateList.forEach((w, cw) -> cw.accept(w));
	}
}
