package com.team1389.hardware.inputs.hardware;

import com.kauailabs.navx.frc.AHRS;
import com.team1389.hardware.registry.port_types.SPIPort;


import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.interfaces.Gyro;

//TODO: Figure out why the NavX libraries don't register in VSCode (i.e. VSCode doesn't know it has access to navx libraries. Still builds & compiles)
public class NavX extends AHRS implements Gyro {

	public NavX(Port spi_port_id) {
		super(spi_port_id);
	}
	public NavX(SPIPort spi){
		this(spi.getPort());
	}

	@Override
	public void calibrate() {
		super.reset();
	}
	
}
