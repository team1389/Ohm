package com.team1389.watch.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.team1389.watch.Watchable;

/**
 * an abstract class forcing the ability to return a name(key)
 * 
 * @author Kenneth
 *
 */
public interface SimpleWatchable extends Watchable {

	/**
	 * @return a number representation of this info for spreadsheet logs
	 */
	public double getLoggable();

	@Override
	public default Map<String, SimpleWatchable> getFlat(Optional<String> parent) {
		Map<String, SimpleWatchable> map = new HashMap<>();
		map.put(parent.map(this::getPath).orElse(getName()), this);
		return map;
	}
}
