package org.wfrobotics.robot;

import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.controller.*;
import org.wfrobotics.controller.Xbox.RumbleType;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{
    static Xbox xboxDrive = new Xbox(0);
    static Xbox xboxMan = new Xbox(1);
    static Panel panel = new Panel(2);
    
    // drive controller

    Button buttonDriveBack = new XboxButton(xboxDrive, Xbox.ButtonType.kBack);
    Button buttonDriveStart = new XboxButton(xboxDrive, Xbox.ButtonType.kStart);
    
    // manipulator controller
    //Button buttonManBack = new XboxButton(xboxMan, Xbox.ButtonType.kBack);
    
    // panel
    //Button buttonPanelLeftWhite = new PanelButton(panel, Panel.ButtonType.kWhiteL);
    
    public OI() 
    {
        //buttonDriveStart.toggleWhenPressed(new DriveSwerveSingleWheelTest());
        //buttonDriveBack.toggleWhenPressed(new DriveSwerveWheelCalibration());
        //buttonDriveStart.toggleWhenPressed(new DriveTankArcade());
        //buttonDriveBack.toggleWhenPressed(new DriveTank(true));
                
        //////////////////////////
        
        //////////////////////////
    }
    
    public static class DriveTankOI
    {
        public static double getL()
        {
            return xboxDrive.getY(Hand.kLeft);
        }
        
        public static double getR()
        {
            return xboxDrive.getY(Hand.kRight);        
        }
        
        public static double getThrottleSpeedAdjust()
        {
            return 0.5 + .5 * xboxDrive.getTriggerVal(Hand.kLeft);
        }
    }
    
    public static class DriveArcadeOI
    {
        public static double getThrottle()
        {
            return xboxDrive.getY(Hand.kLeft);
        }
        
        public static double getTurn()
        {
            return xboxDrive.getX(Hand.kLeft);
        }
        
        public static double getThrottleSpeedAdjust()
        {
            return 0.5 + .5 * xboxDrive.getTriggerVal(Hand.kLeft);
        }
    }
    
    public static class DriveMecanumOI
    {
        public static double getY()
        {
            return xboxDrive.getY(Hand.kLeft);
        }

        public static double getX()
        {
            return xboxDrive.getX(Hand.kLeft);
        }
        
        public static double getRotation()
        {
            return xboxDrive.getX(Hand.kRight);
        }
    }
    
    
    public static class DriveSwerveOI
    {
        private static final double DEADBAND = 0.2;

        /**
         * Get the Rotation value of the joystick for Halo Drive
         * 
         * @return The Rotation value of the joystick.
         */
        public static double getHaloDrive_Rotation()
        {
            double value = 0;
        
            value = xboxDrive.getAxis(Xbox.AxisType.kRightX);
        
            if (Math.abs(value) < DEADBAND)
            {
                value = 0;
            }
            return value;
        }

        /**
         * Get the {@link Vector} (mag & angle) of the velocity joystick for Halo
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public static Vector getHaloDrive_Velocity()
        {
            Vector value = xboxDrive.getVector(Hand.kLeft);
        
            if (value.getMag() < DEADBAND)
            {
                value.setMag(0);
            }
        
            return value;
        }

        public static double getHaloDrive_Heading45()
        {
            return -1;
        }
        
        /**
         * Get the heading/angle in degrees for Angle Drive
         * 
         * @return The angle in degrees of the joystick.
         */
        public static double getAngleDrive_Heading()
        {
            double Angle = -1;
            
            if (xboxDrive.getMagnitude(Hand.kRight) > 0.65)
            {
                Angle = xboxDrive.getDirectionDegrees(Hand.kRight);
            }
        
            return Angle;
        }

        /**
         * Get the rotation for Angle Drive
         * 
         * @return The rotation rate in rad/s.
         */
        public static double getAngleDrive_Rotation()
        {
            double Rotation = 0;
            int dpad = getDpad();
            
            if (dpad == 90)
            {
                Rotation = .75;
            }
            else if (dpad == 270)
            {
                Rotation = -.75;
            }
            return Rotation;
        }

        /**
         * Get the {@link Vector} (mag & angle) of the velocity joystick for Angle
         * Drive
         * 
         * @return The vector of the joystick.
         */
        public static Vector getAngleDrive_Velocity()
        {
            Vector value = xboxDrive.getVector(Hand.kLeft);
        
            if (value.getMag() < DEADBAND)
            {
                value.setMag(0);
            }
            return value;
        }

        public static boolean getHighGearEnable()
        {
            return xboxDrive.getBumper(Hand.kRight);
        }
        
        public static double getCrawlSpeed()
        {
            return xboxDrive.getTriggerVal(Hand.kRight);
        }
        
        public static boolean getBrake()
        {
            return false;
        }
        
        public static int getDpad()
        {
            return xboxDrive.getPOV(0);
        }

        public static boolean getFieldRelative()
        {
            return false;
        }

        public static boolean getResetGyro()
        {
            return false;
        }

    }
    
    public static void setDriveRumble(RumbleType type, float value)
    {
        xboxDrive.setRumble(type, value);
    }

}

