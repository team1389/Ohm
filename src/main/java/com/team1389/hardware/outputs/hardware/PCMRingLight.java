package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.DigitalOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.registry.port_types.PCM;

import edu.wpi.first.wpilibj.Solenoid;

public class PCMRingLight extends Hardware<PCM>
{
    // note that Solenoid is used here to represent controlling something
    // through PCM
    private Optional<Solenoid> light;
    private CAN moduleID;

    public PCMRingLight(PCM requestedPort, Registry registry)
    {
        this(new CAN(1), requestedPort, registry);
    }

    /**
     * @param moduleID
     *                          the port of the PCM that this solenoid is
     *                          connected to
     * @param requestedPort
     *                          the port to attempt to initialize this hardware
     * @param registry
     *                          the registry associated with the robot
     */
    public PCMRingLight(CAN moduleID, PCM requestedPort, Registry registry)
    {
        this.moduleID = moduleID;
        attachHardware(requestedPort, registry);
    }

    @Override
    protected void init(PCM port)
    {
        light = Optional.of(new Solenoid(moduleID.index(), port.index()));
    }

    @Override
    protected void failInit()
    {
        light = Optional.empty();
    }

    @Override
    protected String getHardwareIdentifier()
    {
        return "PCM Ring Light";
    }

    /**
     * 
     * @return stream that controls whether the light is on or off
     */
    public DigitalOut getLightController()
    {
        return new DigitalOut(val -> light.ifPresent(internalLight -> internalLight.set(val)));
    }
}