package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup 
{     
    public enum STATE { START, GO, BACK, END};
    
    private STATE state;
    
    private GearDetection camera;
    private AutoDrive drive;
    private PIDController pid;
    private double heading;

    private double backTime;

    private boolean startingFieldRelative;
    
    public VisionGearDropOff() 
    {
        pid = new PIDController(1.1, 0.015, 0.0001, .5);
        camera = new GearDetection(GearDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, -1, 999);
        
        addParallel(camera);
        addSequential(drive); // TODO Create a new constructor for updating, rather than one that does nothing with a big timeout
    }
    
    protected void initialize()
    {
//        double robotHeading = Robot.driveSubsystem.getLastHeading();
//        
//        if (robotHeading < 67.5 && robotHeading > 22.5)  
//        {
//            heading = 45;  // Snap to left spring
//        }
//        else if (robotHeading > -67.5 && robotHeading < -22.5)  
//        {
//            heading = -45;  // Snap to right spring
//        }
//        else
//        {
//            heading = 0;
//        }
        state = STATE.GO;
        
        //startingFieldRelative = Robot.driveSubsystem.getFieldRelative();
        //Robot.driveSubsystem.setFieldRelative(false);
    }
    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double visionWidth = camera.getFullWidth();
        double valueY = 0;
        double valueX = 0;
        
        Utilities.PrintCommand("VisionGearDetect", this, heading + "  " + state.toString());
        
        switch(state)
        {
            case START:
                drive.set(0, 0, 0, -1); 
                state = STATE.GO;
                break;
                
            case GO:
                if(camera.getIsFound())
                {
                    valueX = pid.update(distanceFromCenter);
                
                    if(Math.abs(distanceFromCenter) < .3)
                    {
                        valueY = -Utilities.scaleToRange(Math.abs(distanceFromCenter), 0, .3, -.55, -.2);
                        
                    }
                    if(Math.abs(distanceFromCenter) < .1)
                    {
                        pid.resetError();
                    }
                    
                    if(visionWidth > 15)
                    {
                        //drive.set(valueX, valueY, getRotationalSpeed, heading);  // TODO update AutoDrive after we add that ability to autodrive
                        drive.set(valueX, valueY, 0, -1);  // TODO update AutoDrive after we add that ability to autodrive
                    }
                    else
                    {
                        state = STATE.BACK;
                        backTime = Timer.getFPGATimestamp();
                    }
                }
                break;
                
            case BACK:
                drive.set(0, -.5, 0, -1);  // TODO update AutoDrive after we add that ability to autodrive
                
                if(Timer.getFPGATimestamp() - backTime > 1)
                {
                    state = STATE.END;
                }
                break;
                
            case END:
                break;
            
            default:
                break;
        }
        
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putNumber("VisionGearY", valueY);
        SmartDashboard.putNumber("VisionGearX", valueX);
        SmartDashboard.putNumber("VisionWidth", visionWidth);
        SmartDashboard.putBoolean("GearFound", camera.getIsFound());
    }

    protected boolean isFinished() 
    {
        return state == STATE.END;
        
    }
    
    protected void end()
    {
        //Robot.driveSubsystem.setFieldRelative(startingFieldRelative);
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
}
