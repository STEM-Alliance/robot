package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class ConserveCompressor extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final SuperStructure sp = SuperStructure.getInstance();
    private boolean isTeleop = true;

    public ConserveCompressor()
    {
        requires(sp);
    }

    protected void initialize()
    {
        isTeleop = DriverStation.getInstance().isOperatorControl();
    }

    protected void execute()
    {
        sp.setCompressor(isTeleop && !state.isLiftMoving);
    }

    protected boolean isFinished()
    {
        return false;
    }
}