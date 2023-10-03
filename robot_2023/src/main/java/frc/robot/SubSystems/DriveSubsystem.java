//package edu.wpi.first.wpilibj.examples.ramsetecommand.subsystems;

package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.kauailabs.navx.frc.*;
import edu.wpi.first.wpilibj.DataLogManager;

import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.*;

public class DriveSubsystem extends SubsystemBase {
    private CANSparkMax m_leftMotor;
    private CANSparkMax m_leftMotorFollower;

    private CANSparkMax m_rightMotor;
    private CANSparkMax m_rightMotorFollower;

    // The robot's drive
    private DifferentialDrive m_drive;

    // Slew rate controllers to prevent the robot from going from 0 to infinity and vice versa
    private SlewRateLimiter m_forwardLimiter;
    private SlewRateLimiter m_turnLimiter;

    // The gyro sensor
    private AHRS m_ahrs;

    RelativeEncoder m_leftEncoder;
    RelativeEncoder m_rightEncoder;

    double m_fwd;
    double m_rot;
    double m_fwdFiltered;
    double m_rotFiltered;

    // Odometry class for tracking robot pose
    private final DifferentialDriveOdometry m_odometry;

    private final Field2d m_field = new Field2d();

    LogPlayback m_playback = new LogPlayback("logs/FRC_20230208_015431.csv");

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem(int leftMotor1, int leftMotor2, int rightMotor1, int rightMotor2) {
        m_leftMotor = new CANSparkMax(leftMotor1, MotorType.kBrushless);
        m_leftMotorFollower = new CANSparkMax(leftMotor2, MotorType.kBrushless);

        m_rightMotor = new CANSparkMax(rightMotor1, MotorType.kBrushless);
        m_rightMotorFollower = new CANSparkMax(rightMotor2, MotorType.kBrushless);

        // The robot's drive
        m_drive = new DifferentialDrive(m_leftMotor, m_rightMotor);

        m_leftEncoder = m_leftMotor.getEncoder();
        m_rightEncoder = m_rightMotor.getEncoder();

        m_leftMotor.restoreFactoryDefaults();
        m_leftMotorFollower.restoreFactoryDefaults();
        m_rightMotor.restoreFactoryDefaults();
        m_rightMotorFollower.restoreFactoryDefaults();

        m_leftMotorFollower.follow(m_leftMotor);
        m_rightMotorFollower.follow(m_rightMotor);
        

        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        m_leftMotor.setInverted(true);
        m_rightMotor.setInverted(false);

        /*
         * 120 rotations to go 20 feet on the left
         * 120 rotations to go 20 feet on the right
         * This doesn't make any sesne. So lets say it is 127 rotations to go 20 feet
         * 20 feet is 6.096 meters
         * 6.096 meters / 120 rotations = 0.0508 meters per rotation
         * Rotations/minute * 0.0508 meters / rotation * 1 minute / 60 seconds =
         */
        m_leftEncoder.setPositionConversionFactor(Configuration.MetersPerRotation);
        m_rightEncoder.setPositionConversionFactor(Configuration.MetersPerRotation);
        m_leftEncoder.setVelocityConversionFactor(Configuration.MetersPerRotation / 60);
        m_rightEncoder.setVelocityConversionFactor(Configuration.MetersPerRotation / 60);

        m_forwardLimiter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
        m_turnLimiter = new SlewRateLimiter(Configuration.right_left_slew_rate);

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

        m_ahrs.reset();
        m_ahrs.calibrate();
        resetEncoders();

        m_odometry = new DifferentialDriveOdometry(m_ahrs.getRotation2d(), 0, 0);

        SmartDashboard.putData("Field", m_field);
        //DataLogManager.start();
    }

    @Override

    public void periodic() {
        var lDistance = m_leftEncoder.getPosition();
        var rDistance = m_rightEncoder.getPosition();

        // m_odometry.update(
        // Rotation2d.fromDegrees(-m_ahrs.getYaw()), lDistance, rDistance);
        if (Configuration.Simulate) {
            m_odometry.update(Rotation2d.fromDegrees(m_playback.getHeading()), m_playback.getLPos(), m_playback.getRPos());
            m_playback.stepSim();
        } else {
            m_odometry.update(m_ahrs.getRotation2d(), lDistance, rDistance);
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
        var leftMetersPerSecond = m_leftEncoder.getVelocity();
        var rightMetersPerSecond = m_rightEncoder.getVelocity();


        var speed = new DifferentialDriveWheelSpeeds(leftMetersPerSecond, rightMetersPerSecond);
        if (Configuration.Simulate)
            speed = new DifferentialDriveWheelSpeeds(m_playback.getLVel(), m_playback.getRVel());
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
        m_fwd = fwd;
        m_rot = rot;
        m_fwdFiltered = m_forwardLimiter.calculate(fwd);
        m_rotFiltered = m_turnLimiter.calculate(rot);
        m_drive.arcadeDrive(m_fwdFiltered, m_rotFiltered * 0.75);
        System.out.println("arcadeDrive");
        periodic();
        sendStats();
    }

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
            return Rotation2d.fromRadians(m_playback.getHeading());
        return m_ahrs.getRotation2d();
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
        SmartDashboard.putNumber("CmdSpdLMPS", leftSpeed);
        SmartDashboard.putNumber("CmdSpdRMPS", rightSpeed);

        // The speed values are in meters per second. Convert to -1 to +1
        // TODO: Need to figure this out
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
        // SmartDashboard.putNumber("LeftMotorCmd", leftSpeed);
        // SmartDashboard.putNumber("RightMotorCmd", rightSpeed);
        m_drive.tankDrive(leftSpeed, rightSpeed);

        sendStats();
    }

    public void stop() {
        System.out.println("Stopping robot");
        m_drive.tankDrive(0, 0);
    }

    public void sendStats() {
        /*
         * This is all for telemetry and data logging.
         * If you want to put a value on the dashboard, put it here.
         * This data will also be logged to a log file
         */
        // SmartDashboard.putNumber("Heading", getHeading().getDegrees());
        // SmartDashboard.putNumber("Yaw", m_ahrs.getYaw());
        // SmartDashboard.putNumber("FusedHeading", m_ahrs.getFusedHeading());

        // SmartDashboard.putNumber("CmdL", m_leftMotor.get());
        // SmartDashboard.putNumber("CmdR", m_rightMotor.get());
        // SmartDashboard.putNumber("EncL", m_leftEncoder.getPosition()); // These are in rotations
        // SmartDashboard.putNumber("EncR", m_rightEncoder.getPosition());
        // SmartDashboard.putNumber("VelL", m_leftEncoder.getVelocity());
        // SmartDashboard.putNumber("VelR", m_rightEncoder.getVelocity());

        // var pose = getPose();
        // SmartDashboard.putNumber("PoseX", pose.getX());
        // SmartDashboard.putNumber("PoseY", pose.getY());

        // // Motor stats
        // SmartDashboard.putNumber("Motor1OutputL", m_leftMotor.getAppliedOutput());
        // SmartDashboard.putNumber("Motor1VoltageL", m_leftMotor.getBusVoltage());
        // SmartDashboard.putNumber("Motor1FaultsL", (double) m_leftMotor.getFaults());
        // SmartDashboard.putNumber("Motor1TempL", m_leftMotor.getMotorTemperature());
        // SmartDashboard.putNumber("Motor1CurrentL", m_leftMotor.getOutputCurrent());
        // SmartDashboard.putNumber("Motor2OutputL", m_leftMotorFollower.getAppliedOutput());
        // SmartDashboard.putNumber("Motor2VoltageL", m_leftMotorFollower.getBusVoltage());
        // SmartDashboard.putNumber("Motor2FaultsL", (double) m_leftMotorFollower.getFaults());
        // SmartDashboard.putNumber("Motor2TempL", m_leftMotorFollower.getMotorTemperature());
        // SmartDashboard.putNumber("Motor2CurrentL", m_leftMotorFollower.getOutputCurrent());

        // SmartDashboard.putNumber("Motor1OutputR", m_rightMotor.getAppliedOutput());
        // SmartDashboard.putNumber("Motor1VoltageR", m_rightMotor.getBusVoltage());
        // SmartDashboard.putNumber("Motor1FaultsR", (double) m_rightMotor.getFaults());
        // SmartDashboard.putNumber("Motor1TempR", m_rightMotor.getMotorTemperature());
        // SmartDashboard.putNumber("Motor1CurrentR", m_rightMotor.getOutputCurrent());
        // SmartDashboard.putNumber("Motor2OutputR", m_rightMotorFollower.getAppliedOutput());
        // SmartDashboard.putNumber("Motor2VoltageR", m_rightMotorFollower.getBusVoltage());
        // SmartDashboard.putNumber("Motor2FaultsR", (double) m_rightMotorFollower.getFaults());
        // SmartDashboard.putNumber("Motor2TempR", m_rightMotorFollower.getMotorTemperature());
        // SmartDashboard.putNumber("Motor2CurrentR", m_rightMotorFollower.getOutputCurrent());

        // SmartDashboard.putNumber("JoyFwd", m_fwd);
        // SmartDashboard.putNumber("JoyRot", m_rot);
        // SmartDashboard.putNumber("JoyFwdFiltered", m_fwdFiltered);
        // SmartDashboard.putNumber("JoyRotFiltered", m_rotFiltered);

        // In order to simulate things, it is much easier if all of the data we need is on a single line. So this is duplication, but makes life easier
        double fullCapture[] = new double[30];
        fullCapture[0]  = m_leftEncoder.getPosition();
        fullCapture[1]  = m_rightEncoder.getPosition();
        fullCapture[2]  = m_leftEncoder.getVelocity();
        fullCapture[3]  = m_rightEncoder.getVelocity();
        fullCapture[4]  = m_ahrs.getFusedHeading();
        fullCapture[5]  = m_ahrs.getYaw();
        fullCapture[6]  = m_ahrs.getYaw();
        fullCapture[7]  = m_ahrs.getPitch();
        fullCapture[8]  = m_ahrs.getRoll();
        fullCapture[9]  = m_ahrs.getAngle();
        fullCapture[10] = m_ahrs.getCompassHeading();
        fullCapture[11] = m_ahrs.getRawAccelX();
        fullCapture[12] = m_ahrs.getRawAccelY();
        fullCapture[13] = m_ahrs.getRawAccelZ();
        fullCapture[14] = m_ahrs.getRawGyroX();
        fullCapture[15] = m_ahrs.getRawGyroY();
        fullCapture[16] = m_ahrs.getRawGyroZ();
        fullCapture[17] = m_ahrs.getRawMagX();
        fullCapture[18] = m_ahrs.getRawMagY();
        fullCapture[19] = m_ahrs.getRawMagZ();
        fullCapture[20] = m_ahrs.getRotation2d().getDegrees();
        fullCapture[21] = m_odometry.getPoseMeters().getX();
        fullCapture[22] = m_odometry.getPoseMeters().getY();
        fullCapture[23] = m_odometry.getPoseMeters().getRotation().getDegrees();
        fullCapture[24] = m_leftMotor.get();
        fullCapture[25] = m_rightMotor.get();
        SmartDashboard.putNumberArray("FullData", fullCapture);
        
    }
}
