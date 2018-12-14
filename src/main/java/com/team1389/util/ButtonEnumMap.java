package com.team1389.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.StringInfo;

/**
 * Maps a set of boolean streams to the values of an enum
 * 
 * <p>
 * When any of the streams returns true, the value is set to the mapping of that stream
 * 
 * <p>
 * This class works best with latched streams.
 * 
 * @see com.team1389.hardware.inputs.interfaces.BinaryInput#getLatched(com.team1389.hardware.inputs.interfaces.BinaryInput)
 *      Latching an input stream
 * 
 * @author amind
 *
 * @param <E> the enum type
 */
@SuppressWarnings("rawtypes")
public class ButtonEnumMap<E extends Enum> {
	List<ButtonEnum> mappings;
	List<Runnable> changes;

	/**
	 * @param onChange an action to perform on when the map changes values
	 */
	public void addChangeListener(Runnable onChange) {
		changes.add(onChange);
	}

	/**
	 * @param defaultValue the default enum value of the map
	 */
	public ButtonEnumMap(E defaultValue) {
		currentVal = defaultValue;
		changes = new ArrayList<>();
		this.mappings = new ArrayList<>();
	}

	/**
	 * @param mappings the button mappings
	 */
	@SafeVarargs
	public final void setMappings(ButtonEnum... mappings) {
		this.mappings.addAll(Arrays.asList(mappings));
	}

	E currentVal;
	E lastVal;

	/**
	 * @return the current value of the map
	 */
	public E getVal() {
		Iterator<ButtonEnum> i = mappings.iterator();
		while (i.hasNext()) {
			ButtonEnum mapping = i.next();
			if (mapping.button.get()) {
				currentVal = mapping.val;
			}
		}
		if (lastVal != currentVal) {
			for (Runnable r : changes) {
				r.run();
			}
		}
		lastVal = currentVal;
		return currentVal;
	}

	/**
	 * @return gets the current mapping without polling the mappings
	 */
	public E getCurrentVal() {
		return currentVal;
	}

	/**
	 * represents a single mapping from a boolean stream to an enum value
	 * 
	 * @author amind
	 *
	 */
	public class ButtonEnum {
		DigitalIn button;
		E val;

		/**
		 * @param button the trigger stream
		 * @param val the enum value to trigger
		 */
		public ButtonEnum(DigitalIn button, E val) {
			this.button = button;
			this.val = val;
		}
	}

	/**
	 * 
	 * @param name a String identifier for the watchable
	 * @return a Watchable version of this map
	 */
	public Watchable getWatchable(String name) {
		return new StringInfo(name, () -> getCurrentVal().name());
	}
}
