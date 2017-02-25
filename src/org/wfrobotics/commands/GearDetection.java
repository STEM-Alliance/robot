package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led.HARDWARE;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 */
public class GearDetection extends Command
{
    public GearDetection()
    {
        requires(Robot.targetGearSubsystem);
    }
    
    @Override
    protected void initialize()
    {
    }

    @Override
    protected void execute()
    {
        Utilities.PrintCommand("GearDetectin", this);
        Robot.targetGearSubsystem.run();

        double xDistance = Robot.targetGearSubsystem.DistanceFromCenter;
        
    }
    
    @Override
    protected boolean isFinished()
    {
        return false;
    }

    @Override
    protected void end()
    {
        
    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
