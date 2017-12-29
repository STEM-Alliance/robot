package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.vision.VisionModeSet;
import org.wfrobotics.robot.config.VisionMode;

public class VisionModeCamera1 extends VisionModeSet
{
    public int getMode()
    {
        return VisionMode.CAMERA1.getValue();
    }
}
