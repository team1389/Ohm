package com.team1389.motion_profile;

/**
 * This is a single {@link MotionProfile} that represents the combination of two input {@link MotionProfile}s
 * as if the end of the first one was combined to the beginning of the second. No check is made to ensure continuity of velocity.
 *
 */
public class CombinedProfile extends MotionProfile {
	MotionProfile profile;
	MotionProfile profile2;

	/**
	 * 
	 * @param profile The first part of the motion
	 * @param profile2 The second part of the motion
	 */
	public CombinedProfile(MotionProfile profile, MotionProfile profile2) {
		this.profile = profile;
		this.profile2 = profile2;
	}

	/**
	 * The duration is just the sum of the two components' durations
	 */
	@Override
	public double getDuration() {
		return profile.getDuration() + profile2.getDuration();
	}

	/**
	 * Two cases: <br>
	 * The time is less than the first's duration, in which case the position is just the first's position at that time. <br>
	 * The time is more than the first's duration but less than the total duration, in which case the position is the
	 * first's position at the first's duration plus the second's position at the inputed time minus the first's duration. 
	 */
	@Override
	protected double providePosition(double time) {
		if (time < profile.getDuration()) {
			return profile.providePosition(time);
		} else {
			return profile2.providePosition(time - profile.getDuration()) + profile.getPosition(profile.getDuration());
		}
	}

	/**
	 * See documentation for {@link #providePosition(double) providePosition}, this is the same except for velocity instead of position.
	 */
	@Override
	protected double provideVelocity(double time) {
		if (time < profile.getDuration()) {
			return profile.provideVelocity(time);
		} else {
			return profile2.provideVelocity(time);
		}
	}


	/**
	 * See documentation for {@link #providePosition(double) providePosition}, this is the same except for acceleration instead of position.
	 */
	@Override
	protected double provideAcceleration(double time) {
		if (time < profile.getDuration()) {
			return profile.provideAcceleration(time);
		} else {
			return profile2.provideAcceleration(time);
		}
	}

	@Override
	public String toString() {
		return "[" + profile + "," + profile2 + "]";
	}
}
