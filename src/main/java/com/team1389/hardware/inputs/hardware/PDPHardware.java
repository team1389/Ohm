package com.team1389.hardware.inputs.hardware;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.CAN;
import com.team1389.hardware.value_types.Value;
import com.team1389.util.list.AddList;
import com.team1389.watch.Watchable;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * represents a Power Distribution Panel attached to the robot's CAN interface can track the current
 * being drawn from ports on the PDP
 * 
 * @author amind
 *
 */
public class PDPHardware extends Hardware<CAN> {
	Optional<PowerDistributionPanel> wpiPDP;
	private final int TOTAL_PDP_CHANNELS = 15;

	/**
	 * @param registry the registry associated with the robot generates an instance of the PDP on
	 *            the default CAN port (0)
	 * @see <a href=
	 *      "http://wpilib.screenstepslive.com/s/4485/m/13809/l/219414-power-distribution-panel">WPILib
	 *      PDP docs</a>
	 */
	public PDPHardware(Registry registry) {
		this(new CAN(0), registry);
	}

	/**
	 * @param registry the registry associated with the robot generates an instance of the PDP on
	 *            the given can port
	 * @see <a href=
	 *      "http://wpilib.screenstepslive.com/s/4485/m/13809/l/219414-power-distribution-panel">WPILib
	 *      PDP docs</a>
	 */
	public PDPHardware(CAN can, Registry registry) {
		super(can, registry);
	}

	/**
	 * @param port port to check current on
	 * @return a value stream that tracks the current flowing through the port
	 */
	public RangeIn<Value> getCurrentIn(int port) {
		return new RangeIn<Value>(Value.class, () -> getCurrentDraw(port),0,1);
	}

	public double getCurrentDraw(int port) {
		double val = wpiPDP.map(pdp -> pdp.getCurrent(port)).orElse(0.0);
		return val;
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		stem.addAll(IntStream
				.range(0, TOTAL_PDP_CHANNELS)
					.mapToObj(port -> getCurrentIn(port).getWatchable("port " + port + " current"))
					.collect(Collectors.toList()));
		return stem;
	}

	/**
	 * 
	 * @return TODO determine max PDP current
	 */
	public RangeIn<Value> getCurrentIn() {
		return new RangeIn<Value>(Value.class, () -> wpiPDP.map(pdp -> pdp.getTotalCurrent()).orElse(0.0), 0, 100);
	}

	@Override
	protected String getHardwareIdentifier() {
		return "PDP";
	}

	@Override
	public void init(CAN port) {
		this.wpiPDP = Optional.of(new PowerDistributionPanel(port.index()));
	}

	@Override
	public void failInit() {
		this.wpiPDP = Optional.empty();
	}
}
