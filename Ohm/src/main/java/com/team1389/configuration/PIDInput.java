package com.team1389.configuration;

import java.util.function.Consumer;

import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;
import com.team1389.watch.input.listener.NumberInput;

/**
 * represents a set of PID constants that can be tuned on the smart dashboard
 * 
 * @author amind
 *
 */
public class PIDInput implements CompositeWatchable {
	NumberInput p;
	NumberInput i;
	NumberInput d;
	NumberInput f;
	boolean useFeedforward;
	String name;

	/**
	 * @param name the string identifier of this tuner
	 * @param defaultVal the default PIDconstants
	 * @param useFeedforward whether to display the feedforward gain as part of the tuner (useful for tuning speed PID loops)
	 * @param fun an action to perform when any one of the tuner values changes. takes in the changes pid constants
	 */
	public PIDInput(String name, PIDConstants defaultVal, boolean useFeedforward, Consumer<PIDConstants> fun) {
		this.name = name;
		this.useFeedforward = useFeedforward;
		this.p = new NumberInput("p", defaultVal.p, np -> fun.accept(new PIDConstants(np, i.get(), d.get(), f.get())));
		this.i = new NumberInput("i", defaultVal.i, ni -> fun.accept(new PIDConstants(p.get(), ni, d.get(), f.get())));
		this.d = new NumberInput("d", defaultVal.d, nd -> fun.accept(new PIDConstants(p.get(), i.get(), nd, f.get())));
		this.f = new NumberInput("f", defaultVal.f, nf -> fun.accept(new PIDConstants(p.get(), i.get(), d.get(), nf)));
	}
/*	public PIDInput(String name, PIDConstants defaultVal, Consumer<PIDConstants> fun){
		this(name,defaultVal,false,fun);
	}*/

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		if (useFeedforward) {
			stem.put(f);
		}
		return stem.put(p, i, d);
	}
}
