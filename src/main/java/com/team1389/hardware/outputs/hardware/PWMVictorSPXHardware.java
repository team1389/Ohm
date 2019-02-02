package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.PercentOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.PWM;
import com.team1389.hardware.registry.port_types.PortInstance;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.PWMVictorSPX;

/**
 * provides voltage control stream for a PWM Victor SPX
 */
public class PWMVictorSPXHardware extends Hardware<PWM>
{

    private Optional<PWMVictorSPX> wpiVictor;
    private boolean outputInverted;

    public PWMVictorSPXHardware(PWM requestedPort, Registry registry)
    {
        this(false, requestedPort, registry);
    }

    /**
     * @param outputInverted
     *                           Whether to reverse motor direction
     * @param requestedPort
     *                           PWM port to initialize hardware on
     * @param registry
     *                           registry for the client robot project
     */
    public PWMVictorSPXHardware(boolean outputInverted, PWM requestedPort, Registry registry)
    {
        this.outputInverted = outputInverted;
        attachHardware(requestedPort, registry);
    }

    /**
     * @return a voltage output stream for this Victor SPX
     */
    public PercentOut getVoltageController()
    {
        return new PercentOut(voltage -> wpiVictor.ifPresent(s -> s.set(voltage)));
    }

    /**
     * @return the list of watchables with a watchable for the voltage output
     *         stream added
     */
    @Override
    public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
    {
        return super.getSubWatchables(stem).put(getVoltageController().getWatchable("voltage"));
    }

    /**
     * runs if port is open. Configures Victor to match settings from
     * constructors
     */
    @Override
    protected void init(PWM port)
    {
        PWMVictorSPX victor = new PWMVictorSPX(port.index());
        victor.setInverted(outputInverted);
        wpiVictor = Optional.of(victor);
    }

    /**
     * runs if port is not open
     */
    @Override
    protected void failInit()
    {
        wpiVictor = Optional.empty();
    }

    @Override
    protected String getHardwareIdentifier()
    {
        return "PWM Victor SPX";
    }
}