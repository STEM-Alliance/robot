package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ElevatorZero extends CommandGroup
{
    public ElevatorZero()
    {
        this.addSequential(new ElevatorGoHome(0.5, 0.5));
        this.addSequential(new ElevatorGoHome(-0.2, 15.0));
    }

    protected void end()
    {
        Elevator.getInstance().setOpenLoop(0.0);
    }
}
