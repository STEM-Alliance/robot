package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ElevatorZeroThenOpenLoop extends ConditionalCommand
{
    public ElevatorZeroThenOpenLoop()
    {
        super(new ElevatorOpenLoop(), new ElevatorZero());
    }

    protected boolean condition()
    {
        return Elevator.getInstance().hasZeroed();
    }
    
    public static class ElevatorZero extends CommandGroup
    {
        public ElevatorZero()
        {
            this.addSequential(new ElevatorGoHome(0.5, 0.1));
            this.addSequential(new ElevatorGoHome(-0.5, 15.0));
        }

        protected void end()
        {
            Elevator.getInstance().setOpenLoop(0.0);
        }
    }
}
