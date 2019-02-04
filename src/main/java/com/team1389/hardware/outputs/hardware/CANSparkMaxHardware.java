package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Position;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * Provides input (position) or output (voltage control) streams for the
 * SparkMax controlled by CAN
 */
public class CANSparkMaxHardware extends Hardware<CAN>
{

	public static final int kTimeoutMs = 10;

	private boolean outInverted;
	private boolean inInverted;
	private CAN requestedPort;
	private Optional<CANSparkMax> revSpark;
	private boolean idleModeCoast;

	public CANSparkMaxHardware(boolean outInverted, boolean inInverted, boolean idleModeCoast, CAN requestedPort,
			Registry registry)
	{
		this.outInverted = outInverted;
		this.inInverted = inInverted;
		this.idleModeCoast = idleModeCoast;
		this.requestedPort = requestedPort;
		attachHardware(requestedPort, registry);
	}

	public PercentOut getVoltageController()
	{
		return new PercentOut(voltage -> revSpark.ifPresent(spark -> spark.set(voltage)));
	}

	/**
	 * 
	 * @param limit
	 *                  Current in amps to keep spark current draw below
	 */
	public void setCurrentLimit(int limit)
	{
		revSpark.ifPresent(spark -> spark.setSmartCurrentLimit(limit));
	}

	/**
	 * Precondition: Will not read encoder values properly unless Spark is
	 * properly configured for whichever sensor is being used Requires encoder
	 * to be plugged in
	 * 
	 * @return Stream which provides position in rotations
	 * 
	 */
	// TODO: Figure out how to configure spark from code end instead of manually
	// via the client
	public RangeIn<Position> getPositionStreamInRotations()
	{
		// Min/Max in RangeIn don't matter. That's why the max is set to one.
		// Doesn't cap off values
		if (inInverted)
		{
			return new RangeIn<>(Position.class,
					() -> revSpark.map(spark -> -spark.getEncoder().getPosition()).orElse(0.0), 0, 1);
		}
		return new RangeIn<>(Position.class, () -> revSpark.map(spark -> spark.getEncoder().getPosition()).orElse(0.0),
				0, 1);
	}

	/**
	 * runs if requested port is open
	 */
	@Override
	protected void init(CAN port)
	{
		CANSparkMax spark;
		spark = new CANSparkMax(requestedPort.index(), MotorType.kBrushless);
		spark.setCANTimeout(kTimeoutMs);
		spark.setInverted(outInverted);
		if (idleModeCoast)
		{
			spark.setIdleMode(IdleMode.kCoast);
		}
		else
		{
			spark.setIdleMode(IdleMode.kBrake);
		}
		revSpark = Optional.of(spark);
	}

	/**
	 * runs if requested port is taken
	 */
	@Override
	protected void failInit()
	{
		revSpark = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier()
	{
		return "Spark MAX";
	}

	/**
	 * Stores all configuration options for SparkMax. Expects to be passed in to
	 * constructor of CANSparkMaxHardware
	 * 
	 */

}