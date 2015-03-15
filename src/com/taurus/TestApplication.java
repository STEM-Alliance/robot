package com.taurus;

import com.taurus.controller.Controller;
import com.taurus.controller.ControllerChooser;
import com.taurus.robotspecific2015.Constants;
import com.taurus.robotspecific2015.LinearActuator;
import com.taurus.robotspecific2015.LinearActuatorPot;
import com.taurus.robotspecific2015.SensorDigital;
import com.taurus.swerve.SwerveChassis;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestApplication implements Application {

    // App generic
    private final double TIME_RATE_DASH = .2;
    private final double TIME_RATE_SWERVE = .01;
    private double TimeLastDash = 0;
    private double TimeLastSwerve = 0;

    protected SwerveChassis drive;
    protected Controller controller;
    private ControllerChooser controllerChooser;
    private PowerDistributionPanel PDP;
    public static Preferences prefs;
    
    // App specific
    LinearActuator Actuator;
    private SensorDigital ZeroSensor;
    
    public TestApplication()
    {
        // App generic
        prefs = Preferences.getInstance();        
        PDP = new PowerDistributionPanel();
        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();        
        drive = new SwerveChassis(controller);
        
        // App specific        
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING,
                Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD,
                Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);
        ZeroSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO_LEFT);
    }

    @Override
    public void TeleopInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodic()
    {        
        boolean button1 = controller.getRawButton(1);
        boolean button2 = controller.getRawButton(2);
        boolean button3 = controller.getRawButton(3);
        boolean button4 = controller.getRawButton(4);
        boolean button5 = controller.getRawButton(5);
        boolean button6 = controller.getRawButton(6);

        // Service drive
        if ((Timer.getFPGATimestamp() - TimeLastDash) > TIME_RATE_DASH)
        {
            TimeLastDash = Timer.getFPGATimestamp();
            
            //UpdateDashboard();
            
            //SmartDashboard.putNumber("Dash Task Length", Timer.getFPGATimestamp() - TimeLastDash);
        }

        if ((Timer.getFPGATimestamp() - TimeLastSwerve) > TIME_RATE_SWERVE)
        {
            TimeLastSwerve = Timer.getFPGATimestamp();
            
            drive.run();
            
            SmartDashboard.putNumber("Swerve Task Length", Timer.getFPGATimestamp() - TimeLastSwerve);
        }
        
        // App specific
        if (button1)
        {
            Actuator.SetPosition(0);
        }
        else if (button2)
        {
            Actuator.SetPosition(1);
        }
        else if (button3)
        {
            Actuator.SetPosition(2);
        }
        else if (button4)
        {
            Actuator.SetPosition(3);
        }
        else if (button5)
        {
            Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_FORWARD);
        }
        else if (button6)
        {
            Actuator.SetSpeedRaw(Constants.MOTOR_DIRECTION_BACKWARD);
        }
        else
        {
            Actuator.SetSpeedRaw(0);
        }
        
        if (ZeroSensor.IsOn())
        {
            Actuator.Zero();
        }
        
        SmartDashboard.putBoolean("Zero Sensor", ZeroSensor.IsOn());
        SmartDashboard.putNumber("Actuator Raw", Actuator.GetRaw());
        SmartDashboard.putNumber("Actuator Distance", Actuator.GetDistance());
        SmartDashboard.putNumber("Actuator Position", Actuator.GetPositionRaw());
    }

    @Override
    public void TeleopDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousPeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModePeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeDeInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledInit()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledPeriodic()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledDeInit()
    {
        // TODO Auto-generated method stub

    }
}
