package com.team1389.motion_profile;

import com.team1389.controllers.MotionProfileController;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;

public class ProfileConstants {
	MotionProfile profile;
	MotionProfileController profileController;

	public ProfileConstants(double dx, double maxAccel, double maxDecel, double maxSpeed) {
		profile = ProfileUtil.trapezoidal(dx, 0, maxAccel, maxDecel, maxSpeed);
	}

	public ProfileConstants(double dx, double vO, double maxAccel, double maxDecel, double maxSpeed) {

		profile = ProfileUtil.trapezoidal(dx, vO, maxAccel, maxDecel, maxSpeed);
	}

	public ProfileConstants(double kP, double kI, double kD, double kF, RangeIn<Position> source, RangeIn<Speed> vel,
			RangeOut<Percent> output) {
		profileController = new MotionProfileController(kP, kI, kD, kF, source, vel, output);
	}

	public MotionProfile getProfile() {
		return profile;
	}

	public MotionProfileController getController() {
		return profileController;
	}
}
