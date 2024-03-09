package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class ClimbingSystem extends SubsystemBase {
    public final CANSparkMax leftCLimber;
    public final CANSparkMax rightCLimber;
    private boolean IntakeRunning;

    private double m_rotateSpeed = 0;
    /** Creates a new DriveSubsystem. */
    public ClimbingSystem(int leftClimberID, int RightClimberID) {
        leftCLimber = new CANSparkMax(leftClimberID, MotorType.kBrushless);
        rightCLimber = new CANSparkMax(RightClimberID, MotorType.kBrushless);

        leftCLimber.setSmartCurrentLimit(Configuration.Neo550Limit);
        rightCLimber.setSmartCurrentLimit(Configuration.Neo550Limit);
       
        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */
    }

    @Override

    public void periodic() {
        // Things we need to do on a periodic basis
        //System.out.println("Gripper period")
    }

    public void doStuff() {
        // Do stuff
    }
    public Command Climb(){
       return null; 
    }
}
