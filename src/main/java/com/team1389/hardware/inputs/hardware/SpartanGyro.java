package com.team1389.hardware.inputs.hardware;

import java.util.Optional;

import com.team1389.command_framework.command_base.Command;
import com.team1389.hardware.Hardware;
import com.team1389.hardware.inputs.hardware.ADXRS453_Gyro;
import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.registry.Registry;
import com.team1389.hardware.registry.port_types.SPIPort;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.util.Timer;

public class SpartanGyro extends Hardware<SPIPort> {
	private Optional<ADXRS453_Gyro> gyro;

	public SpartanGyro(SPIPort port, Registry registry) {
		super(port, registry);
	}

	public AngleIn<Position> getAngleInput() {
		return new AngleIn<>(Position.class, () -> gyro.map(g -> g.getAngle()).orElse(0.0));
	}

	public AngleIn<Speed> getRateInput() {
		return new AngleIn<>(Speed.class, () -> gyro.map(g -> g.getRate()).orElse(0.0));
	}

	public void calibrate() {
		gyro.ifPresent(g -> g.calibrate());
	}

	public void endCalibrate() {
		gyro.ifPresent(g -> g.endCalibrate());
	}

	public void cancelCalibrate() {
		gyro.ifPresent(g -> g.cancelCalibrate());
	}

	public void startCalibrate() {
		gyro.ifPresent(g -> g.startCalibrate());
	}

	public double getCenter() {
		return gyro.map(g -> g.getCenter()).orElse(0.0);
	}

	@Override
	protected void init(SPIPort port) {
		gyro = Optional.of(new ADXRS453_Gyro(port));
	}

	@Override
	protected void failInit() {
		this.gyro = Optional.empty();
	}

	@Override
	protected String getHardwareIdentifier() {
		return "Gyro";
	}

	public class CalibrateCommand extends Command {
		Timer timer;
		boolean indefinite;
		boolean finishedFirstCalibrate;

		public CalibrateCommand() {
			this(false);
		}

		public CalibrateCommand(boolean indefinite) {
			this.indefinite = indefinite;
			timer = new Timer();
			this.finishedFirstCalibrate = false;
		}

		@Override
		protected void initialize() {
			startCalibrate();
			timer.zero();
		}

		@Override
		protected boolean execute() {
			boolean finishedCurrentCalibrate = timer.get() > 5;
			finishedFirstCalibrate = finishedFirstCalibrate || finishedCurrentCalibrate;
			if (finishedCurrentCalibrate) {
				System.out.println("Gyro calibrated, new zero is " + getCenter());
				endCalibrate();
				if (indefinite) {
					startCalibrate();
					timer.zero();
				}
			}
			return finishedFirstCalibrate && !indefinite;
		}

		@Override
		protected void done() {
			if (!finishedFirstCalibrate) {
				System.err.println("gyro did not finish calibration before cancel");
			}
			cancelCalibrate();
		}

		@Override
		public void cancel() {
			System.out.println("cancelling current calibration process");
			super.cancel();
		}
	}

}
