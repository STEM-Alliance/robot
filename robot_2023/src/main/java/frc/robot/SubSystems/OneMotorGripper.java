package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

import java.util.function.BooleanSupplier;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class OneMotorGripper extends SubsystemBase {
    private final CANSparkMax m_Motor;
    private final RelativeEncoder m_Enc;
    public PIDController m_PID = new PIDController(0.15, 0.005, 0);
    boolean m_done;
    boolean Stop;

    /** Creates a new DriveSubsystem. */
    public OneMotorGripper(int leftMotorCanID) {
        m_Motor = new CANSparkMax(leftMotorCanID, MotorType.kBrushless);
        m_Motor.restoreFactoryDefaults();
        m_Motor.setSmartCurrentLimit(20);
        m_Enc = m_Motor.getEncoder();
        m_Enc.setPosition(0);
        SmartDashboard.putData(m_PID);
        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {
        
        SmartDashboard.putNumber("encoder", m_Enc.getPosition());
    }

    void driveMotorToCloseLoop()
    {
        m_Motor.set(m_PID.calculate(m_Enc.getPosition()));
    }


    void done()
    {
      
    }
    
    public Command close() {
 
        // Close the grabber until we hit the limit switch
        // return this.runOnce(() -> System.out.println("Close Grabber"));
        return new FunctionalCommand(() -> m_PID.setSetpoint(-6), () -> driveMotorToCloseLoop(), interrupted -> done(), () -> Stop == true);
    }

    public Command open() {
        // Open the grabber until we hit the limit switch
        return new FunctionalCommand(() -> m_PID.setSetpoint(0), () -> driveMotorToCloseLoop(), interrupted -> done() , () -> Stop == true);
    }
    
    public Command Stop() {
        return this.runOnce(() -> Stop = true);
    }
    public Command Go() {
        return this.runOnce(() -> Stop = false);
    }
}
