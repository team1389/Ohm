package com.team1389.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;

/**
 * stores a list of {@link Subsystem} objects that can be updated periodically. ensures that
 * Subsystems are initialized before update is called
 * 
 * @author amind
 *
 */
public class SystemManager {
	ArrayList<Subsystem> systems;
	boolean hasInited;

	/**
	 * @param systems the subsystems to update
	 */
	public SystemManager(Subsystem... systems) {
		this.systems = new ArrayList<>();
		this.systems.addAll(Arrays.asList(systems));
		hasInited = false;
	}

	/**
	 * adds the given Subsystems to this manager's list of systems to update
	 * 
	 * @param systems the Subsystems to add
	 */
	public void register(Subsystem... systems) {
		if (hasInited) {
			for (Subsystem system : systems) {
				system.init();
			}
		}
		this.systems.addAll(Arrays.asList(systems));
	}

	/**
	 * initializes this manager
	 */
	public void init() {
		hasInited = true;
		for (Subsystem system : systems) {
			system.init();
		}
	}

	/**
	 * updates the systems associated with this manager <br>
	 * if the manager has not yet been initialized, it {@link SystemManager#init()} will be called
	 * before updating
	 */
	public void update() {
		if (!hasInited) {
			init();
		}
		for (Subsystem system : systems) {
			system.thisUpdate();
		}
	}

	public AddList<Watchable> getSystemWatchables() {
		return CompositeWatchable.makeStem().put(systems.stream().map(s -> s).collect(Collectors.toList()));
	}
}
