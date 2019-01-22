package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class SparkMaxHardware extends Hardware<CAN> {

	public static final int kTimeoutMs = 10;

	boolean outInverted;
	boolean inInverted;
	CAN requestedPort;
	Optional<CANSparkMax> revSpark;
	Registry registry;
	boolean brushless;

	public SparkMaxHardware(boolean outInverted, boolean inInverted, boolean brushless, CAN requestedPort, Registry registry) {
		this.outInverted = outInverted; 
		this.inInverted = inInverted;
		this.requestedPort = requestedPort;
		this.registry = registry;
		this.brushless = brushless;
	}
    
    @Override
	protected void init(CAN port) {
		CANSparkMax spark;
		if(brushless) {
			spark = new CANSparkMax(requestedPort.index(), MotorType.kBrushless);
		} else {
			spark = new CANSparkMax(requestedPort.index(), MotorType.kBrushed);
		}
		spark.setCANTimeout(kTimeoutMs);
		spark.setInverted(outInverted);
		revSpark.of(spark);

	}

	@Override
	protected void failInit() {
		revSpark = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Spark";
	}

	public void setCurrentLimit(int limit) {
		revSpark.get().setSmartCurrentLimit(limit);
	}
    

}