package org.wfrobotics.robot.vision;


public class VisionMessage
{
    private final String message;

    public VisionMessage(String serialized)
    {
        message = serialized;
    }

    public VisionMessage(String type, String value)
    {
        message = String.format("%s,%s", type, value);
    }

    public byte[] bytes()
    {
        return message.getBytes();
    }
}
