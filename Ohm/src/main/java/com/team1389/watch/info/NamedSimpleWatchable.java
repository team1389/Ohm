package com.team1389.watch.info;

public abstract class NamedSimpleWatchable implements SimpleWatchable {
	protected String name;

	protected NamedSimpleWatchable(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
