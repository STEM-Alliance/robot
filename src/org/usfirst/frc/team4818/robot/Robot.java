package org.usfirst.frc.team4818.robot;

import com.taurus.controller.ControllerChooser;
import com.taurus.controller.ControllerJoysticks;
import com.taurus.controller.Controller;
import com.taurus.controller.ControllerXbox;
import com.taurus.swerve.DriveScheme;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SampleRobot
{

    private final double TIME_RATE_DASH = .1;
    private final double TIME_RATE_SWERVE = .01;

    // Motor Objects
    private SwerveChassis drive;

    // Joysticks
    private Controller controller;
    private ControllerChooser controllerChooser;

    private PowerDistributionPanel PDP;

    private DriveScheme driveScheme;

    private boolean TEST = true;
    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private final int TEST_MODE_CALIBRATION_1 = 2;
    private final int TEST_MODE_CALIBRATION_2 = 3;
    private final int TEST_MODE_CALIBRATION_3 = 4;

    private SendableChooser testChooser = new SendableChooser();
    private SendableChooser testWheelChooser = new SendableChooser();

    // private CameraServer cam;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {

        drive = new SwerveChassis();
        driveScheme = new DriveScheme();

        // cam = CameraServer.getInstance();
        // cam.setQuality(50);
        // cam.startAutomaticCapture("cam0");

        PDP = new PowerDistributionPanel();

        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();

        // set up the choosers for running tests while in teleop mode
        testChooser = new SendableChooser();
        testChooser.addDefault("Normal", Integer.valueOf(TEST_MODE_NORMAL));
        testChooser.addObject("Wheel Test", Integer.valueOf(TEST_MODE_WHEEL));
        testChooser.addObject("Wheel Calibration 1",
                Integer.valueOf(TEST_MODE_CALIBRATION_1));
        testChooser.addObject("Wheel Calibration 2",
                Integer.valueOf(TEST_MODE_CALIBRATION_2));
        testChooser.addObject("Wheel Calibration 3",
                Integer.valueOf(TEST_MODE_CALIBRATION_3));

        SmartDashboard.putData("Test", testChooser);

        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right", Integer.valueOf(2));
        testWheelChooser.addObject("Back Left", Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);

        SmartDashboard.putBoolean("TEST MODE", TEST);

    }

    /**
     * runs during operator control
     */
    public void operatorControl()
    {
        double TimeLastDash = 0;
        double TimeLastSwerve = 0;

        controller = controllerChooser.GetController();
        drive.ZeroGyro();

        while (isOperatorControl() && isEnabled())
        {
            if ((Timer.getFPGATimestamp() - TimeLastDash) > TIME_RATE_DASH)
            {
                TimeLastDash = Timer.getFPGATimestamp();
                UpdateDashboard();
            }

            if ((Timer.getFPGATimestamp() - TimeLastSwerve) > TIME_RATE_SWERVE)
            {
                TimeLastSwerve = Timer.getFPGATimestamp();
                if (!TEST)
                {
                    DriveNormal();
                } else
                {
                    TestRun();
                }
            }
        }
    }

    /**
     * Pneumatics
     */
    public void pneumaticsControl()
    {

    }

    /**
     * Update the dashboard with the common entries
     */
    private void UpdateDashboard()
    {
        for (int i = 0; i < 16; i++)
        {
            SmartDashboard.putNumber("PDP " + i, PDP.getCurrent(i));
        }

        SmartDashboard.putNumber("PDP Total Current", PDP.getTotalCurrent());
        SmartDashboard.putNumber("PDP Total Power", PDP.getTotalPower());
        SmartDashboard.putNumber("PDP Total Energy", PDP.getTotalEnergy());

        // display the joysticks on smart dashboard
        SmartDashboard.putNumber("Left Mag",
                controller.getMagnitude(Hand.kLeft));
        SmartDashboard.putNumber("Left Angle",
                controller.getDirectionDegrees(Hand.kLeft));
        SmartDashboard.putNumber("Right Mag",
                controller.getMagnitude(Hand.kRight));
        SmartDashboard.putNumber("Right Angle",
                controller.getDirectionDegrees(Hand.kRight));

        if (driveScheme.get() == DriveScheme.ANGLE_DRIVE)
        {
            SmartDashboard.putNumber("Angle heading",
                    controller.getAngleDrive_Heading());
        }

        // display each wheel's mag and angle in SmartDashboard
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Mag",
                    drive.getWheelActual(i).getMag());
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Angle",
                    drive.getWheelActual(i).getAngle());
        }

        drive.MaxAvailableVelocity = SmartDashboard.getNumber("Max Velocity",
                drive.MaxAvailableVelocity);

        SmartDashboard.putNumber("Gyro Angle", drive.Gyro.getYaw());

        // update the test mode
        // disable for competitions?
        TEST = SmartDashboard.getBoolean("TEST MODE", TEST);

    }

    /**
     * Run the test functions
     */
    private void TestRun()
    {
        int i = ((Integer) testWheelChooser.getSelected()).intValue();

        switch (((Integer) testChooser.getSelected()).intValue())
        {
        case TEST_MODE_WHEEL:
            TestWheel(i);
            break;

        case TEST_MODE_CALIBRATION_1:
            drive.getWheel(i).MotorAngle.set(controller.getX(Hand.kRight) * .3);
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
            DriveNormal();
            break;
        }
    }

    /**
     * Test an individual wheel module
     */
    private void TestWheel(int index)
    {
        // use the left joystick to control the wheel module
        SwerveVector WheelActual = drive.getWheel(index).setDesired(
                controller.getHaloDrive_Velocity(),
                controller.getHighGearEnable(), controller.getBrake());

        // display in SmartDashboard
        SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
        SmartDashboard.putNumber("Test Wheel Angle Actual",
                WheelActual.getAngle());

        // if the button is not held down, we're in high gear
        drive.setGearHigh(controller.getHighGearEnable());
        drive.UpdateShifter();
    }

    /**
     * Run the normal operating Drive system
     */
    private void DriveNormal()
    {
        // Use the Joystick inputs to update the drive system
        switch (driveScheme.get())
        {
        case DriveScheme.ANGLE_DRIVE:
            drive.UpdateAngleDrive(controller.getAngleDrive_Velocity(),
                    controller.getAngleDrive_Heading());
            break;

        case DriveScheme.COMBO_DRIVE:
            drive.UpdateHaloDrive(controller.getHaloDrive_Velocity(),
                    controller.getHaloDrive_Rotation(), controller.getDPad());
            break;

        default:
        case DriveScheme.HALO_DRIVE:
            drive.UpdateHaloDrive(controller.getHaloDrive_Velocity(),
                    controller.getHaloDrive_Rotation());
            break;
        }

        drive.setGearHigh(controller.getHighGearEnable());
        drive.setBrake(controller.getBrake());
        drive.setFieldRelative(controller.getFieldRelative());
        if (controller.getResetGyro())
        {
            drive.ZeroGyro();
        }
    }

}
