package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command rev's the shooters motor. 
 * This may be useful by itself in situations when you anticipate the need to shoot, reducing the setup time.
 * If another command tries to use the shooter subsystem, I envision this command ending.
 * @author drlindne
 *
 */
public class Rev extends Command
{
    private double setpoint;    
    
    public Rev(double speed)
    {
        requires(Robot.shooterSubsystem);
        
        setpoint = speed;
    }
    
    public Rev(double speed, double timeout)
    {
        requires(Robot.shooterSubsystem);
        
        setpoint = speed;
        setTimeout(timeout);
    }

    @Override
    protected void initialize()
    {
        
    }

    @Override
    protected void execute()
    {
        Robot.shooterSubsystem.setSpeed(setpoint);
    }

    @Override
    protected boolean isFinished()
    {
        return isTimedOut();
    }

    @Override
    protected void end()
    {
        // If you need to shut off the motors, probably create a new Rev(0) command???
    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
