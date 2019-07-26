package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JoystickMove extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    private final IO io = IO.getInstance();
    ArmSubsystem armSubsystem = ArmSubsystem.getInstance();

    double posX, 
            posY;

    public JoystickMove()
    {
        posX = 0.0;
        posY = 0.0;

        requires(armSubsystem);
    }
    protected void execute()
    {
        posX = io.getX() + 0.5;
        posY = io.getY() + 0.5;
        armSubsystem.moveBottom(posX);
        armSubsystem.moveTop(posY);
        SmartDashboard.putNumber("x", posX);
        SmartDashboard.putNumber("Y", posY);


    }
    protected void end()
    {
        armSubsystem.moveBottom(posX);
        armSubsystem.moveTop(posY);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
