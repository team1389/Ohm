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

public class PWMVictorSPXHardware extends Hardware<PWM>{
    private Optional<PWMVictorSPX> wpiVictor;
    private boolean outputInverted;

    public PWMVictorSPXHardware(PWM port, Registry registry){
        this(false, port, registry);
    }
    public PWMVictorSPXHardware( boolean outputInverted, PWM port,Registry registry){
        this.outputInverted = outputInverted;
        attachHardware(port, registry);
    }

	/**
	 * @return a voltage output stream for this victor
	 */
	public PercentOut getVoltageController() {
		return new PercentOut(voltage -> wpiVictor.ifPresent(s -> s.set(voltage)));
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return super.getSubWatchables(stem).put(getVoltageController().getWatchable("voltage"));
    }

    @Override
    protected void init(PWM port) {
        PWMVictorSPX victor = new PWMVictorSPX(port.index());
        victor.setInverted(outputInverted);
        wpiVictor = Optional.of(victor);
    }
    @Override
    protected void failInit() {
        wpiVictor = Optional.empty();
    }
    @Override
    protected String getHardwareIdentifier() {
        return "PWM Victor SPX";
    }
}