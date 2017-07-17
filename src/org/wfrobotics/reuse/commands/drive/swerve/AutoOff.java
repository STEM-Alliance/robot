package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Drive Off - Stay put until another command uses the drive subsystem
 */
public class AutoOff extends Command
{   
    public AutoOff()
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
        Utilities.PrintCommand("Drive", this, "AutoDrive Off");
    }

    protected boolean isFinished()
    {
        SmartDashboard.putBoolean("AutoIsFinished", true);
        
        return true;
    }
}
