package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.LEDSignal;
import org.wfrobotics.reuse.commands.drive.swerve.DriveOff;
import org.wfrobotics.reuse.commands.drive.swerve.TurnToInViewTarget;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.AXIS;
import org.wfrobotics.reuse.controller.Xbox.BUTTON;
import org.wfrobotics.reuse.controller.Xbox.DPAD;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.auto.VisionGearDropAndBackup;
import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.Lift;
import org.wfrobotics.robot.commands.Rev;
import org.wfrobotics.robot.commands.Shoot;
import org.wfrobotics.robot.commands.Up;
import org.wfrobotics.robot.driveoi.Arcade.ArcadeIO;
import org.wfrobotics.robot.driveoi.Mecanum.MecanumIO;
import org.wfrobotics.robot.driveoi.Swerve.SwerveIO;
import org.wfrobotics.robot.driveoi.Swerve.SwerveXbox;
import org.wfrobotics.robot.driveoi.Tank.TankIO;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps controllers to Commands **/
public class IO
{
    private final static double CLIMB_DEADBAND = .2;
    private static IO instance = null;

    private final Xbox driver;
    private final Xbox operator;
    private final Panel panel;
    public TankIO tankIO;
    public ArcadeIO arcadeIO;
    public MecanumIO mecanumIO;
    public SwerveIO swerveIO;

    private ArrayList<Button> robotSpecific = new ArrayList<Button>();  // Keep buttons instantiated

    private IO(Xbox driver, Xbox operator, Panel panel)
    {
        this.driver = driver;
        this.operator = operator;
        this.panel = panel;
        swerveIO = new SwerveXbox(driver, operator);

        robotSpecific.add(ButtonFactory.makeButton(driver, BUTTON.B, TRIGGER.WHILE_HELD, new Shoot(Conveyor.MODE.CONTINUOUS)));
        robotSpecific.add(ButtonFactory.makeButton(driver, BUTTON.A, TRIGGER.WHILE_HELD, new Rev(Rev.MODE.SHOOT)));

        robotSpecific.add(ButtonFactory.makeButton(operator, BUTTON.LB, TRIGGER.WHILE_HELD, new Conveyor(Conveyor.MODE.UNJAM)));
        robotSpecific.add(ButtonFactory.makeButton(operator, BUTTON.RB, TRIGGER.WHILE_HELD, new Conveyor(Conveyor.MODE.ON_HOLD)));
        robotSpecific.add(ButtonFactory.makeButton(operator, DPAD.UP, TRIGGER.WHILE_HELD, new Lift(Lift.MODE.UP)));
        robotSpecific.add(ButtonFactory.makeButton(operator, AXIS.RIGHT_TRIGGER, .25, TRIGGER.WHILE_HELD, new Lift(Lift.MODE.DOWN)));
        robotSpecific.add(ButtonFactory.makeButton(operator, BUTTON.LEFT_STICK, TRIGGER.WHEN_PRESSED, new LEDSignal(3)));

        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveOff()));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_B, TRIGGER.WHEN_PRESSED, new VisionGearDropAndBackup()));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.BLACK_B, TRIGGER.WHEN_PRESSED, new TurnToInViewTarget(.1)));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.WHITE_T, TRIGGER.WHILE_HELD, new Up(Up.MODE.CLIMB)));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.GREEN_T, TRIGGER.WHILE_HELD, new Rev(Rev.MODE.SHOOT)));
    }

    public static IO getInstance()
    {
        if (instance == null) { instance = new IO(new Xbox(0), new Xbox(1), new Panel(2)); }
        return instance;
    }

    public double getClimbSpeedUp()
    {
        double raw = operator.getTrigger(Hand.kLeft);
        double adjusted = 0;
        if(raw > CLIMB_DEADBAND)
        {
            adjusted = Utilities.scaleToRange(raw, CLIMB_DEADBAND, 1, 0, 1);
        }
        return adjusted;
    }

    public double getAugerSpeedAdjust()
    {
        return operator.getTrigger(Hand.kLeft);
    }

    public boolean getIntake()
    {
        return operator.getButtonPressed(BUTTON.Y) || panel.getButton(Panel.BUTTON.BLACK_T);
    }

    public int getAutonomousSide()
    {
        return panel.getRotary();
    }

    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
        driver.setRumble(Hand.kLeft, state);
        driver.setRumble(Hand.kRight, state);
    }
}