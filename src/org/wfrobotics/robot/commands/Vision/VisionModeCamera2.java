package org.wfrobotics.robot.commands.Vision;

import org.wfrobotics.reuse.commands.vision.VisionModeSet;
import org.wfrobotics.robot.config.VisionMode;

public class VisionModeCamera2 extends VisionModeSet
{
    public VisionMode getMode()
    {
        return VisionMode.CAMERA2;
    }
}
