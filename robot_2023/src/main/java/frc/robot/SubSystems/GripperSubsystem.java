package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

import java.util.function.BooleanSupplier;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class GripperSubsystem extends SubsystemBase {
    private final CANSparkMax m_lMotor;
    private final CANSparkMax m_rMotor;
    public PIDController m_PID = new PIDController(0, 0, 0);
   
    
    
    /** Creates a new DriveSubsystem. */
    public GripperSubsystem(int leftMotorCanID, int rightMotorCanID) {
        m_lMotor = new CANSparkMax(leftMotorCanID, MotorType.kBrushless);
        m_rMotor = new CANSparkMax(rightMotorCanID, MotorType.kBrushless);
        m_lMotor.restoreFactoryDefaults();
        m_rMotor.restoreFactoryDefaults();
        RelativeEncoder m_lEnc = m_lMotor.getEncoder();
        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {
        
    }

}
