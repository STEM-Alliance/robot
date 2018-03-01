package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.led.RevLEDs;
import org.wfrobotics.robot.commands.LED.SetColor;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

public class LED extends Subsystem
{
    public Spark underglow;
    public LED()
    {
        underglow = new Spark(RobotMap.REV_UNDERGLOW);
    }
    public void setLed(double pattern)
    {
        underglow.set(pattern);
    }
    protected void initDefaultCommand()
    {
        setDefaultCommand(new SetColor(RevLEDs.getValue(RevLEDs.PatternName.Yellow)));
    }

}
