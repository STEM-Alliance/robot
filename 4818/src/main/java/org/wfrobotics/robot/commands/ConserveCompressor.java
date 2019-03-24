package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

public final class ConserveCompressor extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final SuperStructure sp = SuperStructure.getInstance();
    private boolean isTeleop = true;

    public ConserveCompressor()
    {
        requires(sp);
    }

    protected void initialize()
    {
        // isTeleop = DriverStation.getInstance().isOperatorControl();
        isTeleop = true;  // Doing teleop in sandstorm
    }

    protected void execute()
    {
        final boolean elevatorNotMovingFast = elevator.onTarget();
        sp.setCompressor(isTeleop && elevatorNotMovingFast);
    }

    protected boolean isFinished()
    {
        return false;
    }
}