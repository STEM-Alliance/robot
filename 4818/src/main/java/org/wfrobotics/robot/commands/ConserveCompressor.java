package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class ConserveCompressor extends Command
{
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
        // TODO Turn of when lift is moving
        sp.setCompressor(isTeleop);
    }

    protected boolean isFinished()
    {
        return false;
    }
}