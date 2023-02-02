//package edu.wpi.first.wpilibj.examples.ramsetecommand.subsystems;

package frc.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.wpilibj.DataLogManager;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSubsystem extends SubsystemBase {
    private final CANSparkMax m_leftMotor1 = new CANSparkMax(1, MotorType.kBrushless);
    private final CANSparkMax m_leftMotor2 = new CANSparkMax(2, MotorType.kBrushless);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);

    private final CANSparkMax m_rightMotor1 = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax m_rightMotor2 = new CANSparkMax(4, MotorType.kBrushless);
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);

    // The robot's drive
    private final DifferentialDrive m_drive = new DifferentialDrive(m_leftMotors, m_rightMotors);

    // The gyro sensor
    private AHRS m_ahrs;

    RelativeEncoder m_leftEncoder = m_leftMotor1.getEncoder();
    RelativeEncoder m_rightEncoder = m_rightMotor1.getEncoder();

    // Odometry class for tracking robot pose
    private final DifferentialDriveOdometry m_odometry;

    private final Field2d m_field = new Field2d();

    double m_simAngle = 0;
    double m_rSimDistance = 0;
    double m_lSimDistance = 0;
    double m_lSimCmd = 0;
    double m_rSimCmd = 0;
    double m_lSimVel = 0;
    double m_rSimVel = 0;
    int m_simIndex = 0;

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem() {

        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        m_rightMotors.setInverted(true);

        // The left side encoders are negative

        try {
            /* Communicate w/navX-MXP via the MXP SPI Bus. */
            /* Alternatively: I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB */
            /*
             * See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for
             * details.
             */
            m_ahrs = new AHRS(SPI.Port.kMXP);
        } catch (RuntimeException ex) {
            DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
        }

        m_ahrs.enableLogging(true);
        m_ahrs.reset();
        m_ahrs.calibrate();

        resetEncoders();
        m_odometry = new DifferentialDriveOdometry(
                new Rotation2d(m_ahrs.getFusedHeading()), m_leftEncoder.getPosition(), m_rightEncoder.getPosition());

        SmartDashboard.putData("Field", m_field);
        DataLogManager.start();
    }

    @Override

    public void periodic() {
        var lDistance = -m_leftEncoder.getPosition() * Configuration.MetersPerRotation;
        var rDistance = m_rightEncoder.getPosition() * Configuration.MetersPerRotation;

        SmartDashboard.putNumber("lDistance", lDistance);
        SmartDashboard.putNumber("rDistance", rDistance);

        // m_odometry.update(
        // Rotation2d.fromDegrees(-m_ahrs.getYaw()), lDistance, rDistance);
        if (Configuration.Simulate) {
            var lCmdSpeed = m_lSimCmd * Configuration.MaxRobotSpeedMPS;
            var rCmdSpeed = m_rSimCmd * Configuration.MaxRobotSpeedMPS;
            m_lSimDistance += lCmdSpeed * 0.02;
            m_rSimDistance += rCmdSpeed * 0.02;
            m_odometry.update(getHeading(), m_lSimDistance, m_rSimDistance);
        } else {
            m_odometry.update(getHeading(), lDistance, rDistance);
        }
        m_field.setRobotPose(m_odometry.getPoseMeters());
        sendStats();
    }

    /**
     * Returns the currently-estimated pose of the robot.
     *
     * @return The pose.
     */
    public Pose2d getPose() {
        return m_odometry.getPoseMeters();
    }

    /**
     * Returns the current wheel speeds of the robot.
     *
     * @return The current wheel speeds.
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
        // This function is being called periodically
        // System.out.println("getWheelSpeeds");
        var lRPM = -m_leftEncoder.getVelocity();
        var rRPM = m_rightEncoder.getVelocity();

        // The velocity is in revs/minute, multiply by 60 to get revs/second, then we
        // know 1 rev = 0.053 meters (see Robot code)
        var leftMetersPerSecond = (lRPM / 60) * Configuration.MetersPerRotation;
        var rightMetersPerSecond = (rRPM / 60) * Configuration.MetersPerRotation;
        if (Configuration.Simulate) {
            m_lSimVel = m_lSimCmd * Configuration.MaxRobotSpeedMPS;
            m_rSimVel = m_rSimCmd * Configuration.MaxRobotSpeedMPS;
            var speed = new DifferentialDriveWheelSpeeds(m_lSimVel, m_rSimVel);
            return speed;
        }
        var speed = new DifferentialDriveWheelSpeeds(leftMetersPerSecond, rightMetersPerSecond);
        return speed;
    }

    /**
     * Resets the odometry to the specified pose.
     *
     * @param pose The pose to which to set the odometry.
     */
    public void resetOdometry(Pose2d pose) {
        resetEncoders();
        m_odometry.resetPosition(m_ahrs.getRotation2d(), 0, 0, pose);
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */
    public void arcadeDrive(double fwd, double rot) {
        m_drive.arcadeDrive(fwd, rot);
        //System.out.println("arcadeDrive");
        periodic();
        sendStats();
    }

    /**
     * Controls the left and right sides of the drive directly with voltages.
     *
     * @param leftVolts  the commanded left output
     * @param rightVolts the commanded right output
     */
    // public void tankDriveVolts(double leftVolts, double rightVolts) {
    // m_leftMotors.setVoltage(leftVolts);
    // m_rightMotors.setVoltage(rightVolts);
    // m_drive.feed();
    // }

    /** Resets the drive encoders to currently read a position of 0. */
    public void resetEncoders() {
        System.out.println("resetEncoders");
        m_leftEncoder.setPosition(0);
        m_rightEncoder.setPosition(0);
    }

    /**
     * Gets the average distance of the two encoders.
     *
     * @return the average of the two encoder readings
     */
    public double getAverageEncoderDistance() {
        System.out.println("getAverageEncoderDistance");
        return (m_leftEncoder.getPosition() + m_rightEncoder.getPosition()) / 2.0;
    }

    /**
     * Gets the left drive encoder.;
     *
     * @return the left drive encoder
     */
    // public Encoder getLeftEncoder() {
    // //return m_leftEncoder;
    // }

    /**
     * Gets the right drive encoder.
     *
     * @return the right drive encoder
     */
    // public Encoder getRightEncoder() {
    // //return m_rightEncoder;
    // }

    /**
     * Sets the max output of the drive. Useful for scaling the drive to drive more
     * slowly.
     *
     * @param maxOutput the maximum output to which the drive will be constrained
     */
    public void setMaxOutput(double maxOutput) {
        m_drive.setMaxOutput(maxOutput);
    }

    /** Zeroes the heading of the robot. */
    public void zeroHeading() {
        System.out.println("Zero Heading");
        m_ahrs.zeroYaw();
        // m_rightMotors.setInverted(false);
    }

    /**
     * Returns the heading of the robot.
     *
     * @return the robot's heading in degrees, from -180 to 180
     */
    public Rotation2d getHeading() {
        //System.out.println("getHeading");
        if (Configuration.Simulate)
            return new Rotation2d(m_simAngle);
        return Rotation2d.fromDegrees(-m_ahrs.getFusedHeading());
    }

    /**
     * Returns the turn rate of the robot.
     *
     * @return The turn rate of the robot, in degrees per second
     */
    public double getTurnRate() {
        System.out.println("getTurnRate");
        SmartDashboard.putNumber("TurnRate", m_ahrs.getRate());
        return -m_ahrs.getRate();
    }

    public void move(double leftSpeed, double rightSpeed) {
        leftSpeed = -leftSpeed;
        rightSpeed = -rightSpeed;
        SmartDashboard.putNumber("CmdSpdLMPS", leftSpeed);
        SmartDashboard.putNumber("CmdSpdRMPS", rightSpeed);

        // The speed values are in meters per second. Convert to -1 to +1
        leftSpeed *= 0.19;
        rightSpeed *= 0.19;
        if (leftSpeed > 1) {
            leftSpeed = 1;
        } else if (leftSpeed < -1) {
            leftSpeed = -1;
        }
        if (rightSpeed > 1) {
            rightSpeed = 1;
        } else if (rightSpeed < -1) {
            rightSpeed = -1;
        }
        SmartDashboard.putNumber("LeftMotorCmd", leftSpeed);
        SmartDashboard.putNumber("RightMotorCmd", rightSpeed);
        m_drive.tankDrive(leftSpeed, rightSpeed);

        m_lSimCmd = -leftSpeed;
        m_rSimCmd = -rightSpeed;

        sendStats();
    }

    public void stop() {
        System.out.println("Stopping robot");
        m_drive.tankDrive(0, 0);
    }

    public void sendStats() {
        SmartDashboard.putNumber("Heading", getHeading().getRadians());
        SmartDashboard.putNumber("Yaw", Rotation2d.fromDegrees(-m_ahrs.getYaw()).getRadians());
        SmartDashboard.putNumber("pitch", m_ahrs.getPitch());
        SmartDashboard.putNumber("roll", m_ahrs.getRoll());
        SmartDashboard.putNumber("lEnc", -m_leftEncoder.getPosition()); // These are in rotations
        SmartDashboard.putNumber("rEnc", m_rightEncoder.getPosition());
        SmartDashboard.putNumber("lRPM", -m_leftEncoder.getVelocity()); // These are in RPM
        SmartDashboard.putNumber("rRPM", m_rightEncoder.getVelocity());
        SmartDashboard.putNumber("lVel", -m_leftEncoder.getVelocity() / 60 * Configuration.MetersPerRotation); // These
                                                                                                               // are
                                                                                                               // in RPM
        SmartDashboard.putNumber("rVel", m_rightEncoder.getVelocity() / 60 * Configuration.MetersPerRotation);
        if (Configuration.Simulate) {
            SmartDashboard.putNumber("lSimCmd", m_lSimCmd);
            SmartDashboard.putNumber("lSimVel", m_lSimVel);
            SmartDashboard.putNumber("lSimDistance", m_lSimDistance);
            SmartDashboard.putNumber("rSimCmd", m_rSimCmd);
            SmartDashboard.putNumber("rSimVel", m_rSimVel);
            SmartDashboard.putNumber("rSimDistance", m_rSimDistance);
        }

        var pose = getPose();
        SmartDashboard.putNumber("PoseX", pose.getX());
        SmartDashboard.putNumber("PoseY", pose.getY());

        // Motor stats
        SmartDashboard.putNumber("lMotor1Output", m_leftMotor1.getAppliedOutput());
        SmartDashboard.putNumber("lMotor1Voltage", m_leftMotor1.getBusVoltage());
        SmartDashboard.putNumber("lMotor1Faults", (double) m_leftMotor1.getFaults());
        SmartDashboard.putNumber("lMotor1Temp", m_leftMotor1.getMotorTemperature());
        SmartDashboard.putNumber("lMotor1Current", m_leftMotor1.getOutputCurrent());
        SmartDashboard.putNumber("lMotor2Output", m_leftMotor2.getAppliedOutput());
        SmartDashboard.putNumber("lMotor2Voltage", m_leftMotor2.getBusVoltage());
        SmartDashboard.putNumber("lMotor2Faults", (double) m_leftMotor2.getFaults());
        SmartDashboard.putNumber("lMotor2Temp", m_leftMotor2.getMotorTemperature());
        SmartDashboard.putNumber("lMotor2Current", m_leftMotor2.getOutputCurrent());

        SmartDashboard.putNumber("rMotor1Output", m_rightMotor1.getAppliedOutput());
        SmartDashboard.putNumber("rMotor1Voltage", m_rightMotor1.getBusVoltage());
        SmartDashboard.putNumber("rMotor1Faults", (double) m_rightMotor1.getFaults());
        SmartDashboard.putNumber("rMotor1Temp", m_rightMotor1.getMotorTemperature());
        SmartDashboard.putNumber("rMotor1Current", m_rightMotor1.getOutputCurrent());
        SmartDashboard.putNumber("rMotor2Output", m_rightMotor2.getAppliedOutput());
        SmartDashboard.putNumber("rMotor2Voltage", m_rightMotor2.getBusVoltage());
        SmartDashboard.putNumber("rMotor2Faults", (double) m_rightMotor2.getFaults());
        SmartDashboard.putNumber("rMotor2Temp", m_rightMotor2.getMotorTemperature());
        SmartDashboard.putNumber("rMotor2Current", m_rightMotor2.getOutputCurrent());

    }
}
