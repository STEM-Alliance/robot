package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeLift extends Command
{
    private final Wrist wrist;

    public IntakeLift()
    {
        wrist = Robot.wrist;
        requires(wrist);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Wrist", this.getClass().getSimpleName());
    }

    protected void execute()
    {
        wrist.setIntakeLiftSpeed(Robot.controls.getIntakeLift());
    }

    protected boolean isFinished()
    {
        return false;
    }
}
