package org.wfrobotics.robot.commands.LED;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetColor extends Command
{
    public SetColor(double patern)
    {
        Robot.led.setLed(patern);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
