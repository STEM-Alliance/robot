package com.taurus;

import com.taurus.robotspecific2015.Constants;
import com.taurus.robotspecific2015.LinearActuator;
import com.taurus.robotspecific2015.LinearActuatorPot;
import com.taurus.robotspecific2015.SensorDigital;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TestApplication extends Application {

    LinearActuator Actuator;
    private SensorDigital ZeroSensor;
    
    public TestApplication()
    {
        super();
        
        Actuator = new LinearActuatorPot(Constants.LIFT_MOTOR_PINS,
                Constants.LIFT_MOTOR_SCALING,
                Constants.LIFT_POSTITIONS,
                Constants.LIFT_THRESHOLD,
                Constants.LIFT_POT_PIN,
                Constants.LIFT_POT_DISTANCE);
        ZeroSensor = new SensorDigital(Constants.CHANNEL_DIGITAL_CAR_ZERO);
    }

    @Override
    public void TeleopInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodicRobotSpecific()
    {
        
        boolean button1 = controller.getRawButton(1);
        boolean button2 = controller.getRawButton(2);
        boolean button3 = controller.getRawButton(3);
        boolean button4 = controller.getRawButton(4);
        boolean button5 = controller.getRawButton(5);
        boolean button6 = controller.getRawButton(6);
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
    public void TeleopDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModePeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

}
