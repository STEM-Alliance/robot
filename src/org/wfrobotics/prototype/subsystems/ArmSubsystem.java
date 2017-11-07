package org.wfrobotics.prototype.subsystems;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmSubsystem extends Subsystem {

    public static CANTalon baseMotor = new CANTalon(0);
    public static CANTalon elbowMotor = new CANTalon(1);
    public static CANTalon handMotor = new CANTalon(2);
    DoubleSolenoid handSolenoid = new DoubleSolenoid(1, 2); //possibly change addresses

    boolean elbow;
    boolean rotate;
    double up; //up is positive
    double clockwise; //clockwise is positive

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void spinUntilLimit(boolean atMax, boolean atMin, double speed, CANTalon motor)
    {
        if((speed < 0) && !atMin || (speed > 0 && !atMax))
        {
            motor.set(speed);
        }
        else
        {
            motor.set(0);
        }
    }

    public void moveHand (boolean in)
    {

        handSolenoid.set(in ? Value.kForward : Value.kReverse);
    }
    public void handReset ()
    {
        handSolenoid.set(Value.kOff);
    }

    public static Boolean isAtTop()
    {
        return false;
    }

    public static Boolean isAtBottom()
    {
        return false;

    }
    public static Boolean isAtBaseCW()
    {
        return false;

    }
    public static Boolean isAtBaseCCW()
    {
        return false;
    }
    public static Boolean isAtHandCW()
    {
        return false;

    }
    public static Boolean isAtHandCCW()
    {
        return false;
    }

        public void initDefaultCommand()
        {
            baseMotor.set(0);
            elbowMotor.set(0);
            handMotor.set(0);
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

}

