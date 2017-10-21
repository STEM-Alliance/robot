package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.LEDSignal;
import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;
import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Panel.COLOR;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.BUTTON;
import org.wfrobotics.reuse.controller.Xbox.DPAD;
import org.wfrobotics.reuse.utilities.Utilities;
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
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps controllers to Commands **/
public class IO
{
    private final static double CLIMB_DEADBAND = .2;

    static Xbox xboxDriver = new Xbox(0);
    static Joystick joystickDrive = new Joystick(0);
    static Xbox xboxOperator = new Xbox(1);
    static Panel panel = new Panel(2);


    public final TankOI tankOI = new TankXbox(xboxDriver);
    public final ArcadeOI arcadeOI = new ArcadeXbox(xboxDriver);
    public final MecanumOI mecanumOI = new MecanumXBox(xboxDriver);
    public final SwerveOI swerveOI = new SwerveXBox(xboxDriver, xboxOperator, panel);


    Button buttonPanelWhiteTop = ButtonFactory.makeButton(panel, Panel.BUTTON.WHITE_T, TRIGGER.WHILE_HELD, new Up(Up.MODE.CLIMB));

    Button buttonManLB = ButtonFactory.makeButton(xboxOperator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Conveyor(Conveyor.MODE.UNJAM));
    Button buttonManRB = ButtonFactory.makeButton(xboxOperator, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new Conveyor(Conveyor.MODE.ON_HOLD));

    Button buttonPanelYellowTop = ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveSwerve(DriveSwerve.MODE.STOP));
    Button buttonPanelYellowBottom = ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_B, TRIGGER.WHEN_PRESSED, new VisionGearDropAndBackup());
    Button buttonPanelGreenTop = ButtonFactory.makeButton(panel, Panel.BUTTON.GREEN_T, TRIGGER.WHILE_HELD, new Rev(Rev.MODE.SHOOT));

    Button buttonManualLiftDown = ButtonFactory.makeButton(xboxOperator, Hand.kRight, .25, TRIGGER.WHILE_HELD, new Lift(Lift.MODE.DOWN));

    // used for manual intake DO NOT USE OTHERWISE
    Button buttonPanelBlackBottom = ButtonFactory.makeButton(panel, Panel.BUTTON.BLACK_B, TRIGGER.WHEN_PRESSED, new TurnToInViewTarget(.1));

    public static Button buttonManDpadUp = ButtonFactory.makeButton(xboxOperator, DPAD.UP, TRIGGER.WHILE_HELD, new Lift(Lift.MODE.UP));
    public static Button buttonManJoystickL = ButtonFactory.makeButton(xboxOperator, BUTTON.LEFT_STICK, TRIGGER.WHEN_PRESSED, new LEDSignal(3));

    public static double getClimbSpeedUp()
    {
        double raw = xboxOperator.getTrigger(Hand.kLeft);
        double adjusted = 0;

        if(raw > CLIMB_DEADBAND)
        {
            adjusted = Utilities.scaleToRange(raw, CLIMB_DEADBAND, 1, 0, 1);
        }

        return adjusted;
    }

    public static double getAugerSpeedAdjust()
    {
        return xboxOperator.getTrigger(Hand.kLeft);
    }

    public static boolean getIntake()
    {
        return xboxOperator.getButtonPressed(BUTTON.Y) || panel.getButton(Panel.BUTTON.BLACK_T);
    }

    public static boolean getZeroGyro()
    {
        return IO.xboxDriver.getButtonPressed(BUTTON.START);
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

    public static void setLiftRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        IO.xboxOperator.setRumble(Hand.kLeft, state);
        IO.xboxOperator.setRumble(Hand.kRight, state);
        IO.xboxDriver.setRumble(Hand.kLeft, state);
        IO.xboxDriver.setRumble(Hand.kRight, state);
    }
}