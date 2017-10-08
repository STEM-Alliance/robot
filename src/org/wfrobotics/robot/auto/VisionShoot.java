package org.wfrobotics.robot.auto;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.commands.drive.swerve.AutoTurn;
import org.wfrobotics.reuse.commands.vision.VisionDetect;
import org.wfrobotics.reuse.commands.vision.VisionDisable;
import org.wfrobotics.reuse.commands.vision.VisionEnable;
import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionShoot extends CommandGroup
{
    private VisionDetect camera;
    private AutoTurn rotate;
    private PIDController pidRotate;

    private boolean done;
    private double startTime = 0;
    private double atAngleTime = 0;

    public VisionShoot()
    {
        pidRotate = new PIDController(2, 0.0002, 0.0001, .4);
        camera = new VisionDetect(Robot.targetShooterSubsystem);
        rotate = new AutoTurn(0);

        addSequential(new VisionEnable(Robot.targetShooterSubsystem));
        addParallel(camera);
        addSequential(rotate);
        addSequential(new VisionDisable(Robot.targetShooterSubsystem));
    }

    protected void initialize()
    {
        done = false;
        startTime = timeSinceInitialized();
        atAngleTime = timeSinceInitialized();
        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        pidRotate.resetError();
    }

    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();

        if (!isCameraReady())
        {
            rotate.set(new Vector(0, 0), 0, -1);
        }
        else
        {
            doVisionDrive();
        }

        Utilities.PrintCommand("VisionShoot", this, Utilities.round(distanceFromCenter,2)
                + " " + rotate.isRunning()
                + " " + done + " " + isCameraReady());
    }

    protected boolean isFinished()
    {
        return done;
    }

    protected void end()
    {
        //camera.endEarly();
        rotate.endEarly();
    }

    protected void interrupted()
    {
        end();
    }

    public boolean isCameraReady()
    {
        double now = timeSinceInitialized();
        boolean found = true;

        if (now - startTime < 1 || !camera.getIsEnabled())
        {
            found = false;

            if (now - startTime > 4)
            {
                done = true;
            }
        }
        else if (!camera.getIsFound())
        {
            found = false;
            done = true;
        }

        return found;
    }

    public void doVisionDrive()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueAngle = 0;
        double setPointAngle;

        // we think we've found at least one target
        SmartDashboard.putNumber("ShooterCenter", distanceFromCenter);

        // so get an estimate speed to line us up
        valueAngle = -pidRotate.update(distanceFromCenter);

        if(Math.abs(distanceFromCenter) < .1)
        {
            // if we started really far away from center, this will then reduce that overshoot maybe
            //pidRotate.resetError();
        }

        // try and see if we've been at the target for long enough
        if(Math.abs(distanceFromCenter) > .1)
        {
            atAngleTime = timeSinceInitialized();
        }
        else if(timeSinceInitialized() - atAngleTime > .5)
        {
            done = true;
        }

        // we can still see a target
        if(visionWidth > 15)
        {
            setPointAngle = valueAngle;
            //TODO this only works for the front facing spring address field heading here
        }
        else
        {
            // if the detected target width is less than 15, we're either at the target or something went wrong
            done = true;
            setPointAngle = 0;
        }

        SmartDashboard.putNumber("ShooterAngle", setPointAngle);
        rotate.set(new Vector(0, 0), setPointAngle, -1);
    }
}

