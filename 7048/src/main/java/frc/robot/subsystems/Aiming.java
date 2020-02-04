package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.RobotContainer;
import frc.robot.commands.StaticAim;
import frc.robot.reuse.hardware.sensors.GyroNavx;

// This is the way we brush our teeth, brush our teeth, brush our teeth

public class Aiming extends SubsystemBase {

    // Create drive component objects
    private CANSparkMax rotationControl;
    private GyroNavx navx;

    private RobotContainer container;
    private CANSparkMax elevationControl;

    public Aiming(RobotContainer container) {
        this.container = container;

        // creates gyro
        navx = new GyroNavx();

        // Create motors
        rotationControl = new CANSparkMax(9, MotorType.kBrushless);
        elevationControl = new CANSparkMax(9, MotorType.kBrushless);

        setDefaultCommand(new StaticAim(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    public void aim() {
        SmartDashboard.putString("Aiming", "On");

        // robotDrive.tankDrive(Robot.oi.driver.getRawAxis(1)* -1,
        // Robot.oi.driver.getRawAxis(5)* -1, true);
        // System.out.println(Robot.oi.driver.getY());
        // \System.out.println(Robot.oi.driver.getX());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }
    
    public void moveElevation(double speed){
        elevationControl.set(speed);
    }

    public void moveRotation(double speed){
        rotationControl.set(speed);
    }
}
