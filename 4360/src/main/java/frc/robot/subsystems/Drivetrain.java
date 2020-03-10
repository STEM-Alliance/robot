package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.Constants;
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
        left2 = new WPI_VictorSPX(13);
        right1 = new WPI_TalonSRX(10);
        right2 = new WPI_VictorSPX(15);
        
        // Clear any residual bad values
   left2.follow(left1);
right2.follow(right1);
right2.setInverted(true);
        // Set master-slave bindings
    
       
        
        // Initialize the WPI drivetrain object with our motors
        robotDrive = new DifferentialDrive(left1, right1); // only need to set the masters
        // Set some values for the drivetrain control
        robotDrive.setSafetyEnabled(true); // enable motor safety
        robotDrive.setExpiration(0.1); // timeout for motor safety checks
        robotDrive.setMaxOutput(1); // default is FULL SEND
        
        robotDrive.setRightSideInverted(true); // maybe will need to do this
        
        robotDrive.setDeadband(0.06);

        setDefaultCommand(new Driving(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    public void driveeeee() {
        robotDrive.arcadeDrive(container.xbox.getRawAxis(4)*0.85 , container.xbox.getRawAxis(1)*0.85 , true);
   //robotDrive.arcadeDrive(container.go.getY(), container.turn.getX(), true);
    // robotDrive.arcadeDrive(container.xbox.getRawAxis(1) * 1, container.go.getX() * -1, true);


      //  robotDrive.tankDrive(container.xbox.getRawAxis(1)* -1,container.xbox.getRawAxis(5)* -1, true);
        // System.out.println(Robot.oi.driver.getY());
        // \System.out.println(Robot.oi.driver.getX());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }
	public void setBrake(boolean b) {
      
	}

public void auto(double speed){
    left1.set(ControlMode.PercentOutput, speed);
    right1.set(ControlMode.PercentOutput, speed);
}

}
