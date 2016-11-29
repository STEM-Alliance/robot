package org.wfrobotics.subsystems;

import org.wfrobotics.commands.drive.DriveMecanum;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class MecanumDriveSubsystem extends Subsystem {
    
    private RobotDrive robotDrive;
    private CANTalon frontLeft;
    private CANTalon frontRight;
    private CANTalon backLeft;
    private CANTalon backRight;

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public MecanumDriveSubsystem()
    {
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
    
    public void drive(double x, double y, double rotation)
    {
        robotDrive.mecanumDrive_Cartesian(x, y, rotation, 0);
   
    }
}

