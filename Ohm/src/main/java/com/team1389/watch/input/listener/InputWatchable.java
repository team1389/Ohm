package com.team1389.watch.input.listener;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.team1389.watch.info.NamedSimpleWatchable;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;

/**
 * rather than <em>periodically</em> publishing itself, an inputWatchable publishes its default
 * value <em>once</em> upon initialization, then tracks changes in the value from elsewhere in the
 * network.
 * 
 * @author amind
 *
 * @param <T> the type of value to watch in the table
 */
public abstract class InputWatchable<T> extends NamedSimpleWatchable implements Supplier<T> {
	boolean init;
	Consumer<T> onChange;

	/**
	 * @param name the name of the input
	 * @param defaultVal the default value to apply to the network table
	 * @param onChange an action to perfom when the table value changes
	 */
	public InputWatchable(String name, T defaultVal, Consumer<T> onChange) {
		super(name);
		this.onChange = onChange;
		this.val = defaultVal;
	}

	T val;

	/**
	 * @return the current value in the table
	 */
	@Override
	public T get() {
		return val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void publishUnderName(String name, NetworkTable table) {
		if (!table.containsKey(name)) {
			put(table, name, val);

		} else {
			val = get(table, name, val);
		}
		table.addEntryListener(name, (NetworkTable t, String s, NetworkTableEntry  entry,NetworkTableValue val, int flag) -> {
			onChange.accept((T) val);
		}, TableEntryListener.kUpdate);
	}

	protected abstract void put(NetworkTable table, String name, T val);

	protected abstract T get(NetworkTable table, String name, T defaultVal);

	@Override
	public String getPrintString() {
		return name + ": " + val;
	}
}
