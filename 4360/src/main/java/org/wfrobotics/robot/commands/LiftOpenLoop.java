package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

//Sets ExampleSubsystem to a safe state
public class LiftOpenLoop extends Command
{
    public LiftOpenLoop()
    {
        requires(Lift.getInstance());
    }

    protected void execute()
    {
        int speed = 0;
        Lift.getInstance().setSpeed(speed);
    }

    protected boolean isFinished()
    {
        return true;
    }
}