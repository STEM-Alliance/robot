package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup 
{
    final static double HEXAGON_ANGLE = 30;  // All corners are 120 on the interior, therefore the sides we want are 30 degrees past straight ahead

    private DetectGear camera;
    private AutoDrive drive;
    private PIDController pidX;
    private double heading = -1;

    private boolean done;

    public VisionGearDropOff() 
    {
        pidX = new PIDController(.8, 0.025, 0.0001, .5);
        camera = new DetectGear(DetectGear.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, -1, 999);

        addParallel(camera);
        addSequential(drive);
    }

    protected void initialize()
    {
        Robot.leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        done = false;
    }

    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueY = 0;
        double valueX = 0;

        if(camera.getIsFound())
        {
            // we think we've found at least one target

            // so get an estimate speed to line us up
            valueX = pidX.update(distanceFromCenter);

            // then if we're somewhat lined up
            if(Math.abs(distanceFromCenter) < .25)
            {
                // start approaching slowly
                valueY = -Utilities.scaleToRange(Math.abs(distanceFromCenter), 0, .4, -.55, -.2);
            }

            if(Math.abs(distanceFromCenter) < .1)
            {
                // if we started really far away from center, 
                // this will then reduce that overshoot
                // maybe
                pidX.resetError();
            }

            // we can still see a target
            if(visionWidth > 15)
            {
                Vector vector = new Vector(valueX, valueY);

                //TODO this only works for the front facing spring
                // address field heading here
                drive.set(vector, 0, -1);
            }
            else
            {
                // if the detected target width is less than 15, 
                // we're either at the target
                // or something went wrong
                done = true;
            }
        }
        else
        {
            done = true;
        }

        Utilities.PrintCommand("VisionGearDetect", this, camera.getIsFound() + "");
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putNumber("VisionGearY", valueY);
        SmartDashboard.putNumber("VisionGearX", valueX);
        SmartDashboard.putNumber("VisionWidth", visionWidth);
        SmartDashboard.putBoolean("GearFound", camera.getIsFound());
    }

    protected boolean isFinished() 
    {
        return done;
    }

    protected void end()
    {

    }

    protected void interrupted()
    {
        end();
    }

    double getRotationalSpeed()
    {
        double headingError = heading - Robot.driveSubsystem.getLastHeading();
        double speed = 0;

        if (Math.abs(headingError) > 5)  // 
        {
            double signR = Math.signum(headingError);
            double speedR = (heading == 0) ? 0 : .2;

            speed = signR * speedR;
        }

        return speed;
    }
    
    /**
     * Assume we are parallel to the spring (no rotation)
     * @param inView
     * @param distanceFromCenter
     * @param visionWidth
     * @return
     */
    private boolean SimpleExecute(boolean inView, double distanceFromCenter, double visionWidth)
    {
        double errorBadX = .1;
        double errorBadY = 15;
        double PIDXInput = 0;
        double PIDXOutput;
        double speedX;
        double speedY = 0;
        boolean done = false;
        
        if (!inView)
        {
            done = true;  // Never in view. We can't do this :(
        }
        else if(Math.abs(distanceFromCenter) > errorBadX)  // Fix X this round
        {
            PIDXInput = pidX.update(distanceFromCenter);
        }
        else if (visionWidth < errorBadY)  // Fix Y this round
        {
            speedY = -Utilities.scaleToRange(Math.abs(distanceFromCenter), 0, .4, -.55, -.2);
        }
        else
        {
            done = true;  // We lost the target. We probably scored!
        }
        
        PIDXOutput = pidX.update(PIDXInput);
        speedX = (PIDXInput == 0) ? 0 : PIDXOutput;
        
        drive.set(speedX, speedY, 0, -1);
        
        return done;
    }
}
