package com.team1389.auto;

/**
 * This class selects, runs, and stops (if necessary) a specified autonomous mode.
 */
public class AutoModeExecuter implements Runnable {
	private AutoModeBase m_auto_mode;

	/**
	 * sets the auto mode to run
	 * 
	 * @param new_auto_mode the auto mode to run
	 */
	public void setAutoMode(AutoModeBase new_auto_mode) {
		m_auto_mode = new_auto_mode;
	}

	/**
	 * runs the stored auto mode
	 */
	public void run() {
		if (m_auto_mode != null) {
			m_auto_mode.run();
		} else {
			System.err.println("failed to run auto mode, no mode set");
		}
	}

	/**
	 * cancels the current auto mode
	 */
	public void stop() {
		if (m_auto_mode != null) {
			m_auto_mode.stop();
		}
	}

}