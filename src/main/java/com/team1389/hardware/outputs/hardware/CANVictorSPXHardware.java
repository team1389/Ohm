package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;

/**
 * This class offers input/output stream sources for a Victor SPX. Note that
 * this class does not support offloading pid control to the victor, and only
 * supports voltage control. Also can only current limit if slave for a CANTalon
 */
public class CANVictorSPXHardware extends Hardware<CAN>
{

    // constants
    public static final int kTimeoutMs = 10;
    public static final int kPrimaryPIDLoopIdx = 0;

    private Optional<VictorSPX> ctreVictor;

    // configs
    private boolean outputInverted;
    private boolean inputInverted;
    private FeedbackDevice selectedSensor;
    private int sensorRange;

    public CANVictorSPXHardware(boolean outputInverted, CAN requestedPort, Registry registry)
    {
        this(outputInverted, false, FeedbackDevice.QuadEncoder, 0, requestedPort, registry);
    }

    public CANVictorSPXHardware(boolean outputInverted, boolean inputInverted, FeedbackDevice selectedSensor,
            int sensorRange, CAN requestedPort, Registry registry)
    {
        this.outputInverted = outputInverted;
        this.inputInverted = inputInverted;
        this.selectedSensor = selectedSensor;
        this.sensorRange = sensorRange;
        attachHardware(requestedPort, registry);
    }

    @Override
    protected void init(CAN port)
    {
        VictorSPX victor = new VictorSPX(port.index());
        victor.setInverted(outputInverted);
        // Line below determines whether to invert sensor
        victor.setSensorPhase(inputInverted);
        victor.configSelectedFeedbackSensor(selectedSensor);
        victor.configNominalOutputForward(0, kTimeoutMs);
        victor.configNominalOutputReverse(0, kTimeoutMs);
        victor.configPeakOutputForward(1, kTimeoutMs);
        victor.configPeakOutputReverse(-1, kTimeoutMs);
        ctreVictor = Optional.of(victor);
    }

    public PercentOut getVoltageController()
    {
        return new PercentOut(voltage -> ctreVictor.ifPresent(s -> s.set(ControlMode.PercentOutput, voltage)));
    }

    public RangeIn<Position> getSensorPositionStream()
    {
        return new RangeIn<>(Position.class,
                () -> ctreVictor.map(t -> (double) t.getSelectedSensorPosition(kPrimaryPIDLoopIdx)).orElse(0.0), 0.0,
                sensorRange);
    }

    @Override
    protected void failInit()
    {
        ctreVictor = Optional.empty();
    }

    @Override
    protected String getHardwareIdentifier()
    {
        return "PWM Victor SPX";
    }

    protected Optional<VictorSPX> getVictor()
    {
        return ctreVictor;
    }

    private static void configFollowerMode(IMotorController toFollow, VictorSPX victor)
    {
        victor.follow(toFollow);
    }

    public void follow(CANTalonHardware toFollow)
    {

        if (toFollow.getTalon().isPresent())
        {
            ctreVictor.ifPresent(victor -> configFollowerMode(toFollow.getTalon().get(), victor));
        }
    }

    public void follow(CANVictorSPXHardware toFollow)
    {

        if (toFollow.getVictor().isPresent())
        {
            ctreVictor.ifPresent(victor -> configFollowerMode(toFollow.getVictor().get(), victor));
        }
    }

}