package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class ConserveCompressor extends Command
{
    private final SuperStructure sp = SuperStructure.getInstance();
    private final Lift lift = Lift.getInstance();
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
        sp.setCompressor(isTeleop && lift.onTarget());
        sp.setCompressor(false);  // vision testing
    }

    protected boolean isFinished()
    {
        return false;
    }
}