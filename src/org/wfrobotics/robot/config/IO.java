package org.wfrobotics.robot.config;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.commands.LEDSignal;
import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;
import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Panel.COLOR;
import org.wfrobotics.reuse.controller.PanelButton;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.XboxButton;
import org.wfrobotics.reuse.controller.XboxDpadButton;
import org.wfrobotics.reuse.controller.XboxJoystickButton;
import org.wfrobotics.reuse.controller.XboxTriggerButton;
import org.wfrobotics.robot.auto.VisionGearDropAndBackup;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.Lift;
import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.commands.Up;
import org.wfrobotics.robot.driveoi.Arcade.ArcadeOI;
import org.wfrobotics.robot.driveoi.Arcade.ArcadeXbox;
import org.wfrobotics.robot.driveoi.Mecanum.MecanumOI;
import org.wfrobotics.robot.driveoi.Mecanum.MecanumXBox;
import org.wfrobotics.robot.driveoi.Swerve.SwerveOI;
import org.wfrobotics.robot.driveoi.Swerve.SwerveXBox;
import org.wfrobotics.robot.driveoi.Tank.TankOI;
import org.wfrobotics.robot.driveoi.Tank.TankXbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;

// TODO consider list/add()

/** Maps controllers to Commands **/
public class IO
{
    private final static double CLIMB_DEADBAND = .2;

    public static Xbox xboxDrive = new Xbox(0);
    public static Joystick joystickDrive = new Joystick(0);
    public static Xbox xboxMan = new Xbox(1);
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

    Button buttonManualLiftDown = new XboxTriggerButton(xboxMan, Hand.kRight, .25);
    Button buttonManualLiftUp = new XboxButton(xboxMan, Xbox.BUTTON.B);

    // used for manual intake DO NOT USE OTHERWISE
    public static Button buttonPanelBlackTop = new PanelButton(panel, Panel.BUTTON.BLACK_T);
    Button buttonPanelBlackBottom = new PanelButton(panel, Panel.BUTTON.BLACK_B);

    public static Button buttonManX= new XboxButton (xboxMan, Xbox.BUTTON.X);
    public static Button buttonManY= new XboxButton (xboxMan, Xbox.BUTTON.Y);

    public static Button buttonManDpadUp = new XboxDpadButton(xboxMan, 0);
    public static Button buttonManJoystickL = new XboxJoystickButton(xboxMan, Hand.kLeft);

    //  Button buttonLEDTest = new XboxButton(xboxDrive, Xbox.BUTTON.LB);
    //  Button buttonIntakeLeftStart = new XboxButton(xboxDrive, Xbox.BUTTON.X);
    //  Button buttonIntakeRightStart = new XboxButton(xboxDrive, Xbox.BUTTON.Y);


    public IO()
    {
        buttonManDpadUp.whileHeld(new Lift(Lift.MODE.UP));
        buttonManualLiftDown.whileHeld(new Lift(Lift.MODE.DOWN));
        buttonManJoystickL.whenPressed(new LEDSignal(3));

        //        buttonDriveLB.whenPressed(new DriveConfig(DriveConfig.MODE.HIGH_GEAR));
        //        buttonDriveBack.whenPressed(new DriveConfig(DriveConfig.MODE.FIELD_RELATIVE));
        //        buttonDriveStart.whenPressed(new DriveConfig(DriveConfig.MODE.GYRO_ZERO));
        buttonPanelYellowBottom.whenPressed(new VisionGearDropAndBackup());
        buttonPanelBlackBottom.whenPressed(new TurnToInViewTarget(.1));
        //buttonPanelYellowBottom.toggleWhenPressed(new GearDetection(GearDetection.MODE.GETDATA));
        //buttonPanelBlackBottom.toggleWhenPressed(new ShooterDetection(ShooterDetection.MODE.GETDATA));

        buttonPanelYellowTop.toggleWhenPressed(new DriveSwerve(DriveSwerve.MODE.STOP));

        //        buttonDriveA.whileHeld(new Rev(Rev.MODE.SHOOT));
        //        buttonDriveB.whileHeld(new Shoot(Conveyor.MODE.CONTINUOUS));
        //buttonDriveA.toggleWhenPressed(new Conveyor(Conveyor.MODE.OFF));

        buttonManRB.whileHeld(new Conveyor(Conveyor.MODE.ON_HOLD));
        buttonManLB.whileHeld(new Conveyor(Conveyor.MODE.UNJAM));

        buttonPanelGreenTop.whileHeld(new Rev(Rev.MODE.SHOOT));
        //buttonPanelGreenBottom.whenPressed(new LED(Led.HARDWARE.SIDE, LED.MODE.BLINK, 5));

        //buttonPanelBlackBottom.whenPressed(new LED(Led.HARDWARE.SIDE, LED.MODE.BLINK, 5));
        //buttonPanelBlackTop.whileHeld(new IntakeSetup(true));
        buttonPanelWhiteTop.whileHeld(new Up(Up.MODE.CLIMB));
        //buttonPanelWhiteBottom.whileHeld(new Up(Up.MODE.VARIABLE_SPEED));

        //buttonDriveRB.toggleWhenPressed(new VisionShoot());

        //      buttonIntakeLeftStart.toggleWhenPressed(new IntakeSetup(false, true));
        //      buttonIntakeRightStart.toggleWhenPressed(new IntakeSetup(true, false));
        //      buttonLEDTest.toggleWhenPressed(new LED(HARDWARE.ALL, LED.MODE.BLINK));
    }

    public static double getClimbSpeedUp()
    {
        double raw = xboxMan.getTriggerAxis(Hand.kLeft);
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

    public static void setManRumble(XboxController.RumbleType type, float value)
    {
        xboxMan.setRumble(type, value);
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

