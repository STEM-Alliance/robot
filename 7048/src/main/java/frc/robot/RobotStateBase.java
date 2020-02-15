package frc.robot;

import java.util.Map;

import frc.robot.math.geometry.Kinematics;
import frc.robot.math.geometry.Pose2d;
import frc.robot.math.geometry.Rotation2d;
import frc.robot.math.geometry.Twist2d;
import frc.robot.math.interpolation.InterpolatingDouble;
import frc.robot.math.interpolation.InterpolatingTreeMap;
import frc.robot.utilities.Reportable;


/** RobotStateBase provides global, formatted state of the robot for reuse subsystems
 * @author STEM Alliance of Fargo Moorhead
 * */
public abstract class RobotStateBase implements Reportable
{
    protected RobotStateBase()
    {
        resetDriveState(0.0, new Pose2d());
        resetRobotSpecificState();
    }

    public void reportState()
    {

    }

    /** Reset all robot-specific state to its default values */
    protected abstract void resetRobotSpecificState();

    // --------------------------------------
    // ------------ Drive State -------------
    // --------------------------------------

    private static final int kObservationBufferSize = 100;

    private InterpolatingTreeMap<InterpolatingDouble, Pose2d> field_to_vehicle_;
    private double distance_driven_;
    private Twist2d vehicle_velocity_predicted_;
    private Twist2d vehicle_velocity_measured_;

    public synchronized void addFieldToVehicleObservation(double timestamp, Pose2d observation)
    {
        field_to_vehicle_.put(new InterpolatingDouble(timestamp), observation);
    }

    /** Process the latest drivetrain encoder data for odometry (estimate position of robot) */
    public synchronized void addRobotObservation(double timestamp, Twist2d measured_velocity, Twist2d predicted_velocity)
    {
        addFieldToVehicleObservation(timestamp, Kinematics.integrateForwardKinematics(getLatestFieldToVehicle().getValue(), measured_velocity));
        vehicle_velocity_measured_ = measured_velocity;
        vehicle_velocity_predicted_ = predicted_velocity;
    }

    /** Linear distance (hypotenuse) driven forwards by robot relative to when encoders last zeroed (inches) */
    public synchronized double getDistanceDriven()
    {
        return distance_driven_;
    }

    /** Returns the robot's position on the field at a certain time. Linearly interpolates between stored robot positions to fill in the gaps. */
    public synchronized Pose2d getFieldToVehicle(double timestamp) {
        return field_to_vehicle_.getInterpolated(new InterpolatingDouble(timestamp));
    }

    public synchronized Map.Entry<InterpolatingDouble, Pose2d> getLatestFieldToVehicle()
    {
        return field_to_vehicle_.lastEntry();
    }

    public synchronized Twist2d getMeasuredVelocity()
    {
        return vehicle_velocity_measured_;
    }

    public synchronized Pose2d getPredictedFieldToVehicle(double lookahead_time)
    {
        return getLatestFieldToVehicle().getValue().transformBy(Pose2d.exp(vehicle_velocity_predicted_.scaled(lookahead_time)));
    }

    /** In inches per second */
    public synchronized Twist2d getPredictedVelocity()
    {
        return vehicle_velocity_predicted_;
    }

    public synchronized Twist2d getRobotOdometry(double left_encoder_delta_distance, double right_encoder_delta_distance, Rotation2d current_gyro_angle)
    {
        final Pose2d last_measurement = getLatestFieldToVehicle().getValue();
        final Twist2d delta = Kinematics.forwardKinematics(last_measurement.getRotation(), left_encoder_delta_distance, right_encoder_delta_distance, current_gyro_angle);
        distance_driven_ += delta.dx;
        return delta;
    }

    /** Reset all drive state to its default values (robot stationary) */
    public synchronized void resetDriveState(double start_time, Pose2d initial_field_to_vehicle)
    {
        field_to_vehicle_ = new InterpolatingTreeMap<>(kObservationBufferSize);
        field_to_vehicle_.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
        vehicle_velocity_predicted_ = Twist2d.identity();
        vehicle_velocity_measured_ = Twist2d.identity();
        distance_driven_ = 0.0;
    }

    public synchronized void resetDistanceDriven()
    {
        distance_driven_ = 0.0;
    }
}
