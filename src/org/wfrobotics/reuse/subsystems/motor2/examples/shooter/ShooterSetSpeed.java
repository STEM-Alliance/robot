package org.wfrobotics.reuse.subsystems.motor2.examples.shooter;

import org.wfrobotics.reuse.subsystems.motor2.examples.ExampleRobot;
import org.wfrobotics.reuse.subsystems.motor2.examples.goals.Goal;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Team 4818 WFRobotics
 */
public class ShooterSetSpeed extends Command 
{
    private final Goal setpoint;
    
    protected ShooterSetSpeed(Goal desired)
    {
        setpoint = desired;
    }
    
    protected void initialize()
    {
        ExampleRobot.shooter.set(setpoint);
    }
    
    protected boolean isFinished()
    {
        return ExampleRobot.shooter.atSetpoint(setpoint);
    }
}
