package frc.robot.subsystems;

import org.wfrobotics.reuse.math.HerdAngle;
import frc.robot.EnhancedRobot;

/** Controller (does the math) to heading correct (steer) the robot */
public class SteeringController
{
    public final double kStopSteeringDegrees = 2.0;
    private final double kP;
    private final double kI;
    private final double kMaxSpeed;
    private double integral;
    private double prevTime;

    public SteeringController(double kP, double kI)
    {
        this.kP = kP; //0.000022;
        this.kI = kI; //0.000005;
        kMaxSpeed = EnhancedRobot.getConfig().getTankConfig().VELOCITY_MAX;
        reset(0.0);
    }

    public void reset(double timestamp)
    {
        prevTime = timestamp;
        integral = 0.0;
    }

    public double correctHeading(double timestamp, double desired, double actual, boolean doneSteering)
    {
        double dt = timestamp - prevTime;
        double error = new HerdAngle(desired - actual).getAngle();

        error = Math.min(error, kStopSteeringDegrees);
        error = Math.max(error, -kStopSteeringDegrees);

        integral += error * dt;

        double p_out = error * kP;
        double i_out = integral * kI;
        double output = p_out + i_out;

        output *= (doneSteering) ? 0.0 : 1.0;

        double outputInRange = output * kMaxSpeed;

        prevTime = timestamp;
        return outputInRange;
    }
}
