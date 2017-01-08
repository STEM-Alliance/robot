package org.wfrobotics.subsystems;

import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;

/**
 *
 */
public class MecanumDriveSubsystem extends DriveSubsystem {
    
    private RobotDrive robotDrive;
    private CANTalon frontLeft;
    private CANTalon frontRight;
    private CANTalon backLeft;
    private CANTalon backRight;

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public MecanumDriveSubsystem()
    {
        super();
        
        frontLeft = new CANTalon(RobotMap.CAN_MECANUM_TALONS_LEFT[0]);
        frontRight = new CANTalon(RobotMap.CAN_MECANUM_TALONS_RIGHT[0]);
        backLeft = new CANTalon(RobotMap.CAN_MECANUM_TALONS_LEFT[1]);
        backRight = new CANTalon(RobotMap.CAN_MECANUM_TALONS_RIGHT[1]);
        
        robotDrive = new RobotDrive(frontLeft, backLeft, frontRight, backRight);
        robotDrive.setInvertedMotor(MotorType.kFrontLeft, false);    // invert the left side motors
        robotDrive.setInvertedMotor(MotorType.kRearLeft, false);     // you may need to change or remove this to match your robot
        robotDrive.setInvertedMotor(MotorType.kFrontRight, true);    // invert the left side motors
        robotDrive.setInvertedMotor(MotorType.kRearRight, true);     // you may need to change or remove this to match your robot
        robotDrive.setExpiration(0.1);
   
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
        setDefaultCommand(new DriveMecanum());
    }

    @Override
    public void driveTank(double right, double left)
    {
        //TODO?
    }

    @Override
    public void driveVector(double magnitude, double angle, double rotation)
    {
        if(m_fieldRelative)
            robotDrive.mecanumDrive_Polar(magnitude, m_gyro.getYaw() - angle, rotation);
        else
            robotDrive.mecanumDrive_Polar(magnitude, angle, rotation);
    }

    @Override
    public void driveVector(Vector velocity, double rotation)
    {
        driveVector(velocity.getMag(), velocity.getAngle(), rotation);
    }

    @Override
    public void driveXY(double x, double y, double rotation)
    {
        if(m_fieldRelative)
            robotDrive.mecanumDrive_Cartesian(x, y, rotation, m_gyro.getYaw());
        else
            robotDrive.mecanumDrive_Cartesian(x, y, rotation, 0);
            
    }
}

