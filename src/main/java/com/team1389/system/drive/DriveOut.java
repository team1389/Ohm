package com.team1389.system.drive;

import java.util.function.UnaryOperator;

import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.Pair;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;

/**
 * A stream combining two Range streams, intended to represent the left and right wheels of a
 * drivetrain
 * 
 * @author amind
 *
 * @param <T> the range type of the wheel streams
 */
public class DriveOut<T extends Value> extends Pair<RangeOut<T>, RangeOut<T>> implements CompositeWatchable {

	/**
	 * @param left the left wheel stream
	 * @param right the right wheel stream
	 */
	public DriveOut(RangeOut<T> left, RangeOut<T> right) {
		super(left, right);
	}

	/**
	 * @param sig this values to apply to each stream
	 */
	public void set(DriveSignal sig) {
		this.set(sig.leftMotor, sig.rightMotor);
	}

	/**
	 * @param leftSig the value to apply to the left stream
	 * @param rightSig the value to apply to the right stream
	 */
	public void set(double leftSig, double rightSig) {
		left().set(leftSig);
		right().set(rightSig);
	}

	/**
	 * sets left and right values to 0
	 */
	public void neutral() {
		this.set(0, 0);
	}
	public RangeOut<T> left(){
		return one;
	}
	public RangeOut<T> right(){
		return two;
	}

	@Override
	public String getName() {
		return "drive";
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return new AddList<Watchable>().put(left().getWatchable("left")).put(right().getWatchable("right"));
	}

	public DriveOut<T> getInverted() {
		return forBoth(r -> r.getInverted());
	}

	public DriveOut<T> copy() {
		return forBoth(r -> r.copy());
	}

	public DriveOut<T> forBoth(UnaryOperator<RangeOut<T>> op) {
		return new DriveOut<>(op.apply(left()), op.apply(right()));
	}
}
