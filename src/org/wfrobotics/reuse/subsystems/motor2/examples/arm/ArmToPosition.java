package org.wfrobotics.reuse.subsystems.motor2.examples.arm;

import org.wfrobotics.reuse.subsystems.motor2.examples.ExampleRobot;
import org.wfrobotics.reuse.subsystems.motor2.goals.Goal;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Team 4818 WFRobotics
 */
public class ArmToPosition extends Command
{
    private final Goal setpoint;
    
    protected ArmToPosition(Goal desired)
    {
        setpoint = desired;
    }
    
    protected void initialize()
    {
        ExampleRobot.arm.set(setpoint);
    }

    protected boolean isFinished()
    {
        return ExampleRobot.arm.atSetpoint(ArmConfig.TOP);
    }
}
