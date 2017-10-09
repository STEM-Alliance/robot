package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.VisionMode;

public class VisionModeGear extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.GEAR.getValue();
    }
}
