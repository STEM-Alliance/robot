package org.wfrobotics.robot.vision.commands;

import org.wfrobotics.robot.config.VisionMode;

public class OffMode extends SetCameraMode
{
    public int getMode()
    {
        return VisionMode.OFF.getValue();
    }
}
