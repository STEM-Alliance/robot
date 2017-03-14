package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup 
{    
    private ShooterDetection camera;
    private AutoDrive rotate;
    private PIDController pidX;

    private boolean done;
    
    public VisionShoot() 
    {
        camera = new ShooterDetection(ShooterDetection.MODE.GETDATA);
        rotate = new AutoDrive(0, 0, 999); //TODO: make this rotate correctly
        pidX = new PIDController(.8, 0.025, 0.0001, .5);
        
        addParallel(camera);
        addSequential(rotate); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueY = 0;
        double valueAngle = 0;

        if(camera.getIsFound())
        {
            // we think we've found at least one target

            // so get an estimate speed to line us up
            valueAngle = pidX.update(distanceFromCenter);

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
                pidX.resetError();  // DRL I'd remove this. This makes tuning your PID exponentially harder and if tuned correctly you wont oscillate much anyway. You can't remove all oscillation.
            }

            // we can still see a target
            if(visionWidth > 15)
            {
                Vector vector = new Vector(0, valueY);

                //TODO this only works for the front facing spring
                // address field heading here
                rotate.set(vector, valueAngle, -1);
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
        // TODO: make This work as a shoot command, not as gear
    }
}

