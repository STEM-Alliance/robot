package com.taurus.subsystems;

import com.taurus.commands.DriveWithXbox;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RockerDriveSubsystem extends Subsystem 
{
    CANTalon motorFR = new CANTalon(10);
    CANTalon motorFL = new CANTalon(11);
    CANTalon motorMR = new CANTalon(12);
    CANTalon motorML = new CANTalon(13);
    CANTalon motorBR = new CANTalon(14);
    CANTalon motorBL = new CANTalon(15);
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveWithXbox());
    }
    
    /**
     * Set the chassis drive motors
     * @param right side value
     * @param left side value
     */
    public void drive (double right, double left) {
        motorBL.set(left);
        motorBR.set(right);
        motorML.set(left);
        motorMR.set(right);
        motorFL.set(left);
        motorFR.set(right);        
    }
}
