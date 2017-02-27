package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup 
{     
    final static double HEXAGON_ANGLE = 30;  // All corners are 120 on the interior, therefore the sides we want are 30 degrees past straight ahead
    private enum STATE {APPROACH, WAIT_FOR_HUMAN, BACKOUT};
    
    private GearDetection camera;
    private AutoDrive drive;
    private PIDController pid;
    private double heading;
    private STATE state;
    
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
        
        // Angle to snap to, surrounding each spring, generous on the sides. 
        // Ex: Spring at 30 degrees, technically range could be 15-45 degrees, allow 15-135 since this is the outside spring
        if (robotHeading < HEXAGON_ANGLE * 3 && robotHeading > HEXAGON_ANGLE * .5)
        {
            heading = HEXAGON_ANGLE;  // Snap to left spring
        }
        else if (robotHeading > -HEXAGON_ANGLE * 3 && robotHeading < -HEXAGON_ANGLE * .5)  
        {
            heading = -HEXAGON_ANGLE;  // Snap to right spring
        }
        else
        {
            heading = 0;
        }
        state = STATE.APPROACH;
    }
    
    protected void execute()
    {
        double valueX = 0;
        double valueY = 0;
        
        if (state == STATE.APPROACH)
        {
            double distanceFromCenter = camera.getDistanceFromCenter();
            
            if(camera.getIsFound())
            {
                valueY = pid.update(distanceFromCenter);
            }
            
            if(Math.abs(distanceFromCenter) < .15)
            {
                valueX = -.4;
            }
            
            // TODO apply rotational speed and heading to become parallel to spring
            // TODO If we get a SPRING_IN_GEAR sensor, convert this into a approach-wait-retry state machine
        }
        else if (state == STATE.WAIT_FOR_HUMAN)
        {
            // Not supported yet; we just do STATE.APPROACH
        }
        else if (state == STATE.BACKOUT)
        {
            // Not supported yet; we just do STATE.APPROACH
        }
        
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
