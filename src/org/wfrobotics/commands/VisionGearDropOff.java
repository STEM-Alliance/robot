package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup 
{     
    private GearDetection camera;
    private AutoDrive drive;
    private PIDController pid;
    private double heading;
    
    public VisionGearDropOff() 
    {
        pid = new PIDController(1.6, 0.0015, 0.0001, .5);
        camera = new GearDetection(GearDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, 0, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void initialize()
    {
        double robotHeading = Robot.driveSubsystem.getLastHeading();
        
        if (robotHeading < 67.5 && robotHeading > 22.5)  
        {
            heading = 45;  // Snap to left spring
        }
        else if (robotHeading > -67.5 && robotHeading < -22.5)  
        {
            heading = -45;  // Snap to right spring
        }
        else
        {
            heading = 0;
        }
    }
    
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double valueY = 0;
        double valueX = 0;
        
        if(camera.getIsFound())
        {
            valueY = pid.update(distanceFromCenter);
        }
        
        if(Math.abs(distanceFromCenter) < .15)
        {
            valueX = -.4;
        }
        
        //drive.set(valueX, valueY, getRotationalSpeed, heading);  // TODO update AutoDrive after we add that ability to autodrive
        drive.set(valueX, valueY, 0, -1);  // TODO update AutoDrive after we add that ability to autodrive
    }
    
    private double getRotationalSpeed()
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
}
