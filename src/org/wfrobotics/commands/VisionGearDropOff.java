package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearDropOff extends CommandGroup 
{
    final static double HEXAGON_ANGLE = 30;  // All corners are 120 on the interior, therefore the sides we want are 30 degrees past straight ahead
    public enum STATE { START, APPROACH, CLOSE, WAIT, BACK, END};
    
    private STATE state;
    
    private GearDetection camera;
    private AutoDrive drive;
    private PIDController pidX;
    private double heading = -1;

    private double stateTime;

    private boolean startingFieldRelative;
    
    public VisionGearDropOff() 
    {
        pidX = new PIDController(.8, 0.025, 0.0001, .5);
        camera = new GearDetection(GearDetection.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, -1, 999);
        
        addParallel(camera);
        addSequential(drive);
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
        state = STATE.START;
        
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
                if(camera.getIsFound())
                {
                    state = STATE.APPROACH;
                    stateTime = Timer.getFPGATimestamp();
                }
                else
                {
                    state = STATE.END;
                }
               break;
            case APPROACH:
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
                        pidX.resetError();  // DRL I'd remove this. This makes tuning your PID exponentially harder and if tuned correctly you wont oscillate much anyway. You can't remove all oscillation.
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
                        state = STATE.WAIT;
                    }
                    stateTime = Timer.getFPGATimestamp();
                }
                else
                {
                    state = STATE.END;
                }
                break;
                
            case CLOSE:
                if(camera.getIsFound())
                {
                    state = STATE.APPROACH;
                }
                else
                {
                    // we lost a camera, so give it some time before we cancel everything
                    if(Timer.getFPGATimestamp() - stateTime > 2)
                    {
                        state = STATE.END;
                    }
                }
                break;
                
                
            case WAIT:
                // time to pull the spring up?
                if(Timer.getFPGATimestamp() - stateTime > 1)
                {
                    state = STATE.BACK;
                }
                break;
                
            case BACK:
                // backup for a second
                //TODO account for Heading
                drive.set(0, -.5, 0, -1);
                
                if(Timer.getFPGATimestamp() - stateTime > 1)
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
}
