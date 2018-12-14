package com.team1389;

import com.team1389.hardware.inputs.hardware.NavX;

import edu.wpi.first.wpilibj.SPI.Port;

public class Main {
	public static void main(String[] args) {
		NavX n = new NavX(Port.kOnboardCS0);
		System.out.println(n);
	}
}
