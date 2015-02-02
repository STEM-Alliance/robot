package com.taurus.robotspecific2015;

import com.taurus.swerve.SwerveVector;
import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Timer;


public class Application extends com.taurus.Application
{
    private double AutoStateTime;
    private AUTO_STATE_MACHINE AutoState;
    
    Lift lift;
    Car TestModeCar;
    Ejector TestModeEjector;
    Sensor TestModeToteIntakeSensor;
    private Vision vision;

    public Application()
    {
        super(); // Initialize anything in the super class constructor

        lift = new Lift();
        vision = new Vision();
        
        vision.Start();
    }
    
    public void TeleopInitRobotSpecific()
    {

    }

    public void TeleopPeriodicRobotSpecific()
    {
        boolean button1 = false; // TODO: Get these button values from the controller
        boolean button2 = false;
        boolean button3 = false;
        boolean button4 = false; 
        // TODO: Do we want a button to cancel the current routine

        // TODO: Make sure that we stay in the same mode after the button
        // is pressed until we are done doing that routine

        if (button1)
        {
            lift.AddChuteToteToStack();
        }
        else if (button2)
        {
            lift.AddFloorToteToStack();
        }
        else if (button3)
        {
            lift.AddContainerToStack();
        }
        else if (button4)
        {
            lift.EjectStack();
        }
    }

    public void TeleopDeInitRobotSpecific()
    {

    }

    public void AutonomousInitRobotSpecific()
    {
        drive.ZeroGyro();
        drive.SetGyroZero(-90); // starting facing left, so fix offset

        AutoState = Constants.AUTO_STATE_MACHINE.DRIVE_FOR;
        AutoStateTime = Timer.getFPGATimestamp();
    }

    public void AutonomousPeriodicRobotSpecific()
    {
        switch (AutoState)
        {
            case DRIVE_FOR:
                drive.UpdateDrive(new SwerveVector(0, -1), 0, -1);
                if (Timer.getFPGATimestamp() - AutoStateTime > 2)
                {
                    AutoState = AUTO_STATE_MACHINE.DRIVE_STOP;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case DRIVE_STOP:
                drive.UpdateDrive(new SwerveVector(0, 0.001), 0, -1);
                if (Timer.getFPGATimestamp() - AutoStateTime > .5)
                {
                    AutoState = AUTO_STATE_MACHINE.DRIVE_RIGHT;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case DRIVE_RIGHT:
                drive.UpdateDrive(new SwerveVector(1, 0), 0, 270);
                if (Timer.getFPGATimestamp() - AutoStateTime > 2)
                {
                    AutoState = AUTO_STATE_MACHINE.AUTO_END;
                    AutoStateTime = Timer.getFPGATimestamp();
                }
                break;

            case AUTO_END:
                drive.UpdateDrive(new SwerveVector(0, 0), 0, -1);

                break;

            default:
                AutoState = AUTO_STATE_MACHINE.AUTO_END;
                break;
        }
    }

    public void AutonomousDeInitRobotSpecific()
    {

    }

    public void TestModeInitRobotSpecific()
    {
        TestModeCar = lift.LiftCar;
        TestModeEjector = lift.StackEjector;
        TestModeToteIntakeSensor = lift.ToteIntakeSensor;
    }

    public void TestModePeriodicRobotSpecific()
    {
        int testMode = 0;
        boolean button1 = false; // TODO: Get these button values from the
                                 // controller
        boolean button2 = false;
        boolean button3 = false;
        boolean button4 = false;
        boolean button5 = false;

        // TODO: Add test modes for cylinders and motors and features.
        switch (testMode)
        {
            case Constants.TEST_MODE_PNEUMATIC:
                PneumaticSubsystem testCylinders;

                if (button1)
                {
                    testCylinders = lift.CylindersRails;
                }
                else if (button2)
                {
                    testCylinders = lift.CylindersContainerCar;
                }
                else if (button3)
                {
                    testCylinders = lift.CylindersContainerFixed;
                }
                else if (button4)
                {
                    testCylinders = lift.CylindersStackHolder;
                }
                else if (button5)
                {
                    testCylinders = lift.CylindersJawsOfLife;
                }
                else
                {
                    testCylinders = lift.CylindersRails;
                }

                // Toggle selected cylinders to opposite position
                if (testCylinders.IsExtended())
                {
                    testCylinders.Contract();
                }
                else
                {
                    testCylinders.Extend();
                }
                break;
            case Constants.TEST_MODE_MOTORS:
                if (button1)
                {
                    TestModeCar.Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button2)
                {
                    TestModeCar.Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
                }
                else if (button3)
                {
                    TestModeEjector.Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button4)
                {
                    TestModeEjector.Motors.Set(Constants.MOTOR_DIRECTION_BACKWARD);
                }
                break;
            default:
                break;
        }
        // TODO: Get the value of one sensor and report that somehow
        if (TestModeToteIntakeSensor.IsOn())
        {
            // TODO: Update smartdashboard or however we show sensors
        }
    }

    public void TestModeDeInitRobotSpecific()
    {

    }

    public void DisabledInitRobotSpecific()
    {

    }

    public void DisabledPeriodicRobotSpecific()
    {

    }

    public void DisabledDeInitRobotSpecific()
    {

    }
}
