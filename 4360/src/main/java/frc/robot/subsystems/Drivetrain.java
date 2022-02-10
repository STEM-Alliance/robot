package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.RobotContainer;
import frc.robot.commands.Driving;
import frc.robot.reuse.hardware.sensors.GyroNavx;

// This is the way we brush our teeth, brush our teeth, brush our teeth

public class Drivetrain extends SubsystemBase {

    // Create drive component objects
    private WPI_TalonSRX left1;
    private WPI_VictorSPX left2;
    private WPI_TalonSRX right1;
    private WPI_VictorSPX right2;
    public DifferentialDrive robotDrive;
    private GyroNavx navx;

    private RobotContainer container;

    public Drivetrain(RobotContainer container) {
        this.container = container;

        // creates gyro
        navx = new GyroNavx();

        // Create motors
        left1 = new WPI_TalonSRX(11);
        left2 = new WPI_VictorSPX(12);
        right1 = new WPI_TalonSRX(14);
        right2 = new WPI_VictorSPX(13);
        
        // Clear any residual bad values
   

        // Set master-slave bindings
        left2.follow(left1);
        right2.follow(right1);

        // Initialize the WPI drivetrain object with our motors
        robotDrive = new DifferentialDrive(left1, right1); // only need to set the masters
        // Set some values for the drivetrain control
        robotDrive.setSafetyEnabled(true); // enable motor safety
        robotDrive.setExpiration(0.1); // timeout for motor safety checks
        robotDrive.setMaxOutput(.8); // default is FULL SEND
        // TODO: This function doesn't exist. Do we need to invert one side?
        //robotDrive.setRightSideInverted(true); // maybe will need to do this
        robotDrive.setDeadband(0.06);

        setDefaultCommand(new Driving(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    public void driveeeee() {
    robotDrive.arcadeDrive(container.xbox.getRawAxis(1) *.70, container.xbox.getRawAxis(4) * -.90, true);
     //robotDrive.arcadeDrive(container.xbox.getRawAxis(1) * 1, container.go.getX() * -1, true);
        SmartDashboard.putNumber("left", container.xbox.getRawAxis(1));
        SmartDashboard.putNumber("rickwr", container.xbox.getRawAxis(5));

         //robotDrive.tankDrive(Robot.oi.driver.getRawAxis(1)* -1,Robot.oi.driver.getRawAxis(5)* -1, true);
        // System.out.println(Robot.oi.driver.getY());
        // \System.out.println(Robot.oi.driver.getX());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }
	public void setBrake(boolean b) {
      
	}

}
