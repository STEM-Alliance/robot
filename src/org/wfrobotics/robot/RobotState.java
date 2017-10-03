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
    public boolean visionInView = false;
    public double visionError = 1;

    protected RobotState() {}

    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    public double getVisionError(int mode)
    {
        if (!visionInView || mode == VisionMode.OFF.getValue())
        {
            return 1;
        }
        return visionError;
    }

    // ------------- State Producers Only -------------

    public void addVisionUpdate(VisionUpdate v)
    {
        if (v.mode != visionMode.getValue())
        {
            resetVisionState();
        }

        if (v.mode == VisionMode.SHOOTER.getValue())
        {
            processShooterUpdate(v);
        }
        else if (v.mode == VisionMode.GEAR.getValue())
        {
            processGearUpdate(v);
        }
    }

    private synchronized void resetVisionState()
    {
        visionInView = false;
        visionError = 1;
        visionMode = VisionMode.OFF;
    }

    private void processShooterUpdate(VisionUpdate v)
    {
        boolean targetsInView = v.targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter

        synchronized(this)
        {
            visionInView = targetsInView;
            visionError = newError;
            visionMode = VisionMode.SHOOTER;
        }
    }

    private void processGearUpdate(VisionUpdate v)
    {
        boolean targetsInView = v.targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter

        synchronized(this)
        {
            visionInView = targetsInView;
            visionError = newError;  // TODO calc this specific to shooter
            visionMode = VisionMode.GEAR;
        }
    }
}