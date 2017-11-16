package org.wfrobotics.robot;

import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.Drive;
import org.wfrobotics.robot.config.VisionMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Up-to-date info about Robot, favor over coupling to raw subsystem state in Commands **/
public class RobotState
{
    // ------------- BEGIN Public State (Read-Only) -------------

    public double robotHeading;                        // Angle of robot relative to when gyro was last zeroed
    public boolean robotGear;                          // True: High, False: Low

    public double visionError;                         // Location of target relative to center of camera
    public boolean visionInView;                       // If vision determined the criteria for seeing the target(s) is met
    public VisionMode visionMode;                      // What vision co-processor is using it's camera(s) for
    public double visionWidth;                         // How big is the target(s), and therefore how close is it

    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    public synchronized HerdVector getRobotVelocity()  // Speed and direction robot is driving  // TODO clarify FR or RR, which is ideal?
    {
        return new HerdVector(robotVelocity);
    }

    // ------------- END Public State (Read-Only) -------------
    // ------------- BEGIN Private -------------

    public enum SHIFT_STATE
    {
        HIGH_GEAR,
        LOW_GEAR,
    }

    private static RobotState instance = null;
    private HerdLogger log = new HerdLogger(Robot.class);

    private HerdVector robotVelocity;

    protected RobotState()
    {
        robotGear = Drive.SHIFTER_INITIAL_STATE;
        robotHeading = 0;
        robotVelocity = new HerdVector(0, 0);
        resetVisionState();
    }

    public void logState()
    {
        log.info("Heading", prettyHeading());
        log.info("High Gear", robotGear);
    }

    // ------------- END Private -------------
    // ------------- BEGIN State Producers Only -------------

    public void updateRobotGear(boolean isHighGear)
    {
        robotGear = isHighGear;
    }

    public void updateRobotHeading(double fieldRelativeHeading)
    {
        robotHeading = fieldRelativeHeading;
    }

    public void updateRobotVelocity(HerdVector velocity)
    {
        robotVelocity = new HerdVector(velocity);
    }

    public void addVisionUpdate(VisionMessageTargets v)
    {
        if ( !v.Targets.isEmpty())
        {
        SmartDashboard.putNumber("Target Hight", v.Targets.get(0).height);
        SmartDashboard.putNumber("Target Width", v.Targets.get(0).width);
        
        }
        
        // TODO
//        if (v.mode != visionMode.getValue())
//        {
//            resetVisionState();
//        }
//
//        if (v.mode == VisionMode.SHOOTER.getValue())
//        {
//            processShooterUpdate(v);
//        }
//        else if (v.mode == VisionMode.GEAR.getValue())
//        {
//            processGearUpdate(v);
//        }
    }

    private synchronized void resetVisionState()
    {
        visionInView = false;
        visionError = 1;
        visionWidth = 0;
        visionMode = VisionMode.OFF;
    }

    // TODO confirm target a few times before sensing? Want here since all commands dislike false positives
    private synchronized void processShooterUpdate(VisionMessageTargets v)
    {
        boolean targetsInView = v.Targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter
        double newWidth = 0;  // TODO

        visionInView = targetsInView;
        visionError = newError;
        visionWidth = newWidth;
        visionMode = VisionMode.SHOOTER;
    }

    private synchronized void processGearUpdate(VisionMessageTargets v)
    {
        boolean targetsInView = v.Targets.size() > 1;
        double newError = 0;  // TODO calc this specific to shooter
        double newWidth = 0;  // TODO

        visionInView = targetsInView;
        visionError = newError;  // TODO calc this specific to shooter
        visionWidth = newWidth;  // TODO
        visionMode = VisionMode.GEAR;
    }

    // ------------- END State Producers Only -------------

    private String prettyHeading()
    {
        return String.format("%.1f\u00b0", robotHeading);
    }
}