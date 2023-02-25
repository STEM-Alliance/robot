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
        SmartDashboard.putData(m_PID);
    }

    public Command close() {
 
        // Close the grabber until we hit the limit switch
        return this.runOnce(() -> System.out.println("Close Grabber"));
        //return new FunctionalCommand(() -> m_PID.setSetpoint(100), () -> m_lMotor.set(m_PID.calculate(m_lEnc.getPosition())), null, () -> m_PID.atSetpoint());
    }
    
    

    public Command open() {
        // Open the grabber until we hit the limit switch
        return this.runOnce(() -> System.out.println("Open Grabber"));
    }

    public void slideGripper(double commandValue) {
        if (Math.abs(commandValue) > Configuration.GripperDeadband)
        {
            /*
             * Drive the motor directly.
             * TODO: Make sure these directions are correct
             */
            m_lMotor.set(commandValue);
            m_rMotor.set(commandValue);
        }
    }
}
