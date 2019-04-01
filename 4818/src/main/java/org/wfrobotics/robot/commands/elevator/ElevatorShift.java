package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Climb;


import edu.wpi.first.wpilibj.command.Command;

public class ElevatorShift extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final Climb climb = Climb.getInstance();

    private final boolean desired;

    public ElevatorShift(boolean liftNotClimb)
    {
        requires(elevator);
        requires(climb);

        desired = liftNotClimb;
    }
    protected void execute()
    {
        climb.setOpenLoop(IO.getInstance().getClimbArmsStick());
        elevator.setOpenLoop(IO.getInstance().getElevatorStick());
        climb.setPullers(1.0);
    }

    protected void initialize()
    {
        elevator.setShifter(desired);
    }

    protected boolean isFinished()
    {
        return false;
    }
    protected void end()
    {
        elevator.setShifter(!desired);
        climb.setPullers(0.0);
    }
}