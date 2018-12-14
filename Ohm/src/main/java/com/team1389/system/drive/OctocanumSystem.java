package com.team1389.system.drive;

import java.util.Arrays;
import java.util.function.Supplier;

import com.team1389.command_framework.CommandUtil;
import com.team1389.command_framework.command_base.Command;
import com.team1389.hardware.controls.ControlBoard;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.outputs.software.DigitalOut;
import com.team1389.hardware.value_types.Percent;
import com.team1389.hardware.value_types.Position;
import com.team1389.util.bezier.BezierCurve;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.BooleanInfo;
import com.team1389.watch.info.StringInfo;

public class OctocanumSystem extends DriveSystem
{
	protected DriveMode currentMode;
	protected CurvatureDriveSystem tank;
	protected MecanumDriveSystem mecanum;
	protected DigitalOut octoShifter;
	protected DigitalIn switchModes;
	protected FourDriveOut<Percent> voltageDrive;

	public OctocanumSystem(FourDriveOut<Percent> voltageDrive, DigitalOut octoShifter, AngleIn<Position> gyro,
			PercentIn xAxis, PercentIn yAxis, PercentIn twist, PercentIn trim, DigitalIn switchModes, DigitalIn trigger)
	{
		this.voltageDrive = voltageDrive;
		this.octoShifter = octoShifter;
		this.switchModes = switchModes;
		setupTankDriveSystem(voltageDrive.getAsTank(), xAxis, yAxis, trim, trigger);
		setupMecanumDriveSystem(voltageDrive, xAxis, yAxis.copy().invert(), twist, trigger, gyro);
	}

	private void setupTankDriveSystem(DriveOut<Percent> drive, PercentIn xAxis, PercentIn yAxis, PercentIn trim,
			DigitalIn quickTurn)
	{
		BezierCurve xCurve = new BezierCurve(0, .5, .79, -0.06);
		BezierCurve yCurve = new BezierCurve(.0, 0.54, 0.45, -0.07);
		xAxis.map(d -> xCurve.getPoint(d).getY());
		yAxis.map(d -> yCurve.getPoint(d).getY());
		tank = new CurvatureDriveSystem(drive, yAxis, xAxis, quickTurn, ControlBoard.turnSensitivity,
				ControlBoard.spinSensitivity);
	}

	public Supplier<DriveMode> getDriveModeTracker()
	{
		return () -> currentMode;
	}

	private void setupMecanumDriveSystem(FourDriveOut<Percent> wheels, PercentIn xAxis, PercentIn yAxis,
			PercentIn twistAxis, DigitalIn toggleFOD, AngleIn<Position> gyro)
	{
		mecanum = new MecanumDriveSystem(xAxis, yAxis, twistAxis, wheels, gyro, toggleFOD);
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return stem.put(voltageDrive, new StringInfo("drive mode", () -> currentMode.name()),
				new BooleanInfo("drive-mode-val", () -> currentMode.solenoidVal));
	}

	public enum DriveMode
	{
		TANK(false), MECANUM(true);
		public final boolean solenoidVal;

		DriveMode(boolean solenoidVal)
		{
			this.solenoidVal = solenoidVal;
		}

		public DriveMode getFromBoolean(boolean value)
		{
			return Arrays.stream(DriveMode.values()).filter(m -> m.solenoidVal).findFirst().orElse(TANK);
		}
	}

	@Override
	public String getName()
	{
		return "Drive";
	}

	@Override
	public void init()
	{
		setMode(DriveMode.TANK);
	}

	private void switchModes()
	{
		setMode(currentMode == DriveMode.TANK ? DriveMode.MECANUM : DriveMode.TANK);
	}

	protected void setMode(DriveMode mode)
	{
		octoShifter.set(mode.solenoidVal);
		currentMode = mode;
	}

	protected Command setModeCommand(DriveMode mode)
	{
		return CommandUtil.createCommand(() -> setMode(mode));
	}

	@Override
	public void update()
	{
		if (switchModes.get())
		{
			switchModes();
		}
		switch (currentMode)
		{
		case MECANUM:
			mecanum.update();
			break;
		case TANK:
			tank.update();
			break;
		default:
			break;

		}
	}
}