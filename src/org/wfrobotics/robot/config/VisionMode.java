package org.wfrobotics.robot.config;

/** Robot-specific vision co-processor set-able states **/
public enum VisionMode
{
    OFF(0),
    SHOOTER(1),
    GEAR(2);

    private final int value;

    private VisionMode(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static VisionMode robotDefault()
    {
        return VisionMode.OFF;
    }
}
