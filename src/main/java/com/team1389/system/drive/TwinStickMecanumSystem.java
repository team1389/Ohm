package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.system.Subsystem;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * Mecanum Drive System for controller with two sticks. Still Untested.
 */
public class TwinStickMecanumSystem extends Subsystem {
	PercentIn leftStickY;
	PercentIn leftStickX;
	PercentIn rightStickX;
	FourDriveOut<Percent> drive;

	public void setPower(double leftStickY, double leftStickX, double rightStickX, FourDriveOut<Percent> drive) {
		double leftF, leftB, rightF, rightB; // front & back, left & right
		leftF = leftStickY;
		rightF = leftStickY;
		leftB = leftStickY;
		rightB = leftStickY;

		leftF += leftStickX;
		rightF += -leftStickX;
		leftB += -leftStickX;
		rightB += leftStickX;

		leftF += rightStickX;
		rightF += -rightStickX;
		leftB += rightStickX;
		rightB += -rightStickX;

		double max = Math.max(Math.max(Math.abs(leftF), Math.abs(rightF)), Math.max(Math.abs(leftB), Math.abs(rightB)));

		if (max > 1) {
			leftF = leftF / max;
			rightF = rightF / max;
			leftB = leftB / max;
			rightB = rightB / max;
		}
		drive.set(new FourWheelSignal(leftF, rightF, leftB, rightB));
	}

	public TwinStickMecanumSystem(PercentIn leftStickY, PercentIn leftStickX, PercentIn rightStickX,
			FourDriveOut<Percent> drive) {
		this.leftStickY = leftStickY;
		this.leftStickX = leftStickX;
		this.rightStickX = rightStickX;
		this.drive = drive;
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(leftStickY.getWatchable("left stick y"), rightStickX.getWatchable("rightStickX"), drive);
	}

	@Override
	public String getName() {
		return "Twin Stick Mecanum Drive";
	}

	@Override
	public void init() {

	}

	@Override
	public void update() {
		setPower(leftStickY.get(), leftStickX.get(), rightStickX.get(), drive);
	}

}