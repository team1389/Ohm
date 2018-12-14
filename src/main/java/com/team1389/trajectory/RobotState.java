package com.team1389.trajectory;

import java.util.Map;

import com.team1389.util.interpolable.InterpolatingDouble;
import com.team1389.util.interpolable.InterpolatingTreeMap;

/**
 * stores a map of recent poses of the robot for state estimation
 * 
 * @see RobotStateEstimator
 * @author amind
 */
public class RobotState {

	/**
	 * the number of stale state observations to store
	 */
	public static final int kObservationBufferSize = 100;
	protected InterpolatingTreeMap<InterpolatingDouble, RigidTransform2d> fieldToVehicle;
	protected RigidTransform2d.Delta vehicleVel;

	public RobotState() {
		reset(0, new RigidTransform2d());
	}

	/**
	 * resets the robots pose and state history
	 * 
	 * @param start_time the timestamp of the initial observation
	 * @param initial_field_to_vehicle the initial pose observation
	 */
	public synchronized void reset(double start_time, RigidTransform2d initial_field_to_vehicle) {
		fieldToVehicle = new InterpolatingTreeMap<>(kObservationBufferSize);
		fieldToVehicle.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
		vehicleVel = new RigidTransform2d.Delta(0, 0, 0);
	}

	/**
	 * looks up a past pose at the given timestamp <br>
	 * will give meaningless values for timeStamps before the initial observation
	 * 
	 * @param timestamp at what point in time do you want to know what the robot's pose was
	 * @return the pose at time {@code timeStamp}
	 */
	public synchronized RigidTransform2d getFieldToVehicle(double timestamp) {
		return fieldToVehicle.getInterpolated(new InterpolatingDouble(timestamp));
	}

	/**
	 * @return the number of stored observations so far (max possible value is
	 *         {@link RobotState#kObservationBufferSize}).
	 */
	public synchronized double getObservationCount() {
		return fieldToVehicle.size();
	}

	/**
	 * get the most recent robot pose estimation
	 * 
	 * @return the most recent field to vehicle observation
	 */
	public synchronized Map.Entry<InterpolatingDouble, RigidTransform2d> getLatestFieldToVehicle() {
		return fieldToVehicle.lastEntry();
	}

	/**
	 * predict the robot's pose some time in the future based on extrapolation from known poses and
	 * velocity
	 * 
	 * @param lookahead_time the time into the future to predict the robot's pose
	 * @return the pose at time {@code lookahead_time} from now
	 */
	public synchronized RigidTransform2d getPredictedFieldToVehicle(double lookahead_time) {
		return getLatestFieldToVehicle().getValue().transformBy(
				RigidTransform2d.fromVelocity(new RigidTransform2d.Delta(vehicleVel.dx * lookahead_time,
						vehicleVel.dy * lookahead_time, vehicleVel.dtheta * lookahead_time)));
	}

	/**
	 * register a pose observation
	 * 
	 * @param timestamp the time of the observation
	 * @param observation the robot's pose at time of observing
	 */
	public synchronized void addFieldToVehicleObservation(double timestamp, RigidTransform2d observation) {
		addObservations(timestamp, observation,
				new RigidTransform2d.Delta(getFieldToVehicle(timestamp - 1), observation));
	}

	/**
	 * register a pose+velocity observation
	 * 
	 * @param timestamp the time of the observation
	 * @param field_to_vehicle the robot's position at time of observing
	 * @param velocity the robot's velocity at time of observing
	 */
	public synchronized void addObservations(double timestamp, RigidTransform2d observation,
			RigidTransform2d.Delta velocity) {
		fieldToVehicle.put(new InterpolatingDouble(timestamp), observation);
		vehicleVel = velocity;
	}

	public synchronized RigidTransform2d.Delta getCurrentVelocity() {
		return vehicleVel;
	}

	public double getLatestX() {
		return getLatestFieldToVehicle().getValue().getTranslation().x_;
	}

	public double getLatestY() {
		return getLatestFieldToVehicle().getValue().getTranslation().y_;
	}

	public double getLatestTheta() {
		return getLatestFieldToVehicle().getValue().getRotation().getDegrees();
	}

}
