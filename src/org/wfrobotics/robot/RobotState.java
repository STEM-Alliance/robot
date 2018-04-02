package org.wfrobotics.robot;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.VisionMode;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    public boolean robotHasCube;
    public double intakeDistance;
    public double liftHeightInches;
    public double wristAngle;
    public RobotConfig config;
    public static RobotState getInstance()
    {
        if (instance == null) { instance = new RobotState(); }
        return instance;
    }

    // ------------- END Public State (Read-Only) -------------

    // ------------- BEGIN Private -------------

    private static RobotState instance = null;
    private double hasCubeCounts;

    protected RobotState()
    {
        robotDistanceDriven = 0;
        robotGear = false;
        robotHeading = 0;
        robotVelocity = new HerdVector(0, 0);
        //        resetVisionState();

        robotHasCube = false;
        intakeDistance = 9999;
        liftHeightInches = 0;
        wristAngle = 0;
        hasCubeCounts = 0;
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Heading", robotHeading);
        //        SmartDashboard.putBoolean("High Gear", robotGear);

        SmartDashboard.putNumber("Wrist Angle", wristAngle);
    }

    // ------------- END Private -------------

    // ------------- BEGIN State Producers Robot-generic (Write-Only) -------------

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

    //    public synchronized void addVisionUpdate(VisionMessageTargets v)
    //    {
    //        if (v.source != visionMode.getTarget())
    //        {
    //            resetVisionState();
    //        }
    //
    //        DriverStation.reportWarning("RobotState not configured for vision update specific parsing", false);
    //    }

    //    private synchronized void resetVisionState()
    //    {
    //        visionInView = false;
    //        visionError = 1;
    //        visionWidth = 0;
    //        visionMode = VisionMode.OFF;
    //    }

    // ------------- END State Producers Robot-generic (Write-Only) -------------

    // ------------- Begin State Producers Robot-specific (Write-Only) -------------

    double timeSinceRumbleOn;
    public synchronized void updateIntakeSensor(double distance)
    {
        intakeDistance = distance;

        if (intakeDistance < 13)
        {
            hasCubeCounts++;
        }
        else if (hasCubeCounts <= 20)
        {
            timeSinceRumbleOn = Timer.getFPGATimestamp();
        }
        else
        {
            hasCubeCounts = 0;
        }
        robotHasCube = hasCubeCounts > 20;

        if(hasCubeCounts > 20)
        {
            if (Timer.getFPGATimestamp() - timeSinceRumbleOn < 1)
            {
                IO.getInstance().setRumble(true);
            }
            else
            {
                IO.getInstance().setRumble(false);
                timeSinceRumbleOn = 0;
            }
        }
        else
        {
            IO.getInstance().setRumble(false);
        }
    }

    public synchronized void updateLiftHeight(double inches)
    {
        liftHeightInches = inches;
    }

    public synchronized void updateWristPosition(double ticks)
    {
        wristAngle = ticks / 4096.0 * 90.0;
    }

    // ------------- END State Producers Robot-specific (Write-Only) -------------
}