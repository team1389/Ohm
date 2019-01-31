package com.team1389.system.drive;

import java.util.function.UnaryOperator;

import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.Value;
import com.team1389.system.drive.DriveOut;
import com.team1389.system.drive.FourWheelSignal;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;

public class SixDriveOut<T extends Value> implements CompositeWatchable
{
	public final DriveOut<T> front;
	public final DriveOut<T> middle;
	public final DriveOut<T> rear;

	public SixDriveOut(DriveOut<T> front, DriveOut<T> middle, DriveOut<T> rear)
	{
		this.front = front;
		this.rear = rear;
		this.middle = middle;
	}

	public SixDriveOut(RangeOut<T> frontLeft, RangeOut<T> frontRight, RangeOut<T> middleLeft, RangeOut<T> middleRight,
			RangeOut<T> backLeft, RangeOut<T> backRight)
	{
		this(new DriveOut<T>(frontLeft, frontRight), new DriveOut<T>(middleLeft, middleRight),
				new DriveOut<T>(backLeft, backRight));
	}

	public void set(SixWheelSignal driveSignal)
	{
		front.set(driveSignal.getTopWheels());
		middle.set(driveSignal.getMiddleWheels());
		rear.set(driveSignal.getBottomWheels());
	}

	public DriveOut<T> getAsTank()
	{
		RangeOut<T> left = front.left().copy().getWithAddedFollowers(rear.left().getWithAddedFollowers(middle.left()));
		RangeOut<T> right = front.right().copy().getWithAddedFollowers(rear.right().getWithAddedFollowers(middle.right()));
		return new DriveOut<>(left, right);
	}

	@Override
	public String getName()
	{
		return "Drive";
	}

	public FourDriveOut<T> forEach(UnaryOperator<RangeOut<T>> op)
	{
		return new FourDriveOut<>(front.forBoth(op), rear.forBoth(op));
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return stem.put(CompositeWatchable.of("front", front.getSubWatchables(CompositeWatchable.makeStem())),
				CompositeWatchable.of("rear", rear.getSubWatchables(CompositeWatchable.makeStem())));
	}
}
