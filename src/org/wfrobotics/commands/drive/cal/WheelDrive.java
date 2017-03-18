package org.wfrobotics.commands.drive.cal;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class WheelDrive extends Command 
{    
    private final double TIME_WORST_F = 1;
    private final double TIME_CLOSED_LOOP_ERROR = TIME_WORST_F + 4;
    
    private double startTime;
    
    public WheelDrive()
    {
        this.requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        startTime = timeSinceInitialized();
        
        // TODO swap out motors
    }
    
    protected void execute()
    {
        double timeElapsed = timeSinceInitialized() - startTime;
        
        if (timeElapsed < TIME_WORST_F)
        {
            Robot.driveSubsystem.driveTank(0, 1);
        }
        else if (timeElapsed < TIME_CLOSED_LOOP_ERROR)
        {
            double command = Utilities.scaleToRange(timeElapsed - TIME_WORST_F, TIME_WORST_F, TIME_CLOSED_LOOP_ERROR, 0, 1);
                    
            Robot.driveSubsystem.driveTank(0, command);
        }
    }
    
    protected boolean isFinished()
    {
        return false;
    }
}
