package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.value_types.Percent;
import com.team1389.system.Subsystem;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * Mecanum Drive System for controller with two sticks. 
 */
public class TwinStickMecanumSystem extends Subsystem {
	private PercentIn yInput;
	private PercentIn xInput;
	private PercentIn turnInput;
	private FourDriveOut<Percent> drive;


	/**
	 * @param yInput input for forwards-backwards translation
	 * @param xInput input for horizontal translation
	 * @param turnInput input for turning 
	 * @param drive stream that controls the four wheels of the drive train
	 */
	public TwinStickMecanumSystem(PercentIn yInput, PercentIn xInput, PercentIn turnInput,
			FourDriveOut<Percent> drive) {
		this.yInput = yInput;
		this.xInput = xInput;
		this.turnInput = turnInput;
		this.drive = drive;
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(yInput.getWatchable("forward-back movement input"), xInput.getWatchable("horizontal movement input"), turnInput.getWatchable("turn input"), drive);
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
		setPower(yInput.get(), xInput.get(), turnInput.get(), drive);
	}
	
	/**
	 * calculates power to apply to motors to achieve desired movement
	 * @param yInput input for forwards-backwards translation
	 * @param xInput input for horizontal translation
	 * @param turnInput input for turning 
	 * @param drive stream that controls the four wheels of the drive train
	 */
	private void setPower(double yInput, double xInput, double turnInput, FourDriveOut<Percent> drive) {
		double leftF, leftB, rightF, rightB; // front & back, left & right
		leftF = yInput;
		rightF = yInput;
		leftB = yInput;
		rightB = yInput;

		leftF += xInput;
		rightF += -xInput;
		leftB += -xInput;
		rightB += xInput;

		leftF += turnInput;
		rightF += -turnInput;
		leftB += turnInput;
		rightB += -turnInput;

		double max = Math.max(Math.max(Math.abs(leftF), Math.abs(rightF)), Math.max(Math.abs(leftB), Math.abs(rightB)));

		if (max > 1) {
			leftF = leftF / max;
			rightF = rightF / max;
			leftB = leftB / max;
			rightB = rightB / max;
		}
		drive.set(new FourWheelSignal(leftF, rightF, leftB, rightB));
	}

}