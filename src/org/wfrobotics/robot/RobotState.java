package org.wfrobotics.robot;

import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionTargetInfo;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Preferred provider of global, formatted state about the robot. Commands can get information from one place rather than from multiple subsystems. **/
public final class RobotState extends RobotStateBase
{
    private static final RobotState instance = new RobotState();
    private double hasCubeCounts;

    // Robot-specific state
    public boolean robotHasCube;
    public double intakeDistance;
    public double liftHeightInches;
    public double wristTicks;

    //vision specific updates
    // ToDo: Move all of these to the RobotStateBase
    public VisionMessageTargets latestUpdate;
    public VisionTargetInfo largestDetected;
    public int centerX;
    public int centerY;
    // boolean visionInView
    // double visionWidth
    // double visionError;

    public static RobotState getInstance()
    {
        return instance;
    }

    public void reportState()
    {
        super.reportState();
        SmartDashboard.putBoolean("Has Cube", robotHasCube);
        SmartDashboard.putNumber("Cube", intakeDistance);
        SmartDashboard.putNumber("Wrist Angle", wristTicks);
        SmartDashboard.putString("Vision MSG", latestUpdate.msg);
    }

    protected synchronized void resetRobotSpecificState()
    {
        robotHasCube = false;
        intakeDistance = 9999;
        liftHeightInches = 0;
        hasCubeCounts = 0;

        largestDetected = null;
    }

    public synchronized void addVisionUpdate(VisionMessageTargets v)
    {
        if (v.source != visionMode.getTarget())
        {
            resetVisionState();
        }
        else
        {
            latestUpdate = v;

            centerX = latestUpdate.imageWidth / 2;
            centerY = latestUpdate.imageHeight / 2;

            if (v.Targets.size() > 0)
            {
                visionInView = true;

                VisionTargetInfo largestTarget = null;
                for (VisionTargetInfo target : v.Targets)
                {
                    if ( target.area() >= largestTarget.area() || largestTarget == null)
                    {
                        largestTarget = target;
                    }
                }
                largestDetected = largestTarget;
                visionWidth = largestTarget.width;
                calcVisionError();
            }
        }
    }
    /**
     *
     * Ask or look up
     *  0) I should probibly split up the method above...or take out some fetures.... How so?
     *  1) If i acutely did this correctly (mag of the vector)
     *  2) How to turn this into a precentage error instead of the mag
     *  3) Test how the TurnUntilTargetInView works with this precentage
     *  4) Rewrite the aformentioned command to use a RobotState is centered (in tol)
     *  5) delete all of the unneccary things that was written
     *
     */
    public void calcVisionError()
    {
        visionError = largestDetected.getHerdVector().getMag();
    }
    // I don't think I did this in the most efficient way
    public boolean isCentered()
    {
        boolean centered = false;
        double vistol = 0.05;
        if ((centerX - largestDetected.center_x) < (vistol * visionWidth*2))
        {
            return true;
        }
        return centered;
    }
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
        wristTicks = ticks;
    }
}