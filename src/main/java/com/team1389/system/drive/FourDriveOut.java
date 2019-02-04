package com.team1389.system.drive;

import java.util.function.UnaryOperator;

import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Value;
import com.team1389.system.drive.DriveOut;
import com.team1389.system.drive.FourWheelSignal;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;

public class FourDriveOut<T extends Value> implements CompositeWatchable {
	public final DriveOut<T> front;
	public final DriveOut<T> rear;

	public FourDriveOut(DriveOut<T> front, DriveOut<T> rear) {
		this.front = front;
		this.rear = rear;
	}

	public FourDriveOut(RangeOut<T> frontLeft, RangeOut<T> frontRight, RangeOut<T> backLeft, RangeOut<T> backRight) {
		this(new DriveOut<T>(frontLeft, frontRight), new DriveOut<T>(backLeft, backRight));
	}

	public void set(FourWheelSignal driveSignal) {
		front.set(driveSignal.getTopWheels());
		rear.set(driveSignal.getBottomWheels());
	}

	public DriveOut<T> getAsTank() {
		RangeOut<T> left = front.left().copy().getWithAddedFollowers(rear.left());
		RangeOut<T> right = front.right().copy().getWithAddedFollowers(rear.right());
		return new DriveOut<>(left, right);
	}

	@Override
	public String getName() {
		return "Drive";
	}
	public FourDriveOut<T> forEach(UnaryOperator<RangeOut<T>> op){
		return new FourDriveOut<>(front.forBoth(op), rear.forBoth(op));
	}
	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(CompositeWatchable.of("front", front.getSubWatchables(CompositeWatchable.makeStem())),
				CompositeWatchable.of("rear", rear.getSubWatchables(CompositeWatchable.makeStem())));
	}
}
