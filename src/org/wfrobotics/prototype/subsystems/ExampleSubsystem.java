package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveTank;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

/** TODO: comment what this Subsystem does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleSubsystem extends Subsystem
{
    // TODO create any hardware this Subsystem needs to control the robot
    
    CANTalon left;
    CANTalon left2;
    CANTalon right;
    CANTalon right2;
    
    public ExampleSubsystem()
    
    {
        // TODO setup any hardware this Subsystem will need
        left = new CANTalon(0);
        left2 = new CANTalon(1);
        right = new CANTalon(2);
        right2 = new CANTalon(6);
        right.setInverted(true);
        right2.setInverted(true);
        left2.changeControlMode(TalonControlMode.Follower);
        left2.set(0);
        right2.changeControlMode(TalonControlMode.Follower);
        right2.set(2);
    }

    protected void initDefaultCommand()
    {
        // TODO pick which Command runs whenever buttons are not being pressed
        setDefaultCommand(new DriveTank());
    }

    // TODO rename me (right click -> refactor -> rename)
    public void setSpeed(double x, double y)  // TODO pass any parameters needed to each method
    {
        // TODO create any methods needed to control this Subsystem, called by Commands
        left.set(y-x); // -1x, 0y = 1=left, -1=right; -1x, 1y = 1=left, .5=right; 0x, 1y = 1=left, 1=right;
        right.set(y+x); // x = -left, right; y = left, right;
    }
}