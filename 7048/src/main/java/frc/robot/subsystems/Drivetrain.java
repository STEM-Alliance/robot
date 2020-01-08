package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.RobotContainer;
import frc.robot.commands.Driving;
import frc.robot.reuse.hardware.sensors.GyroNavx;

// This is the way we brush our teeth, brush our teeth, brush our teeth

public class Drivetrain extends SubsystemBase {

    // Create drive component objects
    private CANSparkMax left1;
    private CANSparkMax left2;
    private CANSparkMax right1;
    private CANSparkMax right2;
    public DifferentialDrive robotDrive;
    private GyroNavx navx;

    private RobotContainer container;

    public Drivetrain(RobotContainer container) {
        this.container = container;

        // creates gyro
        navx = new GyroNavx();

        // Create motors
        left1 = new CANSparkMax(17, MotorType.kBrushless);
        left2 = new CANSparkMax(12, MotorType.kBrushless);
        right1 = new CANSparkMax(13, MotorType.kBrushless);
        right2 = new CANSparkMax(14, MotorType.kBrushless);

        // Clear any residual bad values
        left1.restoreFactoryDefaults();
        left2.restoreFactoryDefaults();
        right1.restoreFactoryDefaults();
        right2.restoreFactoryDefaults();

        left1.setIdleMode(IdleMode.kCoast);
        left2.setIdleMode(IdleMode.kCoast);
        right1.setIdleMode(IdleMode.kCoast);
        right2.setIdleMode(IdleMode.kCoast);

        // Set master-slave bindings
        left2.follow(left1);
        right2.follow(right1);

        // Initialize the WPI drivetrain object with our motors
        robotDrive = new DifferentialDrive(left1, right1); // only need to set the masters
        // Set some values for the drivetrain control
        robotDrive.setSafetyEnabled(true); // enable motor safety
        robotDrive.setExpiration(0.1); // timeout for motor safety checks
        robotDrive.setMaxOutput(.8); // default is FULL SEND
        robotDrive.setRightSideInverted(true); // maybe will need to do this
        robotDrive.setDeadband(0.06);

        setDefaultCommand(new Driving(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    public void driveeeee() {
        robotDrive.arcadeDrive(container.xbox.getRawAxis(1) * -1, container.xbox.getRawAxis(4), true);

        // robotDrive.tankDrive(Robot.oi.driver.getRawAxis(1)* -1,
        // Robot.oi.driver.getRawAxis(5)* -1, true);
        // System.out.println(Robot.oi.driver.getY());
        // \System.out.println(Robot.oi.driver.getX());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }
	public void setBrake(boolean b) {
        left1.setIdleMode(IdleMode.kBrake);
        left2.setIdleMode(IdleMode.kBrake);
        right1.setIdleMode(IdleMode.kBrake);
        right2.setIdleMode(IdleMode.kBrake);
	}

}
