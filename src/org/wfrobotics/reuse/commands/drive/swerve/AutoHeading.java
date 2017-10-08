package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Turn to an Angle - Rotate to a field relative angle
 */
public class AutoHeading extends Command
{
    protected double rotate;
    protected double heading;
    protected double headingTolerance = 0;

    /**
     * Note: This constructor doesn't need a timeout because the command will end when the angle is reached (within tolerance)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param angle Angle to turn the robot to a field relative angle (Range: -180 to 180, Units: Degrees)
     * @param tolerance Maximum error while at that angle(Units: Degrees)
     */
    public AutoHeading(double speedR, double angle, double tolerance)
    {
        requires(Robot.driveSubsystem);
        rotate = speedR;
        heading = angle;
        headingTolerance = tolerance;
    }

    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0, heading));
        //TODO Use a PID loop here if this isn't good enough
    }

    protected void execute()
    {
        Utilities.PrintCommand("Drive", this, "AutoDrive Heading" + " H" + heading + " R" + Utilities.round(rotate, 2));
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), rotate, heading));
    }

    protected boolean isFinished()
    {
        boolean done;

        if(heading != -1)
        {
            done = Math.abs(heading - RobotState.getInstance().robotHeading) < headingTolerance;
        }
        else
        {
            done = false;
        }

        SmartDashboard.putBoolean("AutoIsFinished", done);
        return done;
    }
}
