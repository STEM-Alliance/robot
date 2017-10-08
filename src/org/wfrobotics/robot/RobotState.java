package org.wfrobotics.robot;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.VisionMode;
import org.wfrobotics.robot.vision.messages.VisionUpdate;

/** Up-to-date info about Robot, favor over coupling to raw subsystem state in Commands **/
public class RobotState
{
    private static RobotState instance = null;
    private HerdVector robotVelocity = new HerdVector(0, 0);

    // ------------- Robot State -------------
    public double robotHeading = 0;
    public VisionMode visionMode = VisionMode.OFF;
    public boolean visionInView = false;
    public double visionError = 1;

    protected RobotState() {}

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

    // ------------- State Producers Only -------------

    // TODO should this be a HerdVector?
    // TODO call this from drive for gryo, eliminate from subsystem last heading members
    // TODO should we expose the chassisAngleControler error as robotHeadingError for some reason?
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
        visionMode = VisionMode.OFF;
    }

    // TODO confirm target a few times before sensing?
    // TODO do in robot state, since false positives in commands are always bad
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