package com.taurus.swerve;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Application extends com.taurus.Application {

    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private final int TEST_MODE_CALIBRATION_1 = 2;
    private final int TEST_MODE_CALIBRATION_2 = 3;
    private final int TEST_MODE_CALIBRATION_3 = 4;

    private SendableChooser testChooser = new SendableChooser();
    private SendableChooser testWheelChooser = new SendableChooser();
    
    public Application()
    {
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

        SmartDashboard.putData("Test", testChooser);

        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right", Integer.valueOf(2));
        testWheelChooser.addObject("Back Left", Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);
    }
    
    public void TeleopInitRobotSpecific()
    {

    }

    public void TeleopPeriodicRobotSpecific()
    {

    }

    public void TeleopDeInitRobotSpecific()
    {

    }

    public void AutonomousInitRobotSpecific()
    {

    }

    public void AutonomousPeriodicRobotSpecific()
    {

    }

    public void AutonomousDeInitRobotSpecific()
    {

    }

    public void TestModeInitRobotSpecific()
    {

    }

    public void TestModePeriodicRobotSpecific()
    {
        int i = ((Integer) testWheelChooser.getSelected()).intValue();

        switch (((Integer) testChooser.getSelected()).intValue())
        {
            case TEST_MODE_WHEEL:
             // use the left joystick to control the wheel module
                SwerveVector WheelActual = drive.getWheel(i).setDesired(
                        controller.getHaloDrive_Velocity(),
                        controller.getHighGearEnable(), controller.getBrake());

                // display in SmartDashboard
                SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
                SmartDashboard.putNumber("Test Wheel Angle Actual",
                        WheelActual.getAngle());

                // if the button is not held down, we're in high gear
                drive.setGearHigh(controller.getHighGearEnable());
                drive.UpdateShifter();
                break;

            case TEST_MODE_CALIBRATION_1:
                drive.getWheel(i).MotorAngle
                        .set(controller.getX(Hand.kRight) * .3);
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
                drive.run(controller);
                break;
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
