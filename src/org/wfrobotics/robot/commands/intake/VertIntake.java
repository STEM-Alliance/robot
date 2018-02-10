package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VertIntake extends InstantCommand
{
    private static boolean last = false;
    private final boolean newValue;

    public VertIntake()
    {
        newValue = last;
    }

    protected void initialize()
    {
        Robot.intakeSolenoidSubsystem.setVert(newValue);

        SmartDashboard.putBoolean("vert intake", newValue);
        VertIntake.last = !VertIntake.last;
    }
}
