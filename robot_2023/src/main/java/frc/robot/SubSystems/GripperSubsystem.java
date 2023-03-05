package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class GripperSubsystem extends SubsystemBase {
    private final TalonSRX m_lMotor;
    private final TalonSRX m_rMotor;
    private final TalonSRX m_rotateMotor;

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
        //System.out.println("Gripper period");
    }

    private void controlMotor(TalonSRX motor, double commandValue)
    {
        if (Math.abs(commandValue) > Configuration.GripperDeadband)
        {
            /*
             * Not sure if this control scheme is going to work well.
             * But the idea is you could use the two joysticks to move the
             * grippers independently of each other
             */
            motor.set(TalonSRXControlMode.PercentOutput, commandValue * 0.35);
        }
        else
        {
            motor.set(TalonSRXControlMode.PercentOutput, 0);
        }
    }

    public void slideGripper(double leftCommand, double rightCommand)
    {
        System.out.println("slide gripper: " + leftCommand + " : " + rightCommand);
        controlMotor(m_lMotor, -rightCommand);
        controlMotor(m_rMotor, leftCommand);
    }

    public Command slideLeft()
    {
        return this.run(() -> slideGripper(Configuration.GripperSlideFast, Configuration.GripperSlideSlow));
    }

    public Command slideRight()
    {
        return this.run(() -> slideGripper(-Configuration.GripperSlideSlow, -Configuration.GripperSlideFast));
    }

    public Command openGripper()
    {
        return this.run(() -> slideGripper(-Configuration.GripperOpenCloseSpeed, Configuration.GripperOpenCloseSpeed));
    }

    public Command closeGripper()
    {
        return this.run(() -> slideGripper(Configuration.GripperOpenCloseSpeed, -Configuration.GripperOpenCloseSpeed));
    }

    public Command rotateHome()
    {
        return this.run(() -> rotateControl(Configuration.RotateMotorMaxSpeed));
    }

    public Command rotatePerpendicular()
    {
        return this.run(() -> rotateControl(-Configuration.RotateMotorMaxSpeed));
    }

    public void rotateControl(double speed)
    {
        m_rotateMotor.set(TalonSRXControlMode.PercentOutput, speed);
    }
}
