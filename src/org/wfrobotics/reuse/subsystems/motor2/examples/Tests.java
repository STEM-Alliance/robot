package org.wfrobotics.reuse.subsystems.motor2.examples;

import org.wfrobotics.reuse.subsystems.motor2.ClosedLoopMotor;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXMagPot;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXMotor;
import org.wfrobotics.reuse.subsystems.motor2.hardware.SRXPID;

import com.ctre.CANTalon;

public class Tests
{
    public static void main(String[] args)
    {
        // Config
        CANTalon ct = new CANTalon(0);
        SRXMotor motor = new SRXMotor.Builder(ct).maxVolts(11).build();
        SRXPID pid = new SRXPID.Builder(ct).p(2).build();
        SRXMagPot sensor = new SRXMagPot(ct, 360);
        ClosedLoopMotor srxArm = new ClosedLoopMotor(motor, pid, sensor);
        
        // Robot.java
        RotationSubsystem arm = new RotationSubsystem(srxArm, 5);
    }
}
