
package org.wfrobotics.commands;

import org.wfrobotics.Vector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive extends Command
{
    private Vector vector;
    private double rotate;
    private double heading;
    private double headingTolerance = 0;
    
    /**
     * Drive Off
     * Details: Stay put until another command uses the drive subsystem
     */
    public AutoDrive()
    {
        requires(Robot.driveSubsystem);
        this.vector = new Vector(0, 0);
        this.rotate = 0;
        this.heading = SwerveDriveSubsystem.HEADING_IGNORE;
    }

    /**
     * Drive Normally
     * Details: Drive any magnitude and angle. Forward, backwards, sideways, at an angle.
     * Note: Use this constructor or the turn to angle one for the majority of autonomous driving
     * @param speedX Magnitude to move right (positive) and left (negative). (Range: -1 to 1)
     * @param speedY Magnitude to move forward (positive) and backward (negative). (Range: -1 to 1)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param timeout How long before this command finishes (Seconds)
     */
    public AutoDrive(double speedX, double speedY, double speedR, double timeout)
    {
        requires(Robot.driveSubsystem);
        vector = new Vector(speedX, speedY);
        rotate = speedR;
        heading = SwerveDriveSubsystem.HEADING_IGNORE;
        setTimeout(timeout);
    }
    
    /**
     * Turn to an Angle
     * Details: Rotate to a field relative angle
     * Note: This constructor doesn't need a timeout because the command will end when the angle is reached (within tolerance)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param angle Angle to turn the robot to a field relative angle (Range: -180 to 180, Units: Degrees)
     * @param tolerance Maximum error while at that angle(Units: Degrees)
     */
    public AutoDrive(double speedR, double angle, double tolerance)
    {
        requires(Robot.driveSubsystem);
        vector = new Vector(0, 0);
        rotate = speedR;
        heading = angle; 
        headingTolerance = tolerance;
    }
    
    /**
     * Drive Halo
     * Details: Drive while simultaneously turning to (and thereafter maintaining) a specific field relative angle. 
     * Note: A.K.A drive like your drive system is legit
     * @param speedX Magnitude to move right (positive) and left (negative). (Range: -1 to 1)
     * @param speedY Magnitude to move forward (positive) and backward (negative). (Range: -1 to 1)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param angle Angle to turn the robot to a field relative angle while driving (Range: -180 to 180, Units: Degrees)
     * @param timeout How long before this command finishes (Seconds)
     */
    public AutoDrive(double speedX, double speedY, double speedR, double angle, double timeout)
    {
        requires(Robot.driveSubsystem);
        vector = new Vector(speedX, speedY);
        rotate = speedR;
        heading = angle;
        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        
        //TODO Use a PID loop here if this isn't good enough
    }

    protected void execute() 
    {
        SmartDashboard.putString("AutoDrive", "X: " + vector.getX()+ "Y: " + vector.getY() + "R: " + rotate + "H: " + heading);
        Robot.driveSubsystem.driveWithHeading(vector, rotate, heading);
    }

    protected boolean isFinished()
    {
        boolean done;
        
        if(headingTolerance != 0)
        {
            done = Math.abs(heading - Robot.driveSubsystem.getLastHeading()) < headingTolerance;
        }
        else
        {
            done = isTimedOut();
        }
        
        return done;
    }
    
    protected void end() 
    {
        
    }

    protected void interrupted()
    {
        
    }
}
