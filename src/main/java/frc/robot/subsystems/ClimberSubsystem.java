package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;

import java.util.logging.Logger;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configuration;
import frc.robot.LoggedNumber;

public class ClimberSubsystem extends SubsystemBase {
    CANSparkMax m_climbMotor;
    RelativeEncoder m_climbEncoder;

    PWM m_climbStop;
    boolean m_brakeEngaged = false;

    /** Creates a new ClimberSubsystem. */
    public ClimberSubsystem(int climbMotor) {
        m_climbMotor = new CANSparkMax(climbMotor, MotorType.kBrushless);
        m_climbMotor.setSmartCurrentLimit(Configuration.Neo550Limit);
        m_climbEncoder = m_climbMotor.getEncoder();
        m_climbEncoder.setPositionConversionFactor(0.150458);
        m_climbStop = new PWM(Configuration.kClimbStopChannel);
        m_climbEncoder.setPosition(0);
        m_climbStop.setSpeed(1);

    }

    public void periodic() {
        SmartDashboard.putBoolean("ClimbBrakeEngaged", m_brakeEngaged);
        LoggedNumber.getInstance().logNumber("ClimbCurrent", m_climbMotor.getOutputCurrent());
        LoggedNumber.getInstance().logNumber("ClimbSpeed", m_climbMotor.get());
    }

    public void runClimber(double setpoint) {
        if (!m_brakeEngaged && (setpoint < 0) && (m_climbEncoder.getPosition() > -30)) {
            m_climbMotor.set(setpoint);
        }
        else if (!m_brakeEngaged && (setpoint > 0) && (m_climbEncoder.getPosition() < 0)) {
            m_climbMotor.set(setpoint);
        }
        else {
            m_climbMotor.set(0);
        }
        SmartDashboard.putNumber("Climbenc", m_climbEncoder.getPosition());
    }

    private void toggleClimbBrake() {
        m_brakeEngaged = !m_brakeEngaged;
        if (m_brakeEngaged) {
            m_climbStop.setSpeed(-1);
        }
        else {
            m_climbStop.setSpeed(1);
        }
    }

    public Command toggleClimbBrakeCmd() {
        return new InstantCommand(() -> toggleClimbBrake());
    }

    public Command climbCmd(double value) {
        return new FunctionalCommand(() -> {}, 
                                     () -> {m_climbMotor.set(value);}, 
                                     interrupted -> {m_climbMotor.set(0);}, 
                                     () -> m_brakeEngaged, 
                                     this);
    }

    // public Command climbUp() {
    //     return new FunctionalCommand(
    //         () -> {},
    //         {},
    //         interrupted -> {},
    //         {},
    //         this);
    // }


    // // public Command climbUp() {
    // //     return new FunctionalCommand(
    // //         () -> m_climbMotor.set(0),
    // //         () -> {},
    // //         interrupted -> m_climbMotor.set(0),
    // //         () -> m_climbMotor.set(0.5),
    // //         this);
    // // }

    // public Command climbDown() {
    // return new FunctionalCommand(
    //     () -> m_climbMotor.set(0),
    //     () -> {},
    //     interrupted -> m_climbMotor.set(0),
    //     () -> m_climbMotor.set(-0.5),
    //     this);
    // }
}