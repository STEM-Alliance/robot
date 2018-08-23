package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoZero extends CommandGroup
{
    public AutoZero()
    {
        this.addSequential(new LiftGoHome(0.5, 0.5));
        this.addSequential(new LiftGoHome(-0.2, 15.0));
    }

    protected void end()
    {
        LiftSubsystem.getInstance().setOpenLoop(0);
    }
}
