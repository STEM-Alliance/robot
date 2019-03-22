package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class UpdateSuperStructure extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final SuperStructure sp = SuperStructure.getInstance();
    private boolean isTeleop = true;

    public UpdateSuperStructure()
    {
        requires(sp);
    }

    protected void initialize()
    {
        isTeleop = DriverStation.getInstance().isOperatorControl();
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