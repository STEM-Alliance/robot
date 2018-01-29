package org.wfrobotics.robot;

import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.Drive;
import org.wfrobotics.robot.config.VisionMode;

import edu.wpi.first.wpilibj.DriverStation;

/** Up-to-date info about Robot, favor over coupling to raw subsystem state in Commands **/
public class RobotState
{
    // ------------- BEGIN Public State (Read-Only) -------------

    public double robotDistanceDriven;  // Distance driven by robot since encoder distance last zeroed (inches)
    public double robotHeading;         // Angle of robot relative to when gyro was last zeroed
    public boolean robotGear;           // True: High, False: Low
    public HerdVector robotVelocity;    // Speed and direction robot is driving  // TODO clarify FR or RR, which is ideal?

    public double visionError;          // Location of target relative to center of camera
    public boolean visionInView;        // If vision determined the criteria for seeing the target(s) is met
    public VisionMode visionMode;       // What vision co-processor is using it's camera(s) for
    public double visionWidth;          // How big is the target(s), and therefore how close is it

    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    // ------------- END Public State (Read-Only) -------------

    // ------------- BEGIN Private -------------

    private static RobotState instance = null;
    private final HerdLogger log = new HerdLogger(Robot.class);

    protected RobotState()
    {
        robotDistanceDriven = 0;
        robotGear = Drive.SHIFTER_INITIAL_STATE;
        robotHeading = 0;
        robotVelocity = new HerdVector(0, 0);
        resetVisionState();
    }

    public void logState()
    {
        log.info("Heading", String.format("%.1f\u00b0", robotHeading));
        log.info("High Gear", robotGear);
    }

    // ------------- END Private -------------

    // ------------- BEGIN State Producers (Write-Only) -------------

    public synchronized void updateRobotDistanceDriven(double inchesDrivenTotal)
    {
        robotDistanceDriven = inchesDrivenTotal;
    }

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

    public synchronized void addVisionUpdate(VisionMessageTargets v)
    {
        if (v.source != visionMode.getTarget())
        {
            resetVisionState();
        }

        DriverStation.reportWarning("RobotState not configured for vision update specific parsing", false);
    }

    private synchronized void resetVisionState()
    {
        visionInView = false;
        visionError = 1;
        visionWidth = 0;
        visionMode = VisionMode.OFF;
    }

    public HerdVector getRobotVelocity()
    {
        return null;
    }

    // ------------- END State Producers (Write-Only) -------------
}