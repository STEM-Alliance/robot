package org.usfirst.frc.team4818.robot;


import com.ni.vision.NIVision;
import com.ni.vision.NIVision.CompressionType;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.FlattenType;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.RawData;
import com.taurus.controller.Controller;
import com.taurus.controller.ControllerChooser;
import com.taurus.swerve.DriveScheme;
import com.taurus.swerve.SwerveChassis;
import com.taurus.swerve.SwerveConstants;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
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
public class Robot extends SampleRobot {

    private final double TIME_RATE_DASH = .2;
    private final double TIME_RATE_SWERVE = .01;
    private final double TIME_RATE_VISION = .033;

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
    private SendableChooser imageChooser = new SendableChooser();
    
    public static Preferences prefs;

    public class VisionTask implements Runnable {

        private static final String ATTR_VIDEO_MODE = "AcquisitionAttributes::VideoMode";
        private static final String ATTR_WB_MODE = "CameraAttributes::WhiteBalance::Mode";
        private static final String ATTR_WB_VALUE = "CameraAttributes::WhiteBalance::Value";
        private static final String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
        private static final String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";
        private static final String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
        private static final String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
        
        int session;
        Image frame;
        Image frameTH;
                
        @Override
        public void run()
        {
            frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
            
            // the camera name (ex "cam0") can be found through the roborio web interface
            session = NIVision.IMAQdxOpenCamera("cam0",
                    NIVision.IMAQdxCameraControlMode.CameraControlModeController);

//            NIVision.dxEnumerateVideoModesResult enumerated = NIVision.IMAQdxEnumerateVideoModes(session);
//            String Modes = "";
//            for (NIVision.IMAQdxEnumItem mode : enumerated.videoModeArray)
//            {
//                Modes += mode.Name + " " + mode.Value + ", ";
//            }
//            SmartDashboard.putString("Modes", Modes);
            
            int videoMode = Robot.prefs.getInt("VIDEO_MODE", 93);

            NIVision.IMAQdxSetAttributeU32(session, ATTR_VIDEO_MODE, videoMode);
            NIVision.IMAQdxConfigureGrab(session);
            
            NIVision.IMAQdxStartAcquisition(session);
            
            double TimeLastVision = 0;
            
            CameraServer.getInstance().setQuality(15);
            
            while (true)
            {
                if ((Timer.getFPGATimestamp() - TimeLastVision) > TIME_RATE_VISION)
                {
                    TimeLastVision = Timer.getFPGATimestamp();

                    NIVision.IMAQdxGrab(session, frame, 1);
                    
                    if (((Integer)imageChooser.getSelected()).intValue() == 0)
                    {
                        CameraServer.getInstance().setImage(frame);
                    }

                    NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(100, 60), new Point(60, 180), 0.0f);
                    NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(101, 60), new Point(61, 180), 0.0f);
                    NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(320-100, 60), new Point(320-60, 180), 0.0f);
                    NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(320-101, 60), new Point(320-61, 180), 0.0f);
//                    for(int i = 0; i <= 480; i += 40){
//                        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(0, i), new Point(640, i) , 0.0f);
//                    }
//                    for(int i = 0; i <= 640; i += 40){
//                        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE, new Point(i, 0), new Point(i, 480) , 0.0f);
//                    }
                    
                    if (((Integer)imageChooser.getSelected()).intValue() == 1)
                    {
                        CameraServer.getInstance().setImage(frame);
                    }
                    
                    SmartDashboard.putNumber("Vision Task Length", Timer.getFPGATimestamp() - TimeLastVision);
                }
                Timer.delay(Math.max(0, TIME_RATE_VISION-(Timer.getFPGATimestamp()-TimeLastVision)));
            }

            //NIVision.IMAQdxStopAcquisition(session);
        }
    }

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit()
    {
        prefs = Preferences.getInstance();
        
        drive = new SwerveChassis();
        driveScheme = new DriveScheme();

        Thread visionThread = new Thread(new VisionTask());
        visionThread.setPriority(Thread.MIN_PRIORITY);
        visionThread.start();
        
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
        
        imageChooser = new SendableChooser();
        imageChooser.addDefault("Input image", Integer.valueOf(0));        
        imageChooser.addObject("Processed image", Integer.valueOf(1));
        SmartDashboard.putData("Image to show", imageChooser);

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
                //UpdateDashboard();
                SmartDashboard.putNumber("Dash Task Length", Timer.getFPGATimestamp() - TimeLastDash);
            }

            if ((Timer.getFPGATimestamp() - TimeLastSwerve) > TIME_RATE_SWERVE)
            {
                TimeLastSwerve = Timer.getFPGATimestamp();
                if (!TEST)
                {
                    DriveNormal();
                }
                else
                {
                    TestRun();
                }
                SmartDashboard.putNumber("Swerve Task Length", Timer.getFPGATimestamp() - TimeLastSwerve);
            }
        }
    }
    
    
    public static enum AUTO_STATE_MACHINE
    {
        INITSTATE,
        DRIVE_FOR,
        DRIVE_STOP,
        DRIVE_RIGHT,
        AUTO_END,
        
        
    }
   
    
    public void autonomous() {
       
        double StateTime = 0;
        
        AUTO_STATE_MACHINE state = AUTO_STATE_MACHINE.INITSTATE;
        
        while(isAutonomous() && isEnabled()){
            
            switch(state)
             {
                case INITSTATE: 
                    drive.ZeroGyro();
                    
                    state  = AUTO_STATE_MACHINE.DRIVE_FOR;
                    StateTime = Timer.getFPGATimestamp();
                    break;
                case DRIVE_FOR:
                    drive.UpdateDrive(new SwerveVector(0, -1), 0, -1);
                    if(Timer.getFPGATimestamp() - StateTime > 2 )
                    {
                        StateTime = Timer.getFPGATimestamp();
                        state = AUTO_STATE_MACHINE.DRIVE_STOP;
                    }
                    break;
                case DRIVE_STOP:
                    drive.UpdateDrive(new SwerveVector(0, 0.001), 0, -1);
                    if(Timer.getFPGATimestamp() - StateTime > .5 )
                    {
                        StateTime = Timer.getFPGATimestamp();
                        state = AUTO_STATE_MACHINE.DRIVE_RIGHT;
                    }
                    break;
                case DRIVE_RIGHT:
                    drive.UpdateDrive(new SwerveVector(1, 0), 0, -1);
                    if (Timer.getFPGATimestamp() - StateTime > 2)
                    {
                        StateTime = Timer.getFPGATimestamp();
                        state = AUTO_STATE_MACHINE.AUTO_END;
                    }
                    break;
                case AUTO_END:
                    drive.UpdateDrive(new SwerveVector(0, 0), 0, -1);
                    
                    break;
                
                    
                default:
                    // TODO: Put error condition here
                    break;
                
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
        SmartDashboard.putNumber("Voltage", PDP.getVoltage());

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

        SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getYaw());

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
                drive.UpdateDrive(controller.getAngleDrive_Velocity(), 0,
                        controller.getAngleDrive_Heading());
                break;

            case DriveScheme.HALO_DRIVE:
                drive.UpdateHaloDrive(controller.getHaloDrive_Velocity(),
                        controller.getHaloDrive_Rotation());
                break;

            default:
            case DriveScheme.COMBO_DRIVE:
                drive.UpdateDrive(controller.getHaloDrive_Velocity(),
                        controller.getHaloDrive_Rotation(),
                        controller.getDPad());
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
