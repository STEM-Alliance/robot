package org.wfrobotics.reuse.subsystems.motor2.examples.shooter;

import org.wfrobotics.reuse.subsystems.motor2.HerdMotor;
import org.wfrobotics.reuse.subsystems.motor2.examples.goals.Speed;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXMagPot;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXMotor;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXPID;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Team 4818 WFRobotics
 */
public abstract class ShooterConfig 
{
    private static CANTalon hw = new CANTalon(1);
    
    // States
    public static Speed IDLE = new Speed("Idle", 0, 0 * .05);
    
    public static HerdMotor getMotor()
    {
        SRXMotor motor = new SRXMotor.Builder(hw).maxVolts(11).build();
        SRXPID pid = new SRXPID.Builder(hw).p(2).build();
        SRXMagPot sensor = new SRXMagPot(hw, 5000);
        
        return new HerdMotor(motor, pid, sensor);
    }
    
    public static Command getDefaultCommand()
    {
        return new ShooterSetSpeed(ShooterConfig.IDLE);
    }
}
