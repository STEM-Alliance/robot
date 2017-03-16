package org.wfrobotics.robot;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.Conveyor;
import org.wfrobotics.commands.DetectGear;
import org.wfrobotics.commands.LED;
import org.wfrobotics.commands.Lift;
import org.wfrobotics.commands.Rev;
import org.wfrobotics.commands.Shoot;
import org.wfrobotics.commands.DetectShooter;
import org.wfrobotics.commands.Up;
import org.wfrobotics.commands.VisionGearDropOff;
import org.wfrobotics.commands.VisionShoot;
import org.wfrobotics.commands.drive.DriveConfig;
import org.wfrobotics.commands.drive.DriveSwerve;
import org.wfrobotics.commands.drive.DriveSwerveCalibration;
import org.wfrobotics.controller.Panel;
import org.wfrobotics.controller.Panel.COLOR;
import org.wfrobotics.controller.PanelButton;
import org.wfrobotics.controller.Xbox;
import org.wfrobotics.controller.XboxButton;
import org.wfrobotics.robot.driveoi.Arcade.*;
import org.wfrobotics.robot.driveoi.Mecanum.*;
import org.wfrobotics.robot.driveoi.Swerve.*;
import org.wfrobotics.robot.driveoi.Tank.*;
import org.wfrobotics.subsystems.Led;
import org.wfrobotics.subsystems.Lifter;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{
    private final static double CLIMB_DEADBAND = .2;
    
    static Xbox xboxDrive = new Xbox(0);
    static Joystick joystickDrive = new Joystick(0);
    static Xbox xboxMan = new Xbox(1);
    public static Panel panel = new Panel(2);
    
    
    public final TankOI tankOI = new TankXbox(xboxDrive);
    public final ArcadeOI arcadeOI = new ArcadeXbox(xboxDrive);
    public final MecanumOI mecanumOI = new MecanumXBox(xboxDrive);
    public final SwerveOI swerveOI = new SwerveXBox(xboxDrive, xboxMan, panel);
    //public final SwerveOI swerveOI = new SwerveJoyStick(joystickDrive, xboxMan, panel);
    

//    Button buttonDriveLB = new XboxButton(xboxDrive, Xbox.BUTTON.LB);
//    Button buttonDriveBack = new XboxButton(xboxDrive, Xbox.BUTTON.BACK);
//    Button buttonDriveStart = new XboxButton(xboxDrive, Xbox.BUTTON.START);

    Button buttonPanelSwitchL = new PanelButton(panel, Panel.BUTTON.SWITCH_L);
    Button buttonPanelSwitchR = new PanelButton(panel, Panel.BUTTON.SWITCH_R);

    Button buttonPanelWhiteTop = new PanelButton(panel, Panel.BUTTON.WHITE_T);
    Button buttonPanelWhiteBottom = new PanelButton(panel, Panel.BUTTON.WHITE_B);
    

    Button buttonManLB = new XboxButton(xboxMan, Xbox.BUTTON.LB);
    Button buttonManRB = new XboxButton(xboxMan, Xbox.BUTTON.RB);
    
//    Button buttonDriveX = new XboxButton(xboxDrive, Xbox.BUTTON.X);
//    Button buttonDriveY = new XboxButton(xboxDrive, Xbox.BUTTON.Y);
//    Button buttonDriveB = new XboxButton(xboxDrive, Xbox.BUTTON.B);
//    Button buttonDriveA = new XboxButton(xboxDrive, Xbox.BUTTON.A);
//    Button buttonDriveRB = new XboxButton(xboxDrive, Xbox.BUTTON.RB);
    Button buttonPanelYellowTop = new PanelButton(panel, Panel.BUTTON.YELLOW_T);
    Button buttonPanelYellowBottom = new PanelButton(panel, Panel.BUTTON.YELLOW_B);
    Button buttonPanelGreenTop = new PanelButton(panel, Panel.BUTTON.GREEN_T);
    Button buttonPanelGreenBottom = new PanelButton(panel, Panel.BUTTON.GREEN_B);
    
    Button buttonManual = new XboxButton(xboxMan, Xbox.BUTTON.A);
    
    // used for manual intake DO NOT USE OTHERWISE
    public static Button buttonPanelBlackTop = new PanelButton(panel, Panel.BUTTON.BLACK_T);
    public static Button buttonPanelBlackBottom = new PanelButton(panel, Panel.BUTTON.BLACK_B);
    public static Button buttonManX= new XboxButton (xboxMan, Xbox.BUTTON.X);
    public static Button buttonManY= new XboxButton (xboxMan, Xbox.BUTTON.Y);
    
//  Button buttonLEDTest = new XboxButton(xboxDrive, Xbox.BUTTON.LB);
//  Button buttonIntakeLeftStart = new XboxButton(xboxDrive, Xbox.BUTTON.X);
//  Button buttonIntakeRightStart = new XboxButton(xboxDrive, Xbox.BUTTON.Y);

    
    public OI()
    {        
        
        buttonManual.whenPressed(new Lift(true));;
        
//        buttonDriveLB.whenPressed(new DriveConfig(DriveConfig.MODE.HIGH_GEAR));
//        buttonDriveBack.whenPressed(new DriveConfig(DriveConfig.MODE.FIELD_RELATIVE));
//        buttonDriveStart.whenPressed(new DriveConfig(DriveConfig.MODE.GYRO_ZERO));
        buttonPanelYellowBottom.toggleWhenPressed(new VisionGearDropOff());
        buttonPanelBlackBottom.toggleWhenPressed(new VisionShoot());
        //buttonPanelBlackBottom.whenPressed(new LED(Led.HARDWARE.SIDE, LED.MODE.BLINK, 5));
        //buttonPanelYellowBottom.toggleWhenPressed(new GearDetection(GearDetection.MODE.GETDATA));
        //buttonPanelBlackBottom.toggleWhenPressed(new ShooterDetection(ShooterDetection.MODE.GETDATA));
        
        buttonPanelYellowTop.toggleWhenPressed(new DriveSwerve(DriveSwerve.MODE.STOP));
        
        buttonPanelSwitchL.whileHeld(new DriveSwerveCalibration(DriveSwerveCalibration.MODE.PANEL));
        buttonPanelSwitchR.whileHeld(new DriveSwerveCalibration(DriveSwerveCalibration.MODE.PANEL));

//        buttonDriveA.whileHeld(new Rev(Rev.MODE.SHOOT));
//        buttonDriveB.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
        //buttonDriveA.toggleWhenPressed(new Conveyor(Conveyor.MODE.OFF));
        
        buttonManRB.whileHeld(new Conveyor(Conveyor.MODE.ON_HOLD));
        buttonManLB.whileHeld(new Conveyor(Conveyor.MODE.UNJAM));
        buttonPanelGreenTop.whileHeld(new Conveyor(Conveyor.MODE.ON_HOLD));
        buttonPanelGreenBottom.whileHeld(new Conveyor(Conveyor.MODE.UNJAM));
        
        //buttonPanelBlackBottom.whenPressed(new LED(Led.HARDWARE.SIDE, LED.MODE.BLINK, 5));
        
        buttonPanelWhiteTop.whileHeld(new Up(Up.MODE.CLIMB));
        buttonPanelWhiteBottom.whileHeld(new Up(Up.MODE.VARIABLE_SPEED));
        
        //buttonDriveRB.toggleWhenPressed(new VisionShoot());
        
//      buttonIntakeLeftStart.toggleWhenPressed(new IntakeSetup(false, true));
//      buttonIntakeRightStart.toggleWhenPressed(new IntakeSetup(true, false));
//      buttonLEDTest.toggleWhenPressed(new LED(HARDWARE.ALL, LED.MODE.BLINK));
    }
    
    public static double getClimbSpeedUp()
    {
        double raw = xboxMan.getTriggerAxis(Hand.kRight);
        double adjusted = 0;
        
        if(raw > CLIMB_DEADBAND)
        {
            adjusted = Utilities.scaleToRange(raw, CLIMB_DEADBAND, 1, 0, 1);
        }
        
        return adjusted;
    }

    public static double getAugerSpeedAdjust()
    {   
        return xboxMan.getTriggerAxis(Hand.kLeft);
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

