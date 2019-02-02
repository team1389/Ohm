package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class CANVictorSPXHardware extends Hardware<CAN>
{

    public static final int kTimeoutMs = 10;

    private Optional<VictorSPX> ctreVictor;
    private boolean outputInverted;

    public CANVictorSPXHardware(CAN requestedPort, Registry registry)
    {
        this(false, requestedPort, registry);
    }

    public CANVictorSPXHardware(boolean outputInverted, CAN requestedPort, Registry registry)
    {
        this.outputInverted = outputInverted;
        attachHardware(requestedPort, registry);
    }

    @Override
    protected void init(CAN port)
    {
        VictorSPX victor = new VictorSPX(port.index());
        victor.setInverted(outputInverted);
        ctreVictor = Optional.of(victor);
    }

    public PercentOut getVoltageController()
    {
        return new PercentOut(voltage -> ctreVictor.ifPresent(s -> s.set(ControlMode.PercentOutput, voltage)));
    }

    @Override
    public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
    {
        return super.getSubWatchables(stem).put(getVoltageController().getWatchable("voltage"));
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
}