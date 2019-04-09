package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class ConserveCompressor extends Command
{
    private final SuperStructure sp = SuperStructure.getInstance();
    //    private final LiftSubsystem lift = LiftSubsystem.getInstance();
    private boolean isTeleop = true;

    public ConserveCompressor()
    {
        requires(sp);
    }

    protected void initialize()
    {
        //isTeleop = DriverStation.getInstance().isOperatorControl();
        sp.setCompressor(true);
    }

    protected void execute()
    {
        //        sp.setCompressor(isTeleop && lift.onTarget());
        sp.setCompressor(true);
    }

    protected boolean isFinished()
    {
        return false;
    }
}