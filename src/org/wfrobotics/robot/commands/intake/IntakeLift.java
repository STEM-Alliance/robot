package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeLift extends Command
{
    public IntakeLift()
    {
        requires(Robot.wrist);
    }

    protected void execute()
    {
        double speed = Robot.controls.getIntakeLift();
        Robot.wrist.setIntakeLiftSpeed(speed);
        SmartDashboard.putNumber("Intake Lift Speed", speed);
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.wrist.setIntakeLiftSpeed(0);
    }
}
