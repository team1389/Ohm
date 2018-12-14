package com.team1389.system;

import com.team1389.util.Timer;

public abstract class TimedSubsystem extends Subsystem {
	private double deltaTime;
	private Timer timer;

	public TimedSubsystem() {
		this.timer = new Timer();
		timer.zero();
	}

	@Override
	protected void thisUpdate() {
		deltaTime = timer.getSinceMark();
		timer.mark();
		super.thisUpdate();
	}

	/**
	 * @return the time between updates in milliseconds
	 */
	public double getDelta() {
		return deltaTime;
	}
}
