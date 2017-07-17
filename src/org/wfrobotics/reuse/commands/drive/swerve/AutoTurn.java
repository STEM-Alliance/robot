package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoTurn extends Command
{
    protected Vector vector;
    protected double rotate;
    protected double heading;
    protected double headingTolerance = 0;
    private boolean endEarly = false;
    
    public AutoTurn(double speedR)
    {
        requires(Robot.driveSubsystem);
        vector = new Vector(0, 0);
        rotate = speedR;
        heading = -1; 
        headingTolerance = 0;
        endEarly = false;
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0, heading));
        endEarly = false;
        //TODO Use a PID loop here if this isn't good enough
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this, "AutoDrive Turn" + " M" + Utilities.round(vector.getMag(), 2) 
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
        SmartDashboard.putBoolean("AutoIsFinished", endEarly);
        return endEarly;
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
