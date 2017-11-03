package org.wfrobotics.robot;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.Drive;
import org.wfrobotics.robot.config.VisionMode;
import org.wfrobotics.robot.vision.messages.VisionUpdate;

// TODO use robotGear in commands

/** Up-to-date info about Robot, favor over coupling to raw subsystem state in Commands **/
public class RobotState
{
    // ------------- BEGIN Public State (Read-Only) -------------

    public double robotHeading;
    public boolean robotGear;  // True: High, False: Low

    public boolean visionInView;
    public VisionMode visionMode;
    public double visionWidth;

    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    public HerdVector getRobotVelocity()
    {
        return new HerdVector(robotVelocity);
    }

    public double getVisionError()
    {
        if (!visionInView)
        {
            return 1;
        }
        return visionError;
    }

    // ------------- END Public State (Read-Only) -------------
    // ------------- BEGIN Private -------------

    public enum SHIFT_STATE
    {
        HIGH_GEAR,
        LOW_GEAR,
    }

    private static RobotState instance = null;

    private HerdVector robotVelocity;

    private double visionError;

    protected RobotState()
    {
        robotGear = Drive.SHIFTER_INITIAL_STATE;
        robotHeading = 0;
        robotVelocity = new HerdVector(0, 0);
        resetVisionState();
    }

    // ------------- END Private -------------
    // ------------- BEGIN State Producers Only -------------

    public synchronized void updateRobotGear(boolean isHighGear)
    {
        robotGear = isHighGear;
    }

    public synchronized void updateRobotHeading(double fieldRelativeHeading)
    {
        robotHeading = fieldRelativeHeading;
    }

    public synchronized void updateRobotVelocity(HerdVector velocity)
    {
        robotVelocity = new HerdVector(velocity);
    }

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
        visionWidth = 0;
        visionMode = VisionMode.OFF;
    }

    // TODO confirm target a few times before sensing? Want here since all commands dislike false positives
    private void processShooterUpdate(VisionUpdate v)
    {
        boolean targetsInView = v.targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter
        double newWidth = 0;  // TODO

        synchronized(this)
        {
            visionInView = targetsInView;
            visionError = newError;
            visionWidth = newWidth;
            visionMode = VisionMode.SHOOTER;
        }
    }

    private void processGearUpdate(VisionUpdate v)
    {
        boolean targetsInView = v.targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter
        double newWidth = 0;  // TODO

        synchronized(this)
        {
            visionInView = targetsInView;
            visionError = newError;  // TODO calc this specific to shooter
            visionWidth = newWidth;  // TODO
            visionMode = VisionMode.GEAR;
        }
    }

    // ------------- END State Producers Only -------------
}