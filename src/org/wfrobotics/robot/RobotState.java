package org.wfrobotics.robot;

import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.RobotStateBase;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.DriverStation;
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
    }

    protected synchronized void resetRobotSpecificState()
    {
        robotHasCube = false;
        intakeDistance = 9999;
        liftHeightInches = 0;
        hasCubeCounts = 0;
    }

    public synchronized void addVisionUpdate(VisionMessageTargets v)
    {
        if (v.source != visionMode.getTarget())
        {
            resetVisionState();
        }

        DriverStation.reportWarning("RobotState not configured to receive and parse vision updates right now", false);
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