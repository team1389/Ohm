package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.DigitalIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Value;

import edu.wpi.first.wpilibj.Compressor;

public class CompressorHardware extends Hardware<CAN>
{
    private Optional<Compressor> wpiCompressor;
    private final int MAX_CURRENT_IN_AMPS = 10;

    public CompressorHardware(Registry registry)
    {
        attachHardware(new CAN(1), registry);
    }

    /**
     * @param moduleID
     *                     CAN ID for the PCM that the compressor is on
     */
    public CompressorHardware(CAN moduleID, Registry registry)
    {
        attachHardware(moduleID, registry);
    }

    public RangeIn<Value> getCurrent()
    {
        return new RangeIn<Value>(Value.class,
                () -> wpiCompressor.map(compressor -> compressor.getCompressorCurrent()).orElse(0.0), 0,
                MAX_CURRENT_IN_AMPS);
    }

    public DigitalIn isEnabled()
    {
        return new DigitalIn(() -> wpiCompressor.map(compressor -> compressor.enabled()).orElse(false));
    }

    @Override
    protected void init(CAN moduleID)
    {
        Compressor compressor = new Compressor(moduleID.index());
        wpiCompressor = Optional.of(compressor);
    }

    @Override
    protected void failInit()
    {
        wpiCompressor = Optional.empty();
    }

    @Override
    protected String getHardwareIdentifier()
    {
        return "Compressor";
    }
}