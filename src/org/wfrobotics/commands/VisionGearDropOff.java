package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup {
    enum STATE {
        YAW, APPROACH, SPRING
    };

    STATE state;
    // comment
    double tolerance;

    public VisionGearDropOff()
    {
        requires(Robot.targetGearSubsystem);
        state = STATE.YAW;
    }


    protected void execute()
    {
        Utilities.PrintCommand("VisionGearDropOff", this, state.toString());
        
        Robot.targetGearSubsystem.run();
        
        double xDistance = Robot.targetGearSubsystem.DistanceFromCenter;
        
        switch (state)
        {
            case YAW:
                
                if(Robot.targetGearSubsystem.InView)
                {
                    if (xDistance > 0.1) // too far to the right
                    {
                        addSequential(new AutoDrive(-.25, 0, 0, 0, 1));
                    }
                    else if (xDistance < 0.1) // too far to the left
                    {
                        addSequential(new AutoDrive(.25, 0, 0, 0, 1));
                    }
                    else
                    {
                        state = STATE.APPROACH;
                    }
                }
                break;
            case APPROACH:
                addSequential(new AutoDrive(0, .25, 0, 0, .5));
                // if(data.Depth == 0 | Robot.driveSubsystem.isSpringInGear())
                // {
                // state = STATE.SPRING;
                // }
                // else if(data.Depth > 0)
                // {
                // addSequential( new AutoDrive(0, .25, 0, 0, .5));
                // }
                break;
            case SPRING:
                if (!Robot.driveSubsystem.isGearStored())
                {
                    isFinished();
                }
                break;
        }
    }
}
