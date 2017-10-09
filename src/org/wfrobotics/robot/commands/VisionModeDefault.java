package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.VisionMode;

public class VisionModeDefault extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.robotDefault().getValue();
    }
}
