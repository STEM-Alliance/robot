
package org.wfrobotics.reuse.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSubsystem;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoDrive extends Command
{
    protected enum MODE {DRIVE, HEADING, TURN, OFF}
    
    protected final MODE mode;
    protected Vector vector;
    protected double rotate;
    protected double heading;
    protected double headingTolerance = 0;
    private boolean endEarly = false;
    
    /**
     * Drive Off
     * Details: Stay put until another command uses the drive subsystem
     */
    public AutoDrive()
    {
        requires(Robot.driveSubsystem);
        this.mode = MODE.OFF;
        this.vector = new Vector(0, 0);
        this.rotate = 0;
        this.heading = SwerveSubsystem.HEADING_IGNORE;
        endEarly = false;
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
        this.mode = MODE.DRIVE;
        vector = new Vector(speedX, speedY);
        rotate = speedR;
        heading = SwerveSubsystem.HEADING_IGNORE;
        setTimeout(timeout);
        endEarly = false;
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
        this.mode = MODE.HEADING;
        vector = new Vector(0, 0);
        rotate = speedR;
        heading = angle; 
        headingTolerance = tolerance;
        endEarly = false;
    }

    public AutoDrive(double speedR)
    {
        requires(Robot.driveSubsystem);
        this.mode = MODE.TURN;
        vector = new Vector(0, 0);
        rotate = speedR;
        heading = -1; 
        headingTolerance = 0;
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
        this.mode = MODE.DRIVE;
        vector = new Vector(speedX, speedY);
        rotate = speedR;
        heading = angle;

        endEarly = false;
        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new Vector(), 0, heading);
        endEarly = false;
        //TODO Use a PID loop here if this isn't good enough
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this, mode.toString() + " M" + Utilities.round(vector.getMag(), 2) 
                                                              + " A" + Utilities.round(vector.getAngle(), 2)
                                                              + " H" + heading 
                                                              + " R" + Utilities.round(rotate, 2));
        if (mode != MODE.OFF)
        {
            Robot.driveSubsystem.driveWithHeading(vector.clone(), rotate, heading);
        }
    }
    
    public void endEarly()
    {
        endEarly  = true;
    }

    protected boolean isFinished()
    {
        boolean done;
        
        if(!endEarly)
        {
            if (mode == MODE.DRIVE || mode == MODE.OFF)
            {
                done = isTimedOut();
            }
            else if (mode == MODE.HEADING)
            {
                if(heading != -1)
                {
                    done = Math.abs(heading - Robot.driveSubsystem.getLastHeading()) < headingTolerance;
                }
                else
                {
                    done = false;
                }
            }
            else
            {
                done = false;
            }
        }
        else
        {
            done = true;
        }
        
        SmartDashboard.putBoolean("AutoIsFinished", done);
        return done;
    }
    
    protected void end() 
    {
        
    }

    protected void interrupted()
    {
        end();
    }
    
    /**
     * Update the AutoDrive command over time (such as based on sensor feedback)
     * @param speedX Magnitude to move right (positive) and left (negative). (Range: -1 to 1)
     * @param speedY Magnitude to move forward (positive) and backward (negative). (Range: -1 to 1)
     * @param speedR Magnitude to turn clockwise (positive) or counterclockwise (negative) while driving. (Range: -1 to 1, Zero: Means don't spin)
     * @param angle Angle to turn the robot to a field relative angle while driving (Range: 0 to 360, -1 is disable, Units: Degrees)
     */
    public void set(double speedX, double speedY, double speedR, double angle)
    {
        vector = new Vector(speedX, speedY);
        rotate = speedR;
        heading = angle;
    }
    
    public void set(Vector vect, double speedR, double angle)
    {
        vector = vect;
        rotate = speedR;
        heading = angle;
    }
}
