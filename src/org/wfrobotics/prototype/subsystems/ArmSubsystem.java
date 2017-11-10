package org.wfrobotics.prototype.subsystems;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmSubsystem extends Subsystem {

    public CANTalon baseMotor = new CANTalon(0);
    public CANTalon elbowMotor = new CANTalon(1); //possibly change addresses

    boolean elbow;
    boolean rotate;
    double up; //up is positive
    double clockwise; //clockwise is positive

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public Boolean isAtTop()
    {
        return false;
    }

    public Boolean isAtBottom()
    {
        return false;

    }
    public Boolean isAtBaseCW()
    {
        return false;

    }
    public Boolean isAtBaseCCW()
    {
        return false;
    }


        public void initDefaultCommand()
        {
            baseMotor.set(0);
            elbowMotor.set(0);
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

}

