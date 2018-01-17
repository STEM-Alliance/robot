package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.robot.config.VisionMode;

public class VisionModeOff extends VisionModeSet
{
    public VisionMode getMode()
    {
        return VisionMode.OFF;
    }
}
