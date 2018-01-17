package org.wfrobotics.robot.config;

import javafx.util.Pair;

/** Robot-specific vision co-processor set-able states **/
public enum VisionMode
{
    OFF(0,0), //TODO currently, we don't have an 'off'
    CAMERA1(0,0),
    CAMERA2(1,1),
    CAMERA1_TARGETS_2_STREAM(0,1),
    CAMERA2_TARGETS_1_STREAM(1,0);

    private final int targetSource;
    private final int streamerSource;

    private VisionMode(int target, int streamer)
    {
        this.targetSource = target;
        this.streamerSource = streamer;
    }

    public Pair<Integer, Integer> getValue()
    {
        return new Pair<>(targetSource, streamerSource);
    }

    public static VisionMode robotDefault()
    {
        return VisionMode.OFF;
    }

    public int getTarget()
    {
        return this.targetSource;
    }
    
    public int getStreamer()
    {
        return this.streamerSource;
    }
}
