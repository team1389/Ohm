package com.team1389.system.drive;

import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.system.Subsystem;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

public class TwoStickDriveStraightSystem extends Subsystem
{

	DriveOut drive;
	RangeIn leftY;
	RangeIn rightY;

	public TwoStickDriveStraightSystem(DriveOut drive, RangeIn leftY, RangeIn rightY)
	{
		super();
		this.drive = drive;  
		this.leftY = leftY;
		this.rightY = rightY;
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return stem;
	}

	@Override
	public String getName()
	{
		return "TwoStickDrive";
	}

	@Override
	public void init()
	{

	}

	/**
	 * when drive straight btn pressed, only factors in left stick
	 */
	@Override
	public void update()
	{
		
		drive.set(leftY.get(), rightY.get());
	}

}
