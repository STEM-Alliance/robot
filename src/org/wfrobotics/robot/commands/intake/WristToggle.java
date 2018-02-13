package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WristToggle extends InstantCommand
{
    private static boolean last = false;
    private final boolean flex;

    // TODO Make this work like JawsToggle, which is super working

    public WristToggle()
    {
        flex = last;
    }

    protected void initialize()
    {
        Robot.intakeSubsystem.setVert(flex);  // TODO Rename to setFlex()?

        SmartDashboard.putBoolean("vert intake", flex);
        WristToggle.last = !WristToggle.last;
    }
}
