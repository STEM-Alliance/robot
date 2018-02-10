package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HorizontalIntake extends Command
{
    private static boolean toggle = false;
    final boolean open;

    public HorizontalIntake()
    {
        requires(Robot.intakeSolenoidSubsystem);
        open = toggle;
    }

    public HorizontalIntake(boolean high)
    {
        requires(Robot.intakeSolenoidSubsystem);
        open = high;
    }

    protected void initialize()
    {
        SmartDashboard.putBoolean("Intake", open);
        Robot.intakeSolenoidSubsystem.setHorizontal(open);
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        toggle = !open;
    }
}
