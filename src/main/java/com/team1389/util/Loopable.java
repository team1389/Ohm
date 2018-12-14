package com.team1389.util;

import java.util.Arrays;
import java.util.function.Supplier;

public interface Loopable {
	public default void init() {
	};

	public void update();

	public static void pollUntil(int millisBetweenPolls, Loopable loopable, Supplier<Boolean> finished) {
		loopable.init();
		while (!finished.get()) {
			loopable.update();
			try {
				Thread.sleep(millisBetweenPolls);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static Loopable combine(Loopable... loopables) {
		return new Loopable() {
			@Override
			public void init() {
				Arrays.stream(loopables).forEach(l -> l.init());
			}

			@Override
			public void update() {
				Arrays.stream(loopables).forEach(l -> l.update());
			}

		};
	}
}
