package com.team1389.trajectory;

import java.util.function.Supplier;

import com.team1389.hardware.inputs.software.AngleIn;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.value_types.Position;
import com.team1389.hardware.value_types.Speed;
import com.team1389.util.list.AddList;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.Watchable;
import com.team1389.watch.info.NumberInfo;

import edu.wpi.first.wpilibj.Timer;

/**
 * supplies a {@link RigidTransform2d} representing an estimate of the robot's pose based on sensor
 * input
 * 
 * @author amind
 */
public class RobotStateEstimator implements Supplier<RigidTransform2d>, CompositeWatchable {

	private RangeIn<Position> left;
	private RangeIn<Position> right;
	private RangeIn<Speed> leftVel;
	private RangeIn<Speed> rightVel;
	private AngleIn<Position> gyro;
	private Kinematics kinematics;
	double left_encoder_prev_distance_ = 0;
	double right_encoder_prev_distance_ = 0;
	RobotState state;

	/**
	 * @param trackWidth the width of the wheelbase
	 * @param trackLength the distance from the front axle to the rear axle
	 * @param scrub constant to deal with discrepancy between theoretical effectivDiam and actual
	 *            effective diam
	 */
	public RobotStateEstimator(RobotState state, RangeIn<Position> left, RangeIn<Position> right, RangeIn<Speed> leftS,
			RangeIn<Speed> rightS, AngleIn<Position> gyro, double trackWidth, double trackLength, double scrub) {
		this(state, left, right, leftS, rightS, gyro, new Kinematics(trackLength, trackWidth, scrub));
	}

	public RobotStateEstimator(RobotState state, RangeIn<Position> left, RangeIn<Position> right, RangeIn<Speed> leftS,
			RangeIn<Speed> rightS, AngleIn<Position> gyro, Kinematics kinematics) {
		this.kinematics = kinematics;
		this.gyro = gyro;
		this.left = left;
		this.right = right;
		this.leftVel = leftS;
		this.rightVel = rightS;
		this.state = state;
	}

	@Override
	public RigidTransform2d get() {
		double time = Timer.getFPGATimestamp();
		double left_distance = left.get();
		double right_distance = right.get();
		Rotation2d gyro_angle = Rotation2d.fromDegrees(gyro.get());
		RigidTransform2d odometry = generateOdometryFromSensors(left_distance - left_encoder_prev_distance_,
				right_distance - right_encoder_prev_distance_, gyro_angle);
		RigidTransform2d.Delta velocity = kinematics.forwardKinematics(leftVel.get(), rightVel.get());
		state.addObservations(time, odometry, velocity);
		left_encoder_prev_distance_ = left_distance;
		right_encoder_prev_distance_ = right_distance;
		return state.getLatestFieldToVehicle().getValue();
	}

	/**
	 * estimates the robot's current pose based on the most recent pose observation and the distance
	 * travelled since then
	 * 
	 * @param left_encoder_delta_distance the distance travelled by the left wheels since last state
	 *            observation
	 * @param right_encoder_delta_distance the distance travelled by the right wheels since last
	 *            state observation
	 * @param current_gyro_angle the angle rotated since last state observation
	 * @return the current pose estimate
	 */
	public RigidTransform2d generateOdometryFromSensors(double left_encoder_delta_distance,
			double right_encoder_delta_distance, Rotation2d current_gyro_angle) {
		RigidTransform2d last_measurement = state.getLatestFieldToVehicle().getValue();
		return getKinematics().integrateForwardKinematics(last_measurement, left_encoder_delta_distance,
				right_encoder_delta_distance, current_gyro_angle);
	}

	public Kinematics getKinematics() {
		return kinematics;
	}

	@Override
	public String getName() {
		return "robot state";
	}

	@Override
	public AddList<Watchable> getSubWatchables(AddList<Watchable> stem) {
		return stem.put(new NumberInfo("x", state::getLatestX), new NumberInfo("y", state::getLatestY),
				new NumberInfo("theta", state::getLatestTheta));
	}

}
