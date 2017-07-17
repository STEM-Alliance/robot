
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive extends Command
{
    protected Vector vector;
    protected double rotate;
    protected double heading;
    protected double headingTolerance = 0;
    private boolean endEarly = false;

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
        heading = SwerveSignal.HEADING_IGNORE;
        setTimeout(timeout);
        endEarly = false;
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

        endEarly = false;
        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0, heading));
        endEarly = false;
        //TODO Use a PID loop here if this isn't good enough
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this, "AutoDrive" + " M" + Utilities.round(vector.getMag(), 2) 
                                                          + " A" + Utilities.round(vector.getAngle(), 2)
                                                          + " H" + heading 
                                                          + " R" + Utilities.round(rotate, 2));
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(vector.getMag(), vector.getAngle()), rotate, heading));
    }
    
    public void endEarly()
    {
        endEarly  = true;
    }

    protected boolean isFinished()
    {
        boolean done;
        
        if(endEarly)
        {
            done = true;
        }
        else
        {
            done = isTimedOut();
        }
        
        SmartDashboard.putBoolean("AutoIsFinished", done);
        return done;
    }
    
    /**
     * Update the AutoDrive command over time (such as based on sensor feedback)
    */
    public void set(Vector vect, double speedR, double angle)
    {
        vector = vect;
        rotate = speedR;
        heading = angle;
    }
}
