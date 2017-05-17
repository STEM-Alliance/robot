package org.wfrobotics.reuse.subsystems.motor2.examples.arm;

import org.wfrobotics.reuse.hardware.motors2.HerdMotor;
import org.wfrobotics.reuse.hardware.motors2.wrappers.SRXMagPot;
import org.wfrobotics.reuse.hardware.motors2.wrappers.SRXMotor;
import org.wfrobotics.reuse.hardware.motors2.wrappers.SRXPID;
import org.wfrobotics.reuse.subsystems.motor2.goals.Position;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Team 4818 WFRobotics
 */
public abstract class ArmConfig 
{
    private static CANTalon hw = new CANTalon(0);
    
    // States
    public static Top TOP = new Top(hw, 180);
    public static Position BOTTOM = new Position("Bottom", 0, 5);
    public static Position TRANSPORT = new Position("Transport", 45, 7.5);
    
    public static HerdMotor getMotor()
    {
        SRXMotor motor = new SRXMotor.Builder(hw).maxVolts(11).build();
        SRXPID pid = new SRXPID.Builder(hw).p(2).build();
        SRXMagPot sensor = new SRXMagPot(hw, 360);
        
        return new HerdMotor(motor, pid, sensor);
    }
    
    public static Command getDefaultCommand()
    {
        return new ArmToPosition(TOP);
    }
}
