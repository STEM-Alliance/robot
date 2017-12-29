package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.vision.VisionModeSet;
import org.wfrobotics.robot.config.VisionMode;

public class VisionModeCamera2 extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.CAMERA2.getValue();
    }
}
