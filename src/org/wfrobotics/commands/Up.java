package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Controls the climber's motion
 * This command controls the climber subsystem motor
 * @author drlindne
 *
 */
public class Up extends Command
{
    public enum MODE {CLIMB, HOLD, DOWN, OFF};
    
    private final MODE mode;
    
    public Up(MODE mode)
    {
        requires(Robot.climberSubsystem);
        
        this.mode = mode;
    }
    
    @Override
    protected void initialize()
    {

    }

    @Override
    protected void execute()
    {
        throw new RuntimeException("Modes not implemented yet");
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

    }

}
