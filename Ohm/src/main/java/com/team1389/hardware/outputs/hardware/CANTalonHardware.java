package com.team1389.hardware.outputs.hardware;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.team1389.configuration.PIDConstants;
import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.PercentIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

/**
 * This class offers input/output stream sources for a Talon SRX.
 * <p>
 * Furthermore, this class will ensure that the Talon has been given all
 * required configuration before it enters any control mode. <br>
 * TODO add limit switch support
 * 
 * @author amind
 *
 */
public class CANTalonHardware extends Hardware<CAN>
{
	public static final int kTimeoutMs = 10;
	public static final int kMagicProfileSlotIdx = 0;

	public static final int kCascadedPIDLoopIdx = 1;
	public static final int kPrimaryPIDLoopIdx = 0;

	public static final int kPositionPIDSlot = 0;
	public static final int kVelocityPIDSlot = 1;
	public static final int kMagicPIDSlot = 2;

	private Optional<WPI_TalonSRX> wpiTalon;
	private Consumer<WPI_TalonSRX> initialConfig;
	private boolean outputInverted;
	private boolean sensorPhase;
	private FeedbackDevice selectedSensor;
	private double sensorRange;

	/**
	 * @param outInverted
	 *            whether the motor output should be inverted (used for both
	 *            voltage and position control modes)
	 * @param inpInverted
	 *            whether the sensor input should be inverted
	 * @param requestedPort
	 *            the port to attempt to initialize this hardware
	 * @param registry
	 *            the registry associated with the robot
	 * @see <a href=
	 *      "https://www.ctr-electronics.com/Talon%20SRX%20Software%20Reference%20Manual.pdf">Talon
	 *      SRX user manual</a> for more information on output/input inversion
	 */
	public CANTalonHardware(boolean outInverted, boolean sensorPhase, FeedbackDevice selectedSensor, double sensorRange,
			CAN requestedPort, Registry registry, Consumer<WPI_TalonSRX> initialConfig)
	{
		this.outputInverted = outInverted;
		this.sensorPhase = sensorPhase;
		this.selectedSensor = selectedSensor;
		this.sensorRange = sensorRange;
		this.initialConfig = initialConfig;
		attachHardware(requestedPort, registry);
	}

	public CANTalonHardware(boolean outInverted, boolean sensorPhase, FeedbackDevice selectedSensor, double sensorRange,
			CAN requestedPort, Registry registry)
	{
		this(outInverted, sensorPhase, selectedSensor, sensorRange, requestedPort, registry, t ->
		{
		});
	}

	/**
	 * assumes input is not inverted
	 * 
	 * @param outInverted
	 *            whether the motor output should be inverted (used for both
	 *            voltage and position control modes)
	 * @param requestedPort
	 *            the port to attempt to initialize this hardware
	 * @param registry
	 *            the registry associated with the robot
	 * @see <a href=
	 *      "https://www.ctr-electronics.com/Talon%20SRX%20Software%20Reference%20Manual.pdf">Talon
	 *      SRX user manual</a> for more information on output/input inversion
	 */
	public CANTalonHardware(boolean outInverted, CAN requestedPort, Registry registry,
			Consumer<WPI_TalonSRX> initialConfig)
	{
		this(outInverted, false, FeedbackDevice.None, 0, requestedPort, registry, initialConfig);
	}

	public CANTalonHardware(boolean outInverted, CAN requestedPort, Registry registry)
	{
		this(outInverted, requestedPort, registry, t ->
		{
		});
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return stem;
	}

	@Override
	protected String getHardwareIdentifier()
	{
		return "Talon";
	}

	@Override
	public void init(CAN port)
	{
		WPI_TalonSRX talon = new WPI_TalonSRX(port.index());
		talon.setSensorPhase(sensorPhase);
		talon.configSelectedFeedbackSensor(selectedSensor, kPrimaryPIDLoopIdx, kTimeoutMs);
		talon.setInverted(outputInverted);
		talon.configNominalOutputForward(0, kTimeoutMs);
		talon.configNominalOutputReverse(0, kTimeoutMs);
		talon.configPeakOutputForward(1, kTimeoutMs);
		talon.configPeakOutputReverse(-1, kTimeoutMs);
		initialConfig.accept(talon);
		wpiTalon = Optional.of(talon);
	}

	@Override
	public void failInit()
	{
		wpiTalon = Optional.empty();
	}

	public RangeIn<Position> getSensorPositionStream()
	{
		return new RangeIn<>(Position.class,
				() -> wpiTalon.map(t -> (double) t.getSelectedSensorPosition(kPrimaryPIDLoopIdx)).orElse(0.0), 0.0,
				sensorRange);
	}

	public RangeIn<Speed> getVelocityStream()
	{
		return new RangeIn<Speed>(Speed.class,
				() -> wpiTalon.map(t -> (double) t.getSelectedSensorVelocity(kPrimaryPIDLoopIdx)).orElse(0.0), 0.0,
				sensorRange);
	}

	public PercentIn getVoltageTracker()
	{
		return new PercentIn(() -> wpiTalon.map(t -> t.getMotorOutputVoltage()).orElse(0.0));
	}

	private static void setPID(WPI_TalonSRX talon, int slot, PIDConstants pid)
	{
		talon.config_kF(slot, pid.f, kTimeoutMs);
		talon.config_kP(slot, pid.p, kTimeoutMs);
		talon.config_kI(slot, pid.i, kTimeoutMs);
		talon.config_kD(slot, pid.d, kTimeoutMs);
	}

	public void setPID(int slot, PIDConstants pid)
	{
		wpiTalon.ifPresent(t -> setPID(t, slot, pid));
	}

	public RangeIn<Value> getClosedLoopErrorStream()
	{
		return new RangeIn<>(Value.class,
				() -> wpiTalon.map(t -> (double) t.getClosedLoopError(kPrimaryPIDLoopIdx)).orElse(0.0), 0d,
				sensorRange);
	}

	// Configurations
	/**
	 * @param talon
	 *            talon to configure
	 * @param vcruise
	 *            max velocity in sensor units/sec
	 * @param acc
	 *            max velocity in sensor units/sec^2
	 * @param pid
	 */
	private static void configMotionMagic(WPI_TalonSRX talon, int vCruise, int acc, PIDConstants pid)
	{
		int vCruiseConv = vCruise / 10;
		int accConv = acc / 10;
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, kTimeoutMs);
		talon.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, kTimeoutMs);

		talon.selectProfileSlot(kMagicProfileSlotIdx, kPrimaryPIDLoopIdx);

		setPID(talon, kMagicPIDSlot, pid);
		/* set acceleration and vcruise velocity - see documentation */
		talon.configMotionCruiseVelocity(vCruiseConv, kTimeoutMs);
		talon.configMotionAcceleration(accConv, kTimeoutMs);
		/* zero the sensor */
		talon.selectProfileSlot(kMagicPIDSlot, kPrimaryPIDLoopIdx);
		talon.set(ControlMode.MotionMagic, talon.getSelectedSensorPosition(kPrimaryPIDLoopIdx));
	}

	public RangeOut<Position> getMotionController(int vCruise, int acc, PIDConstants pid)
	{
		wpiTalon.ifPresent(t -> configMotionMagic(t, vCruise, acc, pid));
		Consumer<Double> positionSetter = d ->
		{
			if (wpiTalon.map(t -> t.getControlMode() == ControlMode.MotionMagic).orElse(false))
			{
				wpiTalon.ifPresent(t -> t.set(ControlMode.MotionMagic, d));
			} else
			{
				throw new RuntimeException(
						"Error! attempted to use motion magic after control mode was changed, ensure you are only controlling the talon from one place!");
			}
		};
		return new RangeOut<>(positionSetter, 0, sensorRange);
	}

	private static void configPositionControl(WPI_TalonSRX talon, PIDConstants pid)
	{
		talon.configAllowableClosedloopError(0, kPositionPIDSlot,
				kTimeoutMs); /* always servo */
		/* set closed loop gains in slot0 */
		setPID(talon, kPositionPIDSlot, pid);
		talon.selectProfileSlot(kPositionPIDSlot, kPrimaryPIDLoopIdx);
		talon.set(ControlMode.Position, talon.getSelectedSensorPosition(kPrimaryPIDLoopIdx));
	}

	public RangeOut<Position> getPositionController(PIDConstants pid)
	{
		wpiTalon.ifPresent(t -> configPositionControl(t, pid));
		Consumer<Double> positionSetter = d ->
		{
			if (wpiTalon.map(t -> t.getControlMode() == ControlMode.Position).orElse(false))
			{
				wpiTalon.ifPresent(t -> t.set(ControlMode.Position, d));
			} else
			{
				throw new RuntimeException(
						"Error! attempted to use position mode after control mode was changed, ensure you are only controlling the talon from one place!");
			}
		};
		return new RangeOut<>(positionSetter, 0, sensorRange);
	}

	private static void configVelocityControl(WPI_TalonSRX talon, PIDConstants pid)
	{
		talon.configAllowableClosedloopError(0, kPrimaryPIDLoopIdx,
				kTimeoutMs); /* always servo */
		setPID(talon, kVelocityPIDSlot, pid);
		talon.selectProfileSlot(kVelocityPIDSlot, kPrimaryPIDLoopIdx);
		talon.set(ControlMode.Velocity, 0.0);
	}

	/**
	 * takes velocity in native units of rot/sec
	 * 
	 * @param pid
	 * @return
	 */
	public RangeOut<Speed> getVelocityController(PIDConstants pid)
	{

		wpiTalon.ifPresent(t -> configVelocityControl(t, pid));
		Consumer<Double> velocitySetter = d ->
		{
			if (wpiTalon.map(t -> t.getControlMode() == ControlMode.Velocity).orElse(false))
			{
				wpiTalon.ifPresent(t ->
				{
					t.set(ControlMode.Velocity, d / 10);
				});
			}
		};

		return new RangeOut<Speed>(velocitySetter, 0, sensorRange);
	}

	private static void configVoltageMode(WPI_TalonSRX talon)
	{
		talon.set(ControlMode.PercentOutput, 0);
	}

	public PercentOut getVoltageController()
	{
		wpiTalon.ifPresent(CANTalonHardware::configVoltageMode);
		return new PercentOut(d -> wpiTalon.ifPresent(t ->
		{
			if (t.getControlMode() == ControlMode.PercentOutput)
			{
				t.set(ControlMode.PercentOutput, d);
			} else
			{
				throw new RuntimeException(
						"Error! attempted to use voltage mode after control mode was changed, ensure you are only controlling the talon from one place!");
			}
		}));
	}

	/**
	 * sets hard limit on current, ignores checking for peak current before
	 * activating current limit
	 * 
	 * @param talon 
	 * @param limit the current limit in amps
	 */
	private static void configMaxCurrent(WPI_TalonSRX talon, int limit)
	{
		talon.configPeakCurrentLimit(30, kTimeoutMs);
		talon.configPeakCurrentDuration(100, kTimeoutMs);
		talon.configContinuousCurrentLimit(limit, kTimeoutMs);
		talon.enableCurrentLimit(true);
	}
	public void setMaxCurrent(int limit)
	{
		wpiTalon.ifPresent((t) -> configMaxCurrent(t, limit));
	}

	public void ifPresent(Consumer<WPI_TalonSRX> doIfPresent)
	{
		wpiTalon.ifPresent(doIfPresent);
	}

	public <T> Optional<T> map(Function<WPI_TalonSRX, T> mapFunc)
	{
		return wpiTalon.map(mapFunc);
	}

	private static void configFollowerMode(WPI_TalonSRX toFollow, WPI_TalonSRX wpiTalon)
	{
		wpiTalon.follow(toFollow);
	}

	public void follow(CANTalonHardware toFollow)
	{
		if (toFollow.wpiTalon.isPresent())
		{
			wpiTalon.ifPresent(t -> configFollowerMode(toFollow.wpiTalon.get(), t));
		}
	}

}
