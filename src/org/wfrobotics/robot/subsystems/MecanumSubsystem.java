package org.wfrobotics.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.commands.DriveMecanum;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.*;

/**
 *
 */
public class MecanumSubsystem extends Subsystem {
    MecanumDrive drive;
    WPI_TalonSRX frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    public Gyro gyro;
    
    public MecanumSubsystem()
    {
        this.frontLeftMotor = new WPI_TalonSRX(10);
        this.rearLeftMotor = new WPI_TalonSRX(11);
        this.frontRightMotor = new WPI_TalonSRX(12);
        this.rearRightMotor = new WPI_TalonSRX(13);
        this.gyro = Gyro.getInstance();

        this.rearLeftMotor.setInverted(true);
        this.frontLeftMotor.setInverted(true);
        drive = new MecanumDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        double rampRate = .5; //TODO is this right? seconds from neutral to full
        frontRightMotor.configOpenloopRamp(rampRate, 0);
        frontLeftMotor.configOpenloopRamp(rampRate, 0);
        rearRightMotor.configOpenloopRamp(rampRate, 0);
        rearLeftMotor.configOpenloopRamp(rampRate, 0);
    }
    public void driveWithHeading(HerdVector vector, double rotation)
    {
        drive.drivePolar(
                vector.getMag(),
                vector.getAngle() - this.gyro.getYaw(),
                rotation);
    }

    public void drive(HerdVector vector, double rotation)
    {
        drive.drivePolar(
                vector.getMag(),
                vector.getAngle(),
                rotation);
    }
    

    public void initDefaultCommand() {
        
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveMecanum());
    }
}

