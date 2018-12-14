package com.team1389.watch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.team1389.util.list.AddList;
import com.team1389.watch.info.SimpleWatchable;

import edu.wpi.first.networktables.NetworkTable;

/**
 * implements the <em>Composite Pattern</em>: <br>
 * represents a group of {@link Watchable} objects, but also implements {@link Watchable} itself
 * 
 * @author amind
 * @see <a href= "https://www.javacodegeeks.com/2015/09/composite-design-pattern.html"> Composite
 *      pattern</a>
 * @see SimpleWatchable
 * @see Watchable
 */
public interface CompositeWatchable extends Watchable {

	/**
	 * a blank list used to simplify the process of building a subWatchables list
	 * 
	 * @see CompositeWatchable#getSubWatchables(AddList stem)
	 */

	/**
	 * @param stem a supplied empty list to build the subWatchables list from
	 * @return a list of watchables tracked by this composite
	 * @see CompositeWatchable#stem
	 */
	AddList<Watchable> getSubWatchables(AddList<Watchable> stem);

	public default AddList<Watchable> getSubWatchables(){
		return getSubWatchables(CompositeWatchable.makeStem());
	}
	
	@Override
	default void publishUnderName(String name, NetworkTable table) {
		getSubWatchables(makeStem()).forEach(w -> w.publish(name, table));
	}

	@Override
	public default String getPrintString() {
		String s = "";
		return s;
	}

	@Override
	public default Map<String, SimpleWatchable> getFlat(Optional<String> parent) {
		Map<String, SimpleWatchable> map = new HashMap<>();
		getSubWatchables(makeStem())
				.forEach(w -> map.putAll(w.getFlat(Optional.of(parent.map(this::getPath).orElse(getName())))));
		return map;
	}

	/**
	 * generates a composite watchable
	 * 
	 * @param name the name of the composite watchable
	 * @param subWatchables the watchables to combine
	 * @return a composite watchable containing the given subWatchables
	 */
	public static CompositeWatchable of(String name, Watchable... subWatchables) {
		return new CompositeWatchable() {

			@Override
			public String getName() {
				return name;
			}

			@Override
			public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
				return stem.put(subWatchables);
			}

		};
	}

	/**
	 * generates a composite watchable
	 * 
	 * @param name the name of the composite watchable
	 * @param subWatchables the list of watchables to combine
	 * @return a composite watchable containing the given subWatchables
	 */
	public static CompositeWatchable of(String name, List<Watchable> subWatchables) {
		return of(name, subWatchables.toArray(new Watchable[0]));
	}

	public static AddList<Watchable> makeStem() {
		return new AddList<Watchable>();
	}
}
