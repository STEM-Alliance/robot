package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Config.RobotContainer;
import frc.robot.commands.Driving;
import frc.robot.reuse.hardware.sensors.GyroNavx;

// This is the way we brush our teeth, brush our teeth, brush our teeth

public class DriveMotion extends SubsystemBase {

    // Create drive component objects
    private WPI_TalonSRX left1;
    private WPI_TalonSRX right1;
    private WPI_TalonSRX left2;
    private WPI_TalonSRX right2;
    public DifferentialDrive robotDrive;
    private GyroNavx navx;
    private int destination;

    private RobotContainer container;

    public DriveMotion(RobotContainer container) {
        this.container = container;

        // creates gyro
        navx = new GyroNavx();

        // Create motors
        left1 = new WPI_TalonSRX(12);
        right1 = new WPI_TalonSRX(14);
        left1.setSelectedSensorPosition(0);
        right1.setSelectedSensorPosition(0);
        left1.setInverted(true);
        // left2 = new WPI_TalonSRX(11);
        // right2 = new WPI_TalonSRX(15);
        // left2.follow(left1);
        // right2.follow(right1);
        // right1.setInverted(false);
        
        // Clear any residual bad values
   

        // Set master-slave bindings

        // Initialize the WPI drivetrain object with our motors
        robotDrive = new DifferentialDrive(left1, right1); // only need to set the masters
        // Set some values for the drivetrain control
        robotDrive.setSafetyEnabled(true); // enable motor safety
        robotDrive.setExpiration(0.1); // timeout for motor safety checks
        robotDrive.setMaxOutput(.8); // default is FULL SEND
        robotDrive.setRightSideInverted(true); // maybe will need to do this
        robotDrive.setDeadband(0.06);

        // setDefaultCommand(new Driving(this));
    }
    public double getAngle()
    {
        return navx.getAngle();
    }

    // public void driveeeee() {
    //     robotDrive.arcadeDrive(container.xbox.getRawAxis(1) * -1, container.xbox.getRawAxis(4), true);
    //     SmartDashboard.putNumber("x", container.xbox.getRawAxis(1));
    //     SmartDashboard.putNumber("y", container.xbox.getRawAxis(4));

    //     // robotDrive.tankDrive(Robot.oi.driver.getRawAxis(1)* -1,
    //     // Robot.oi.driver.getRawAxis(5)* -1, true);
    //     // System.out.println(Robot.oi.driver.getY());
    //     // \System.out.println(Robot.oi.driver.getX());
    // }

    public void goTo(int ticks){
        int left = left1.getSelectedSensorPosition();
        // int right = right1.getSelectedSensorPosition();
        SmartDashboard.putNumber("Left ticks", left);
        // SmartDashboard.putNumber("Right ticks", right);
        // int sign = (int) Math.signum(left);
        destination = Math.abs(left) + ticks;
        SmartDashboard.putNumber("Destination", destination);
        // right1.follow(left1, FollowerType.AuxOutput1);
        left1.set(ControlMode.MotionMagic, ticks);
        // right1.set(ControlMode.MotionMagic, (right + ticks));
        // left1.set(ControlMode.MotionMagic, 1, DemandType.AuxPID, 0);
    }

    public void stop() {
        left1.set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void periodic() {
        if (null != CommandScheduler.getInstance() && null != getCurrentCommand()) {
            SmartDashboard.putString("Current DriveMotion command", this.getCurrentCommand().getName());
        }
        // Put code here to be run every loop

    }
	public void setBrake(boolean b) {
      
    }
    
    public boolean isThere(double percent) {
        SmartDashboard.putNumber("Current", left1.getSelectedSensorPosition());
        SmartDashboard.putNumber("Current Dest", destination);
        SmartDashboard.putBoolean("isThere", Math.abs(destination) <= Math.abs(left1.getSelectedSensorPosition()));
        return Math.abs(destination) <= Math.abs(left1.getSelectedSensorPosition());
    }

}
