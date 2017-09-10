package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDrive;
import org.wfrobotics.reuse.commands.drive.swerve.AutoOff;
import org.wfrobotics.robot.auto.AutoGear;
import org.wfrobotics.robot.auto.AutoShoot;

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
            case SHOOT:
                autonomousCommand = new AutoShoot(AutoShoot.MODE_DRIVE.DEAD_RECKONING_MIDPOINT, AutoShoot.MODE_SHOOT.DEAD_RECKONING);
                break;
            case SHOOT_THEN_HOPPER:
                autonomousCommand = new AutoShoot();
                break;
            case SHOOT_THEN_GEAR:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.VISION, true);
                break;
            case DRIVE:
                autonomousCommand = new AutoDrive(0,Commands.AUTONOMOUS_DRIVE_SPEED, 0, Commands.AUTONOMOUS_TIME_DRIVE_MODE);
                break;
            case DRIVE_HG:
                autonomousCommand = new AutoDrive(0,Commands.AUTONOMOUS_DRIVE_SPEED*.75, 0, Commands.AUTONOMOUS_TIME_DRIVE_MODE*.75);
                break;
            case GEAR_VISION:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.VISION, false);
                break;
            case GEAR_DR:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.DEAD_RECKONING, false);
                break;
            case NONE:
            default:
                autonomousCommand = new AutoOff();
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
