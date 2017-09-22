package org.wfrobotics.robot.vision;

//* Robot-specific message-specific parsers *//
public abstract class VisionMessages
{
    public static VisionMessage setSource(int key)
    {
        return new VisionMessage("source", String.format("%d", key));
    }
}
