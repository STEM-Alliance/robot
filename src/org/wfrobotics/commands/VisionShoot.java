package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionShoot extends CommandGroup 
{    
    private DetectShooter camera;
    private AutoDrive rotate;
    private PIDController pidRotate;

    private boolean done;
    
    public VisionShoot() 
    {
        pidRotate = new PIDController(.8, 0.025, 0.0001, .5);
        camera = new DetectShooter(DetectShooter.MODE.GETDATA);
        rotate = new AutoDrive(0, -1, 999); //TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
        
        addParallel(camera);
        addSequential(rotate);
    }
    
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueAngle = 0;

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

            // we can still see a target
            if(visionWidth > 15)
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
}

