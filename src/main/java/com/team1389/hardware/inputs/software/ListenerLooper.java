package com.team1389.hardware.inputs.software;

import java.util.ArrayList;
import java.util.List;

import com.team1389.hardware.inputs.interfaces.Listener;
import com.team1389.util.Loopable;

public class ListenerLooper {
	static List<Listener<?>> updateList = new ArrayList<>();

	static Loopable getWatcherUpdater() {
		return ListenerLooper::update;
	}

	public static void resetWatchers() {
		updateList = new ArrayList<>();
		System.out.println("change listeners reset");
	}

	public static synchronized void update() {
		updateList.forEach(listener -> listener.check());
	}

	public static void addListener(Listener<?> listener) {
		updateList.add(listener);
	}
}
