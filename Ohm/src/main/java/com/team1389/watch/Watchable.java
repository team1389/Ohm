package com.team1389.watch;

import java.util.Map;
import java.util.Optional;

import com.team1389.watch.info.SimpleWatchable;

import edu.wpi.first.networktables.NetworkTable;

/**
 * represents a value or set of values that can be displayed in a variety of
 * ways when passed to a {@link Watcher}
 * 
 * @see Watcher
 * @author Kenneth
 *
 */
public interface Watchable {

	/**
	 * @return the specific name of this watchable <br>
	 *         <em>NOTE</em>: this does not take watchable hierarchy into account
	 */
	public String getName();

	/**
	 * @param name
	 *            that the value will be stored under
	 * @param table
	 *            where key+value is published to
	 */
	void publishUnderName(String name, NetworkTable table);

	/**
	 * returns a flat hashmap of simple watchables with hierarchical naming
	 * 
	 * @param parent
	 *            a name to concat at the top of the hierarchy
	 * @return a map of simple watchables in t
	 */
	Map<String, SimpleWatchable> getFlat(Optional<String> parent);

	/**
	 * adds parent to the name, then calls {@link Watchable#publishUnderName}
	 * 
	 * @param parent
	 *            additional organizer
	 * @param table
	 *            where key+value is published to
	 */
	public default void publish(String parent, NetworkTable table) {
		publishUnderName(getFullName(parent), table);
	}

	/**
	 * publishing using name provided by object
	 * 
	 * @param table
	 *            where key+value is published to
	 * 
	 */
	public default void publish(NetworkTable table) {
		publishUnderName(getName(), table);
	}

	/**
	 * determines the full name of this watchable based on its position in the
	 * hierarchy
	 * 
	 * @param parent
	 *            the path of the parent watchable
	 * @return the full path of this watchable in context
	 */
	public default String getPath(String parent) {
		return parent + "/" + getName();
	}

	/**
	 * determines the full name of this watchable based on its position in the
	 * hierarchy
	 * 
	 * @param parent
	 *            the full name of the parent watchable
	 * @return the full string name of this watchable in context
	 */
	public default String getFullName(String parent) {
		return parent + "." + getName();
	}

	/**
	 * @return a String representation of this watchable
	 */
	public String getPrintString();

}
