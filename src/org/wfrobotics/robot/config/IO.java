package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.drivebasic.DriveOff;
import org.wfrobotics.reuse.commands.driveconfig.ShiftToggle;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.AXIS;
import org.wfrobotics.reuse.driveio.Arcade.ArcadeIO;
import org.wfrobotics.reuse.driveio.Arcade.ArcadeRocketXbox;
import org.wfrobotics.reuse.driveio.Mecanum.MecanumIO;
import org.wfrobotics.reuse.driveio.Swerve.SwerveIO;
import org.wfrobotics.reuse.driveio.Tank.TankIO;
import org.wfrobotics.robot.commands.intake.JawsToggle;
import org.wfrobotics.robot.commands.intake.WristToggle;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.lift.LiftToHeight;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Maps controllers to Commands **/
public class IO
{
    private static IO instance = null;
    private final ArrayList<Button> robotSpecific = new ArrayList<Button>();  // Keep buttons instantiated
    private final Xbox driver;
    private final Xbox operator;
    private final Panel panel;

    /* Only one of these should be instantiated at a time */
    public TankIO tankIO;
    public ArcadeIO arcadeIO;
    public MecanumIO mecanumIO;
    public SwerveIO swerveIO;

    private IO(Xbox driver, Xbox operator, Panel panel)
    {
        this.driver = driver;
        this.operator = operator;
        this.panel = panel;

        // ------------------- Select Drive-style  ----------------
        arcadeIO = new ArcadeRocketXbox(driver);
        //        //tankIO = new TankXbox(driver);


        // ------------------------- Drive ------------------------
        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new ShiftToggle()));
        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveOff()));


        // ------------------------- Climb ------------------------


        // ------------------------ Intake ------------------------
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.WHEN_PRESSED, new WristToggle()));
        //        robotSpecific.add(ButtonFactory.makeButton(operator,  AXIS.RIGHT_TRIGGER, .33, TRIGGER.WHILE_HELD, new IntakePush()));
        robotSpecific.add(ButtonFactory.makeButton(operator,  Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new JawsToggle()));

        // ------------------------- Lift -------------------------
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new LiftToHeight(LiftHeight.Scale.get())));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new LiftToHeight(LiftHeight.Switch.get())));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new LiftToHeight(LiftHeight.Intake.get())));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new LiftGoHome()));




        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush(.5)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull(.5)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new Elevate(-1)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Elevate(1)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.X, TRIGGER.TOGGLE_WHEN_PRESSED, new IntakeSolenoid(true)));
        //
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new ElevateRendezvous((5000), .5)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new ElevatePID((18000/2)/4096)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LEFT_STICK, TRIGGER.WHILE_HELD, new ElevatePID(0)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.RIGHT_STICK, TRIGGER.WHILE_HELD, new ElevatePID(18000/4096)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull()));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Elevate(-1)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new Elevate(1)));
        //        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.TOGGLE_WHEN_PRESSED, new IntakeSolenoid(true)));
        //
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new Elevate(-1)));
    }

    // ------------------- Robot-specific --------------------

    public double getIntakeIn()
    {
        double value = operator.getTrigger(Hand.kRight);
        SmartDashboard.putNumber("Intake Wheels In", value);
        return (value > .1) ? value : 0;
    }

    public double getIntakeOut()
    {
        double value = operator.getTrigger(Hand.kLeft);
        SmartDashboard.putNumber("Intake Wheels Out", value);
        return (value > .1) ? value : 0;
    }

    public double getLiftStick()
    {
        return operator.getAxis(AXIS.RIGHT_Y);
    }

    // ------------------------ Reuse ------------------------

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