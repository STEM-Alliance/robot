package com.taurus.swerve;

import com.taurus.controller.Controller;
import com.taurus.controller.ControllerChooser;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Application implements com.taurus.Application {
    
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
    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private final int TEST_MODE_CALIBRATION_1 = 2;
    private final int TEST_MODE_CALIBRATION_2 = 3;
    private final int TEST_MODE_CALIBRATION_3 = 4;

    private SendableChooser testChooser = new SendableChooser();
    private SendableChooser testWheelChooser = new SendableChooser();
    
    public Application()
    {
        // App generic
        prefs = Preferences.getInstance();        
        PDP = new PowerDistributionPanel();
        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();        
        drive = new SwerveChassis(controller);
        
        // App specific
        // set up the choosers for running tests while in teleop mode
        testChooser = new SendableChooser();
        testChooser.addDefault("Normal", Integer.valueOf(TEST_MODE_NORMAL));
        testChooser.addObject("Wheel Test Full", Integer.valueOf(TEST_MODE_WHEEL));
        testChooser.addObject("Wheel Spin Raw",
                Integer.valueOf(TEST_MODE_CALIBRATION_1));
        testChooser.addObject("Wheel Set Angle",
                Integer.valueOf(TEST_MODE_CALIBRATION_2));
        testChooser.addObject("Wheel To Heading",
                Integer.valueOf(TEST_MODE_CALIBRATION_3));

        SmartDashboard.putData("Test Mode", testChooser);

        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right", Integer.valueOf(2));
        testWheelChooser.addObject("Back Left", Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);
    }
    
    public void TeleopInit()
    {

    }

    public void TeleopPeriodic()
    {
        // Service drive
        if ((Timer.getFPGATimestamp() - TimeLastDash) > TIME_RATE_DASH)
        {
            TimeLastDash = Timer.getFPGATimestamp();
            
            UpdateDashboard();
            
            //SmartDashboard.putNumber("Dash Task Length", Timer.getFPGATimestamp() - TimeLastDash);
        }

        if ((Timer.getFPGATimestamp() - TimeLastSwerve) > TIME_RATE_SWERVE)
        {
            TimeLastSwerve = Timer.getFPGATimestamp();
            
            drive.run();
            
            SmartDashboard.putNumber("Swerve Task Length", Timer.getFPGATimestamp() - TimeLastSwerve);
        }
        
        int i = ((Integer) testWheelChooser.getSelected()).intValue();

        switch (((Integer) testChooser.getSelected()).intValue())
        {
            case TEST_MODE_WHEEL:
             // use the left joystick to control the wheel module
                SwerveVector WheelActual = drive.getWheel(i).setDesired(
                        controller.getHaloDrive_Velocity(),
                      /*  controller.getHighGearEnable(),*/ controller.getSwerveBrake());

                // display in SmartDashboard
                SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
                SmartDashboard.putNumber("Test Wheel Angle Actual",
                        WheelActual.getAngle());

                // if the button is not held down, we're in high gear
//                drive.setGearHigh(controller.getHighGearEnable());
//                drive.UpdateShifter();
                break;

            case TEST_MODE_CALIBRATION_1:
                drive.getWheel(i).MotorAngle
                        .set(controller.getX(Hand.kRight) * .4);
                SmartDashboard.putNumber("motor set",
                        drive.getWheel(i).MotorAngle.get());
                SmartDashboard.putNumber("pot read", drive.getWheel(i)
                        .getAnglePotValue());

                break;

            case TEST_MODE_CALIBRATION_2:
                drive.getWheel(i).updateAngleMotor(
                        controller.getDirectionDegrees(Hand.kRight), 1.0);

                break;

            case TEST_MODE_CALIBRATION_3:
                SwerveVector TestVector = new SwerveVector();
                TestVector.setMagAngle(.5, controller.getDPad());
                if (controller.getDPad() != -1)
                {
                    drive.UpdateHaloDrive(TestVector,
                            controller.getHaloDrive_Rotation());
                }

                break;
            case TEST_MODE_NORMAL:
            default:
                drive.run();
                break;
        }
    }

    public void TeleopDeInit()
    {

    }

    public void AutonomousInit()
    {
        controller = controllerChooser.GetController();
    }

    public void AutonomousPeriodic()
    {

    }

    public void AutonomousDeInit()
    {

    }

    public void TestModeInit()
    {
        controller = controllerChooser.GetController();
    }

    public void TestModePeriodic()
    {
    }

    public void TestModeDeInit()
    {

    }

    public void DisabledInit()
    {

    }

    public void DisabledPeriodic()
    {

    }

    public void DisabledDeInit()
    {

    }
    
    /**
     * Update the dashboard with the common entries
     */
    private void UpdateDashboard()
    {
//        for (int i = 0; i < 16; i++)
//        {
//            SmartDashboard.putNumber("PDP " + i, PDP.getCurrent(i));
//        }
//
//        SmartDashboard.putNumber("PDP Total Current", PDP.getTotalCurrent());
//        SmartDashboard.putNumber("PDP Total Power", PDP.getTotalPower());
//        SmartDashboard.putNumber("PDP Total Energy", PDP.getTotalEnergy());
        SmartDashboard.putNumber("Voltage", PDP.getVoltage());

        // display the joysticks on smart dashboard
//        SmartDashboard.putNumber("Left Mag",
//                controller.getMagnitude(Hand.kLeft));
//        SmartDashboard.putNumber("Left Angle",
//                controller.getDirectionDegrees(Hand.kLeft));
//        SmartDashboard.putNumber("Right Mag",
//                controller.getMagnitude(Hand.kRight));
//        SmartDashboard.putNumber("Right Angle",
//                controller.getDirectionDegrees(Hand.kRight));

//        if (driveScheme.get() == DriveScheme.ANGLE_DRIVE)
//        {
//            SmartDashboard.putNumber("Angle heading",
//                    controller.getAngleDrive_Heading());
//        }
//
//        // display each wheel's mag and angle in SmartDashboard
//        for (int i = 0; i < SwerveConstants.WheelCount; i++)
//        {
//            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Mag",
//                    drive.getWheelActual(i).getMag());
//            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Angle",
//                    drive.getWheelActual(i).getAngle());
//        }

        SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getYaw());
        SmartDashboard.putNumber("Last Heading", drive.getLastHeading());
        SmartDashboard.putBoolean("Field Relative", drive.getFieldRelative());

        SmartDashboard.putBoolean("Brake", drive.getBrake());
    }
}
