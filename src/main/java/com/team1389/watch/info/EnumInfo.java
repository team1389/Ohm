package com.team1389.watch.info;

import java.util.function.Supplier;

public class EnumInfo extends StringInfo {

	public EnumInfo(String name, Supplier<? extends Enum<?>> source) {
		super(name, () -> source.get().name());
	}

}
