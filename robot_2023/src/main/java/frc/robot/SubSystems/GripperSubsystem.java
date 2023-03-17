package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj2.command.*;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class GripperSubsystem extends SubsystemBase {
    private final TalonSRX m_lMotor;
    private final TalonSRX m_rMotor;
    private final TalonSRX m_rotateMotor;

    enum GripperState {
        IDLE,
        OPEN,
        CLOSE,
        SLIDE_LEFT,
        SLIDE_RIGHT
    }

    GripperState m_gripperState = GripperState.IDLE;
    private double m_rotateSpeed = 0;

    /** Creates a new DriveSubsystem. */
    public GripperSubsystem(int leftMotorCanID, int rightMotorCanID, int rotateCanID) {
        m_lMotor = new TalonSRX(leftMotorCanID);
        m_rMotor = new TalonSRX(rightMotorCanID);
        m_rotateMotor = new TalonSRX(rotateCanID);
        m_lMotor.configFactoryDefault();
        m_rMotor.configFactoryDefault();
        m_rotateMotor.configFactoryDefault();

        m_lMotor.configPeakCurrentLimit(Configuration.M775ProLimit);
        m_rMotor.configPeakCurrentLimit(Configuration.M775ProLimit);
        m_rotateMotor.configPeakCurrentLimit(Configuration.WindowLimit);

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {
        //System.out.println("Gripper period")
        switch (m_gripperState)
        {
            case IDLE:
                slideGripper(0, 0);
                break;
            case OPEN:
                slideGripper(-Configuration.GripperSlideFast, Configuration.GripperSlideFast);
                break;
            case CLOSE:
                slideGripper(Configuration.GripperSlideFast, -Configuration.GripperSlideFast);
                break;
            case SLIDE_LEFT:
                slideGripper(Configuration.GripperSlideFast, Configuration.GripperSlideSlow);
                break;
            case SLIDE_RIGHT:
                slideGripper(-Configuration.GripperSlideFast, -Configuration.GripperSlideFast);
                break;
        }

        // Control the rotate motor
        m_rotateMotor.set(TalonSRXControlMode.PercentOutput, m_rotateSpeed);
    }

    public void slideGripper(double leftCommand, double rightCommand)
    {
        //System.out.println("slide gripper: " + leftCommand + " : " + rightCommand);
        m_lMotor.set(TalonSRXControlMode.PercentOutput, -rightCommand);
        m_rMotor.set(TalonSRXControlMode.PercentOutput, leftCommand);
    }

    public Command slideLeft()
    {
        return this.startEnd(() -> m_gripperState = GripperState.SLIDE_LEFT, () -> m_gripperState = GripperState.IDLE);
    }

    public Command slideRight()
    {
        return this.startEnd(() -> m_gripperState = GripperState.SLIDE_RIGHT, () -> m_gripperState = GripperState.IDLE);
    }

    public Command openGripper()
    {
        return this.startEnd(() -> m_gripperState = GripperState.OPEN, () -> m_gripperState = GripperState.IDLE);
    }

    public Command closeGripper()
    {
        return this.startEnd(() -> m_gripperState = GripperState.CLOSE, () -> m_gripperState = GripperState.IDLE);
    }

    public Command rotateLeft()
    {
        return this.startEnd(() -> m_rotateSpeed = Configuration.RotateMotorMaxSpeed, () -> m_rotateSpeed = 0);
    }

    public Command rotateRight()
    {
        return this.startEnd(() -> m_rotateSpeed = -Configuration.RotateMotorMaxSpeed, () -> m_rotateSpeed = 0);
    }

    public void rotateControl(double speed)
    {
        System.out.println("rotate motor " + speed);
        m_rotateMotor.set(TalonSRXControlMode.PercentOutput, speed);
    }
}
