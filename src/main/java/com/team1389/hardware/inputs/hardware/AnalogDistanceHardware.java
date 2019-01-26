package com.team1389.hardware.inputs.hardware;

import java.util.Optional;
import java.util.function.Function;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.Analog;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Value;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogDistanceHardware extends Hardware<Analog>{

    private Optional<AnalogInput> wpiIR;  
    private Function<Double, Double> getDistInInches;
    private double samplesPerSecond;
    
    public AnalogDistanceHardware(SensorType sensor, Analog requestedPort, Registry registry){
        getDistInInches = sensor.getDistInInches;
        samplesPerSecond = sensor.samplesPerSecond;
        attachHardware(requestedPort, registry);
    }  

    public enum SensorType{
        
        //Don't want to magic number this but I don't see a great other option
        SHARP_GP2Y0A21YK0F(volt -> 27/(volt * 2.54), 26);

        private Function<Double, Double> getDistInInches;
        private double samplesPerSecond;

        private SensorType(Function <Double,Double> getDistInInches, double samplesPerSecond){
            this.getDistInInches = getDistInInches;
            this.samplesPerSecond = samplesPerSecond;
        }
    }

    public RangeIn<Position> getPositionInInches(){
        return new RangeIn<Position>(Position.class,() -> getDistInInches.apply(wpiIR.map(s -> s.getVoltage()).orElse(0.0)), 0, 1);
    }

    public RangeIn<Value> getVoltage(){
        return new RangeIn<Value>(Value.class, () -> wpiIR.map(s -> s.getVoltage()).orElse(0.0), 0, 1);
    }

    @Override
    protected void init(Analog port) {
        AnalogInput IRDistanceSensor = new AnalogInput(port.index());
        AnalogInput.setGlobalSampleRate(samplesPerSecond);
        wpiIR = Optional.of(IRDistanceSensor);
    }
    @Override
    protected void failInit() {
        wpiIR = Optional.empty();
    }
    @Override
    protected String getHardwareIdentifier() {
        return "IR Distance Sensor";
    }


} 
