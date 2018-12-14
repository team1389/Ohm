package com.team1389.motion_profile;

/**
 * Represents constant acceleration (i.e. a linear speed graph with non zero slope)
 *
 */
public class ConstantAccelProfile extends MotionProfile {
	private SimpleKinematics full;
	private int inverted = 1; //-1 if it is inverted

	/**
	 * No check is made to see if this is possible (for example: negative a, positive s, 0 for Vo),
	 * so be careful when selecting values (you might end up with a negative time and all sorts of complications)
	 * @param a The value to constantly accelerate at
	 * @param S The total distance to travel
	 * @param Vo The initial velocity
	 */
	public ConstantAccelProfile(double a, double S, double Vo) {
		this(new SimpleKinematics(Vo, Double.NaN, Double.NaN, a, S));
	}
	
	/**
	 * Same as {@link #ConstantAccelProfile(double, double, double) ConstantAccelProfile(double, double, double)} except for the inverted parameter.
	 * @param a The value to constantly accelerate at
	 * @param S The total distance to travel
	 * @param Vo The initial velocity
	 * @param inverted If true, then position, velocity, and acceleration will be multiplied by -1 ("inverted") 
	 * when their respective provide methods are called.
	 */
	public ConstantAccelProfile(double a, double S, double Vo, boolean inverted) {
		this(new SimpleKinematics(Vo, Double.NaN, Double.NaN, a, S));
		if(inverted){
			this.inverted = -1;
		}
	}

	/**
	 * @param kinematics Represents the acceleration. Validity is not checked (should have a positive time)
	 */
	public ConstantAccelProfile(SimpleKinematics kinematics) {
		this.full = kinematics;
	}
	
	/**
	 * Same as  {@link #ConstantAccelProfile(SimpleKinematics) ConstantAccelProfile(SimpleKinematics)} except for the inverted parameter.
	 * @param kinematics Represents the acceleration. Validity is not checked (should have a positive time)
	 * @param inverted If true, then position, velocity, and acceleration will be multiplied by -1 ("inverted") 
	 * when their respective provide methods are called.
	 */
	public ConstantAccelProfile(SimpleKinematics kinematics, boolean inverted) {
		this.full = kinematics;
		if(inverted){
			this.inverted = -1;
		}
	}
	

	@Override
	public double providePosition(double time) {
		return getCurrentKinematics(time).x * inverted;
	}

	@Override
	public double provideVelocity(double time) {
		return getCurrentKinematics(time).vf * inverted;
	}

	@Override
	public double provideAcceleration(double time) {
		return full.a * inverted;
	}

	@Override
	public double getDuration() {
		return full.t;
	}

	private SimpleKinematics getCurrentKinematics(double time) {
		return new SimpleKinematics(full.vo, Double.NaN, time, full.a, Double.NaN);
	}
}
