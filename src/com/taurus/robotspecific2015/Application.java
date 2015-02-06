package com.taurus.robotspecific2015;

import com.taurus.Utilities;
import com.taurus.swerve.SwerveVector;
import com.taurus.robotspecific2015.Constants.*;
import com.taurus.robotspecific2015.VisionApplication.Autostate;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Application extends com.taurus.Application
{
    Vision vision = new Vision();
    SendableChooser autoChooser;
    
    private double AutoStateTime;
    private AUTO_STATE_MACHINE AutoState;
    
    Lift lift;
    Car TestModeCar;
    Ejector TestModeEjector;
    Sensor TestModeToteIntakeSensor;
    
    public Application()
    {
        super(); // Initialize anything in the super class constructor

        lift = new Lift();
        vision = new Vision();
        
        vision.Start();
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Drive forward", Integer.valueOf(0));
        autoChooser.addObject("Go to tote", Integer.valueOf(1));
        autoChooser.addObject("Drive L", Integer.valueOf(2));
        SmartDashboard.putData("Autonomous mode", autoChooser);
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
        int automode = ((Integer) autoChooser.getSelected()).intValue();
        
        switch (automode)
        {
            case 0:
                DriveForwardAuto();
                break;

            case 1:
                GotoToteAuto();
                break;
            
            case 2:
                Drive_L();
                break;
        }
    }

    public void Drive_L()
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
    
    enum Autostate {
        start, go, stop

    }

    @SuppressWarnings("incomplete-switch")
    private void DriveForwardAuto()
    {

        double DriveTime = 5;

        switch (AutoState)
        {
            case AUTO_START:
                AutoStateTime = Timer.getFPGATimestamp();
                AutoState = AUTO_STATE_MACHINE.DRIVE_FOR;
                break;

            case DRIVE_FOR:
                SwerveVector Velocity = new SwerveVector(0, 1);

                drive.setGearHigh(false);
                drive.UpdateDrive(Velocity, 0, 0);
                if (Timer.getFPGATimestamp() > AutoStateTime + DriveTime)
                {
                    AutoState = AUTO_STATE_MACHINE.AUTO_END;
                }
                break;

            case AUTO_END:
                drive.UpdateDrive(new SwerveVector(), 0, -1);
                break;
        }

    }

    private void GotoToteAuto()
    {
        double maxSlide = prefs.getDouble("MaxSlide", 1), slideP = prefs
                .getDouble("SlideP", 1), targetX = prefs.getDouble(
                "SlideTargetX", .5), forwardSpeed = prefs.getDouble(
                "AutoSpeed", .5);

        SwerveVector Velocity;

        if (vision.getToteSeen())
        {
            Velocity = new SwerveVector(-forwardSpeed, Utilities.clampToRange(
                    (vision.getResultX() - targetX) * slideP, -maxSlide,
                    maxSlide));
        }
        else
        {
            Velocity = new SwerveVector(-forwardSpeed, 0);
        }

        double Rotation = 0;
        double Heading = 270;

        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());

        drive.setGearHigh(false);
        drive.UpdateDrive(Velocity, Rotation, Heading);
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
        boolean button1 = controller.getRawButtion(1); // TODO: Get these button values from the
        boolean button2 = controller.getRawButtion(2);
        boolean button3 = controller.getRawButtion(3);
        boolean button4 = controller.getRawButtion(4);
        boolean button5 = controller.getRawButtion(5);
        boolean button6 = controller.getRawButtion(6);

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
                    TestModeCar.MotorEncoder.SetPosition(0);
                }
                else if (button2)
                {
                    TestModeCar.MotorEncoder.SetPosition(1);
                }
                else if (button3)
                {
                    TestModeCar.MotorEncoder.SetPosition(2);
                }
                else if (button4)
                {
                    TestModeCar.MotorEncoder.SetPosition(3);
                }
                else if (button5)
                {
                    TestModeEjector.Motors.Set(Constants.MOTOR_DIRECTION_FORWARD);
                }
                else if (button6)
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
