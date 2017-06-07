package org.wfrobotics.robot;

import org.wfrobotics.reuse.commands.drive.AutoDrive;
import org.wfrobotics.robot.config.Commands;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous
{
    public static final double START_ANGLE_SHOOT = 99;
    public static final double START_ANGLE_GEAR_ONLY = 90;
    
    public enum POSITION_ROTARY {SIDE_BOILER, CENTER, SIDE_LOADING_STATION};
    
    public enum AUTO_COMMAND
    {
        NONE,
        DRIVE,
        SHOOT, SHOOT_THEN_HOPPER, SHOOT_THEN_GEAR,
        GEAR_VISION, GEAR_DR, DRIVE_HG;
        
        public Command getCommand(POSITION_ROTARY startingPosition)
        {
            Command autonomousCommand;
            
            switch(this)
            {
           
            case NONE:
            default:
                autonomousCommand = new AutoDrive();
                break;
            }
            
            return autonomousCommand;
        }
        
        public double getGyroOffset(POSITION_ROTARY startingPosition)
        {
            int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1; // X driving based on alliance for mirrored field
            
            double startAngle;
            
            switch(this)
            {
            case SHOOT:
                startAngle = 180;
                break;
            case SHOOT_THEN_HOPPER:
            case SHOOT_THEN_GEAR:
                startAngle = signX * START_ANGLE_SHOOT;
                break;
            case GEAR_DR:
//                if(startingPosition == POSITION_ROTARY.SIDE_BOILER)
//                {
//                    startAngle = signX * START_ANGLE_SHOOT;                 
//                }
//                else
//                {
//                    startAngle = START_ANGLE_GEAR_ONLY;
//                }
//                break;
            case GEAR_VISION:
                startAngle = START_ANGLE_GEAR_ONLY;
                break;
            default:
                startAngle = 0;
                break;
            }
            
            return startAngle;
        }
    }
    
    /**
     * Saved the starting position on field for when we switch to autonomous mode
     * @return Starting position selected by panel rotary dial
     */
    public static POSITION_ROTARY getRotaryStartingPosition()
    {
        int dial = IO.panel.getRotary();
        Alliance alliance = DriverStation.getInstance().getAlliance();
        String displayValue;
        POSITION_ROTARY position;
        
        if (alliance == Alliance.Blue && dial == 7 ||
            alliance == Alliance.Red && dial == 1)
        {
            position = POSITION_ROTARY.SIDE_BOILER;
            displayValue = "BOILER";
        }
        else if (alliance == Alliance.Blue && dial == 1 ||
                alliance == Alliance.Red && dial == 7)
        {
            position = POSITION_ROTARY.SIDE_LOADING_STATION;
            displayValue = "LOADING STATION";
        }
        else if (dial == 0)
        {
            position = POSITION_ROTARY.CENTER;
            displayValue = "CENTER";
        }
        else
        {
            position = POSITION_ROTARY.CENTER;
            displayValue = "(defaulting to) CENTER";
        }
        SmartDashboard.putString("Autonomous Starting Position", displayValue);
        
        return position;
    }
}
