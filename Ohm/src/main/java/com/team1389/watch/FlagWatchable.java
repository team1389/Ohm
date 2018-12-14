package com.team1389.watch;

import com.team1389.util.list.AddList;

public interface FlagWatchable extends CompositeWatchable {
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem);

}
