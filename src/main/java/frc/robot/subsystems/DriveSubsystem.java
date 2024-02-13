//package edu.wpi.first.wpilibj.examples.ramsetecommand.subsystems;

package frc.robot.subsystems;

import javax.naming.ldap.LdapContext;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.*;
import com.revrobotics.CANSparkLowLevel.*;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configuration;
import frc.robot.LoggedNumber;
import frc.robot.commands.AimbotCommand;

public class DriveSubsystem extends SubsystemBase {
    private CANSparkMax m_leftMotor;
    private CANSparkMax m_leftMotorFollower;

    private CANSparkMax m_rightMotor;
    private CANSparkMax m_rightMotorFollower;

    private CANSparkMax m_hdrive;

    // The robot's drive
    private DifferentialDrive m_drive;

    // The gyro sensor
    private AHRS m_ahrs;

    RelativeEncoder m_leftEncoder;
    RelativeEncoder m_rightEncoder;

    double m_fwd;
    double m_rot;
    double m_fwdFiltered;
    double m_rotFiltered;
    double m_hDriveFiltered;

    double m_lMotorSP = 0;
    double m_rMotorSP = 0;

    boolean m_enableTurbo = false;
    boolean m_autoDriveDone = false;

    PIDController m_lPID = new PIDController(2.2, 0, 0);
    PIDController m_rPID = new PIDController(2.2, 0, 0);
    // This works ok, but it is a little slow
    //PIDController m_levelPID = new PIDController(0.02, 0.001, 0);
    PIDController m_levelPID = new PIDController(0.02, 0.002, 0);

    double m_desiredDistance;
    double m_currentEnc;

    // Odometry class for tracking robot pose
    private final DifferentialDriveOdometry m_odometry;

    private final Field2d m_field = new Field2d();

    /** Creates a new DriveSubsystem. */
    public DriveSubsystem(int leftMotor1, int leftMotor2, int rightMotor1, int rightMotor2, int hdriveID) {
        m_leftMotor = new CANSparkMax(leftMotor1, MotorType.kBrushless);
        m_leftMotorFollower = new CANSparkMax(leftMotor2, MotorType.kBrushless);

        m_rightMotor = new CANSparkMax(rightMotor1, MotorType.kBrushless);
        m_rightMotorFollower = new CANSparkMax(rightMotor2, MotorType.kBrushless);

        m_hdrive = new CANSparkMax(hdriveID, MotorType.kBrushless);

        // The robot's drive
        m_drive = new DifferentialDrive(m_leftMotor, m_rightMotor);

        m_leftEncoder = m_leftMotor.getEncoder();
        m_rightEncoder = m_rightMotor.getEncoder();

        m_leftMotor.restoreFactoryDefaults();
        m_leftMotorFollower.restoreFactoryDefaults();
        m_rightMotor.restoreFactoryDefaults();
        m_rightMotorFollower.restoreFactoryDefaults();

        m_hdrive.restoreFactoryDefaults();
        m_hdrive.setSmartCurrentLimit(Configuration.NeoLimit);


        m_leftMotorFollower.follow(m_leftMotor);
        m_rightMotorFollower.follow(m_rightMotor);

        setBrakeMode();

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

        // m_forwardLimiter = new SlewRateLimiter(Configuration.forward_back_slew_rate);
        // m_turnLimiter = new SlewRateLimiter(Configuration.right_left_slew_rate);

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
        //m_ahrs.calibrate();
        resetEncoders();

        m_odometry = new DifferentialDriveOdometry(m_ahrs.getRotation2d(), 0, 0);

        SmartDashboard.putData("Field", m_field);
    }

    @Override

    public void periodic() {
        var lDistance = m_leftEncoder.getPosition();
        var rDistance = m_rightEncoder.getPosition();

        LoggedNumber.getInstance().logNumber(lDistance, "lDistance");
        LoggedNumber.getInstance().logNumber(rDistance, "rDistance");

        m_odometry.update(m_ahrs.getRotation2d(), lDistance, rDistance);
        m_field.setRobotPose(m_odometry.getPoseMeters());
        saveStats();
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
        LoggedNumber.getInstance().logNumber(leftMetersPerSecond, "leftMetersPerSecond");
        LoggedNumber.getInstance().logNumber(rightMetersPerSecond, "rightMetersPerSecond");

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

    public double exponential_scaling(double base, double exponent)
    {
        if (base > 0)
        {
            return Math.pow(Math.abs(base), exponent);
        }
        return -Math.pow(Math.abs(base), exponent);
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */

    
    public void arcadeDrive(double fwd, double rot, double hDriveCmd) {
        m_fwd = fwd;
        m_rot = rot;
        

        //System.out.println("arcade drive being called");

        if (Configuration.EnableExpoControl)
        {
            m_fwdFiltered = exponential_scaling(m_fwd , Configuration.ExpControl);
            m_rotFiltered = exponential_scaling(m_rot, Configuration.ExpControl);
        }
        else
        {
            m_fwdFiltered = m_fwd;
            m_rotFiltered = m_rot;
        }

        LoggedNumber.getInstance().logNumber(m_fwdFiltered, "m_fwdFiltered");
        LoggedNumber.getInstance().logNumber(m_rotFiltered, "m_rotFiltered");

        if (!m_enableTurbo)
        {
            m_fwdFiltered = m_fwdFiltered * Configuration.FwdRevMaxLimit;
        }
        m_rotFiltered = m_rotFiltered * Configuration.TurnMaxLimit;

        if ((Math.abs(m_fwdFiltered) < 0.2) && (Math.abs(m_rotFiltered) < 0.2))
        {
            m_drive.arcadeDrive(0, 0);
        }
        else
        {
            m_drive.arcadeDrive(m_fwdFiltered, m_rotFiltered);
        }

        if (Math.abs(hDriveCmd) > 0.2)
        {
            m_hDriveFiltered = exponential_scaling(hDriveCmd, Configuration.ExpControl);
            m_hdrive.set(m_hDriveFiltered);
            // psi.deployHDrive();
        }
        else
        {
            // psi.retractHDrive();
            m_hdrive.set(0);
        }

        //periodic();
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
        m_drive.tankDrive(leftSpeed, rightSpeed);
    }

    public void stop() {
        System.out.println("Stopping robot");
        m_drive.tankDrive(0, 0);
    }

    public void setBrakeMode()
    {
        m_leftMotor.setIdleMode(IdleMode.kCoast);
        m_rightMotor.setIdleMode(IdleMode.kCoast);
        m_leftMotorFollower.setIdleMode(IdleMode.kCoast);
        m_rightMotorFollower.setIdleMode(IdleMode.kCoast);
        System.out.println("Enabling Brake mode");
        LoggedNumber.getInstance().logNumber(1.0, "BrakeEnabled");
}

    public void setCoastMode()
    {
        m_leftMotor.setIdleMode(IdleMode.kCoast);
        m_rightMotor.setIdleMode(IdleMode.kCoast);
        m_leftMotorFollower.setIdleMode(IdleMode.kCoast);
        m_rightMotorFollower.setIdleMode(IdleMode.kCoast);
        System.out.println("Enabling coast mode");
        LoggedNumber.getInstance().logNumber(0.0, "BrakeEnabled");
    }

    public Command setBrakeModeCmd()
    {
        return new InstantCommand(() -> setBrakeMode());
    }

    public Command setCoastModeCmd()
    {
        return new InstantCommand(() -> setCoastMode());
    }

    public Command enableTurbo()
    {
        return new InstantCommand(() -> m_enableTurbo = true);
    }

    public Command disableTurbo()
    {
        return new InstantCommand(() -> m_enableTurbo = false);
    }

    public boolean checkLevel()
    {
        return false;
    }

    public Command captureLevel()
    {
        return new InstantCommand(() -> m_levelPID.setSetpoint(m_ahrs.getPitch()));
    }
    
    public void enableLevel()
    {
        System.out.println("Enable level with pitch: " + m_ahrs.getPitch());
        LoggedNumber.getInstance().logNumber("PitchInitial", m_levelPID.getSetpoint());
    }

    public void runAutoLevel()
    {
        var pidOut = m_levelPID.calculate(m_ahrs.getPitch());
        var output = Math.min(pidOut, Configuration.MaxLevelSpeed);
        output = Math.max(output, -Configuration.MaxLevelSpeed);
        m_drive.arcadeDrive(output, 0);
        System.out.println("Init: " + m_levelPID.getSetpoint() + " Leveling " + m_ahrs.getPitch() + " PID out: " + output);
        LoggedNumber.getInstance().logNumber("LevelPID", pidOut);
        LoggedNumber.getInstance().logNumber("LevelPID2", output);
    }

    public Command autoLevel()
    {
        return new FunctionalCommand(() -> enableLevel(), () -> runAutoLevel(), interrupted -> {}, () -> checkLevel());
    }

    public void autoInitDrive(double distance)
    {
        m_autoDriveDone = false;
        m_desiredDistance = distance;
        m_lMotorSP = distance + m_leftEncoder.getPosition();
        m_rMotorSP = distance + m_rightEncoder.getPosition();
        m_lPID.setSetpoint(m_lMotorSP);
        m_rPID.setSetpoint(m_rMotorSP);
        LoggedNumber.getInstance().logNumber(m_lMotorSP, "lPIDSetPoint");
        LoggedNumber.getInstance().logNumber(m_rMotorSP, "rPIDSetPoint");
        System.out.println("autoInitDrive " + distance);
    }

    public void autoDriveExecute()
    {
        double doneTolerance = 0.1;
        var lDrive = m_lPID.calculate(m_leftEncoder.getPosition());
        var rDrive = m_rPID.calculate(m_rightEncoder.getPosition());
        LoggedNumber.getInstance().logNumber(lDrive, "lPIDError");
        LoggedNumber.getInstance().logNumber(rDrive, "rPIDError");

        if ((Math.abs(lDrive) < doneTolerance) &&
            (Math.abs(rDrive) < doneTolerance))
        {
            LoggedNumber.getInstance().logNumber(1.0, "AtDriveGoal");
            System.out.println("At the correct position, stopping");
            stop();
            m_autoDriveDone = true;
        }
        else
        {
            LoggedNumber.getInstance().logNumber(0.0, "AtDriveGoal");
            lDrive = Math.max(lDrive, Configuration.MaxAutoSpeed);
            rDrive = Math.max(rDrive, Configuration.MaxAutoSpeed);
            lDrive = Math.min(lDrive, -Configuration.MaxAutoSpeed);
            rDrive = Math.min(rDrive, -Configuration.MaxAutoSpeed);
            double fwd = (lDrive + rDrive) / 2;
            System.out.println("L/R drive: " + lDrive + "/" + rDrive + " enc: " + m_leftEncoder.getPosition() + "/" + m_rightEncoder.getPosition());
            //m_leftMotor.set(lDrive);
            //m_rightMotor.set(rDrive);
            m_drive.arcadeDrive(fwd, 0);
        }
    }

    public boolean autoDriveDone()
    {
        return m_autoDriveDone;
    }

    public Command driveDistance(double distance)
    {
        return new FunctionalCommand(() -> autoInitDrive(distance), () -> autoDriveExecute(), interrupted -> {}, () -> autoDriveDone(), this);
    }

    public void saveStats() {
        /*
         * This is all for telemetry and data logging.
         * If you want to put a value on the dashboard, put it here.
         * This data will also be logged to a log file
         */
        LoggedNumber.getInstance().logNumber(getHeading().getDegrees(), "Heading");
        LoggedNumber.getInstance().logNumber(m_ahrs.getYaw(), "Yaw");
        LoggedNumber.getInstance().logNumber(m_ahrs.getFusedHeading(), "FusedHeading");
        LoggedNumber.getInstance().logNumber(m_ahrs.getPitch(), "Pitch");

        // Motor stats
        LoggedNumber.getInstance().logNumber("Motor1OutputL", m_leftMotor.getAppliedOutput());
        LoggedNumber.getInstance().logNumber("Motor1TempL", m_leftMotor.getMotorTemperature());
        LoggedNumber.getInstance().logNumber("Motor1CurrentL", m_leftMotor.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("Motor1VoltageL", m_leftMotor.getBusVoltage());
        LoggedNumber.getInstance().logNumber("Motor1FaultsL", (double) m_leftMotor.getFaults());
        LoggedNumber.getInstance().logNumber("Motor2OutputL", m_leftMotorFollower.getAppliedOutput());
        LoggedNumber.getInstance().logNumber("Motor2TempL", m_leftMotorFollower.getMotorTemperature());
        LoggedNumber.getInstance().logNumber("Motor2CurrentL", m_leftMotorFollower.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("Motor2VoltageL", m_leftMotorFollower.getBusVoltage());
        LoggedNumber.getInstance().logNumber("Motor2FaultsL", (double) m_leftMotorFollower.getFaults());

        LoggedNumber.getInstance().logNumber("Motor1OutputR", m_rightMotor.getAppliedOutput());
        LoggedNumber.getInstance().logNumber("Motor1TempR", m_rightMotor.getMotorTemperature());
        LoggedNumber.getInstance().logNumber("Motor1CurrentR", m_rightMotor.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("Motor1VoltageR", m_rightMotor.getBusVoltage());
        LoggedNumber.getInstance().logNumber("Motor1FaultsR", (double) m_rightMotor.getFaults());
        LoggedNumber.getInstance().logNumber("Motor2OutputR", m_rightMotorFollower.getAppliedOutput());
        LoggedNumber.getInstance().logNumber("Motor2TempR", m_rightMotorFollower.getMotorTemperature());
        LoggedNumber.getInstance().logNumber("Motor2CurrentR", m_rightMotorFollower.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("Motor2VoltageR", m_rightMotorFollower.getBusVoltage());
        LoggedNumber.getInstance().logNumber("Motor2FaultsR", (double) m_rightMotorFollower.getFaults());
    }
}
