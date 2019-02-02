package com.team1389.hardware.outputs.hardware;

import java.util.Optional;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.outputs.software.DigitalOut;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.registry.port_types.PCM;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * a single solenoid attached to the PCM
 * 
 * @author amind
 *
 */
public class SolenoidHardware extends Hardware<PCM>
{
	private CAN moduleID;

	/**
	 * @param requestedPort
	 *                          the port to attempt to initialize this hardware
	 * @param registry
	 *                          the registry associated with the robot
	 */
	public SolenoidHardware(PCM requestedPort, Registry registry)
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
	public SolenoidHardware(CAN moduleID, PCM requestedPort, Registry registry)
	{
		this.moduleID = moduleID;
	}

	private Optional<Solenoid> wpiSolenoid;

	/**
	 * 
	 * @return a boolean output stream that controls the position of the
	 *         solenoid
	 */
	public DigitalOut getDigitalOut()
	{
		return new DigitalOut(val -> wpiSolenoid.ifPresent(w -> w.set(val)));
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem)
	{
		return super.getSubWatchables(stem).put(getDigitalOut().getWatchable("state"));
	}

	@Override
	protected String getHardwareIdentifier()
	{
		return "Solenoid";
	}

	@Override
	public void init(PCM port)
	{
		wpiSolenoid = Optional.of(new Solenoid(moduleID.index(), port.index()));
	}

	@Override
	public void failInit()
	{
		wpiSolenoid = Optional.empty();
	}

}
