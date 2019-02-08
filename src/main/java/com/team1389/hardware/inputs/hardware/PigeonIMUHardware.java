package com.team1389.hardware.inputs.hardware;

import java.util.Optional;
import java.util.function.Supplier;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Position;

/**
 * IMU wrapper for the Pigeon IMU, which provides yaw, pitch, and roll. Note
 * that the IMU should be temperature calibrated before first time use
 */
public class PigeonIMUHardware extends Hardware<CAN>
{

    private Optional<PigeonIMU> wpiIMU;

    public PigeonIMUHardware(CAN requestedPort, Registry registry)
    {
        attachHardware(requestedPort, registry);
    }

    @Override
    protected void init(CAN port)
    {
        // automatically calibrates on startup
        wpiIMU = Optional.of(new PigeonIMU(port.index()));
    }

    @Override
    protected void failInit()
    {
        wpiIMU = Optional.empty();
    }

    @Override
    protected String getHardwareIdentifier()
    {
        return "Pigeon IMU";
    }

    public AngleIn<Position> getYawInput()
    {
        return new AngleIn<Position>(Position.class, () -> wpiIMU.map((imu) ->
        {
            double[] yprInDegrees = new double[3];
            imu.getYawPitchRoll(yprInDegrees);
            return yprInDegrees[0];
        }).orElse(0.0));
    }

    public AngleIn<Position> getPitchInput()
    {
        return new AngleIn<Position>(Position.class, () -> wpiIMU.map((imu) ->
        {
            double[] yprInDegrees = new double[3];
            imu.getYawPitchRoll(yprInDegrees);
            return yprInDegrees[1];
        }).orElse(0.0));
    }

    public AngleIn<Position> getRollInput()
    {
        return new AngleIn<Position>(Position.class, () -> wpiIMU.map((imu) ->
        {
            double[] yprInDegrees = new double[3];
            imu.getYawPitchRoll(yprInDegrees);
            return yprInDegrees[2];
        }).orElse(0.0));
    }
}