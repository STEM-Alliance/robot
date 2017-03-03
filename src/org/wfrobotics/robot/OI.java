package org.wfrobotics.robot;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.*;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.controller.*;
import org.wfrobotics.controller.Panel.BUTTON;
import org.wfrobotics.controller.Panel.COLOR;
import org.wfrobotics.subsystems.Intake;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{
    static Xbox xboxDrive = new Xbox(0);
    static Xbox xboxMan = new Xbox(1);
    public static Panel panel = new Panel(2);
    

    Button buttonDriveLB = new XboxButton(xboxDrive, Xbox.BUTTON.LB);
    Button buttonDriveBack = new XboxButton(xboxDrive, Xbox.BUTTON.BACK);
    Button buttonDriveStart = new XboxButton(xboxDrive, Xbox.BUTTON.START);

    Button buttonPanelSwitchL = new PanelButton(panel, Panel.BUTTON.SWITCH_L);
    Button buttonPanelSwitchR = new PanelButton(panel, Panel.BUTTON.SWITCH_R);

    Button buttonPanelWhiteTop = new PanelButton(panel, Panel.BUTTON.WHITE_T);
    Button buttonPanelWhiteBottom = new PanelButton(panel, Panel.BUTTON.WHITE_B);
    

    Button buttonManLB = new XboxButton(xboxMan, Xbox.BUTTON.LB);
    Button buttonManRB = new XboxButton(xboxMan, Xbox.BUTTON.RB);
    
    Button buttonDriveX = new XboxButton(xboxDrive, Xbox.BUTTON.X);
    Button buttonDriveY = new XboxButton(xboxDrive, Xbox.BUTTON.Y);
    Button buttonDriveB = new XboxButton(xboxDrive, Xbox.BUTTON.B);
    Button buttonDriveA = new XboxButton(xboxDrive, Xbox.BUTTON.A);
    Button buttonDriveRB = new XboxButton(xboxDrive, Xbox.BUTTON.RB);
    Button buttonPanelYellowTop = new PanelButton(panel, Panel.BUTTON.YELLOW_T);
    Button buttonPanelYellowBottom = new PanelButton(panel, Panel.BUTTON.YELLOW_B);
    Button buttonPanelGreenTop = new PanelButton(panel, Panel.BUTTON.GREEN_T);
    Button buttonPanelGreenBottom = new PanelButton(panel, Panel.BUTTON.GREEN_B);
    
    // used for manual intake DO NOT USE OTHERWISE
    public static Button buttonPanelBlackTop = new PanelButton(panel, Panel.BUTTON.BLACK_B);
    public static Button buttonPanelBlackBottom = new PanelButton(panel, Panel.BUTTON.BLACK_T);
    public static Button buttonManX= new XboxButton (xboxMan, Xbox.BUTTON.X);
    public static Button buttonManY= new XboxButton (xboxMan, Xbox.BUTTON.Y);
    
//  Button buttonLEDTest = new XboxButton(xboxDrive, Xbox.BUTTON.LB);
//  Button buttonIntakeLeftStart = new XboxButton(xboxDrive, Xbox.BUTTON.X);
//  Button buttonIntakeRightStart = new XboxButton(xboxDrive, Xbox.BUTTON.Y);

    
    public OI()
    {        
        buttonDriveLB.whenPressed(new DriveConfig(DriveConfig.MODE.HIGH_GEAR));
        buttonDriveBack.whenPressed(new DriveConfig(DriveConfig.MODE.FIELD_RELATIVE));
        buttonDriveStart.whenPressed(new DriveConfig(DriveConfig.MODE.GYRO_ZERO));
        buttonPanelYellowBottom.toggleWhenPressed(new VisionGearDropOff());
        
        buttonPanelYellowTop.toggleWhenPressed(new DriveSwerve(DriveSwerve.MODE.STOP));
        
        buttonPanelSwitchL.whileHeld(new DriveSwerveCalibration(DriveSwerveCalibration.MODE.PANEL));
        buttonPanelSwitchR.whileHeld(new DriveSwerveCalibration(DriveSwerveCalibration.MODE.PANEL));

        buttonDriveA.whileHeld(new Rev(Rev.MODE.SHOOT));
        buttonDriveB.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
        //buttonDriveA.toggleWhenPressed(new Conveyor(Conveyor.MODE.OFF));

        buttonManRB.whileHeld(new Conveyor(Conveyor.MODE.ON_HOLD));
        buttonManLB.whileHeld(new Conveyor(Conveyor.MODE.UNJAM));
        buttonPanelGreenTop.whileHeld(new Conveyor(Conveyor.MODE.ON_HOLD));
        buttonPanelGreenBottom.whileHeld(new Conveyor(Conveyor.MODE.UNJAM));
        
        buttonPanelWhiteTop.whileHeld(new Up(Up.MODE.VARIABLE_SPEED));
        buttonPanelWhiteBottom.whileHeld(new Up(Up.MODE.OFF));
        
        //buttonDriveRB.toggleWhenPressed(new VisionShoot());
        
//      buttonIntakeLeftStart.toggleWhenPressed(new IntakeSetup(false, true));
//      buttonIntakeRightStart.toggleWhenPressed(new IntakeSetup(true, false));
//      buttonLEDTest.toggleWhenPressed(new LED(HARDWARE.ALL, LED.MODE.BLINK));
    }
    
    private static double AdjustClimb(double value)
    {
        if(value < .2)
        {
            value = 0;
        }
        else
        {
            value = Utilities.scaleToRange(value, .2, 1, 0, 1);
        }
        
        return value;
    }
    
    public static double getClimbSpeedUp()
    {
        return AdjustClimb(xboxMan.getTriggerAxis(Hand.kRight));
    }

    public static double getAugerSpeedAdjust()
    {   
        return xboxMan.getTriggerAxis(Hand.kLeft);
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
            return 0.5 + .5 * xboxDrive.getTriggerAxis(Hand.kLeft);
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
            return 0.5 + .5 * xboxDrive.getTriggerAxis(Hand.kLeft);
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
        
            value = xboxDrive.getAxis(Xbox.AXIS.RIGHT_X);
        
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

        public static double getCrawlSpeed()
        {
            return xboxDrive.getTriggerAxis(Hand.kLeft);
        }
        
        public static int getDpad()
        {
            return xboxDrive.getPOV(0);
        }
        
        public static double[] getPanelKnobs()
        {
            return new double[] {
                            panel.getTopDial(Hand.kLeft) * 180.0,
                            panel.getTopDial(Hand.kRight) * 180.0,
                            panel.getBottomDial(Hand.kLeft) * 180.0,
                            panel.getBottomDial(Hand.kRight) * 180.0, 
                    };
        }

        public static boolean getPanelSave()
        {
            return panel.getButton(BUTTON.SWITCH_L) && panel.getButton(BUTTON.SWITCH_R);
        }

        public static double getFusionDrive_Rotation()
        {
            return xboxMan.getX(Hand.kRight);
        }
    }
    
    public static void setDriveRumble(XboxController.RumbleType type, float value)
    {
        xboxDrive.setRumble(type, value);
    }

    public static void setPanelLEDs(Hand hand, COLOR LED1, COLOR LED2, COLOR LED3, COLOR LED4)
    {
        panel.setLEDs(hand, LED1, LED2, LED3, LED4);
    }
    
    public static void setPanelLEDs(COLOR ledsL[], COLOR ledsR[])
    {
        panel.setLEDs(Hand.kLeft, ledsL[0], ledsL[1], ledsL[2], ledsL[3]);
        panel.setLEDs(Hand.kRight, ledsR[0], ledsR[1], ledsR[2], ledsR[3]);
    }
}

