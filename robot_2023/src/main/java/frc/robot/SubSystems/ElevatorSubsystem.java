package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Configuration;

import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;




public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final CANSparkMax m_extendMotor;
    public Object control;
    boolean m_done;



    PIDController ElevatorPID = new PIDController(0, 0, 0);
    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID, int extendMotorID) {
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        m_extendMotor = new CANSparkMax(extendMotorID, MotorType.kBrushless);
        m_rotateMotor.restoreFactoryDefaults();
        m_extendMotor.restoreFactoryDefaults();
        m_extendMotor.setSmartCurrentLimit(50);
        m_rotateMotor.setSmartCurrentLimit(50);
        RelativeEncoder m_RotateENC = m_rotateMotor.getEncoder();
        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */
        m_extendMotor.setIdleMode(IdleMode.kBrake);
        m_rotateMotor.setIdleMode(IdleMode.kBrake);
    }   

    @Override
    public void periodic() {

    }
    // Void ElevatorCloseLoop(){
    //     m_rotateMotor.set(ElevatorPID.calculate(m_RotateENC.getPosition()));
    // }
    // public Command low() {
    //     return new FunctionalCommand(() -> ElevatorPID.setSetpoint(0), () -> ElevatorCloseLoop(), interrupted -> done(), null);
    // }
    // void done()
    // {

    // }

    public void control(double Rotation, double Extend)
    {
        if (Rotation > Configuration.controller_dead_zone){
            m_rotateMotor.set(Rotation);
        }
        else if (Rotation < Configuration.controller_dead_zone){
            m_rotateMotor.set(Rotation);
        }
        if (Extend > Configuration.controller_dead_zone){
            m_extendMotor.set(Extend * Configuration.ExtendMaxSpeed);
        }
        else if (Extend < Configuration.controller_dead_zone){
            m_extendMotor.set(Extend * Configuration.ExtendMaxSpeed);
        }
        else {
            m_rotateMotor.set(0);
            m_extendMotor.set(0);
        }
        
    }
}
