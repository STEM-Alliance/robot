package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.VisionMode;

public class VisionModeOff extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.OFF.getValue();
    }
}
