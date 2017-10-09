package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.VisionMode;

public class VisionModeShooter extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.SHOOTER.getValue();
    }
}
