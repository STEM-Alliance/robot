package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionShoot extends CommandGroup 
{    
    private DetectShooter camera;
    private AutoDrive rotate;
    private PIDController pidRotate;

    private boolean done;
    private double startTime = 0;
    
    public VisionShoot() 
    {
        pidRotate = new PIDController(3, 0.003, 0.000, .35);
        camera = new DetectShooter(DetectShooter.MODE.GETDATA);
        rotate = new AutoDrive(0, -1, 5); //TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
        
        addParallel(camera);
        addSequential(rotate);
    }
    
    protected void initialize()
    {
        done = false;
        
        startTime = Timer.getFPGATimestamp();
        Robot.leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
    }
    
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueAngle = 0;

        if(Timer.getFPGATimestamp() - startTime > 1 && camera.getIsEnabled())
        {
            if(camera.getIsFound())
            {
                // we think we've found at least one target
                SmartDashboard.putNumber("ShooterCenter", distanceFromCenter);
    
                // so get an estimate speed to line us up
                valueAngle = -pidRotate.update(distanceFromCenter);
    
                if(Math.abs(distanceFromCenter) < .1)
                {
                    // if we started really far away from center, 
                    // this will then reduce that overshoot
                    // maybe
                    //pidRotate.resetError();
                }
    
                // we can still see a target & we're not pointing at the target
                if(visionWidth > 15 && Math.abs(distanceFromCenter) > .05)
                {
                    SmartDashboard.putNumber("ShooterAngle", valueAngle);
                    //TODO this only works for the front facing spring
                    // address field heading here
                    rotate.set(new Vector(0, 0), valueAngle, -1);
                }
                else
                {
                    // if the detected target width is less than 15, 
                    // we're either at the target
                    // or something went wrong
                    done = true;
                    rotate.set(new Vector(0, 0), 0, -1);
                }
            }
            else
            {
                done = true;
            }
        }
        else if (Timer.getFPGATimestamp() - startTime > 4)
        {
            // timeout after 4 seconds of waiting for the camera to switch
            done = true;
        }
    }
    
    protected boolean isFinished() 
    {
        return done;
    }

    protected void end()
    {
        rotate.endEarly();
    }

    protected void interrupted()
    {
        end();
    }

}

