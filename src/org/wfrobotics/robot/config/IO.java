package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.drivebasic.DriveOff;
import org.wfrobotics.reuse.commands.drivebasic.TurnToInViewTarget;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.robot.commands.Elevate;
import org.wfrobotics.robot.commands.ElevatePID;
import org.wfrobotics.robot.commands.ElevateRendezvous;
import org.wfrobotics.robot.commands.IntakePull;
import org.wfrobotics.robot.commands.IntakePush;
import org.wfrobotics.robot.commands.IntakeSolenoid;
import org.wfrobotics.robot.driveoi.Arcade.ArcadeIO;
import org.wfrobotics.robot.driveoi.Mecanum.MecanumIO;
import org.wfrobotics.robot.driveoi.Swerve.SwerveIO;
import org.wfrobotics.robot.driveoi.Tank.TankIO;
import org.wfrobotics.robot.driveoi.Tank.TankXbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps controllers to Commands **/
public class IO
{
    private static IO instance = null;

    private final Xbox driver;
    private final Xbox operator;
    private final Panel panel;

    /* Only one of these should be instantiated at a time */
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
        tankIO = new TankXbox(driver);

        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveOff()));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.BLACK_B, TRIGGER.WHEN_PRESSED, new TurnToInViewTarget(.1)));

        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush(.5)));
        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull(.5)));
        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new Elevate(-1)));
        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Elevate(1)));
        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.X, TRIGGER.TOGGLE_WHEN_PRESSED, new IntakeSolenoid(true)));

        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new ElevateRendezvous((5000), .5)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new ElevatePID((18000/2)/4096)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LEFT_STICK, TRIGGER.WHILE_HELD, new ElevatePID(0)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.RIGHT_STICK, TRIGGER.WHILE_HELD, new ElevatePID(18000/4096)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull()));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Elevate(-1)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new Elevate(1)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.TOGGLE_WHEN_PRESSED, new IntakeSolenoid(true)));

    }

    public static IO getInstance()
    {
        if (instance == null) { instance = new IO(new Xbox(0), new Xbox(1), new Panel(2)); }
        return instance;
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