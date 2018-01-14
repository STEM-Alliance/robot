package org.wfrobotics.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.commands.DriveMecanum;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.*;

/**
 *
 */
public class MecanumSubsystem extends Subsystem {
    RobotDrive drive;
    CANTalon frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    public Gyro gyro;
    
    public MecanumSubsystem()
    {
        this.frontLeftMotor = new CANTalon(10);
        this.rearLeftMotor = new CANTalon(11);
        this.frontRightMotor = new CANTalon(12);
        this.rearRightMotor = new CANTalon(13);
        this.gyro = Gyro.getInstance();

        this.rearLeftMotor.setInverted(true);
        this.frontLeftMotor.setInverted(true);
        drive = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        double rampRate = 40;
        frontRightMotor.setVoltageRampRate(rampRate);
        frontLeftMotor.setVoltageRampRate(rampRate);
        rearRightMotor.setVoltageRampRate(rampRate);
        rearLeftMotor.setVoltageRampRate(rampRate);
    }
    public void driveWithHeading(HerdVector vector, double rotation)
    {
        drive.mecanumDrive_Polar(
                vector.getMag(),
                vector.getAngle() - this.gyro.getYaw(),
                rotation);
    }

    public void drive(HerdVector vector, double rotation)
    {
        drive.mecanumDrive_Polar(
                vector.getMag(),
                vector.getAngle(),
                rotation);
    }
    

    public void initDefaultCommand() {
        
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveMecanum());
    }
}

