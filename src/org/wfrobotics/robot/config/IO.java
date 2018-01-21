package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.drivebasic.DriveOff;
import org.wfrobotics.reuse.commands.drivebasic.TurnToInViewTarget;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Xbox;
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
        swerveIO = new SwerveXbox(driver, operator);

        //robotSpecific.add(ButtonFactory.makeButton(operator, BUTTON.LEFT_STICK, TRIGGER.WHEN_PRESSED, new LEDSignal(3)));

        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveOff()));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.BLACK_B, TRIGGER.WHEN_PRESSED, new TurnToInViewTarget(.1)));
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