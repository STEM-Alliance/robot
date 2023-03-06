package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj2.command.*;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class GripperSubsystem extends SubsystemBase {
    private final TalonSRX m_lMotor;
    private final TalonSRX m_rMotor;
    private final TalonSRX m_rotateMotor;
    int m_state = 0;

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
        switch (m_state)
        {
            case 0:
                slideGripper(0, 0);
                break;
            // Open
            case 1:
                slideGripper(-Configuration.GripperSlideFast, Configuration.GripperSlideFast);
                break;
            // close
            case 2:
                slideGripper(Configuration.GripperSlideFast, -Configuration.GripperSlideFast);
                break;
            // left
            case 3:
                slideGripper(Configuration.GripperSlideFast, Configuration.GripperSlideSlow);
                break;
            // right
            case 4:
                slideGripper(-Configuration.GripperSlideFast, -Configuration.GripperSlideFast);
                break;
        }
        //return this.startEnd(() -> slideGripper(Configuration.GripperSlideFast, Configuration.GripperSlideSlow), () -> slideGripper(0, 0));
        ;
    }

    public void slideGripper(double leftCommand, double rightCommand)
    {
        //System.out.println("slide gripper: " + leftCommand + " : " + rightCommand);
        m_lMotor.set(TalonSRXControlMode.PercentOutput, -rightCommand);
        m_rMotor.set(TalonSRXControlMode.PercentOutput, leftCommand);
    }

    public Command slideLeft()
    {
        //return this.startEnd(() -> slideGripper(Configuration.GripperSlideFast, Configuration.GripperSlideSlow), () -> slideGripper(0, 0));
        return this.startEnd(() -> m_state = 3, () -> m_state = 0);
    }

    public Command slideRight()
    {
        //return this.startEnd(() -> slideGripper(-Configuration.GripperSlideFast, -Configuration.GripperSlideSlow), () -> slideGripper(0, 0));
        return this.startEnd(() -> m_state = 4, () -> m_state = 0);
    }

    public Command openGripper()
    {
        //return this.startEnd(() -> slideGripper(-Configuration.GripperSlideFast, Configuration.GripperSlideSlow), () -> slideGripper(0, 0));
        return this.startEnd(() -> m_state = 1, () -> m_state = 0);
    }

    public Command closeGripper()
    {
        //return this.startEnd(() -> slideGripper(Configuration.GripperSlideFast, -Configuration.GripperSlideSlow), () -> slideGripper(0, 0));
        return this.startEnd(() -> m_state = 2, () -> m_state = 0);
    }

    public Command rotateLeft()
    {
        return new InstantCommand(() -> rotateControl(Configuration.GripperSlideFast));
    }

    public Command rotateRight()
    {
        return new InstantCommand(() -> rotateControl(-Configuration.GripperSlideFast));
    }

    public void rotateControl(double speed)
    {
        System.out.println("rotate motor " + speed);
        m_rotateMotor.set(TalonSRXControlMode.PercentOutput, speed);
    }
}
