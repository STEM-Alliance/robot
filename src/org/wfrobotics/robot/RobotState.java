package org.wfrobotics.robot;

import org.wfrobotics.robot.config.VisionMode;
import org.wfrobotics.robot.vision.messages.VisionUpdate;

/** Up-to-date info about Robot, favor over coupling to raw subsystem state in Commands **/
public class RobotState
{
    private static RobotState instance = null;

    // Delicious thread-safe, formatted state for Commands to feast on as they please
    // ------------- Robot State -------------
    public VisionMode visionMode = VisionMode.OFF;
    // ------------- Robot State -------------

    protected RobotState() {}

    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    public void addVisionUpdate(VisionUpdate v)
    {
        if (v.mode == VisionMode.SHOOTER.getValue())
        {
            processShooterUpdate();
        }
        else if (v.mode == VisionMode.GEAR.getValue())
        {
            processGearUpdate();
        }
        else if (v.mode == VisionMode.OFF.getValue() && visionMode != VisionMode.OFF)
        {
            visionJustTurnedOff();
        }
    }

    private synchronized void visionJustTurnedOff()
    {
        // TODO reset robot state for all vision/camera stuff is off
        visionMode = VisionMode.OFF;
    }

    private void processShooterUpdate()
    {
        // TODO determine stuff from update in shooter-specific way
        synchronized(this)
        {
            // TODO reset robot state based on what we just determined
            visionMode = VisionMode.SHOOTER;
        }
    }

    private void processGearUpdate()
    {
        // TODO determine stuff from update in gear-specific way
        synchronized(this)
        {
            // TODO reset robot state based on what we just determined
            visionMode = VisionMode.GEAR;
        }
    }
}